# 成员A任务书 - 前端开发

## 📋 职责范围
- Vue 3 + TypeScript 前端开发
- UI界面实现与交互
- API对接与状态管理

## 🛠️ 技术栈
- **框架**: Vue 3.5 + TypeScript
- **构建**: Vite 6
- **路由**: Vue Router 4
- **HTTP**: Axios
- **样式**: Scoped CSS (深色主题)

## 📂 工作目录
```
frontend/
├── src/
│   ├── views/         # 页面组件
│   ├── components/    # 公共组件
│   ├── composables/   # 组合式函数
│   ├── api/           # API封装
│   ├── router/        # 路由配置
│   └── styles/        # 全局样式
```

---

# Phase 1 任务清单

## 模块A：课程与权限管理

### A-1. CourseDetailView 完善 [优先级: P0]

**文件**: `src/views/CourseDetailView.vue`

**功能需求**:
1. 显示课程基本信息（名称、描述、创建时间）
2. 显示课程邀请码（教师可见，可复制）
3. 成员列表（表格形式）
   - 列：学号、姓名、角色、加入时间、操作
   - 教师可移除成员、修改角色
4. 学生可退出课程按钮

**API调用**:
```typescript
// 获取课程详情
GET /api/course/{id}
Response: { id, name, description, role, inviteCode, memberCount, createdAt }

// 获取成员列表
GET /api/course/{id}/members
Response: { members: [{ id, userId, studentId, name, role, joinedAt }], total }

// 移除成员
DELETE /api/course/{id}/members/{memberId}

// 修改成员角色
PUT /api/course/{id}/members/{memberId}
Body: { role: "teacher" | "student" }

// 退出课程
POST /api/course/{id}/leave
```

**UI设计要点**:
- 课程信息卡片在顶部
- 邀请码显示为可复制的代码框 + 复制按钮
- 成员列表使用表格，支持分页（每页20条）
- 操作按钮使用图标，hover显示文字

**代码示例**:
```vue
<template>
  <div class="course-detail-page">
    <!-- 返回按钮 -->
    <div class="page-header">
      <button @click="goBack">← 返回</button>
      <h1>{{ course?.name }}</h1>
    </div>
    
    <!-- 课程信息卡片 -->
    <section class="course-info-card">
      <div class="info-row">
        <span class="label">课程描述</span>
        <span>{{ course?.description || '暂无描述' }}</span>
      </div>
      <div class="info-row">
        <span class="label">我的角色</span>
        <span class="role-badge" :class="course?.role">
          {{ course?.role === 'teacher' ? '教师' : '学生' }}
        </span>
      </div>
      <div class="info-row">
        <span class="label">成员数量</span>
        <span>{{ course?.memberCount }} 人</span>
      </div>
      
      <!-- 邀请码（仅教师可见） -->
      <div v-if="course?.role === 'teacher'" class="invite-code-box">
        <span class="label">邀请码</span>
        <div class="code-display">
          <code>{{ course?.inviteCode }}</code>
          <button @click="copyInviteCode">📋 复制</button>
        </div>
        <p class="hint">将邀请码分享给学生，学生可通过邀请码加入课程</p>
      </div>
    </section>
    
    <!-- 成员列表 -->
    <section class="members-section">
      <div class="section-header">
        <h2>成员列表</h2>
      </div>
      <table class="members-table">
        <thead>
          <tr>
            <th>学号</th>
            <th>姓名</th>
            <th>角色</th>
            <th>加入时间</th>
            <th v-if="course?.role === 'teacher'">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="member in members" :key="member.id">
            <td>{{ member.studentId }}</td>
            <td>{{ member.name }}</td>
            <td>
              <span class="role-badge" :class="member.role">
                {{ member.role === 'teacher' ? '教师' : '学生' }}
              </span>
            </td>
            <td>{{ formatDate(member.joinedAt) }}</td>
            <td v-if="course?.role === 'teacher'">
              <button 
                v-if="member.userId !== currentUserId"
                @click="removeMember(member.id)"
                class="btn-danger"
              >
                移除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
    
    <!-- 学生操作区 -->
    <section v-if="course?.role === 'student'" class="student-actions">
      <button @click="leaveCourse" class="btn-danger">退出课程</button>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuth } from '../composables/useAuth';
import { http } from '../api/http';

interface Course {
  id: string;
  name: string;
  description: string;
  role: 'teacher' | 'student';
  inviteCode: string;
  memberCount: number;
  createdAt: string;
}

interface Member {
  id: string;
  userId: string;
  studentId: string;
  name: string;
  role: 'teacher' | 'student';
  joinedAt: string;
}

const route = useRoute();
const router = useRouter();
const { currentUser, initAuth } = useAuth();

const course = ref<Course | null>(null);
const members = ref<Member[]>([]);
const loading = ref(false);

const currentUserId = computed(() => currentUser.value?.id);

const goBack = () => router.push('/courses');

const loadCourseDetail = async () => {
  const id = route.params.id as string;
  loading.value = true;
  try {
    const { data } = await http.get(`/course/${id}`);
    if (data.success) {
      course.value = data.data;
    }
  } catch (err) {
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const loadMembers = async () => {
  const id = route.params.id as string;
  try {
    const { data } = await http.get(`/course/${id}/members`);
    if (data.success) {
      members.value = data.data.members;
    }
  } catch (err) {
    console.error(err);
  }
};

const copyInviteCode = async () => {
  if (course.value?.inviteCode) {
    await navigator.clipboard.writeText(course.value.inviteCode);
    alert('邀请码已复制');
  }
};

const removeMember = async (memberId: string) => {
  if (!confirm('确定移除该成员？')) return;
  try {
    await http.delete(`/course/${course.value?.id}/members/${memberId}`);
    await loadMembers();
  } catch (err) {
    alert('移除失败');
  }
};

const leaveCourse = async () => {
  if (!confirm('确定退出该课程？')) return;
  try {
    await http.post(`/course/${course.value?.id}/leave`);
    router.push('/courses');
  } catch (err) {
    alert('退出失败');
  }
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN');
};

onMounted(() => {
  initAuth();
  loadCourseDetail();
  loadMembers();
});
</script>
```

