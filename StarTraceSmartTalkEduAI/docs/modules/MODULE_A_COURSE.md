# 模块A：课程与权限管理（Java Vue MySQL 版）

> 🎯 本文档供 AI 编程工具阅读

## 技术栈

- **后端**：Java 17 + SpringBoot 3.2 + MyBatis-Plus
- **前端**：Vue 3.5.13 + Element Plus
- **数据库**：MySQL 8.0

---

## 功能范围

### 阶段1（当前）
- ✅ 课程 CRUD
- ✅ 成员管理（添加/移除）
- ✅ 角色：teacher / student

### 阶段2（后续）
- 课程级权限设置
- 邀请码功能
- 与 Sim 工作区集成

---

## 数据库表

### courses 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | UUID 主键 |
| name | VARCHAR(255) | 课程名称 |
| description | TEXT | 描述 |
| instructor_id | VARCHAR(36) | 创建者 |
| workspace_id | VARCHAR(36) | Sim 工作区 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### course_members 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | UUID 主键 |
| course_id | VARCHAR(36) | 课程 ID |
| user_id | VARCHAR(36) | 用户 ID |
| role | ENUM | teacher / student |
| joined_at | DATETIME | 加入时间 |

---

## API 路径

使用单数 `/api/course`：

- `POST /api/course` - 创建
- `GET /api/course` - 列表
- `GET /api/course/:id` - 详情
- `PUT /api/course/:id` - 更新
- `DELETE /api/course/:id` - 删除
- `GET /api/course/:id/members` - 成员列表
- `POST /api/course/:id/members` - 添加成员

---

## 后端代码结构

```
backend/src/main/java/com/eduai/
├── controller/
│   ├── CourseController.java
│   └── MemberController.java
├── service/
│   ├── CourseService.java
│   └── MemberService.java
├── service/impl/
│   ├── CourseServiceImpl.java
│   └── MemberServiceImpl.java
├── entity/
│   ├── Course.java
│   └── CourseMember.java
├── dto/
│   ├── CreateCourseDTO.java
│   ├── CourseListDTO.java
│   └── AddMemberDTO.java
└── mapper/
    ├── CourseMapper.java
    └── CourseMemberMapper.java
```

---

## 前端代码结构

```
frontend/src/
├── views/
│   ├── Home.vue
│   ├── Courses.vue
│   └── CourseDetail.vue
├── components/
│   ├── course/CourseSelector.vue
│   └── member/MemberList.vue
└── api/index.ts
```
