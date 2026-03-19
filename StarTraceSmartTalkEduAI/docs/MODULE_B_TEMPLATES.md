# 模块B：教学模板库 - 详细设计文档

## 📋 模块概述

教学模板库是预设的AI工作流模板集合，帮助教师快速创建教学场景的智能助手。

---

## 🎯 核心功能

### B-1. 模板浏览与筛选

**功能描述**：
- 展示11个预设教学模板
- 支持按分类筛选（辅导/评分/写作/编程等）
- 支持按难度筛选（入门/中级/高级）
- 显示每个模板的预估Token消耗

**UI设计**：
```
┌─────────────────────────────────────────────────────────────┐
│  教学模板库                                                   │
│                                                               │
│  [全部] [辅导] [评分] [写作] [编程] [研究]    🔍 搜索模板      │
│                                                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐│
│  │ 🎓 智能辅导助手  │  │ 📝 作业评分助手  │  │ ✍️ 论文写作助手  ││
│  │                 │  │                 │  │                 ││
│  │ 根据学生问题提供 │  │ 自动评分并提供   │  │ 辅助论文大纲、  ││
│  │ 个性化辅导      │  │ 详细反馈        │  │ 段落写作        ││
│  │                 │  │                 │  │                 ││
│  │ ⭐入门  ~1000 T │  │ ⭐⭐中级 ~2000 T│  │ ⭐⭐中级 ~3000 T││
│  │                 │  │                 │  │                 ││
│  │ [查看详情]      │  │ [查看详情]      │  │ [查看详情]      ││
│  └─────────────────┘  └─────────────────┘  └─────────────────┘│
│  ...                                                          │
└─────────────────────────────────────────────────────────────┘
```

---

### B-2. 11个预设模板详细规格

#### 模板1: 智能辅导助手 (tutor)
```yaml
id: template-001
name: 智能辅导助手
category: tutor
difficulty: beginner
estimatedCost: ~1000 tokens/次
description: 根据学生问题提供个性化辅导，支持多轮对话
tags: ["辅导", "问答", "个性化"]

systemPrompt: |
  你是一位耐心的课程辅导助手。你的任务是：
  1. 理解学生的问题
  2. 用清晰易懂的方式解释概念
  3. 提供相关示例
  4. 引导学生自主思考，而不是直接给出答案
  5. 如果学生理解有误，温和地纠正
  
  注意：
  - 使用鼓励性语言
  - 分步骤解释复杂问题
  - 根据学生水平调整解释深度

inputSchema: |
  {
    "question": "string, 学生问题",
    "context": "string, 可选, 课程背景或之前的对话"
  }

outputSchema: |
  {
    "answer": "string, 辅导回答",
    "relatedTopics": "array, 相关知识点",
    "followUpQuestions": "array, 引导问题"
  }
```

#### 模板2: 作业评分助手 (grading)
```yaml
id: template-002
name: 作业评分助手
category: grading
difficulty: intermediate
estimatedCost: ~2000 tokens/份
description: 根据评分标准自动评分并提供详细反馈
tags: ["评分", "反馈", "作业"]

systemPrompt: |
  你是一位严谨的作业评分助手。你的任务是：
  1. 根据提供的评分标准评估作业
  2. 给出具体分数和评分理由
  3. 指出优点和需要改进的地方
  4. 提供具体的改进建议
  
  评分时注意：
  - 客观公正，有理有据
  - 反馈具体，指出具体位置和问题
  - 语气友善，鼓励学生

inputSchema: |
  {
    "assignment": "string, 作业内容",
    "rubric": "string, 评分标准",
    "maxScore": "number, 满分值"
  }

outputSchema: |
  {
    "score": "number, 得分",
    "feedback": "string, 总体反馈",
    "strengths": "array, 优点列表",
    "improvements": "array, 改进建议",
    "detailedComments": "array, 逐项评语"
  }
```

