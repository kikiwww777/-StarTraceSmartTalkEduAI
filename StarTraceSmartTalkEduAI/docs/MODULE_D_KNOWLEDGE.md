# 模块D：知识库管理 - 详细设计文档

## 📋 模块概述

知识库管理模块允许教师上传课程资料，系统自动进行向量化处理，支持学生在工作流中进行语义搜索和知识检索。

---

## 🎯 核心功能

### D-1. 知识库创建与管理

**功能描述**：
- 每个课程可创建多个知识库
- 知识库按主题分类（如：课程讲义、参考文献、习题集）
- 显示知识库状态和统计信息

**UI设计**：
```
┌─────────────────────────────────────────────────────────────┐
│  📖 知识库管理 - 人工智能导论                                 │
│                                                               │
│  [+ 创建知识库]                                               │
│                                                               │
│  ┌─────────────────────────────────────────────────────────┐│
│  │  📚 课程讲义                            ✅ 可用          ││
│  │                                                         ││
│  │  12 份文档  |  1,234 个文本块  |  上次更新: 2小时前     ││
│  │                                                         ││
│  │  [管理文档]  [测试搜索]  [设置]  [删除]                 ││
│  └─────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌─────────────────────────────────────────────────────────┐│
│  │  📄 参考文献                            ⏳ 处理中 (3/5)  ││
│  │                                                         ││
│  │  5 份文档  |  处理中...  |  上次更新: 10分钟前          ││
│  │                                                         ││
│  │  [管理文档]  [查看进度]  [设置]  [删除]                 ││
│  └─────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌─────────────────────────────────────────────────────────┐│
│  │  📝 习题集                              ⚠️ 1个文档失败   ││
│  │                                                         ││
│  │  8 份文档  |  658 个文本块  |  上次更新: 昨天           ││
│  │                                                         ││
│  │  [管理文档]  [查看错误]  [设置]  [删除]                 ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

### D-2. 文档上传与处理

**支持的文档格式**：
- PDF (.pdf)
- Word (.docx, .doc)
- PowerPoint (.pptx, .ppt)
- 纯文本 (.txt, .md)
- Excel (.xlsx) - 按行处理

**处理流程**：
```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  上传    │ -> │  解析    │ -> │  分块    │ -> │  向量化  │
│  文档    │    │  文本    │    │  处理    │    │  存储    │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
     ↓               ↓               ↓               ↓
  排队中 →       解析中 →        分块中 →       完成/失败
```

**文档管理UI**：
```
┌─────────────────────────────────────────────────────────────┐
│  📚 课程讲义 - 文档管理                                      │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  拖拽文件到此处上传，或 [选择文件]                     │   │
│  │                                                        │   │
│  │  支持格式: PDF, DOCX, PPTX, TXT, MD, XLSX              │   │
│  │  单文件最大: 50MB                                       │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
│  ┌─ 文档列表 ─────────────────────────────────────────────┐ │
│  │                                                         │ │
│  │  状态    文件名              大小     分块数   上传时间 │ │
│  │  ───────────────────────────────────────────────────── │ │
│  │  ✅      第1章-机器学习基础.pdf  2.3MB   45     昨天    │ │
│  │  ✅      第2章-深度学习.pdf      4.1MB   82     昨天    │ │
│  │  ⏳      第3章-强化学习.pdf      3.2MB   处理中  刚刚   │ │
│  │  ❌      损坏的文件.pdf          1.1MB   解析失败 刚刚   │ │
│  │                                                         │ │
│  │  [重试失败的文档]  [删除选中]                           │ │
│  │                                                         │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

### D-3. 语义搜索测试

**功能描述**：
- 教师可测试知识库搜索效果
- 查看相似度分数和来源文档
- 调整搜索参数（top_k, threshold等）

