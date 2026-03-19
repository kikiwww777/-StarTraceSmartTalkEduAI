package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.CourseGroup;
import java.util.List;

/**
 * 课程组Mapper接口
 */
@Mapper
public interface CourseGroupMapper {
    
    /**
     * 根据ID查询课程组
     * @param id 课程组ID
     * @return 课程组信息
     */
    CourseGroup selectById(@Param("id") Long id);
    
    /**
     * 根据教师ID查询课程组列表
     * @param teacherId 教师ID
     * @param semester 学期（可选）
     * @return 课程组列表
     */
    List<CourseGroup> selectByTeacherId(@Param("teacherId") Long teacherId, @Param("semester") String semester);
    
    /**
     * 根据学期查询课程组列表
     * @param semester 学期
     * @return 课程组列表
     */
    List<CourseGroup> selectBySemester(@Param("semester") String semester);
    
    /**
     * 根据课程ID查询课程组列表
     * @param courseId 课程ID
     * @return 课程组列表
     */
    List<CourseGroup> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询某教师开设某课程的“最新”课程组
     * 用于班级详情中拼接 courseGroup（学期/容量等），避免在 Java 侧遍历筛选导致错误
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return 最新课程组（可能为 null）
     */
    CourseGroup selectLatestByCourseIdAndTeacherId(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);
    
    /**
     * 插入新课程组
     * @param courseGroup 课程组信息
     * @return 影响的行数
     */
    int insert(CourseGroup courseGroup);
    
    /**
     * 更新课程组信息
     * @param courseGroup 课程组信息
     * @return 影响的行数
     */
    int update(CourseGroup courseGroup);
    
    /**
     * 根据ID删除课程组
     * @param id 课程组ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新当前学生数
     * @param id 课程组ID
     * @param currentStudents 当前学生数
     * @return 影响的行数
     */
    int updateCurrentStudents(@Param("id") Long id, @Param("currentStudents") Integer currentStudents);
}