#### 模板3: 论文写作助手 (writing)
```yaml
id: template-003
name: 论文写作助手
category: writing
difficulty: intermediate
estimatedCost: ~3000 tokens/次
description: 辅助论文大纲、段落写作、语法润色
tags: ["写作", "论文", "润色"]

systemPrompt: |
  你是一位学术写作助手。你的任务是：
  1. 帮助构建论文结构和大纲
  2. 辅助撰写各部分内容
  3. 提供语法和表达的改进建议
  4. 确保学术规范性
  
  注意：
  - 保持学术风格
  - 引导学生表达自己的观点
  - 提供多个可选的表达方式
  - 不要代替学生写完整论文

inputSchema: |
  {
    "taskType": "string, outline/draft/polish",
    "content": "string, 当前内容或主题",
    "requirements": "string, 具体要求"
  }

outputSchema: |
  {
    "result": "string, 输出内容",
    "suggestions": "array, 改进建议",
    "references": "array, 参考格式提示"
  }
```

#### 模板4: 代码调试助手 (programming)
```yaml
id: template-004
name: 代码调试助手
category: programming
difficulty: beginner
estimatedCost: ~1500 tokens/次
description: 帮助学生调试代码、解释错误、提供修复建议
tags: ["编程", "调试", "代码"]

systemPrompt: |
  你是一位编程导师。你的任务是：
  1. 分析学生代码中的问题
  2. 解释错误信息的含义
  3. 提供修复建议
  4. 解释为什么会出错以及如何避免
  
  注意：
  - 不要直接给出完整解决方案
  - 引导学生理解问题本质
  - 提供调试思路和方法
  - 鼓励学生尝试

inputSchema: |
  {
    "code": "string, 有问题的代码",
    "language": "string, 编程语言",
    "errorMessage": "string, 可选, 错误信息",
    "expectedOutput": "string, 可选, 期望输出"
  }

outputSchema: |
  {
    "analysis": "string, 问题分析",
    "issues": "array, 问题列表",
    "hints": "array, 修复提示",
    "explanation": "string, 原因解释"
  }
```

#### 模板5: 文献综述助手 (research)
```yaml
id: template-005
name: 文献综述助手
category: research
difficulty: advanced
estimatedCost: ~5000 tokens/次
description: 帮助整理、分析和综述研究文献
tags: ["研究", "文献", "综述"]

systemPrompt: |
  你是一位研究助手。你的任务是：
  1. 帮助整理和分类文献
  2. 提取关键论点和发现
  3. 识别研究趋势和空白
  4. 辅助撰写文献综述部分
  
  注意：
  - 保持学术严谨性
  - 正确引用来源
  - 批判性分析而非简单罗列

inputSchema: |
  {
    "papers": "array, 文献列表（含标题、摘要）",
    "researchQuestion": "string, 研究问题",
    "taskType": "string, summarize/compare/synthesize"
  }

outputSchema: |
  {
    "summary": "string, 综述内容",
    "themes": "array, 主要主题",
    "gaps": "array, 研究空白",
    "recommendations": "array, 建议"
  }
```

#### 模板6-11: 简要规格
```yaml
# 模板6: 课程问答机器人
id: template-006
name: 课程问答机器人
category: qa
difficulty: beginner
estimatedCost: ~500 tokens/次
description: 基于课程知识库回答学生问题

# 模板7: 实验报告助手
id: template-007
name: 实验报告助手
category: lab
difficulty: intermediate
estimatedCost: ~2000 tokens/次
description: 辅助实验报告的结构化写作

# 模板8: 考试复习助手
id: template-008
name: 考试复习助手
category: exam
difficulty: beginner
estimatedCost: ~1000 tokens/次
description: 生成复习题、解析和知识点整理

# 模板9: 项目规划助手
id: template-009
name: 项目规划助手
category: project
difficulty: intermediate
estimatedCost: ~1500 tokens/次
description: 帮助规划项目任务、时间线和里程碑

# 模板10: 数据分析助手
id: template-010
name: 数据分析助手
category: data
difficulty: advanced
estimatedCost: ~3000 tokens/次
description: 辅助数据分析方法选择、代码生成和结果解读

# 模板11: 语言学习助手
id: template-011
name: 语言学习助手
category: language
difficulty: beginner
estimatedCost: ~800 tokens/次
description: 语言学习对话练习、语法纠正和词汇扩展
```

