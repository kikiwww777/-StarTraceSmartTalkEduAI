import { db } from '@sim/db'
import { courseMember, user } from '@sim/db/schema'
import { createLogger } from '@sim/logger'
import { and, eq } from 'drizzle-orm'
import type { NextRequest } from 'next/server'
import { NextResponse } from 'next/server'
import { z } from 'zod'
import { checkCoursePermission } from '../middleware'
import { findUserByIdOrEmail } from '@/lib/courses/utils'

const logger = createLogger('CourseMembers')

const addMemberSchema = z.object({
  userId: z.string().optional(),
  email: z.string().email().optional(),
}).refine(data => data.userId || data.email, {
  message: 'Either userId or email is required',
})

// POST /api/courses/:id/members - Add a member to a course
export async function POST(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  const { id: courseId } = await params

  // Check if user has permission (must be teacher or admin)
  const permissionResult = await checkCoursePermission(request, courseId, 'teacher')
  
  if (permissionResult.error) {
    return NextResponse.json(
      { error: permissionResult.error.message },
      { status: permissionResult.error.status }
    )
  }

  try {
    const body = await request.json()
    const { userId, email } = addMemberSchema.parse(body)

    // Find user by ID or email
    const identifier = userId || email!
    const targetUser = await findUserByIdOrEmail(identifier)

    if (!targetUser) {
      return NextResponse.json(
        { error: `User not found: ${identifier}` },
        { status: 404 }
      )
    }

    const targetUserId = targetUser.id

    // Check if user is already a member of the course
    const existingMembers = await db
      .select()
      .from(courseMember)
      .where(
        and(
          eq(courseMember.courseId, courseId),
          eq(courseMember.userId, targetUserId)
        )
      )
      .limit(1)

    if (existingMembers.length > 0) {
      return NextResponse.json(
        { error: 'User is already a member of this course' },
        { status: 409 }
      )
    }

    // Add user as student to the course
    const now = new Date()
    const [newMember] = await db
      .insert(courseMember)
      .values({
        id: crypto.randomUUID(),
        courseId,
        userId: targetUserId,
        role: 'student',
        joinedAt: now,
      })
      .returning()

    logger.info(
      `Added user ${targetUserId} (${targetUser.email}) as student to course ${courseId} by ${permissionResult.userId}`
    )

    return NextResponse.json({
      member: {
        ...newMember,
        email: targetUser.email,
      },
    })
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json(
        { error: error.errors[0].message },
        { status: 400 }
      )
    }

    logger.error('Error adding course member:', error)
    return NextResponse.json(
      { error: 'Failed to add course member' },
      { status: 500 }
    )
  }
}

// GET /api/courses/:id/members - Get all members of a course
export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  const { id: courseId } = await params

  // Check if user has permission (must be a member)
  const permissionResult = await checkCoursePermission(request, courseId)
  
  if (permissionResult.error) {
    return NextResponse.json(
      { error: permissionResult.error.message },
      { status: permissionResult.error.status }
    )
  }

  try {
    // Get all members of the course with user information
    const membersWithUsers = await db
      .select({
        id: courseMember.id,
        userId: courseMember.userId,
        role: courseMember.role,
        joinedAt: courseMember.joinedAt,
        userEmail: user.email,
        userName: user.name,
        userImage: user.image,
      })
      .from(courseMember)
      .innerJoin(user, eq(courseMember.userId, user.id))
      .where(eq(courseMember.courseId, courseId))
      .orderBy(courseMember.joinedAt)

    const members = membersWithUsers.map(({ id, userId, role, joinedAt, userEmail, userName, userImage }) => ({
      id,
      userId,
      role,
      joinedAt,
      email: userEmail,
      name: userName,
      image: userImage,
    }))

    return NextResponse.json({ members })
  } catch (error) {
    logger.error('Error fetching course members:', error)
    return NextResponse.json(
      { error: 'Failed to fetch course members' },
      { status: 500 }
    )
  }
}

