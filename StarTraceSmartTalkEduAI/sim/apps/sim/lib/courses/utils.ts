import { db } from '@sim/db'
import { user } from '@sim/db/schema'
import { createLogger } from '@sim/logger'
import { eq } from 'drizzle-orm'

const logger = createLogger('CourseUtils')

/**
 * Find user by email address
 * @param email - User email address
 * @returns User object with id and email, or null if not found
 */
export async function findUserByEmail(email: string): Promise<{ id: string; email: string } | null> {
  try {
    const users = await db
      .select({ id: user.id, email: user.email })
      .from(user)
      .where(eq(user.email, email.toLowerCase().trim()))
      .limit(1)

    if (users.length === 0) {
      logger.warn(`No user found with email: ${email}`)
      return null
    }

    return { id: users[0].id, email: users[0].email }
  } catch (error) {
    logger.error('Error finding user by email:', error)
    return null
  }
}

/**
 * Find user by ID or email
 * @param identifier - User ID or email address
 * @returns User object with id and email, or null if not found
 */
export async function findUserByIdOrEmail(
  identifier: string
): Promise<{ id: string; email: string } | null> {
  // First try as ID (direct lookup)
  try {
    const users = await db
      .select({ id: user.id, email: user.email })
      .from(user)
      .where(eq(user.id, identifier))
      .limit(1)

    if (users.length > 0) {
      return { id: users[0].id, email: users[0].email }
    }
  } catch (error) {
    // If ID lookup fails, try email
    logger.debug('ID lookup failed, trying email:', error)
  }

  // Try as email
  return findUserByEmail(identifier)
}