---

### B-3. 模板详情页

**功能描述**：
- 显示模板完整信息
- 使用文档/教程
- 示例输入输出
- 导入到课程功能

**UI设计**：
```
┌─────────────────────────────────────────────────────────────┐
│  ← 返回模板库                                                │
│                                                               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  🎓 智能辅导助手                                        │  │
│  │                                                         │  │
│  │  分类: 辅导    难度: ⭐入门    预估消耗: ~1000 tokens   │  │
│  │                                                         │  │
│  │  根据学生问题提供个性化辅导，支持多轮对话。适合用于     │  │
│  │  课后答疑、知识点讲解、习题辅导等场景。                 │  │
│  │                                                         │  │
│  │  标签: #辅导 #问答 #个性化                              │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌─ 使用说明 ─────────────────────────────────────────────┐  │
│  │                                                         │  │
│  │  1. 导入模板到您的课程工作区                            │  │
│  │  2. 配置相关参数（如课程名称、知识范围）               │  │
│  │  3. 学生通过工作区访问该助手                           │  │
│  │  4. 查看使用统计和对话记录                             │  │
│  │                                                         │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌─ 示例对话 ─────────────────────────────────────────────┐  │
│  │                                                         │  │
│  │  学生: 什么是递归？我不太理解这个概念。                 │  │
│  │                                                         │  │
│  │  助手: 递归是一种函数调用自身的编程技术。让我用一个     │  │
│  │  生活中的例子来解释：想象你在照两面相对的镜子...       │  │
│  │                                                         │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                               │
│  ┌─ 导入到课程 ───────────────────────────────────────────┐  │
│  │                                                         │  │
│  │  选择课程: [人工智能导论 ▼]                            │  │
│  │                                                         │  │
│  │  [立即导入]                                             │  │
│  │                                                         │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

### B-4. 模板导入流程

**流程描述**：
1. 用户选择目标课程
2. 系统在课程工作区创建模板实例
3. 自动配置基础参数
4. 用户可进一步自定义配置

**API设计**：
```
POST /api/templates/{templateId}/import
Body: {
  "courseId": "课程ID",
  "customName": "可选，自定义名称",
  "customConfig": {
    "temperature": 0.7,
    "maxTokens": 1000
  }
}
Response: {
  "success": true,
  "data": {
    "workflowId": "生成的工作流ID",
    "workspaceUrl": "/workspace/xxx"
  }
}
```

---

## 🗄️ 数据库设计

### templates 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| name | VARCHAR(255) | 模板名称 |
| category | VARCHAR(50) | 分类 |
| description | TEXT | 描述 |
| difficulty | ENUM | beginner/intermediate/advanced |
| estimated_cost | VARCHAR(50) | 预估消耗 |
| system_prompt | TEXT | 系统提示词 |
| input_schema | JSON | 输入格式定义 |
| output_schema | JSON | 输出格式定义 |
| example_input | JSON | 示例输入 |
| example_output | JSON | 示例输出 |
| documentation | TEXT | 使用文档 |
| tags | JSON | 标签数组 |
| is_system | BOOLEAN | 是否系统预设 |
| creator_id | VARCHAR(36) | 创建者ID |
| usage_count | INT | 使用次数 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### template_instances 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| template_id | VARCHAR(36) | 模板ID |
| course_id | VARCHAR(36) | 课程ID |
| workflow_id | VARCHAR(36) | 工作流ID |
| custom_name | VARCHAR(255) | 自定义名称 |
| custom_config | JSON | 自定义配置 |
| created_by | VARCHAR(36) | 导入者ID |
| created_at | DATETIME | 创建时间 |

### template_usage_logs 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| instance_id | VARCHAR(36) | 实例ID |
| user_id | VARCHAR(36) | 使用者ID |
| input_tokens | INT | 输入Token数 |
| output_tokens | INT | 输出Token数 |
| used_at | DATETIME | 使用时间 |

---

## 📱 前端组件

### TemplatesView.vue
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
          placeholder="搜索模板..."
          @input="handleSearch"
        />
      </div>
    </div>
    
    <!-- 模板网格 -->
    <div class="templates-grid">
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
          <span v-for="tag in template.tags" :key="tag" class="tag">
            #{{ tag }}
          </span>
        </div>
        <button class="view-btn" @click.stop="goToDetail(template.id)">
          查看详情 →
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { http } from '../api/http';

interface Template {
  id: string;
  name: string;
  category: string;
  description: string;
  difficulty: 'beginner' | 'intermediate' | 'advanced';
  estimatedCost: string;
  tags: string[];
  usageCount: number;
}

const router = useRouter();

const templates = ref<Template[]>([]);
const loading = ref(false);
const currentCategory = ref('all');
const searchKeyword = ref('');

const categories = [
  { value: 'all', label: '全部', icon: '📚' },
  { value: 'tutor', label: '辅导', icon: '🎓' },
  { value: 'grading', label: '评分', icon: '📝' },
  { value: 'writing', label: '写作', icon: '✍️' },
  { value: 'programming', label: '编程', icon: '💻' },
  { value: 'research', label: '研究', icon: '🔬' },
  { value: 'other', label: '其他', icon: '📦' }
];

const filteredTemplates = computed(() => {
  let result = templates.value;
  
  if (currentCategory.value !== 'all') {
    result = result.filter(t => t.category === currentCategory.value);
  }
  
  if (searchKeyword.value) {
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
  const labels = {
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
}

.view-btn:hover {
  background: rgba(109, 230, 255, 0.2);
}
</style>
```