**搜索测试UI**：
```
┌─────────────────────────────────────────────────────────────┐
│  🔍 搜索测试 - 课程讲义                                      │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  什么是反向传播算法？                           [搜索]│   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
│  搜索设置: 返回数量 [5▼]  相似度阈值 [0.7▼]                  │
│                                                               │
│  ┌─ 搜索结果 ─────────────────────────────────────────────┐ │
│  │                                                         │ │
│  │  📄 匹配 1                                相似度: 0.92 │ │
│  │  ───────────────────────────────────────────────────── │ │
│  │  "反向传播（Backpropagation）是训练神经网络的核心算法。│ │
│  │  它通过计算损失函数相对于每个权重的梯度，然后使用梯度  │ │
│  │  下降法更新权重。具体步骤包括..."                       │ │
│  │                                                         │ │
│  │  来源: 第2章-深度学习.pdf (第23页)                      │ │
│  │  ───────────────────────────────────────────────────── │ │
│  │                                                         │ │
│  │  📄 匹配 2                                相似度: 0.85 │ │
│  │  ───────────────────────────────────────────────────── │ │
│  │  "在多层神经网络中，反向传播算法解决了梯度如何从输出  │ │
│  │  层传递到隐藏层的问题。通过链式法则..."                 │ │
│  │                                                         │ │
│  │  来源: 第2章-深度学习.pdf (第25页)                      │ │
│  │                                                         │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

### D-4. 知识库在工作流中的使用

**集成方式**：
- 模板可配置使用特定知识库
- 工作流节点：知识检索节点
- 上下文增强：将检索结果注入Prompt

**工作流示例**：
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  用户问题   │ -> │  知识检索   │ -> │  LLM生成    │ -> 回答
│             │    │  (RAG)      │    │  增强回答   │
└─────────────┘    └─────────────┘    └─────────────┘
                         ↓
                   ┌─────────────┐
                   │  知识库     │
                   │  (向量DB)   │
                   └─────────────┘
```

---

## 🗄️ 数据库设计

### knowledge_bases 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| course_id | VARCHAR(36) | 课程ID |
| name | VARCHAR(255) | 知识库名称 |
| description | TEXT | 描述 |
| document_count | INT | 文档数量 |
| chunk_count | INT | 文本块数量 |
| total_size_bytes | BIGINT | 总大小(字节) |
| status | ENUM | active/processing/error |
| embedding_model | VARCHAR(100) | 嵌入模型名称 |
| created_by | VARCHAR(36) | 创建者ID |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### knowledge_documents 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| knowledge_base_id | VARCHAR(36) | 知识库ID |
| filename | VARCHAR(255) | 文件名 |
| original_filename | VARCHAR(255) | 原始文件名 |
| file_type | VARCHAR(50) | 文件类型 |
| file_size | BIGINT | 文件大小(字节) |
| storage_path | VARCHAR(500) | 存储路径 |
| chunk_count | INT | 分块数量 |
| char_count | INT | 字符数 |
| status | ENUM | pending/processing/completed/error |
| error_message | TEXT | 错误信息 |
| processed_at | DATETIME | 处理完成时间 |
| uploaded_by | VARCHAR(36) | 上传者ID |
| created_at | DATETIME | 创建时间 |

### knowledge_chunks 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| document_id | VARCHAR(36) | 文档ID |
| chunk_index | INT | 块序号 |
| content | TEXT | 文本内容 |
| token_count | INT | Token数量 |
| metadata | JSON | 元数据(页码、标题等) |
| vector_id | VARCHAR(100) | 向量数据库ID |
| created_at | DATETIME | 创建时间 |

---

## 🔧 后端API

### POST /api/knowledge-base
创建知识库

```java
@PostMapping
public Result<KnowledgeBase> createKnowledgeBase(
        @RequestBody CreateKnowledgeBaseDTO dto,
        @RequestHeader("X-User-Id") String userId) {
    KnowledgeBase kb = knowledgeBaseService.create(dto, userId);
    return Result.ok(kb, "创建成功");
}
```

### GET /api/knowledge-base
获取课程知识库列表

```java
@GetMapping
public Result<List<KnowledgeBaseDTO>> listKnowledgeBases(
        @RequestParam String courseId,
        @RequestHeader("X-User-Id") String userId) {
    List<KnowledgeBaseDTO> list = knowledgeBaseService.listByCourse(courseId, userId);
    return Result.ok(list);
}
```

### POST /api/knowledge-base/{id}/documents
上传文档

