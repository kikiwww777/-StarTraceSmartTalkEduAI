# 成员C任务书 - 测试与文档

## 📋 职责范围
- 功能测试与Bug报告
- API接口文档维护
- 部署脚本与环境配置
- 用户手册编写

## 🛠️ 工具
- **API测试**: Postman / curl
- **浏览器测试**: Chrome DevTools
- **文档**: Markdown
- **自动化测试**: Jest (前端) / JUnit (后端)

## 📂 工作目录
```
docs/
├── api/              # API文档
├── test/             # 测试用例
├── deploy/           # 部署文档
└── user-guide/       # 用户手册
```

---

# Phase 1 测试任务清单

## 模块A：课程与权限管理

### A-TEST-1. 注册流程测试 [优先级: P0]

**测试用例**:

| 编号 | 场景 | 输入 | 预期结果 |
|------|------|------|----------|
| A1-01 | 学生正常注册 | 学号+姓名+密码+角色=student | 注册成功，跳转登录页 |
| A1-02 | 教师正常注册 | 工号+姓名+密码+角色=teacher | 注册成功，跳转登录页 |
| A1-03 | 学号已存在 | 已注册的学号 | 提示"该学号/工号已被注册" |
| A1-04 | 密码过短 | 密码<6位 | 提示"密码至少6位" |
| A1-05 | 密码不一致 | 两次密码不同 | 提示"两次输入的密码不一致" |
| A1-06 | 必填字段为空 | 学号为空 | 提示"学号/工号不能为空" |

**API测试**:
```bash
# 正常注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"studentId":"test001","name":"测试用户","password":"123456","role":"student"}'

# 预期响应
{"success":true,"data":{"id":"xxx","studentId":"test001","name":"测试用户","role":"student"},"message":"注册成功"}
```

---

### A-TEST-2. 登录流程测试 [优先级: P0]

**测试用例**:

| 编号 | 场景 | 输入 | 预期结果 |
|------|------|------|----------|
| A2-01 | 正常登录 | 正确的学号+密码 | 登录成功，返回token和用户信息 |
| A2-02 | 用户不存在 | 未注册的学号 | 提示"用户不存在" |
| A2-03 | 密码错误 | 错误的密码 | 提示"密码错误" |
| A2-04 | 登录后刷新 | 刷新页面 | 保持登录状态 |
| A2-05 | 登出 | 点击登出 | 清除登录状态，跳转登录页 |

**API测试**:
```bash
# 正常登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"studentId":"test001","password":"123456"}'

# 预期响应
{"success":true,"data":{"token":"xxx.xxx.xxx","user":{"id":"xxx","studentId":"test001","name":"测试用户","role":"student"}},"message":"登录成功"}
```

---

### A-TEST-3. 课程创建测试 [优先级: P0]

**前置条件**: 以教师身份登录

**测试用例**:

| 编号 | 场景 | 输入 | 预期结果 |
|------|------|------|----------|
| A3-01 | 正常创建 | 课程名+描述 | 创建成功，显示课程ID和邀请码 |
| A3-02 | 仅课程名 | 课程名（无描述） | 创建成功 |
| A3-03 | 名称为空 | 空课程名 | 提示"课程名称不能为空" |
| A3-04 | 学生创建 | 学生身份尝试创建 | 不显示创建按钮/创建失败 |

**API测试**:
```bash
# 创建课程
curl -X POST http://localhost:8080/api/course \
  -H "Content-Type: application/json" \
  -H "X-User-Id: teacher_user_id" \
  -d '{"name":"人工智能导论","description":"2024年春季学期"}'

# 预期响应
{"success":true,"data":{"id":"xxx","name":"人工智能导论","description":"2024年春季学期","instructorId":"xxx","defaultInviteCode":"ABC12345"},"message":"创建成功"}
```

---

### A-TEST-4. 加入课程测试 [优先级: P0]

**前置条件**: 已有课程存在

**测试用例**:

| 编号 | 场景 | 输入 | 预期结果 |
|------|------|------|----------|
| A4-01 | 正确邀请码 | 有效的8位邀请码 | 加入成功，课程列表显示该课程 |
| A4-02 | 错误邀请码 | 无效的邀请码 | 提示"邀请码无效或已过期" |
| A4-03 | 重复加入 | 已加入的课程邀请码 | 提示"您已加入该课程" |
| A4-04 | 大小写不敏感 | 小写邀请码 | 加入成功 |

**API测试**:
```bash
# 通过邀请码加入
curl -X POST http://localhost:8080/api/course/join-by-code \
  -H "Content-Type: application/json" \
  -H "X-User-Id: student_user_id" \
  -d '{"inviteCode":"ABC12345"}'

# 预期响应
{"success":true,"message":"加入成功"}
```

