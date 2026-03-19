# Sim Studio Backend API 文档

## 目录
- [用户相关API](#用户相关api)
- [课程相关API](#课程相关api)
- [课程组相关API](#课程组相关api)
- [班级相关API](#班级相关api)
- [学生相关API](#学生相关api)
- [教师相关API](#教师相关api)
- [课程材料相关API](#课程材料相关api)

---

## 用户相关API

### 1. 用户注册
**接口地址：** `POST /api/user/register`

**请求参数：**
```json
{
  "studentId": "S2021001",      // 学号/工号（必填）
  "password": "password123",     // 密码（必填）
  "role": "student"              // 角色：student/teacher/admin（可选，默认student）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "studentId": "S2021001",
    "name": null,
    "role": "student",
    "subject": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

### 2. 用户登录
**接口地址：** `POST /api/user/login`

**请求参数：**
```json
{
  "username": "S2021001",        // 学号（必填）
  "password": "password123"      // 密码（必填）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "studentId": "S2021001",
    "name": "张三",
    "role": "student",
    "subject": "计算机科学",
    "email": "zhangsan@student.edu.cn",
    "phone": "13800000007",
    "gender": 1,
    "status": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

### 3. 根据学号查询用户
**接口地址：** `GET /api/user/{studentId}`

**路径参数：**
- `studentId` (String): 学号

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "studentId": "S2021001",
    "name": "张三",
    "role": "student",
    "subject": "计算机科学",
    "email": "zhangsan@student.edu.cn",
    "phone": "13800000007",
    "gender": 1,
    "status": 1
  }
}
```

---

### 4. 根据用户ID查询用户
**接口地址：** `GET /api/user/by-id/{id}`

**路径参数：**
- `id` (Long): 用户ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "studentId": "S2021001",
    "name": "张三",
    "role": "student",
    "subject": "计算机科学"
  }
}
```

---

### 5. 查询所有教师
**接口地址：** `GET /api/user/teachers`

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "studentId": "T001",
      "name": "张教授",
      "role": "teacher",
      "subject": "计算机科学",
      "email": "zhang@simstudio.edu.cn",
      "phone": "13800000002",
      "gender": 1,
      "status": 1
    }
  ]
}
```

---

### 6. 获取当前用户个人信息
**接口地址：** `GET /api/user/profile/{id}`

**路径参数：**
- `id` (Long): 用户ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "studentId": "S2021001",
    "name": "张三",
    "role": "student",
    "subject": "计算机科学",
    "email": "zhangsan@student.edu.cn",
    "phone": "13800000007",
    "gender": 1,
    "avatar": null,
    "status": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

**说明：**
- 返回完整的用户个人信息
- 密码字段会被移除，不会返回给前端

---

### 7. 更新用户个人信息
**接口地址：** `PUT /api/user/profile/update`

**请求参数：**
```json
{
  "id": 1,                            // 用户ID（必填）
  "name": "张三",                     // 姓名（可选）
  "subject": "软件工程",              // 学科/专业（可选）
  "email": "zhangsan@student.edu.cn", // 邮箱（可选）
  "phone": "13800000007",             // 手机号（可选）
  "gender": 1,                        // 性别：0-未知，1-男，2-女（可选）
  "avatar": "http://example.com/avatar.jpg", // 头像URL（可选）
  "status": 1                         // 状态：1-正常，0-禁用（可选）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "studentId": "S2021001",
    "name": "张三",
    "role": "student",
    "subject": "软件工程",
    "email": "zhangsan@student.edu.cn",
    "phone": "13800000007",
    "gender": 1,
    "avatar": "http://example.com/avatar.jpg",
    "status": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

**说明：**
- 只能更新个人信息，不能修改密码、学号、角色、创建时间等敏感字段
- 只更新传入的字段，未传入的字段保持不变
- 更新后会自动更新 `updatedAt` 字段

---

## 课程相关API

### 1. 获取所有课程
**接口地址：** `GET /api/course/list`

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "courseCode": "CS101",
      "name": "计算机科学导论",
      "description": "介绍计算机科学的基本概念...",
      "credit": 3,
      "totalHours": 48,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 2. 根据ID获取课程详情
**接口地址：** `GET /api/course/{id}`

**路径参数：**
- `id` (Long): 课程ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "courseCode": "CS101",
    "name": "计算机科学导论",
    "description": "介绍计算机科学的基本概念...",
    "credit": 3,
    "totalHours": 48
  }
}
```

---

### 3. 创建课程
**接口地址：** `POST /api/course/create`

**请求参数：**
```json
{
  "courseCode": "CS101",              // 课程代码（必填）
  "name": "计算机科学导论",            // 课程名称（必填）
  "description": "课程描述",          // 课程描述（可选）
  "credit": 3,                        // 学分（必填）
  "totalHours": 48                     // 总学时（必填）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "courseCode": "CS101",
    "name": "计算机科学导论",
    "description": "课程描述",
    "credit": 3,
    "totalHours": 48,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

### 4. 更新课程
**接口地址：** `PUT /api/course/update`

**请求参数：**
```json
{
  "id": 1,                            // 课程ID（必填）
  "courseCode": "CS101",
  "name": "计算机科学导论（更新）",
  "description": "更新后的描述",
  "credit": 4,
  "totalHours": 64
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "courseCode": "CS101",
    "name": "计算机科学导论（更新）",
    "description": "更新后的描述",
    "credit": 4,
    "totalHours": 64,
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

---

### 5. 删除课程
**接口地址：** `DELETE /api/course/{id}`

**路径参数：**
- `id` (Long): 课程ID

**响应示例：**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 6. 搜索课程
**接口地址：** `GET /api/course/search`

**查询参数：**
- `name` (String): 课程名称（必填）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "courseCode": "CS101",
      "name": "计算机科学导论",
      "description": "介绍计算机科学的基本概念...",
      "credit": 3,
      "totalHours": 48
    }
  ]
}
```

---

## 课程组相关API

### 1. 创建课程组
**接口地址：** `POST /api/course/group/create`

**请求参数：**
```json
{
  "courseId": 1,                      // 课程ID（必填）
  "teacherId": 2,                      // 教师ID（必填）
  "semester": "2024春季",              // 学期（必填）
  "maxStudents": 50,                   // 最大学生数（可选，默认50）
  "scheduleSlots": []                  // 时间安排（可选）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "courseId": 1,
    "teacherId": 2,
    "semester": "2024春季",
    "maxStudents": 50,
    "currentStudents": 0,
    "status": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

### 2. 根据ID获取课程组详情
**接口地址：** `GET /api/course/group/{id}`

**路径参数：**
- `id` (Long): 课程组ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "courseId": 1,
    "teacherId": 2,
    "semester": "2024春季",
    "maxStudents": 50,
    "currentStudents": 32,
    "status": 1
  }
}
```

---

### 3. 根据学期获取课程组列表
**接口地址：** `GET /api/course/group/semester/{semester}`

**路径参数：**
- `semester` (String): 学期（需要URL编码，如：2024%E6%98%A5%E5%AD%A3）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "courseId": 1,
      "teacherId": 2,
      "semester": "2024春季",
      "maxStudents": 50,
      "currentStudents": 32,
      "status": 1
    }
  ]
}
```

---

### 4. 根据教师ID获取课程组列表
**接口地址：** `GET /api/course/group/teacher/{teacherId}`

**路径参数：**
- `teacherId` (Long): 教师ID

**查询参数：**
- `semester` (String, 可选): 学期

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "courseId": 1,
      "teacherId": 2,
      "semester": "2024春季",
      "maxStudents": 50,
      "currentStudents": 32,
      "status": 1
    }
  ]
}
```

