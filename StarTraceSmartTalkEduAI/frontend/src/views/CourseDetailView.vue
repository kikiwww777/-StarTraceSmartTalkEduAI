<template>
  <div class="course-detail-page fade-in">
    <!-- Top Navigation & Title -->
    <div class="page-top-bar">
      <button class="btn-back" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <line x1="19" y1="12" x2="5" y2="12" />
          <polyline points="12 19 5 12 12 5" />
        </svg>
        <span>返回课程中心</span>
      </button>
      <div v-if="course" class="header-content">
        <div class="header-left">
          <div class="course-badge" :class="course.role">
            {{ course.role === 'teacher' ? '教师视角' : '学生视角' }}
          </div>
          <h1 class="course-title">{{ course.name }}</h1>
        </div>
        <div class="header-actions">
          <button v-if="course.role === 'teacher'" class="btn-add-member" @click="openInviteModal">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
              <line x1="19" y1="8" x2="19" y2="14" />
              <line x1="16" y1="11" x2="22" y2="11" />
            </svg>
            添加新成员
          </button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>正在同步课程详情...</p>
    </div>

    <div v-else-if="course" class="detail-container">
      <!-- 课程概览卡片 -->
      <div class="overview-section">
        <div class="section-card">
          <div class="card-header">
            <div class="header-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5v-15z" />
              </svg>
            </div>
            <h2 class="card-title">课程简介</h2>
          </div>
          <div v-if="course" class="course-info">
            <div class="info-row">
              <span class="info-label">课程代码：</span>
              <span class="info-value">{{ course.courseCode }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">课程名称：</span>
              <span class="info-value">{{ course.name }}</span>
            </div>
            <p class="course-desc">{{ course.description || '该课程暂无详细描述，由任课教师随后补充。' }}</p>
            <div class="info-row">
              <span class="info-label">学分：</span>
              <span class="info-value">{{ course.credit }} 学分</span>
            </div>
            <div class="info-row">
              <span class="info-label">总学时：</span>
              <span class="info-value">{{ course.totalHours }} 小时</span>
            </div>
            <div v-if="courseGroup" class="info-row">
              <span class="info-label">学期：</span>
              <span class="info-value">{{ courseGroup.semester }}</span>
            </div>
            <div v-if="courseGroup" class="info-row">
              <span class="info-label">任课教师：</span>
              <span class="info-value">{{ courseGroup.teacher?.name || '未知' }}</span>
            </div>
          </div>
          <div class="quick-stats">
            <div class="q-stat">
              <span class="q-val">{{ members.length }}</span>
              <span class="q-label">当前学生</span>
            </div>
            <div class="q-stat" v-if="courseGroup">
              <span class="q-val">{{ courseGroup.maxStudents }}</span>
              <span class="q-label">最大容量</span>
            </div>
            <div class="q-stat" v-if="courseGroup">
              <span class="q-val">{{ courseGroup.status === 1 ? '活跃中' : '已停用' }}</span>
              <span class="q-label">课程状态</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 课程资料区域 -->
      <div class="materials-section">
        <div class="section-card">
          <div class="card-header">
            <div class="header-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5v-15z" />
              </svg>
            </div>
            <h2 class="card-title">课程资料</h2>
            <div v-if="course && course.role === 'teacher'" class="header-actions">
              <button class="btn-add-material" @click="openAddMaterialModal">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19" />
                  <line x1="5" y1="12" x2="19" y2="12" />
                </svg>
                添加资料
              </button>
            </div>
          </div>

          <div v-if="loadingMaterials" class="loading-text">加载中...</div>
          <div v-else-if="materials.length === 0" class="empty-state">
            <p>暂无课程资料</p>
            <p v-if="course && course.role === 'teacher'" class="empty-hint">点击"添加资料"按钮开始添加</p>
          </div>
          <div v-else class="materials-list">
            <div v-for="material in materials" :key="material.id" class="material-item">
              <div class="material-icon" :class="material.type.toLowerCase()">
                <svg v-if="material.type === 'ARTICLE'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5v-15z" />
                </svg>
                <svg v-else-if="material.type === 'SLIDE'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="3" width="18" height="18" rx="2" />
                  <line x1="9" y1="3" x2="9" y2="21" />
                </svg>
                <svg v-else-if="material.type === 'FILE'" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z" />
                  <polyline points="13 2 13 9 20 9" />
                </svg>
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
                  <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
                </svg>
              </div>
              <div class="material-content">
                <div class="material-title">{{ material.title }}</div>
                <div class="material-meta">
                  <span class="material-type-badge" :class="material.type.toLowerCase()">
                    {{ getMaterialTypeText(material.type) }}
                  </span>
                  <span class="material-date">
                    {{ formatDate(material.createdAt) }}
                  </span>
                </div>
                <!-- 外链/文件链接展示 -->
                <div
                  v-if="material.url && (material.type === 'LINK' || material.type === 'FILE' || material.type === 'SLIDE')"
                  class="material-link"
                >
                  <a :href="material.url" target="_blank" rel="noopener noreferrer" class="link-text">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <!-- 外链图标 -->
                      <template v-if="material.type === 'LINK'">
                        <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
                        <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
                      </template>
                      <!-- 文件/课件图标 -->
                      <template v-else>
                        <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z" />
                        <polyline points="13 2 13 9 20 9" />
                      </template>
                    </svg>
                    {{ material.url }}
                  </a>
                </div>
              </div>
              <div class="material-actions">
                <button class="btn-view" @click="openViewMaterialModal(material)" title="查看详细">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                  查看
                </button>
                <button v-if="course && course.role === 'teacher'" class="btn-edit" @click="openEditMaterialModal(material)" title="编辑">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
                  </svg>
                </button>
                <button v-if="course && course.role === 'teacher'" class="btn-delete" @click="deleteMaterial(material.id)" title="删除">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6" />
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 班级区域（教师：管理班级；学生：进入班级） -->
      <div v-if="course" class="classes-section">
        <div class="section-card">
          <div class="card-header">
            <div class="header-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
            </div>
            <h2 class="card-title">
              {{ course.role === 'teacher' ? '班级管理' : '我的班级' }}
            </h2>
            <!-- 教师且还没有班级时，显示创建按钮；学生不展示 -->
            <div v-if="course.role === 'teacher' && classes.length === 0" class="header-actions">
              <button class="btn-add-class" @click="openCreateClassModal">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19" />
                  <line x1="5" y1="12" x2="19" y2="12" />
                </svg>
                创建班级
              </button>
            </div>
          </div>

          <div v-if="loadingClasses" class="loading-text">加载中...</div>
          <!-- 没有班级时的提示：教师提示去创建；学生提示等待老师分配 -->
          <div v-else-if="classes.length === 0" class="empty-state">
            <p v-if="course.role === 'teacher'">暂无班级</p>
            <p v-else>你当前还未被分配到班级</p>
            <p v-if="course.role === 'teacher'" class="empty-hint">点击"创建班级"按钮开始创建</p>
          </div>
          <!-- 一个课程只对应一个班级：有班级时仅展示第一个，并提供一个进入按钮（师生共用） -->
          <div v-else class="classes-list">
            <div
              v-for="classItem in classes.slice(0, 1)"
              :key="classItem.id"
              class="class-item"
            >
              <div class="class-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                  <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                </svg>
              </div>
              <div class="class-content">
                <div class="class-title">{{ classItem.className }}</div>
                <div class="class-meta">
                  <span v-if="classItem.classCode" class="class-code">班级代码：{{ classItem.classCode }}</span>
                  <span class="class-status" :class="classItem.status === 1 ? 'active' : 'inactive'">
                    {{ classItem.status === 1 ? '正常' : '停用' }}
                  </span>
                </div>
                <div v-if="classItem.description" class="class-description">{{ classItem.description }}</div>
              </div>
              <div class="class-actions">
                <!-- 只有一个“进入班级”按钮 -->
                <button class="btn-enter-class" @click="enterClass(classItem.id)" title="进入班级">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M5 12h14M12 5l7 7-7 7" />
                  </svg>
                  进入
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 成员管理列表 -->
      <div class="members-section">
        <div class="section-card">
          <div class="card-header">
            <div class="header-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
            </div>
            <h2 class="card-title">班级成员名单</h2>
          </div>

          <div class="table-frame">
            <table class="academic-table">
              <thead>
                <tr>
                  <th>成员信息</th>
                  <th>学号/工号</th>
                  <th>选课状态</th>
                  <th>成绩</th>
                  <th>加入日期</th>
                  <th v-if="course && course.role === 'teacher'" class="cell-actions">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="member in members" :key="member.id">
                  <td class="cell-user">
                    <div class="mini-avatar">{{ member.user?.name?.[0] || '?' }}</div>
                    <span class="user-name">{{ member.user?.name || '未知' }}</span>
                  </td>
                  <td class="cell-id font-mono">{{ member.user?.studentId || '-' }}</td>
                  <td>
                    <div class="status-pill" :class="member.enrollStatus?.toLowerCase()">
                      {{ member.enrollStatus === 'ENROLLED' ? '已选课' : 
                         member.enrollStatus === 'ENROLLING' ? '申请中' :
                         member.enrollStatus === 'COMPLETED' ? '已完成' : 
                         member.enrollStatus === 'DROPPED' ? '已退课' : '未知' }}
                    </div>
                  </td>
                  <td class="cell-grade">
                    {{ member.grade !== null && member.grade !== undefined ? member.grade : '-' }}
                  </td>
                  <td class="cell-date">
                    <template v-if="member.joinedAt && member.joinedAt !== '-'">
                      {{ member.joinedAt }}
                    </template>
                    <template v-else>
                      <span style="color: #94a3b8;">-</span>
                    </template>
                  </td>
                  <td v-if="course && course.role === 'teacher'" class="cell-actions">
                    <div class="action-buttons">
                      <button 
                        v-if="member.enrollStatus === 'ENROLLED'" 
                        class="btn-grade" 
                        @click="openGradeModal(member)" 
                        title="打分"
                      >
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" />
                        </svg>
                        打分
                      </button>
                      <button class="btn-delete" @click="removeMember(member.id, parseInt(member.userId))" title="移出课程">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <polyline points="3 6 5 6 21 6" />
                          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                          <line x1="10" y1="11" x2="10" y2="17" />
                          <line x1="14" y1="11" x2="14" y2="17" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals (Flat Transparent Style) -->
    <div v-if="showInviteModal" class="modal-overlay" @click="showInviteModal = false">
      <div class="modal-box" @click.stop>
        <div class="modal-header">
          <h2>添加新成员</h2>
          <button class="btn-close" @click="showInviteModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>筛选学生（可多选）</label>
            <input 
              v-model="filterKeyword" 
              type="text" 
              placeholder="输入学号或姓名筛选" 
              class="modern-input"
            />
          </div>
          <div class="input-group">
            <label>选择学生 <span style="color: #94a3b8; font-weight: normal;">（已选择 {{ selectedStudentIds.length }} 名）</span></label>
            <div class="student-select-container">
              <div v-if="loadingAllStudents" class="loading-text">加载中...</div>
              <div v-else-if="filteredStudents.length === 0" class="empty-text">暂无学生数据</div>
              <div v-else class="student-select-list">
                <div 
                  v-for="student in filteredStudents" 
                  :key="student.id"
                  class="student-select-item"
                  :class="{ 
                    selected: isStudentSelected(student.id!),
                    disabled: studentStatusMap.get(student.id!) === 'ENROLLED' || studentStatusMap.get(student.id!) === 'COMPLETED'
                  }"
                  @click="toggleStudentSelection(student.id!)"
                >
                  <div class="student-select-info">
                    <div class="student-select-name">{{ student.name || '未知' }}</div>
                    <div class="student-select-id">{{ student.studentId }}</div>
                  </div>
                  <div class="student-select-status">
                    <span 
                      class="status-badge"
                      :class="getStudentStatusClass(studentStatusMap.get(student.id!) || '')"
                    >
                      {{ getStudentStatusText(studentStatusMap.get(student.id!) || '') }}
                    </span>
                    <div v-if="isStudentSelected(student.id!)" class="check-icon">✓</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showInviteModal = false">取消</button>
          <button class="btn-submit" @click="handleAddMember" :disabled="adding">
            {{ adding ? '添加中...' : '确认加入' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 打分弹窗 -->
    <div v-if="showGradeModal" class="modal-overlay" @click="showGradeModal = false">
      <div class="modal-box" @click.stop>
        <div class="modal-header">
          <h2>给学生打分</h2>
          <button class="btn-close" @click="showGradeModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>学生姓名</label>
            <input 
              :value="currentGradeMember?.userName || ''" 
              type="text" 
              class="modern-input" 
              disabled
            />
          </div>
          <div class="input-group">
            <label>当前成绩</label>
            <input 
              :value="currentGradeMember?.currentGrade !== null && currentGradeMember?.currentGrade !== undefined ? currentGradeMember.currentGrade : '未打分'" 
              type="text" 
              class="modern-input" 
              disabled
            />
          </div>
          <div class="input-group">
            <label>成绩 <span class="required">*</span></label>
            <input 
              v-model.number="gradeForm.grade" 
              type="number" 
              min="0" 
              max="100" 
              step="0.1"
              placeholder="请输入成绩 (0-100)" 
              class="modern-input" 
              required
            />
            <p class="input-tip">成绩范围：0-100分</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showGradeModal = false">取消</button>
          <button class="btn-submit" @click="handleGrade" :disabled="grading">
            {{ grading ? '提交中...' : '确认打分' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑资料弹窗 -->
    <div v-if="showMaterialModal" class="modal-overlay" @click="showMaterialModal = false">
      <div class="modal-box material-modal" @click.stop>
        <div class="modal-header">
          <h2>{{ editingMaterial ? '编辑资料' : '添加资料' }}</h2>
          <button class="btn-close" @click="showMaterialModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>标题 <span class="required">*</span></label>
            <input 
              v-model="materialForm.title" 
              type="text" 
              placeholder="请输入资料标题" 
              class="modern-input" 
              required
            />
          </div>
          
          <div class="input-group">
            <label>类型 <span class="required">*</span></label>
            <select v-model="materialForm.type" class="modern-select" required>
              <option value="ARTICLE">文章</option>
              <option value="SLIDE">课件</option>
              <option value="FILE">文件</option>
              <option value="LINK">外链</option>
            </select>
          </div>

          <!-- ARTICLE类型：内容输入 -->
          <div v-if="materialForm.type === 'ARTICLE'" class="input-group">
            <label>内容</label>
            <textarea 
              v-model="materialForm.content" 
              placeholder="请输入文章内容（支持Markdown格式）" 
              class="modern-textarea"
              rows="8"
            ></textarea>
          </div>

          <!-- FILE/SLIDE类型：文件上传 -->
          <div v-if="materialForm.type === 'FILE' || materialForm.type === 'SLIDE'" class="input-group">
            <label>{{ materialForm.type === 'SLIDE' ? '课件文件' : '文件' }} <span class="required">*</span></label>
            <div class="file-upload-area">
              <input 
                type="file" 
                @change="handleFileSelect"
                class="file-input"
                :accept="materialForm.type === 'SLIDE' ? '.pdf,.ppt,.pptx' : '*'"
              />
              <div v-if="selectedFile" class="file-info">
                <span>已选择：{{ selectedFile.name }}</span>
                <span class="file-size">({{ formatFileSize(selectedFile.size) }})</span>
              </div>
              <div v-else-if="editingMaterial && editingMaterial.url" class="file-info">
                <span>当前文件：{{ editingMaterial.url }}</span>
              </div>
              <div v-else class="file-placeholder">
                点击选择文件或拖拽文件到此处
              </div>
            </div>
            <p class="input-tip">{{ materialForm.type === 'SLIDE' ? '支持PDF、PPT、PPTX格式' : '支持所有文件格式' }}</p>
          </div>

          <!-- LINK类型：URL输入 -->
          <div v-if="materialForm.type === 'LINK'" class="input-group">
            <label>外链地址 <span class="required">*</span></label>
            <input 
              v-model="materialForm.url" 
              type="url" 
              placeholder="https://example.com" 
              class="modern-input" 
              required
            />
          </div>

          <!-- FILE类型（非上传）：URL输入 -->
          <div v-if="materialForm.type === 'FILE' && !selectedFile" class="input-group">
            <label>文件URL <span class="required">*</span></label>
            <input 
              v-model="materialForm.url" 
              type="url" 
              placeholder="https://example.com/file.pdf 或上传文件" 
              class="modern-input" 
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showMaterialModal = false">取消</button>
          <button class="btn-submit" @click="handleMaterialSubmit" :disabled="uploadingFile">
            {{ uploadingFile ? '上传中...' : (editingMaterial ? '保存' : '创建') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 查看资料详细模态框 -->
    <div v-if="showViewMaterialModal" class="modal-overlay" @click="showViewMaterialModal = false">
      <div class="modal-box modal-box-large" @click.stop>
        <div class="modal-header">
          <h2>资料详情</h2>
          <button class="btn-close" @click="showViewMaterialModal = false">×</button>
        </div>
        <div class="modal-body" v-if="viewingMaterial">
          <div class="material-detail">
            <div class="detail-row">
              <label>标题：</label>
              <div class="detail-value">{{ viewingMaterial.title }}</div>
            </div>
            <div class="detail-row">
              <label>类型：</label>
              <div class="detail-value">
                <span class="material-type-badge" :class="viewingMaterial.type.toLowerCase()">
                  {{ getMaterialTypeText(viewingMaterial.type) }}
                </span>
              </div>
            </div>
            <div class="detail-row" v-if="viewingMaterial.type === 'ARTICLE' && viewingMaterial.content">
              <label>内容：</label>
              <div class="detail-value detail-content">
                <pre>{{ viewingMaterial.content }}</pre>
              </div>
            </div>
            <div class="detail-row" v-if="viewingMaterial.type === 'LINK' && viewingMaterial.url">
              <label>外链地址：</label>
              <div class="detail-value">
                <a :href="viewingMaterial.url" target="_blank" rel="noopener noreferrer" class="link-text">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71" />
                    <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71" />
                  </svg>
                  {{ viewingMaterial.url }}
                </a>
              </div>
            </div>
            <div class="detail-row" v-if="(viewingMaterial.type === 'FILE' || viewingMaterial.type === 'SLIDE') && viewingMaterial.url">
              <label>{{ viewingMaterial.type === 'SLIDE' ? '课件' : '文件' }}地址：</label>
              <div class="detail-value">
                <a :href="viewingMaterial.url" target="_blank" rel="noopener noreferrer" class="link-text">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z" />
                    <polyline points="13 2 13 9 20 9" />
                  </svg>
                  {{ viewingMaterial.url }}
                </a>
              </div>
            </div>
            <div class="detail-row">
              <label>创建时间：</label>
              <div class="detail-value">{{ formatDate(viewingMaterial.createdAt) }}</div>
            </div>
            <div class="detail-row" v-if="viewingMaterial.updatedAt">
              <label>更新时间：</label>
              <div class="detail-value">{{ formatDate(viewingMaterial.updatedAt) }}</div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showViewMaterialModal = false">关闭</button>
        </div>
      </div>
    </div>

    <!-- 创建班级模态框 -->
    <div v-if="showCreateClassModal" class="modal-overlay" @click="showCreateClassModal = false">
      <div class="modal-box" @click.stop>
        <div class="modal-header">
          <h2>创建班级</h2>
          <button class="btn-close" @click="showCreateClassModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>班级名称 <span class="required">*</span></label>
            <input 
              v-model="classForm.className"
              ref="classNameInputRef"
              type="text" 
              placeholder="例如：2024级计算机1班" 
              class="modern-input" 
              required
            />
          </div>
          <div class="input-group">
            <label>班级代码</label>
            <input 
              v-model="classForm.classCode" 
              type="text" 
              placeholder="例如：CS2024-01（可选）" 
              class="modern-input"
            />
            <p class="input-tip">班级代码用于快速识别，如不填写将自动生成</p>
          </div>
          <div class="input-group">
            <label>班级描述</label>
            <textarea 
              v-model="classForm.description" 
              placeholder="请输入班级描述（可选）" 
              class="modern-textarea"
              rows="4"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showCreateClassModal = false">取消</button>
          <button class="btn-submit" @click="handleCreateClass" :disabled="creatingClass">
            {{ creatingClass ? '创建中...' : '确认创建' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuth } from "../composables/useAuth";
import { adminCourseApi, teacherApi, teacherMaterialApi, classApi, type CourseGroup, type StudentCourseRelation, type Student, type User, type CourseMaterial, type CourseMaterialDTO, type CourseMaterialType, type Class, type ClassDTO } from "../api/course";

const router = useRouter();
const route = useRoute();
const { isTeacher, currentUser } = useAuth();

const loading = ref(true);
const courseGroup = ref<CourseGroup | null>(null);
const students = ref<StudentCourseRelation[]>([]);
const showInviteModal = ref(false);
const adding = ref(false);
const loadingAllStudents = ref(false);
const allStudents = ref<User[]>([]);
const selectedStudentIds = ref<number[]>([]);
const filterKeyword = ref("");

// 打分相关
const showGradeModal = ref(false);
const grading = ref(false);
const currentGradeMember = ref<{
  id: string;
  userId: string;
  relationId: number;
  userName: string;
  currentGrade?: number;
} | null>(null);
const gradeForm = ref({ grade: null as number | null });

// 课程资料相关
const materials = ref<CourseMaterial[]>([]);
const loadingMaterials = ref(false);
const showMaterialModal = ref(false);
const showViewMaterialModal = ref(false);
const viewingMaterial = ref<CourseMaterial | null>(null);
const editingMaterial = ref<CourseMaterial | null>(null);
const materialForm = ref<CourseMaterialDTO>({
  title: '',
  type: 'ARTICLE',
  content: '',
  url: ''
});
const uploadingFile = ref(false);
const selectedFile = ref<File | null>(null);

// 班级管理相关
const classes = ref<Class[]>([]);
const loadingClasses = ref(false);
const showCreateClassModal = ref(false);
const creatingClass = ref(false);
const classForm = ref<ClassDTO>({
  className: '',
  classCode: '',
  teacherId: 0,
  courseId: 0,
  description: '',
  status: 1
});
const classNameInputRef = ref<HTMLInputElement | null>(null);

// 学生ID到选课状态的映射
const studentStatusMap = computed(() => {
  const map = new Map<number, string>();
  students.value.forEach(relation => {
    if (relation.studentId) {
      map.set(relation.studentId, relation.enrollStatus || '');
    }
  });
  return map;
});

// 过滤后的学生列表（用于下拉框显示）
const filteredStudents = computed(() => {
  let result = allStudents.value;
  
  // 关键词过滤
  if (filterKeyword.value.trim()) {
    const keyword = filterKeyword.value.trim().toLowerCase();
    result = result.filter(student => 
      (student.studentId && student.studentId.toLowerCase().includes(keyword)) ||
      (student.name && student.name.toLowerCase().includes(keyword))
    );
  }
  
  return result;
});

// 计算属性：课程信息
const course = computed(() => {
  if (!courseGroup.value) return null;
  return {
    id: String(courseGroup.value.id),
    name: courseGroup.value.course?.name || '未知课程',
    description: courseGroup.value.course?.description || '',
    courseCode: courseGroup.value.course?.courseCode || '',
    credit: courseGroup.value.course?.credit || 0,
    totalHours: courseGroup.value.course?.totalHours || 0,
    role: isTeacher.value ? 'teacher' as const : 'student' as const
  };
});

// 计算属性：成员列表（转换为显示格式）
const members = computed(() => {
  return students.value.map(relation => {
    // 如果后端返回了student对象，直接使用；否则需要通过studentId查询
    let studentInfo = relation.student;
    if (!studentInfo && relation.studentId) {
      // 如果后端没有返回student信息，这里可以后续优化为批量查询
      studentInfo = {
        id: relation.studentId,
        studentId: String(relation.studentId),
        name: undefined,
        role: 'student'
      };
    }
    
    // 格式化加入日期 - 支持多种可能的字段名和格式
    let joinedAtStr = '';
    // 尝试获取日期字段（可能是 createdAt 或 created_at）
    const dateValue = (relation as any).createdAt || (relation as any).created_at;
    
    if (dateValue) {
      try {
        let date: Date;
        
        // 处理数组格式：[年, 月, 日, 时, 分, 秒]
        if (Array.isArray(dateValue)) {
          // 数组格式：[2026, 2, 22, 10, 23, 29]
          // 注意：月份在数组中是从1开始的（2表示2月），但Date构造函数中月份是从0开始的
          if (dateValue.length >= 3) {
            const year = dateValue[0];
            const month = dateValue[1] - 1; // 转换为0-based月份
            const day = dateValue[2];
            const hour = dateValue[3] || 0;
            const minute = dateValue[4] || 0;
            const second = dateValue[5] || 0;
            date = new Date(year, month, day, hour, minute, second);
          } else {
            throw new Error("日期数组格式不正确");
          }
        } else if (typeof dateValue === 'string') {
          // 处理字符串格式：ISO 8601 或其他格式
          let dateStr = dateValue.trim();
          if (dateStr.includes('T')) {
            date = new Date(dateStr);
          } else if (dateStr.includes(' ')) {
            // 格式：2024-01-01 10:00:00
            date = new Date(dateStr.replace(' ', 'T'));
          } else {
            // 格式：2024-01-01
            date = new Date(dateStr);
          }
        } else if (dateValue instanceof Date) {
          date = dateValue;
        } else {
          // 如果是时间戳或其他格式
          date = new Date(dateValue);
        }
        
        if (!isNaN(date.getTime())) {
          // 格式化为 YYYY-MM-DD
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          joinedAtStr = `${year}-${month}-${day}`;
        } else {
          console.warn("日期解析失败，值:", dateValue, "类型:", typeof dateValue);
        }
      } catch (e) {
        console.error("日期格式化失败:", e, "原始值:", dateValue);
      }
    } else {
      console.warn("未找到创建时间字段，relation ID:", relation.id);
    }
    
    // 调试：打印日期处理结果
    if (relation.id) {
      console.log(`学生 ${relation.studentId} 日期处理:`, {
        dateValue: dateValue,
        joinedAtStr: joinedAtStr,
        relationCreatedAt: (relation as any).createdAt,
        relationCreated_at: (relation as any).created_at
      });
    }
    
    const memberData = {
      id: String(relation.id),
      userId: String(relation.studentId),
      relationId: relation.id, // 保留relation的ID用于打分
      role: 'student' as const,
      joinedAt: joinedAtStr || '-',
      enrollStatus: relation.enrollStatus,
      grade: relation.grade,
      user: studentInfo ? {
        name: studentInfo.name || '',
        studentId: studentInfo.studentId || String(relation.studentId)
      } : {
        name: '',
        studentId: String(relation.studentId)
      }
    };
    
    return memberData;
  });
});

const goBack = () => router.push("/courses");

// 加载课程组详情
const loadCourseGroup = async () => {
  const groupId = route.params.id as string;
  if (!groupId) {
    alert("课程组ID不存在");
    router.push("/courses");
    return;
  }

  loading.value = true;
  try {
    const groupIdNum = parseInt(groupId);
    if (isNaN(groupIdNum)) {
      throw new Error("无效的课程组ID");
    }

    // 获取课程组详情
    const groupResult = await adminCourseApi.getCourseGroupById(groupIdNum);
    if (groupResult.code === 200 && groupResult.data) {
      courseGroup.value = groupResult.data;
    } else {
      alert(groupResult.message || "获取课程组信息失败");
      router.push("/courses");
      return;
    }

    // 获取学生列表
    await loadStudents(groupIdNum);
    
    // 加载课程资料
    await loadMaterials(groupIdNum);
    
    // 加载班级列表（教师和学生都需要看到所属班级）
    await loadClasses();
  } catch (error: any) {
    console.error("加载课程组详情异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "加载课程组详情失败";
    alert(message);
    router.push("/courses");
  } finally {
    loading.value = false;
  }
};

// 加载学生列表
const loadStudents = async (groupId: number) => {
  try {
    const result = await teacherApi.getCourseGroupStudents(groupId);
    if (result.code === 200 && result.data) {
      students.value = result.data;
      // 调试：打印第一个学生的数据，检查createdAt字段
      if (result.data.length > 0) {
        const firstStudent = result.data[0];
        console.log("学生数据示例:", {
          id: firstStudent.id,
          studentId: firstStudent.studentId,
          createdAt: (firstStudent as any).createdAt,
          created_at: (firstStudent as any).created_at,
          fullData: JSON.stringify(firstStudent, null, 2)
        });
      }
    } else {
      console.error("获取学生列表失败:", result.message);
      students.value = [];
    }
  } catch (error: any) {
    console.error("获取学生列表异常:", error);
    students.value = [];
  }
};

// 加载课程资料
const loadMaterials = async (groupId: number) => {
  loadingMaterials.value = true;
  try {
    const result = await teacherMaterialApi.listByGroup(groupId);
    if (result.code === 200 && result.data) {
      materials.value = result.data;
    } else {
      console.error("获取课程资料失败:", result.message);
      materials.value = [];
    }
  } catch (error: any) {
    console.error("获取课程资料异常:", error);
    materials.value = [];
  } finally {
    loadingMaterials.value = false;
  }
};

// 打开添加资料弹窗
const openAddMaterialModal = () => {
  editingMaterial.value = null;
  materialForm.value = {
    title: '',
    type: 'ARTICLE',
    content: '',
    url: ''
  };
  selectedFile.value = null;
  showMaterialModal.value = true;
};

// 打开编辑资料弹窗
const openEditMaterialModal = (material: CourseMaterial) => {
  editingMaterial.value = material;
  materialForm.value = {
    title: material.title,
    type: material.type,
    content: material.content || '',
    url: material.url || ''
  };
  selectedFile.value = null;
  showMaterialModal.value = true;
};

// 打开查看资料详细弹窗
const openViewMaterialModal = (material: CourseMaterial) => {
  viewingMaterial.value = material;
  showViewMaterialModal.value = true;
};

// 获取材料类型中文
const getMaterialTypeText = (type: CourseMaterialType): string => {
  const typeMap: Record<CourseMaterialType, string> = {
    'ARTICLE': '文章',
    'SLIDE': '课件',
    'FILE': '文件',
    'LINK': '外链'
  };
  return typeMap[type] || type;
};

// 格式化日期
const formatDate = (dateStr?: string | any): string => {
  if (!dateStr) return '-';
  try {
    let date: Date;
    if (Array.isArray(dateStr)) {
      // 数组格式：[年, 月, 日, 时, 分, 秒]
      if (dateStr.length >= 3) {
        date = new Date(dateStr[0], dateStr[1] - 1, dateStr[2]);
      } else {
        return '-';
      }
    } else if (typeof dateStr === 'string') {
      date = new Date(dateStr);
    } else {
      date = new Date(dateStr);
    }
    
    if (!isNaN(date.getTime())) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  } catch (e) {
    console.error("日期格式化失败:", e);
  }
  return '-';
};

// 处理文件选择
const handleFileSelect = (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0];
  if (file) {
    selectedFile.value = file;
  }
};

// 上传文件
const uploadFile = async (): Promise<string | null> => {
  if (!selectedFile.value) return null;
  
  uploadingFile.value = true;
  try {
    const result = await teacherMaterialApi.uploadFile(selectedFile.value);
    if (result.code === 200 && result.data) {
      return result.data.url;
    } else {
      alert(result.message || "文件上传失败");
      return null;
    }
  } catch (error: any) {
    console.error("文件上传异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "文件上传失败";
    alert(message);
    return null;
  } finally {
    uploadingFile.value = false;
  }
};

// 提交资料表单
const handleMaterialSubmit = async () => {
  if (!materialForm.value.title.trim()) {
    alert("请输入标题");
    return;
  }

  if (!courseGroup.value) {
    alert("课程组信息不存在");
    return;
  }

  const groupId = courseGroup.value.id;
  let url = materialForm.value.url;

  // 如果是FILE或SLIDE类型，需要上传文件
  if ((materialForm.value.type === 'FILE' || materialForm.value.type === 'SLIDE') && selectedFile.value) {
    const uploadedUrl = await uploadFile();
    if (!uploadedUrl) {
      return; // 上传失败，不继续
    }
    url = uploadedUrl;
  }

  // 如果是FILE或LINK类型，必须有URL
  if ((materialForm.value.type === 'FILE' || materialForm.value.type === 'LINK') && !url) {
    alert("请输入URL或上传文件");
    return;
  }

  // 如果是ARTICLE类型，建议有内容
  if (materialForm.value.type === 'ARTICLE' && !materialForm.value.content?.trim()) {
    if (!confirm("文章内容为空，是否继续？")) {
      return;
    }
  }

  try {
    if (!currentUser.value?.id) {
      alert("当前未登录或用户信息缺失，无法提交资料");
      return;
    }
    const teacherId = typeof currentUser.value.id === 'string'
      ? parseInt(currentUser.value.id)
      : currentUser.value.id;

    const dto: CourseMaterialDTO = {
      title: materialForm.value.title.trim(),
      type: materialForm.value.type,
      content: materialForm.value.content || undefined,
      url: url || undefined
    };

    let result;
    if (editingMaterial.value) {
      // 更新
      result = await teacherMaterialApi.update(editingMaterial.value.id, dto);
    } else {
      // 创建
      result = await teacherMaterialApi.create(groupId, dto, teacherId);
    }

    if (result.code === 200) {
      alert(result.message || (editingMaterial.value ? "更新成功" : "创建成功"));
      showMaterialModal.value = false;
      await loadMaterials(groupId);
    } else {
      alert(result.message || (editingMaterial.value ? "更新失败" : "创建失败"));
    }
  } catch (error: any) {
    console.error("提交资料异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? (editingMaterial.value ? "更新失败" : "创建失败");
    alert(message);
  }
};

// 删除资料
const deleteMaterial = async (materialId: number) => {
  if (!confirm("确定要删除这个资料吗？")) {
    return;
  }

  try {
    const result = await teacherMaterialApi.remove(materialId);
    if (result.code === 200) {
      alert(result.message || "删除成功");
      if (courseGroup.value) {
        await loadMaterials(courseGroup.value.id);
      }
    } else {
      alert(result.message || "删除失败");
    }
  } catch (error: any) {
    console.error("删除资料异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "删除失败";
    alert(message);
  }
};

// ========== 班级管理相关函数 ==========

// 加载班级列表
const loadClasses = async () => {
  if (!courseGroup.value || !courseGroup.value.course?.id) {
    return;
  }

  loadingClasses.value = true;
  try {
    const result = await classApi.getByCourseId(courseGroup.value.course.id);
    if (result.code === 200 && result.data) {
      classes.value = result.data;
    } else {
      console.error("获取班级列表失败:", result.message);
      classes.value = [];
    }
  } catch (error: any) {
    console.error("获取班级列表异常:", error);
    classes.value = [];
  } finally {
    loadingClasses.value = false;
  }
};

// 打开创建班级弹窗
const openCreateClassModal = () => {
  if (!courseGroup.value || !currentUser.value?.id) {
    alert("课程信息或用户信息不存在");
    return;
  }

  const teacherId = typeof currentUser.value.id === 'string' 
    ? parseInt(currentUser.value.id) 
    : currentUser.value.id;
  const courseId = courseGroup.value.course?.id;

  if (!courseId) {
    alert("课程ID不存在");
    return;
  }

  classForm.value = {
    className: '',
    classCode: '',
    teacherId: teacherId,
    courseId: courseId,
    description: '',
    status: 1
  };
  showCreateClassModal.value = true;
  // 下一帧自动聚焦到班级名称输入框
  nextTick(() => {
    classNameInputRef.value?.focus();
  });
};

// 创建班级
const handleCreateClass = async () => {
  if (!classForm.value.className.trim()) {
    alert("请输入班级名称");
    return;
  }

  creatingClass.value = true;
  try {
    const result = await classApi.create(classForm.value);
    if (result.code === 200 && result.data) {
      alert(result.message || "创建班级成功！");
      showCreateClassModal.value = false;
      await loadClasses();
    } else {
      alert(result.message || "创建班级失败");
    }
  } catch (error: any) {
    console.error("创建班级异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "创建班级失败";
    alert(message);
  } finally {
    creatingClass.value = false;
  }
};

// 删除班级
const deleteClass = async (classId: number) => {
  if (!confirm("确定要删除这个班级吗？")) {
    return;
  }

  try {
    const result = await classApi.delete(classId);
    if (result.code === 200) {
      alert(result.message || "删除成功");
      await loadClasses();
    } else {
      alert(result.message || "删除失败");
    }
  } catch (error: any) {
    console.error("删除班级异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "删除失败";
    alert(message);
  }
};

// 进入班级
const enterClass = (classId: number) => {
  router.push(`/class/${classId}`);
};

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
};

// 获取所有学生列表（用于下拉框）
const loadAllStudents = async () => {
  loadingAllStudents.value = true;
  try {
    const result = await teacherApi.searchStudents(); // 不带参数获取所有学生
    if (result.code === 200 && result.data) {
      allStudents.value = result.data;
    } else {
      console.error("获取学生列表失败:", result.message);
      allStudents.value = [];
    }
  } catch (error: any) {
    console.error("获取学生列表异常:", error);
    allStudents.value = [];
  } finally {
    loadingAllStudents.value = false;
  }
};

// 获取学生状态文本
const getStudentStatusText = (status: string): string => {
  switch (status) {
    case 'ENROLLING': return '申请中';
    case 'ENROLLED': return '已选课';
    case 'DROPPED': return '已退课';
    case 'COMPLETED': return '已完成';
    default: return '未选课';
  }
};

// 获取学生状态样式类
const getStudentStatusClass = (status: string): string => {
  switch (status) {
    case 'ENROLLING': return 'status-enrolling';
    case 'ENROLLED': return 'status-enrolled';
    case 'DROPPED': return 'status-dropped';
    case 'COMPLETED': return 'status-completed';
    default: return 'status-none';
  }
};

// 切换学生选择
const toggleStudentSelection = (studentId: number) => {
  // 检查学生状态，已选课和已完成的学生不能再次选择
  const status = studentStatusMap.value.get(studentId);
  if (status === 'ENROLLED' || status === 'COMPLETED') {
    return; // 不允许选择
  }
  
  const index = selectedStudentIds.value.indexOf(studentId);
  if (index > -1) {
    selectedStudentIds.value.splice(index, 1);
  } else {
    selectedStudentIds.value.push(studentId);
  }
};

// 检查学生是否已选中
const isStudentSelected = (studentId: number): boolean => {
  return selectedStudentIds.value.includes(studentId);
};

// 添加成员（批量邀请学生）
const handleAddMember = async () => {
  if (selectedStudentIds.value.length === 0) {
    alert("请至少选择一个学生");
    return;
  }

  if (!courseGroup.value) {
    alert("课程组信息不存在");
    return;
  }

  adding.value = true;
  let successCount = 0;
  let failCount = 0;
  const errors: string[] = [];

  try {
    // 批量邀请选中的学生
    for (const studentId of selectedStudentIds.value) {
      try {
        const result = await teacherApi.inviteStudent({
          groupId: courseGroup.value.id,
          studentId: studentId
        });

        if (result.code === 200 && result.data) {
          successCount++;
        } else {
          failCount++;
          errors.push(result.message || `学生ID ${studentId} 邀请失败`);
        }
      } catch (error: any) {
        failCount++;
        const message = error?.response?.data?.message ?? error?.message ?? `学生ID ${studentId} 邀请失败`;
        errors.push(message);
      }
    }

    // 显示结果
    if (successCount > 0) {
      alert(`成功邀请 ${successCount} 名学生${failCount > 0 ? `，失败 ${failCount} 名` : ''}`);
      if (errors.length > 0) {
        console.error("邀请错误:", errors);
      }
    } else {
      alert(`邀请失败：${errors.join('; ')}`);
    }

    // 刷新学生列表
    await loadStudents(courseGroup.value.id);
    
    // 重置表单
    showInviteModal.value = false;
    selectedStudentIds.value = [];
    filterKeyword.value = "";
  } catch (error: any) {
    console.error("批量邀请学生异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "邀请学生失败";
    alert(message);
  } finally {
    adding.value = false;
  }
};

// 打开添加成员弹窗时，加载所有学生列表
const openInviteModal = async () => {
  showInviteModal.value = true;
  selectedStudentIds.value = [];
  filterKeyword.value = "";
  
  // 确保学生状态映射是最新的
  if (courseGroup.value) {
    await loadStudents(courseGroup.value.id);
  }
  
  // 加载所有学生列表
  if (allStudents.value.length === 0) {
    await loadAllStudents();
  }
};

// 打开打分弹窗
const openGradeModal = (member: any) => {
  currentGradeMember.value = {
    id: member.id,
    userId: member.userId,
    relationId: member.relationId,
    userName: member.user?.name || '未知',
    currentGrade: member.grade
  };
  gradeForm.value.grade = member.grade || null;
  showGradeModal.value = true;
};

// 提交打分
const handleGrade = async () => {
  if (gradeForm.value.grade === null || gradeForm.value.grade === undefined) {
    alert("请输入成绩");
    return;
  }

  if (gradeForm.value.grade < 0 || gradeForm.value.grade > 100) {
    alert("成绩范围应为 0-100");
    return;
  }

  if (!currentGradeMember.value || !courseGroup.value) {
    alert("信息不完整");
    return;
  }

  grading.value = true;
  try {
    const result = await teacherApi.gradeStudent(
      courseGroup.value.id,
      parseInt(currentGradeMember.value.userId),
      gradeForm.value.grade
    );

    if (result.code === 200 && result.data) {
      alert(result.message || "打分成功");
      showGradeModal.value = false;
      currentGradeMember.value = null;
      gradeForm.value.grade = null;
      // 刷新学生列表
      await loadStudents(courseGroup.value.id);
    } else {
      alert(result.message || "打分失败");
    }
  } catch (error: any) {
    console.error("打分异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "打分失败";
    alert(message);
  } finally {
    grading.value = false;
  }
};

// 移除成员
const removeMember = async (relationId: string, studentId: number) => {
  if (!confirm('确定将该成员从课程中移除吗？')) {
    return;
  }

  if (!courseGroup.value) {
    alert("课程组信息不存在");
    return;
  }

  try {
    const result = await teacherApi.removeStudentFromGroup(courseGroup.value.id, studentId);
    if (result.code === 200) {
      alert(result.message || "移除成功");
      // 刷新学生列表
      await loadStudents(courseGroup.value.id);
    } else {
      alert(result.message || "移除失败");
    }
  } catch (error: any) {
    console.error("移除成员异常:", error);
    const message = error?.response?.data?.message ?? error?.message ?? "移除成员失败";
    alert(message);
  }
};

onMounted(() => {
  loadCourseGroup();
});
</script>

<style scoped>
.course-detail-page {
  padding-bottom: 80px;
}

.page-top-bar {
  margin-bottom: 40px;
}

.btn-back {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  color: #64748b;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0;
  margin-bottom: 24px;
  transition: color 0.2s;
}

.btn-back:hover {
  color: #1e293b;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.course-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 0.7rem;
  font-weight: 800;
  margin-bottom: 12px;
}

.course-badge.teacher { background: #eff6ff; color: #2563eb; }
.course-badge.student { background: #f0fdf4; color: #16a34a; }

.course-title {
  font-size: 2.2rem;
  font-weight: 800;
  color: #1e293b;
}

.btn-add-member {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #2563eb;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-member:hover {
  background: #1d4ed8;
  transform: translateY(-2px);
}

/* Sections */
.detail-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.section-card {
  background: white;
  border-radius: 24px;
  border: 1px solid #f1f5f9;
  padding: 32px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.header-icon {
  width: 40px;
  height: 40px;
  background: #f8fafc;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 800;
  color: #1e293b;
}

.course-desc {
  font-size: 1rem;
  color: #475569;
  line-height: 1.8;
  margin-bottom: 32px;
}

.quick-stats {
  display: flex;
  gap: 60px;
  border-top: 1px solid #f8fafc;
  padding-top: 24px;
}

.q-stat {
  display: flex;
  flex-direction: column;
}

.q-val {
  font-size: 1.25rem;
  font-weight: 800;
  color: #1e293b;
}

.q-label {
  font-size: 0.7rem;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Table Design */
.table-frame {
  margin: 0 -32px; /* Pull out of padding */
  overflow-x: auto;
}

.academic-table {
  width: 100%;
  border-collapse: collapse;
}

.academic-table th {
  background: #f8fafc;
  padding: 16px 32px;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
}

.academic-table td {
  padding: 20px 32px;
  border-bottom: 1px solid #f8fafc;
  font-size: 0.9rem;
  color: #475569;
}

.cell-user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.mini-avatar {
  width: 32px;
  height: 32px;
  background: #334155;
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 0.8rem;
}

.user-name {
  font-weight: 700;
  color: #1e293b;
}

.cell-id { color: #64748b; }
.cell-date { color: #94a3b8; }

.role-pill {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 700;
}

.role-pill.student { background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; }
.role-pill.teacher { background: #fff7ed; color: #ea580c; border: 1px solid #ffedd5; }

.cell-actions {
  text-align: right;
  padding-right: 32px;
}

.action-buttons {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

.btn-grade {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #16a34a;
  cursor: pointer;
  transition: all 0.2s;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
}

.btn-grade:hover {
  background: #dcfce7;
  border-color: #86efac;
  color: #15803d;
}

.btn-delete {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  transition: all 0.2s;
  padding: 6px;
  border-radius: 8px;
}

.btn-delete:hover {
  color: #ef4444;
  background: #fef2f2;
}

/* Loading */
.loading-state {
  text-align: center;
  padding: 120px 0;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #f1f5f9;
  border-top-color: #2563eb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* Modals */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.modal-box {
  background: white;
  width: 100%;
  max-width: 480px;
  border-radius: 16px;
  padding: 40px;
  border: 1px solid #cbd5e1;
}

.modal-box-large {
  max-width: 700px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.modal-header h2 { font-size: 1.25rem; font-weight: 800; color: #1e293b; }

.btn-close {
  background: #f1f5f9;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-group { margin-bottom: 24px; }
.input-group label { display: block; font-size: 0.85rem; font-weight: 700; color: #475569; margin-bottom: 8px; }
.input-group .required { color: #ef4444; }
.input-tip { font-size: 0.8rem; color: #94a3b8; margin-top: 6px; }

/* 课程资料区域 */
.materials-section {
  margin-top: 32px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-add-material {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #2563eb;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-material:hover {
  background: #1d4ed8;
}

.materials-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.material-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.2s;
}

.material-item:hover {
  border-color: #cbd5e1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.material-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  flex-shrink: 0;
}

.material-icon.article {
  background: #eff6ff;
  color: #2563eb;
}

.material-icon.slide {
  background: #fef3c7;
  color: #f59e0b;
}

.material-icon.file {
  background: #f0fdf4;
  color: #16a34a;
}

.material-icon.link {
  background: #f3e8ff;
  color: #9333ea;
}

.material-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.material-title {
  font-weight: 700;
  color: #1e293b;
  font-size: 1rem;
}

.material-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 0.85rem;
}

.material-type-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.8rem;
}

.material-type-badge.article {
  background: #dbeafe;
  color: #1e40af;
}

.material-type-badge.slide {
  background: #fef3c7;
  color: #92400e;
}

.material-type-badge.file {
  background: #dcfce7;
  color: #166534;
}

.material-type-badge.link {
  background: #f3e8ff;
  color: #6b21a8;
}

.material-date {
  color: #94a3b8;
}

.material-link {
  margin-top: 4px;
}

.material-link .link-text {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #2563eb;
  text-decoration: none;
  font-size: 0.85rem;
  word-break: break-all;
  transition: color 0.2s;
}

.material-link .link-text:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

.material-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-view {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  color: #2563eb;
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-view:hover {
  background: #dbeafe;
  border-color: #93c5fd;
  color: #1d4ed8;
}

.btn-edit {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  color: #16a34a;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit:hover {
  background: #dcfce7;
  border-color: #86efac;
}

.empty-state {
  text-align: center;
  padding: 48px 24px;
  color: #94a3b8;
}

.empty-hint {
  font-size: 0.85rem;
  margin-top: 8px;
}

/* 文件上传区域 */
.file-upload-area {
  position: relative;
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  background: #f8fafc;
  transition: all 0.2s;
}

.file-upload-area:hover {
  border-color: #94a3b8;
  background: #f1f5f9;
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.file-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #1e293b;
  font-weight: 600;
}

.file-size {
  font-size: 0.85rem;
  color: #64748b;
  font-weight: normal;
}

.file-placeholder {
  color: #94a3b8;
  font-size: 0.9rem;
}

.modern-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.2s;
}

.modern-textarea:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.material-modal {
  max-width: 600px;
}

.modern-input, .modern-select {
  width: 100%;
  padding: 12px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  font-size: 0.95rem;
}

.modal-footer { display: flex; gap: 12px; margin-top: 32px; }
.btn-cancel { flex: 1; padding: 12px; border-radius: 12px; font-weight: 700; background: white; border: 1px solid #e2e8f0; color: #64748b; cursor: pointer; }
.btn-submit { flex: 1; padding: 12px; border-radius: 12px; font-weight: 700; background: #2563eb; color: white; border: none; cursor: pointer; }

/* 课程信息样式 */
.course-info {
  margin-bottom: 32px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 0.95rem;
}

.info-label {
  font-weight: 700;
  color: #64748b;
  min-width: 100px;
}

.info-value {
  color: #1e293b;
  font-weight: 600;
}

/* 状态标签样式 */
.status-pill {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 700;
}

.status-pill.enrolled {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.status-pill.enrolling {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #fde68a;
}

.status-pill.completed {
  background: #dbeafe;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}

.status-pill.dropped {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.cell-grade {
  font-weight: 600;
  color: #1e293b;
}

/* 搜索学生结果样式 */
.search-results {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
}

.student-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s;
}

.student-item:hover {
  background: white;
}

.student-item.selected {
  background: #eff6ff;
  border-color: #3b82f6;
}

.student-item:last-child {
  border-bottom: none;
}

.student-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.student-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 0.95rem;
}

.student-id {
  font-size: 0.85rem;
  color: #64748b;
  font-family: monospace;
}

.check-icon {
  color: #2563eb;
  font-weight: 800;
  font-size: 1.2rem;
}

/* 学生选择下拉框样式 */
.student-select-container {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
}

.student-select-list {
  display: flex;
  flex-direction: column;
}

.student-select-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.student-select-item:hover:not(.disabled) {
  background: #f1f5f9;
}

.student-select-item.selected:not(.disabled) {
  background: #eff6ff;
  border-color: #3b82f6;
}

.student-select-item.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: #f8fafc;
}

.student-select-item:last-child {
  border-bottom: none;
}

/* 资料详细模态框样式 */
.material-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row label {
  font-size: 0.85rem;
  font-weight: 700;
  color: #475569;
}

.detail-value {
  font-size: 0.95rem;
  color: #1e293b;
  word-break: break-word;
}

.detail-content {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.detail-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  font-size: 0.9rem;
  line-height: 1.6;
}

.detail-value .link-text {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #2563eb;
  text-decoration: none;
  word-break: break-all;
  transition: color 0.2s;
}

.detail-value .link-text:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

.student-select-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.student-select-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 0.95rem;
}

.student-select-id {
  font-size: 0.85rem;
  color: #64748b;
  font-family: monospace;
}

.student-select-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 700;
  white-space: nowrap;
}

.status-badge.status-enrolling {
  background: #fef3c7;
  color: #d97706;
  border: 1px solid #fde68a;
}

.status-badge.status-enrolled {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}

.status-badge.status-dropped {
  background: #fee2e2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.status-badge.status-completed {
  background: #dbeafe;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}

.status-badge.status-none {
  background: #f8fafc;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.loading-text,
.empty-text {
  padding: 40px 20px;
  text-align: center;
  color: #94a3b8;
  font-size: 0.9rem;
}

/* 班级管理区域样式 */
.classes-section {
  margin-top: 32px;
}

.btn-add-class {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #2563eb;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add-class:hover {
  background: #1d4ed8;
}

.classes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.class-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.2s;
}

.class-item:hover {
  border-color: #cbd5e1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.class-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 10px;
  flex-shrink: 0;
}

.class-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.class-title {
  font-weight: 700;
  color: #1e293b;
  font-size: 1rem;
}

.class-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 0.85rem;
}

.class-code {
  color: #64748b;
}

.class-status {
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.8rem;
}

.class-status.active {
  background: #dcfce7;
  color: #166534;
}

.class-status.inactive {
  background: #fee2e2;
  color: #991b1b;
}

.class-description {
  font-size: 0.85rem;
  color: #64748b;
  margin-top: 4px;
}

.class-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-enter-class {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  color: #2563eb;
  border-radius: 8px;
  padding: 6px 12px;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-enter-class:hover {
  background: #dbeafe;
  border-color: #93c5fd;
  color: #1d4ed8;
}
</style>
