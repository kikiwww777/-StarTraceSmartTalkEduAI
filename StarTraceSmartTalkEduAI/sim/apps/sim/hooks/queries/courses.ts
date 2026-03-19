import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

export interface Course {
  id: string
  name: string
  description: string | null
  instructorId: string | null
  workspaceId: string
  createdAt: Date
  updatedAt: Date
  role?: 'teacher' | 'student' | 'admin'
  joinedAt?: Date
}

export interface CourseMember {
  id: string
  userId: string
  role: 'teacher' | 'student' | 'admin'
  joinedAt: Date
  email?: string
  name?: string
  image?: string | null
}

/**
 * Query key factories for course-related queries
 */
export const courseKeys = {
  all: ['courses'] as const,
  lists: () => [...courseKeys.all, 'list'] as const,
  list: () => [...courseKeys.lists()] as const,
  details: () => [...courseKeys.all, 'detail'] as const,
  detail: (id: string) => [...courseKeys.details(), id] as const,
  members: (id: string) => [...courseKeys.detail(id), 'members'] as const,
}

/**
 * Fetch all courses for the current user
 */
async function fetchCourses(): Promise<Course[]> {
  const response = await fetch('/api/edu/courses')

  if (!response.ok) {
    throw new Error('Failed to fetch courses')
  }

  const data = await response.json()
  return data.courses || []
}

/**
 * Hook to fetch all courses for the current user
 */
export function useCourses() {
  return useQuery({
    queryKey: courseKeys.list(),
    queryFn: fetchCourses,
    staleTime: 30 * 1000,
    placeholderData: keepPreviousData,
  })
}

/**
 * Create course mutation parameters
 */
interface CreateCourseParams {
  name: string
  description?: string
}

/**
 * Create a new course
 */
async function createCourse(params: CreateCourseParams): Promise<Course> {
  const response = await fetch('/api/edu/courses', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(params),
  })

  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.error || 'Failed to create course')
  }

  const data = await response.json()
  return data.course
}

/**
 * Hook to create a new course
 */
export function useCreateCourse() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: createCourse,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: courseKeys.list() })
    },
  })
}

/**
 * Fetch course members
 */
async function fetchCourseMembers(courseId: string): Promise<CourseMember[]> {
  const response = await fetch(`/api/courses/${courseId}/members`)

  if (!response.ok) {
    throw new Error('Failed to fetch course members')
  }

  const data = await response.json()
  return data.members || []
}

/**
 * Hook to fetch course members
 */
export function useCourseMembers(courseId: string | undefined) {
  return useQuery({
    queryKey: courseKeys.members(courseId!),
    queryFn: () => fetchCourseMembers(courseId!),
    enabled: !!courseId,
    staleTime: 30 * 1000,
    placeholderData: keepPreviousData,
  })
}

/**
 * Add member to course mutation parameters
 */
interface AddMemberParams {
  courseId: string
  userId?: string
  email?: string
}

/**
 * Add a member to a course
 */
async function addCourseMember(params: AddMemberParams): Promise<CourseMember> {
  const response = await fetch(`/api/courses/${params.courseId}/members`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      userId: params.userId,
      email: params.email,
    }),
  })

  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.error || 'Failed to add course member')
  }

  const data = await response.json()
  return data.member
}

/**
 * Hook to add a member to a course
 */
export function useAddCourseMember() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: addCourseMember,
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: courseKeys.members(variables.courseId) })
    },
  })
}

/**
 * Join a course as a student
 */
async function joinCourse(courseId: string): Promise<{ member: CourseMember; course: Course }> {
  const response = await fetch(`/api/courses/${courseId}/join`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
  })

  if (!response.ok) {
    const error = await response.json()
    throw new Error(error.error || 'Failed to join course')
  }

  const data = await response.json()
  return data
}

/**
 * Hook to join a course as a student
 */
export function useJoinCourse() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: joinCourse,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: courseKeys.list() })
    },
  })
}

