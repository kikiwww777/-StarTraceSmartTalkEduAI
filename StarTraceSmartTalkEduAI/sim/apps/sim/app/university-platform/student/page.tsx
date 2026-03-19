'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import {
  GraduationCap,
  BookOpen,
  Users,
  FileText,
  Award,
  Bot,
  ArrowLeft,
  TrendingUp,
  Clock,
  CheckCircle2,
} from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { Progress } from '@/components/ui/progress'
import { useSession } from '@/lib/auth/auth-client'

export default function StudentWorkspacePage() {
  const router = useRouter()
  const { data: session } = useSession()

  // 检查用户角色
  const userRole = localStorage.getItem('userRole')
  if (userRole !== 'student') {
    return (
      <div className='min-h-screen flex items-center justify-center'>
        <Card className='max-w-md'>
          <CardHeader>
            <CardTitle>访问受限</CardTitle>
            <CardDescription>
              您没有权限访问学生工作区，请先选择学生身份
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
              学生工作区
            </h1>
            <p className='text-muted-foreground'>
              欢迎，{session?.user?.email}。这里是您的专属学习空间
            </p>
          </div>
        </div>

        {/* 学习进度概览 */}
        <Card className='mb-8'>
          <CardHeader>
            <CardTitle>学习进度概览</CardTitle>
            <CardDescription>您的整体学习进度</CardDescription>
          </CardHeader>
          <CardContent>
            <div className='space-y-4'>
              <div>
                <div className='flex justify-between text-sm mb-2'>
                  <span className='text-muted-foreground'>总体进度</span>
                  <span className='font-medium'>68%</span>
                </div>
                <Progress value={68} className='h-3' />
              </div>
              <div className='grid grid-cols-3 gap-4 text-center'>
                <div>
                  <div className='text-2xl font-bold'>5</div>
                  <div className='text-xs text-muted-foreground'>进行中课程</div>
                </div>
                <div>
                  <div className='text-2xl font-bold'>12</div>
                  <div className='text-xs text-muted-foreground'>已完成作业</div>
                </div>
                <div>
                  <div className='text-2xl font-bold'>3</div>
                  <div className='text-xs text-muted-foreground'>待完成作业</div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 统计卡片 */}
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8'>
          <Card>
            <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
              <CardTitle className='text-sm font-medium'>我的课程</CardTitle>
              <BookOpen className='h-4 w-4 text-muted-foreground' />
            </CardHeader>
            <CardContent>
              <div className='text-2xl font-bold'>5</div>
              <p className='text-xs text-muted-foreground mt-1'>
                正在学习的课程
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
              <CardTitle className='text-sm font-medium'>我的班级</CardTitle>
              <Users className='h-4 w-4 text-muted-foreground' />
            </CardHeader>
            <CardContent>
              <div className='text-2xl font-bold'>3</div>
              <p className='text-xs text-muted-foreground mt-1'>
                加入的班级数
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
              <CardTitle className='text-sm font-medium'>已完成作业</CardTitle>
              <CheckCircle2 className='h-4 w-4 text-muted-foreground' />
            </CardHeader>
            <CardContent>
              <div className='text-2xl font-bold'>12</div>
              <p className='text-xs text-muted-foreground mt-1'>
                已提交的作业数
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
              <CardTitle className='text-sm font-medium'>平均成绩</CardTitle>
              <TrendingUp className='h-4 w-4 text-muted-foreground' />
            </CardHeader>
            <CardContent>
              <div className='text-2xl font-bold'>85</div>
              <p className='text-xs text-muted-foreground mt-1'>
                当前平均分
              </p>
            </CardContent>
          </Card>
        </div>

        {/* 功能模块 */}
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8'>
          {/* 课程学习 */}
          <Card
            className='hover:shadow-lg transition-all duration-200 cursor-pointer'
            onClick={() => router.push('/university-platform/courses')}
          >
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-blue-100 dark:bg-blue-900 rounded-lg'>
                  <BookOpen className='h-6 w-6 text-blue-600 dark:text-blue-400' />
                </div>
                <CardTitle>课程学习</CardTitle>
              </div>
              <CardDescription>
                浏览和选择课程，查看课程内容和学习资料
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button className='w-full' variant='outline'>
                进入课程学习
              </Button>
            </CardContent>
          </Card>

          {/* 班级互动 */}
          <Card
            className='hover:shadow-lg transition-all duration-200 cursor-pointer'
            onClick={() => router.push('/university-platform/classes')}
          >
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-green-100 dark:bg-green-900 rounded-lg'>
                  <Users className='h-6 w-6 text-green-600 dark:text-green-400' />
                </div>
                <CardTitle>班级互动</CardTitle>
              </div>
              <CardDescription>
                查看所在班级，与同学和老师互动交流
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button className='w-full' variant='outline'>
                进入班级
              </Button>
            </CardContent>
          </Card>

          {/* 作业管理 */}
          <Card className='hover:shadow-lg transition-all duration-200'>
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-purple-100 dark:bg-purple-900 rounded-lg'>
                  <FileText className='h-6 w-6 text-purple-600 dark:text-purple-400' />
                </div>
                <CardTitle>作业管理</CardTitle>
              </div>
              <CardDescription>
                查看作业要求，提交作业，查看成绩和反馈
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className='w-full'
                variant='outline'
                onClick={() => alert('作业管理功能开发中')}
              >
                进入作业管理
              </Button>
            </CardContent>
          </Card>

          {/* 学习进度 */}
          <Card className='hover:shadow-lg transition-all duration-200'>
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-orange-100 dark:bg-orange-900 rounded-lg'>
                  <TrendingUp className='h-6 w-6 text-orange-600 dark:text-orange-400' />
                </div>
                <CardTitle>学习进度</CardTitle>
              </div>
              <CardDescription>
                跟踪学习进度，查看学习统计和分析
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className='w-full'
                variant='outline'
                onClick={() => router.push('/dashboard')}
              >
                查看学习进度
              </Button>
            </CardContent>
          </Card>

          {/* AI学习助手 */}
          <Card
            className='hover:shadow-lg transition-all duration-200 cursor-pointer'
            onClick={() => router.push('/university-platform/agents')}
          >
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-pink-100 dark:bg-pink-900 rounded-lg'>
                  <Bot className='h-6 w-6 text-pink-600 dark:text-pink-400' />
                </div>
                <CardTitle>AI学习助手</CardTitle>
              </div>
              <CardDescription>
                使用AI智能体辅助学习，解答疑问
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button className='w-full' variant='outline'>
                进入智能体
              </Button>
            </CardContent>
          </Card>

          {/* 成绩与证书 */}
          <Card className='hover:shadow-lg transition-all duration-200'>
            <CardHeader>
              <div className='flex items-center gap-3 mb-2'>
                <div className='p-3 bg-teal-100 dark:bg-teal-900 rounded-lg'>
                  <Award className='h-6 w-6 text-teal-600 dark:text-teal-400' />
                </div>
                <CardTitle>成绩与证书</CardTitle>
              </div>
              <CardDescription>
                查看成绩单，获取学习证书和成就
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className='w-full'
                variant='outline'
                onClick={() => alert('成绩与证书功能开发中')}
              >
                查看成绩
              </Button>
            </CardContent>
          </Card>
        </div>

        {/* 最近活动 */}
        <Card>
          <CardHeader>
            <CardTitle>最近活动</CardTitle>
            <CardDescription>您的学习活动记录</CardDescription>
          </CardHeader>
          <CardContent>
            <div className='space-y-4'>
              {[
                {
                  action: '完成了作业',
                  target: '数据结构作业 #15',
                  time: '1小时前',
                },
                {
                  action: '加入了课程',
                  target: '机器学习进阶',
                  time: '3小时前',
                },
                {
                  action: '提交了作业',
                  target: '算法设计作业',
                  time: '1天前',
                },
                {
                  action: '获得了成绩',
                  target: '人工智能基础 - 92分',
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

