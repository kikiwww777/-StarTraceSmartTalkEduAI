# 成员B任务书 - 后端开发

## 📋 职责范围
- Spring Boot 后端API开发
- MySQL数据库设计与操作
- 业务逻辑实现

## 🛠️ 技术栈
- **框架**: Spring Boot 3.2 + Java 17
- **ORM**: MyBatis-Plus
- **数据库**: MySQL 8.0
- **认证**: JWT (jjwt)
- **构建**: Maven

## 📂 工作目录
```
backend/
├── src/main/java/com/eduai/
│   ├── controller/    # API控制器
│   ├── service/       # 业务逻辑
│   ├── mapper/        # 数据库操作
│   ├── entity/        # 实体类
│   ├── dto/           # 数据传输对象
│   ├── common/        # 公共类
│   └── util/          # 工具类
└── src/main/resources/
    ├── application.yml
    └── schema.sql
```

---

# Phase 1 任务清单

## 模块A：课程与权限管理

### A-1. 邀请码系统 [优先级: P0]

**数据库设计**:
```sql
-- 在schema.sql中添加 --

-- 课程邀请码表
CREATE TABLE IF NOT EXISTS course_invite_codes (
    id VARCHAR(36) PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    code VARCHAR(8) NOT NULL UNIQUE,         -- 8位随机码
    created_by VARCHAR(36) NOT NULL,          -- 创建者ID
    expires_at DATETIME,                      -- 过期时间（可选）
    max_uses INT DEFAULT 0,                   -- 最大使用次数（0=无限）
    use_count INT DEFAULT 0,                  -- 已使用次数
    is_active BOOLEAN DEFAULT TRUE,           -- 是否有效
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 给courses表添加default_invite_code字段
ALTER TABLE courses ADD COLUMN default_invite_code VARCHAR(8);
```

**实体类**:
```java
// entity/CourseInviteCode.java
package com.eduai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_invite_codes")
public class CourseInviteCode {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    private String courseId;
    private String code;
    private String createdBy;
    private LocalDateTime expiresAt;
    private Integer maxUses;
    private Integer useCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
```

**Mapper**:
```java
// mapper/CourseInviteCodeMapper.java
package com.eduai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduai.entity.CourseInviteCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseInviteCodeMapper extends BaseMapper<CourseInviteCode> {
}
```

**Service添加**:
```java
// 在CourseService接口添加
String generateInviteCode(String courseId, String userId);
void joinCourseByCode(String inviteCode, String userId);
```

**Service实现**:
```java
// 在CourseServiceImpl中添加

private final CourseInviteCodeMapper inviteCodeMapper;

@Override
@Transactional
public String generateInviteCode(String courseId, String userId) {
    // 检查权限
    checkTeacherPermission(courseId, userId);
    
    // 生成8位随机码
    String code = generateRandomCode(8);
    
    CourseInviteCode inviteCode = new CourseInviteCode();
    inviteCode.setId(UUID.randomUUID().toString());
    inviteCode.setCourseId(courseId);
    inviteCode.setCode(code);
    inviteCode.setCreatedBy(userId);
    inviteCode.setIsActive(true);
    inviteCode.setMaxUses(0); // 无限使用
    inviteCode.setUseCount(0);
    
    inviteCodeMapper.insert(inviteCode);
    
    // 更新课程默认邀请码
    Course course = courseMapper.selectById(courseId);
    course.setDefaultInviteCode(code);
    courseMapper.updateById(course);
    
    return code;
}

@Override
@Transactional
public void joinCourseByCode(String code, String userId) {
    // 查找邀请码
    CourseInviteCode inviteCode = inviteCodeMapper.selectOne(
        new LambdaQueryWrapper<CourseInviteCode>()
            .eq(CourseInviteCode::getCode, code)
            .eq(CourseInviteCode::getIsActive, true)
    );
    
    if (inviteCode == null) {
        throw new RuntimeException("邀请码无效或已过期");
    }
    
    // 检查是否已过期
    if (inviteCode.getExpiresAt() != null && 
        inviteCode.getExpiresAt().isBefore(LocalDateTime.now())) {
        throw new RuntimeException("邀请码已过期");
    }
    
    // 检查使用次数
    if (inviteCode.getMaxUses() > 0 && 
        inviteCode.getUseCount() >= inviteCode.getMaxUses()) {
        throw new RuntimeException("邀请码使用次数已达上限");
    }
    
    String courseId = inviteCode.getCourseId();
    
    // 检查是否已加入
    CourseMember existing = memberMapper.selectOne(
        new LambdaQueryWrapper<CourseMember>()
            .eq(CourseMember::getCourseId, courseId)
            .eq(CourseMember::getUserId, userId)
    );
    if (existing != null) {
        throw new RuntimeException("您已加入该课程");
    }
    
    // 添加为学生
    CourseMember member = new CourseMember();
    member.setId(UUID.randomUUID().toString());
    member.setCourseId(courseId);
    member.setUserId(userId);
    member.setRole("student");
    memberMapper.insert(member);
    
    // 更新使用次数
    inviteCode.setUseCount(inviteCode.getUseCount() + 1);
    inviteCodeMapper.updateById(inviteCode);
}

private String generateRandomCode(int length) {
    String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 排除易混淆字符
    StringBuilder sb = new StringBuilder();
    Random random = new SecureRandom();
    for (int i = 0; i < length; i++) {
        sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
}
```

**Controller添加**:
```java
// 在CourseController中添加

/**
 * 通过邀请码加入课程
 */
@PostMapping("/join-by-code")
public Result<Map<String, Object>> joinByCode(
        @RequestBody Map<String, String> body,
        @RequestHeader("X-User-Id") String userId) {
    String code = body.get("inviteCode");
    if (code == null || code.trim().isEmpty()) {
        return Result.fail("请输入邀请码");
    }
    
    courseService.joinCourseByCode(code.trim().toUpperCase(), userId);
    return Result.ok(null, "加入成功");
}

/**
 * 生成邀请码
 */
@PostMapping("/{id}/invite-code")
public Result<Map<String, String>> generateInviteCode(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId) {
    String code = courseService.generateInviteCode(id, userId);
    return Result.ok(Map.of("code", code), "生成成功");
}
```

