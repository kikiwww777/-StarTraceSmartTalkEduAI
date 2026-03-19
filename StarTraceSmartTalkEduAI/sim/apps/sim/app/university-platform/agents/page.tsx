'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import {
  Bot,
  Plus,
  Search,
  Play,
  Edit,
  Trash2,
  ArrowLeft,
  Sparkles,
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

interface Agent {
  id: string
  name: string
  description: string
  type: 'teaching' | 'learning' | 'grading' | 'assistant'
  status: 'active' | 'inactive'
  createdAt: string
}

export default function AgentsPage() {
  const router = useRouter()
  const [searchQuery, setSearchQuery] = useState('')
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [newAgent, setNewAgent] = useState({
    name: '',
    description: '',
    type: 'assistant' as Agent['type'],
  })

  const userRole = localStorage.getItem('userRole') as 'teacher' | 'student' | null

  // 模拟智能体数据
  const [agents, setAgents] = useState<Agent[]>([
    {
      id: '1',
      name: '课程助手',
      description: '帮助学生解答课程相关问题',
      type: 'learning',
      status: 'active',
      createdAt: '2024-01-15',
    },
    {
      id: '2',
      name: '作业批改助手',
      description: '自动批改学生作业并提供反馈',
      type: 'grading',
      status: 'active',
      createdAt: '2024-01-20',
    },
    {
      id: '3',
      name: '学习规划助手',
      description: '为学生制定个性化学习计划',
      type: 'learning',
      status: 'active',
      createdAt: '2024-02-01',
    },
  ])

  const filteredAgents = agents.filter(
    (agent) =>
      agent.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      agent.description.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const handleCreateAgent = () => {
    if (!newAgent.name) {
      alert('请填写智能体名称')
      return
    }

    const agent: Agent = {
      id: Date.now().toString(),
      name: newAgent.name,
      description: newAgent.description,
      type: newAgent.type,
      status: 'active',
      createdAt: new Date().toISOString().split('T')[0],
    }

    setAgents([...agents, agent])
    setNewAgent({ name: '', description: '', type: 'assistant' })
    setIsCreateDialogOpen(false)
  }

  const handleDeleteAgent = (id: string) => {
    if (confirm('确定要删除这个智能体吗？')) {
      setAgents(agents.filter((agent) => agent.id !== id))
    }
  }

  const getTypeLabel = (type: Agent['type']) => {
    const labels = {
      teaching: '教学助手',
      learning: '学习助手',
      grading: '批改助手',
      assistant: '通用助手',
    }
    return labels[type]
  }

  const getTypeColor = (type: Agent['type']) => {
    const colors = {
      teaching: 'bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-400',
      learning: 'bg-green-100 text-green-600 dark:bg-green-900 dark:text-green-400',
      grading: 'bg-purple-100 text-purple-600 dark:bg-purple-900 dark:text-purple-400',
      assistant: 'bg-gray-100 text-gray-600 dark:bg-gray-900 dark:text-gray-400',
    }
    return colors[type]
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
                智能体操作
              </h1>
              <p className='text-muted-foreground'>
                {userRole === 'teacher'
                  ? '创建和管理AI教学智能体'
                  : '使用AI学习智能体辅助学习'}
              </p>
            </div>
            <Button onClick={() => setIsCreateDialogOpen(true)}>
              <Plus className='mr-2 h-4 w-4' />
              创建智能体
            </Button>
          </div>
        </div>

        {/* 搜索栏 */}
        <div className='mb-6'>
          <div className='relative max-w-md'>
            <Search className='absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground' />
            <Input
              placeholder='搜索智能体...'
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className='pl-10'
            />
          </div>
        </div>

        {/* 智能体列表 */}
        {filteredAgents.length === 0 ? (
          <Card>
            <CardContent className='flex flex-col items-center justify-center py-12'>
              <Bot className='h-12 w-12 text-muted-foreground mb-4' />
              <p className='text-muted-foreground'>
                {searchQuery ? '没有找到匹配的智能体' : '暂无智能体'}
              </p>
            </CardContent>
          </Card>
        ) : (
          <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'>
            {filteredAgents.map((agent) => (
              <Card
                key={agent.id}
                className='hover:shadow-lg transition-all duration-200'
              >
                <CardHeader>
                  <div className='flex items-start justify-between mb-2'>
                    <div className='p-2 bg-primary/10 rounded-lg'>
                      <Bot className='h-6 w-6 text-primary' />
                    </div>
                    <Badge
                      className={getTypeColor(agent.type)}
                      variant='secondary'
                    >
                      {getTypeLabel(agent.type)}
                    </Badge>
                  </div>
                  <CardTitle className='text-xl flex items-center gap-2'>
                    {agent.name}
                    {agent.status === 'active' && (
                      <span className='w-2 h-2 bg-green-500 rounded-full' />
                    )}
                  </CardTitle>
                  <CardDescription>{agent.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className='flex items-center gap-1 text-sm text-muted-foreground mb-4'>
                    <Sparkles className='h-4 w-4' />
                    <span>创建于 {agent.createdAt}</span>
                  </div>
                  <div className='flex gap-2'>
                    <Button
                      className='flex-1'
                      onClick={() => {
                        alert('启动智能体功能开发中')
                      }}
                    >
                      <Play className='mr-2 h-4 w-4' />
                      启动
                    </Button>
                    <Button
                      variant='outline'
                      size='icon'
                      onClick={() => {
                        alert('编辑功能开发中')
                      }}
                    >
                      <Edit className='h-4 w-4' />
                    </Button>
                    <Button
                      variant='outline'
                      size='icon'
                      onClick={() => handleDeleteAgent(agent.id)}
                    >
                      <Trash2 className='h-4 w-4' />
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}

        {/* 创建智能体对话框 */}
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>创建新智能体</DialogTitle>
              <DialogDescription>
                配置AI智能体的基本信息
              </DialogDescription>
            </DialogHeader>
            <div className='space-y-4 py-4'>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  智能体名称 *
                </label>
                <Input
                  placeholder='例如：课程助手'
                  value={newAgent.name}
                  onChange={(e) =>
                    setNewAgent({ ...newAgent, name: e.target.value })
                  }
                />
              </div>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  智能体类型
                </label>
                <select
                  className='w-full px-3 py-2 border border-input rounded-md bg-background'
                  value={newAgent.type}
                  onChange={(e) =>
                    setNewAgent({
                      ...newAgent,
                      type: e.target.value as Agent['type'],
                    })
                  }
                >
                  <option value='teaching'>教学助手</option>
                  <option value='learning'>学习助手</option>
                  <option value='grading'>批改助手</option>
                  <option value='assistant'>通用助手</option>
                </select>
              </div>
              <div>
                <label className='text-sm font-medium mb-2 block'>
                  智能体描述
                </label>
                <textarea
                  className='w-full min-h-[100px] px-3 py-2 border border-input rounded-md bg-background'
                  placeholder='描述智能体的功能和用途...'
                  value={newAgent.description}
                  onChange={(e) =>
                    setNewAgent({ ...newAgent, description: e.target.value })
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
              <Button onClick={handleCreateAgent}>创建</Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  )
}