---

### A-2. 加入课程改用邀请码 [优先级: P0]

**文件**: `src/views/CoursesView.vue`

**修改内容**:
将"通过课程ID加入"改为"通过邀请码加入"

```vue
<!-- 加入课程弹窗 -->
<div v-if="showJoinModal" class="modal-overlay">
  <div class="modal-content">
    <h3>加入课程</h3>
    <form @submit.prevent="handleJoinCourse">
      <div class="form-group">
        <label>邀请码</label>
        <input 
          v-model="joinForm.inviteCode" 
          placeholder="请输入老师提供的邀请码"
          required
        />
      </div>
      <p class="hint">💡 请向课程教师获取邀请码</p>
      <div class="form-actions">
        <button type="button" @click="showJoinModal = false">取消</button>
        <button type="submit" :disabled="joining">
          {{ joining ? '加入中...' : '加入' }}
        </button>
      </div>
    </form>
  </div>
</div>
```

**API调用**:
```typescript
// 通过邀请码加入课程
POST /api/course/join-by-code
Body: { inviteCode: "ABC123" }
Response: { success: true, data: { courseId, courseName } }
```

---

### A-3. 导航组件优化 [优先级: P1]

**文件**: `src/App.vue`

**修改内容**:
1. 未登录时只显示"登录"和"注册"
2. 登录后显示用户信息和"登出"按钮
3. 根据角色显示不同菜单项

```vue
<template>
  <div id="app">
    <nav class="main-nav">
      <div class="nav-brand">
        <router-link to="/">Sim Studio</router-link>
      </div>
      
      <div class="nav-links" v-if="isAuthenticated">
        <router-link to="/">首页</router-link>
        <router-link to="/courses">课程</router-link>
        <router-link to="/templates">模板</router-link>
        <!-- 工作台暂时隐藏 -->
      </div>
      
      <div class="nav-user">
        <template v-if="isAuthenticated">
          <span class="user-name">
            {{ currentUser?.name }}
            <span class="user-role">({{ currentUser?.role === 'teacher' ? '教师' : '学生' }})</span>
          </span>
          <button @click="handleLogout" class="logout-btn">登出</button>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuth } from './composables/useAuth';

const router = useRouter();
const { currentUser, isAuthenticated, logout, initAuth } = useAuth();

const handleLogout = () => {
  logout();
  localStorage.removeItem('token');
  router.push('/login');
};

onMounted(() => {
  initAuth();
});
</script>
```

---

### A-4. HomeView 首页完善 [优先级: P1]

**文件**: `src/views/HomeView.vue`

**功能需求**:
1. 平台介绍
2. 快速入口（登录/注册 或 我的课程）
3. 功能特性展示

```vue
<template>
  <div class="home-page">
    <!-- Hero区域 -->
    <section class="hero">
      <h1>EduAI Studio</h1>
      <p class="tagline">面向高等教育的AI智能体工作台</p>
      
      <div class="hero-actions" v-if="!isAuthenticated">
        <router-link to="/register" class="btn-primary">立即注册</router-link>
        <router-link to="/login" class="btn-secondary">登录</router-link>
      </div>
      <div class="hero-actions" v-else>
        <router-link to="/courses" class="btn-primary">进入课程</router-link>
      </div>
    </section>
    
    <!-- 功能特性 -->
    <section class="features">
      <h2>核心功能</h2>
      <div class="feature-grid">
        <div class="feature-card">
          <div class="icon">📚</div>
          <h3>课程管理</h3>
          <p>教师创建课程，学生通过邀请码加入</p>
        </div>
        <div class="feature-card">
          <div class="icon">🎨</div>
          <h3>教学模板</h3>
          <p>11个预设AI工作流模板，快速上手</p>
        </div>
        <div class="feature-card">
          <div class="icon">💰</div>
          <h3>成本管理</h3>
          <p>Token配额管理，成本可控</p>
        </div>
        <div class="feature-card">
          <div class="icon">📖</div>
          <h3>知识库</h3>
          <p>上传课程资料，智能检索</p>
        </div>
      </div>
    </section>
  </div>
</template>
```