---

### A-TEST-5. 课程详情与成员管理测试 [优先级: P0]

**测试用例**:

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| A5-01 | 查看课程详情 | 进入课程详情页 | 显示课程信息、成员列表 |
| A5-02 | 教师查看邀请码 | 教师查看详情 | 显示邀请码和复制按钮 |
| A5-03 | 学生不见邀请码 | 学生查看详情 | 不显示邀请码 |
| A5-04 | 教师移除成员 | 点击移除按钮 | 成员被移除 |
| A5-05 | 学生退出课程 | 点击退出按钮 | 退出成功，返回课程列表 |
| A5-06 | 创建者无法退出 | 创建者尝试退出 | 提示无法退出 |

---

### A-TEST-6. 权限控制测试 [优先级: P1]

**测试用例**:

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| A6-01 | 无Token访问API | 不带Authorization头 | 401 Unauthorized |
| A6-02 | 无X-User-Id访问 | 不带X-User-Id头 | 400 Bad Request |
| A6-03 | 访问无权课程 | 访问未加入的课程 | 403或提示无权访问 |
| A6-04 | 学生修改课程 | 学生调用PUT /api/course/{id} | 提示无权限 |

---

## 模块B：模板库测试

### B-TEST-1. 模板列表测试 [优先级: P2]

**前置条件**: 用户已登录

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| B1-01 | 查看全部模板 | 进入模板库页面 | 显示11个预设模板卡片 |
| B1-02 | 按分类筛选-辅导 | 点击"辅导"分类 | 只显示辅导相关模板 |
| B1-03 | 按分类筛选-评分 | 点击"评分"分类 | 只显示评分相关模板 |
| B1-04 | 按分类筛选-写作 | 点击"写作"分类 | 只显示写作相关模板 |
| B1-05 | 搜索模板 | 搜索"代码" | 显示代码调试助手 |
| B1-06 | 搜索无结果 | 搜索"不存在的模板" | 显示空状态提示 |
| B1-07 | 清除筛选 | 点击"全部" | 显示所有模板 |

**API测试**:
```bash
# 获取全部模板
curl -X GET http://localhost:8080/api/templates \
  -H "X-User-Id: user_id"

# 预期响应
{
  "success": true,
  "data": [
    {
      "id": "tpl-tutor-001",
      "name": "智能辅导助手",
      "category": "tutor",
      "description": "根据学生问题提供个性化辅导...",
      "difficulty": "beginner",
      "estimatedCost": "~1000 tokens/次",
      "tags": ["辅导", "问答", "个性化"],
      "usageCount": 128,
      "isSystem": true
    },
    ...
  ]
}

# 按分类筛选
curl -X GET "http://localhost:8080/api/templates?category=tutor" \
  -H "X-User-Id: user_id"
```

---

### B-TEST-2. 模板详情测试 [优先级: P2]

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| B2-01 | 查看模板详情 | 点击模板卡片 | 进入详情页，显示完整信息 |
| B2-02 | 显示系统提示词 | 查看详情页 | 显示systemPrompt内容 |
| B2-03 | 显示示例对话 | 查看详情页 | 显示示例输入和输出 |
| B2-04 | 显示使用说明 | 查看详情页 | 显示documentation |
| B2-05 | 返回列表 | 点击返回按钮 | 返回模板列表页 |

**API测试**:
```bash
# 获取模板详情
curl -X GET http://localhost:8080/api/templates/tpl-tutor-001 \
  -H "X-User-Id: user_id"

# 预期响应
{
  "success": true,
  "data": {
    "id": "tpl-tutor-001",
    "name": "智能辅导助手",
    "category": "tutor",
    "description": "...",
    "difficulty": "beginner",
    "estimatedCost": "~1000 tokens/次",
    "systemPrompt": "你是一个专业的教学辅导助手...",
    "exampleInput": "什么是递归？",
    "exampleOutput": "递归是一种函数调用自身...",
    "documentation": "1. 导入模板到课程工作区...",
    "tags": ["辅导", "问答"],
    "usageCount": 128
  }
}
```

---

### B-TEST-3. 模板导入测试 [优先级: P2]

**前置条件**: 以教师身份登录，已有课程

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| B3-01 | 导入模板 | 选择课程后点击导入 | 导入成功提示 |
| B3-02 | 重复导入 | 再次导入同一模板 | 提示"该模板已导入此课程" |
| B3-03 | 学生导入 | 学生尝试导入 | 没有可选课程/导入失败 |
| B3-04 | 无课程导入 | 新教师无课程 | 提示先创建课程 |
| B3-05 | 查看课程模板 | 进入课程工作区 | 显示已导入的模板 |

