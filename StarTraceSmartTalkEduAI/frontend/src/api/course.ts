import { http } from "./http";

// ===== 类型定义
export interface Course {
  id: number;
  courseCode: string;
  name: string;
  description?: string;
  credit: number;
  totalHours: number;
  createdAt?: string;
}

export interface CourseGroup {
  id: number;
  courseId: number;
  teacherId: number;
  semester: string;
  maxStudents: number;
  currentStudents: number;
  status: number; // 1-正常, 0-停开
  course?: Course; // 关联的课程信息
  teacher?: { id: number; name: string }; // 关联的老师信息
  scheduleSlots?: CourseScheduleSlot[]; // 时间安排
}

export interface CourseScheduleSlot {
  id?: number;
  groupId?: number;
  classroomId: number;
  dayOfWeek: number; // 1-7, 1表示周一
  sectionStart: number; // 开始周数（第几周开始）
  sectionEnd: number; // 结束周数（第几周结束）
  startTime: string; // "HH:mm"
  endTime: string; // "HH:mm"
  classroom?: { id: number; buildingName: string; roomNumber: string }; // 关联的教室信息
}

export interface Classroom {
  id: number;
  buildingName: string;
  roomNumber: string;
  capacity: number;
  type: string; // "LECTURE" | "LAB" | "OFFICE"
}

export interface Teacher {
  id: number;
  studentId: string;
  name?: string;
  role: string;
  subject?: string;
}

export interface ScheduleSlotDTO {
  classroomId: number;
  dayOfWeek: number;
  sectionStart: number;
  sectionEnd: number;
  startTime: string;
  endTime: string;
}

export interface CourseGroupDTO {
  courseId: number;
  teacherId: number;
  semester: string;
  maxStudents: number;
  scheduleSlots: ScheduleSlotDTO[];
}

export interface StudentCourseRelation {
  id: number;
  studentId: number;
  groupId: number;
  enrollStatus: string; // "ENROLLED" | "DROPPED" | "COMPLETED"
  grade?: number;
  createdAt?: string; // 创建时间（加入日期）
  created_at?: string; // 创建时间（下划线格式，兼容）
  courseGroup?: CourseGroup; // 关联的课程组信息
  student?: Student; // 关联的学生信息
}

export interface CourseChangeRequest {
  id: number;
  groupId: number;
  applicantId: number;
  changeType: string; // "TIME" | "CLASSROOM" | "CANCEL"
  content: string; // JSON格式
  reason: string;
  auditStatus: number; // 0-待审批, 1-已通过, 2-已驳回
  adminId?: number;
  createdAt?: string;
  courseGroup?: CourseGroup; // 关联的课程组信息
}

export interface CourseChangeRequestDTO {
  groupId: number;
  changeType: string;
  content: string; // JSON格式
  reason: string;
}

export interface EnrollCourseDTO {
  groupId: number;
  studentId?: number;
}

export interface Student {
  id: number;
  studentId: string;
  name?: string;
  role: string;
  email?: string;
  subject?: string;
}

export interface User {
  id?: number;
  studentId: string;
  name?: string;
  role: string;
  email?: string;
  subject?: string;
  password?: string;
}

export interface InviteStudentDTO {
  groupId: number;
  studentId: number;
}

// ===== 课程课件/文章（老师维护）
export type CourseMaterialType = "ARTICLE" | "SLIDE" | "FILE" | "LINK";