---

### A-2. 退出课程API [优先级: P0]

**Service添加**:
```java
// CourseService接口
void leaveCourse(String courseId, String userId);
```

**Service实现**:
```java
@Override
@Transactional
public void leaveCourse(String courseId, String userId) {
    // 检查课程是否存在
    Course course = courseMapper.selectById(courseId);
    if (course == null) {
        throw new RuntimeException("课程不存在");
    }
    
    // 检查是否是课程创建者
    if (course.getInstructorId().equals(userId)) {
        throw new RuntimeException("课程创建者无法退出，请先转让课程或删除课程");
    }
    
    // 查找成员记录
    CourseMember member = memberMapper.selectOne(
        new LambdaQueryWrapper<CourseMember>()
            .eq(CourseMember::getCourseId, courseId)
            .eq(CourseMember::getUserId, userId)
    );
    
    if (member == null) {
        throw new RuntimeException("您未加入该课程");
    }
    
    // 删除成员记录
    memberMapper.deleteById(member.getId());
}
```

**Controller添加**:
```java
/**
 * 退出课程
 */
@PostMapping("/{id}/leave")
public Result<Void> leaveCourse(
        @PathVariable String id,
        @RequestHeader("X-User-Id") String userId) {
    courseService.leaveCourse(id, userId);
    return Result.ok(null, "已退出课程");
}
```

---

### A-3. 用户搜索API [优先级: P1]

**Controller**:
```java
// 新建 UserController.java
package com.eduai.controller;

import com.eduai.common.Result;
import com.eduai.entity.User;
import com.eduai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 搜索用户（通过学号或姓名）
     */
    @GetMapping("/search")
    public Result<List<User>> searchUsers(
            @RequestParam String keyword,
            @RequestHeader("X-User-Id") String userId) {
        List<User> users = userService.searchUsers(keyword);
        return Result.ok(users);
    }
}
```

**Service**:
```java
// UserService接口
List<User> searchUsers(String keyword);

// UserServiceImpl实现
@Override
public List<User> searchUsers(String keyword) {
    if (keyword == null || keyword.trim().length() < 2) {
        return Collections.emptyList();
    }
    
    return userMapper.selectList(
        new LambdaQueryWrapper<User>()
            .like(User::getStudentId, keyword)
            .or()
            .like(User::getName, keyword)
            .last("LIMIT 20")
    );
}
```

---

### A-4. 课程详情增强 [优先级: P0]

修改 `CourseDetailDTO` 添加邀请码：

```java
// dto/CourseDetailDTO.java
@Data
public class CourseDetailDTO {
    private String id;
    private String name;
    private String description;
    private String instructorId;
    private String workspaceId;
    private String role;
    private Integer memberCount;
    private String inviteCode;      // 新增：课程邀请码
    private String createdAt;
    private String updatedAt;
}
```

修改 `getCourseDetail` 方法：
```java
@Override
public CourseDetailDTO getCourseDetail(String courseId, String userId) {
    // ... 现有代码 ...
    
    // 如果是教师，返回邀请码
    if ("teacher".equals(membership.getRole())) {
        dto.setInviteCode(course.getDefaultInviteCode());
    }
    
    return dto;
}
```

---

## 模块B：教学模板库

### B-1. 模板表设计 [优先级: P2]

**数据库设计** (`schema.sql`添加):
```sql
-- 模板表
CREATE TABLE IF NOT EXISTS templates (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,           -- tutor/grading/writing/programming/research/qa/lab/exam/project/data/language
    description TEXT,
    difficulty ENUM('beginner', 'intermediate', 'advanced') DEFAULT 'beginner',
    estimated_cost VARCHAR(50),              -- 预估消耗 "~1000 tokens/次"
    system_prompt TEXT,                       -- 系统提示词
    input_schema TEXT,                        -- 输入格式说明
    output_schema TEXT,                       -- 输出格式说明
    example_input TEXT,                       -- 示例输入
    example_output TEXT,                      -- 示例输出
    documentation TEXT,                       -- 使用说明文档
    workflow_json JSON,                       -- 工作流定义JSON (后续对接)
    tags JSON,                                -- 标签数组 ["辅导", "问答"]
    is_system BOOLEAN DEFAULT FALSE,          -- 是否系统预设
    creator_id VARCHAR(36),
    usage_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 模板导入记录表
CREATE TABLE IF NOT EXISTS template_instances (
    id VARCHAR(36) PRIMARY KEY,
    template_id VARCHAR(36) NOT NULL,
    course_id VARCHAR(36) NOT NULL,
    imported_by VARCHAR(36) NOT NULL,
    custom_name VARCHAR(255),                 -- 自定义名称
    custom_config JSON,                       -- 自定义配置
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES templates(id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 模板使用日志表
CREATE TABLE IF NOT EXISTS template_usage_logs (
    id VARCHAR(36) PRIMARY KEY,
    template_id VARCHAR(36) NOT NULL,
    instance_id VARCHAR(36),
    course_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    used_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES templates(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- 创建索引
CREATE INDEX idx_templates_category ON templates(category);
CREATE INDEX idx_template_instances_course ON template_instances(course_id);
```

---

### B-2. 预设模板数据 [优先级: P2]