```java
@PostMapping("/{id}/documents")
public Result<DocumentDTO> uploadDocument(
        @PathVariable String id,
        @RequestParam("file") MultipartFile file,
        @RequestHeader("X-User-Id") String userId) {
    DocumentDTO doc = documentService.upload(id, file, userId);
    return Result.ok(doc, "上传成功，正在处理");
}
```

### GET /api/knowledge-base/{id}/documents
获取文档列表

```java
@GetMapping("/{id}/documents")
public Result<List<DocumentDTO>> listDocuments(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId) {
    List<DocumentDTO> docs = documentService.listByKnowledgeBase(id, userId);
    return Result.ok(docs);
}
```

### POST /api/knowledge-base/{id}/search
语义搜索

```java
@PostMapping("/{id}/search")
public Result<List<SearchResultDTO>> search(
        @PathVariable String id,
        @RequestBody SearchRequestDTO dto,
        @RequestHeader("X-User-Id") String userId) {
    List<SearchResultDTO> results = searchService.search(
        id, dto.getQuery(), dto.getTopK(), dto.getThreshold()
    );
    return Result.ok(results);
}
```

---

## 📱 前端组件

### KnowledgeBaseView.vue
```vue
<template>
  <div class="knowledge-base-page">
    <div class="page-header">
      <h1>📖 知识库管理</h1>
      <button @click="showCreateModal = true" class="create-btn">
        + 创建知识库
      </button>
    </div>
    
    <!-- 知识库列表 -->
    <div class="kb-list">
      <div v-for="kb in knowledgeBases" :key="kb.id" class="kb-card">
        <div class="kb-header">
          <div class="kb-icon">📚</div>
          <div class="kb-info">
            <h3>{{ kb.name }}</h3>
            <p class="kb-desc">{{ kb.description || '暂无描述' }}</p>
          </div>
          <div class="kb-status" :class="kb.status">
            {{ getStatusLabel(kb.status) }}
          </div>
        </div>
        
        <div class="kb-stats">
          <span>{{ kb.documentCount }} 份文档</span>
          <span>{{ kb.chunkCount.toLocaleString() }} 个文本块</span>
          <span>更新于 {{ formatTime(kb.updatedAt) }}</span>
        </div>
        
        <div class="kb-actions">
          <button @click="goToDocuments(kb.id)">管理文档</button>
          <button @click="goToSearch(kb.id)">测试搜索</button>
          <button @click="deleteKb(kb.id)" class="danger">删除</button>
        </div>
      </div>
    </div>
    
    <!-- 创建知识库弹窗 -->
    <div v-if="showCreateModal" class="modal-overlay">
      <div class="modal-content">
        <h3>创建知识库</h3>
        <div class="form-group">
          <label>名称</label>
          <input v-model="createForm.name" placeholder="如：课程讲义" />
        </div>
        <div class="form-group">
          <label>描述</label>
          <textarea v-model="createForm.description" placeholder="可选描述"></textarea>
        </div>
        <div class="form-actions">
          <button @click="showCreateModal = false">取消</button>
          <button @click="createKnowledgeBase" class="primary">创建</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { http } from '../api/http';

interface KnowledgeBase {
  id: string;
  name: string;
  description: string;
  documentCount: number;
  chunkCount: number;
  status: 'active' | 'processing' | 'error';
  updatedAt: string;
}

const router = useRouter();
const route = useRoute();
const courseId = route.params.courseId as string;

const knowledgeBases = ref<KnowledgeBase[]>([]);
const showCreateModal = ref(false);
const createForm = ref({ name: '', description: '' });

const getStatusLabel = (status: string) => {
  const labels = {
    active: '✅ 可用',
    processing: '⏳ 处理中',
    error: '❌ 有错误'
  };
  return labels[status] || status;
};

const formatTime = (time: string) => {
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  if (diff < 3600000) return `${Math.floor(diff/60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff/3600000)}小时前`;
  return date.toLocaleDateString('zh-CN');
};

const loadKnowledgeBases = async () => {
  const { data } = await http.get(`/knowledge-base?courseId=${courseId}`);
  if (data.success) {
    knowledgeBases.value = data.data;
  }
};

const createKnowledgeBase = async () => {
  const { data } = await http.post('/knowledge-base', {
    courseId,
    name: createForm.value.name,
    description: createForm.value.description
  });
  if (data.success) {
    showCreateModal.value = false;
    createForm.value = { name: '', description: '' };
    await loadKnowledgeBases();
  }
};

const goToDocuments = (kbId: string) => {
  router.push(`/knowledge-base/${kbId}/documents`);
};

const goToSearch = (kbId: string) => {
  router.push(`/knowledge-base/${kbId}/search`);
};

onMounted(() => {
  loadKnowledgeBases();
});
</script>
```