---

### 5. 更新课程组
**接口地址：** `PUT /api/course/group/update`

**请求参数：**
```json
{
  "id": 1,                            // 课程组ID（必填）
  "courseId": 1,
  "teacherId": 2,
  "semester": "2024春季",
  "maxStudents": 60,
  "status": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "courseId": 1,
    "teacherId": 2,
    "semester": "2024春季",
    "maxStudents": 60,
    "currentStudents": 32,
    "status": 1,
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

---

### 6. 删除课程组
**接口地址：** `DELETE /api/course/group/{id}`

**路径参数：**
- `id` (Long): 课程组ID

**响应示例：**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 7. 邀请学生加入课程
**接口地址：** `POST /api/course/group/invite`

**请求参数：**
```json
{
  "groupId": 1,                       // 课程组ID（必填）
  "studentId": 7                      // 学生ID（必填）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "邀请成功",
  "data": {
    "id": 1,
    "studentId": 7,
    "groupId": 1,
    "enrollStatus": "ENROLLED",
    "grade": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

### 8. 获取课程组的学生列表
**接口地址：** `GET /api/course/group/{groupId}/students`

**路径参数：**
- `groupId` (Long): 课程组ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "studentId": 7,
      "groupId": 1,
      "enrollStatus": "ENROLLED",
      "grade": null,
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 9. 学生退课（将状态置为DROPPED）
**接口地址：** `POST /api/course/group/{groupId}/students/{studentId}/drop`

**路径参数：**
- `groupId` (Long): 课程组ID
- `studentId` (Long): 学生ID

**响应示例：**
```json
{
  "code": 200,
  "message": "移除成功",
  "data": null
}
```

---

### 10. 老师给学生打分
**接口地址：** `POST /api/course/group/{groupId}/students/{studentId}/grade`

**路径参数：**
- `groupId` (Long): 课程组ID
- `studentId` (Long): 学生ID

**查询参数：**
- `grade` (BigDecimal): 成绩（必填）

**响应示例：**
```json
{
  "code": 200,
  "message": "打分成功",
  "data": {
    "id": 1,
    "studentId": 7,
    "groupId": 1,
    "enrollStatus": "COMPLETED",
    "grade": 90.00,
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

---

### 11. 物理删除学生选课关系
**接口地址：** `DELETE /api/course/group/{groupId}/students/{studentId}`

**路径参数：**
- `groupId` (Long): 课程组ID
- `studentId` (Long): 学生ID

**响应示例：**
```json
{
  "code": 200,
  "message": "移除成功",
  "data": null
}
```

---

### 12. 获取课程组的申请列表（按状态筛选）
**接口地址：** `GET /api/course/group/{groupId}/applications`

**路径参数：**
- `groupId` (Long): 课程组ID

**查询参数：**
- `status` (String, 可选): 选课状态（ENROLLING-申请中，ENROLLED-已选课，DROPPED-已退课，COMPLETED-已完成）。不传则返回所有状态

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "studentId": 7,
      "groupId": 1,
      "enrollStatus": "ENROLLING",
      "grade": null,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00",
      "student": {
        "id": 7,
        "studentId": "S2021001",
        "name": "张三",
        "role": "student",
        "subject": "计算机科学",
        "email": "zhangsan@student.edu.cn",
        "phone": "13800000007"
      }
    }
  ]
}
```

---

### 13. 老师审核学生选课申请（通过）
**接口地址：** `POST /api/course/group/{groupId}/students/{studentId}/approve`

**路径参数：**
- `groupId` (Long): 课程组ID
- `studentId` (Long): 学生ID

**响应示例：**
```json
{
  "code": 200,
  "message": "审核通过",
  "data": {
    "id": 1,
    "studentId": 7,
    "groupId": 1,
    "enrollStatus": "ENROLLED",
    "grade": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T11:00:00",
    "student": {
      "id": 7,
      "studentId": "S2021001",
      "name": "张三",
      "role": "student"
    },
    "courseGroup": {
      "id": 1,
      "courseId": 1,
      "teacherId": 2,
      "semester": "2024春季",
      "maxStudents": 50,
      "currentStudents": 1
    }
  }
}
```

**说明：**
- 只能审核状态为 `ENROLLING` 的申请
- 审核通过后，状态变为 `ENROLLED`
- 会自动检查课程是否已满，如果已满则无法通过
- 审核通过后会自动更新课程组的当前学生数

---

### 14. 老师审核学生选课申请（拒绝）
**接口地址：** `POST /api/course/group/{groupId}/students/{studentId}/reject`

**路径参数：**
- `groupId` (Long): 课程组ID
- `studentId` (Long): 学生ID

**响应示例：**
```json
{
  "code": 200,
  "message": "已拒绝申请",
  "data": null
}
```

**说明：**
- 只能拒绝状态为 `ENROLLING` 的申请
- 拒绝后会物理删除选课记录

---

## 班级相关API

### 1. 创建班级
**接口地址：** `POST /api/class/create`

**请求参数：**
```json
{
  "className": "计算机科学导论-1班",  // 班级名称（必填）
  "classCode": "CS101-01",          // 班级代码（可选，唯一）
  "teacherId": 2,                   // 教师ID（必填）
  "courseId": 1,                    // 课程ID（必填）
  "description": "计算机科学导论第一班", // 班级描述（可选）
  "status": 1                       // 状态：1-正常，0-停用（可选，默认1）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "className": "计算机科学导论-1班",
    "classCode": "CS101-01",
    "teacherId": 2,
    "courseId": 1,
    "description": "计算机科学导论第一班",
    "status": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00",
    "teacher": {
      "id": 2,
      "studentId": "T001",
      "name": "张教授",
      "role": "teacher",
      "subject": "计算机科学"
    },
    "course": {
      "id": 1,
      "courseCode": "CS101",
      "name": "计算机科学导论",
      "description": "介绍计算机科学的基本概念...",
      "credit": 3,
      "totalHours": 48
    },
    "courseGroup": {
      "id": 1,
      "courseId": 1,
      "teacherId": 2,
      "semester": "2024春季",
      "maxStudents": 50,
      "currentStudents": 32,
      "status": 1,
      "createdAt": "2024-01-01T09:00:00"
    }
  }
}
```

**说明：**
- 创建班级时会自动关联对应的课程组信息（如果存在）
- 课程组信息包含开课时间、学期等详细信息

---

### 2. 根据ID获取班级详情
**接口地址：** `GET /api/class/{id}`

**路径参数：**
- `id` (Long): 班级ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "className": "计算机科学导论-1班",
    "classCode": "CS101-01",
    "teacherId": 2,
    "courseId": 1,
    "description": "计算机科学导论第一班",
    "status": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00",
    "teacher": {
      "id": 2,
      "studentId": "T001",
      "name": "张教授",
      "role": "teacher",
      "subject": "计算机科学",
      "email": "zhang@simstudio.edu.cn",
      "phone": "13800000002"
    },
    "course": {
      "id": 1,
      "courseCode": "CS101",
      "name": "计算机科学导论",
      "description": "介绍计算机科学的基本概念、编程基础、数据结构与算法等内容...",
      "credit": 3,
      "totalHours": 48
    },
    "courseGroup": {
      "id": 1,
      "courseId": 1,
      "teacherId": 2,
      "semester": "2024春季",
      "maxStudents": 50,
      "currentStudents": 32,
      "status": 1,
      "createdAt": "2024-01-01T09:00:00",
      "updatedAt": "2024-01-01T10:00:00",
      "course": {
        "id": 1,
        "courseCode": "CS101",
        "name": "计算机科学导论"
      },
      "teacher": {
        "id": 2,
        "name": "张教授"
      }
    }
  }
}
```

**说明：**
- 返回的班级信息包含关联的课程组信息（courseGroup）
- 课程组信息中包含开课时间（createdAt）、学期（semester）等详细信息
- 如果该教师没有开设对应的课程组，courseGroup 字段为 null

---

### 3. 根据教师ID获取班级列表
**接口地址：** `GET /api/class/teacher/{teacherId}`

**路径参数：**
- `teacherId` (Long): 教师ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "className": "计算机科学导论-1班",
      "classCode": "CS101-01",
      "teacherId": 2,
      "courseId": 1,
      "description": "计算机科学导论第一班",
      "status": 1,
      "teacher": {
        "id": 2,
        "name": "张教授"
      },
      "course": {
        "id": 1,
        "name": "计算机科学导论"
      },
      "courseGroup": {
        "id": 1,
        "semester": "2024春季",
        "maxStudents": 50,
        "currentStudents": 32
      }
    }
  ]
}
```

