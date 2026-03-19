# EduAI Studio - 项目文档

> 面向高等教育的AI智能体工作台

## 📋 项目概述

基于 [Sim Studio](https://simstudio.ai/) 构建的教育AI平台，提供课程管理、教学模板、成本控制和知识库功能。

---

## 🎯 Phase 1 模块总览

| 模块 | 功能 | 详细设计 | 状态 |
|------|------|----------|------|
| **A. 课程与权限管理** | 用户注册登录、课程CRUD、成员管理、**邀请码** | [成员A](members/MEMBER_A_FRONTEND.md) / [成员B](members/MEMBER_B_BACKEND.md) | 🔨 开发中 |
| **B. 教学模板库** | 11个预设模板、分类筛选、导入课程 | [MODULE_B_TEMPLATES.md](MODULE_B_TEMPLATES.md) | ⏳ 待开发 |
| **C. 成本与配额管理** | Token配额、使用统计、成本报告 | [MODULE_C_QUOTA.md](MODULE_C_QUOTA.md) | ⏳ 待开发 |
| **D. 知识库管理** | 文档上传、向量化、语义搜索 | [MODULE_D_KNOWLEDGE.md](MODULE_D_KNOWLEDGE.md) | ⏳ 待开发 |
| **E. 前端UI增强** | 工作区页面、通用组件、响应式布局 | [MODULE_E_UI.md](MODULE_E_UI.md) | ⏳ 待开发 |

---

## 👥 团队分工

| 成员 | 职责 | 任务书 |
|------|------|--------|
| **成员A** | 前端开发 (Vue 3 + TypeScript) | [MEMBER_A_FRONTEND.md](members/MEMBER_A_FRONTEND.md) |
| **成员B** | 后端开发 (Spring Boot + MySQL) | [MEMBER_B_BACKEND.md](members/MEMBER_B_BACKEND.md) |
| **成员C** | 测试 + 文档 + 部署 | [MEMBER_C_TEST.md](members/MEMBER_C_TEST.md) |

---

## 📂 文档结构

```
docs/
├── README.md                    # 本文件 - 项目总览
│
├── MODULE_B_TEMPLATES.md        # 模块B详细设计
├── MODULE_C_QUOTA.md            # 模块C详细设计
├── MODULE_D_KNOWLEDGE.md        # 模块D详细设计
├── MODULE_E_UI.md               # 模块E详细设计
│
└── members/                     # 成员任务书
    ├── MEMBER_A_FRONTEND.md     # 前端任务（含模块A详细）
    ├── MEMBER_B_BACKEND.md      # 后端任务（含模块A详细）
    └── MEMBER_C_TEST.md         # 测试任务
```

---

## 🛠️ 技术栈

### 前端
- Vue 3.5 + TypeScript + Vite 6
- Vue Router 4 + Axios
- 深色主题 + 响应式布局

### 后端
- Java 17 + Spring Boot 3.2
- MyBatis-Plus + MySQL 8.0
- JWT认证

---

## 📅 开发计划

### Phase 1.1（第1周）- 模块A完成
- [ ] 邀请码系统（后端）
- [ ] 课程详情页完善（前端）
- [ ] 成员管理功能
- [ ] 退出课程功能

### Phase 1.2（第2周）- 模块B+E
- [ ] 11个教学模板数据
- [ ] 模板库浏览页面
- [ ] 工作区页面框架
- [ ] 导航栏增强

### Phase 1.3（第3周）- 模块C+D
- [ ] 配额设置功能
- [ ] 使用统计仪表板
- [ ] 知识库创建和管理
- [ ] 文档上传和处理

---

## 🚀 快速开始

### 1. 启动后端
```bash
cd backend
# 配置 src/main/resources/application.yml
mvn spring-boot:run
```

### 2. 启动前端
```bash
cd frontend
npm install
npm run dev
```

### 3. 访问应用
- 前端: http://localhost:5173
- 后端API: http://localhost:8080/api

---

## 🔑 关键设计决策

| 决策 | 说明 |
|------|------|
| 邀请码加入课程 | 使用8位随机码，教师可查看/复制 |
| 学生可退出课程 | 学生主动退出，创建者不可退出 |
| 工作区暂不对接 | Phase 1显示占位页面 |
| Token配额 | 支持每日/每周/总量限制 |
| 知识库 | 支持PDF、DOCX、TXT等格式 |