### DocumentUpload.vue（拖拽上传组件）
```vue
<template>
  <div 
    class="upload-zone"
    :class="{ dragover: isDragover }"
    @dragover.prevent="isDragover = true"
    @dragleave="isDragover = false"
    @drop.prevent="handleDrop"
  >
    <input 
      type="file" 
      ref="fileInput" 
      @change="handleFileSelect" 
      multiple
      accept=".pdf,.docx,.doc,.pptx,.ppt,.txt,.md,.xlsx"
      style="display: none"
    />
    
    <div class="upload-content">
      <div class="upload-icon">📁</div>
      <p>拖拽文件到此处上传，或 <span @click="$refs.fileInput.click()">选择文件</span></p>
      <p class="upload-hint">支持 PDF, DOCX, PPTX, TXT, MD, XLSX | 最大 50MB</p>
    </div>
    
    <!-- 上传进度列表 -->
    <div v-if="uploadQueue.length" class="upload-list">
      <div v-for="item in uploadQueue" :key="item.id" class="upload-item">
        <span class="filename">{{ item.name }}</span>
        <span class="status">{{ item.status }}</span>
        <div v-if="item.status === 'uploading'" class="progress-bar">
          <div class="progress" :style="{ width: item.progress + '%' }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { http } from '../api/http';

const props = defineProps<{ knowledgeBaseId: string }>();
const emit = defineEmits(['uploaded']);

const isDragover = ref(false);
const uploadQueue = ref<{ id: string; name: string; status: string; progress: number }[]>([]);

const handleDrop = (e: DragEvent) => {
  isDragover.value = false;
  const files = e.dataTransfer?.files;
  if (files) {
    uploadFiles(Array.from(files));
  }
};

const handleFileSelect = (e: Event) => {
  const target = e.target as HTMLInputElement;
  if (target.files) {
    uploadFiles(Array.from(target.files));
  }
};

const uploadFiles = async (files: File[]) => {
  for (const file of files) {
    const id = Date.now().toString();
    uploadQueue.value.push({ id, name: file.name, status: 'uploading', progress: 0 });
    
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      await http.post(`/knowledge-base/${props.knowledgeBaseId}/documents`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        onUploadProgress: (e) => {
          const item = uploadQueue.value.find(i => i.id === id);
          if (item && e.total) {
            item.progress = Math.round((e.loaded / e.total) * 100);
          }
        }
      });
      
      const item = uploadQueue.value.find(i => i.id === id);
      if (item) item.status = '✅ 完成';
      emit('uploaded');
    } catch (err) {
      const item = uploadQueue.value.find(i => i.id === id);
      if (item) item.status = '❌ 失败';
    }
  }
};
</script>
```

---

## ✅ 测试用例

| 编号 | 场景 | 预期结果 |
|------|------|----------|
| D-01 | 创建知识库 | 创建成功 |
| D-02 | 上传PDF文档 | 上传成功，开始处理 |
| D-03 | 上传DOCX文档 | 上传成功，开始处理 |
| D-04 | 上传过大文件 | 提示超出限制 |
| D-05 | 上传不支持格式 | 提示格式不支持 |
| D-06 | 查看处理进度 | 显示正确状态 |
| D-07 | 语义搜索 | 返回相关结果 |
| D-08 | 相似度阈值过滤 | 只返回达标结果 |
| D-09 | 删除文档 | 文档和向量同时删除 |
| D-10 | 删除知识库 | 所有关联数据删除 |