**API测试**:
```bash
# 导入模板到课程
curl -X POST http://localhost:8080/api/templates/tpl-tutor-001/import \
  -H "Content-Type: application/json" \
  -H "X-User-Id: teacher_id" \
  -d '{"courseId": "course_uuid"}'

# 预期响应
{
  "success": true,
  "data": "instance_uuid",
  "message": "导入成功"
}

# 获取课程已导入模板
curl -X GET http://localhost:8080/api/templates/course/course_uuid \
  -H "X-User-Id: user_id"
```

---

## 模块C：配额管理测试

### C-TEST-1. 配额设置测试 [优先级: P3]

**前置条件**: 以教师身份登录，已有课程

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| C1-01 | 查看默认配额 | 进入配额设置 | 显示默认值(日10000,周50000) |
| C1-02 | 修改每日限额 | 设置为5000 | 保存成功 |
| C1-03 | 修改每周限额 | 设置为20000 | 保存成功 |
| C1-04 | 设置无限配额 | 总限额设为0 | 保存成功,显示"无限" |
| C1-05 | 禁用配额 | 关闭配额开关 | 保存成功,不再限制 |
| C1-06 | 设置警告阈值 | 设置为90% | 保存成功 |
| C1-07 | 学生无法设置 | 学生访问设置页 | 无法访问或提示无权限 |

**API测试**:
```bash
# 获取配额设置
curl -X GET http://localhost:8080/api/quota/course/course_uuid/setting \
  -H "X-User-Id: teacher_id"

# 预期响应
{
  "success": true,
  "data": {
    "id": "setting_uuid",
    "courseId": "course_uuid",
    "tokenLimitDaily": 10000,
    "tokenLimitWeekly": 50000,
    "tokenLimitTotal": 0,
    "resetDay": "monday",
    "warningThreshold": 80,
    "isEnabled": true
  }
}

# 更新配额设置
curl -X PUT http://localhost:8080/api/quota/course/course_uuid/setting \
  -H "Content-Type: application/json" \
  -H "X-User-Id: teacher_id" \
  -d '{
    "tokenLimitDaily": 5000,
    "tokenLimitWeekly": 20000,
    "tokenLimitTotal": 0,
    "warningThreshold": 90,
    "isEnabled": true
  }'

# 预期响应
{
  "success": true,
  "message": "配额设置已更新"
}
```

---

### C-TEST-2. 配额状态查看测试 [优先级: P3]

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| C2-01 | 查看个人配额状态 | 进入工作区 | 显示今日/本周使用量和剩余 |
| C2-02 | 配额正常状态 | 使用量<80% | 状态为"normal",绿色 |
| C2-03 | 配额警告状态 | 使用量>=80% | 状态为"warning",黄色 |
| C2-04 | 配额超限状态 | 使用量>=100% | 状态为"exceeded",红色 |
| C2-05 | 显示进度条 | 查看配额组件 | 正确显示百分比进度条 |
| C2-06 | 显示剩余Token | 查看配额组件 | 正确计算剩余量 |

**API测试**:
```bash
# 获取用户配额状态
curl -X GET http://localhost:8080/api/quota/course/course_uuid/status \
  -H "X-User-Id: user_id"

# 预期响应
{
  "success": true,
  "data": {
    "courseId": "course_uuid",
    "courseName": "人工智能导论",
    "dailyLimit": 10000,
    "weeklyLimit": 50000,
    "totalLimit": 0,
    "todayUsed": 3500,
    "weekUsed": 15000,
    "totalUsed": 45000,
    "dailyPercent": 35.0,
    "weeklyPercent": 30.0,
    "totalPercent": 0.0,
    "dailyRemaining": 6500,
    "weeklyRemaining": 35000,
    "status": "normal",
    "isEnabled": true
  }
}
```

---

### C-TEST-3. 配额检查测试 [优先级: P3]

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| C3-01 | 配额充足 | 检查1000tokens | 返回成功 |
| C3-02 | 每日配额不足 | 超出日配额 | 返回错误"今日配额已用尽" |
| C3-03 | 每周配额不足 | 超出周配额 | 返回错误"本周配额已用尽" |
| C3-04 | 配额禁用 | 关闭配额检查 | 返回成功 |

**API测试**:
```bash
# 检查配额是否足够
curl -X GET "http://localhost:8080/api/quota/course/course_uuid/check?tokens=1000" \
  -H "X-User-Id: user_id"

# 预期响应(配额充足)
{
  "success": true,
  "message": "配额充足"
}

# 预期响应(配额不足)
{
  "success": false,
  "error": "今日配额已用尽，剩余 500 tokens"
}
```

---

### C-TEST-4. 使用统计测试（教师） [优先级: P3]