---

## 🔧 后端API

### GET /api/templates
获取模板列表

```java
@GetMapping
public Result<List<TemplateDTO>> listTemplates(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String difficulty,
        @RequestParam(required = false) String keyword) {
    List<TemplateDTO> templates = templateService.listTemplates(category, difficulty, keyword);
    return Result.ok(templates);
}
```

### GET /api/templates/{id}
获取模板详情

```java
@GetMapping("/{id}")
public Result<TemplateDetailDTO> getTemplate(@PathVariable String id) {
    TemplateDetailDTO template = templateService.getTemplateDetail(id);
    if (template == null) {
        return Result.fail("模板不存在");
    }
    return Result.ok(template);
}
```

### POST /api/templates/{id}/import
导入模板到课程

```java
@PostMapping("/{id}/import")
public Result<Map<String, String>> importTemplate(
        @PathVariable String id,
        @RequestBody ImportTemplateDTO dto,
        @RequestHeader("X-User-Id") String userId) {
    // 检查用户是否是课程教师
    // 创建模板实例
    // 生成工作流
    String workflowId = templateService.importToWorkspace(id, dto.getCourseId(), userId, dto.getCustomConfig());
    return Result.ok(Map.of("workflowId", workflowId));
}
```

---

## ✅ 测试用例

| 编号 | 场景 | 预期结果 |
|------|------|----------|
| B-01 | 查看模板列表 | 显示11个预设模板 |
| B-02 | 按分类筛选 | 正确过滤显示 |
| B-03 | 搜索模板 | 匹配名称/描述/标签 |
| B-04 | 查看模板详情 | 显示完整信息和示例 |
| B-05 | 导入模板 | 创建工作流实例 |
| B-06 | 重复导入 | 创建新实例（允许） |
