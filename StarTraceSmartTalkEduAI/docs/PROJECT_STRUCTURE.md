# 项目结构说明

## 📂 根目录

| 目录/文件 | 说明 |
|-----------|------|
| `backend/` | Spring Boot 后端项目源码 |
| `frontend/` | Vue 3 前端项目源码 |
| `docker/` | Docker 部署配置文件 (docker-compose, sql) |
| `docs/` | 项目文档 (设计文档, 任务书, API规范) |
| `templates/` | 预设教学模板库相关文件 |
| `scripts/` | 辅助脚本 (构建, 部署等) |
| `README.md` | 项目入口文档 |

## ☕ Backend (后端)

位于 `backend/src/main/java/com/eduai/`：

```
com.eduai
├── common             # 通用模块
│   ├── Result.java    # 统一响应结果
│   └── GlobalExceptionHandler.java # 全局异常处理
│
├── config             # 这是一个配置类目录
│   ├── MyBatisPlusConfig.java
│   └── WebConfig.java
│
├── controller         # 控制器层 (API Endpoints)
│   ├── AuthController.java
│   ├── CourseController.java
│   ├── TemplateController.java
│   └── ...
│
├── service            # 业务逻辑层
│   ├── impl/          # 服务实现
│   └── CourseService.java
│
├── mapper             # 数据持久层 (MyBatis)
│   └── CourseMapper.java
│
├── entity             # 数据库实体 (对应数据库表)
│   └── Course.java
│
└── dto                # 数据传输对象 (Request/Response)
    ├── LoginDTO.java
    └── CourseDTO.java
```

## 🖥️ Frontend (前端)

位于 `frontend/src/`：

```
src
├── api                # Axios 请求封装
│   ├── auth.ts
│   ├── course.ts
│   └── http.ts        # Axios 拦截器配置
│
├── assets             # 静态资源 (图片, CSS)
│
├── components         # 公共组件
│   ├── NavBar.vue     # 顶部导航
│   └── course/        # 课程相关组件
│
├── composables        # 组合式函数 (Hooks)
│   └── useAuth.ts     # 认证逻辑
│
├── router             # 路由配置
│   └── index.ts
│
├── stores             # Pinia 状态管理
│   └── user.ts
│
├── views              # 页面视图
│   ├── LoginView.vue
│   ├── HomeView.vue
│   ├── CoursesView.vue
│   └── TemplatesView.vue
│
├── App.vue            # 根组件
└── main.ts            # 入口文件
```