---

### 4. 根据课程ID获取班级列表
**接口地址：** `GET /api/class/course/{courseId}`

**路径参数：**
- `courseId` (Long): 课程ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "className": "计算机科学导论-1班",
      "classCode": "CS101-01",
      "teacherId": 2,
      "courseId": 1,
      "description": "计算机科学导论第一班",
      "status": 1,
      "teacher": {
        "id": 2,
        "name": "张教授"
      },
      "course": {
        "id": 1,
        "name": "计算机科学导论"
      },
      "courseGroup": {
        "id": 1,
        "semester": "2024春季",
        "maxStudents": 50,
        "currentStudents": 32
      }
    }
  ]
}
```

---

### 5. 获取所有班级
**接口地址：** `GET /api/class/list`

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "className": "计算机科学导论-1班",
      "classCode": "CS101-01",
      "teacherId": 2,
      "courseId": 1,
      "description": "计算机科学导论第一班",
      "status": 1,
      "createdAt": "2024-01-01T10:00:00",
      "teacher": {
        "id": 2,
        "name": "张教授"
      },
      "course": {
        "id": 1,
        "name": "计算机科学导论"
      },
      "courseGroup": {
        "id": 1,
        "semester": "2024春季",
        "maxStudents": 50,
        "currentStudents": 32
      }
    }
  ]
}
```

