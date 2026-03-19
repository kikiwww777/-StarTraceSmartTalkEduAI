'use client'

import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useRouter } from 'next/navigation'
import { createLogger } from '@sim/logger'
import { parseWorkflowJson } from '@/stores/workflows/json/importer'
import { extractWorkflowName } from '@/lib/workflows/operations/import-export'

const logger = createLogger('EmbedWorkbenchPage')

type ImportMessage = {
  type: 'SIM_IMPORT_EXPERIMENT'
  version: 1
  data: {
    templateId?: number
    templateName?: string
    classId?: number | null
    courseId?: number | null
    teacherId?: number | null
    /** 后端 EDUAI 创建的 student_experiment_attempts.id，用于过程/结果回传 */
    attemptId?: number | null
    workflowJson: string
  }
}

type ImportEnvelope = {
  ts?: number
  payload?: ImportMessage
}

export default function EmbedWorkbenchPage() {
  const router = useRouter()
  const [status, setStatus] = useState<string>('等待导入指令…')
  const handledRef = useRef(false)

  // EDUAI 后端基础地址（用于把实验信息回传到 sim_studio 数据库）
  const eduApiBase = useMemo(() => {
    const base = process.env.NEXT_PUBLIC_EDUAI_BACKEND_URL ?? 'http://localhost:8080'
    return base.replace(/\/+$/, '')
  }, [])

  const allowedOrigins = useMemo(() => {
    const configured =
      process.env.NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS?.split(/[,\s]+/g)
        .map((s) => s.trim())
        .filter(Boolean) ?? []

    const defaults = [
      'http://localhost:5173',
      'http://127.0.0.1:5173',
      'http://localhost:5174',
      'http://127.0.0.1:5174',
      'http://localhost:8080',
      'http://127.0.0.1:8080',
    ]

    return new Set([...defaults, ...configured])
  }, [])

  const handleImport = useCallback(
    async (msg: Partial<ImportMessage>) => {
      if (msg?.type !== 'SIM_IMPORT_EXPERIMENT') return
      if (handledRef.current) return
      handledRef.current = true

      const workflowJson = msg?.data?.workflowJson
      if (!workflowJson || typeof workflowJson !== 'string') {
        setStatus('导入失败：未收到 workflowJson')
        return
      }

      try {
        setStatus('正在解析模板 JSON…')
        const { data: workflowData, errors: parseErrors } = parseWorkflowJson(workflowJson)
        if (!workflowData || parseErrors.length > 0) {
          logger.warn('Failed to parse workflow JSON:', parseErrors)
          setStatus('导入失败：模板 JSON 不是有效的 SIM 工作流格式')
          return
        }

        setStatus('正在创建新的工作台（workspace）…')
        const workspaceName =
          msg.data?.templateName?.trim() ||
          (msg.data?.templateId ? `Experiment_${msg.data.templateId}` : 'Experiment')

        const wsResp = await fetch('/api/workspaces', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ name: workspaceName }),
        })
        if (!wsResp.ok) {
          if (wsResp.status === 401 || wsResp.status === 403) {
            setStatus('未登录，将打开 SIM 登录页面…')
            router.replace('/login?callbackUrl=/embed/workbench')
            return
          }
          setStatus('创建工作台失败：请确认已登录 SIM')
          return
        }
        const wsJson = await wsResp.json()
        const workspaceId = wsJson?.workspace?.id as string | undefined
        if (!workspaceId) {
          setStatus('创建工作台失败：返回缺少 workspaceId')
          return
        }

        setStatus('正在创建实验流程（workflow）…')
        const wfName = extractWorkflowName(workflowJson, `${workspaceName}.json`)
        const createWfResp = await fetch('/api/workflows', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: wfName,
            description: 'Imported from EDUAI class template',
            workspaceId,
          }),
        })
        if (!createWfResp.ok) {
          setStatus('创建 workflow 失败')
          return
        }
        const newWf = (await createWfResp.json()) as { id?: string }
        const workflowId = newWf?.id
        if (!workflowId) {
          setStatus('创建 workflow 失败：返回缺少 workflowId')
          return
        }

        // 若 EDUAI 侧传入了 attemptId，则在 EDUAI 后端记录一次 SETUP 事件，完成与学生实验记录的关联
        try {
          const attemptId = msg.data?.attemptId
          if (attemptId && typeof attemptId === 'number') {
            const payload = {
              eventType: 'SETUP',
              eventAt: new Date().toISOString(),
              seq: 0,
              tokenUsed: 0,
              latencyMs: null,
              payloadJson: JSON.stringify({
                workspaceId,
                workflowId,
                templateId: msg.data?.templateId ?? null,
                templateName: msg.data?.templateName ?? null,
                classId: msg.data?.classId ?? null,
                courseId: msg.data?.courseId ?? null,
                teacherId: msg.data?.teacherId ?? null,
              }),
              payloadText: null,
            }

            await fetch(
              `${eduApiBase}/api/sim/experiments/attempts/${attemptId}/events`,
              {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
              }
            )
            logger.info('Reported experiment setup event to EDUAI backend', {
              attemptId,
              workspaceId,
              workflowId,
            })
          }
        } catch (e) {
          logger.warn('Failed to report experiment setup event to EDUAI backend', {
            error: e,
          })
        }

        setStatus('正在导入实验内容到工作台…')
        const stateResp = await fetch(`/api/workflows/${workflowId}/state`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(workflowData),
        })
        if (!stateResp.ok) {
          setStatus('导入 workflow state 失败')
          return
        }

        setStatus('导入完成，正在打开工作台…')
        router.replace(`/workspace/${workspaceId}/w/${workflowId}`)
      } catch (e) {
        logger.error('Import error:', e)
        setStatus('导入失败：发生异常，请查看控制台日志')
      }
    },
    [router, eduApiBase]
  )

  useEffect(() => {
    const onMessage = async (event: MessageEvent) => {
      // 基础安全校验：只接受来自前端的消息（按需补充域名）
      if (!allowedOrigins.has(event.origin)) {
        return
      }

      // EDUAI 侧发送的是 JSON 字符串，避免 DataCloneError
      let msg: Partial<ImportMessage> | null = null
      if (typeof event.data === 'string') {
        try {
          msg = JSON.parse(event.data) as Partial<ImportMessage>
        } catch {
          return
        }
      } else {
        msg = event.data as Partial<ImportMessage>
      }

      await handleImport(msg ?? {})
    }

    window.addEventListener('message', onMessage)
    return () => window.removeEventListener('message', onMessage)
  }, [allowedOrigins, handleImport])

  useEffect(() => {
    if (handledRef.current) return

    // Fallback path when the page is opened as a top-level tab (no iframe) or when message timing fails.
    try {
      const raw = window.localStorage.getItem('EDUAI_SIM_IMPORT_PAYLOAD_V1')
      if (!raw) return
      const env = JSON.parse(raw) as ImportEnvelope
      const msg = env?.payload
      if (msg?.type !== 'SIM_IMPORT_EXPERIMENT') return

      window.localStorage.removeItem('EDUAI_SIM_IMPORT_PAYLOAD_V1')

      // localStorage is same-origin with this page, so it's safe to bypass origin checks here.
      void handleImport(msg)
    } catch (e) {
      logger.warn('Failed to read EDUAI import payload from localStorage', { error: e })
    }
  }, [handleImport])

  return (
    <div className='flex h-screen w-screen items-center justify-center bg-[var(--bg)] p-6'>
      <div className='w-full max-w-xl rounded-xl border border-[hsl(var(--border))] bg-[hsl(var(--card))] p-5 text-[hsl(var(--foreground))]'>
        <div className='text-sm font-semibold'>内嵌工作台</div>
        <div className='mt-2 text-sm opacity-80'>{status}</div>
        <div className='mt-4 text-xs opacity-60'>
          若提示未登录，请先在此 iframe 内完成 SIM 登录，然后回到班级页再次点击“立即实验”。
        </div>
      </div>
    </div>
  )
}


