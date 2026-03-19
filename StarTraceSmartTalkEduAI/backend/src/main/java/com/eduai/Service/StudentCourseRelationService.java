package com.eduai.Service;

import com.eduai.pojo.dto.InviteStudentDTO;
import com.eduai.pojo.entity.StudentCourseRelation;
import java.math.BigDecimal;
import java.util.List;

/**
 * 学生选课关系服务接口
 */
public interface StudentCourseRelationService {
    
    /**
     * 邀请学生加入课程
     * @param inviteDTO 邀请DTO
     * @return 创建的关系信息
     */
    StudentCourseRelation inviteStudent(InviteStudentDTO inviteDTO);
    
    /**
     * 学生选课
     * @param studentId 学生ID
     * @param groupId 课程组ID
     * @return 创建的关系信息
     */
    StudentCourseRelation enrollCourse(Long studentId, Long groupId);
    
    /**
     * 学生退课
     * @param studentId 学生ID
     * @param groupId 课程组ID
     */
    void dropCourse(Long studentId, Long groupId);
    
    /**
     * 根据学生ID查询选课列表
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @return 选课列表
     */
    List<StudentCourseRelation> getCoursesByStudentId(Long studentId, String semester);
    
    /**
     * 根据课程组ID查询学生列表
     * @param groupId 课程组ID
     * @return 学生选课列表
     */
    List<StudentCourseRelation> getStudentsByGroupId(Long groupId);
    
    /**
     * 检查学生是否已选课
     * @param studentId 学生ID
     * @param groupId 课程组ID
     * @return 是否已选课
     */
    boolean checkEnrolled(Long studentId, Long groupId);

    /**
     * 老师给已选课学生打分，并将状态置为 COMPLETED
     * @param groupId 课程组ID
     * @param studentId 学生ID
     * @param grade 成绩
     * @return 更新后的关系信息
     */
    StudentCourseRelation gradeStudent(Long groupId, Long studentId, BigDecimal grade);

    /**
     * 老师将学生从课程中移除（物理删除选课关系）
     * @param groupId 课程组ID
     * @param studentId 学生ID
     */
    void removeStudentFromGroup(Long groupId, Long studentId);
    
    /**
     * 根据课程组ID和状态查询学生列表
     * @param groupId 课程组ID
     * @param enrollStatus 选课状态（可选）
     * @return 学生选课列表
     */
    List<StudentCourseRelation> getStudentsByGroupIdAndStatus(Long groupId, String enrollStatus);
    
    /**
     * 老师审核学生选课申请（通过）
     * @param groupId 课程组ID
     * @param studentId 学生ID
     * @return 更新后的关系信息
     */
    StudentCourseRelation approveEnrollment(Long groupId, Long studentId);
    
    /**
     * 老师审核学生选课申请（拒绝）
     * @param groupId 课程组ID
     * @param studentId 学生ID
     */
    void rejectEnrollment(Long groupId, Long studentId);
}

