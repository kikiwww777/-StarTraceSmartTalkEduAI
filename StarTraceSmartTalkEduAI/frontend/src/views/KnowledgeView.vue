<template>
  <div class="knowledge-page fade-in">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">知识库管理</h1>
        <p class="page-subtitle">构建您的专属学术大脑，支持多样化教学资源的结构化存储。</p>
      </div>
      <div class="header-right">
        <button class="btn-primary-flat">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <line x1="12" y1="5" x2="12" y2="19" />
            <line x1="5" y1="12" x2="19" y2="12" />
          </svg>
          新建知识库
        </button>
      </div>
    </div>

    <!-- Search & Filter Area -->
    <div class="action-bar">
      <div class="category-tabs">
        <button 
          v-for="cat in categories" 
          :key="cat.id"
          class="tab-btn"
          :class="{ active: activeCategory === cat.id }"
          @click="activeCategory = cat.id"
        >
          {{ cat.label }}
        </button>
      </div>
      <div class="search-box">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
        <input type="text" v-model="searchQuery" placeholder="在知识库中搜索..." />
      </div>
    </div>

    <!-- Knowledge Base Grid -->
    <div class="knowledge-grid">
      <div 
        v-for="kb in filteredKB" 
        :key="kb.id" 
        class="kb-card"
      >
        <div class="kb-header">
          <div class="kb-icon-container" :style="{ color: kb.color }">
            <div v-html="kb.icon"></div>
          </div>
          <div class="kb-options">
            <button class="btn-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="1" /><circle cx="12" cy="5" r="1" /><circle cx="12" cy="19" r="1" />
              </svg>
            </button>
          </div>
        </div>
        
        <div class="kb-body">
          <h3 class="kb-name">{{ kb.name }}</h3>
          <p class="kb-desc">{{ kb.description }}</p>
        </div>

        <div class="kb-stats">
          <div class="stat-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" /><polyline points="14 2 14 8 20 8" />
            </svg>
            <span>{{ kb.fileCount }} 文件</span>
          </div>
          <div class="stat-item">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
            </svg>
            <span>{{ kb.lastModified }}</span>
          </div>
        </div>

        <div class="kb-footer">
          <span class="kb-type-badge">{{ kb.typeLabel }}</span>
          <button class="btn-enter" @click="enterKB(kb)">进入管理</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

const searchQuery = ref('');
const activeCategory = ref('all');

const categories = [
  { id: 'all', label: '全部' },
  { id: 'doc', label: '文档库' },
  { id: 'dataset', label: '数据集' },
  { id: 'multimedia', label: '多媒体' }
];

const knowledgeBases = [
  {
    id: 'kb1',
    name: '计算机科学核心论文集',
    description: '收录了从 1950 年至今的经典计算机科学论文，包含分布式系统、算法与人工智能。',
    type: 'doc',
    typeLabel: '学术文档',
    fileCount: 145,
    lastModified: '2小时前',
    color: '#2563eb',
    icon: `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5v-15z"/></svg>`
  },
  {
    id: 'kb2',
    name: 'LLM 训练评估数据集',
    description: '用于评估大语言模型在中文语境下的多任务处理能力，包含 MMLU-zh 等。',
    type: 'dataset',
    typeLabel: '数据集',
    fileCount: 12,
    lastModified: '昨天',
    color: '#059669',
    icon: `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><polyline points="3.27 6.96 12 12.01 20.73 6.96"/><line x1="12" y1="22.08" x2="12" y2="12"/></svg>`
  },
  {
    id: 'kb3',
    name: '高等数学互动课件',
    description: '包含 PDF、动态 SVG 以及模拟实验脚本，支持可视化的函数推导。',
    type: 'doc',
    typeLabel: '混合资源',
    fileCount: 86,
    lastModified: '3天前',
    color: '#d97706',
    icon: `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>`
  },
  {
    id: 'kb4',
    name: '英美文学经典音视频',
    description: '莎士比亚戏剧朗诵录音及相关解析视频，用于语音语感分析实验。',
    type: 'multimedia',
    typeLabel: '音视频',
    fileCount: 42,
    lastModified: '1周前',
    color: '#7c3aed',
    icon: `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>`
  }
];

const filteredKB = computed(() => {
  return knowledgeBases.filter(kb => {
    const matchesSearch = kb.name.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
                          kb.description.toLowerCase().includes(searchQuery.value.toLowerCase());
    const matchesCategory = activeCategory.value === 'all' || kb.type === activeCategory.value;
    return matchesSearch && matchesCategory;
  });
});

const enterKB = (kb: any) => {
  alert(`正在进入知识库: ${kb.name}`);
};
</script>

<style scoped>
.knowledge-page {
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

.btn-primary-flat {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #2563eb;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary-flat:hover {
  background: #1d4ed8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
}

/* Action Bar */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.category-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  padding: 8px 16px;
  border-radius: 100px;
  font-size: 0.85rem;
  font-weight: 700;
  color: #64748b;
  background: white;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn.active {
  background: #1e293b;
  color: white;
  border-color: #1e293b;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  padding: 10px 16px;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  width: 300px;
}

.search-box input {
  border: none;
  outline: none;
  font-size: 0.9rem;
  width: 100%;
}

/* Grid */
.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
}

.kb-card {
  background: white;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  padding: 28px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.kb-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 12px 30px rgba(0,0,0,0.03);
  transform: translateY(-4px);
}

.kb-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.kb-icon-container {
  width: 56px;
  height: 56px;
  background: #f8fafc;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
}

.kb-body {
  flex: 1;
  margin-bottom: 24px;
}

.kb-name {
  font-size: 1.2rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
}

.kb-desc {
  font-size: 0.9rem;
  color: #64748b;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.kb-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f8fafc;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
}

.kb-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.kb-type-badge {
  font-size: 0.7rem;
  font-weight: 800;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.btn-enter {
  background: none;
  border: none;
  color: #2563eb;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
}

.btn-enter:hover {
  text-decoration: underline;
}
</style>
