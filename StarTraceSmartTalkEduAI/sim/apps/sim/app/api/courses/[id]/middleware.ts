import { db } from '@sim/db'
import { courseMember } from '@sim/db/schema'
import { createLogger } from '@sim/logger'
import type { NextRequest } from 'next/server'
import { and, eq } from 'drizzle-orm'
import { getSession } from '@/lib/auth'

const logger = createLogger('CourseMiddleware')

export type CourseRole = 'teacher' | 'student' | 'admin'

export interface CoursePermissionResult {
  error?: { message: string; status: number }
  memberRole?: CourseRole
  userId?: string
}

/**
 * Check if user has permission to access a course
 * This middleware should be used in all course-related API routes
 *
 * @param request - Next.js request object
 * @param courseId - Course ID from route params
 * @param requireRole - Optional role requirement (teacher, admin, etc.)
 * @returns Permission result with member role or error
 */
export async function checkCoursePermission(
  request: NextRequest,
  courseId: string,
  requireRole?: 'teacher' | 'admin'
): Promise<CoursePermissionResult> {
  // Get authenticated user
  const session = await getSession()
  if (!session?.user?.id) {
    return {
      error: {
        message: 'Unauthorized: Authentication required',
        status: 401,
      },
    }
  }

  const userId = session.user.id

  try {
    // Check if user is a member of the course
    const members = await db
      .select({ role: courseMember.role })
      .from(courseMember)
      .where(and(eq(courseMember.courseId, courseId), eq(courseMember.userId, userId)))
      .limit(1)

    if (members.length === 0) {
      logger.warn(`User ${userId} attempted to access course ${courseId} but is not a member`)
      return {
        error: {
          message: 'Access denied: You are not a member of this course.',
          status: 403,
        },
      }
    }

    const memberRole = members[0].role as CourseRole

    // Check role requirement if specified
    if (requireRole) {
      if (requireRole === 'admin' && memberRole !== 'admin') {
        return {
          error: {
            message: 'Access denied: Admin role required.',
            status: 403,
          },
        }
      }

      if (requireRole === 'teacher' && memberRole !== 'teacher' && memberRole !== 'admin') {
        return {
          error: {
            message: 'Access denied: Teacher or admin role required.',
            status: 403,
          },
        }
      }
    }

    // Check for method-specific restrictions
    const method = request.method
    const pathname = request.nextUrl.pathname

    // Students cannot modify quotas (example rule)
    if (
      method === 'POST' &&
      memberRole === 'student' &&
      (pathname.includes('quota') || pathname.includes('limit'))
    ) {
      return {
        error: {
          message: 'Access denied: Students cannot modify quotas.',
          status: 403,
        },
      }
    }

    logger.info(`User ${userId} (${memberRole}) accessing course ${courseId}`)
    return {
      memberRole,
      userId,
    }
  } catch (error) {
    logger.error('Error checking course permission:', error)
    return {
      error: {
        message: 'Internal server error',
        status: 500,
      },
    }
  }
}

