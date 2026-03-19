<template>
  <div class="courses-page fade-in">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">我的课程</h1>
        <p class="page-subtitle">管理与查看您的学术课程安排</p>
      </div>
      <div class="header-actions">
        <button v-if="!isTeacher" class="btn-join" @click="showJoinModal = true">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M22 10v6M2 10l10-5 10 5-10 5zM6 12v5c0 2 2 3 6 3s6-1 6-3v-5" />
          </svg>
          加入课程
        </button>
        <button v-if="isTeacher" class="btn-create" @click="openCreateModal">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          创建新课程
        </button>
      </div>
    </div>

    <!-- Stats Overview (Quick Look) -->
    <div class="stats-overview">
      <div class="stat-item">
        <span class="stat-val">{{ displayList.length }}</span>
        <span class="stat-label">总课程</span>
      </div>
      <div class="stat-item">
        <span class="stat-val">{{ totalStudents }}</span>
        <span class="stat-label">关联学生</span>
      </div>
      <div class="stat-item">
        <span class="stat-val">4</span>
        <span class="stat-label">今日排课</span>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>正在获取课程列表...</p>
    </div>
    
    <div v-else-if="displayList.length === 0" class="empty-state">
      <div class="empty-illustration">📚</div>
      <h3>暂无课程安排</h3>
      <p>您可以点击上方按钮开始创建或加入课程</p>
    </div>
    
    <div v-else class="courses-container">
      <div class="course-grid">
        <div
          v-for="course in displayList"
          :key="course.id"
          class="course-card"
          @click="goToCourseDetail(course.id)"
        >
          <div class="card-inner">
            <div class="course-tag" :class="course.role">
              {{ course.role === 'teacher' ? '教师' : '学生' }}
            </div>
            <div class="course-header">
              <h3 class="course-name">{{ course.name }}</h3>
              <p class="course-id">{{ course.courseCode || `ID: ${course.id}` }}</p>
            </div>
            <p class="course-desc">{{ course.description || '暂无描述' }}</p>
            <div class="course-meta">
              <div class="meta-item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                  <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                </svg>
                {{ course.memberCount }} 成员
              </div>
              <div class="meta-item" v-if="course.semester">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                  <line x1="16" y1="2" x2="16" y2="6" />
                  <line x1="8" y1="2" x2="8" y2="6" />
                  <line x1="3" y1="10" x2="21" y2="10" />
                </svg>
                {{ course.semester }}
              </div>
              <div class="meta-item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
                </svg>
                {{ course.status || '活跃中' }}
              </div>
            </div>
            <div class="card-action">
              <span>进入课程工作区</span>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <line x1="5" y1="12" x2="19" y2="12" />
                <polyline points="12 5 19 12 12 19" />
              </svg>
              <!-- 学生视角：额外提供“进入班级”快捷按钮 -->
              <button
                v-if="course.role === 'student'"
                class="btn-enter-class"
                @click.stop="enterClassFromCourse(course)"
              >
                进入班级
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals (Styled for Academic Light) -->
    <div v-if="showCreateModal" class="modal-overlay" @click="showCreateModal = false">
      <div class="modal-box" @click.stop>
        <div class="modal-header">
          <h2>创建课程组</h2>
          <button class="btn-close" @click="showCreateModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>选择课程 <span class="required">*</span></label>
            <select v-model="createForm.courseId" class="modern-select" @change="onCourseSelected" required>
              <option value="">请选择课程</option>
              <option v-for="course in availableCourses" :key="course.id" :value="course.id">
                {{ course.courseCode }} - {{ course.name }}
              </option>
            </select>
            <p class="input-tip" v-if="selectedCourseInfo">
              <span v-if="selectedCourseInfo.courseCode">课程代码：{{ selectedCourseInfo.courseCode }}</span>
              <span v-if="selectedCourseInfo.credit"> | 学分：{{ selectedCourseInfo.credit }}</span>
              <span v-if="selectedCourseInfo.totalHours"> | 总学时：{{ selectedCourseInfo.totalHours }}</span>
            </p>
            <p class="input-tip" v-if="selectedCourseInfo?.description">
              {{ selectedCourseInfo.description }}
            </p>
          </div>
          <div class="form-row">
            <div class="input-group">
              <label>学期 <span class="required">*</span></label>
              <input v-model="createForm.semester" type="text" class="modern-input" readonly />
              <p class="input-tip">系统根据当前日期自动生成，不可修改</p>
            </div>
            <div class="input-group">
              <label>最大学生数</label>
              <input v-model.number="createForm.maxStudents" type="number" min="1" placeholder="例如：50" class="modern-input" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showCreateModal = false">取消</button>
          <button class="btn-submit" @click="handleCreateCourse" :disabled="creating">
            {{ creating ? '创建中...' : '确认发布' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showJoinModal" class="modal-overlay" @click="showJoinModal = false">
      <div class="modal-box" @click.stop>
        <div class="modal-header">
          <h2>加入课程</h2>
          <button class="btn-close" @click="showJoinModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>课程邀请码 / ID</label>
            <input v-model="joinForm.courseId" type="text" placeholder="输入 8 位课程唯一标识符" class="modern-input" />
            <p class="input-tip">💡 请从任课教师处获取课程 ID</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showJoinModal = false">取消</button>
          <button class="btn-submit" @click="handleJoinCourse" :disabled="joining">
            {{ joining ? '加入中...' : '确认加入' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useAuth } from "../composables/useAuth";
import { courseApi, teacherApi, studentCourseApi, adminCourseApi, classApi, type Course, type CourseGroup, type StudentCourseRelation, type CourseGroupDTO } from "../api/course";

const router = useRouter();
const { isTeacher, currentUser } = useAuth();

const loading = ref(false);
const showCreateModal = ref(false);
const showJoinModal = ref(false);
const creating = ref(false);
const joining = ref(false);

// 创建课程组表单（从已有课程中选择）
const createForm = ref({
  courseId: null as number | null, // 选中的课程ID
  semester: '', // 学期
  maxStudents: 50 // 最大学生数
});

// 所有可用课程列表（用于下拉选择）
const availableCourses = ref<Course[]>([]);

const joinForm = ref({ courseId: '' });

// 课程组列表（从后端获取）
const courseGroups = ref<CourseGroup[]>([]);
// 学生选课关系列表
const studentRelations = ref<StudentCourseRelation[]>([]);

// 选中的课程信息（用于显示）
const selectedCourseInfo = computed(() => {
  if (!createForm.value.courseId) return null;
  return availableCourses.value.find(c => c.id === createForm.value.courseId) || null;
});

// 当选择课程时触发
const onCourseSelected = () => {
  // 可以在这里添加额外的逻辑
};

// 加载可用课程列表（用于创建课程组时选择）
const loadAvailableCourses = async () => {
  try {
    const result = await courseApi.getAllCourses();
    if (result.code === 200 && result.data) {
      availableCourses.value = result.data;
    } else {
      console.error("获取课程列表失败:", result.message);
      availableCourses.value = [];
    }
  } catch (error: any) {
    console.error("获取课程列表异常:", error);
    availableCourses.value = [];
  }
};

// 加载课程列表（根据角色加载不同的数据）
const loadCourses = async () => {
  if (!currentUser.value || !currentUser.value.id) {
    console.error("用户未登录或用户ID不存在");
    return;
  }

  loading.value = true;
  try {
    const userId = typeof currentUser.value.id === 'string' 
      ? parseInt(currentUser.value.id) 
      : currentUser.value.id;

    if (isTeacher.value) {
      // 老师：获取该老师创建的课程组列表
      const result = await teacherApi.getMyCourseGroups(userId);
      if (result.code === 200 && result.data) {
        courseGroups.value = result.data;
      } else {
        console.error("获取课程组列表失败:", result.message);
        courseGroups.value = [];
      }
    } else {
      // 学生：获取该学生的选课列表
      const result = await studentCourseApi.getList(userId);
      if (result.code === 200 && result.data) {
        studentRelations.value = result.data;
      } else {
        console.error("获取选课列表失败:", result.message);
        studentRelations.value = [];
      }
    }
  } catch (error: any) {
    console.error("获取课程列表异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "获取课程列表失败";
    alert(message);
    courseGroups.value = [];
    studentRelations.value = [];
  } finally {
    loading.value = false;
  }
};

// 显示列表（根据角色显示不同的数据）
const displayList = computed(() => {
  if (isTeacher.value) {
    // 老师：显示课程组，从课程组中获取课程信息
    return courseGroups.value.map(group => ({
      id: String(group.id), // 使用课程组ID
      courseId: group.course?.id ?? null,
      name: group.course?.name || `课程组 ${group.id}`,
      description: group.course?.description || '',
      courseCode: group.course?.courseCode || '',
      credit: group.course?.credit || 0,
      totalHours: group.course?.totalHours || 0,
      memberCount: group.currentStudents || 0,
      role: 'teacher' as const,
      status: group.status === 1 ? '活跃中' : '已停用',
      semester: group.semester,
      courseGroup: group // 保留完整课程组信息
    }));
  } else {
    // 学生：显示选课关系，从选课关系中获取课程组和课程信息
    return studentRelations.value.map(relation => ({
      id: String(relation.groupId), // 使用课程组ID
      courseId: relation.courseGroup?.course?.id ?? null,
      name: relation.courseGroup?.course?.name || `课程组 ${relation.groupId}`,
      description: relation.courseGroup?.course?.description || '',
      courseCode: relation.courseGroup?.course?.courseCode || '',
      credit: relation.courseGroup?.course?.credit || 0,
      totalHours: relation.courseGroup?.course?.totalHours || 0,
      memberCount: relation.courseGroup?.currentStudents || 0,
      role: 'student' as const,
      status: relation.enrollStatus === 'ENROLLED' ? '已选课' : 
              relation.enrollStatus === 'ENROLLING' ? '申请中' :
              relation.enrollStatus === 'COMPLETED' ? '已完成' : '已退课',
      semester: relation.courseGroup?.semester,
      enrollStatus: relation.enrollStatus,
      grade: relation.grade
    }));
  }
});

const totalStudents = computed(() => {
  return displayList.value.reduce((acc, curr) => acc + curr.memberCount, 0);
});

const goToCourseDetail = (id: string) => {
  router.push(`/courses/${id}`);
};

const enterClassFromCourse = async (courseCard: any) => {
  if (!courseCard.courseId) {
    alert("该课程尚未关联班级，请联系任课教师创建班级后再试。");
    return;
  }

  try {
    const result = await classApi.getByCourseId(courseCard.courseId);
    if (result.code === 200 && Array.isArray(result.data) && result.data.length > 0) {
      const firstClass = result.data[0];
      router.push(`/class/${firstClass.id}`);
    } else {
      alert("该课程暂未创建班级，请联系任课教师。");
    }
  } catch (error: any) {
    console.error("根据课程获取班级失败:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "获取班级信息失败";
    alert(message);
  }
};

// 创建课程组（从已有课程中选择）
const handleCreateCourse = async () => {
  // 表单验证
  if (!createForm.value.courseId) {
    alert("请选择课程");
    return;
  }
  if (!createForm.value.semester?.trim()) {
    alert("请输入学期");
    return;
  }
  if (!createForm.value.maxStudents || createForm.value.maxStudents < 1) {
    createForm.value.maxStudents = 50; // 设置默认值
  }

  if (!currentUser.value?.id) {
    alert("用户未登录");
    return;
  }

  creating.value = true;
  try {
    const teacherId = typeof currentUser.value.id === 'string' 
      ? parseInt(currentUser.value.id) 
      : currentUser.value.id;

    const courseGroupDTO: CourseGroupDTO = {
      courseId: createForm.value.courseId,
      teacherId: teacherId,
      semester: createForm.value.semester.trim(),
      maxStudents: createForm.value.maxStudents || 50,
      scheduleSlots: [] // 暂时不设置时间安排
    };

    const groupResult = await adminCourseApi.createCourseGroup(courseGroupDTO);
    
    if (groupResult.code === 200 && groupResult.data) {
      alert(groupResult.message || "创建课程组成功！");
      showCreateModal.value = false;
      // 重置表单
      createForm.value = {
        courseId: null,
        semester: '',
        maxStudents: 50
      };
      // 刷新课程列表
      await loadCourses();
    } else {
      alert(groupResult.message || "创建课程组失败");
    }
  } catch (error: any) {
    console.error("创建课程组异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "创建课程组失败";
    alert(message);
  } finally {
    creating.value = false;
  }
};

const handleJoinCourse = () => {
  joining.value = true;
  setTimeout(() => {
    joining.value = false;
    showJoinModal.value = false;
    alert('模拟加入：已成功加入该课程！');
  }, 800);
};

// 自动生成学期（根据当前日期）
const generateSemester = (): string => {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1; // 0-11 -> 1-12
  
  // 上半年（1-6月）为春季，下半年（7-12月）为秋季
  const semester = month <= 6 ? '春季' : '秋季';
  return `${year}${semester}`;
};

// 打开创建课程组弹窗时，加载可用课程列表并自动填充学期
const openCreateModal = async () => {
  showCreateModal.value = true;
  // 自动填充学期
  createForm.value.semester = generateSemester();
  if (availableCourses.value.length === 0) {
    await loadAvailableCourses();
  }
};

onMounted(() => {
  loadCourses();
  // 如果是老师，预加载可用课程列表
  if (isTeacher.value) {
    loadAvailableCourses();
  }
});
</script>

<style scoped>
.courses-page {
  padding-bottom: 60px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
}

.page-title {
  font-size: 2rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
}

.page-subtitle {
  color: #64748b;
  font-size: 0.95rem;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* Button Styles */
.btn-join, .btn-create {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-join {
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
}

.btn-join:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  color: #1e293b;
}

.btn-create {
  background: #2563eb;
  border: none;
  color: white;
}

.btn-create:hover {
  background: #1d4ed8;
  transform: translateY(-1px);
}

/* Stats Row */
.stats-overview {
  display: flex;
  gap: 48px;
  margin-bottom: 40px;
  background: white;
  padding: 24px 32px;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-val {
  font-size: 1.5rem;
  font-weight: 800;
  color: #1e293b;
}

.stat-label {
  font-size: 0.75rem;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Course Grid */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.course-card {
  background: white;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.course-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 12px 30px rgba(0,0,0,0.03);
  transform: translateY(-4px);
}

.course-tag {
  position: absolute;
  top: 24px;
  right: 24px;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 800;
}

.course-tag.teacher {
  background: #eff6ff;
  color: #2563eb;
}

.course-tag.student {
  background: #f0fdf4;
  color: #16a34a;
}

.course-header {
  margin-bottom: 16px;
}

.course-name {
  font-size: 1.2rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 4px;
}

.course-id {
  font-family: var(--font-mono);
  font-size: 0.75rem;
  color: #94a3b8;
}

.course-desc {
  font-size: 0.9rem;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 24px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.8rem;
  color: #94a3b8;
  font-weight: 600;
}

.card-action {
  border-top: 1px solid #f1f5f9;
  padding-top: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #2563eb;
  font-size: 0.85rem;
  font-weight: 700;
  opacity: 0;
  transition: opacity 0.2s, transform 0.2s;
  transform: translateY(5px);
}

.course-card:hover .card-action {
  opacity: 1;
  transform: translateY(0);
}

.btn-enter-class {
  margin-left: auto;
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background-color: #f8fafc;
  font-size: 0.75rem;
  color: #0f172a;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-enter-class:hover {
  background-color: #e0f2fe;
  border-color: #38bdf8;
  color: #0f172a;
}

/* Modal Styles - Flat Academic Style */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.modal-box {
  background: white;
  width: 100%;
  max-width: 500px;
  border-radius: 16px;
  padding: 40px;
  border: 1px solid #cbd5e1;
  transform: translateY(0);
}



.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.modal-header h2 {
  font-size: 1.25rem;
  font-weight: 800;
  color: #1e293b;
}

.btn-close {
  background: #f1f5f9;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 1.2rem;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-group {
  margin-bottom: 24px;
}

.input-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 700;
  color: #475569;
  margin-bottom: 8px;
}

.input-group label .required {
  color: #ef4444;
  margin-left: 2px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.modern-input {
  width: 100%;
  padding: 12px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 0.95rem;
  transition: all 0.2s;
}

.modern-input:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.modern-select {
  width: 100%;
  padding: 12px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 0.95rem;
  transition: all 0.2s;
  cursor: pointer;
}

.modern-select:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.modern-select option {
  padding: 8px;
}

.modern-input[readonly] {
  background: #f1f5f9;
  cursor: not-allowed;
  color: #64748b;
}

.modern-input[readonly]:focus {
  border-color: #cbd5e1;
  box-shadow: none;
}

.input-tip {
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 6px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  margin-top: 32px;
}

.btn-cancel, .btn-submit {
  flex: 1;
  padding: 12px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background: white;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

.btn-submit {
  background: #2563eb;
  border: none;
  color: white;
}

.btn-submit:hover {
  background: #1d4ed8;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Loading Spinner */
.loading-state {
  text-align: center;
  padding: 100px 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f1f5f9;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  background: white;
  border-radius: 20px;
  border: 1px dashed #e2e8f0;
}

.empty-illustration {
  font-size: 3rem;
  margin-bottom: 20px;
}
</style>
