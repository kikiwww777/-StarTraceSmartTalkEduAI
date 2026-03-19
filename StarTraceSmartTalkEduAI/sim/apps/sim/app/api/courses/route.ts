import { db } from '@sim/db'
import { course, courseMember } from '@sim/db/schema'
import { createLogger } from '@sim/logger'
import { desc, eq } from 'drizzle-orm'
import { NextResponse } from 'next/server'
import { z } from 'zod'
import { getSession } from '@/lib/auth'
import { createWorkspace } from '@/lib/workspaces/create'

const logger = createLogger('Courses')

const createCourseSchema = z.object({
  name: z.string().trim().min(1, 'Name is required'),
  description: z.string().optional(),
})

// GET /api/courses - Get all courses for the current user
export async function GET() {
  const session = await getSession()

  if (!session?.user?.id) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 })
  }

  try {
    // Get all courses where the user is a member
    const userCourses = await db
      .select({
        course: course,
        memberRole: courseMember.role,
        joinedAt: courseMember.joinedAt,
      })
      .from(courseMember)
      .innerJoin(course, eq(courseMember.courseId, course.id))
      .where(eq(courseMember.userId, session.user.id))
      .orderBy(desc(course.createdAt))

    const courses = userCourses.map(({ course, memberRole, joinedAt }) => ({
      ...course,
      role: memberRole,
      joinedAt,
    }))

    return NextResponse.json({ courses })
  } catch (error) {
    logger.error('Error fetching courses:', error)
    return NextResponse.json({ error: 'Failed to fetch courses' }, { status: 500 })
  }
}

// POST /api/courses - Create a new course
export async function POST(req: Request) {
  const session = await getSession()

  if (!session?.user?.id) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 })
  }

  try {
    const { name, description } = createCourseSchema.parse(await req.json())

    // Create workspace for course isolation (named "Course: {CourseName}")
    const workspaceName = `Course: ${name}`
    const newWorkspace = await createWorkspace({
      name: workspaceName,
      ownerId: session.user.id,
      skipInitialWorkflow: true, // Skip initial workflow for course workspaces
    })

    // Create course record in a transaction
    const now = new Date()
    let newCourse

    await db.transaction(async (tx) => {
      // Insert course
      const [insertedCourse] = await tx
        .insert(course)
        .values({
          id: crypto.randomUUID(),
          name,
          description: description || null,
          instructorId: session.user.id,
          workspaceId: newWorkspace.id,
          createdAt: now,
          updatedAt: now,
        })
        .returning()

      newCourse = insertedCourse

      // Add creator as teacher in course_member table
      await tx.insert(courseMember).values({
        id: crypto.randomUUID(),
        courseId: newCourse.id,
        userId: session.user.id,
        role: 'teacher',
        joinedAt: now,
      })

      logger.info(
        `Created course ${newCourse.id} with workspace ${newWorkspace.id} for user ${session.user.id}`
      )
    })

    return NextResponse.json({
      course: {
        ...newCourse!,
        role: 'teacher',
        workspace: newWorkspace,
      },
    })
  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json({ error: error.errors[0].message }, { status: 400 })
    }

    logger.error('Error creating course:', error)
    return NextResponse.json({ error: 'Failed to create course' }, { status: 500 })
  }
}

