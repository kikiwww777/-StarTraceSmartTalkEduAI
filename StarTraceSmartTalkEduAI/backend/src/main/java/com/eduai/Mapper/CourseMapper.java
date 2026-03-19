package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.Course;
import java.util.List;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper {
    
    /**
     * 根据ID查询课程
     * @param id 课程ID
     * @return 课程信息
     */
    Course selectById(@Param("id") Long id);
    
    /**
     * 根据课程代码查询课程
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Course selectByCourseCode(@Param("courseCode") String courseCode);
    
    /**
     * 查询所有课程
     * @return 课程列表
     */
    List<Course> selectAll();
    
    /**
     * 根据名称搜索课程
     * @param name 课程名称（模糊匹配）
     * @return 课程列表
     */
    List<Course> searchByName(@Param("name") String name);
    
    /**
     * 插入新课程
     * @param course 课程信息
     * @return 影响的行数
     */
    int insert(Course course);
    
    /**
     * 更新课程信息
     * @param course 课程信息
     * @return 影响的行数
     */
    int update(Course course);
    
    /**
     * 根据ID删除课程
     * @param id 课程ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
}



