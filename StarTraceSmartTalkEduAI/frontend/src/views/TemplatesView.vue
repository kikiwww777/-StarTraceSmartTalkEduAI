<template>
  <div class="templates-page fade-in">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">教学模板中心</h1>
        <p class="page-subtitle">选择经过学术验证的 AI 教学方案，一键开启智能化实验。</p>
      </div>
      <div class="header-right">
        <div class="search-box">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <input type="text" v-model="searchQuery" placeholder="搜索教学场景..." />
        </div>
      </div>
    </div>

    <!-- Category Filter -->
    <div class="category-tabs">
      <button 
        v-for="cat in categoriesWithAll" 
        :key="cat.id"
        class="tab-btn"
        :class="{ active: activeCategory === cat.id }"
        @click="activeCategory = cat.id"
      >
        {{ cat.label }}
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-container">
      <p class="error-message">{{ error }}</p>
      <button @click="loadData" class="retry-btn">重试</button>
    </div>

    <!-- Templates Grid -->
    <div v-else class="templates-grid">
      <div 
        v-for="tpl in filteredTemplates" 
        :key="tpl.id" 
        class="tpl-card"
        @click="previewTemplate(tpl)"
      >
        <div class="tpl-icon-box" :style="{ backgroundColor: tpl.color + '10' }">
          <div v-html="tpl.icon || ''" class="svg-wrapper" :style="{ color: tpl.color }"></div>
        </div>
        <div class="tpl-content">
          <div class="tpl-meta">
            <span class="tpl-tag">{{ getCategoryLabel(tpl) }}</span>
            <div class="tpl-rating">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="#fbbf24" stroke="#fbbf24">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
              </svg>
              <span>{{ tpl.rating }}</span>
            </div>
          </div>
          <h3 class="tpl-title">{{ tpl.title }}</h3>
          <p class="tpl-desc">{{ tpl.description || '暂无描述' }}</p>
          <div class="tpl-footer">
            <div class="tpl-usage">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
              </svg>
              {{ tpl.usageCount || 0 }}+ 使用
            </div>
            <button 
              class="btn-import-mini"
              @click.stop="handleImport(tpl)"
            >
              导入
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </button>
          </div>
        </div>
      </div>
      <div v-if="filteredTemplates.length === 0" class="empty-state">
        <p>暂无模板数据</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { templateApi, templateCategoryApi, type Template, type TemplateCategory } from '@/api/template';
import { useAuth } from '../composables/useAuth';

const { isAuthenticated } = useAuth();
const router = useRouter();
const searchQuery = ref('');
const activeCategory = ref('all');
const categories = ref<TemplateCategory[]>([]);
const templates = ref<Template[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);

// 添加"全部模板"选项到分类列表
const categoriesWithAll = computed(() => {
  const allOption = { id: 'all', label: '全部模板' } as TemplateCategory;
  return [allOption, ...categories.value];
});

// 获取分类标签
const getCategoryLabel = (template: Template): string => {
  if (template.categoryInfo?.label) {
    return template.categoryInfo.label;
  }
  // 如果后端没有返回 categoryInfo，尝试从 categories 中查找
  const category = categories.value.find(cat => cat.id === template.category);
  return category?.label || template.category || '未知分类';
};

// 加载分类数据
const loadCategories = async () => {
  try {
    const result = await templateCategoryApi.getAllCategories();
    if (result.code === 200 && result.data) {
      categories.value = result.data;
    } else {
      console.error('加载分类失败:', result.message);
    }
  } catch (err: any) {
    console.error('加载分类出错:', err);
  }
};

// 加载模板数据
const loadTemplates = async () => {
  loading.value = true;
  error.value = null;
  try {
    const category = activeCategory.value === 'all' ? undefined : activeCategory.value;
    const keyword = searchQuery.value.trim() || undefined;
    
    const result = await templateApi.getTemplates(category, keyword);
    if (result.code === 200 && result.data) {
      templates.value = result.data;
    } else {
      error.value = result.message || '加载模板失败';
      templates.value = [];
    }
  } catch (err: any) {
    error.value = err.response?.data?.message || err.message || '网络错误，请稍后重试';
    templates.value = [];
    console.error('加载模板出错:', err);
  } finally {
    loading.value = false;
  }
};