---

## 模块B：教学模板库

### B-1. TemplatesView 模板库页面 [优先级: P2]

**文件**: `src/views/TemplatesView.vue`（新建）

**UI设计**:
```
┌─────────────────────────────────────────────────────────────┐
│  📚 教学模板库                                               │
│  选择预设模板，快速创建AI教学助手                             │
│                                                               │
│  [全部] [辅导] [评分] [写作] [编程] [研究]    🔍 搜索模板    │
│                                                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  │ 🎓 智能辅导助手  │  │ 📝 作业评分助手  │  │ ✍️ 论文写作助手  │
│  │                 │  │                 │  │                 │
│  │ 根据学生问题提供 │  │ 自动评分并提供   │  │ 辅助论文大纲、  │
│  │ 个性化辅导      │  │ 详细反馈        │  │ 段落写作        │
│  │                 │  │                 │  │                 │
│  │ ⭐入门  ~1000T  │  │ ⭐⭐中级 ~2000T │  │ ⭐⭐中级 ~3000T │
│  │ #辅导 #问答     │  │ #评分 #反馈     │  │ #写作 #论文     │
│  │                 │  │                 │  │                 │
│  │ [查看详情 →]    │  │ [查看详情 →]    │  │ [查看详情 →]    │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘
└─────────────────────────────────────────────────────────────┘
```

**功能需求**:
1. 模板列表（卡片形式展示）
2. 按分类筛选（全部/辅导/评分/写作/编程/研究/其他）
3. 按关键词搜索
4. 显示难度、预估消耗、标签
5. 点击卡片进入详情页

**数据类型定义** (`src/types/template.ts`):
```typescript
export interface Template {
  id: string;
  name: string;
  category: string;       // tutor/grading/writing/programming/research/qa/lab/exam/project/data/language
  description: string;
  difficulty: 'beginner' | 'intermediate' | 'advanced';
  estimatedCost: string;  // "~1000 tokens/次"
  tags: string[];
  usageCount: number;
  isSystem: boolean;
}

export interface TemplateDetail extends Template {
  systemPrompt: string;
  inputSchema: string;
  outputSchema: string;
  exampleInput: string;
  exampleOutput: string;
  documentation: string;
}

export const CATEGORIES = [
  { value: 'all', label: '全部', icon: '📚' },
  { value: 'tutor', label: '辅导', icon: '🎓' },
  { value: 'grading', label: '评分', icon: '📝' },
  { value: 'writing', label: '写作', icon: '✍️' },
  { value: 'programming', label: '编程', icon: '💻' },
  { value: 'research', label: '研究', icon: '🔬' },
  { value: 'other', label: '其他', icon: '📦' }
];
```

**完整代码** (`src/views/TemplatesView.vue`):
```vue
<template>
  <div class="templates-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>📚 教学模板库</h1>
      <p class="subtitle">选择预设模板，快速创建AI教学助手</p>
    </div>
    
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="category-tabs">
        <button 
          v-for="cat in categories" 
          :key="cat.value"
          :class="{ active: currentCategory === cat.value }"
          @click="currentCategory = cat.value"
        >
          {{ cat.icon }} {{ cat.label }}
        </button>
      </div>
      <div class="search-box">
        <input 
          v-model="searchKeyword" 
          placeholder="🔍 搜索模板..."
        />
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>加载模板中...</p>
    </div>
    
    <!-- 模板网格 -->
    <div v-else class="templates-grid">
      <div 
        v-for="template in filteredTemplates" 
        :key="template.id"
        class="template-card"
        @click="goToDetail(template.id)"
      >
        <div class="template-icon">{{ getCategoryIcon(template.category) }}</div>
        <h3 class="template-name">{{ template.name }}</h3>
        <p class="template-desc">{{ template.description }}</p>
        <div class="template-meta">
          <span class="difficulty" :class="template.difficulty">
            {{ getDifficultyLabel(template.difficulty) }}
          </span>
          <span class="cost">{{ template.estimatedCost }}</span>
        </div>
        <div class="template-tags">
          <span v-for="tag in template.tags.slice(0, 3)" :key="tag" class="tag">
            #{{ tag }}
          </span>
        </div>
        <button class="view-btn">
          查看详情
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import type { Template } from '../types/template';

const router = useRouter();
const templates = ref<Template[]>([]);
const loading = ref(true);
const currentCategory = ref('all');
const searchKeyword = ref('');

const categories = [
  { value: 'all', label: '全部', icon: '📚' },
  { value: 'tutor', label: '辅导', icon: '🎓' },
  { value: 'grading', label: '评分', icon: '📝' },
  { value: 'writing', label: '写作', icon: '✍️' },
  { value: 'programming', label: '编程', icon: '💻' },
  { value: 'research', label: '研究', icon: '🔬' },
  { value: 'qa', label: '问答', icon: '🤖' }
];

// Fetch templates mock
const fetchTemplates = async () => {
    // API call placeholder
    // templates.value = await api.getTemplates();
    loading.value = false;
};

const filteredTemplates = computed(() => {
  return templates.value.filter(t => {
    const matchCat = currentCategory.value === 'all' || t.category === currentCategory.value;
    const matchKey = t.name.includes(searchKeyword.value) || 
                     t.description.includes(searchKeyword.value);
    return matchCat && matchKey;
  });
});

const getCategoryIcon = (cat: string) => {
    const found = categories.find(c => c.value === cat);
    return found ? found.icon : '📦';
};

const getDifficultyLabel = (diff: string) => {
    const map: Record<string, string> = {
        beginner: '⭐ 入门',
        intermediate: '⭐⭐ 中级',
        advanced: '⭐⭐⭐ 高级'
    };
    return map[diff] || diff;
};

const goToDetail = (id: string) => {
  router.push(`/templates/${id}`);
};

onMounted(() => {
  fetchTemplates();
});
</script>
```

