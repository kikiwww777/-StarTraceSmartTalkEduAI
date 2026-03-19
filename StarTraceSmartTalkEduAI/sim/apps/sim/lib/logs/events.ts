import { createHmac } from 'crypto'
import { db } from '@sim/db'
import {
  workflow,
  workspaceNotificationDelivery,
  workspaceNotificationSubscription,
} from '@sim/db/schema'
import { createLogger } from '@sim/logger'
import { and, eq, or, sql } from 'drizzle-orm'
import { v4 as uuidv4 } from 'uuid'
import { isTriggerDevEnabled } from '@/lib/core/config/feature-flags'
import type { WorkflowExecutionLog } from '@/lib/logs/types'
import {
  type AlertCheckContext,
  type AlertConfig,
  shouldTriggerAlert,
} from '@/lib/notifications/alert-rules'
import {
  executeNotificationDelivery,
  workspaceNotificationDeliveryTask,
} from '@/background/workspace-notification-delivery'

const logger = createLogger('LogsEventEmitter')

type EduaiWebhookPayload = {
  id: string
  type: 'workflow.execution.completed'
  timestamp: number
  data: {
    workspaceId?: string
    workflowId: string
    workflowName?: string
    executionId: string
    status: 'success' | 'error'
    level: string
    trigger: string
    startedAt: string
    endedAt: string
    totalDurationMs: number
    cost?: Record<string, unknown>
    tokenUsage?: {
      inputTokens?: number
      outputTokens?: number
      totalTokens?: number
    }
    metrics?: {
      durationMs?: number
      costUsd?: number
    }
    metadata?: Record<string, unknown>
    finalOutput?: unknown
    traceSpans?: unknown[]
  }
}

function getEduaiWebhookUrl(): string | null {
  // Prefer explicit full URL override.
  const direct = process.env.EDUAI_WEBHOOK_URL || process.env.NEXT_PUBLIC_EDUAI_WEBHOOK_URL
  if (direct && String(direct).trim()) return String(direct).trim()

  // Otherwise derive from EDUAI backend base URL (works for local/dev & docker).
  // Recommended in docker: NEXT_PUBLIC_EDUAI_BACKEND_URL=http://host.docker.internal:8080
  const base =
    process.env.NEXT_PUBLIC_EDUAI_BACKEND_URL ||
    process.env.EDUAI_BACKEND_URL ||
    'http://localhost:8080'
  const normalized = String(base).trim().replace(/\/+$/, '')
  return `${normalized}/api/edu/webhook/sim-execution`
}

function getEduaiWebhookSecret(): string {
  return (
    process.env.EDUAI_WEBHOOK_SECRET ||
    process.env.NEXT_PUBLIC_EDUAI_WEBHOOK_SECRET ||
    'simstudio_webhook_secret_123456'
  )
}

function generateSimSignature(secret: string, timestamp: number, body: string): string {
  // sim-signature: t={timestamp},v1={hex(hmac_sha256(secret, "{timestamp}.{body}"))}
  // Keep aligned with `background/workspace-notification-delivery.ts`.
  const signatureBase = `${timestamp}.${body}`
  const hmac = createHmac('sha256', secret)
  hmac.update(signatureBase)
  return hmac.digest('hex')
}

async function deliverEduaiWebhook(payload: EduaiWebhookPayload): Promise<void> {
  const url = getEduaiWebhookUrl()
  if (!url) return

  const secret = getEduaiWebhookSecret()
  const body = JSON.stringify(payload)
  const signature = generateSimSignature(secret, payload.timestamp, body)

  try {
    logger.info('Delivering EDUAI webhook', {
      url,
      executionId: payload.data.executionId,
      workflowId: payload.data.workflowId,
      workspaceId: payload.data.workspaceId,
    })
    const resp = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'sim-event': 'workflow.execution.completed',
        'sim-timestamp': payload.timestamp.toString(),
        'sim-signature': `t=${payload.timestamp},v1=${signature}`,
      },
      body,
    })

    if (!resp.ok) {
      logger.warn('EDUAI webhook delivery failed', { status: resp.status, url })
    } else {
      logger.info('EDUAI webhook delivered', { status: resp.status, url })
    }
  } catch (e) {
    logger.warn('EDUAI webhook delivery error', { error: e, url })
  }
}

function prepareLogData(
  log: WorkflowExecutionLog,
  subscription: {
    includeFinalOutput: boolean
    includeTraceSpans: boolean
  }
) {
  const preparedLog = { ...log, executionData: {} as Record<string, unknown> }

  if (log.executionData) {
    const data = log.executionData as Record<string, unknown>
    const webhookData: Record<string, unknown> = {}

    if (subscription.includeFinalOutput && data.finalOutput) {
      webhookData.finalOutput = data.finalOutput
    }

    if (subscription.includeTraceSpans && data.traceSpans) {
      webhookData.traceSpans = data.traceSpans
    }

    preparedLog.executionData = webhookData
  }

  return preparedLog
}

