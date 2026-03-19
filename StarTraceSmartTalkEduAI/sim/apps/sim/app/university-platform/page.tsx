'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import {
  BookOpen,
  Users,
  GraduationCap,
  UserCog,
  Bot,
  LogIn,
  LogOut,
  Menu,
  X,
} from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { useSession } from '@/lib/auth/auth-client'

export default function UniversityPlatformPage() {
  const router = useRouter()
  const { data: session } = useSession()
  const [userRole, setUserRole] = useState<'teacher' | 'student' | null>(null)
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  useEffect(() => {
    // 从localStorage或session中获取用户角色
    // 这里使用模拟数据，实际应该从API获取
    const role = localStorage.getItem('userRole') as 'teacher' | 'student' | null
    setUserRole(role)
  }, [])

  const handleLogin = () => {
    router.push('/login')
  }

  const handleLogout = () => {
    localStorage.removeItem('userRole')
    setUserRole(null)
    router.push('/login')
  }

  const handleRoleSelect = (role: 'teacher' | 'student') => {
    localStorage.setItem('userRole', role)
    setUserRole(role)
  }

  return (
    <div className='min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900'>
      {/* 导航栏 */}
      <nav className='bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 shadow-sm'>
        <div className='container mx-auto px-4 py-4'>
          <div className='flex items-center justify-between'>
            <div className='flex items-center gap-3'>
              <GraduationCap className='h-8 w-8 text-primary' />
              <h1 className='text-2xl font-bold text-gray-900 dark:text-white'>
                高校智能教育平台
              </h1>
            </div>
            <div className='hidden md:flex items-center gap-4'>
              {session?.user ? (
                <>
                  <span className='text-sm text-gray-600 dark:text-gray-400'>
                    {session.user.email}
                  </span>
                  {userRole && (
                    <span className='px-3 py-1 text-xs font-medium rounded-full bg-primary/10 text-primary'>
                      {userRole === 'teacher' ? '教师' : '学生'}
                    </span>
                  )}
                  <Button variant='outline' onClick={handleLogout}>
                    <LogOut className='mr-2 h-4 w-4' />
                    退出登录
                  </Button>
                </>
              ) : (
                <Button onClick={handleLogin}>
                  <LogIn className='mr-2 h-4 w-4' />
                  登录
                </Button>
              )}
            </div>
            <Button
              variant='ghost'
              className='md:hidden'
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              {isMenuOpen ? <X className='h-6 w-6' /> : <Menu className='h-6 w-6' />}
            </Button>
          </div>
          {isMenuOpen && (
            <div className='md:hidden mt-4 pb-4 space-y-2'>
              {session?.user ? (
                <>
                  <div className='text-sm text-gray-600 dark:text-gray-400 py-2'>
                    {session.user.email}
                  </div>
                  <Button variant='outline' className='w-full' onClick={handleLogout}>
                    <LogOut className='mr-2 h-4 w-4' />
                    退出登录
                  </Button>
                </>
              ) : (
                <Button className='w-full' onClick={handleLogin}>
                  <LogIn className='mr-2 h-4 w-4' />
                  登录
                </Button>
              )}
            </div>
          )}
        </div>
      </nav>

      {/* 主内容区 */}
      <div className='container mx-auto px-4 py-12 max-w-7xl'>
        {/* 欢迎区域 */}
        <div className='text-center mb-12'>
          <h2 className='text-4xl font-bold text-gray-900 dark:text-white mb-4'>
            欢迎使用高校智能教育平台
          </h2>
          <p className='text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto'>
            集成AI智能体的现代化教育管理平台，为教师和学生提供全方位的教学和学习支持
          </p>
        </div>

        {/* 角色选择（未登录或未选择角色时显示） */}
        {!session?.user && (
          <Card className='mb-8 max-w-md mx-auto'>
            <CardHeader>
              <CardTitle>请先登录</CardTitle>
              <CardDescription>登录后选择您的身份角色</CardDescription>
            </CardHeader>
            <CardContent>
              <Button className='w-full' onClick={handleLogin}>
                <LogIn className='mr-2 h-4 w-4' />
                前往登录
              </Button>
            </CardContent>
          </Card>
        )}

        {session?.user && !userRole && (
          <Card className='mb-8 max-w-md mx-auto'>
            <CardHeader>
              <CardTitle>选择您的身份</CardTitle>
              <CardDescription>请选择您的角色以访问相应功能</CardDescription>
            </CardHeader>
            <CardContent className='space-y-4'>
              <Button
                className='w-full h-auto py-6'
                variant='outline'
                onClick={() => handleRoleSelect('teacher')}
              >
                <UserCog className='mr-2 h-5 w-5' />
                <div className='text-left'>
                  <div className='font-semibold'>教师</div>
                  <div className='text-sm text-muted-foreground'>管理课程和班级</div>
                </div>
              </Button>
              <Button
                className='w-full h-auto py-6'
                variant='outline'
                onClick={() => handleRoleSelect('student')}
              >
                <GraduationCap className='mr-2 h-5 w-5' />
                <div className='text-left'>
                  <div className='font-semibold'>学生</div>
                  <div className='text-sm text-muted-foreground'>学习课程和参与班级</div>
                </div>
              </Button>
            </CardContent>
          </Card>
        )}

        {/* 功能卡片网格 */}
        {session?.user && userRole && (
          <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8'>
            {/* 课程管理 */}
            <Card
              className='hover:shadow-lg transition-all duration-200 cursor-pointer border-2 hover:border-primary'
              onClick={() => router.push('/university-platform/courses')}
            >
              <CardHeader>
                <div className='flex items-center gap-3 mb-2'>
                  <div className='p-3 bg-blue-100 dark:bg-blue-900 rounded-lg'>
                    <BookOpen className='h-6 w-6 text-blue-600 dark:text-blue-400' />
                  </div>
                  <CardTitle>课程管理</CardTitle>
                </div>
                <CardDescription>
                  {userRole === 'teacher'
                    ? '创建和管理课程，设置课程内容与要求'
                    : '浏览和选择课程，查看课程详情'}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Button className='w-full' variant='outline'>
                  进入课程管理
                </Button>
              </CardContent>
            </Card>

            {/* 班级管理 */}
            <Card
              className='hover:shadow-lg transition-all duration-200 cursor-pointer border-2 hover:border-primary'
              onClick={() => router.push('/university-platform/classes')}
            >
              <CardHeader>
                <div className='flex items-center gap-3 mb-2'>
                  <div className='p-3 bg-green-100 dark:bg-green-900 rounded-lg'>
                    <Users className='h-6 w-6 text-green-600 dark:text-green-400' />
                  </div>
                  <CardTitle>班级管理</CardTitle>
                </div>
                <CardDescription>
                  {userRole === 'teacher'
                    ? '创建和管理班级，添加学生成员'
                    : '查看所在班级，与同学互动'}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Button className='w-full' variant='outline'>
                  进入班级管理
                </Button>
              </CardContent>
            </Card>

            {/* 智能体操作 */}
            <Card
              className='hover:shadow-lg transition-all duration-200 cursor-pointer border-2 hover:border-primary'
              onClick={() => router.push('/university-platform/agents')}
            >
              <CardHeader>
                <div className='flex items-center gap-3 mb-2'>
                  <div className='p-3 bg-purple-100 dark:bg-purple-900 rounded-lg'>
                    <Bot className='h-6 w-6 text-purple-600 dark:text-purple-400' />
                  </div>
                  <CardTitle>智能体操作</CardTitle>
                </div>
                <CardDescription>
                  {userRole === 'teacher'
                    ? '创建和管理AI智能体，辅助教学'
                    : '使用AI智能体辅助学习'}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Button className='w-full' variant='outline'>
                  进入智能体
                </Button>
              </CardContent>
            </Card>
          </div>
        )}

        {/* 工作区入口 */}
        {session?.user && userRole && (
          <div className='grid grid-cols-1 md:grid-cols-2 gap-6'>
            {/* 教师工作区 */}
            {userRole === 'teacher' && (
              <Card
                className='hover:shadow-xl transition-all duration-200 cursor-pointer border-2 border-primary/20 hover:border-primary bg-gradient-to-br from-blue-50 to-purple-50 dark:from-blue-950 dark:to-purple-950'
                onClick={() => router.push('/university-platform/teacher')}
              >
                <CardHeader>
                  <div className='flex items-center gap-3 mb-2'>
                    <div className='p-4 bg-primary/10 rounded-lg'>
                      <UserCog className='h-8 w-8 text-primary' />
                    </div>
                    <div>
                      <CardTitle className='text-2xl'>教师工作区</CardTitle>
                      <CardDescription className='mt-1'>
                        专属教师工作空间
                      </CardDescription>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <ul className='space-y-2 text-sm text-gray-600 dark:text-gray-400 mb-4'>
                    <li className='flex items-center gap-2'>
                      <span className='w-1.5 h-1.5 bg-primary rounded-full' />
                      课程创建与管理
                    </li>
                    <li className='flex items-center gap-2'>
                      <span className='w-1.5 h-1.5 bg-primary rounded-full' />
                      学生作业批改
                    </li>
                    <li className='flex items-center gap-2'>
                      <span className='w-1.5 h-1.5 bg-primary rounded-full' />
                      教学数据分析
                    </li>
                    <li className='flex items-center gap-2'>
                      <span className='w-1.5 h-1.5 bg-primary rounded-full' />
                      AI教学助手
                    </li>
                  </ul>
                  <Button className='w-full' size='lg'>
                    进入教师工作区
                  </Button>
                </CardContent>
              </Card>
            )}

            {/* 学生工作区 */}
            {userRole === 'student' && (
              <Card
                className='hover:shadow-xl transition-all duration-200 cursor-pointer border-2 border-primary/20 hover:border-primary bg-gradient-to-br from-green-50 to-blue-50 dark:from-green-950 dark:to-blue-950'
                onClick={() => router.push('/university-platform/student')}
              >
                <CardHeader>
                  <div className='flex items-center gap-3 mb-2'>
                    <div className='p-4 bg-primary/10 rounded-lg'>
                      <GraduationCap className='h-8 w-8 text-primary' />
                    </div>
                    <div>
                      <CardTitle className='text-2xl'>学生工作区</CardTitle>
                      <CardDescription className='mt-1'>
                        专属学生学习空间
                      </CardDescription>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <ul className='space-y-2 text-sm text-gray-600 dark:text-gray-400 mb-4'>
                    <li className='flex items-center gap-2'>
                      <span className='w-1.5 h-1.5 bg-primary rounded-full' />
                      课程学习与作业
                    </li>
                    <li className='flex items-center gap-2'>
                      <span className='w-1.5 h-1.5 bg-primary rounded-full' />
                      学习进度跟踪
                    </li>
                    <li className='flex items-center gap-2'>
                      <span className='w-1.5 h-1.5 bg-primary rounded-full' />
                      成绩与反馈
                    </li>
                    <li className='flex items-center gap-2'>
                      <span className='w-1.5 h-1.5 bg-primary rounded-full' />
                      AI学习助手
                    </li>
                  </ul>
                  <Button className='w-full' size='lg'>
                    进入学生工作区
                  </Button>
                </CardContent>
              </Card>
            )}
          </div>
        )}

        {/* 平台介绍 */}
        {!session?.user && (
          <div className='mt-12 grid grid-cols-1 md:grid-cols-3 gap-6'>
            <Card>
              <CardHeader>
                <BookOpen className='h-8 w-8 text-primary mb-2' />
                <CardTitle>课程管理</CardTitle>
              </CardHeader>
              <CardContent>
                <p className='text-sm text-muted-foreground'>
                  教师可以创建和管理课程，学生可以浏览和选择课程，实现完整的课程生命周期管理
                </p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <Users className='h-8 w-8 text-primary mb-2' />
                <CardTitle>班级管理</CardTitle>
              </CardHeader>
              <CardContent>
                <p className='text-sm text-muted-foreground'>
                  灵活的班级组织和管理功能，支持学生分组、作业分发和班级互动
                </p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <Bot className='h-8 w-8 text-primary mb-2' />
                <CardTitle>AI智能体</CardTitle>
              </CardHeader>
              <CardContent>
                <p className='text-sm text-muted-foreground'>
                  集成AI智能体技术，为教学和学习提供智能化的辅助和支持
                </p>
              </CardContent>
            </Card>
          </div>
        )}
      </div>
    </div>
  )
}