**初始化数据** (`data.sql`):
```sql
-- 11个预设教学模板
INSERT INTO templates (id, name, category, description, difficulty, estimated_cost, system_prompt, example_input, example_output, documentation, tags, is_system) VALUES

-- 1. 智能辅导助手
('tpl-tutor-001', '智能辅导助手', 'tutor', 
'根据学生问题提供个性化辅导，支持多轮对话。适合课后答疑、知识点讲解、习题辅导。',
'beginner', '~1000 tokens/次',
'你是一个专业的教学辅导助手。请用清晰、耐心的方式帮助学生理解概念。
规则：
1. 使用苏格拉底式提问，引导学生思考
2. 提供示例和类比来解释抽象概念
3. 如果学生的理解有误，温和地纠正
4. 鼓励学生提出更多问题',
'什么是递归？我不太理解这个概念。',
'递归是一种函数调用自身的编程技术。让我用一个生活中的例子来解释：

想象你在排队，你想知道你是第几个人。你可以问前面的人："你是第几个？"
- 如果前面有人，他也可以问他前面的人
- 如果前面没人了，那他就是第1个
- 然后答案一层层传回来：他是第1个，我就是第2个...

这就是递归的思想：把大问题分解成同类型的小问题，直到最简单的情况。

你能想到编程中还有什么问题可以用递归解决吗？',
'1. 导入模板到课程工作区
2. 学生通过工作区向助手提问
3. 助手会引导学生思考并解答',
'["辅导", "问答", "个性化", "多轮对话"]', TRUE),

-- 2. 作业评分助手
('tpl-grading-001', '作业评分助手', 'grading',
'自动评分并提供详细反馈，支持多种作业类型。',
'intermediate', '~2000 tokens/份',
'你是一个专业的作业评分助手。请按以下标准评分：
1. 内容正确性 (60%)
2. 表达清晰度 (20%)
3. 格式规范性 (20%)

输出格式：
- 总分：X/100
- 各项得分明细
- 优点列举
- 改进建议',
'评分标准：计算复杂度分析(100分)
学生答案：快速排序的时间复杂度是O(n^2)',
'## 评分结果

**总分：65/100**

### 各项得分
- 内容正确性：40/60 (部分正确)
- 表达清晰度：15/20
- 格式规范性：10/20

### 评价
**优点：**
- 了解快速排序算法
- 给出了具体复杂度表示

**改进建议：**
- O(n²)是最坏情况时间复杂度，通常讨论平均情况O(n log n)
- 建议补充空间复杂度分析
- 可以区分最好、平均、最坏情况',
'1. 教师设置评分标准
2. 学生提交作业答案
3. 系统自动评分并生成反馈',
'["评分", "反馈", "作业", "自动化"]', TRUE),

-- 3. 论文写作助手
('tpl-writing-001', '论文写作助手', 'writing',
'辅助论文大纲、段落写作和润色，提升学术写作质量。',
'intermediate', '~3000 tokens/次',
'你是学术写作助手，帮助学生改进论文写作。
职责：
1. 优化文章结构和逻辑
2. 改进学术表达方式
3. 检查引用格式
4. 指出需要补充的内容
注意：不要替学生写论文，而是指导改进。',
'帮我优化这段摘要：我的研究是关于机器学习的，我用了很多数据训练模型，结果很好。',
'## 优化建议

**原文问题：**
1. 过于笼统，缺少具体信息
2. "很多数据"和"结果很好"不够学术化
3. 缺少研究方法和贡献说明

**优化后示例：**
> 本研究探讨了[具体问题]，采用[具体方法/模型]对[数据规模]样本进行分析。实验结果表明，所提方法在[评估指标]上达到[具体数值]，相较于基线方法提升了[百分比]。

**你需要补充的信息：**
- 研究的具体问题是什么？
- 使用了什么机器学习方法？
- 数据集规模和来源？
- 具体的评估指标和数值？',
'1. 输入需要优化的文本
2. 获取结构化改进建议
3. 根据建议修改后可再次提交',
'["写作", "论文", "学术", "润色"]', TRUE),

-- 4. 代码调试助手
('tpl-code-001', '代码调试助手', 'programming',
'帮助学生调试代码问题，解释错误原因并提供修复建议。',
'beginner', '~1500 tokens/次',
'你是代码调试助手，帮助学生理解和修复代码问题。
规则：
1. 先解释错误原因
2. 提供修复代码示例
3. 说明如何避免类似问题
4. 不要直接给出完整解决方案，引导学生思考',
NULL, NULL,
'1. 粘贴有问题的代码
2. 描述遇到的错误信息
3. 获取调试建议',
'["编程", "调试", "代码", "Python"]', TRUE),

-- 5. 文献综述助手
('tpl-research-001', '文献综述助手', 'research',
'帮助整理和综述文献，生成结构化的文献分析。',
'advanced', '~5000 tokens/次',
'你是文献综述助手，帮助学生组织和分析学术文献。
输出包括：
1. 文献主题分类
2. 研究方法对比
3. 主要发现总结
4. 研究空白分析',
NULL, NULL,
'1. 输入多篇文献的基本信息
2. 获取结构化的综述框架
3. 识别研究空白和未来方向',
'["研究", "文献", "综述", "分析"]', TRUE),

-- 6. 课程问答机器人
('tpl-qa-001', '课程问答机器人', 'qa',
'回答课程相关问题，基于课程知识库。',
'beginner', '~500 tokens/次',
'你是课程问答助手，帮助学生解答课程相关问题。
规则：
1. 只回答与课程内容相关的问题
2. 引用课程材料时注明来源
3. 对于超出范围的问题，建议学生咨询老师',
NULL, NULL,
'1. 上传课程材料到知识库
2. 学生提问课程相关问题
3. 基于知识库生成回答',
'["问答", "课程", "知识库"]', TRUE),

-- 7. 实验报告助手
('tpl-lab-001', '实验报告助手', 'lab',
'辅助实验报告写作，提供结构和内容建议。',
'intermediate', '~2000 tokens/次',
'你是实验报告写作助手。帮助学生完善报告的：
1. 实验目的和背景
2. 方法描述
3. 结果分析
4. 结论总结',
NULL, NULL,
'1. 输入实验基本信息
2. 获取报告结构建议
3. 对各部分内容提供改进意见',
'["实验", "报告", "写作"]', TRUE),

-- 8. 考试复习助手
('tpl-exam-001', '考试复习助手', 'exam',
'生成复习题和解析，帮助备考。',
'beginner', '~1000 tokens/次',
'你是考试复习助手，帮助学生备考。功能：
1. 根据知识点生成练习题
2. 提供答案解析
3. 总结易错点',
NULL, NULL,
'1. 输入需要复习的章节或知识点
2. 获取练习题
3. 查看答案和解析',
'["复习", "考试", "练习", "题目"]', TRUE),

-- 9-11省略，格式类似...
('tpl-project-001', '项目规划助手', 'project', '帮助规划项目任务和进度', 'intermediate', '~1500 tokens/次', NULL, NULL, NULL, '帮助制定项目计划', '["项目", "规划"]', TRUE),
('tpl-data-001', '数据分析助手', 'data', '辅助数据分析和可视化建议', 'advanced', '~3000 tokens/次', NULL, NULL, NULL, '提供数据分析建议', '["数据", "分析"]', TRUE),
('tpl-lang-001', '语言学习助手', 'language', '语言学习对话练习', 'beginner', '~800 tokens/次', NULL, NULL, NULL, '语言对话练习', '["语言", "学习"]', TRUE);
```

