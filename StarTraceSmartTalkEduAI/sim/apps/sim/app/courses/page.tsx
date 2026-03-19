'use client'

import { useState } from 'react'
import { createLogger } from '@sim/logger'
import { Plus, BookOpen, Users, Calendar } from 'lucide-react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/emcn'
import { Badge } from '@/components/emcn'
import { useCourses, useCreateCourse, type Course } from '@/hooks/queries/courses'

const logger = createLogger('CoursesPage')

export default function CoursesPage() {
  const router = useRouter()
  const { data: courses = [], isLoading } = useCourses()
  const createCourse = useCreateCourse()
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [courseName, setCourseName] = useState('')
  const [courseDescription, setCourseDescription] = useState('')

  const handleCreateCourse = async () => {
    if (!courseName.trim()) {
      return
    }

    try {
      const newCourse = await createCourse.mutateAsync({
        name: courseName.trim(),
        description: courseDescription.trim() || undefined,
      })

      logger.info('Course created:', newCourse)

      // Reset form
      setCourseName('')
      setCourseDescription('')
      setIsCreateDialogOpen(false)

      // Navigate to the course's workspace
      router.push(`/workspace/${newCourse.workspaceId}/w`)
    } catch (error) {
      logger.error('Failed to create course:', error)
    }
  }

  const handleCourseClick = (course: Course) => {
    logger.info('Navigating to course workspace:', course.workspaceId)
    router.push(`/workspace/${course.workspaceId}/w`)
  }

  return (
    <div className='container mx-auto py-8 px-4 max-w-7xl'>
      <div className='flex items-center justify-between mb-8'>
        <div>
          <h1 className='text-3xl font-bold mb-2'>课程管理</h1>
          <p className='text-muted-foreground'>创建和管理您的课程</p>
        </div>
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button>
              <Plus className='mr-2 h-4 w-4' />
              创建课程
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>创建新课程</DialogTitle>
              <DialogDescription>
                创建一个新课程，系统将自动为其创建一个隔离的工作区
              </DialogDescription>
            </DialogHeader>
            <div className='space-y-4 py-4'>
              <div className='space-y-2'>
                <Label htmlFor='course-name'>课程名称 *</Label>
                <Input
                  id='course-name'
                  placeholder='例如：人工智能导论'
                  value={courseName}
                  onChange={(e) => setCourseName(e.target.value)}
                />
              </div>
              <div className='space-y-2'>
                <Label htmlFor='course-description'>课程描述</Label>
                <Textarea
                  id='course-description'
                  placeholder='输入课程描述...'
                  value={courseDescription}
                  onChange={(e) => setCourseDescription(e.target.value)}
                  rows={4}
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                variant='outline'
                onClick={() => setIsCreateDialogOpen(false)}
              >
                取消
              </Button>
              <Button
                onClick={handleCreateCourse}
                disabled={!courseName.trim() || createCourse.isPending}
              >
                {createCourse.isPending ? '创建中...' : '创建课程'}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      {isLoading ? (
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'>
          {[1, 2, 3].map((i) => (
            <Card key={i} className='animate-pulse'>
              <CardHeader>
                <div className='h-6 bg-muted rounded w-3/4' />
                <div className='h-4 bg-muted rounded w-1/2 mt-2' />
              </CardHeader>
              <CardContent>
                <div className='h-4 bg-muted rounded w-full' />
                <div className='h-4 bg-muted rounded w-2/3 mt-2' />
              </CardContent>
            </Card>
          ))}
        </div>
      ) : courses.length === 0 ? (
        <Card>
          <CardContent className='flex flex-col items-center justify-center py-12'>
            <BookOpen className='h-12 w-12 text-muted-foreground mb-4' />
            <p className='text-muted-foreground mb-4 text-lg'>还没有课程</p>
            <Button onClick={() => setIsCreateDialogOpen(true)}>
              <Plus className='mr-2 h-4 w-4' />
              创建第一个课程
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'>
          {courses.map((course) => (
            <Card
              key={course.id}
              className='cursor-pointer hover:shadow-lg transition-all duration-200 hover:border-primary/50'
              onClick={() => handleCourseClick(course)}
            >
              <CardHeader>
                <div className='flex items-start justify-between'>
                  <div className='flex-1'>
                    <CardTitle className='text-xl mb-2'>{course.name}</CardTitle>
                    <CardDescription className='flex items-center gap-2 mt-1'>
                      <Calendar className='h-3 w-3' />
                      {new Date(course.createdAt).toLocaleDateString('zh-CN')}
                    </CardDescription>
                  </div>
                  {course.role && (
                    <Badge
                      variant={
                        course.role === 'teacher'
                          ? 'default'
                          : course.role === 'admin'
                            ? 'secondary'
                            : 'outline'
                      }
                    >
                      {course.role === 'teacher'
                        ? '教师'
                        : course.role === 'admin'
                          ? '管理员'
                          : '学生'}
                    </Badge>
                  )}
                </div>
              </CardHeader>
              <CardContent>
                {course.description ? (
                  <p className='text-sm text-muted-foreground line-clamp-3'>
                    {course.description}
                  </p>
                ) : (
                  <p className='text-sm text-muted-foreground italic'>暂无描述</p>
                )}
              </CardContent>
              <CardFooter className='flex items-center justify-between pt-4 border-t'>
                <div className='flex items-center gap-4 text-xs text-muted-foreground'>
                  <div className='flex items-center gap-1'>
                    <Users className='h-3 w-3' />
                    <span>工作区</span>
                  </div>
                </div>
                <Link
                  href={`/courses/${course.id}/members`}
                  onClick={(e) => e.stopPropagation()}
                >
                  <Button variant='ghost' size='sm'>
                    管理成员
                  </Button>
                </Link>
              </CardFooter>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