---

### B-2. TemplateDetailView 模板详情页 [优先级: P2]

**文件**: `src/views/TemplateDetailView.vue`

**功能需求**:
1. 展示模板详细信息（包括System Prompt和示例）
2. "导入到课程"功能
3. 导入时选择目标课程

```vue
<template>
  <div class="template-detail" v-if="template">
    <div class="header">
      <button @click="$router.back()" class="back-btn">← 返回列表</button>
      <h1>{{ template.name }}</h1>
      <div class="tags">
        <span class="tag" v-for="tag in template.tags" :key="tag">#{{ tag }}</span>
      </div>
    </div>

    <div class="content-layout">
      <!-- 左侧：信息概览 -->
      <div class="info-sidebar">
        <div class="info-card">
          <h3>基本信息</h3>
          <p><strong>分类:</strong> {{ template.category }}</p>
          <p><strong>难度:</strong> {{ template.difficulty }}</p>
          <p><strong>预估消耗:</strong> {{ template.estimatedCost }}</p>
          
          <div class="actions">
            <button class="import-btn" @click="showImportModal = true">
              📥 导入到我的课程
            </button>
          </div>
        </div>
        
        <div class="usage-doc">
            <h3>使用说明</h3>
            <div class="markdown-body">{{ template.documentation }}</div>
        </div>
      </div>

      <!-- 右侧：详细配置 -->
      <div class="detail-main">
        <section class="prompt-section">
          <h3>🤖 系统提示词 (System Prompt)</h3>
          <div class="code-block">{{ template.systemPrompt }}</div>
        </section>

        <section class="example-section">
          <h3>💬 对话示例</h3>
          <div class="chat-example">
            <div class="msg user">
              <span class="avatar">👤</span>
              <div class="bubble">{{ template.exampleInput }}</div>
            </div>
            <div class="msg ai">
              <span class="avatar">🤖</span>
              <div class="bubble">{{ template.exampleOutput }}</div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- 导入弹窗 -->
    <div v-if="showImportModal" class="modal-overlay">
      <div class="modal">
        <h3>导入模板</h3>
        <p>请选择要导入的目标课程：</p>
        <select v-model="selectedCourseId">
          <option v-for="c in myTeachingCourses" :key="c.id" :value="c.id">
            {{ c.name }}
          </option>
        </select>
        <div class="modal-actions">
          <button @click="showImportModal = false">取消</button>
          <button @click="handleImport" :disabled="!selectedCourseId">确认导入</button>
        </div>
      </div>
    </div>
  </div>
</template>
```

---

## 模块C：成本与配额管理

### C-1. 配额显示组件 [优先级: P3]

**文件**: `src/components/quota/QuotaStatus.vue`

**功能需求**:
1. 展示今日/本周剩余配额
2. 进度条预警颜色（绿/黄/红）

```vue
<template>
  <div class="quota-status-card">
    <h3>📊 配额使用情况</h3>
    
    <div class="quota-item">
      <div class="label">
        <span>今日使用</span>
        <span class="value">{{ status.todayUsed }} / {{ status.dailyLimit }}</span>
      </div>
      <div class="progress-bar">
        <div 
          class="progress" 
          :style="{ width: status.dailyPercent + '%', backgroundColor: getStatusColor(status.dailyPercent) }"
        ></div>
      </div>
      <p class="remaining">剩余: {{ status.dailyRemaining }} tokens</p>
    </div>

    <div class="quota-item">
      <div class="label">
        <span>本周使用</span>
        <span class="value">{{ status.weekUsed }} / {{ status.weeklyLimit }}</span>
      </div>
      <div class="progress-bar">
        <div 
          class="progress" 
          :style="{ width: status.weeklyPercent + '%', backgroundColor: getStatusColor(status.weeklyPercent) }"
        ></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Props & Logic
</script>
```

### C-2. 配额设置页面 (教师) [优先级: P3]

