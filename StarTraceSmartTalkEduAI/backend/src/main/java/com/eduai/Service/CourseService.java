package com.eduai.Service;

import com.eduai.pojo.entity.Course;
import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {
    
    /**
     * 根据ID查询课程
     * @param id 课程ID
     * @return 课程信息
     */
    Course getCourseById(Long id);
    
    /**
     * 查询所有课程
     * @return 课程列表
     */
    List<Course> getAllCourses();
    
    /**
     * 根据名称搜索课程
     * @param name 课程名称
     * @return 课程列表
     */
    List<Course> searchCourses(String name);
    
    /**
     * 创建课程
     * @param course 课程信息
     * @return 创建的课程信息
     */
    Course createCourse(Course course);
    
    /**
     * 更新课程
     * @param course 课程信息
     * @return 更新后的课程信息
     */
    Course updateCourse(Course course);
    
    /**
     * 删除课程
     * @param id 课程ID
     */
    void deleteCourse(Long id);
}



