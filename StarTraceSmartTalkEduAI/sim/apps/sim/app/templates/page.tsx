'use client'

import { useState, useMemo } from 'react'
import { createLogger } from '@sim/logger'
import {
  Search,
  Filter,
  Download,
  BookOpen,
  GraduationCap,
  Users,
  Brain,
  Code,
  FileText,
  Calculator,
  Globe,
  Music,
  Palette,
  FlaskConical,
  Eye,
  CheckCircle2,
} from 'lucide-react'
import { useRouter, useParams } from 'next/navigation'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
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
} from '@/components/ui/dialog'
import { Badge } from '@/components/emcn'
import { useSession } from '@/lib/auth/auth-client'

const logger = createLogger('EducationTemplatesPage')

// 教育模板数据结构
interface EducationTemplate {
  id: string
  name: string
  description: string
  category: 'language' | 'math' | 'science' | 'coding' | 'art' | 'music' | 'social' | 'general'
  difficulty: 'beginner' | 'intermediate' | 'advanced'
  cost: 'low' | 'medium' | 'high'
  icon: React.ComponentType<{ className?: string }>
  tags: string[]
}

// 11个教育工作流模板
const EDUCATION_TEMPLATES: EducationTemplate[] = [
  {
    id: 'lang-tutor',
    name: '语言学习助手',
    description: '帮助学生练习外语对话、语法和词汇',
    category: 'language',
    difficulty: 'beginner',
    cost: 'low',
    icon: Globe,
    tags: ['语言', '对话', '练习'],
  },
  {
    id: 'math-solver',
    name: '数学解题助手',
    description: '逐步解答数学问题，提供详细解题过程',
    category: 'math',
    difficulty: 'intermediate',
    cost: 'medium',
    icon: Calculator,
    tags: ['数学', '解题', '步骤'],
  },
  {
    id: 'code-reviewer',
    name: '代码审查助手',
    description: '审查学生代码，提供改进建议和最佳实践',
    category: 'coding',
    difficulty: 'advanced',
    cost: 'high',
    icon: Code,
    tags: ['编程', '代码审查', '最佳实践'],
  },
  {
    id: 'essay-helper',
    name: '论文写作助手',
    description: '帮助学生构思、组织和改进论文写作',
    category: 'general',
    difficulty: 'intermediate',
    cost: 'medium',
    icon: FileText,
    tags: ['写作', '论文', '构思'],
  },
  {
    id: 'science-lab',
    name: '科学实验助手',
    description: '设计实验方案，分析实验结果',
    category: 'science',
    difficulty: 'advanced',
    cost: 'high',
    icon: FlaskConical,
    tags: ['科学', '实验', '分析'],
  },
  {
    id: 'vocab-builder',
    name: '词汇构建器',
    description: '通过上下文和练习帮助记忆新词汇',
    category: 'language',
    difficulty: 'beginner',
    cost: 'low',
    icon: BookOpen,
    tags: ['词汇', '记忆', '练习'],
  },
  {
    id: 'study-planner',
    name: '学习计划助手',
    description: '制定个性化学习计划和时间表',
    category: 'general',
    difficulty: 'beginner',
    cost: 'low',
    icon: GraduationCap,
    tags: ['计划', '时间管理', '学习'],
  },
  {
    id: 'group-project',
    name: '小组项目协调器',
    description: '协调小组项目，分配任务和跟踪进度',
    category: 'social',
    difficulty: 'intermediate',
    cost: 'medium',
    icon: Users,
    tags: ['协作', '项目管理', '团队'],
  },
  {
    id: 'creative-writing',
    name: '创意写作助手',
    description: '激发创意，帮助创作故事和诗歌',
    category: 'art',
    difficulty: 'intermediate',
    cost: 'medium',
    icon: Palette,
    tags: ['创意', '写作', '艺术'],
  },
  {
    id: 'music-theory',
    name: '音乐理论学习',
    description: '学习音乐理论、和声和作曲',
    category: 'music',
    difficulty: 'intermediate',
    cost: 'medium',
    icon: Music,
    tags: ['音乐', '理论', '作曲'],
  },
  {
    id: 'critical-thinking',
    name: '批判性思维训练',
    description: '通过案例分析和讨论培养批判性思维',
    category: 'general',
    difficulty: 'advanced',
    cost: 'high',
    icon: Brain,
    tags: ['思维', '分析', '批判'],
  },
]

const CATEGORY_LABELS: Record<EducationTemplate['category'], string> = {
  language: '语言',
  math: '数学',
  science: '科学',
  coding: '编程',
  art: '艺术',
  music: '音乐',
  social: '社交',
  general: '通用',
}

const DIFFICULTY_LABELS: Record<EducationTemplate['difficulty'], string> = {
  beginner: '初级',
  intermediate: '中级',
  advanced: '高级',
}