**文件**: `src/views/QuotaSettingsView.vue`

**功能需求**:
1. 表单：每日限额、每周限额、警告阈值
2. 开关：启用/禁用配额
3. 仅教师可见

---

## 模块D：知识库管理 (Knowledge Base)

### D-1. KnowledgeBaseView 知识库列表 [优先级: P3]

**文件**: `src/views/KnowledgeBaseView.vue`

**功能需求**:
1. 知识库列表展示
2. 创建新知识库
3. 上传文档入口

```vue
<template>
  <div class="kb-page">
    <div class="header">
      <h1>📚 课程知识库</h1>
      <button class="create-btn" @click="showCreateModal=true">
        + 新建知识库
      </button>
    </div>

    <!-- 列表 -->
    <div class="kb-list">
      <div v-for="kb in kbs" :key="kb.id" class="kb-card">
        <h3>{{ kb.name }}</h3>
        <p>{{ kb.description }}</p>
        <div class="stats">
          <span>📄 {{ kb.documentCount }} 文档</span>
          <span :class="kb.status">{{ kb.status }}</span>
        </div>
        <div class="actions">
          <button @click="router.push(`/kb/${kb.id}`)">管理文档</button>
        </div>
      </div>
    </div>
  </div>
</template>
```

### D-2. DocumentUpload 文档上传组件 [优先级: P3]

**文件**: `src/components/kb/DocumentUpload.vue`

**功能需求**:
1. 拖拽上传区域
2. 支持PDF/TXT/Markdown
3. 显示上传进度和解析状态

---

## 模块E：UI/UX 增强

### E-1. 全局样式优化 [优先级: P2]

**文件**: `src/assets/main.css`

**任务**:
1. 定义 CSS 变量主题色
2. 统一按钮、卡片、输入框样式
3. 响应式布局调整

```css
:root {
  --color-primary: #4A90E2;
  --color-secondary: #50E3C2;
  --color-text: #2C3E50;
  --bg-page: #F5F7FA;
}

.btn-primary {
  background: var(--color-primary);
  color: white;
  border-radius: 8px;
  padding: 8px 16px;
  transition: all 0.3s;
}
.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}
```

### E-2. 交互反馈增强 [优先级: P3]

**任务**:
1. 添加 `LoadingSpinner` 组件
2. 统一使用 `ElMessage` 或自定义 Toast 提示
3. 页面切换过渡动画

```vue
<!-- LoadingSpinner.vue -->
<template>
  <div class="spinner-overlay">
    <div class="spinner"></div>
  </div>
</template>

<style scoped>
.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
```

---
          查看详情 →
        </button>
      </div>
      
      <!-- 空状态 -->
      <div v-if="filteredTemplates.length === 0" class="empty-state">
        <div class="empty-icon">🔍</div>
        <h3>未找到模板</h3>
        <p>尝试其他筛选条件或搜索词</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { http } from '../api/http';
import type { Template } from '../types/template';
import { CATEGORIES } from '../types/template';

const router = useRouter();

const templates = ref<Template[]>([]);
const loading = ref(false);
const currentCategory = ref('all');
const searchKeyword = ref('');
const categories = CATEGORIES;

// 筛选后的模板列表
const filteredTemplates = computed(() => {
  let result = templates.value;
  
  // 分类筛选
  if (currentCategory.value !== 'all') {
    result = result.filter(t => t.category === currentCategory.value);
  }
  
  // 关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(t => 
      t.name.toLowerCase().includes(keyword) ||
      t.description.toLowerCase().includes(keyword) ||
      t.tags.some(tag => tag.toLowerCase().includes(keyword))
    );
  }
  
  return result;
});

const getCategoryIcon = (category: string) => {
  const cat = categories.find(c => c.value === category);
  return cat?.icon || '📦';
};

const getDifficultyLabel = (difficulty: string) => {
  const labels: Record<string, string> = {
    beginner: '⭐ 入门',
    intermediate: '⭐⭐ 中级',
    advanced: '⭐⭐⭐ 高级'
  };
  return labels[difficulty] || difficulty;
};

const loadTemplates = async () => {
  loading.value = true;
  try {
    const { data } = await http.get('/templates');
    if (data.success) {
      templates.value = data.data;
    }
  } catch (err) {
    console.error('加载模板失败', err);
  } finally {
    loading.value = false;
  }
};

const goToDetail = (templateId: string) => {
  router.push(`/templates/${templateId}`);
};

onMounted(() => {
  loadTemplates();
});
</script>

<style scoped>
.templates-page {
  padding: 24px;
  color: #e8edf7;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  color: #6de6ff;
  margin: 0 0 8px 0;
}