| 编号 | 场景 | 操作 | 预期结果 |
|------|------|------|----------|
| C4-01 | 查看课程使用概览 | 教师进入统计页 | 显示所有成员使用情况 |
| C4-02 | 显示成员列表 | 查看统计表格 | 按使用量排序 |
| C4-03 | 显示活跃成员数 | 查看统计 | 正确统计本周使用人数 |
| C4-04 | 显示课程总消耗 | 查看统计 | 正确累计所有成员消耗 |
| C4-05 | 学生无法访问 | 学生访问概览 | 提示无权限 |

**API测试**:
```bash
# 获取课程使用概览
curl -X GET http://localhost:8080/api/quota/course/course_uuid/overview \
  -H "X-User-Id: teacher_id"

# 预期响应
{
  "success": true,
  "data": {
    "courseId": "course_uuid",
    "courseName": "人工智能导论",
    "totalMembers": 25,
    "activeMembers": 18,
    "courseTotalUsed": 350000,
    "memberUsages": [
      {
        "userId": "student_1",
        "userName": "张三",
        "studentId": "2024001",
        "totalUsed": 15000,
        "requestCount": 42
      },
      ...
    ]
  }
}
```

---

## 模块D：知识库测试

### D-TEST-1. 文档上传测试 [优先级: P3]

| 编号 | 场景 | 操作 | 预期结果 |

---

# 测试报告模板

## Bug报告格式

```markdown
## Bug #XXX: [简要描述]

**严重程度**: P0/P1/P2/P3
**发现时间**: YYYY-MM-DD
**发现人**: 成员C

### 复现步骤
1. ...
2. ...
3. ...

### 预期结果
...

### 实际结果
...

### 截图/日志
...

### 环境信息
- 浏览器: Chrome xx
- 后端版本: xxx
- 数据库: MySQL 8.0
```

---

# API接口文档

## 认证接口

### POST /api/auth/register
注册新用户

**请求体**:
```json
{
  "studentId": "string, 必填, 学号/工号",
  "name": "string, 必填, 姓名",
  "password": "string, 必填, 密码(>=6位)",
  "role": "string, 可选, teacher/student, 默认student"
}
```

**成功响应**:
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "studentId": "xxx",
    "name": "xxx",
    "role": "student"
  },
  "message": "注册成功"
}
```

### POST /api/auth/login
用户登录

**请求体**:
```json
{
  "studentId": "string, 必填",
  "password": "string, 必填"
}
```

**成功响应**:
```json
{
  "success": true,
  "data": {
    "token": "jwt_token",
    "user": {
      "id": "uuid",
      "studentId": "xxx",
      "name": "xxx",
      "role": "teacher/student"
    }
  }
}
```

---

## 课程接口

### GET /api/course
获取用户课程列表

**请求头**: `X-User-Id: uuid`

**响应**:
```json
{
  "success": true,
  "data": {
    "total": 2,
    "teaching": [
      {
        "id": "uuid",
        "name": "课程名",
        "description": "描述",
        "role": "teacher",
        "memberCount": 10
      }
    ],
    "learning": [...]
  }
}
```

### POST /api/course
创建课程（需教师权限）

**请求头**: `X-User-Id: uuid`

**请求体**:
```json
{
  "name": "string, 必填",
  "description": "string, 可选"
}
```

### GET /api/course/{id}
获取课程详情

### POST /api/course/join-by-code
通过邀请码加入课程

**请求体**:
```json
{
  "inviteCode": "string, 8位邀请码"
}
```

### POST /api/course/{id}/leave
退出课程

### GET /api/course/{id}/members
获取成员列表

### DELETE /api/course/{id}/members/{memberId}
移除成员（需教师权限）

---

# 部署文档

## 开发环境配置

### 前端
```bash
cd frontend
npm install
npm run dev
# 访问 http://localhost:5173
```

### 后端
```bash
cd backend
# 配置 application.yml 中的数据库连接
mvn spring-boot:run
# 访问 http://localhost:8080
```

### 数据库
```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE eduai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行初始化脚本
source backend/src/main/resources/schema.sql
```

---

## 测试账号

| 角色 | 学号 | 密码 | 说明 |
|------|------|------|------|
| 教师 | teacher001 | 123456 | 测试教师账号 |
| 学生 | student001 | 123456 | 测试学生账号 |
| 学生 | student002 | 123456 | 测试学生账号 |

---

# 每日测试检查清单

- [ ] 后端服务启动正常
- [ ] 前端服务启动正常
- [ ] 数据库连接正常
- [ ] 注册功能正常
- [ ] 登录功能正常
- [ ] 课程创建正常
- [ ] 加入课程正常
- [ ] 成员管理正常
