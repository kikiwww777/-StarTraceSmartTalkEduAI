package com.eduai.Service.impl;

import com.eduai.Mapper.CourseGroupMapper;
import com.eduai.Mapper.CourseMapper;
import com.eduai.Mapper.StudentCourseRelationMapper;
import com.eduai.Mapper.UserMapper;
import com.eduai.Service.StudentCourseRelationService;
import com.eduai.pojo.dto.InviteStudentDTO;
import com.eduai.pojo.entity.Course;
import com.eduai.pojo.entity.CourseGroup;
import com.eduai.pojo.entity.StudentCourseRelation;
import com.eduai.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生选课关系服务实现类
 */
@Service
public class StudentCourseRelationServiceImpl implements StudentCourseRelationService {
    
    @Autowired
    private StudentCourseRelationMapper studentCourseRelationMapper;
    
    @Autowired
    private CourseGroupMapper courseGroupMapper;

    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public StudentCourseRelation inviteStudent(InviteStudentDTO inviteDTO) {
        if (inviteDTO == null) {
            throw new RuntimeException("邀请信息不能为空");
        }
        
        if (inviteDTO.getGroupId() == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        
        if (inviteDTO.getStudentId() == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        
        // 检查课程组是否存在
        CourseGroup courseGroup = courseGroupMapper.selectById(inviteDTO.getGroupId());
        if (courseGroup == null) {
            throw new RuntimeException("课程组不存在");
        }
        
        // 检查学生是否存在
        User student = userMapper.selectById(inviteDTO.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // 检查是否已选课
        StudentCourseRelation existingRelation = studentCourseRelationMapper.selectByStudentIdAndGroupId(
            inviteDTO.getStudentId(), inviteDTO.getGroupId());
        if (existingRelation != null) {
            if ("ENROLLED".equals(existingRelation.getEnrollStatus())) {
                throw new RuntimeException("学生已选此课程");
            }
            // 如果之前退过课，重新选课
            existingRelation.setEnrollStatus("ENROLLED");
            existingRelation.setUpdatedAt(LocalDateTime.now());
            studentCourseRelationMapper.update(existingRelation);
            
            // 更新课程组当前学生数
            updateCurrentStudentsCount(inviteDTO.getGroupId());
            
            return existingRelation;
        }
        
        // 检查是否超过最大学生数
        if (courseGroup.getCurrentStudents() >= courseGroup.getMaxStudents()) {
            throw new RuntimeException("课程已满，无法邀请更多学生");
        }
        
        // 创建选课关系
        StudentCourseRelation relation = new StudentCourseRelation();
        relation.setStudentId(inviteDTO.getStudentId());
        relation.setGroupId(inviteDTO.getGroupId());
        relation.setEnrollStatus("ENROLLED");
        
        LocalDateTime now = LocalDateTime.now();
        relation.setCreatedAt(now);
        relation.setUpdatedAt(now);
        
        int result = studentCourseRelationMapper.insert(relation);
        if (result <= 0) {
            throw new RuntimeException("邀请学生失败");
        }
        
        // 更新课程组当前学生数
        updateCurrentStudentsCount(inviteDTO.getGroupId());
        
        // 加载关联信息
        relation.setCourseGroup(courseGroup);
        student.setPassword(null);
        relation.setStudent(student);
        
        return relation;
    }
    
    @Override
    @Transactional
    public StudentCourseRelation enrollCourse(Long studentId, Long groupId) {
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        
        if (groupId == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        
        // 检查课程组是否存在
        CourseGroup courseGroup = courseGroupMapper.selectById(groupId);
        if (courseGroup == null) {
            throw new RuntimeException("课程组不存在");
        }
        
        // 检查学生是否存在
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // 检查是否已存在选课记录
        StudentCourseRelation existingRelation = studentCourseRelationMapper.selectByStudentIdAndGroupId(studentId, groupId);
        if (existingRelation != null) {
            if ("ENROLLING".equals(existingRelation.getEnrollStatus())) {
                throw new RuntimeException("选课申请已提交，等待审核");
            }
            if ("ENROLLED".equals(existingRelation.getEnrollStatus())) {
                throw new RuntimeException("学生已选此课程");
            }
            // 如果之前退过课，重新申请
            existingRelation.setEnrollStatus("ENROLLING");
            existingRelation.setUpdatedAt(LocalDateTime.now());
            studentCourseRelationMapper.update(existingRelation);
            
            // 加载关联信息
            existingRelation.setCourseGroup(courseGroup);
            student.setPassword(null);
            existingRelation.setStudent(student);
            
            return existingRelation;
        }
        
        // 检查是否超过最大学生数（申请时暂不检查，审核时再检查）
        
        // 创建选课申请（状态为ENROLLING）
        StudentCourseRelation relation = new StudentCourseRelation();
        relation.setStudentId(studentId);
        relation.setGroupId(groupId);
        relation.setEnrollStatus("ENROLLING");  // 申请中状态
        
        LocalDateTime now = LocalDateTime.now();
        relation.setCreatedAt(now);
        relation.setUpdatedAt(now);
        
        int result = studentCourseRelationMapper.insert(relation);
        if (result <= 0) {
            throw new RuntimeException("选课申请失败");
        }
        
        // 加载关联信息
        relation.setCourseGroup(courseGroup);
        student.setPassword(null);
        relation.setStudent(student);
        
        return relation;
    }
    
    @Override
    @Transactional
    public void dropCourse(Long studentId, Long groupId) {
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        
        if (groupId == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        
        StudentCourseRelation relation = studentCourseRelationMapper.selectByStudentIdAndGroupId(studentId, groupId);
        if (relation == null) {
            throw new RuntimeException("选课记录不存在");
        }
        
        if ("DROPPED".equals(relation.getEnrollStatus())) {
            throw new RuntimeException("学生已退课");
        }
        
        // 更新状态为退课
        relation.setEnrollStatus("DROPPED");
        relation.setUpdatedAt(LocalDateTime.now());
        int result = studentCourseRelationMapper.update(relation);
        if (result <= 0) {
            throw new RuntimeException("退课失败");
        }
        
        // 更新课程组当前学生数
        updateCurrentStudentsCount(groupId);
    }
    
    @Override
    public List<StudentCourseRelation> getCoursesByStudentId(Long studentId, String semester) {
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        List<StudentCourseRelation> relations = studentCourseRelationMapper.selectByStudentId(studentId, semester);
        // 加载关联的课程组信息
        return relations.stream().map(relation -> {
            if (relation.getGroupId() != null) {
                CourseGroup courseGroup = courseGroupMapper.selectById(relation.getGroupId());
                if (courseGroup != null && courseGroup.getCourseId() != null) {
                    Course course = courseMapper.selectById(courseGroup.getCourseId());
                    courseGroup.setCourse(course);
                }
                if (courseGroup != null && courseGroup.getTeacherId() != null) {
                    User teacher = userMapper.selectById(courseGroup.getTeacherId());
                    if (teacher != null) {
                        teacher.setPassword(null);
                    }
                    courseGroup.setTeacher(teacher);
                }
                relation.setCourseGroup(courseGroup);
            }
            return relation;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<StudentCourseRelation> getStudentsByGroupId(Long groupId) {
        if (groupId == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        List<StudentCourseRelation> relations = studentCourseRelationMapper.selectByGroupId(groupId);
        // 加载关联的学生信息
        return relations.stream().map(relation -> {
            if (relation.getStudentId() != null) {
                User student = userMapper.selectById(relation.getStudentId());
                if (student != null) {
                    student.setPassword(null);
                }
                relation.setStudent(student);
            }
            if (relation.getGroupId() != null) {
                CourseGroup courseGroup = courseGroupMapper.selectById(relation.getGroupId());
                relation.setCourseGroup(courseGroup);
            }
            return relation;
        }).collect(Collectors.toList());
    }
    
    @Override
    public boolean checkEnrolled(Long studentId, Long groupId) {
        if (studentId == null || groupId == null) {
            return false;
        }
        StudentCourseRelation relation = studentCourseRelationMapper.selectByStudentIdAndGroupId(studentId, groupId);
        // 已选课或申请中都算作已选课（前端可能需要区分显示）
        return relation != null && ("ENROLLED".equals(relation.getEnrollStatus()) || "ENROLLING".equals(relation.getEnrollStatus()));
    }

    @Override
    @Transactional
    public StudentCourseRelation gradeStudent(Long groupId, Long studentId, BigDecimal grade) {
        if (groupId == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        if (grade == null) {
            throw new RuntimeException("成绩不能为空");
        }
        // 简单约束：0-100（如需百分制以外，可再调整）
        if (grade.compareTo(BigDecimal.ZERO) < 0 || grade.compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("成绩范围应为 0-100");
        }

        StudentCourseRelation relation = studentCourseRelationMapper.selectByStudentIdAndGroupId(studentId, groupId);
        if (relation == null) {
            throw new RuntimeException("选课记录不存在");
        }
        if (!"ENROLLED".equals(relation.getEnrollStatus())) {
            throw new RuntimeException("仅已选课状态的学生可打分");
        }

        relation.setGrade(grade);
        relation.setEnrollStatus("COMPLETED");
        relation.setUpdatedAt(LocalDateTime.now());
        int result = studentCourseRelationMapper.update(relation);
        if (result <= 0) {
            throw new RuntimeException("打分失败");
        }

        // COMPLETED 不计入当前学生数
        updateCurrentStudentsCount(groupId);

        // 回填关联信息（前端可能直接使用返回对象）
        User student = userMapper.selectById(studentId);
        if (student != null) {
            student.setPassword(null);
        }
        relation.setStudent(student);
        CourseGroup courseGroup = courseGroupMapper.selectById(groupId);
        relation.setCourseGroup(courseGroup);
        return relation;
    }

    @Override
    @Transactional
    public void removeStudentFromGroup(Long groupId, Long studentId) {
        if (groupId == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }

        StudentCourseRelation relation = studentCourseRelationMapper.selectByStudentIdAndGroupId(studentId, groupId);
        if (relation == null) {
            throw new RuntimeException("选课记录不存在");
        }

        int result = studentCourseRelationMapper.deleteByStudentIdAndGroupId(studentId, groupId);
        if (result <= 0) {
            throw new RuntimeException("移除失败");
        }

        updateCurrentStudentsCount(groupId);
    }
    
    @Override
    public List<StudentCourseRelation> getStudentsByGroupIdAndStatus(Long groupId, String enrollStatus) {
        if (groupId == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        List<StudentCourseRelation> relations = studentCourseRelationMapper.selectByGroupIdAndStatus(groupId, enrollStatus);
        // 加载关联的学生信息
        return relations.stream().map(relation -> {
            if (relation.getStudentId() != null) {
                User student = userMapper.selectById(relation.getStudentId());
                if (student != null) {
                    student.setPassword(null);
                }
                relation.setStudent(student);
            }
            if (relation.getGroupId() != null) {
                CourseGroup courseGroup = courseGroupMapper.selectById(relation.getGroupId());
                relation.setCourseGroup(courseGroup);
            }
            return relation;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public StudentCourseRelation approveEnrollment(Long groupId, Long studentId) {
        if (groupId == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        
        StudentCourseRelation relation = studentCourseRelationMapper.selectByStudentIdAndGroupId(studentId, groupId);
        if (relation == null) {
            throw new RuntimeException("选课记录不存在");
        }
        
        if (!"ENROLLING".equals(relation.getEnrollStatus())) {
            throw new RuntimeException("只能审核申请中的选课记录");
        }
        
        // 检查课程组是否存在
        CourseGroup courseGroup = courseGroupMapper.selectById(groupId);
        if (courseGroup == null) {
            throw new RuntimeException("课程组不存在");
        }
        
        // 检查是否超过最大学生数
        if (courseGroup.getCurrentStudents() >= courseGroup.getMaxStudents()) {
            throw new RuntimeException("课程已满，无法通过申请");
        }
        
        // 更新状态为已选课
        relation.setEnrollStatus("ENROLLED");
        relation.setUpdatedAt(LocalDateTime.now());
        int result = studentCourseRelationMapper.update(relation);
        if (result <= 0) {
            throw new RuntimeException("审核失败");
        }
        
        // 更新课程组当前学生数
        updateCurrentStudentsCount(groupId);
        
        // 加载关联信息
        User student = userMapper.selectById(studentId);
        if (student != null) {
            student.setPassword(null);
        }
        relation.setStudent(student);
        relation.setCourseGroup(courseGroup);
        
        return relation;
    }
    
    @Override
    @Transactional
    public void rejectEnrollment(Long groupId, Long studentId) {
        if (groupId == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        
        StudentCourseRelation relation = studentCourseRelationMapper.selectByStudentIdAndGroupId(studentId, groupId);
        if (relation == null) {
            throw new RuntimeException("选课记录不存在");
        }
        
        if (!"ENROLLING".equals(relation.getEnrollStatus())) {
            throw new RuntimeException("只能拒绝申请中的选课记录");
        }
        
        // 物理删除选课记录
        int result = studentCourseRelationMapper.deleteByStudentIdAndGroupId(studentId, groupId);
        if (result <= 0) {
            throw new RuntimeException("拒绝申请失败");
        }
    }
    
    /**
     * 更新课程组的当前学生数
     */
    private void updateCurrentStudentsCount(Long groupId) {
        List<StudentCourseRelation> relations = studentCourseRelationMapper.selectByGroupId(groupId);
        long enrolledCount = relations.stream()
            .filter(r -> "ENROLLED".equals(r.getEnrollStatus()))
            .count();
        courseGroupMapper.updateCurrentStudents(groupId, (int) enrolledCount);
    }
}

