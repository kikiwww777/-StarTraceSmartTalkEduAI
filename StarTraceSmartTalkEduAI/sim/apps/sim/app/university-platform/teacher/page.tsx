'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import {
  UserCog,
  BookOpen,
  Users,
  FileCheck,
  BarChart3,
  Bot,
  ArrowLeft,
  TrendingUp,
  Clock,
  Award,
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

export default function TeacherWorkspacePage() {
  const router = useRouter()
  const { data: session } = useSession()

  // 检查用户角色
  const userRole = localStorage.getItem('userRole')
  if (userRole !== 'teacher') {
    return (
      <div className='min-h-screen flex items-center justify-center'>
        <Card className='max-w-md'>
          <CardHeader>
            <CardTitle>访问受限</CardTitle>
            <CardDescription>
              您没有权限访问教师工作区，请先选择教师身份
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => router.push('/university-platform')}>
              返回主页
            </Button>
          </CardContent>
        </Card>
      </div>
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
          <div>
            <h1 className='text-3xl font-bold text-gray-900 dark:text-white mb-2'>
              教师工作区
            </h1>
            <p className='text-muted-foreground'>
              欢迎，{session?.user?.email}。这里是您的专属教学工作空间
            </p>
          </div>
        </div>

        {/* 统计卡片 */}
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8'>
          <Card>
            <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
              <CardTitle className='text-sm font-medium'>我的课程</CardTitle>
              <BookOpen className='h-4 w-4 text-muted-foreground' />
            </CardHeader>
            <CardContent>
              <div className='text-2xl font-bold'>8</div>
              <p className='text-xs text-muted-foreground mt-1'>
                活跃课程数量
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
              <CardTitle className='text-sm font-medium'>我的班级</CardTitle>
              <Users className='h-4 w-4 text-muted-foreground' />
            </CardHeader>
            <CardContent>
              <div className='text-2xl font-bold'>12</div>
              <p className='text-xs text-muted-foreground mt-1'>
                管理的班级数
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
              <CardTitle className='text-sm font-medium'>待批改作业</CardTitle>
              <FileCheck className='h-4 w-4 text-muted-foreground' />
            </CardHeader>
            <CardContent>
              <div className='text-2xl font-bold'>24</div>
              <p className='text-xs text-muted-foreground mt-1'>
                需要处理的作业
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
              <CardTitle className='text-sm font-medium'>学生总数</CardTitle>
              <TrendingUp className='h-4 w-4 text-muted-foreground' />
            </CardHeader>
            <CardContent>
              <div className='text-2xl font-bold'>156</div>
              <p className='text-xs text-muted-foreground mt-1'>
                所有班级学生数
              </p>
            </CardContent>
          </Card>
        </div>

        {/* 功能模块 */}
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8'>
          {/* 课程管理 */}
          <Card
            className='hover:shadow-lg transition-all duration-200 cursor-pointer'
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
                创建和管理您的课程，设置课程内容和要求
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
            className='hover:shadow-lg transition-all duration-200 cursor-pointer'
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
                创建和管理班级，添加和管理学生成员
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button className='w-full' variant='outline'>
                进入班级管理
              </Button>
            </CardContent>
          </Card>

          {/* 作业批改 */}
          <Card className='hover:shadow-lg transition-all duration-200'>
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-purple-100 dark:bg-purple-900 rounded-lg'>
                  <FileCheck className='h-6 w-6 text-purple-600 dark:text-purple-400' />
                </div>
                <CardTitle>作业批改</CardTitle>
              </div>
              <CardDescription>
                查看和批改学生提交的作业，提供反馈
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className='w-full'
                variant='outline'
                onClick={() => alert('作业批改功能开发中')}
              >
                进入作业批改
              </Button>
            </CardContent>
          </Card>

          {/* 教学数据分析 */}
          <Card className='hover:shadow-lg transition-all duration-200'>
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-orange-100 dark:bg-orange-900 rounded-lg'>
                  <BarChart3 className='h-6 w-6 text-orange-600 dark:text-orange-400' />
                </div>
                <CardTitle>教学数据分析</CardTitle>
              </div>
              <CardDescription>
                查看学生学习数据，分析教学效果
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className='w-full'
                variant='outline'
                onClick={() => alert('数据分析功能开发中')}
              >
                查看数据分析
              </Button>
            </CardContent>
          </Card>

          {/* AI教学助手 */}
          <Card
            className='hover:shadow-lg transition-all duration-200 cursor-pointer'
            onClick={() => router.push('/university-platform/agents')}
          >
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-pink-100 dark:bg-pink-900 rounded-lg'>
                  <Bot className='h-6 w-6 text-pink-600 dark:text-pink-400' />
                </div>
                <CardTitle>AI教学助手</CardTitle>
              </div>
              <CardDescription>
                使用AI智能体辅助教学，提高教学效率
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button className='w-full' variant='outline'>
                进入智能体
              </Button>
            </CardContent>
          </Card>

          {/* 教学资源 */}
          <Card className='hover:shadow-lg transition-all duration-200'>
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-teal-100 dark:bg-teal-900 rounded-lg'>
                  <Award className='h-6 w-6 text-teal-600 dark:text-teal-400' />
                </div>
                <CardTitle>教学资源</CardTitle>
              </div>
              <CardDescription>
                管理和分享教学资源，课件和资料
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className='w-full'
                variant='outline'
                onClick={() => alert('教学资源功能开发中')}
              >
                管理资源
              </Button>
            </CardContent>
          </Card>
        </div>

        {/* 最近活动 */}
        <Card>
          <CardHeader>
            <CardTitle>最近活动</CardTitle>
            <CardDescription>您的教学活动记录</CardDescription>
          </CardHeader>
          <CardContent>
            <div className='space-y-4'>
              {[
                {
                  action: '创建了新课程',
                  target: '机器学习进阶',
                  time: '2小时前',
                },
                {
                  action: '批改了作业',
                  target: '数据结构作业 #15',
                  time: '5小时前',
                },
                {
                  action: '添加了学生',
                  target: 'AI基础班-2024春季',
                  time: '1天前',
                },
                {
                  action: '发布了作业',
                  target: '算法设计作业',
                  time: '2天前',
                },
              ].map((activity, index) => (
                <div
                  key={index}
                  className='flex items-center gap-4 p-3 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800'
                >
                  <Clock className='h-4 w-4 text-muted-foreground' />
                  <div className='flex-1'>
                    <p className='text-sm'>
                      <span className='font-medium'>{activity.action}</span>{' '}
                      <span className='text-muted-foreground'>
                        {activity.target}
                      </span>
                    </p>
                    <p className='text-xs text-muted-foreground mt-1'>
                      {activity.time}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