export async function emitWorkflowExecutionCompleted(log: WorkflowExecutionLog): Promise<void> {
  try {
    const workflowData = await db
      .select({ workspaceId: workflow.workspaceId })
      .from(workflow)
      .where(eq(workflow.id, log.workflowId))
      .limit(1)

    if (workflowData.length === 0 || !workflowData[0].workspaceId) return

    const workspaceId = workflowData[0].workspaceId

    // Always best-effort send to EDUAI webhook (independent of workspace notification subscriptions).
    // This ensures EDUAI can persist sim_webhook_logs / sim_execution_logs / student_token_usage.
    try {
      const executionData = (log.executionData || {}) as Record<string, any>
      const triggerObj = (executionData.trigger || {}) as Record<string, any>
      const triggerData = (triggerObj.data || {}) as Record<string, any>
      const eduMetadata = (triggerData.metadata || null) as Record<string, unknown> | null
      const cost = (log.cost || {}) as Record<string, any>
      const tokens = (cost.tokens || {}) as Record<string, any>

      const payload: EduaiWebhookPayload = {
        id: `evt_${uuidv4()}`,
        type: 'workflow.execution.completed',
        timestamp: Date.now(),
        data: {
          workspaceId,
          workflowId: log.workflowId,
          workflowName: undefined,
          executionId: log.executionId,
          status: log.level === 'error' ? 'error' : 'success',
          level: log.level,
          trigger: log.trigger,
          startedAt: log.startedAt,
          endedAt: log.endedAt,
          totalDurationMs: log.totalDurationMs || 0,
          cost: log.cost as Record<string, unknown>,
          tokenUsage: {
            inputTokens: typeof tokens.input === 'number' ? tokens.input : undefined,
            outputTokens: typeof tokens.output === 'number' ? tokens.output : undefined,
            totalTokens: typeof tokens.total === 'number' ? tokens.total : undefined,
          },
          metrics: {
            durationMs: log.totalDurationMs || 0,
            costUsd: typeof cost.total === 'number' ? cost.total : undefined,
          },
          ...(eduMetadata ? { metadata: eduMetadata } : {}),
        },
      }

      // Include finalOutput/traceSpans if present (matches EDUAI backend optional fields)
      const execData = (log.executionData || {}) as Record<string, unknown>
      const maybeFinal = (execData as any)?.finalOutput
      const maybeTrace = (execData as any)?.traceSpans
      if (maybeFinal) payload.data.finalOutput = maybeFinal
      if (maybeTrace) payload.data.traceSpans = maybeTrace

      await deliverEduaiWebhook(payload)
    } catch (e) {
      logger.warn('Failed to deliver EDUAI webhook (non-blocking)', { error: e })
    }

    const subscriptions = await db
      .select()
      .from(workspaceNotificationSubscription)
      .where(
        and(
          eq(workspaceNotificationSubscription.workspaceId, workspaceId),
          eq(workspaceNotificationSubscription.active, true),
          or(
            eq(workspaceNotificationSubscription.allWorkflows, true),
            sql`${log.workflowId} = ANY(${workspaceNotificationSubscription.workflowIds})`
          )
        )
      )

    if (subscriptions.length === 0) return

    logger.debug(
      `Found ${subscriptions.length} active notification subscriptions for workspace ${workspaceId}`
    )

    for (const subscription of subscriptions) {
      const levelMatches = subscription.levelFilter.includes(log.level)
      const triggerMatches = subscription.triggerFilter.includes(log.trigger)

      if (!levelMatches || !triggerMatches) {
        logger.debug(`Skipping subscription ${subscription.id} due to filter mismatch`)
        continue
      }

      const alertConfig = subscription.alertConfig as AlertConfig | null

      if (alertConfig) {
        const context: AlertCheckContext = {
          workflowId: log.workflowId,
          executionId: log.executionId,
          status: log.level === 'error' ? 'error' : 'success',
          durationMs: log.totalDurationMs || 0,
          cost: (log.cost as { total?: number })?.total || 0,
          triggerFilter: subscription.triggerFilter,
        }

        const shouldAlert = await shouldTriggerAlert(alertConfig, context, subscription.lastAlertAt)

        if (!shouldAlert) {
          logger.debug(`Alert condition not met for subscription ${subscription.id}`)
          continue
        }

        await db
          .update(workspaceNotificationSubscription)
          .set({ lastAlertAt: new Date() })
          .where(eq(workspaceNotificationSubscription.id, subscription.id))

        logger.info(`Alert triggered for subscription ${subscription.id}`, {
          workflowId: log.workflowId,
          alertConfig,
        })
      }

      const deliveryId = uuidv4()

      await db.insert(workspaceNotificationDelivery).values({
        id: deliveryId,
        subscriptionId: subscription.id,
        workflowId: log.workflowId,
        executionId: log.executionId,
        status: 'pending',
        attempts: 0,
        nextAttemptAt: new Date(),
      })

      const notificationLog = prepareLogData(log, subscription)

      const payload = {
        deliveryId,
        subscriptionId: subscription.id,
        notificationType: subscription.notificationType,
        log: notificationLog,
        alertConfig: alertConfig || undefined,
      }

      if (isTriggerDevEnabled) {
        await workspaceNotificationDeliveryTask.trigger(payload)
        logger.info(
          `Enqueued ${subscription.notificationType} notification ${deliveryId} via Trigger.dev`
        )
      } else {
        void executeNotificationDelivery(payload).catch((error) => {
          logger.error(`Direct notification delivery failed for ${deliveryId}`, { error })
        })
        logger.info(`Enqueued ${subscription.notificationType} notification ${deliveryId} directly`)
      }
    }
  } catch (error) {
    logger.error('Failed to emit workflow execution completed event', {
      error,
      workflowId: log.workflowId,
      executionId: log.executionId,
    })
  }
}
