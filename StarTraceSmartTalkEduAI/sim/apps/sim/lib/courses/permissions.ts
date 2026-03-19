import { db } from '@sim/db'
import { courseMember, course } from '@sim/db/schema'
import { createLogger } from '@sim/logger'
import { and, eq } from 'drizzle-orm'

const logger = createLogger('CoursePermissions')

export type CourseRole = 'teacher' | 'student' | 'admin'

/**
 * Check if a user has a specific role in a course
 */
export async function hasCourseRole(
  userId: string,
  courseId: string,
  role: CourseRole
): Promise<boolean> {
  try {
    const member = await db
      .select({ role: courseMember.role })
      .from(courseMember)
      .where(and(eq(courseMember.courseId, courseId), eq(courseMember.userId, userId)))
      .limit(1)

    if (member.length === 0) {
      return false
    }

    return member[0].role === role
  } catch (error) {
    logger.error('Error checking course role:', error)
    return false
  }
}

/**
 * Check if a user is a teacher or admin in a course
 */
export async function isCourseTeacherOrAdmin(
  userId: string,
  courseId: string
): Promise<boolean> {
  try {
    const member = await db
      .select({ role: courseMember.role })
      .from(courseMember)
      .where(and(eq(courseMember.courseId, courseId), eq(courseMember.userId, userId)))
      .limit(1)

    if (member.length === 0) {
      return false
    }

    const role = member[0].role
    return role === 'teacher' || role === 'admin'
  } catch (error) {
    logger.error('Error checking if user is teacher or admin:', error)
    return false
  }
}

/**
 * Check if a user is the instructor of a course
 */
export async function isCourseInstructor(userId: string, courseId: string): Promise<boolean> {
  try {
    const courseData = await db
      .select({ instructorId: course.instructorId })
      .from(course)
      .where(eq(course.id, courseId))
      .limit(1)

    if (courseData.length === 0) {
      return false
    }

    return courseData[0].instructorId === userId
  } catch (error) {
    logger.error('Error checking if user is instructor:', error)
    return false
  }
}

/**
 * Get user's role in a course
 */
export async function getUserCourseRole(
  userId: string,
  courseId: string
): Promise<CourseRole | null> {
  try {
    const member = await db
      .select({ role: courseMember.role })
      .from(courseMember)
      .where(and(eq(courseMember.courseId, courseId), eq(courseMember.userId, userId)))
      .limit(1)

    if (member.length === 0) {
      return null
    }

    return member[0].role as CourseRole
  } catch (error) {
    logger.error('Error getting user course role:', error)
    return null
  }
}

