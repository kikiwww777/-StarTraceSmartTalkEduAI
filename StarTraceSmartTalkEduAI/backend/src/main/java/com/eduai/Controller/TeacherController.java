package com.eduai.Controller;

import com.eduai.Mapper.UserMapper;
import com.eduai.pojo.Result;
import com.eduai.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取所有教师
     * @return 教师列表
     */
    @GetMapping("/list")
    public Result<List<User>> getAllTeachers() {
        try {
            List<User> teachers = userMapper.selectByRole("teacher");
            if (teachers != null) {
                teachers.forEach(t -> t.setPassword(null));
            }
            return Result.success(teachers);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据学科获取教师
     * @param subject 学科
     * @return 教师列表
     */
    @GetMapping("/subject/{subject}")
    public Result<List<User>> getTeachersBySubject(@PathVariable String subject) {
        try {
            List<User> teachers = userMapper.selectByRoleAndSubject("teacher", subject);
            if (teachers != null) {
                teachers.forEach(t -> t.setPassword(null));
            }
            return Result.success(teachers);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据课程ID获取匹配的教师
     * @param courseId 课程ID
     * @return 教师列表
     */
    @GetMapping("/by-course/{courseId}")
    public Result<List<User>> getTeachersByCourse(@PathVariable Long courseId) {
        try {
            // 这里可以根据课程信息匹配教师，暂时返回所有教师
            List<User> teachers = userMapper.selectByRole("teacher");
            if (teachers != null) {
                teachers.forEach(t -> t.setPassword(null));
            }
            return Result.success(teachers);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
}



