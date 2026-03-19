'use client'

import { createLogger } from '@sim/logger'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useCourses } from '@/hooks/queries/courses'
import { useRouter, useParams } from 'next/navigation'

const logger = createLogger('CourseSelector')

interface CourseSelectorProps {
  currentWorkspaceId?: string
}

/**
 * Course selector component to switch between courses
 * Displays a dropdown to select a course and switches to its workspace
 */
export function CourseSelector({ currentWorkspaceId }: CourseSelectorProps) {
  const router = useRouter()
  const params = useParams()
  const { data: courses = [], isLoading } = useCourses()

  // Find current course based on workspace ID
  const currentCourse = courses.find((course) => course.workspaceId === currentWorkspaceId)

  const handleCourseSelect = (courseId: string) => {
    const selectedCourse = courses.find((c) => c.id === courseId)
    if (!selectedCourse) {
      logger.warn('Selected course not found:', courseId)
      return
    }

    logger.info(`Switching to course workspace: ${selectedCourse.workspaceId}`)

    // Switch to the course's workspace
    // Navigate to the workspace root page
    router.push(`/workspace/${selectedCourse.workspaceId}/w`)
  }

  // Don't render if no courses or loading
  if (isLoading) {
    return (
      <div className='p-4 border-b border-[var(--border)]'>
        <div className='text-xs text-muted-foreground mb-1'>当前课程</div>
        <div className='h-10 rounded-[8px] bg-muted animate-pulse' />
      </div>
    )
  }

  if (courses.length === 0) {
    return null
  }

  return (
    <div className='p-4 border-b border-[var(--border)]'>
      <label className='text-xs text-muted-foreground block mb-1'>当前课程</label>
      <Select
        onValueChange={handleCourseSelect}
        value={currentCourse?.id || ''}
      >
        <SelectTrigger className='w-full mt-1 h-9'>
          <SelectValue placeholder='选择课程...' />
        </SelectTrigger>
        <SelectContent>
          {courses.map((course) => (
            <SelectItem key={course.id} value={course.id}>
              {course.name}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  )
}