const COST_LABELS: Record<EducationTemplate['cost'], string> = {
  low: '低',
  medium: '中',
  high: '高',
}

export default function EducationTemplatesPage() {
  const router = useRouter()
  const params = useParams()
  const { data: session } = useSession()
  const workspaceId = params.workspaceId as string | undefined

  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCategory, setSelectedCategory] = useState<string>('all')
  const [selectedDifficulty, setSelectedDifficulty] = useState<string>('all')
  const [selectedCost, setSelectedCost] = useState<string>('all')
  const [isImporting, setIsImporting] = useState<string | null>(null)
  const [previewTemplate, setPreviewTemplate] = useState<EducationTemplate | null>(null)

  // 筛选模板
  const filteredTemplates = useMemo(() => {
    return EDUCATION_TEMPLATES.filter((template) => {
      const matchesSearch =
        searchQuery === '' ||
        template.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        template.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
        template.tags.some((tag) => tag.toLowerCase().includes(searchQuery.toLowerCase()))

      const matchesCategory = selectedCategory === 'all' || template.category === selectedCategory
      const matchesDifficulty =
        selectedDifficulty === 'all' || template.difficulty === selectedDifficulty
      const matchesCost = selectedCost === 'all' || template.cost === selectedCost

      return matchesSearch && matchesCategory && matchesDifficulty && matchesCost
    })
  }, [searchQuery, selectedCategory, selectedDifficulty, selectedCost])

  const handleImportTemplate = async (templateId: string) => {
    if (!session?.user?.id) {
      router.push('/login')
      return
    }

    if (!workspaceId) {
      // 如果没有 workspaceId，尝试获取第一个工作区
      try {
        const response = await fetch('/api/workspaces')
        if (response.ok) {
          const data = await response.json()
          const workspaces = data.workspaces || []
          if (workspaces.length > 0) {
            const firstWorkspace = workspaces[0]
            router.push(`/workspace/${firstWorkspace.id}/templates`)
            return
          }
        }
      } catch (error) {
        logger.error('Failed to fetch workspaces:', error)
      }
      alert('请先选择一个工作区')
      return
    }

    setIsImporting(templateId)
    try {
      const response = await fetch(`/api/edu/templates/${templateId}/import`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          workspaceId,
        }),
      })

      if (!response.ok) {
        throw new Error('Failed to import template')
      }

      const { workflowId } = await response.json()
      logger.info(`Template imported: ${templateId} to workflow: ${workflowId}`)

      // 导入成功后，导航到工作流编辑页面
      router.push(`/workspace/${workspaceId}/w/${workflowId}`)
    } catch (error) {
      logger.error('Failed to import template:', error)
      alert('导入模板失败，请重试')
    } finally {
      setIsImporting(null)
      setPreviewTemplate(null)
    }
  }

  return (
    <div className='container mx-auto py-8 px-4 max-w-7xl'>
      <div className='mb-8'>
        <h1 className='text-3xl font-bold mb-2'>教育工作流模板库</h1>
        <p className='text-muted-foreground'>
          浏览和导入专为教育场景设计的工作流模板，一键创建您的教学工具
        </p>
      </div>

      {/* 搜索和筛选 */}
      <div className='mb-6 space-y-4'>
        <div className='flex gap-4 items-center'>
          <div className='flex-1 relative'>
            <Search className='absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground' />
            <Input
              placeholder='搜索模板...'
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className='pl-10'
            />
          </div>
        </div>

        <div className='flex gap-4 items-center flex-wrap'>
          <div className='flex items-center gap-2'>
            <Filter className='h-4 w-4 text-muted-foreground' />
            <span className='text-sm font-medium'>筛选：</span>
          </div>
          <Select value={selectedCategory} onValueChange={setSelectedCategory}>
            <SelectTrigger className='w-[140px]'>
              <SelectValue placeholder='分类' />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value='all'>全部分类</SelectItem>
              {Object.entries(CATEGORY_LABELS).map(([value, label]) => (
                <SelectItem key={value} value={value}>
                  {label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select value={selectedDifficulty} onValueChange={setSelectedDifficulty}>
            <SelectTrigger className='w-[140px]'>
              <SelectValue placeholder='难度' />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value='all'>全部难度</SelectItem>
              {Object.entries(DIFFICULTY_LABELS).map(([value, label]) => (
                <SelectItem key={value} value={value}>
                  {label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select value={selectedCost} onValueChange={setSelectedCost}>
            <SelectTrigger className='w-[140px]'>
              <SelectValue placeholder='成本' />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value='all'>全部成本</SelectItem>
              {Object.entries(COST_LABELS).map(([value, label]) => (
                <SelectItem key={value} value={value}>
                  {label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* 模板列表 */}
      {filteredTemplates.length === 0 ? (
        <Card>
          <CardContent className='flex flex-col items-center justify-center py-12'>
            <p className='text-muted-foreground mb-4'>没有找到匹配的模板</p>
            <Button
              variant='outline'
              onClick={() => {
                setSearchQuery('')
                setSelectedCategory('all')
                setSelectedDifficulty('all')
                setSelectedCost('all')
              }}
            >
              清除筛选
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6'>
          {filteredTemplates.map((template) => {
            const Icon = template.icon
            return (
              <Card
                key={template.id}
                className='flex flex-col hover:shadow-lg transition-all duration-200'
              >
                <CardHeader>
                  <div className='flex items-start justify-between mb-2'>
                    <Icon className='h-8 w-8 text-primary' />
                    <div className='flex gap-1 flex-wrap'>
                      <Badge variant='secondary' size='sm'>
                        {CATEGORY_LABELS[template.category]}
                      </Badge>
                    </div>
                  </div>
                  <CardTitle className='text-xl'>{template.name}</CardTitle>
                  <CardDescription className='mt-2'>{template.description}</CardDescription>
                </CardHeader>
                <CardContent className='flex-1'>
                  <div className='flex gap-2 flex-wrap mb-4'>
                    {template.tags.map((tag) => (
                      <Badge key={tag} variant='outline' size='sm'>
                        {tag}
                      </Badge>
                    ))}
                  </div>
                  <div className='flex gap-2 text-sm text-muted-foreground'>
                    <span>难度: {DIFFICULTY_LABELS[template.difficulty]}</span>
                    <span>•</span>
                    <span>成本: {COST_LABELS[template.cost]}</span>
                  </div>
                </CardContent>
                <CardFooter className='flex gap-2'>
                  <Button
                    variant='outline'
                    className='flex-1'
                    onClick={() => setPreviewTemplate(template)}
                  >
                    <Eye className='mr-2 h-4 w-4' />
                    预览
                  </Button>
                  <Button
                    className='flex-1'
                    onClick={() => handleImportTemplate(template.id)}
                    disabled={isImporting === template.id}
                  >
                    {isImporting === template.id ? (
                      <>导入中...</>
                    ) : (
                      <>
                        <Download className='mr-2 h-4 w-4' />
                        导入
                      </>
                    )}
                  </Button>
                </CardFooter>
              </Card>
            )
          })}
        </div>
      )}

      {/* 预览对话框 */}
      <Dialog open={!!previewTemplate} onOpenChange={(open) => !open && setPreviewTemplate(null)}>
        <DialogContent className='max-w-2xl'>
          {previewTemplate && (
            <>
              <DialogHeader>
                <div className='flex items-center gap-3'>
                  {(() => {
                    const Icon = previewTemplate.icon
                    return <Icon className='h-6 w-6 text-primary' />
                  })()}
                  <DialogTitle className='text-2xl'>{previewTemplate.name}</DialogTitle>
                </div>
                <DialogDescription className='text-base mt-2'>
                  {previewTemplate.description}
                </DialogDescription>
              </DialogHeader>
              <div className='space-y-4 py-4'>
                <div>
                  <h4 className='font-semibold mb-2'>模板信息</h4>
                  <div className='grid grid-cols-3 gap-4 text-sm'>
                    <div>
                      <span className='text-muted-foreground'>分类：</span>
                      <Badge variant='secondary' className='ml-2'>
                        {CATEGORY_LABELS[previewTemplate.category]}
                      </Badge>
                    </div>
                    <div>
                      <span className='text-muted-foreground'>难度：</span>
                      <Badge variant='outline' className='ml-2'>
                        {DIFFICULTY_LABELS[previewTemplate.difficulty]}
                      </Badge>
                    </div>
                    <div>
                      <span className='text-muted-foreground'>成本：</span>
                      <Badge variant='outline' className='ml-2'>
                        {COST_LABELS[previewTemplate.cost]}
                      </Badge>
                    </div>
                  </div>
                </div>
                <div>
                  <h4 className='font-semibold mb-2'>标签</h4>
                  <div className='flex gap-2 flex-wrap'>
                    {previewTemplate.tags.map((tag) => (
                      <Badge key={tag} variant='outline'>
                        {tag}
                      </Badge>
                    ))}
                  </div>
                </div>
                <div>
                  <h4 className='font-semibold mb-2'>功能说明</h4>
                  <p className='text-sm text-muted-foreground'>
                    {previewTemplate.description}
                  </p>
                </div>
              </div>
              <DialogFooter>
                <Button
                  variant='outline'
                  onClick={() => setPreviewTemplate(null)}
                >
                  取消
                </Button>
                <Button
                  onClick={() => handleImportTemplate(previewTemplate.id)}
                  disabled={isImporting === previewTemplate.id}
                >
                  {isImporting === previewTemplate.id ? (
                    <>导入中...</>
                  ) : (
                    <>
                      <CheckCircle2 className='mr-2 h-4 w-4' />
                      确认导入
                    </>
                  )}
                </Button>
              </DialogFooter>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}
