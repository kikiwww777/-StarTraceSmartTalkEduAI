package com.eduai.Controller;

import com.eduai.Mapper.UserMapper;
import com.eduai.Service.StudentCourseRelationService;
import com.eduai.pojo.Result;
import com.eduai.pojo.entity.StudentCourseRelation;
import com.eduai.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生控制器
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private StudentCourseRelationService studentCourseRelationService;
    
    /**
     * 搜索学生（用于邀请）
     * @param keyword 关键词（学号或姓名）
     * @param subject 学科（可选，用于筛选）
     * @return 学生列表
     */
    @GetMapping("/search")
    public Result<List<User>> searchStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String subject) {
        try {
            List<User> students = userMapper.selectByRole("student");
            if (students != null) {
                students.forEach(s -> s.setPassword(null));

                // 学科筛选（如果提供）
                if (subject != null && !subject.trim().isEmpty()) {
                    String subjectTrim = subject.trim();
                    students = students.stream()
                            .filter(s -> subjectTrim.equals(s.getSubject()))
                            .collect(java.util.stream.Collectors.toList());
                }

                // 关键词筛选（如果提供）
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String lowerKeyword = keyword.trim().toLowerCase();
                    students = students.stream()
                            .filter(s -> (s.getStudentId() != null && s.getStudentId().toLowerCase().contains(lowerKeyword))
                                    || (s.getName() != null && s.getName().toLowerCase().contains(lowerKeyword)))
                            .collect(java.util.stream.Collectors.toList());
                }
            }
            return Result.success(students);
        } catch (Exception e) {
            return Result.error("搜索失败：" + e.getMessage());
        }
    }
    
    /**
     * 学生选课
     * @param groupId 课程组ID
     * @param studentId 学生ID（可选，从token中获取）
     * @return 创建的关系信息
     */
    @PostMapping("/course/enroll")
    public Result<StudentCourseRelation> enrollCourse(
            @RequestParam Long groupId,
            @RequestParam(required = false) Long studentId) {
        try {
            if (studentId == null) {
                return Result.error("学生ID不能为空");
            }
            StudentCourseRelation relation = studentCourseRelationService.enrollCourse(studentId, groupId);
            return Result.success("选课成功", relation);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("选课失败：" + e.getMessage());
        }
    }
    
    /**
     * 学生退课
     * @param studentId 学生ID
     * @param groupId 课程组ID
     * @return 退课结果
     */
    @PostMapping("/course/drop")
    public Result<Void> dropCourse(
            @RequestParam Long studentId,
            @RequestParam Long groupId) {
        try {
            studentCourseRelationService.dropCourse(studentId, groupId);
            return Result.success("退课成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("退课失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询学生的选课列表
     * @param studentId 学生ID
     * @param semester 学期（可选）
     * @return 选课列表
     */
    @GetMapping("/course/list")
    public Result<List<StudentCourseRelation>> getCourseList(
            @RequestParam Long studentId,
            @RequestParam(required = false) String semester) {
        try {
            List<StudentCourseRelation> relations = studentCourseRelationService.getCoursesByStudentId(studentId, semester);
            return Result.success(relations);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查学生是否已选课
     * @param studentId 学生ID
     * @param groupId 课程组ID
     * @return 是否已选课
     */
    @GetMapping("/course/check")
    public Result<Boolean> checkEnrolled(
            @RequestParam Long studentId,
            @RequestParam Long groupId) {
        try {
            boolean enrolled = studentCourseRelationService.checkEnrolled(studentId, groupId);
            return Result.success(enrolled);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}

