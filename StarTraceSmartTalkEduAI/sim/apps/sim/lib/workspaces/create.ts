import { db } from '@sim/db'
import { permissions, workflow, workspace } from '@sim/db/schema'
import { createLogger } from '@sim/logger'
import { buildDefaultWorkflowArtifacts } from '@/lib/workflows/defaults'
import { saveWorkflowToNormalizedTables } from '@/lib/workflows/persistence/utils'

const logger = createLogger('WorkspaceCreation')

interface CreateWorkspaceOptions {
  name: string
  ownerId: string
  skipInitialWorkflow?: boolean
}

/**
 * Create a new workspace with admin permissions for the owner
 * Optionally creates an initial default workflow
 *
 * @param options - Workspace creation options
 * @returns The created workspace object
 */
export async function createWorkspace(options: CreateWorkspaceOptions) {
  const { name, ownerId, skipInitialWorkflow = false } = options

  const workspaceId = crypto.randomUUID()
  const workflowId = crypto.randomUUID()
  const now = new Date()

  // Create the workspace and initial workflow in a transaction
  try {
    await db.transaction(async (tx) => {
      // Create the workspace
      await tx.insert(workspace).values({
        id: workspaceId,
        name,
        ownerId,
        billedAccountUserId: ownerId,
        allowPersonalApiKeys: true,
        createdAt: now,
        updatedAt: now,
      })

      // Create admin permissions for the workspace owner
      await tx.insert(permissions).values({
        id: crypto.randomUUID(),
        entityType: 'workspace' as const,
        entityId: workspaceId,
        userId: ownerId,
        permissionType: 'admin' as const,
        createdAt: now,
        updatedAt: now,
      })

      // Optionally create initial workflow for the workspace
      if (!skipInitialWorkflow) {
        // Create the workflow
        await tx.insert(workflow).values({
          id: workflowId,
          userId: ownerId,
          workspaceId,
          folderId: null,
          name: 'default-agent',
          description: 'Your first workflow - start building here!',
          color: '#3972F6',
          lastSynced: now,
          createdAt: now,
          updatedAt: now,
          isDeployed: false,
          runCount: 0,
          variables: {},
        })

        logger.info(
          `Created workspace ${workspaceId} with initial workflow ${workflowId} for user ${ownerId}`
        )
      } else {
        logger.info(`Created workspace ${workspaceId} without initial workflow for user ${ownerId}`)
      }
    })

    // Seed default workflow state if workflow was created
    if (!skipInitialWorkflow) {
      const { workflowState } = buildDefaultWorkflowArtifacts()
      const seedResult = await saveWorkflowToNormalizedTables(workflowId, workflowState)

      if (!seedResult.success) {
        throw new Error(seedResult.error || 'Failed to seed default workflow state')
      }
    }
  } catch (error) {
    logger.error(`Failed to create workspace ${workspaceId}:`, error)
    throw error
  }

  // Return the workspace data
  return {
    id: workspaceId,
    name,
    ownerId,
    billedAccountUserId: ownerId,
    allowPersonalApiKeys: true,
    createdAt: now,
    updatedAt: now,
    role: 'owner',
  }
}

