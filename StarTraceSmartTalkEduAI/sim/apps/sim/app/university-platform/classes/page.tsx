'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import {
  Users,
  Plus,
  Search,
  Edit,
  Trash2,
  UserPlus,
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

interface Class {
  id: string
  name: string
  description: string
  courseId: string
  courseName: string
  studentCount: number
  maxStudents: number
  teacher: string
  createdAt: string
}

export default function ClassesPage() {
  const router = useRouter()
  const { data: session } = useSession()
  const [searchQuery, setSearchQuery] = useState('')
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [newClass, setNewClass] = useState({
    name: '',
    description: '',
    courseId: '',
    maxStudents: '30',
  })

  // 模拟班级数据
  const [classes, setClasses] = useState<Class[]>([
    {
      id: '1',
      name: 'AI基础班-2024春季',
      description: '人工智能基础课程春季班',
      courseId: '1',
      courseName: '人工智能基础',
      studentCount: 25,
      maxStudents: 30,
      teacher: '张教授',
      createdAt: '2024-01-15',
    },
    {
      id: '2',
      name: '数据结构班-2024春季',
      description: '数据结构与算法课程春季班',
      courseId: '2',
      courseName: '数据结构与算法',
      studentCount: 30,
      maxStudents: 35,
      teacher: '李教授',
      createdAt: '2024-01-20',
    },
  ])

  const userRole = localStorage.getItem('userRole') as 'teacher' | 'student' | null
  const isTeacher = userRole === 'teacher'

  const filteredClasses = classes.filter(
    (classItem) =>
      classItem.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      classItem.courseName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      classItem.description.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const handleCreateClass = () => {
    if (!newClass.name || !newClass.courseId) {
      alert('请填写班级名称和关联课程')
      return
    }

    const classItem: Class = {
      id: Date.now().toString(),
      name: newClass.name,
      description: newClass.description,
      courseId: newClass.courseId,
      courseName: '关联课程', // 实际应该从课程列表获取
      studentCount: 0,
      maxStudents: parseInt(newClass.maxStudents),
      teacher: session?.user?.email || '当前用户',
      createdAt: new Date().toISOString().split('T')[0],
    }

    setClasses([...classes, classItem])
    setNewClass({ name: '', description: '', courseId: '', maxStudents: '30' })
    setIsCreateDialogOpen(false)
  }

  const handleDeleteClass = (id: string) => {
    if (confirm('确定要删除这个班级吗？')) {
      setClasses(classes.filter((classItem) => classItem.id !== id))
    }
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
                班级管理
              </h1>
              <p className='text-muted-foreground'>
                {isTeacher
                  ? '创建和管理您的班级'
                  : '查看您所在的班级'}
              </p>
            </div>
            {isTeacher && (
              <Button onClick={() => setIsCreateDialogOpen(true)}>
                <Plus className='mr-2 h-4 w-4' />
                创建班级
              </Button>
            )}
          </div>
        </div>

        {/* 搜索栏 */}
        <div className='mb-6'>
          <div className='relative max-w-md'>
            <Search className='absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground' />
            <Input
              placeholder='搜索班级名称或课程...'
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className='pl-10'
            />
          </div>
        </div>

        {/* 班级列表 */}
        {filteredClasses.length === 0 ? (
          <Card>
            <CardContent className='flex flex-col items-center justify-center py-12'>
              <Users className='h-12 w-12 text-muted-foreground mb-4' />
              <p className='text-muted-foreground'>
                {searchQuery ? '没有找到匹配的班级' : '暂无班级'}
              </p>
            </CardContent>
          </Card>
        ) : (
          <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'>
            {filteredClasses.map((classItem) => (
              <Card
                key={classItem.id}
                className='hover:shadow-lg transition-all duration-200'
              >
                <CardHeader>
                  <div className='flex items-start justify-between mb-2'>
                    <div className='p-2 bg-primary/10 rounded-lg'>
                      <Users className='h-6 w-6 text-primary' />
                    </div>
                    <Badge variant='secondary'>
                      {classItem.courseName}
                    </Badge>
                  </div>
                  <CardTitle className='text-xl'>{classItem.name}</CardTitle>
                  <CardDescription>{classItem.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className='space-y-3 mb-4'>
                    <div className='flex items-center justify-between text-sm'>
                      <span className='text-muted-foreground'>学生人数</span>
                      <span className='font-medium'>
                        {classItem.studentCount} / {classItem.maxStudents}
                      </span>
                    </div>
                    <div className='w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2'>
                      <div
                        className='bg-primary h-2 rounded-full'
                        style={{
                          width: `${
                            (classItem.studentCount / classItem.maxStudents) *
                            100
                          }%`,
                        }}
                      />
                    </div>
                    <div className='flex items-center gap-1 text-sm text-muted-foreground'>
                      <Calendar className='h-4 w-4' />
                      <span>创建于 {classItem.createdAt}</span>
                    </div>
                  </div>
                  <div className='flex gap-2'>
                    <Button
                      className='flex-1'
                      onClick={() => {
                        router.push(
                          `/university-platform/classes/${classItem.id}`
                        )
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
                            alert('添加学生功能开发中')
                          }}
                        >
                          <UserPlus className='h-4 w-4' />
                        </Button>
                        <Button
                          variant='outline'
                          size='icon'
                          onClick={() => handleDeleteClass(classItem.id)}
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

        {/* 创建班级对话框 */}
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>创建新班级</DialogTitle>
              <DialogDescription>
                填写班级信息以创建新班级
              </DialogDescription>
            </DialogHeader>
            <div className='space-y-4 py-4'>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  班级名称 *
                </label>
                <Input
                  placeholder='例如：AI基础班-2024春季'
                  value={newClass.name}
                  onChange={(e) =>
                    setNewClass({ ...newClass, name: e.target.value })
                  }
                />
              </div>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  关联课程 *
                </label>
                <Input
                  placeholder='课程ID'
                  value={newClass.courseId}
                  onChange={(e) =>
                    setNewClass({ ...newClass, courseId: e.target.value })
                  }
                />
              </div>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  最大学生数
                </label>
                <Input
                  type='number'
                  placeholder='30'
                  value={newClass.maxStudents}
                  onChange={(e) =>
                    setNewClass({ ...newClass, maxStudents: e.target.value })
                  }
                />
              </div>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  班级描述
                </label>
                <textarea
                  className='w-full min-h-[100px] px-3 py-2 border border-input rounded-md bg-background'
                  placeholder='描述班级信息...'
                  value={newClass.description}
                  onChange={(e) =>
                    setNewClass({ ...newClass, description: e.target.value })
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
              <Button onClick={handleCreateClass}>创建</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  )
}

