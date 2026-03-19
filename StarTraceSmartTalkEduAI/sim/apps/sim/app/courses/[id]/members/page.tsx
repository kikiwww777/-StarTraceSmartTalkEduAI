'use client'

import { useState } from 'react'
import { createLogger } from '@sim/logger'
import { Plus, Mail } from 'lucide-react'
import { useParams, useRouter } from 'next/navigation'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
  Avatar,
  AvatarFallback,
  AvatarImage,
  Badge,
} from '@/components/emcn'
import { useCourseMembers, useAddCourseMember, useCourses } from '@/hooks/queries/courses'
import { useSession } from '@/lib/auth/auth-client'

const logger = createLogger('CourseMembersPage')

export default function CourseMembersPage() {
  const params = useParams()
  const router = useRouter()
  const courseId = params.id as string
  const { data: session } = useSession()

  const { data: members = [], isLoading } = useCourseMembers(courseId)
  const { data: courses = [] } = useCourses()
  const addMember = useAddCourseMember()
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false)
  const [memberEmail, setMemberEmail] = useState('')

  // Find current course to check user role
  const currentCourse = courses.find((c) => c.id === courseId)
  const canManageMembers = currentCourse?.role === 'teacher' || currentCourse?.role === 'admin'

  const handleAddMember = async () => {
    if (!memberEmail.trim()) {
      return
    }

    try {
      await addMember.mutateAsync({
        courseId,
        email: memberEmail.trim(),
      })

      logger.info('Member added to course:', memberEmail)

      // Reset form
      setMemberEmail('')
      setIsAddDialogOpen(false)
    } catch (error) {
      logger.error('Failed to add member:', error)
      // Error handling can be added here (e.g., toast notification)
    }
  }

  const getRoleBadgeVariant = (role: string) => {
    switch (role) {
      case 'teacher':
        return 'default'
      case 'admin':
        return 'default'
      case 'student':
        return 'outline'
      default:
        return 'outline'
    }
  }

  const getRoleLabel = (role: string) => {
    switch (role) {
      case 'teacher':
        return '教师'
      case 'admin':
        return '管理员'
      case 'student':
        return '学生'
      default:
        return role
    }
  }

  return (
    <div className='container mx-auto py-8 px-4 max-w-4xl'>
      <div className='flex items-center justify-between mb-6'>
        <div>
          <h1 className='text-3xl font-bold'>课程成员</h1>
          <p className='text-muted-foreground mt-2'>管理课程成员列表</p>
        </div>
        {canManageMembers && (
          <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
            <DialogTrigger asChild>
              <Button>
                <Plus className='mr-2 h-4 w-4' />
                添加成员
              </Button>
            </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>添加成员到课程</DialogTitle>
              <DialogDescription>
                通过邮箱地址添加新成员，新成员将自动被设置为学生角色
              </DialogDescription>
            </DialogHeader>
            <div className='space-y-4 py-4'>
              <div className='space-y-2'>
                <Label htmlFor='member-email'>
                  <Mail className='inline mr-2 h-4 w-4' />
                  邮箱地址 *
                </Label>
                <Input
                  id='member-email'
                  type='email'
                  placeholder='student@example.com'
                  value={memberEmail}
                  onChange={(e) => setMemberEmail(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter' && memberEmail.trim()) {
                      handleAddMember()
                    }
                  }}
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                variant='outline'
                onClick={() => setIsAddDialogOpen(false)}
              >
                取消
              </Button>
              <Button
                onClick={handleAddMember}
                disabled={!memberEmail.trim() || addMember.isPending}
              >
                {addMember.isPending ? '添加中...' : '添加成员'}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
        )}
      </div>

      {isLoading ? (
        <div className='space-y-4'>
          {[1, 2, 3].map((i) => (
            <div key={i} className='h-16 bg-muted rounded animate-pulse' />
          ))}
        </div>
      ) : members.length === 0 ? (
        <div className='text-center py-12'>
          <p className='text-muted-foreground mb-4'>还没有成员</p>
          {canManageMembers && (
            <Button onClick={() => setIsAddDialogOpen(true)}>
              <Plus className='mr-2 h-4 w-4' />
              添加第一个成员
            </Button>
          )}
        </div>
      ) : (
        <div className='border rounded-lg overflow-hidden'>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className='w-[50px]'>头像</TableHead>
                <TableHead>姓名</TableHead>
                <TableHead>邮箱</TableHead>
                <TableHead>角色</TableHead>
                <TableHead>加入时间</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {members.map((member) => (
                <TableRow key={member.id}>
                  <TableCell>
                    <Avatar className='h-8 w-8'>
                      <AvatarImage src={member.image || undefined} alt={member.name || member.email} />
                      <AvatarFallback>
                        {(member.name || member.email || 'U').charAt(0).toUpperCase()}
                      </AvatarFallback>
                    </Avatar>
                  </TableCell>
                  <TableCell className='font-medium'>
                    {member.name || '未设置'}
                  </TableCell>
                  <TableCell className='text-muted-foreground'>
                    {member.email || member.userId}
                  </TableCell>
                  <TableCell>
                    <Badge variant={getRoleBadgeVariant(member.role)}>
                      {getRoleLabel(member.role)}
                    </Badge>
                  </TableCell>
                  <TableCell className='text-muted-foreground text-sm'>
                    {new Date(member.joinedAt).toLocaleDateString('zh-CN', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                    })}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
      )}
    </div>
  )
}