---

### B-3. 实体类定义 [优先级: P2]

**Template实体** (`entity/Template.java`):
```java
package com.eduai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "templates", autoResultMap = true)
public class Template {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    private String name;
    private String category;
    private String description;
    private String difficulty;
    private String estimatedCost;
    
    private String systemPrompt;
    private String inputSchema;
    private String outputSchema;
    private String exampleInput;
    private String exampleOutput;
    private String documentation;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object workflowJson;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;
    
    private Boolean isSystem;
    private String creatorId;
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**TemplateInstance实体** (`entity/TemplateInstance.java`):
```java
package com.eduai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName(value = "template_instances", autoResultMap = true)
public class TemplateInstance {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    private String templateId;
    private String courseId;
    private String importedBy;
    private String customName;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object customConfig;
    
    private Boolean isActive;
    private LocalDateTime createdAt;
}
```

---

### B-4. Mapper接口 [优先级: P2]

```java
// mapper/TemplateMapper.java
package com.eduai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduai.entity.Template;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TemplateMapper extends BaseMapper<Template> {
    
    @Update("UPDATE templates SET usage_count = usage_count + 1 WHERE id = #{id}")
    void incrementUsageCount(String id);
}

// mapper/TemplateInstanceMapper.java
package com.eduai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduai.entity.TemplateInstance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TemplateInstanceMapper extends BaseMapper<TemplateInstance> {
}
```

---

### B-5. DTO定义 [优先级: P2]

```java
// dto/TemplateListDTO.java
package com.eduai.dto;

import lombok.Data;
import java.util.List;

@Data
public class TemplateListDTO {
    private String id;
    private String name;
    private String category;
    private String description;
    private String difficulty;
    private String estimatedCost;
    private List<String> tags;
    private Integer usageCount;
    private Boolean isSystem;
}

// dto/TemplateDetailDTO.java
package com.eduai.dto;

import lombok.Data;
import java.util.List;

@Data
public class TemplateDetailDTO {
    private String id;
    private String name;
    private String category;
    private String description;
    private String difficulty;
    private String estimatedCost;
    private List<String> tags;
    private Integer usageCount;
    private Boolean isSystem;
    
    // 详情额外字段
    private String systemPrompt;
    private String inputSchema;
    private String outputSchema;
    private String exampleInput;
    private String exampleOutput;
    private String documentation;
}

// dto/TemplateImportDTO.java
package com.eduai.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class TemplateImportDTO {
    @NotBlank(message = "课程ID不能为空")
    private String courseId;
    
    private String customName;  // 可选：自定义名称
}
```

---

### B-6. Service层 [优先级: P2]

**TemplateService接口** (`service/TemplateService.java`):
```java
package com.eduai.service;

import com.eduai.dto.TemplateDetailDTO;
import com.eduai.dto.TemplateImportDTO;
import com.eduai.dto.TemplateListDTO;
import java.util.List;

public interface TemplateService {
    
    /**
     * 获取模板列表
     * @param category 分类筛选（可选）
     */
    List<TemplateListDTO> listTemplates(String category);
    
    /**
     * 获取模板详情
     */
    TemplateDetailDTO getTemplateDetail(String templateId);
    
    /**
     * 导入模板到课程
     */
    String importToWorkspace(String templateId, TemplateImportDTO dto, String userId);
    