export interface CourseMaterial {
  id: number;
  groupId: number;
  title: string;
  type: CourseMaterialType;
  /** 富文本/markdown/纯文本内容（按后端约定） */
  content?: string;
  /** 外链或文件 URL（按后端约定） */
  url?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CourseMaterialDTO {
  title: string;
  type: CourseMaterialType;
  content?: string;
  url?: string;
}

export interface UploadFileResult {
  url: string;
  filename?: string;
}

export interface Result<T> {
  code: number;
  message: string;
  data: T;
}

// ===== API 调用函数

/**
 * 学生选课 API
 */
export const studentCourseApi = {
  // 选课
  enroll: async (groupId: number, studentId?: number): Promise<Result<StudentCourseRelation>> => {
    const response = await http.post<Result<StudentCourseRelation>>(
      `/student/course/enroll`,
      { groupId, studentId },
      { params: studentId ? { studentId } : {} }
    );
    return response.data;
  },

  // 退课
  drop: async (studentId: number, groupId: number): Promise<Result<void>> => {
    const response = await http.post<Result<void>>(
      `/student/course/drop`,
      null,
      { params: { studentId, groupId } }
    );
    return response.data;
  },

  // 查询选课列表
  getList: async (studentId: number, semester?: string): Promise<Result<StudentCourseRelation[]>> => {
    const response = await http.get<Result<StudentCourseRelation[]>>(
      `/student/course/list`,
      { params: { studentId, ...(semester ? { semester } : {}) } }
    );
    return response.data;
  },

  // 检查是否已选课
  checkEnrolled: async (studentId: number, groupId: number): Promise<Result<boolean>> => {
    const response = await http.get<Result<boolean>>(
      `/student/course/check`,
      { params: { studentId, groupId } }
    );
    return response.data;
  },
};

/**
 * 老师调课 API
 */
export const teacherChangeApi = {
  // 提交调课申请
  submitChangeRequest: async (
    requestDTO: CourseChangeRequestDTO,
    applicantId: number
  ): Promise<Result<CourseChangeRequest>> => {
    const response = await http.post<Result<CourseChangeRequest>>(
      `/course/change/submit`,
      requestDTO,
      { params: { applicantId } }
    );
    return response.data;
  },

  // 查询我的调课申请
  getMyChangeRequests: async (applicantId: number): Promise<Result<CourseChangeRequest[]>> => {
    const response = await http.get<Result<CourseChangeRequest[]>>(
      `/course/change/applicant/${applicantId}`
    );
    return response.data;
  },
};

/**
 * 管理员排课 API
 */
export const adminCourseApi = {
  // 创建课程组（排课）
  createCourseGroup: async (courseGroupDTO: CourseGroupDTO): Promise<Result<CourseGroup>> => {
    const response = await http.post<Result<CourseGroup>>(
      `/course/group/create`,
      courseGroupDTO
    );
    return response.data;
  },

  // 查询课程组详情
  getCourseGroupById: async (id: number): Promise<Result<CourseGroup>> => {
    const response = await http.get<Result<CourseGroup>>(`/course/group/${id}`);
    return response.data;
  },

  // 按学期查询课程组
  getCourseGroupsBySemester: async (semester: string): Promise<Result<CourseGroup[]>> => {
    // 对学期参数进行 URL 编码，处理中文字符
    const encodedSemester = encodeURIComponent(semester);
    const response = await http.get<Result<CourseGroup[]>>(
      `/course/group/semester/${encodedSemester}`
    );
    return response.data;
  },

  // 更新课程组
  updateCourseGroup: async (courseGroup: CourseGroup): Promise<Result<CourseGroup>> => {
    const response = await http.put<Result<CourseGroup>>(
      `/course/group/update`,
      courseGroup
    );
    return response.data;
  },

  // 删除课程组
  deleteCourseGroup: async (id: number): Promise<Result<void>> => {
    const response = await http.delete<Result<void>>(`/course/group/${id}`);
    return response.data;
  },

  // 审核调课申请
  auditChangeRequest: async (
    requestId: number,
    auditStatus: number,
    adminId: number
  ): Promise<Result<void>> => {
    const response = await http.post<Result<void>>(
      `/course/change/audit`,
      null,
      { params: { requestId, auditStatus, adminId } }
    );
    return response.data;
  },

  // 查询所有调课申请（按状态）
  getChangeRequestsByStatus: async (auditStatus: number): Promise<Result<CourseChangeRequest[]>> => {
    const response = await http.get<Result<CourseChangeRequest[]>>(
      `/course/change/status/${auditStatus}`
    );
    return response.data;
  },
};

/**
 * 通用课程 API（获取课程列表等）
 */
export const courseApi = {
  // 获取所有课程
  getAllCourses: async (): Promise<Result<Course[]>> => {
    const response = await http.get<Result<Course[]>>(`/course/list`);
    return response.data;
  },

  // 获取课程详情
  getCourseById: async (id: number): Promise<Result<Course>> => {
    const response = await http.get<Result<Course>>(`/course/${id}`);
    return response.data;
  },

  // 创建课程（管理员）
  createCourse: async (course: Partial<Course>): Promise<Result<Course>> => {
    const response = await http.post<Result<Course>>(`/course/create`, course);
    return response.data;
  },

  // 更新课程（管理员）
  updateCourse: async (course: Course): Promise<Result<Course>> => {
    const response = await http.put<Result<Course>>(`/course/update`, course);
    return response.data;
  },

  // 删除课程（管理员）
  deleteCourse: async (id: number): Promise<Result<void>> => {
    const response = await http.delete<Result<void>>(`/course/${id}`);
    return response.data;
  },

  // 搜索课程
  searchCourses: async (name: string): Promise<Result<Course[]>> => {
    const response = await http.get<Result<Course[]>>(`/course/search`, {
      params: { name },
    });
    return response.data;
  },
};

/**
 * 教室 API
 */
export const classroomApi = {
  // 获取所有教室
  getAllClassrooms: async (): Promise<Result<Classroom[]>> => {
    const response = await http.get<Result<Classroom[]>>(`/classroom/list`);
    return response.data;
  },

  // 根据类型获取教室
  getClassroomsByType: async (type: string): Promise<Result<Classroom[]>> => {
    const response = await http.get<Result<Classroom[]>>(`/classroom/type/${type}`);
    return response.data;
  },
};

/**
 * 老师 API
 */
export const teacherApi = {
  // 获取所有老师
  getAllTeachers: async (): Promise<Result<Teacher[]>> => {
    const response = await http.get<Result<Teacher[]>>(`/teacher/list`);
    return response.data;
  },

  // 根据学科获取老师
  getTeachersBySubject: async (subject: string): Promise<Result<Teacher[]>> => {
    const response = await http.get<Result<Teacher[]>>(`/teacher/subject/${subject}`);
    return response.data;
  },

  // 根据课程ID获取匹配的老师
  getTeachersByCourse: async (courseId: number): Promise<Result<Teacher[]>> => {
    const response = await http.get<Result<Teacher[]>>(`/teacher/by-course/${courseId}`);
    return response.data;
  },

  // 获取老师教的课程组列表
  getMyCourseGroups: async (teacherId: number, semester?: string): Promise<Result<CourseGroup[]>> => {
    const response = await http.get<Result<CourseGroup[]>>(
      `/course/group/teacher/${teacherId}`,
      { params: { ...(semester ? { semester } : {}) } }
    );
    return response.data;
  },

  // 获取课程组的学生列表
  getCourseGroupStudents: async (groupId: number): Promise<Result<StudentCourseRelation[]>> => {
    const response = await http.get<Result<StudentCourseRelation[]>>(
      `/course/group/${groupId}/students`
    );
    return response.data;
  },

  // 老师将学生从课程中移除（置为 DROPPED）
  dropStudentFromGroup: async (groupId: number, studentId: number): Promise<Result<void>> => {
    const response = await http.post<Result<void>>(
      `/course/group/${groupId}/students/${studentId}/drop`
    );
    return response.data;
  },

  // 老师给已选课学生打分（打完分自动置为 COMPLETED）
  gradeStudent: async (groupId: number, studentId: number, grade: number): Promise<Result<StudentCourseRelation>> => {
    const response = await http.post<Result<StudentCourseRelation>>(
      `/course/group/${groupId}/students/${studentId}/grade`,
      null,
      { params: { grade } }
    );
    return response.data;
  },

  // 邀请学生加入课程组
  inviteStudent: async (inviteDTO: InviteStudentDTO): Promise<Result<StudentCourseRelation>> => {
    const response = await http.post<Result<StudentCourseRelation>>(
      `/course/group/invite`,
      inviteDTO
    );
    return response.data;
  },

  // 移除学生（物理删除选课关系）
  removeStudentFromGroup: async (groupId: number, studentId: number): Promise<Result<void>> => {
    const response = await http.delete<Result<void>>(
      `/course/group/${groupId}/students/${studentId}`
    );
    return response.data;
  },

  // 搜索学生（根据学号或姓名；不传参数则获取所有学生）
  searchStudents: async (keyword?: string, subject?: string): Promise<Result<User[]>> => {
    const kw = keyword?.trim?.() ?? "";
    const sub = subject?.trim?.() ?? "";
    const params: Record<string, string> = {};
    if (kw) params.keyword = kw;
    if (sub) params.subject = sub;
    // 如需仅学生，可由调用方自行传 subject/keyword 或后端按 role 处理

    const response = await http.get<Result<User[]>>(
      `/student/search`,
      { params }
    );
    return response.data;
  },
};

/**
 * 用户信息 API（用于“查看学生信息”）
 */
export const userApi = {
  // 按用户ID查询（推荐：不会受学号格式影响）
  getById: async (id: number): Promise<Result<Student>> => {
    const response = await http.get<Result<Student>>(`/user/by-id/${id}`);
    return response.data;
  },

  // 按学号查询（兼容后端已有接口）
  getByStudentId: async (studentId: string): Promise<Result<Student>> => {
    const encoded = encodeURIComponent(studentId);
    const response = await http.get<Result<Student>>(`/user/${encoded}`);
    return response.data;
  }
};

/**
 * 老师：课程课件/文章 CRUD
 *
 * 说明：这里先按常见 REST 约定封装，后端接口确定后可直接调整路径/参数。
 */
export const teacherMaterialApi = {
  // 列表（按课程组）
  listByGroup: async (groupId: number): Promise<Result<CourseMaterial[]>> => {
    const response = await http.get<Result<CourseMaterial[]>>(
      `/course/group/${groupId}/materials`
    );
    return response.data;
  },

  // 新增（按课程组）
  create: async (
    groupId: number,
    dto: CourseMaterialDTO,
    uploaderId?: number | string
  ): Promise<Result<CourseMaterial>> => {
    const response = await http.post<Result<CourseMaterial>>(
      `/course/group/${groupId}/materials`,
      dto,
      { params: uploaderId ? { uploaderId } : {} }
    );
    return response.data;
  },

  // 更新（按 materialId）
  update: async (materialId: number, dto: CourseMaterialDTO): Promise<Result<CourseMaterial>> => {
    const response = await http.put<Result<CourseMaterial>>(
      `/course/material/${materialId}`,
      dto
    );
    return response.data;
  },

  // 删除（按 materialId）
  remove: async (materialId: number): Promise<Result<void>> => {
    const response = await http.delete<Result<void>>(
      `/course/material/${materialId}`
    );
    return response.data;
  },

  // 上传文件（用于课件/文章附件）
  uploadFile: async (file: File): Promise<Result<UploadFileResult>> => {
    const form = new FormData();
    form.append("file", file);
    // 不设置 Content-Type，让 axios 自动处理 FormData
    // 文件上传需要更长的超时时间（5分钟）
    const response = await http.post<Result<UploadFileResult>>(
      `/course/material/upload`,
      form,
      {
        timeout: 300000, // 5分钟超时
        onUploadProgress: (progressEvent) => {
          if (progressEvent.total) {
            const percentCompleted = Math.round(
              (progressEvent.loaded * 100) / progressEvent.total
            );
            console.log(`上传进度: ${percentCompleted}%`);
          }
        }
      }
    );
    return response.data;
  },
};

/**
 * 空闲时间接口
 */
// ===== 班级相关类型定义
export interface Class {
  id: number;
  className: string;
  classCode?: string;
  teacherId: number;
  courseId: number;
  description?: string;
  status: number; // 1-正常, 0-停用
  createdAt?: string;
  updatedAt?: string;
  teacher?: User;
  course?: Course;
  courseGroup?: CourseGroup;
}

export interface ClassDTO {
  className: string;
  classCode?: string;
  teacherId: number;
  courseId: number;
  description?: string;
  status?: number;
}

/**
 * 班级管理API
 */
export const classApi = {
  // 创建班级
  create: async (dto: ClassDTO): Promise<Result<Class>> => {
    const response = await http.post<Result<Class>>("/class/create", dto);
    return response.data;
  },

  // 根据ID获取班级详情
  getById: async (id: number): Promise<Result<Class>> => {
    const response = await http.get<Result<Class>>(`/class/${id}`);
    return response.data;
  },

  // 根据教师ID获取班级列表
  getByTeacherId: async (teacherId: number): Promise<Result<Class[]>> => {
    const response = await http.get<Result<Class[]>>(`/class/teacher/${teacherId}`);
    return response.data;
  },

  // 根据课程ID获取班级列表
  getByCourseId: async (courseId: number): Promise<Result<Class[]>> => {
    const response = await http.get<Result<Class[]>>(`/class/course/${courseId}`);
    return response.data;
  },

  // 根据课程ID + 教师ID获取班级列表（避免同课不同教师拿错班级）
  getByCourseAndTeacher: async (courseId: number, teacherId: number): Promise<Result<Class[]>> => {
    const response = await http.get<Result<Class[]>>(`/class/course/${courseId}/teacher/${teacherId}`);
    return response.data;
  },

  // 获取所有班级
  getAll: async (): Promise<Result<Class[]>> => {
    const response = await http.get<Result<Class[]>>("/class/list");
    return response.data;
  },

  // 更新班级信息
  update: async (dto: Class): Promise<Result<Class>> => {
    const response = await http.put<Result<Class>>("/class/update", dto);
    return response.data;
  },

  // 删除班级
  delete: async (id: number): Promise<Result<void>> => {
    const response = await http.delete<Result<void>>(`/class/${id}`);
    return response.data;
  },
};

export interface AvailableTimeDTO {
  dayOfWeek: number; // 1-7, 1表示周一
  dayName: string; // 周几名称
  availableTimeSlots: string[]; // 空闲时间段列表（格式：HH:mm-HH:mm）
  occupiedTimeSlots: string[]; // 已占用时间段列表（格式：HH:mm-HH:mm）
}

/**
 * 课表 API
 */
export const scheduleApi = {
  // 获取老师的空闲时间（周一到周日）
  getTeacherAvailableTime: async (
    teacherId: number,
    semester?: string
  ): Promise<Result<AvailableTimeDTO[]>> => {
    const response = await http.get<Result<AvailableTimeDTO[]>>(
      `/schedule/teacher/available`,
      { params: { teacherId, ...(semester ? { semester } : {}) } }
    );
    return response.data;
  },

  // 获取教室的空闲时间（周一到周日）
  getClassroomAvailableTime: async (
    classroomId: number
  ): Promise<Result<AvailableTimeDTO[]>> => {
    const response = await http.get<Result<AvailableTimeDTO[]>>(
      `/schedule/classroom/available`,
      { params: { classroomId } }
    );
    return response.data;
  },

  // 获取老师和教室的共同空闲时间（周一到周日）
  getTeacherAndClassroomAvailableTime: async (
    teacherId: number,
    classroomId: number,
    semester?: string
  ): Promise<Result<AvailableTimeDTO[]>> => {
    const response = await http.get<Result<AvailableTimeDTO[]>>(
      `/schedule/available`,
      { params: { teacherId, classroomId, ...(semester ? { semester } : {}) } }
    );
    return response.data;
  },
};

// ===== 学习模板相关类型与 API（基于 /api/template 后端接口） =====

export interface LearningTemplate {
  id: number;
  templateName: string;
  description?: string;
  imageUrl?: string;
  /** 后端存储的 JSON 字符串 */
  jsonContent: string;
  teacherId: number;
  courseId: number;
  classId?: number | null;
  downloadCount: number;
  status: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateLearningTemplateDTO {
  templateName: string;
  description?: string;
  imageUrl?: string;
  /** 直接传 JSON 字符串 */
  jsonContent: string;
  teacherId: number;
  courseId: number;
  classId?: number | null;
}

export const learningTemplateApi = {
  // 创建模板
  create: async (
    dto: CreateLearningTemplateDTO
  ): Promise<Result<LearningTemplate>> => {
    const response = await http.post<Result<LearningTemplate>>(
      `/template/create`,
      dto
    );
    return response.data;
  },

  // 根据 ID 获取模板详情
  getById: async (id: number): Promise<Result<LearningTemplate>> => {
    const response = await http.get<Result<LearningTemplate>>(
      `/template/${id}`
    );
    return response.data;
  },

  // 根据班级 ID 获取模板列表（一个班级理论上可有多个模板）
  getByClassId: async (
    classId: number
  ): Promise<Result<LearningTemplate[]>> => {
    const response = await http.get<Result<LearningTemplate[]>>(
      `/template/class/${classId}`
    );
    return response.data;
  },

  // 根据教师 ID 获取模板列表
  getByTeacherId: async (
    teacherId: number
  ): Promise<Result<LearningTemplate[]>> => {
    const response = await http.get<Result<LearningTemplate[]>>(
      `/template/teacher/${teacherId}`
    );
    return response.data;
  },

  // 更新模板
  update: async (
    template: Partial<LearningTemplate> & { id: number }
  ): Promise<Result<LearningTemplate>> => {
    const response = await http.put<Result<LearningTemplate>>(
      `/template/update`,
      template
    );
    return response.data;
  },

  // 删除模板
  delete: async (id: number): Promise<Result<void>> => {
    const response = await http.delete<Result<void>>(`/template/${id}`);
    return response.data;
  },

  // 学生下载模板时调用，用于统计 downloadCount
  increaseDownloadCount: async (id: number): Promise<Result<void>> => {
    const response = await http.post<Result<void>>(
      `/template/${id}/download`
    );
    return response.data;
  },
};

// ===== Sim 实验监测 API（student_experiment_* 表） =====

export interface StudentExperimentAttemptRecord {
  id: number;
  studentId: number;
  templateId: number;
  courseId?: number | null;
  classId?: number | null;
  groupId?: number | null;
  teacherId?: number | null;
  attemptNo?: number;
  status?: string;
  isCompleted?: boolean;
  startedAt?: string;
  submittedAt?: string | null;
  completedAt?: string | null;
  durationSeconds?: number | null;
  paramsJson?: string | null;
  paramsDiffJson?: string | null;
  studentResultJson?: string | null;
  studentResultText?: string | null;
  tokenTotal?: number;
  tokenPrompt?: number;
  tokenCompletion?: number;
  tokenEmbedding?: number;
  tokenCost?: number | null;
  modelName?: string | null;
  modelProvider?: string | null;
  runtimeEnvJson?: string | null;
  clientIp?: string | null;
  userAgent?: string | null;
  errorCode?: string | null;
  errorMessage?: string | null;
  templateSnapshotJson?: string | null;
  metadata?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

export interface StudentExperimentEventRecord {
  id: number;
  attemptId: number;
  eventType: string;
  eventAt?: string;
  seq?: number;
  payloadJson?: string | null;
  payloadText?: string | null;
  tokenUsed?: number;
  latencyMs?: number | null;
  createdAt?: string;
}

export interface StudentExperimentArtifactRecord {
  id: number;
  attemptId: number;
  artifactType: string;
  title?: string | null;
  url?: string | null;
  content?: string | null;
  contentType?: string | null;
  fileSize?: number | null;
  createdAt?: string;
}

export const simExperimentApi = {
  // 开始一次实验尝试
  startAttempt: async (
    payload: Partial<StudentExperimentAttemptRecord> & {
      studentId: number;
      templateId: number;
    }
  ): Promise<Result<StudentExperimentAttemptRecord>> => {
    const response = await http.post<Result<StudentExperimentAttemptRecord>>(
      `/sim/experiments/attempts/start`,
      payload
    );
    return response.data;
  },

  // 记录一次过程事件
  recordEvent: async (
    attemptId: number,
    payload: Omit<StudentExperimentEventRecord, "id" | "attemptId" | "createdAt">
  ): Promise<Result<StudentExperimentEventRecord>> => {
    const response = await http.post<Result<StudentExperimentEventRecord>>(
      `/sim/experiments/attempts/${attemptId}/events`,
      payload
    );
    return response.data;
  },

  // 记录一个实验产物
  recordArtifact: async (
    attemptId: number,
    payload: Omit<StudentExperimentArtifactRecord, "id" | "attemptId" | "createdAt">
  ): Promise<Result<StudentExperimentArtifactRecord>> => {
    const response = await http.post<Result<StudentExperimentArtifactRecord>>(
      `/sim/experiments/attempts/${attemptId}/artifacts`,
      payload
    );
    return response.data;
  },

  // 学生提交实验
  submitAttempt: async (
    attemptId: number,
    payload: Partial<StudentExperimentAttemptRecord>
  ): Promise<Result<StudentExperimentAttemptRecord>> => {
    const response = await http.post<Result<StudentExperimentAttemptRecord>>(
      `/sim/experiments/attempts/${attemptId}/submit`,
      payload
    );
    return response.data;
  },

  // 标记实验完成
  completeAttempt: async (
    attemptId: number,
    payload: Partial<StudentExperimentAttemptRecord>
  ): Promise<Result<StudentExperimentAttemptRecord>> => {
    const response = await http.post<Result<StudentExperimentAttemptRecord>>(
      `/sim/experiments/attempts/${attemptId}/complete`,
      payload
    );
    return response.data;
  },

  // 标记实验失败
  failAttempt: async (
    attemptId: number,
    payload: Partial<StudentExperimentAttemptRecord>
  ): Promise<Result<StudentExperimentAttemptRecord>> => {
    const response = await http.post<Result<StudentExperimentAttemptRecord>>(
      `/sim/experiments/attempts/${attemptId}/fail`,
      payload
    );
    return response.data;
  },
};

