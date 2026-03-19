<template>
  <div class="class-detail-page fade-in" v-if="!loading">
    <!-- 顶部返回和标题 -->
    <div class="page-top-bar">
      <button class="btn-back" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <line x1="19" y1="12" x2="5" y2="12" />
          <polyline points="12 19 5 12 12 5" />
        </svg>
        <span>返回课程中心</span>
      </button>

      <div v-if="classInfo" class="header-content">
        <div class="header-left">
          <div class="class-badge">
            班级详情
          </div>
          <h1 class="class-title">
            {{ classInfo.className || '未命名班级' }}
          </h1>
          <p class="class-subtitle" v-if="classInfo.course">
            关联课程：{{ classInfo.course.courseCode }} · {{ classInfo.course.name }}
          </p>
        </div>
        <!-- 仅老师可见：一键导入 JSON 学习模板 -->
        <div v-if="isTeacher" class="header-actions">
          <button class="btn-import-template" @click="openTemplateModal()">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 5v14" />
              <path d="M5 12h14" />
            </svg>
            导入学习模板（JSON）
          </button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>正在加载班级详情...</p>
    </div>

    <div v-else-if="classInfo" class="detail-container">
      <!-- 班级基本信息卡片 -->
      <div class="section-card">
        <div class="card-header">
          <div class="header-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
              <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
              <path d="M16 3.13a4 4 0 0 1 0 7.75" />
            </svg>
          </div>
          <h2 class="card-title">班级基本信息</h2>
        </div>

        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">班级名称</span>
            <span class="info-value">{{ classInfo.className || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">班级代码</span>
            <span class="info-value">{{ classInfo.classCode || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">班级状态</span>
            <span class="info-value">
              <span
                class="status-pill"
                :class="classInfo.status === 1 ? 'status-active' : 'status-inactive'"
              >
                {{ classInfo.status === 1 ? '正常' : '停用' }}
              </span>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">任课教师</span>
            <span class="info-value">
              {{ classInfo.teacher?.name || classInfo.teacherId || '未知' }}
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">所属课程</span>
            <span class="info-value">
              <template v-if="classInfo.course">
                {{ classInfo.course.courseCode }} · {{ classInfo.course.name }}
              </template>
              <template v-else>
                课程ID：{{ classInfo.courseId }}
              </template>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">创建时间</span>
            <span class="info-value">{{ formatDate(classInfo.createdAt) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">更新时间</span>
            <span class="info-value">{{ formatDate(classInfo.updatedAt) }}</span>
          </div>
        </div>

        <div class="info-block" v-if="classInfo.description">
          <div class="info-label">班级描述</div>
          <p class="info-description">
            {{ classInfo.description }}
          </p>
        </div>
      </div>

      <!-- 学习模板列表与搜索 -->
      <div class="section-card">
        <div class="card-header card-header-split">
          <div class="card-header-left">
            <div class="header-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#16a34a" stroke-width="2">
                <rect x="3" y="3" width="18" height="18" rx="2" />
                <path d="M7 8h10" />
                <path d="M7 12h6" />
                <path d="M7 16h4" />
              </svg>
            </div>
            <h2 class="card-title">学习模板</h2>
          </div>

          <button class="btn-sim-center" @click="goToSimCenter">
            进入 SIM 中心
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="5" y1="12" x2="19" y2="12" />
              <polyline points="12 5 19 12 12 19" />
            </svg>
          </button>
        </div>

        <div class="template-toolbar">
          <div class="template-search">
            <input
              v-model="templateSearchName"
              type="text"
              class="modern-input template-search-input"
              placeholder="按模板名称搜索，例如：Python 入门"
            />
          </div>
          <div class="template-count" v-if="templates.length">
            共 {{ filteredTemplates.length }} 个模板
          </div>
        </div>

        <div v-if="!templates.length" class="template-empty">
          <p>当前班级还没有学习模板，老师可以点击页面右上角“导入学习模板（JSON）”来创建模板。</p>
        </div>

        <div v-else class="template-list">
          <div
            v-for="tpl in filteredTemplates"
            :key="tpl.id"
            class="template-item-card"
          >
            <div class="template-item-header">
              <div class="template-item-title">
                <span class="template-name">{{ tpl.templateName }}</span>
                <span class="template-meta">
                  <span v-if="tpl.createdAt">创建：{{ formatDate(tpl.createdAt) }}</span>
                  <span v-if="tpl.updatedAt">更新：{{ formatDate(tpl.updatedAt) }}</span>
                  <span v-if="typeof tpl.downloadCount === 'number'">
                    下载：{{ tpl.downloadCount }}
                  </span>
                </span>
              </div>
              <div class="template-item-actions">
                <button
                  class="btn-toggle-template"
                  @click="toggleTemplateCollapse(tpl.id)"
                >
                  {{ isTemplateCollapsed(tpl.id) ? '展开内容' : '收起内容' }}
                </button>
                <button
                  class="btn-download-template"
                  @click="downloadTemplateJson(tpl)"
                >
                  下载 JSON
                </button>
                <button
                  class="btn-start-experiment"
                  @click="startExperiment(tpl)"
                >
                  立即实验
                </button>
                <button
                  v-if="isTeacher"
                  class="btn-edit-template"
                  @click="openTemplateModal(tpl)"
                >
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                  </svg>
                  编辑
                </button>
                <button
                  v-if="isTeacher"
                  class="btn-delete-template"
                  @click="handleDeleteTemplate(tpl)"
                >
                  删除
                </button>
              </div>
            </div>

            <div v-if="!isTemplateCollapsed(tpl.id)" class="template-item-body">
              <div v-if="tpl.description" class="template-description">
                <p>{{ tpl.description }}</p>
              </div>

              <div v-if="tpl.imageUrl" class="template-image-container">
                <img :src="tpl.imageUrl" alt="模板图片" class="template-image" />
              </div>

              <div class="template-json-section">
                <h3 class="json-section-title">模板内容（JSON）</h3>
                <pre class="template-json-view">{{ prettyTemplate(tpl) }}</pre>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 导入学习模板模态框（仅老师） -->
    <div
      v-if="showTemplateModal && isTeacher"
      class="modal-overlay"
      @click="closeTemplateModal"
    >
      <div class="modal-box modal-box-large" @click.stop>
        <div class="modal-header">
          <h2>{{ templateInfo ? '编辑学习模板' : '导入 JSON 学习模板' }}</h2>
          <button class="btn-close" @click="closeTemplateModal">×</button>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>模板名称 <span class="required">*</span></label>
            <input
              v-model="templateForm.name"
              type="text"
              placeholder="例如：Python基础学习模板"
              class="modern-input"
              required
            />
          </div>

          <div class="input-group">
            <label>模板描述</label>
            <textarea
              v-model="templateForm.description"
              placeholder="请输入模板描述（可选）"
              class="modern-textarea"
              rows="3"
            ></textarea>
          </div>

          <div class="input-group">
            <label>模板图片</label>
            <div class="image-upload-area">
              <input
                type="file"
                accept="image/*"
                class="file-input"
                @change="handleImageFileChange"
                ref="imageInputRef"
              />
              <div v-if="templateForm.imagePreview" class="image-preview">
                <img :src="templateForm.imagePreview" alt="预览" />
                <button class="btn-remove-image" @click="removeImagePreview">×</button>
              </div>
              <div v-else-if="templateInfo?.imageUrl" class="image-preview">
                <img :src="templateInfo.imageUrl" alt="当前图片" />
                <button class="btn-remove-image" @click="removeImagePreview">×</button>
              </div>
              <div v-else class="image-placeholder">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                  <circle cx="8.5" cy="8.5" r="1.5" />
                  <polyline points="21 15 16 10 5 21" />
                </svg>
                <p>点击选择图片或拖拽图片到此处</p>
                <p class="image-tip">支持 JPG、PNG、GIF 等格式，建议尺寸 800x600</p>
              </div>
            </div>
          </div>

          <div class="input-group">
            <label>JSON 模板文件 <span class="required">*</span></label>
            <div class="file-upload-area">
              <input
                type="file"
                accept="application/json,.json"
                class="file-input"
                @change="handleTemplateFileChange"
              />
              <div v-if="templateForm.jsonFileName" class="file-info">
                <span>已选择：{{ templateForm.jsonFileName }}</span>
              </div>
              <div v-else class="file-placeholder">
                点击选择 JSON 文件或拖拽文件到此处
              </div>
            </div>
            <p class="input-tip">请选择一个有效的 JSON 文件</p>
          </div>

          <p v-if="templateError" class="error-text">{{ templateError }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeTemplateModal">取消</button>
          <button class="btn-submit" @click="handleTemplateSubmit" :disabled="submittingTemplate">
            {{ submittingTemplate ? '提交中...' : (templateInfo ? '保存修改' : '确认导入') }}
          </button>
        </div>
      </div>
    </div>

    <!-- SIM 实验 iframe 容器 -->
    <div
      v-if="showSimIframe"
      class="sim-iframe-overlay"
      @click.self="showSimIframe = false"
    >
      <div class="sim-iframe-container">
        <div class="sim-iframe-header">
          <h3>SIM 实验工作台</h3>
          <button class="btn-close-iframe" @click="showSimIframe = false">×</button>
        </div>
        <iframe
          ref="simIframeRef"
          :src="simIframeUrl"
          class="sim-iframe"
          frameborder="0"
          allow="clipboard-read; clipboard-write"
          title="SIM 实验工作台"
        ></iframe>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuth } from "../composables/useAuth";
import {
  classApi,
  teacherMaterialApi,
  learningTemplateApi,
  type Class,
  type LearningTemplate,
} from "../api/course";

const route = useRoute();
const router = useRouter();
const { isTeacher } = useAuth();

const loading = ref(true);
const classInfo = ref<Class | null>(null);

const showTemplateModal = ref(false);
// 当前班级的模板列表
const templates = ref<LearningTemplate[]>([]);
// 当前正在编辑的模板（为空表示新建）
const templateInfo = ref<LearningTemplate | null>(null);
const templateForm = ref({
  name: "",
  description: "",
  imageFile: null as File | null,
  imagePreview: "" as string,
  jsonFile: null as File | null,
  jsonFileName: "",
  jsonContent: null as any
});
const templateError = ref("");
const submittingTemplate = ref(false);
const imageInputRef = ref<HTMLInputElement | null>(null);
// 搜索关键字与折叠状态
const templateSearchName = ref("");
const collapsedTemplateIds = ref<number[]>([]);

// ===== 立即实验：打开 SIM 工作台 =====
// 固定使用 localhost:3000
const SIM_BASE_URL = import.meta.env.VITE_SIM_BASE_URL || 'http://localhost:3000';

// iframe 嵌入相关状态
const showSimIframe = ref(false);
const simIframeUrl = ref("");
const simIframeRef = ref<HTMLIFrameElement | null>(null);

const goBack = () => {
  router.push("/courses");
};

/**
 * 进入 SIM 中心（localhost:3000）
 * best-effort：尝试触发登出后再打开登录页，尽量保证每次都需要登录
 */
const goToSimCenter = () => {
  const base = SIM_BASE_URL;
  const ts = Date.now();

  try {
    const iframe = document.createElement("iframe");
    iframe.style.display = "none";
    iframe.src = `${base}/api/auth/sign-out?ts=${ts}`;
    document.body.appendChild(iframe);
    window.setTimeout(() => iframe.remove(), 1500);
  } catch {
    // ignore
  }

  window.open(`${base}/login?fromLogout=true&ts=${ts}`, "_blank", "noopener,noreferrer");
};

/**
 * 立即实验（兼容没有 /embed/workbench 的 Sim 实例）：
 * - 在 iframe 中打开 Sim 登录页面 `/login`
 * - 用户在 iframe 内登录后，可在 Sim 中手动导入模板 JSON
 */
const startExperiment = async (tpl: LearningTemplate) => {
  // 优先走后端 internal-jwt：自动创建工作流 + 导入模板 JSON，然后 iframe 直达工作台
  try {
    const res = await fetch("/api/sim/prepare-lab", {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ templateId: tpl.id }),
    });

    const payload = await res.json().catch(() => null);
    if (!res.ok || !payload) {
      const msg = payload?.message || payload?.error || `请求失败 (${res.status})`;
      throw new Error(msg);
    }

    // 后端返回结构：Result.success({ workflowId, simUrl })
    const simUrl = payload?.data?.simUrl || payload?.data?.simURL || payload?.simUrl;
    const workflowId = payload?.data?.workflowId || payload?.workflowId;
    if (!simUrl) {
      throw new Error("后端未返回 simUrl");
    }

    // 跳转到自研教学工作台（可拖拽节点：Start/Agent）
    router.push({
      name: "eduWorkbench",
      query: {
        simUrl: String(simUrl),
        workflowId: workflowId ? String(workflowId) : "",
      },
    });
  } catch (e: any) {
    console.error("准备实验环境失败，回退到 Sim 登录页：", e);
    const baseUrl = SIM_BASE_URL.replace(/\/+$/, "");
    router.push({
      name: "eduWorkbench",
      query: {
        simUrl: `${baseUrl}/login`,
        workflowId: "",
      },
    });
  }
};

const formatDate = (value?: string): string => {
  if (!value) return "-";
  try {
    const date = new Date(value);
    if (isNaN(date.getTime())) return "-";
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d}`;
  } catch {
    return "-";
  }
};

const loadClassDetail = async () => {
  const idParam = route.params.id as string | undefined;
  if (!idParam) {
    router.push("/courses");
    return;
  }

  const id = parseInt(idParam);
  if (Number.isNaN(id)) {
    router.push("/courses");
    return;
  }

  loading.value = true;
  try {
    const result = await classApi.getById(id);
    if (result.code === 200 && result.data) {
      classInfo.value = result.data;
      // 班级信息加载成功后，再加载该班级的学习模板
      await loadClassTemplates(result.data.id);
    } else {
      alert(result.message || "获取班级详情失败");
      router.push("/courses");
    }
  } catch (e: any) {
    console.error("获取班级详情异常:", e);
    const msg = e?.response?.data?.message ?? e?.message ?? "获取班级详情失败";
    alert(msg);
    router.push("/courses");
  } finally {
    loading.value = false;
  }
};

// 加载当前班级的学习模板列表
const loadClassTemplates = async (classId: number) => {
  try {
    const res = await learningTemplateApi.getByClassId(classId);
    if (res.code === 200 && Array.isArray(res.data)) {
      templates.value = res.data;
    } else {
      templates.value = [];
    }
  } catch (e) {
    console.error("获取班级学习模板失败:", e);
    templates.value = [];
  }
};

// 学习模板：打开 / 关闭模态框（可选传入要编辑的模板）
const openTemplateModal = (tpl?: LearningTemplate) => {
  templateError.value = "";
  templateInfo.value = tpl ?? null;

  if (templateInfo.value) {
    // 编辑模式：填充现有数据
    templateForm.value = {
      name: templateInfo.value.templateName,
      description: templateInfo.value.description || "",
      imageFile: null,
      imagePreview: templateInfo.value.imageUrl || "",
      jsonFile: null,
      jsonFileName: "",
      // 后端存的是 JSON 字符串，这里解析为对象方便展示与编辑
      jsonContent: safeParseJson(templateInfo.value.jsonContent)
    };
  } else {
    // 新建模式：重置表单
    templateForm.value = {
      name: "",
      description: "",
      imageFile: null,
      imagePreview: "",
      jsonFile: null,
      jsonFileName: "",
      jsonContent: null
    };
  }
  showTemplateModal.value = true;
  nextTick(() => {
    // 自动聚焦到模板名称输入框
    const nameInput = document.querySelector('.modal-box input[type="text"]') as HTMLInputElement;
    if (nameInput) nameInput.focus();
  });
};

const closeTemplateModal = () => {
  showTemplateModal.value = false;
  templateError.value = "";
};

// 处理图片文件选择
const handleImageFileChange = (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0];
  if (!file) return;

  // 验证文件类型
  if (!file.type.startsWith("image/")) {
    templateError.value = "请选择图片文件（JPG、PNG、GIF等）";
    return;
  }

  // 验证文件大小（限制5MB）
  if (file.size > 5 * 1024 * 1024) {
    templateError.value = "图片大小不能超过5MB";
    return;
  }

  templateForm.value.imageFile = file;
  templateError.value = "";

  // 创建预览
  const reader = new FileReader();
  reader.onload = (e) => {
    templateForm.value.imagePreview = e.target?.result as string;
  };
  reader.readAsDataURL(file);
};

// 移除图片预览
const removeImagePreview = () => {
  templateForm.value.imageFile = null;
  templateForm.value.imagePreview = "";
  if (imageInputRef.value) {
    imageInputRef.value.value = "";
  }
};

// 处理JSON模板文件选择
const handleTemplateFileChange = (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0];
  if (!file) return;

  templateForm.value.jsonFile = file;
  templateForm.value.jsonFileName = file.name;
  templateError.value = "";

  const reader = new FileReader();
  reader.onload = () => {
    try {
      const text = String(reader.result || "");
      const json = JSON.parse(text);
      templateForm.value.jsonContent = json;
      templateError.value = "";
    } catch (e) {
      console.error("解析学习模板 JSON 失败:", e);
      templateError.value = "JSON 格式解析失败，请检查文件内容。";
      templateForm.value.jsonContent = null;
    }
  };
  reader.onerror = () => {
    templateError.value = "读取文件失败，请重试。";
    templateForm.value.jsonContent = null;
  };
  reader.readAsText(file, "utf-8");
};

// 提交模板表单
const handleTemplateSubmit = async () => {
  // 验证必填项
  if (!templateForm.value.name.trim()) {
    templateError.value = "请输入模板名称";
    return;
  }

  if (!templateForm.value.jsonContent && !templateForm.value.jsonFile) {
    templateError.value = "请选择JSON模板文件";
    return;
  }

  // 如果选择了新的JSON文件但还没解析，先解析
  if (templateForm.value.jsonFile && !templateForm.value.jsonContent) {
    const reader = new FileReader();
    reader.onload = () => {
      try {
        const text = String(reader.result || "");
        templateForm.value.jsonContent = JSON.parse(text);
        submitTemplate();
      } catch (e) {
        templateError.value = "JSON 格式解析失败，请检查文件内容。";
      }
    };
    reader.readAsText(templateForm.value.jsonFile, "utf-8");
    return;
  }

  await submitTemplate();
};

// 执行提交
const submitTemplate = async () => {
  submittingTemplate.value = true;
  templateError.value = "";

  try {
    let imageUrl = templateForm.value.imagePreview;

    // 如果有新上传的图片，先上传图片
    if (templateForm.value.imageFile) {
      try {
        const uploadResult = await teacherMaterialApi.uploadFile(templateForm.value.imageFile);
        if (uploadResult.code === 200 && uploadResult.data) {
          imageUrl = uploadResult.data.url;
        } else {
          throw new Error(uploadResult.message || "图片上传失败");
        }
      } catch (error: any) {
        console.error("图片上传异常:", error);
        templateError.value = error?.response?.data?.message ?? error?.message ?? "图片上传失败";
        submittingTemplate.value = false;
        return;
      }
    }

    // 组装要提交给后端的 JSON 字符串
    const jsonString =
      typeof templateForm.value.jsonContent === "string"
        ? templateForm.value.jsonContent
        : JSON.stringify(templateForm.value.jsonContent ?? {}, null, 2);

    if (!classInfo.value) {
      throw new Error("班级信息未加载，无法保存模板");
    }

    // 从本地缓存获取当前用户信息（后端也会从 header 中拿 X-User-Id，这里显式传 teacherId 便于后端校验）
    const currentUserStr = localStorage.getItem("currentUser");
    let teacherId: number | undefined;
    if (currentUserStr) {
      try {
        const user = JSON.parse(currentUserStr);
        if (user?.id) {
          teacherId = Number(user.id);
        }
      } catch (e) {
        console.warn("解析 currentUser 失败，用 classInfo.teacherId 作为 teacherId");
      }
    }
    // 兜底：如果没拿到，就用班级里的教师 ID
    if (!teacherId && classInfo.value.teacherId) {
      teacherId = Number(classInfo.value.teacherId);
    }

    if (!teacherId) {
      throw new Error("无法确定教师 ID，保存模板失败");
    }

    // 判定是创建还是更新
    if (templateInfo.value) {
      // 更新
      const res = await learningTemplateApi.update({
        id: templateInfo.value.id,
        templateName: templateForm.value.name.trim(),
        description: templateForm.value.description.trim() || undefined,
        imageUrl: imageUrl || undefined,
        jsonContent: jsonString,
        // 可选：允许更新 teacherId/courseId/classId，通常不变
        teacherId,
        courseId: classInfo.value.courseId,
        classId: classInfo.value.id,
      });

      if (res.code !== 200 || !res.data) {
        throw new Error(res.message || "更新学习模板失败");
      }
      templateInfo.value = res.data;
      // 同步更新列表
      const idx = templates.value.findIndex((t) => t.id === res.data.id);
      if (idx !== -1) {
        templates.value.splice(idx, 1, res.data);
      }
      alert("模板更新成功！");
    } else {
      // 创建
      const res = await learningTemplateApi.create({
        templateName: templateForm.value.name.trim(),
        description: templateForm.value.description.trim() || undefined,
        imageUrl: imageUrl || undefined,
        jsonContent: jsonString,
        teacherId,
        courseId: classInfo.value.courseId,
        classId: classInfo.value.id,
      });

      if (res.code !== 200 || !res.data) {
        throw new Error(res.message || "创建学习模板失败");
      }
      templateInfo.value = res.data;
      templates.value.unshift(res.data);
      alert("模板导入成功！");
    }

    // 关闭模态框并刷新展示
    showTemplateModal.value = false;
  } catch (error: any) {
    console.error("提交模板异常:", error);
    templateError.value = error?.message ?? "提交失败，请重试";
  } finally {
    submittingTemplate.value = false;
  }
};

// 安全解析后端 jsonContent 字段
const safeParseJson = (raw: string | null | undefined): any => {
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch (e) {
    console.warn("解析模板 jsonContent 失败，返回原始字符串:", e);
    return raw;
  }
};

// 美化显示 JSON（从后端的 jsonContent 字符串解析后 pretty print）
const prettyTemplate = (tpl: LearningTemplate): string => {
  if (!tpl?.jsonContent) return "";
  try {
    const obj = JSON.parse(tpl.jsonContent);
    return JSON.stringify(obj, null, 2);
  } catch {
    // 如果不是合法 JSON，就原样展示
    return tpl.jsonContent;
  }
};

// 过滤后的模板列表（按名称搜索）
const filteredTemplates = computed(() => {
  const keyword = templateSearchName.value.trim().toLowerCase();
  if (!keyword) return templates.value;
  return templates.value.filter((t) =>
    (t.templateName || "").toLowerCase().includes(keyword)
  );
});

// 折叠/展开模板内容
const isTemplateCollapsed = (id: number): boolean => {
  return collapsedTemplateIds.value.includes(id);
};

const toggleTemplateCollapse = (id: number) => {
  if (isTemplateCollapsed(id)) {
    collapsedTemplateIds.value = collapsedTemplateIds.value.filter((v) => v !== id);
  } else {
    collapsedTemplateIds.value = [...collapsedTemplateIds.value, id];
  }
};

// 删除模板
const handleDeleteTemplate = async (tpl: LearningTemplate) => {
  if (!confirm(`确定要删除模板「${tpl.templateName}」吗？该操作不可恢复。`)) {
    return;
  }
  try {
    const res = await learningTemplateApi.delete(tpl.id);
    if (res.code !== 200) {
      throw new Error(res.message || "删除学习模板失败");
    }
    templates.value = templates.value.filter((t) => t.id !== tpl.id);
    if (templateInfo.value?.id === tpl.id) {
      templateInfo.value = null;
    }
    collapsedTemplateIds.value = collapsedTemplateIds.value.filter((v) => v !== tpl.id);
    alert("模板删除成功！");
  } catch (e: any) {
    console.error("删除模板异常:", e);
    const msg =
      e?.response?.data?.message ?? e?.message ?? "删除模板失败，请稍后重试";
    alert(msg);
  }
};

// 下载模板 JSON，并统计下载量 +1
const downloadTemplateJson = async (tpl: LearningTemplate) => {
  try {
    // 1) 触发浏览器下载
    const raw = tpl.jsonContent ?? "";
    let content = raw;
    try {
      const obj = JSON.parse(raw);
      content = JSON.stringify(obj, null, 2);
    } catch {
      // 非合法 JSON 就原样下载
      content = raw;
    }

    const safeName = String(tpl.templateName || "learning-template")
      .trim()
      .replace(/[\\/:*?"<>|]/g, "_");
    const filename = `${safeName}_${tpl.id}.json`;

    const blob = new Blob([content], { type: "application/json;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    // 2) 调用后端接口下载量 +1，并同步更新 UI
    const res = await learningTemplateApi.increaseDownloadCount(tpl.id);
    if (res.code === 200) {
      const idx = templates.value.findIndex((t) => t.id === tpl.id);
      if (idx !== -1) {
        const cur = templates.value[idx].downloadCount ?? 0;
        templates.value[idx].downloadCount = cur + 1;
      }
    } else {
      console.warn("增加下载量失败:", res.message);
      alert(res.message || "下载成功，但下载量统计失败");
    }
  } catch (e: any) {
    console.error("下载模板异常:", e);
    const msg = e?.response?.data?.message ?? e?.message ?? "下载失败，请重试";
    alert(msg);
  }
};

onMounted(() => {
  loadClassDetail();
});
</script>

<style scoped>
.class-detail-page {
  padding-bottom: 80px;
}

.page-top-bar {
  margin-bottom: 40px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  color: #64748b;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0;
  margin-bottom: 24px;
  transition: color 0.2s;
}

.btn-back:hover {
  color: #1e293b;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.class-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 800;
  margin-bottom: 4px;
  background: #eff6ff;
  color: #2563eb;
}

.class-title {
  font-size: 2rem;
  font-weight: 800;
  color: #1e293b;
}

.class-subtitle {
  font-size: 0.95rem;
  color: #64748b;
}

.detail-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-card {
  background: white;
  border-radius: 24px;
  border: 1px solid #f1f5f9;
  padding: 32px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.card-header-split {
  justify-content: space-between;
}

.card-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.btn-sim-center {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  color: #1e293b;
  font-weight: 800;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-sim-center:hover {
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.06);
  transform: translateY(-1px);
}

.btn-sim-center:active {
  transform: translateY(0);
}

.header-icon {
  width: 40px;
  height: 40px;
  background: #f8fafc;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 800;
  color: #1e293b;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px 24px;
  margin-bottom: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 0.8rem;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.info-value {
  font-size: 0.95rem;
  color: #0f172a;
  font-weight: 600;
}

.status-pill {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 700;
}

.status-active {
  background: #dcfce7;
  color: #166534;
}

.status-inactive {
  background: #fee2e2;
  color: #991b1b;
}

.info-block {
  margin-top: 8px;
}

.info-description {
  margin-top: 6px;
  font-size: 0.95rem;
  color: #475569;
  line-height: 1.7;
}

/* 模态框通用样式（复用课程详情的设计风格） */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 40;
}

.modal-box {
  width: 520px;
  max-width: 92vw;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.18);
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h2 {
  margin: 0;
  font-size: 1rem;
  font-weight: 700;
  color: #0f172a;
}

.btn-close {
  border: none;
  background: transparent;
  font-size: 1.4rem;
  line-height: 1;
  cursor: pointer;
  color: #94a3b8;
}

.btn-close:hover {
  color: #1f2933;
}

.modal-body {
  padding: 18px 22px 4px 22px;
}

.modal-footer {
  padding: 16px 22px 18px 22px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  border-top: 1px solid #e2e8f0;
}

.modal-tip {
  font-size: 0.9rem;
  color: #64748b;
  margin-bottom: 14px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.input-group label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #475569;
}

.file-input {
  padding: 8px 10px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  font-size: 0.9rem;
}

.error-text {
  color: #dc2626;
  font-size: 0.85rem;
}

.btn-cancel {
  padding: 8px 16px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  font-size: 0.85rem;
  font-weight: 600;
  color: #475569;
  cursor: pointer;
}

.btn-cancel:hover {
  background: #f8fafc;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-import-template {
  display: flex;
  align-items: center;
  gap: 6px;
  border: none;
  border-radius: 999px;
  padding: 8px 14px;
  background: #22c55e;
  color: #ffffff;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(22, 163, 74, 0.25);
}

.btn-import-template:hover {
  background: #16a34a;
}

.template-json-view {
  max-height: 320px;
  overflow: auto;
  background: #0f172a;
  color: #e5e7eb;
  border-radius: 12px;
  padding: 14px 16px;
  font-size: 0.8rem;
  line-height: 1.6;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono",
    "Courier New", monospace;
  border: 1px solid #1f2937;
}

/* 学习模板列表、搜索与折叠 */
.template-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.template-search {
  flex: 1;
}

.template-search-input {
  max-width: 280px;
}

.template-count {
  font-size: 0.85rem;
  color: #64748b;
}

.template-empty {
  padding: 16px;
  border-radius: 12px;
  background: #f8fafc;
  color: #64748b;
  font-size: 0.9rem;
}

.template-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.template-item-card {
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 16px 18px;
  background: #f9fafb;
}

.template-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.template-item-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.template-name {
  font-size: 0.98rem;
  font-weight: 700;
  color: #111827;
}

.template-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 0.8rem;
  color: #94a3b8;
}

.template-item-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.btn-toggle-template {
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  font-size: 0.8rem;
  color: #475569;
  cursor: pointer;
}

.btn-toggle-template:hover {
  background: #f8fafc;
}

.btn-download-template {
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid #bbf7d0;
  background: #f0fdf4;
  font-size: 0.8rem;
  color: #166534;
  cursor: pointer;
  font-weight: 700;
}

.btn-download-template:hover {
  background: #dcfce7;
  border-color: #86efac;
}

.btn-start-experiment {
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid #93c5fd;
  background: #eff6ff;
  font-size: 0.8rem;
  color: #1d4ed8;
  cursor: pointer;
  font-weight: 800;
}

.btn-start-experiment:hover {
  background: #dbeafe;
  border-color: #60a5fa;
}

.sim-overlay {
  z-index: 60;
}

.sim-modal {
  width: 92vw;
  max-width: 1200px;
  height: 86vh;
  display: flex;
  flex-direction: column;
}

.sim-modal-header {
  padding: 14px 18px;
}

.sim-modal-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-sim-reload {
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  color: #334155;
  font-weight: 700;
  font-size: 0.8rem;
  cursor: pointer;
}

.btn-sim-reload:hover {
  background: #f8fafc;
}

.sim-modal-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px;
}

.sim-status {
  font-size: 0.85rem;
  color: #64748b;
  font-weight: 700;
}

.sim-iframe-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.75);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.sim-iframe-container {
  width: 100%;
  max-width: 95vw;
  height: 100%;
  max-height: 95vh;
  background: #fff;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.sim-iframe-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
  background: #f9fafb;
}

.sim-iframe-header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #1e293b;
}

.btn-close-iframe {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-close-iframe:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.sim-iframe {
  width: 100%;
  height: 100%;
  flex: 1;
  border: none;
  background: #fff;
}

.btn-delete-template {
  padding: 4px 10px;
  border-radius: 8px;
  border: 1px solid #fecaca;
  background: #fef2f2;
  font-size: 0.8rem;
  color: #b91c1c;
  cursor: pointer;
  font-weight: 600;
}

.btn-delete-template:hover {
  background: #fee2e2;
  border-color: #fca5a5;
}

.template-item-body {
  margin-top: 12px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 60px 0;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  border: 3px solid #e2e8f0;
  border-top-color: #2563eb;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 模板相关样式 */
.template-description {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  border-left: 4px solid #2563eb;
}

.template-description p {
  margin: 0;
  color: #475569;
  line-height: 1.6;
  font-size: 0.95rem;
}

.template-image-container {
  margin-bottom: 24px;
  text-align: center;
}

.template-image {
  max-width: 100%;
  max-height: 400px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  object-fit: contain;
}

.template-json-section {
  margin-top: 24px;
}

.json-section-title {
  font-size: 1rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
}

/* 模态框样式增强 */
.modal-box-large {
  max-width: 700px;
  width: 90%;
}

.modern-input,
.modern-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9rem;
  transition: all 0.2s;
  font-family: inherit;
}

.modern-input:focus,
.modern-textarea:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.modern-textarea {
  resize: vertical;
  min-height: 80px;
}

.required {
  color: #ef4444;
  margin-left: 2px;
}

.input-tip {
  margin-top: 6px;
  font-size: 0.8rem;
  color: #64748b;
}

/* 图片上传区域 */
.image-upload-area {
  position: relative;
}

.image-preview {
  position: relative;
  display: inline-block;
  margin-top: 12px;
  border-radius: 12px;
  overflow: hidden;
  border: 2px solid #e2e8f0;
}

.image-preview img {
  display: block;
  max-width: 300px;
  max-height: 200px;
  object-fit: cover;
}

.btn-remove-image {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.btn-remove-image:hover {
  background: rgba(0, 0, 0, 0.8);
}

.image-placeholder {
  margin-top: 12px;
  padding: 40px 20px;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  text-align: center;
  background: #f8fafc;
  cursor: pointer;
  transition: all 0.2s;
}

.image-placeholder:hover {
  border-color: #2563eb;
  background: #eff6ff;
}

.image-placeholder svg {
  color: #94a3b8;
  margin-bottom: 12px;
}

.image-placeholder p {
  margin: 8px 0;
  color: #64748b;
  font-size: 0.9rem;
}

.image-tip {
  font-size: 0.8rem !important;
  color: #94a3b8 !important;
}

/* 文件上传区域 */
.file-upload-area {
  margin-top: 8px;
}

.file-info {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  color: #166534;
  font-size: 0.85rem;
}

.file-placeholder {
  margin-top: 8px;
  padding: 20px;
  border: 2px dashed #cbd5e1;
  border-radius: 8px;
  text-align: center;
  color: #64748b;
  font-size: 0.85rem;
  background: #f8fafc;
}

.btn-edit-template {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 8px;
  color: #2563eb;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit-template:hover {
  background: #dbeafe;
  border-color: #93c5fd;
}

.btn-submit {
  padding: 8px 20px;
  border-radius: 999px;
  border: none;
  background: #2563eb;
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-submit:hover:not(:disabled) {
  background: #1d4ed8;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>


