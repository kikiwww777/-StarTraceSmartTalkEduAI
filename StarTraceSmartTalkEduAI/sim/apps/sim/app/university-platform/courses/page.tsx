'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import {
  BookOpen,
  Plus,
  Search,
  Edit,
  Trash2,
  Users,
  Calendar,
  ArrowLeft,
} from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Card,
  CardContent,
  CardDescription,
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
} from '@/components/ui/dialog'
import { Badge } from '@/components/emcn'
import { useSession } from '@/lib/auth/auth-client'

interface Course {
  id: string
  name: string
  description: string
  code: string
  teacher: string
  studentCount: number
  status: 'active' | 'draft' | 'archived'
  createdAt: string
}

export default function CoursesPage() {
  const router = useRouter()
  const { data: session } = useSession()
  const [searchQuery, setSearchQuery] = useState('')
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [newCourse, setNewCourse] = useState({
    name: '',
    description: '',
    code: '',
  })

  // 模拟课程数据
  const [courses, setCourses] = useState<Course[]>([
    {
      id: '1',
      name: '人工智能基础',
      description: '介绍人工智能的基本概念和应用',
      code: 'CS101',
      teacher: '张教授',
      studentCount: 45,
      status: 'active',
      createdAt: '2024-01-15',
    },
    {
      id: '2',
      name: '数据结构与算法',
      description: '学习常见数据结构和算法设计',
      code: 'CS201',
      teacher: '李教授',
      studentCount: 38,
      status: 'active',
      createdAt: '2024-01-20',
    },
    {
      id: '3',
      name: '机器学习',
      description: '深入学习机器学习理论和实践',
      code: 'CS301',
      teacher: '王教授',
      studentCount: 32,
      status: 'active',
      createdAt: '2024-02-01',
    },
  ])

  const userRole = localStorage.getItem('userRole') as 'teacher' | 'student' | null
  const isTeacher = userRole === 'teacher'

  const filteredCourses = courses.filter(
    (course) =>
      course.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      course.code.toLowerCase().includes(searchQuery.toLowerCase()) ||
      course.description.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const handleCreateCourse = () => {
    if (!newCourse.name || !newCourse.code) {
      alert('请填写课程名称和课程代码')
      return
    }

    const course: Course = {
      id: Date.now().toString(),
      name: newCourse.name,
      description: newCourse.description,
      code: newCourse.code,
      teacher: session?.user?.email || '当前用户',
      studentCount: 0,
      status: 'draft',
      createdAt: new Date().toISOString().split('T')[0],
    }

    setCourses([...courses, course])
    setNewCourse({ name: '', description: '', code: '' })
    setIsCreateDialogOpen(false)
  }

  const handleDeleteCourse = (id: string) => {
    if (confirm('确定要删除这门课程吗？')) {
      setCourses(courses.filter((course) => course.id !== id))
    }
  }

  const getStatusBadge = (status: Course['status']) => {
    const variants = {
      active: 'default',
      draft: 'secondary',
      archived: 'outline',
    } as const

    const labels = {
      active: '进行中',
      draft: '草稿',
      archived: '已归档',
    }

    return (
      <Badge variant={variants[status]}>{labels[status]}</Badge>
    )
  }

  return (
    <div className='min-h-screen bg-gray-50 dark:bg-gray-900'>
      <div className='container mx-auto px-4 py-8 max-w-7xl'>
        {/* 头部 */}
        <div className='mb-8'>
          <Button
            variant='ghost'
            onClick={() => router.push('/university-platform')}
            className='mb-4'
          >
            <ArrowLeft className='mr-2 h-4 w-4' />
            返回主页
          </Button>
          <div className='flex items-center justify-between'>
            <div>
              <h1 className='text-3xl font-bold text-gray-900 dark:text-white mb-2'>
                课程管理
              </h1>
              <p className='text-muted-foreground'>
                {isTeacher
                  ? '创建和管理您的课程'
                  : '浏览和选择您感兴趣的课程'}
              </p>
            </div>
            {isTeacher && (
              <Button onClick={() => setIsCreateDialogOpen(true)}>
                <Plus className='mr-2 h-4 w-4' />
                创建课程
              </Button>
            )}
          </div>
        </div>

        {/* 搜索栏 */}
        <div className='mb-6'>
          <div className='relative max-w-md'>
            <Search className='absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground' />
            <Input
              placeholder='搜索课程名称、代码或描述...'
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className='pl-10'
            />
          </div>
        </div>

        {/* 课程列表 */}
        {filteredCourses.length === 0 ? (
          <Card>
            <CardContent className='flex flex-col items-center justify-center py-12'>
              <BookOpen className='h-12 w-12 text-muted-foreground mb-4' />
              <p className='text-muted-foreground'>
                {searchQuery ? '没有找到匹配的课程' : '暂无课程'}
              </p>
            </CardContent>
          </Card>
        ) : (
          <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'>
            {filteredCourses.map((course) => (
              <Card
                key={course.id}
                className='hover:shadow-lg transition-all duration-200'
              >
                <CardHeader>
                  <div className='flex items-start justify-between mb-2'>
                    <div className='p-2 bg-primary/10 rounded-lg'>
                      <BookOpen className='h-6 w-6 text-primary' />
                    </div>
                    {getStatusBadge(course.status)}
                  </div>
                  <CardTitle className='text-xl'>{course.name}</CardTitle>
                  <CardDescription className='flex items-center gap-2'>
                    <span className='font-mono text-sm'>{course.code}</span>
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <p className='text-sm text-muted-foreground mb-4 line-clamp-2'>
                    {course.description}
                  </p>
                  <div className='flex items-center justify-between text-sm text-muted-foreground mb-4'>
                    <div className='flex items-center gap-1'>
                      <Users className='h-4 w-4' />
                      <span>{course.studentCount} 名学生</span>
                    </div>
                    <div className='flex items-center gap-1'>
                      <Calendar className='h-4 w-4' />
                      <span>{course.createdAt}</span>
                    </div>
                  </div>
                  <div className='flex gap-2'>
                    <Button
                      className='flex-1'
                      onClick={() => {
                        // 跳转到课程详情页
                        router.push(`/university-platform/courses/${course.id}`)
                      }}
                    >
                      查看详情
                    </Button>
                    {isTeacher && (
                      <>
                        <Button
                          variant='outline'
                          size='icon'
                          onClick={() => {
                            // 编辑课程
                            alert('编辑功能开发中')
                          }}
                        >
                          <Edit className='h-4 w-4' />
                        </Button>
                        <Button
                          variant='outline'
                          size='icon'
                          onClick={() => handleDeleteCourse(course.id)}
                        >
                          <Trash2 className='h-4 w-4' />
                        </Button>
                      </>
                    )}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}

        {/* 创建课程对话框 */}
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>创建新课程</DialogTitle>
              <DialogDescription>
                填写课程信息以创建新课程
              </DialogDescription>
            </DialogHeader>
            <div className='space-y-4 py-4'>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  课程名称 *
                </label>
                <Input
                  placeholder='例如：人工智能基础'
                  value={newCourse.name}
                  onChange={(e) =>
                    setNewCourse({ ...newCourse, name: e.target.value })
                  }
                />
              </div>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  课程代码 *
                </label>
                <Input
                  placeholder='例如：CS101'
                  value={newCourse.code}
                  onChange={(e) =>
                    setNewCourse({ ...newCourse, code: e.target.value })
                  }
                />
              </div>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  课程描述
                </label>
                <textarea
                  className='w-full min-h-[100px] px-3 py-2 border border-input rounded-md bg-background'
                  placeholder='描述课程内容和学习目标...'
                  value={newCourse.description}
                  onChange={(e) =>
                    setNewCourse({ ...newCourse, description: e.target.value })
                  }
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
              <Button onClick={handleCreateCourse}>创建</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  )
}