// 加载所有数据
const loadData = async () => {
  await Promise.all([loadCategories(), loadTemplates()]);
};

// 过滤后的模板列表（前端二次过滤，主要用于搜索）
const filteredTemplates = computed(() => {
  if (!searchQuery.value.trim()) {
    return templates.value;
  }
  
  const keyword = searchQuery.value.toLowerCase();
  return templates.value.filter(t => {
    const matchesTitle = t.title?.toLowerCase().includes(keyword);
    const matchesDescription = t.description?.toLowerCase().includes(keyword);
    return matchesTitle || matchesDescription;
  });
});

// 预览模板：进入该模板的工作台界面
const previewTemplate = (tpl: Template) => {
  router.push({
    name: 'templateWorkspace',
    params: { id: tpl.id }
  });
};

// 导入模板（增加使用次数），若已登录则成功后跳转到指定地址
const handleImport = async (tpl: Template) => {
  try {
    await templateApi.incrementUsageCount(tpl.id);
    // 重新加载模板数据以更新使用次数
    await loadTemplates();
    // 如果已登录，才提示成功并跳转
    if (isAuthenticated.value) {
      alert(`模板 [${tpl.title}] 导入成功！`);
      // 导入成功后跳转到外部系统
      const simBaseUrl = (() => {
        const envUrl = import.meta.env.VITE_SIM_BASE_URL;
        if (!envUrl || envUrl === '') {
          return '/sim';
        }
        if (envUrl.startsWith('/')) {
          return envUrl.replace(/\/+$/, '');
        }
        return String(envUrl).replace(/\/+$/, '');
      })();
      window.location.href = simBaseUrl;
    }
  } catch (err: any) {
    console.error('导入模板失败:', err);
    alert('导入失败，请稍后重试');
  }
};

// 监听分类变化
watch(activeCategory, () => {
  loadTemplates();
});

// 监听搜索关键词变化（使用防抖）
let searchTimer: ReturnType<typeof setTimeout> | null = null;
watch(searchQuery, () => {
  if (searchTimer) {
    clearTimeout(searchTimer);
  }
  searchTimer = setTimeout(() => {
    loadTemplates();
  }, 500); // 500ms 防抖
});

// 组件挂载时加载数据
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.templates-page {
  padding-bottom: 60px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
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

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  padding: 10px 16px;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  width: 320px;
  transition: all 0.2s;
}

.search-box:focus-within {
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.05);
}

.search-box input {
  border: none;
  outline: none;
  font-size: 0.9rem;
  width: 100%;
}

.category-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
  overflow-x: auto;
  padding: 4px 0;
}

.tab-btn {
  padding: 8px 20px;
  border-radius: 100px;
  font-size: 0.9rem;
  font-weight: 700;
  color: #64748b;
  background: white;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.tab-btn:hover {
  background: #f8fafc;
  color: #475569;
}

.tab-btn.active {
  background: #2563eb;
  color: white;
  border-color: #2563eb;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

/* Grid & Cards */
.templates-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.tpl-card {
  background: white;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  padding: 24px;
  display: flex;
  gap: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tpl-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 12px 30px rgba(0,0,0,0.03);
  transform: translateY(-4px);
}

.tpl-icon-box {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.svg-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.tpl-content {
  flex: 1;
}

.tpl-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.tpl-tag {
  font-size: 0.7rem;
  font-weight: 800;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.tpl-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.75rem;
  font-weight: 800;
  color: #1e293b;
}

.tpl-title {
  font-size: 1.1rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
}

.tpl-desc {
  font-size: 0.85rem;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 20px;
  height: 48px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tpl-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f8fafc;
}

.tpl-usage {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 600;
}

.btn-import-mini {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #f1f5f9;
  color: #475569;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 800;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-import-mini:hover {
  background: #e2e8f0;
  color: #1e293b;
}

/* Loading & Error States */
.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  min-height: 300px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f1f5f9;
  border-top-color: #2563eb;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-message {
  color: #dc2626;
  font-size: 1rem;
  margin-bottom: 16px;
}

.retry-btn {
  padding: 8px 20px;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.retry-btn:hover {
  background: #1d4ed8;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #64748b;
  font-size: 1rem;
}
</style>