.subtitle {
  color: rgba(232, 237, 247, 0.6);
  margin: 0;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.category-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.category-tabs button {
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: transparent;
  color: rgba(232, 237, 247, 0.7);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.category-tabs button.active,
.category-tabs button:hover {
  border-color: #6de6ff;
  color: #6de6ff;
  background: rgba(109, 230, 255, 0.1);
}

.search-box input {
  padding: 10px 16px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
  color: #e8edf7;
  width: 250px;
  font-size: 14px;
}

.search-box input:focus {
  outline: none;
  border-color: #6de6ff;
}

.loading {
  text-align: center;
  padding: 60px;
  color: rgba(232, 237, 247, 0.6);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #6de6ff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.template-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.template-card:hover {
  border-color: #6de6ff;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.template-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.template-name {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: #e8edf7;
}

.template-desc {
  color: rgba(232, 237, 247, 0.6);
  font-size: 14px;
  line-height: 1.5;
  margin: 0 0 12px 0;
  min-height: 42px;
}

.template-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 13px;
}

.difficulty {
  padding: 2px 8px;
  border-radius: 4px;
}

.difficulty.beginner {
  background: rgba(34, 197, 94, 0.2);
  color: #22c55e;
}

.difficulty.intermediate {
  background: rgba(234, 179, 8, 0.2);
  color: #eab308;
}

.difficulty.advanced {
  background: rgba(239, 68, 68, 0.2);
  color: #ef4444;
}

.cost {
  color: rgba(232, 237, 247, 0.5);
}

.template-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 16px;
  min-height: 24px;
}

.tag {
  font-size: 12px;
  color: rgba(109, 230, 255, 0.8);
}