    /**
     * 获取课程已导入的模板
     */
    List<TemplateListDTO> getCourseTemplates(String courseId);
}
```

**TemplateService实现** (`service/impl/TemplateServiceImpl.java`):
```java
package com.eduai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduai.dto.*;
import com.eduai.entity.*;
import com.eduai.mapper.*;
import com.eduai.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    
    private final TemplateMapper templateMapper;
    private final TemplateInstanceMapper instanceMapper;
    private final CourseMemberMapper memberMapper;
    
    @Override
    public List<TemplateListDTO> listTemplates(String category) {
        LambdaQueryWrapper<Template> query = new LambdaQueryWrapper<>();
        
        // 分类筛选
        if (category != null && !category.isEmpty() && !"all".equals(category)) {
            query.eq(Template::getCategory, category);
        }
        
        // 只显示系统模板（用户自建模板后续支持）
        query.eq(Template::getIsSystem, true);
        query.orderByDesc(Template::getUsageCount);
        
        List<Template> templates = templateMapper.selectList(query);
        
        return templates.stream()
            .map(this::toListDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public TemplateDetailDTO getTemplateDetail(String templateId) {
        Template template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        
        TemplateDetailDTO dto = new TemplateDetailDTO();
        BeanUtils.copyProperties(template, dto);
        return dto;
    }
    
    @Override
    @Transactional
    public String importToWorkspace(String templateId, TemplateImportDTO dto, String userId) {
        // 检查模板存在
        Template template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException("模板不存在");
        }
        
        // 检查用户是否是课程教师
        CourseMember member = memberMapper.selectOne(
            new LambdaQueryWrapper<CourseMember>()
                .eq(CourseMember::getCourseId, dto.getCourseId())
                .eq(CourseMember::getUserId, userId)
        );
        
        if (member == null || !"teacher".equals(member.getRole())) {
            throw new RuntimeException("只有课程教师可以导入模板");
        }
        
        // 检查是否已导入
        TemplateInstance existing = instanceMapper.selectOne(
            new LambdaQueryWrapper<TemplateInstance>()
                .eq(TemplateInstance::getTemplateId, templateId)
                .eq(TemplateInstance::getCourseId, dto.getCourseId())
                .eq(TemplateInstance::getIsActive, true)
        );
        
        if (existing != null) {
            throw new RuntimeException("该模板已导入此课程");
        }
        
        // 创建模板实例
        TemplateInstance instance = new TemplateInstance();
        instance.setId(UUID.randomUUID().toString());
        instance.setTemplateId(templateId);
        instance.setCourseId(dto.getCourseId());
        instance.setImportedBy(userId);
        instance.setCustomName(dto.getCustomName() != null ? dto.getCustomName() : template.getName());
        instance.setIsActive(true);
        
        instanceMapper.insert(instance);
        
        // 更新模板使用次数
        templateMapper.incrementUsageCount(templateId);
        
        return instance.getId();
    }
    
    @Override
    public List<TemplateListDTO> getCourseTemplates(String courseId) {
        // 获取课程已导入的模板实例
        List<TemplateInstance> instances = instanceMapper.selectList(
            new LambdaQueryWrapper<TemplateInstance>()
                .eq(TemplateInstance::getCourseId, courseId)
                .eq(TemplateInstance::getIsActive, true)
        );
        
        return instances.stream()
            .map(inst -> {
                Template template = templateMapper.selectById(inst.getTemplateId());
                TemplateListDTO dto = toListDTO(template);
                // 使用自定义名称
                if (inst.getCustomName() != null) {
                    dto.setName(inst.getCustomName());
                }
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    private TemplateListDTO toListDTO(Template template) {
        TemplateListDTO dto = new TemplateListDTO();
        BeanUtils.copyProperties(template, dto);
        return dto;
    }
}
```

---

### B-7. Controller层 [优先级: P2]

```java
// controller/TemplateController.java
package com.eduai.controller;

import com.eduai.common.Result;
import com.eduai.dto.*;
import com.eduai.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {
    
    private final TemplateService templateService;
    
    /**
     * 获取模板列表
     * GET /api/templates
     * GET /api/templates?category=tutor
     */
    @GetMapping
    public Result<List<TemplateListDTO>> listTemplates(
            @RequestParam(required = false) String category) {
        List<TemplateListDTO> templates = templateService.listTemplates(category);
        return Result.ok(templates);
    }
    
    /**
     * 获取模板详情
     * GET /api/templates/{id}
     */
    @GetMapping("/{id}")
    public Result<TemplateDetailDTO> getTemplateDetail(@PathVariable String id) {
        TemplateDetailDTO template = templateService.getTemplateDetail(id);
        return Result.ok(template);
    }
    
    /**
     * 导入模板到课程
     * POST /api/templates/{id}/import
     */
    @PostMapping("/{id}/import")
    public Result<String> importTemplate(
            @PathVariable String id,
            @Valid @RequestBody TemplateImportDTO dto,
            @RequestHeader("X-User-Id") String userId) {
        String instanceId = templateService.importToWorkspace(id, dto, userId);
        return Result.ok(instanceId, "导入成功");
    }
    
    /**
     * 获取课程已导入的模板
     * GET /api/templates/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    public Result<List<TemplateListDTO>> getCourseTemplates(
            @PathVariable String courseId) {
        List<TemplateListDTO> templates = templateService.getCourseTemplates(courseId);
        return Result.ok(templates);
    }
}

---

## 模块C：成本与配额管理

### C-1. 配额表设计 [优先级: P3]

**数据库设计** (`schema.sql`添加):
```sql
-- 配额设置表（课程级别）
CREATE TABLE IF NOT EXISTS quota_settings (
    id VARCHAR(36) PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL UNIQUE,
    token_limit_daily INT DEFAULT 10000,      -- 每日Token上限/人
    token_limit_weekly INT DEFAULT 50000,     -- 每周Token上限/人
    token_limit_total INT DEFAULT 0,          -- 总Token上限(0=无限)
    reset_day ENUM('monday','sunday') DEFAULT 'monday', -- 每周重置日
    warning_threshold INT DEFAULT 80,         -- 警告阈值百分比
    is_enabled BOOLEAN DEFAULT TRUE,          -- 是否启用配额限制
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 用户配额使用记录表（按日期统计）
CREATE TABLE IF NOT EXISTS quota_usage (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    course_id VARCHAR(36) NOT NULL,
    date DATE NOT NULL,
    tokens_used INT DEFAULT 0,
    request_count INT DEFAULT 0,
    UNIQUE KEY uk_user_course_date (user_id, course_id, date),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_usage_date (date),
    INDEX idx_usage_user (user_id)
);

-- 配额使用日志表（详细记录）
CREATE TABLE IF NOT EXISTS quota_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    course_id VARCHAR(36) NOT NULL,
    template_id VARCHAR(36),
    input_tokens INT DEFAULT 0,
    output_tokens INT DEFAULT 0,
    total_tokens INT DEFAULT 0,
    model_name VARCHAR(100),
    executed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    INDEX idx_log_time (executed_at)
);
```

---

### C-2. 实体类定义 [优先级: P3]

**QuotaSetting实体** (`entity/QuotaSetting.java`):
```java
package com.eduai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("quota_settings")
public class QuotaSetting {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    private String courseId;
    private Integer tokenLimitDaily;
    private Integer tokenLimitWeekly;
    private Integer tokenLimitTotal;
    private String resetDay;
    private Integer warningThreshold;
    private Boolean isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**QuotaUsage实体** (`entity/QuotaUsage.java`):
```java
package com.eduai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("quota_usage")
public class QuotaUsage {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    private String userId;
    private String courseId;
    private LocalDate date;
    private Integer tokensUsed;
    private Integer requestCount;
}
```

---

### C-3. DTO定义 [优先级: P3]

```java
// dto/QuotaSettingDTO.java
package com.eduai.dto;

import lombok.Data;
import javax.validation.constraints.Min;

@Data
public class QuotaSettingDTO {
    @Min(value = 0, message = "每日限额不能为负")
    private Integer tokenLimitDaily;
    
    @Min(value = 0, message = "每周限额不能为负")
    private Integer tokenLimitWeekly;
    
    @Min(value = 0, message = "总限额不能为负")
    private Integer tokenLimitTotal;
    
    private String resetDay;  // monday / sunday
    private Integer warningThreshold;
    private Boolean isEnabled;
}

// dto/QuotaStatusDTO.java
package com.eduai.dto;

import lombok.Data;

@Data
public class QuotaStatusDTO {
    private String courseId;
    private String courseName;
    
    // 配额设置
    private Integer dailyLimit;
    private Integer weeklyLimit;
    private Integer totalLimit;
    
    // 当前使用量
    private Integer todayUsed;
    private Integer weekUsed;
    private Integer totalUsed;
    
    // 百分比
    private Double dailyPercent;
    private Double weeklyPercent;
    private Double totalPercent;
    
    // 剩余量
    private Integer dailyRemaining;
    private Integer weeklyRemaining;
    
    // 状态
    private String status;  // normal / warning / exceeded
    private Boolean isEnabled;
}

// dto/UsageStatDTO.java
package com.eduai.dto;

import lombok.Data;
import java.util.List;

@Data
public class UsageStatDTO {
    private String userId;
    private String userName;
    private String studentId;
    private Integer todayUsed;
    private Integer weekUsed;
    private Integer totalUsed;
    private Integer requestCount;
    private String lastUsedAt;
}

// dto/CourseUsageOverviewDTO.java
package com.eduai.dto;

import lombok.Data;
import java.util.List;

@Data
public class CourseUsageOverviewDTO {
    private String courseId;
    private String courseName;
    private Integer totalMembers;
    private Integer activeMembers;  // 本周使用过的成员数
    private Integer courseTotalUsed;
    private List<UsageStatDTO> memberUsages;
}
```

---

### C-4. Mapper接口 [优先级: P3]

```java
// mapper/QuotaSettingMapper.java
package com.eduai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduai.entity.QuotaSetting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuotaSettingMapper extends BaseMapper<QuotaSetting> {
}

// mapper/QuotaUsageMapper.java
package com.eduai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eduai.entity.QuotaUsage;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface QuotaUsageMapper extends BaseMapper<QuotaUsage> {
    
    /**
     * 获取用户在指定日期范围内的总使用量
     */
    @Select("SELECT COALESCE(SUM(tokens_used), 0) FROM quota_usage " +
            "WHERE user_id = #{userId} AND course_id = #{courseId} " +
            "AND date BETWEEN #{startDate} AND #{endDate}")
    Integer sumTokensInRange(@Param("userId") String userId,
                             @Param("courseId") String courseId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);
    
    /**
     * 获取课程所有成员的使用统计
     */
    @Select("SELECT u.id as userId, u.name as userName, u.student_id as studentId, " +
            "COALESCE(SUM(q.tokens_used), 0) as totalUsed, " +
            "COALESCE(SUM(q.request_count), 0) as requestCount " +
            "FROM course_members cm " +
            "JOIN users u ON cm.user_id = u.id " +
            "LEFT JOIN quota_usage q ON q.user_id = u.id AND q.course_id = cm.course_id " +
            "WHERE cm.course_id = #{courseId} " +
            "GROUP BY u.id")
    List<UsageStatDTO> getCourseUsageStats(@Param("courseId") String courseId);
    
    /**
     * 增加用户当日使用量
     */
    @Update("INSERT INTO quota_usage (id, user_id, course_id, date, tokens_used, request_count) " +
            "VALUES (UUID(), #{userId}, #{courseId}, CURDATE(), #{tokens}, 1) " +
            "ON DUPLICATE KEY UPDATE tokens_used = tokens_used + #{tokens}, request_count = request_count + 1")
    void incrementUsage(@Param("userId") String userId,
                        @Param("courseId") String courseId,
                        @Param("tokens") Integer tokens);
}
```

---

### C-5. Service层 [优先级: P3]

**QuotaService接口** (`service/QuotaService.java`):
```java
package com.eduai.service;

import com.eduai.dto.*;
import java.util.List;

public interface QuotaService {
    
    /**
     * 获取课程配额设置
     */
    QuotaSetting getQuotaSetting(String courseId);
    
    /**
     * 更新课程配额设置（仅教师）
     */
    void updateQuotaSetting(String courseId, QuotaSettingDTO dto, String userId);
    
    /**
     * 获取用户在课程中的配额状态
     */
    QuotaStatusDTO getUserQuotaStatus(String courseId, String userId);
    
    /**
     * 获取课程使用概览（教师）
     */
    CourseUsageOverviewDTO getCourseUsageOverview(String courseId, String userId);
    
    /**
     * 检查用户是否可以执行（配额是否足够）
     * @return null表示可以执行，否则返回错误信息
     */
    String checkQuota(String courseId, String userId, Integer estimatedTokens);
    
    /**
     * 记录使用量
     */
    void recordUsage(String courseId, String userId, Integer tokens);
}
```

**QuotaService实现** (`service/impl/QuotaServiceImpl.java`):
```java
package com.eduai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eduai.dto.*;
import com.eduai.entity.*;
import com.eduai.mapper.*;
import com.eduai.service.QuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuotaServiceImpl implements QuotaService {
    
    private final QuotaSettingMapper settingMapper;
    private final QuotaUsageMapper usageMapper;
    private final CourseMemberMapper memberMapper;
    private final CourseMapper courseMapper;
    
    @Override
    public QuotaSetting getQuotaSetting(String courseId) {
        QuotaSetting setting = settingMapper.selectOne(
            new LambdaQueryWrapper<QuotaSetting>()
                .eq(QuotaSetting::getCourseId, courseId)
        );
        
        // 如果没有设置，返回默认值
        if (setting == null) {
            setting = new QuotaSetting();
            setting.setCourseId(courseId);
            setting.setTokenLimitDaily(10000);
            setting.setTokenLimitWeekly(50000);
            setting.setTokenLimitTotal(0);
            setting.setWarningThreshold(80);
            setting.setIsEnabled(true);
        }
        
        return setting;
    }
    
    @Override
    @Transactional
    public void updateQuotaSetting(String courseId, QuotaSettingDTO dto, String userId) {
        // 检查教师权限
        checkTeacherPermission(courseId, userId);
        
        QuotaSetting setting = settingMapper.selectOne(
            new LambdaQueryWrapper<QuotaSetting>()
                .eq(QuotaSetting::getCourseId, courseId)
        );
        
        if (setting == null) {
            setting = new QuotaSetting();
            setting.setId(UUID.randomUUID().toString());
            setting.setCourseId(courseId);
            BeanUtils.copyProperties(dto, setting);
            settingMapper.insert(setting);
        } else {
            BeanUtils.copyProperties(dto, setting);
            settingMapper.updateById(setting);
        }
    }
    
    @Override
    public QuotaStatusDTO getUserQuotaStatus(String courseId, String userId) {
        QuotaSetting setting = getQuotaSetting(courseId);
        Course course = courseMapper.selectById(courseId);
        
        LocalDate today = LocalDate.now();
        LocalDate weekStart = getWeekStart(today, setting.getResetDay());
        
        // 获取使用量
        Integer todayUsed = usageMapper.sumTokensInRange(userId, courseId, today, today);
        Integer weekUsed = usageMapper.sumTokensInRange(userId, courseId, weekStart, today);
        Integer totalUsed = usageMapper.sumTokensInRange(userId, courseId, 
            LocalDate.of(2020, 1, 1), today);
        
        QuotaStatusDTO status = new QuotaStatusDTO();
        status.setCourseId(courseId);
        status.setCourseName(course != null ? course.getName() : "");
        status.setDailyLimit(setting.getTokenLimitDaily());
        status.setWeeklyLimit(setting.getTokenLimitWeekly());
        status.setTotalLimit(setting.getTokenLimitTotal());
        status.setTodayUsed(todayUsed);
        status.setWeekUsed(weekUsed);
        status.setTotalUsed(totalUsed);
        status.setIsEnabled(setting.getIsEnabled());
        
        // 计算百分比
        status.setDailyPercent(calculatePercent(todayUsed, setting.getTokenLimitDaily()));
        status.setWeeklyPercent(calculatePercent(weekUsed, setting.getTokenLimitWeekly()));
        status.setTotalPercent(calculatePercent(totalUsed, setting.getTokenLimitTotal()));
        
        // 计算剩余
        status.setDailyRemaining(Math.max(0, setting.getTokenLimitDaily() - todayUsed));
        status.setWeeklyRemaining(Math.max(0, setting.getTokenLimitWeekly() - weekUsed));
        
        // 判断状态
        status.setStatus(determineStatus(status, setting.getWarningThreshold()));
        
        return status;
    }
    
    @Override
    public CourseUsageOverviewDTO getCourseUsageOverview(String courseId, String userId) {
        checkTeacherPermission(courseId, userId);
        
        Course course = courseMapper.selectById(courseId);
        List<UsageStatDTO> stats = usageMapper.getCourseUsageStats(courseId);
        
        CourseUsageOverviewDTO overview = new CourseUsageOverviewDTO();
        overview.setCourseId(courseId);
        overview.setCourseName(course != null ? course.getName() : "");
        overview.setTotalMembers(stats.size());
        overview.setActiveMembers((int) stats.stream()
            .filter(s -> s.getTotalUsed() > 0).count());
        overview.setCourseTotalUsed(stats.stream()
            .mapToInt(UsageStatDTO::getTotalUsed).sum());
        overview.setMemberUsages(stats);
        
        return overview;
    }
    
    @Override
    public String checkQuota(String courseId, String userId, Integer estimatedTokens) {
        QuotaSetting setting = getQuotaSetting(courseId);
        
        if (!setting.getIsEnabled()) {
            return null; // 配额未启用，允许执行
        }
        
        QuotaStatusDTO status = getUserQuotaStatus(courseId, userId);
        
        // 检查每日配额
        if (status.getTodayUsed() + estimatedTokens > setting.getTokenLimitDaily()) {
            return "今日配额已用尽，剩余 " + status.getDailyRemaining() + " tokens";
        }
        
        // 检查每周配额
        if (status.getWeekUsed() + estimatedTokens > setting.getTokenLimitWeekly()) {
            return "本周配额已用尽，剩余 " + status.getWeeklyRemaining() + " tokens";
        }
        
        // 检查总配额
        if (setting.getTokenLimitTotal() > 0 && 
            status.getTotalUsed() + estimatedTokens > setting.getTokenLimitTotal()) {
            return "总配额已用尽";
        }
        
        return null;
    }
    
    @Override
    @Transactional
    public void recordUsage(String courseId, String userId, Integer tokens) {
        usageMapper.incrementUsage(userId, courseId, tokens);
    }
    
    // 辅助方法
    private void checkTeacherPermission(String courseId, String userId) {
        CourseMember member = memberMapper.selectOne(
            new LambdaQueryWrapper<CourseMember>()
                .eq(CourseMember::getCourseId, courseId)
                .eq(CourseMember::getUserId, userId)
        );
        if (member == null || !"teacher".equals(member.getRole())) {
            throw new RuntimeException("需要教师权限");
        }
    }
    
    private LocalDate getWeekStart(LocalDate date, String resetDay) {
        DayOfWeek dow = "sunday".equalsIgnoreCase(resetDay) 
            ? DayOfWeek.SUNDAY : DayOfWeek.MONDAY;
        return date.with(TemporalAdjusters.previousOrSame(dow));
    }
    
    private Double calculatePercent(Integer used, Integer limit) {
        if (limit == null || limit == 0) return 0.0;
        return Math.min(100.0, (used * 100.0) / limit);
    }
    
    private String determineStatus(QuotaStatusDTO status, Integer threshold) {
        if (status.getDailyPercent() >= 100 || status.getWeeklyPercent() >= 100) {
            return "exceeded";
        }
        if (status.getDailyPercent() >= threshold || status.getWeeklyPercent() >= threshold) {
            return "warning";
        }
        return "normal";
    }
}
```

---

### C-6. Controller层 [优先级: P3]

```java
// controller/QuotaController.java
package com.eduai.controller;

import com.eduai.common.Result;
import com.eduai.dto.*;
import com.eduai.entity.QuotaSetting;
import com.eduai.service.QuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/quota")
@RequiredArgsConstructor
public class QuotaController {
    
    private final QuotaService quotaService;
    
    /**
     * 获取课程配额设置
     * GET /api/quota/course/{courseId}/setting
     */
    @GetMapping("/course/{courseId}/setting")
    public Result<QuotaSetting> getQuotaSetting(@PathVariable String courseId) {
        return Result.ok(quotaService.getQuotaSetting(courseId));
    }
    
    /**
     * 更新课程配额设置（仅教师）
     * PUT /api/quota/course/{courseId}/setting
     */
    @PutMapping("/course/{courseId}/setting")
    public Result<Void> updateQuotaSetting(
            @PathVariable String courseId,
            @Valid @RequestBody QuotaSettingDTO dto,
            @RequestHeader("X-User-Id") String userId) {
        quotaService.updateQuotaSetting(courseId, dto, userId);
        return Result.ok(null, "配额设置已更新");
    }
    
    /**
     * 获取用户配额状态
     * GET /api/quota/course/{courseId}/status
     */
    @GetMapping("/course/{courseId}/status")
    public Result<QuotaStatusDTO> getUserQuotaStatus(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId) {
        return Result.ok(quotaService.getUserQuotaStatus(courseId, userId));
    }
    
    /**
     * 获取课程使用概览（教师）
     * GET /api/quota/course/{courseId}/overview
     */
    @GetMapping("/course/{courseId}/overview")
    public Result<CourseUsageOverviewDTO> getCourseUsageOverview(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId) {
        return Result.ok(quotaService.getCourseUsageOverview(courseId, userId));
    }
    
    /**
     * 检查配额是否足够
     * GET /api/quota/course/{courseId}/check?tokens=1000
     */
    @GetMapping("/course/{courseId}/check")
    public Result<Object> checkQuota(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "1000") Integer tokens,
            @RequestHeader("X-User-Id") String userId) {
        String error = quotaService.checkQuota(courseId, userId, tokens);
        if (error != null) {
            return Result.fail(error);
        }
        return Result.ok(null, "配额充足");
    }
}
```

---

## 模块D：知识库管理

### D-1. 知识库表设计 [优先级: P3]

```sql
-- 知识库表
CREATE TABLE IF NOT EXISTS knowledge_bases (
    id VARCHAR(36) PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    document_count INT DEFAULT 0,
    chunk_count INT DEFAULT 0,
    status ENUM('active', 'processing', 'error') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 知识文档表
CREATE TABLE IF NOT EXISTS knowledge_documents (
    id VARCHAR(36) PRIMARY KEY,
    knowledge_base_id VARCHAR(36) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    chunk_count INT DEFAULT 0,
    status ENUM('pending', 'processing', 'completed', 'error') DEFAULT 'pending',
    error_message TEXT,
    uploaded_by VARCHAR(36) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (knowledge_base_id) REFERENCES knowledge_bases(id) ON DELETE CASCADE
);

-- 知识向量表（与向量数据库配合使用）
CREATE TABLE IF NOT EXISTS knowledge_embeddings (
    id VARCHAR(36) PRIMARY KEY,
    document_id VARCHAR(36) NOT NULL,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    metadata JSON,
    vector_id VARCHAR(100),        -- 向量数据库中的ID
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (document_id) REFERENCES knowledge_documents(id) ON DELETE CASCADE
);
```

---

# API响应格式规范

## 统一响应结构
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private boolean success;
    private T data;
    private String error;
    private String message;
    
    public static <T> Result<T> ok(T data) {
        return new Result<>(true, data, null, null);
    }
    
    public static <T> Result<T> ok(T data, String message) {
        return new Result<>(true, data, null, message);
    }
    
    public static <T> Result<T> fail(String error) {
        return new Result<>(false, null, error, null);
    }
}
```

## 错误处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.fail(e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail("服务器内部错误");
    }
}
```

---

# 开发注意事项

1. **所有API都需要X-User-Id头**（认证拦截器已配置）
2. **数据库字段命名使用下划线**，Java使用驼峰
3. **返回给前端时清除敏感数据**（如password）
4. **使用@Transactional保证事务一致性**
5. **生产环境密码使用BCrypt加密**