---

### 6. 更新班级信息
**接口地址：** `PUT /api/class/update`

**请求参数：**
```json
{
  "id": 1,                            // 班级ID（必填）
  "className": "计算机科学导论-1班（更新）",
  "classCode": "CS101-01-UPDATED",
  "description": "更新后的描述",
  "status": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "className": "计算机科学导论-1班（更新）",
    "classCode": "CS101-01-UPDATED",
    "description": "更新后的描述",
    "status": 1,
    "updatedAt": "2024-01-01T11:00:00",
    "teacher": {
      "id": 2,
      "name": "张教授"
    },
    "course": {
      "id": 1,
      "name": "计算机科学导论"
    },
    "courseGroup": {
      "id": 1,
      "semester": "2024春季"
    }
  }
}
```

**说明：**
- 只能更新班级的基本信息，不能修改教师ID和课程ID
- 更新后会自动重新加载关联信息

---

### 7. 删除班级
**接口地址：** `DELETE /api/class/{id}`

**路径参数：**
- `id` (Long): 班级ID

**响应示例：**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**说明：**
- 删除班级不会影响关联的课程组和学生选课关系

---

## 学生相关API

### 1. 搜索学生
**接口地址：** `GET /api/student/search`

**查询参数：**
- `keyword` (String, 可选): 关键词（学号或姓名）
- `subject` (String, 可选): 学科（用于筛选）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 7,
      "studentId": "S2021001",
      "name": "张三",
      "role": "student",
      "subject": "计算机科学",
      "email": "zhangsan@student.edu.cn",
      "phone": "13800000007",
      "gender": 1,
      "status": 1
    }
  ]
}
```

---

### 2. 学生选课
**接口地址：** `POST /api/student/course/enroll`

**查询参数：**
- `groupId` (Long): 课程组ID（必填）
- `studentId` (Long, 可选): 学生ID（必填，可从token获取）

**响应示例：**
```json
{
  "code": 200,
  "message": "选课成功",
  "data": {
    "id": 1,
    "studentId": 7,
    "groupId": 1,
    "enrollStatus": "ENROLLING",
    "grade": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

**说明：**
- 学生选课后，状态为 `ENROLLING`（申请中）
- 需要等待老师审核通过后，状态才会变为 `ENROLLED`（已选课）
- 如果学生已提交申请，再次选课会提示"选课申请已提交，等待审核"

---

### 3. 学生退课
**接口地址：** `POST /api/student/course/drop`

**查询参数：**
- `studentId` (Long): 学生ID（必填）
- `groupId` (Long): 课程组ID（必填）

**响应示例：**
```json
{
  "code": 200,
  "message": "退课成功",
  "data": null
}
```

---

### 4. 查询学生的选课列表
**接口地址：** `GET /api/student/course/list`

**查询参数：**
- `studentId` (Long): 学生ID（必填）
- `semester` (String, 可选): 学期

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "studentId": 7,
      "groupId": 1,
      "enrollStatus": "ENROLLED",
      "grade": null,
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 5. 检查学生是否已选课
**接口地址：** `GET /api/student/course/check`

**查询参数：**
- `studentId` (Long): 学生ID（必填）
- `groupId` (Long): 课程组ID（必填）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 教师相关API

### 1. 获取所有教师
**接口地址：** `GET /api/teacher/list`

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "studentId": "T001",
      "name": "张教授",
      "role": "teacher",
      "subject": "计算机科学",
      "email": "zhang@simstudio.edu.cn",
      "phone": "13800000002",
      "gender": 1,
      "status": 1
    }
  ]
}
```

---

### 2. 根据学科获取教师（创建课程中使用）
**接口地址：** `GET /api/teacher/subject/{subject}`

**路径参数：**
- `subject` (String): 学科

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "studentId": "T001",
      "name": "张教授",
      "role": "teacher",
      "subject": "计算机科学",
      "email": "zhang@simstudio.edu.cn",
      "phone": "13800000002",
      "gender": 1,
      "status": 1
    }
  ]
}
```

