[![星溯智语 EduAI 项目演示](../assets/demo-preview.gif)](../assets/demo-github.mp4)

# 星溯智语 EduAI

星溯智语 EduAI 是一个面向高校教学场景的智能体教育平台，围绕课程建设、教学工作流、知识管理、实验实训和学习过程追踪，构建教师与学生协同使用的 AI 教学工作台。

项目由三部分组成：

- `frontend/`：Vue 3 + TypeScript 前端应用
- `backend/`：Spring Boot 后端服务
- `sim/`：Sim Studio 智能体工作流集成工程

> 仓库首页展示版 README 位于上一级目录：[../README.md](../README.md)

## 快速启动

```bash
cd docker
docker compose up -d
```

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

```bash
cd frontend
npm install
npm run dev
```

访问地址：

- 前端：http://localhost:5173
- 后端：http://localhost:8080
- API 文档：http://localhost:8080/swagger-ui.html

## 主要能力

- 课程、班级、教师、学生与课程资料管理
- 学习模板、模板分类与知识库沉淀
- 基于 Vue Flow 的教学智能体工作流搭建
- Sim Studio 工作流执行、Webhook 与日志同步
- 学生实验尝试、事件、产物与 token 使用统计

## 界面预览

| 控制面板 | 课程详情 |
| --- | --- |
| ![控制面板](../assets/screenshots/dashboard-student.png) | ![课程详情](../assets/screenshots/course-detail.png) |

更多截图请查看仓库根目录 README。

更多项目介绍、技术栈和配置说明请查看仓库根目录 README。