.view-btn {
  width: 100%;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid rgba(109, 230, 255, 0.3);
  background: rgba(109, 230, 255, 0.1);
  color: #6de6ff;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}

.view-btn:hover {
  background: rgba(109, 230, 255, 0.2);
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px;
}

.empty-icon { font-size: 48px; margin-bottom: 16px; }
.empty-state h3 { color: #e8edf7; margin: 0 0 8px; }
.empty-state p { color: rgba(232, 237, 247, 0.5); margin: 0; }

@media (max-width: 768px) {
  .filter-bar { flex-direction: column; align-items: stretch; }
  .search-box input { width: 100%; }
  .category-tabs { overflow-x: auto; flex-wrap: nowrap; }
}
</style>
```

---

### B-2. TemplateDetailView 模板详情页 [优先级: P2]

**文件**: `src/views/TemplateDetailView.vue`（新建）

**UI设计**:
```
┌─────────────────────────────────────────────────────────────┐
│  ← 返回模板库                                                │
│                                                               │
│  ┌─ 🎓 智能辅导助手 ─────────────────────────────────────────┐│
│  │                                                           ││
│  │  分类: 辅导    难度: ⭐入门    预估消耗: ~1000 tokens     ││
│  │                                                           ││
│  │  根据学生问题提供个性化辅导，支持多轮对话。适合用于       ││
│  │  课后答疑、知识点讲解、习题辅导等场景。                   ││
│  │                                                           ││
│  │  标签: #辅导 #问答 #个性化                                ││
│  │                                                           ││
│  │  已被使用 128 次                                          ││
│  └───────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌─ 使用说明 ─────────────────────────────────────────────────┐│
│  │  1. 导入模板到您的课程工作区                              ││
│  │  2. 配置相关参数（如课程名称、知识范围）                  ││
│  │  3. 学生通过工作区访问该助手                              ││
│  │  4. 查看使用统计和对话记录                                ││
│  └───────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌─ 示例对话 ─────────────────────────────────────────────────┐│
│  │  👤 学生: 什么是递归？我不太理解这个概念。                ││
│  │  🤖 助手: 递归是一种函数调用自身的编程技术...             ││
│  └───────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌─ 导入到课程 ───────────────────────────────────────────────┐│
│  │  选择课程: [人工智能导论         ▼]                       ││
│  │  [立即导入]                                                ││
│  └───────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

**完整代码** (`src/views/TemplateDetailView.vue`):
```vue
<template>
  <div class="template-detail-page">
    <!-- 返回按钮 -->
    <div class="page-nav">
      <button @click="goBack" class="back-btn">← 返回模板库</button>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
    </div>
    
    <template v-else-if="template">
      <!-- 模板信息卡片 -->
      <section class="template-info-card">
        <div class="template-header">
          <span class="template-icon">{{ getCategoryIcon(template.category) }}</span>
          <h1>{{ template.name }}</h1>
        </div>
        
        <div class="template-meta">
          <span class="meta-item">
            <strong>分类:</strong> {{ getCategoryLabel(template.category) }}
          </span>
          <span class="meta-item">
            <strong>难度:</strong> 
            <span class="difficulty" :class="template.difficulty">
              {{ getDifficultyLabel(template.difficulty) }}
            </span>
          </span>
          <span class="meta-item">
            <strong>预估消耗:</strong> {{ template.estimatedCost }}
          </span>
        </div>
        
        <p class="template-desc">{{ template.description }}</p>
        
        <div class="template-tags">
          <span v-for="tag in template.tags" :key="tag" class="tag">#{{ tag }}</span>
        </div>
        
        <p class="usage-count">已被使用 {{ template.usageCount }} 次</p>
      </section>
      
      <!-- 使用说明 -->
      <section class="section-card">
        <h2>📖 使用说明</h2>
        <div class="documentation" v-html="formatDocumentation(template.documentation)"></div>
      </section>
      
      <!-- 示例对话 -->
      <section class="section-card" v-if="template.exampleInput && template.exampleOutput">
        <h2>💬 示例对话</h2>
        <div class="example-dialog">
          <div class="dialog-item user">
            <span class="avatar">👤</span>
            <div class="content">
              <strong>学生:</strong>
              <p>{{ template.exampleInput }}</p>
            </div>
          </div>
          <div class="dialog-item assistant">
            <span class="avatar">🤖</span>
            <div class="content">
              <strong>助手:</strong>
              <p>{{ template.exampleOutput }}</p>
            </div>
          </div>
        </div>
      </section>
      
      <!-- 导入到课程 -->
      <section class="section-card import-section">
        <h2>🚀 导入到课程</h2>
        <div class="import-form">
          <div class="form-group">
            <label>选择课程</label>
            <select v-model="selectedCourseId" :disabled="courses.length === 0">
              <option value="">请选择课程</option>
              <option v-for="course in courses" :key="course.id" :value="course.id">
                {{ course.name }}
              </option>
            </select>
          </div>
          <p v-if="courses.length === 0" class="no-course-hint">
            您还没有可以导入模板的课程。请先
            <router-link to="/courses">创建课程</router-link>
          </p>
          <button 
            @click="importTemplate" 
            :disabled="!selectedCourseId || importing"
            class="import-btn"
          >
            {{ importing ? '导入中...' : '立即导入' }}
          </button>
        </div>
      </section>
    </template>
    
    <!-- 错误状态 -->
    <div v-else class="error-state">
      <p>模板不存在或加载失败</p>
      <button @click="goBack">返回模板库</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { http } from '../api/http';
import type { TemplateDetail } from '../types/template';
import { CATEGORIES } from '../types/template';

const route = useRoute();
const router = useRouter();

const template = ref<TemplateDetail | null>(null);
const courses = ref<{ id: string; name: string }[]>([]);
const loading = ref(false);
const importing = ref(false);
const selectedCourseId = ref('');

const goBack = () => router.push('/templates');

const getCategoryIcon = (category: string) => {
  const cat = CATEGORIES.find(c => c.value === category);
  return cat?.icon || '📦';
};

const getCategoryLabel = (category: string) => {
  const cat = CATEGORIES.find(c => c.value === category);
  return cat?.label || category;
};

const getDifficultyLabel = (difficulty: string) => {
  const labels: Record<string, string> = {
    beginner: '⭐ 入门',
    intermediate: '⭐⭐ 中级',
    advanced: '⭐⭐⭐ 高级'
  };
  return labels[difficulty] || difficulty;
};

const formatDocumentation = (doc: string) => {
  if (!doc) return '<p>暂无使用说明</p>';
  // 简单的换行处理
  return doc.split('\n').map(line => `<p>${line}</p>`).join('');
};

const loadTemplate = async () => {
  const id = route.params.id as string;
  loading.value = true;
  try {
    const { data } = await http.get(`/templates/${id}`);
    if (data.success) {
      template.value = data.data;
    }
  } catch (err) {
    console.error('加载模板详情失败', err);
  } finally {
    loading.value = false;
  }
};

const loadMyCourses = async () => {
  try {
    const { data } = await http.get('/course');
    if (data.success) {
      // 只显示我教的课（教师才能导入模板）
      courses.value = data.data.teaching || [];
    }
  } catch (err) {
    console.error('加载课程列表失败', err);
  }
};

const importTemplate = async () => {
  if (!selectedCourseId.value || !template.value) return;
  
  importing.value = true;
  try {
    const { data } = await http.post(`/templates/${template.value.id}/import`, {
      courseId: selectedCourseId.value
    });
    if (data.success) {
      alert('导入成功！');
      // 可以跳转到工作区
      // router.push(`/workspace/${selectedCourseId.value}`);
    } else {
      alert(data.error || '导入失败');
    }
  } catch (err: any) {
    alert(err?.response?.data?.error || '导入失败');
  } finally {
    importing.value = false;
  }
};

onMounted(() => {
  loadTemplate();
  loadMyCourses();
});
</script>

<style scoped>
.template-detail-page {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
  color: #e8edf7;
}

.page-nav { margin-bottom: 24px; }

.back-btn {
  background: none;
  border: none;
  color: #6de6ff;
  cursor: pointer;
  font-size: 14px;
  padding: 8px 0;
}

.back-btn:hover { text-decoration: underline; }

.loading {
  text-align: center;
  padding: 60px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #6de6ff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto;
}

@keyframes spin { to { transform: rotate(360deg); } }

.template-info-card,
.section-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
}

.template-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.template-icon { font-size: 40px; }

.template-header h1 {
  font-size: 24px;
  margin: 0;
  color: #e8edf7;
}

.template-meta {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  font-size: 14px;
}

.meta-item strong { color: rgba(232, 237, 247, 0.6); }

.difficulty.beginner { color: #22c55e; }
.difficulty.intermediate { color: #eab308; }
.difficulty.advanced { color: #ef4444; }

.template-desc {
  color: rgba(232, 237, 247, 0.8);
  line-height: 1.6;
  margin-bottom: 16px;
}

.template-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.tag {
  color: rgba(109, 230, 255, 0.8);
  font-size: 14px;
}

.usage-count {
  color: rgba(232, 237, 247, 0.5);
  font-size: 13px;
  margin: 0;
}

.section-card h2 {
  font-size: 18px;
  margin: 0 0 16px 0;
  color: #e8edf7;
}

.documentation p {
  color: rgba(232, 237, 247, 0.8);
  line-height: 1.6;
  margin: 0 0 8px 0;
}

.example-dialog {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-item {
  display: flex;
  gap: 12px;
}

.dialog-item .avatar { font-size: 24px; }

.dialog-item .content {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  padding: 12px 16px;
  flex: 1;
}

.dialog-item .content strong {
  color: rgba(232, 237, 247, 0.6);
  font-size: 13px;
}

.dialog-item .content p {
  margin: 4px 0 0 0;
  color: #e8edf7;
  line-height: 1.5;
}

.import-form .form-group {
  margin-bottom: 16px;
}

.import-form label {
  display: block;
  margin-bottom: 8px;
  color: rgba(232, 237, 247, 0.7);
  font-size: 14px;
}

.import-form select {
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
  color: #e8edf7;
  font-size: 14px;
}

.import-form select:focus {
  outline: none;
  border-color: #6de6ff;
}

.no-course-hint {
  color: rgba(232, 237, 247, 0.5);
  font-size: 14px;
  margin-bottom: 16px;
}

.no-course-hint a { color: #6de6ff; }

.import-btn {
  width: 100%;
  padding: 14px;
  border-radius: 8px;
  border: none;
  background: #6de6ff;
  color: #0b1220;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.import-btn:hover:not(:disabled) { background: #5dd5ef; }
.import-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.error-state {
  text-align: center;
  padding: 60px;
  color: rgba(232, 237, 247, 0.6);
}

.error-state button {
  margin-top: 16px;
  padding: 10px 20px;
  border-radius: 8px;
  border: 1px solid #6de6ff;
  background: transparent;
  color: #6de6ff;
  cursor: pointer;
}
</style>
```

---

### B-3. 路由配置

**文件**: `src/router/index.ts`

添加模板相关路由：
```typescript
{
  path: '/templates',
  name: 'Templates',
  component: () => import('../views/TemplatesView.vue'),
  meta: { requiresAuth: true }
},
{
  path: '/templates/:id',
  name: 'TemplateDetail',
  component: () => import('../views/TemplateDetailView.vue'),
  meta: { requiresAuth: true }
}
```

**API调用**:
```typescript
// 获取模板列表
GET /api/templates
GET /api/templates?category=tutor
Response: { success: true, data: Template[] }

// 获取模板详情
GET /api/templates/{id}
Response: { success: true, data: TemplateDetail }

// 导入模板到课程
POST /api/templates/{id}/import
Body: { courseId: string }
Response: { success: true, data: { workflowId: string } }
```

---

## 模块C：成本与配额管理

### C-1. QuotaSettingsView 配额设置页 [优先级: P3]

**文件**: `src/views/QuotaSettingsView.vue`（新建）

**功能需求**:
1. 教师设置课程Token配额
2. 设置每日/每周限额
3. 查看全班使用情况

---

## 模块D：知识库管理

### D-1. KnowledgeBaseView 知识库页面 [优先级: P3]

**文件**: `src/views/KnowledgeBaseView.vue`（新建）

**功能需求**:
1. 知识库列表
2. 文档上传
3. 搜索测试界面

---

## 模块E：前端UI增强

### E-1. WorkspaceView 工作区页面 [优先级: P2]

**文件**: `src/views/WorkspaceView.vue`

**Phase 1实现**:
- 显示当前课程信息
- "工作区即将上线"占位提示
- 后续对接Sim Studio

---

# 开发规范

## API响应格式
```typescript
interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}
```

## 错误处理
```typescript
try {
  const { data } = await http.get('/api/xxx');
  if (!data.success) {
    throw new Error(data.error || '请求失败');
  }
  // 处理成功
} catch (err: any) {
  const message = err?.response?.data?.error || err?.message || '网络错误';
  alert(message);
  // 或使用toast组件
}
```

## 样式规范
- 使用CSS变量定义主题色
- 组件使用scoped样式
- 遵循深色主题设计

```css
:root {
  --color-primary: #6de6ff;
  --color-secondary: #a855f7;
  --color-bg: #0b1220;
  --color-card: rgba(255, 255, 255, 0.04);
  --color-text: #e8edf7;
  --color-text-muted: rgba(232, 237, 247, 0.6);
  --color-border: rgba(255, 255, 255, 0.1);
  --color-danger: #ff6b6b;
}
```
