package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.StudentCourseRelation;
import java.util.List;

/**
 * 学生选课关系Mapper接口
 */
@Mapper
public interface StudentCourseRelationMapper {
    
    /**
     * 根据ID查询关系
     * @param id 关系ID
     * @return 关系信息
     */
    StudentCourseRelation selectById(@Param("id") Long id);
    
    /**
     * 根据学生ID和课程组ID查询关系
     * @param studentId 学生ID
     * @param groupId 课程组ID
     * @return 关系信息
     */
    StudentCourseRelation selectByStudentIdAndGroupId(@Param("studentId") Long studentId, @Param("groupId") Long groupId);
    
    /**
     * 根据学生ID查询选课列表
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @return 选课列表
     */
    List<StudentCourseRelation> selectByStudentId(@Param("studentId") Long studentId, @Param("semester") String semester);
    
    /**
     * 根据课程组ID查询学生列表
     * @param groupId 课程组ID
     * @return 学生选课列表
     */
    List<StudentCourseRelation> selectByGroupId(@Param("groupId") Long groupId);
    
    /**
     * 根据课程组ID和状态查询学生列表
     * @param groupId 课程组ID
     * @param enrollStatus 选课状态（可选）
     * @return 学生选课列表
     */
    List<StudentCourseRelation> selectByGroupIdAndStatus(@Param("groupId") Long groupId, @Param("enrollStatus") String enrollStatus);
    
    /**
     * 插入新关系
     * @param relation 关系信息
     * @return 影响的行数
     */
    int insert(StudentCourseRelation relation);
    
    /**
     * 更新关系信息
     * @param relation 关系信息
     * @return 影响的行数
     */
    int update(StudentCourseRelation relation);
    
    /**
     * 根据ID删除关系
     * @param id 关系ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据学生ID和课程组ID删除关系
     * @param studentId 学生ID
     * @param groupId 课程组ID
     * @return 影响的行数
     */
    int deleteByStudentIdAndGroupId(@Param("studentId") Long studentId, @Param("groupId") Long groupId);
}


