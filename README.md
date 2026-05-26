[![星溯智语 EduAI 项目演示](./assets/demo-preview.gif)](./assets/demo-github.mp4)

点击上方动图可播放完整项目介绍视频。

# 星溯智语 EduAI

星溯智语 EduAI 是一个面向高校教学场景的智能体教育平台，围绕课程建设、教学工作流、知识管理、实验实训和学习过程追踪，构建教师与学生协同使用的 AI 教学工作台。

项目将 Vue 3 前端、Spring Boot 后端与 Sim Studio 智能体工作流能力整合在一起，让教师可以创建课程、管理班级与资料、搭建教学智能体流程，并通过执行日志和统计数据观察智能体在教学中的运行效果。

## 功能亮点

- 课程与班级管理：支持课程、班级、学生、教师、课程分组和课程资料等基础教学对象管理。
- 教学智能体工作台：提供 Start、Agent 等节点式工作流编排能力，可拖拽搭建教学流程并导出 JSON。
- 模板与知识沉淀：支持学习模板、模板分类、知识库和课程资料复用，降低重复搭建成本。
- Sim Studio 集成：对接 Sim 的工作流、执行日志、Webhook、Internal API 和实验运行记录。
- 学习过程监控：记录学生实验尝试、事件、产物和 token 使用情况，为教学复盘提供依据。
- 前后端分离架构：前端使用 Vue 3 + Vite + Element Plus，后端使用 Spring Boot + MyBatis + MySQL。

## 界面预览

更多系统页面截图可放置在 `assets/screenshots/` 目录，并通过 Markdown 图片语法展示在这里。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 前端 | Vue 3, TypeScript, Vite, Element Plus, Pinia, Vue Router, Vue Flow |
| 后端 | Java 17, Spring Boot 3.2, MyBatis, MyBatis-Plus, PageHelper |
| 数据库与缓存 | MySQL 8, Redis, PostgreSQL for Sim logs |
| 智能体平台 | Sim Studio, Workflow, Webhook, Internal JWT |
| 部署辅助 | Docker Compose, Nginx |

## 项目结构

```text
.
├── assets/                         # GitHub 展示资源
│   └── demo.mp4                    # 项目介绍视频
├── uploads/                        # 本地上传文件目录
└── StarTraceSmartTalkEduAI/
    ├── backend/                    # Spring Boot 后端服务
    ├── frontend/                   # Vue 3 前端应用
    ├── sim/                        # Sim Studio 集成工程
    ├── docker/                     # MySQL / Redis 启动配置
    ├── database/                   # 数据库脚本
    └── docs/                       # 项目文档
```

## 快速启动

### 环境要求

- JDK 17+
- Node.js 18+
- Maven 3.6+
- MySQL 8.0+
- Redis 7+
- Git LFS

```bash
git lfs install
git lfs pull
```

### 启动数据库

```bash
cd StarTraceSmartTalkEduAI/docker
docker compose up -d
```

本地配置请以 `StarTraceSmartTalkEduAI/backend/src/main/resources/application.yml` 为准。如果使用 Docker Compose 中的默认账号，请同步调整后端 MySQL 数据库名、用户名和密码。

### 启动后端

```bash
cd StarTraceSmartTalkEduAI/backend
mvn clean install
mvn spring-boot:run
```

后端默认地址：

- API 服务：http://localhost:8080
- Knife4j / Swagger：http://localhost:8080/swagger-ui.html

### 启动前端

```bash
cd StarTraceSmartTalkEduAI/frontend
npm install
npm run dev
```

前端默认地址：http://localhost:5173

## 核心页面

- `/`：教学与智能体运行概览
- `/courses`：课程列表
- `/courses/:id`：课程详情
- `/class/:id`：班级详情
- `/templates`：学习模板中心
- `/templates/:id/workspace`：模板工作区
- `/edu/workbench`：教学工作台
- `/sim/workbench`：Sim 工作台
- `/knowledge`：知识管理
- `/reports`：运行报告
- `/settings`：系统设置

## 后端能力

- 用户登录注册与身份信息
- 教师、学生、班级管理
- 课程、课程分组、课程资料管理
- 学习模板与模板分类
- Sim 实验、执行记录、Webhook 日志同步
- 学生实验事件、产物、尝试记录与 token 使用统计

## Sim Studio 集成

项目中的 `StarTraceSmartTalkEduAI/sim` 目录保留了 Sim Studio 相关工程，并通过后端配置连接：

- `SIM_BASE_URL`
- `SIM_PUBLIC_URL`
- `SIM_INTERNAL_API_SECRET`
- `SIM_API_KEY`
- `SIM_DEFAULT_USER_ID`
- `SIM_DEFAULT_WORKSPACE_ID`

这些变量可通过环境变量覆盖，也可以在 `application.yml` 中调整默认值。

## 常见问题

**视频为什么没有显示？**  
请确认已经安装并拉取 Git LFS 文件：`git lfs pull`。如果 GitHub 未内联播放视频，可直接打开 [assets/demo.mp4](./assets/demo.mp4)。

**前端请求 404？**  
请确认后端运行在 `http://localhost:8080`，并检查 `frontend/vite.config.ts` 中 `/api` 代理配置。

**后端数据库连接失败？**  
请检查 `application.yml` 中的 MySQL 地址、数据库名、用户名和密码是否与本地或 Docker Compose 配置一致。

**Sim 日志同步失败？**  
请检查 `sim.postgres` 配置、Sim 服务地址以及 Internal API Secret 是否一致。

## License

本项目用于教学与课程实验场景。若需要开源发布，请在仓库中补充明确的 License 文件。
