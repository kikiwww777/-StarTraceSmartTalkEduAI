package com.eduai.Service.impl;

import com.eduai.Mapper.CourseMapper;
import com.eduai.Service.CourseService;
import com.eduai.pojo.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Override
    public Course getCourseById(Long id) {
        if (id == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        return courseMapper.selectById(id);
    }
    
    @Override
    public List<Course> getAllCourses() {
        return courseMapper.selectAll();
    }
    
    @Override
    public List<Course> searchCourses(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllCourses();
        }
        return courseMapper.searchByName(name.trim());
    }
    
    @Override
    public Course createCourse(Course course) {
        if (course == null) {
            throw new RuntimeException("课程信息不能为空");
        }
        
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            throw new RuntimeException("课程代码不能为空");
        }
        
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new RuntimeException("课程名称不能为空");
        }
        
        // 检查课程代码是否已存在
        Course existingCourse = courseMapper.selectByCourseCode(course.getCourseCode().trim());
        if (existingCourse != null) {
            throw new RuntimeException("课程代码已存在");
        }
        
        // 设置默认值
        if (course.getCredit() == null) {
            course.setCredit(0);
        }
        if (course.getTotalHours() == null) {
            course.setTotalHours(0);
        }
        
        LocalDateTime now = LocalDateTime.now();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);
        
        int result = courseMapper.insert(course);
        if (result <= 0) {
            throw new RuntimeException("创建课程失败");
        }
        
        return course;
    }
    
    @Override
    public Course updateCourse(Course course) {
        if (course == null || course.getId() == null) {
            throw new RuntimeException("课程信息或ID不能为空");
        }
        
        Course existingCourse = courseMapper.selectById(course.getId());
        if (existingCourse == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 如果更新了课程代码，检查是否重复
        if (course.getCourseCode() != null && !course.getCourseCode().equals(existingCourse.getCourseCode())) {
            Course duplicateCourse = courseMapper.selectByCourseCode(course.getCourseCode().trim());
            if (duplicateCourse != null && !duplicateCourse.getId().equals(course.getId())) {
                throw new RuntimeException("课程代码已存在");
            }
        }
        
        course.setUpdatedAt(LocalDateTime.now());
        int result = courseMapper.update(course);
        if (result <= 0) {
            throw new RuntimeException("更新课程失败");
        }
        
        return courseMapper.selectById(course.getId());
    }
    
    @Override
    public void deleteCourse(Long id) {
        if (id == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        
        Course course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        int result = courseMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除课程失败");
        }
    }
}