---

### 3. 根据课程ID获取匹配的教师
**接口地址：** `GET /api/teacher/by-course/{courseId}`

**路径参数：**
- `courseId` (Long): 课程ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "studentId": "T001",
      "name": "张教授",
      "role": "teacher",
      "subject": "计算机科学"
    }
  ]
}
```

---

## 课程材料相关API

### 1. 获取课程组的材料列表
**接口地址：** `GET /api/course/group/{groupId}/materials`

**路径参数：**
- `groupId` (Long): 课程组ID

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "groupId": 1,
      "uploaderId": 2,
      "title": "计算机科学导论-第一章：计算机基础",
      "type": "ARTICLE",
      "content": "# 第一章：计算机基础\n\n## 1.1 计算机的发展历史...",
      "url": null,
      "originalFilename": null,
      "storedFilename": null,
      "filePath": null,
      "contentType": null,
      "fileSize": null,
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 2. 创建课程材料（老师使用）
**接口地址：** `POST /api/course/group/{groupId}/materials`

**路径参数：**
- `groupId` (Long): 课程组ID

**查询参数：**
- `uploaderId` (Long, 可选): 发布者ID

**请求参数：**
```json
{
  "title": "课程材料标题",              // 标题（必填）
  "type": "ARTICLE",                   // 类型：ARTICLE/SLIDE/FILE/LINK（必填）
  "content": "材料内容",               // 内容（可选，ARTICLE类型使用）
  "url": "https://example.com/file.pdf" // URL（可选，FILE/LINK类型使用）
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "groupId": 1,
    "uploaderId": 2,
    "title": "课程材料标题",
    "type": "ARTICLE",
    "content": "材料内容",
    "url": null,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

---

### 3. 更新课程材料(老师使用)
**接口地址：** `PUT /api/course/material/{materialId}`

**路径参数：**
- `materialId` (Long): 材料ID

**请求参数：**
```json
{
  "title": "更新后的标题",
  "type": "ARTICLE",
  "content": "更新后的内容",
  "url": null
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 1,
    "groupId": 1,
    "uploaderId": 2,
    "title": "更新后的标题",
    "type": "ARTICLE",
    "content": "更新后的内容",
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

---

### 4. 删除课程材料（老师使用）
**接口地址：** `DELETE /api/course/material/{materialId}`

**路径参数：**
- `materialId` (Long): 材料ID

**响应示例：**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 5. 上传文件
**接口地址：** `POST /api/course/material/upload`

**请求参数：**
- `file` (MultipartFile): 文件（必填）

**响应示例：**
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "http://localhost:8080/uploads/course-material/xxx.pdf",
    "filename": "original-file.pdf"
  }
}
```

---

## 通用响应格式

所有API接口统一使用以下响应格式：

```json
{
  "code": 200,           // 状态码：200-成功，其他-失败
  "message": "success",   // 消息提示
  "data": {}             // 数据（成功时返回，失败时为null）
}
```

---

## 状态码说明

- `200`: 操作成功
- `400`: 请求参数错误
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 枚举值说明

### 用户角色 (role)
- `student`: 学生
- `teacher`: 教师
- `admin`: 管理员

### 选课状态 (enrollStatus)
- `ENROLLING`: 申请中（学生提交选课申请，等待老师审核）
- `ENROLLED`: 已选课（老师审核通过）
- `DROPPED`: 已退课
- `COMPLETED`: 已完成

### 课程材料类型 (type)
- `ARTICLE`: 文章
- `SLIDE`: 课件
- `FILE`: 文件
- `LINK`: 外链

### 性别 (gender)
- `0`: 未知
- `1`: 男
- `2`: 女

### 状态 (status)
- `1`: 正常
- `0`: 禁用/停用

---

## 注意事项

1. 所有时间字段使用 ISO 8601 格式：`yyyy-MM-ddTHH:mm:ss`
2. 密码字段在响应中会被移除，不会返回给前端
3. 文件上传接口返回的URL为绝对路径
4. 学期参数在URL中需要URL编码
5. 所有ID字段类型为Long（64位整数）

---

**文档版本：** 1.0  
**最后更新：** 2024-01-31

