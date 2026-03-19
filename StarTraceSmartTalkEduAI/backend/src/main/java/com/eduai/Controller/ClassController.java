package com.eduai.Controller;

import com.eduai.Service.ClassService;
import com.eduai.pojo.Result;
import com.eduai.pojo.entity.Class;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级控制器
 */
@RestController
@RequestMapping("/api/class")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    /**
     * 创建班级
     * @param classInfo 班级信息
     * @return 创建的班级信息
     */
    @PostMapping("/create")
    public Result<Class> createClass(@RequestBody Class classInfo) {
        try {
            Class createdClass = classService.createClass(classInfo);
            return Result.success("创建成功", createdClass);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取班级详情（包含课程组信息，如开课时间、学期等）
     * @param id 班级ID
     * @return 班级信息
     */
    @GetMapping("/{id}")
    public Result<Class> getClassById(@PathVariable Long id) {
        try {
            Class classInfo = classService.getClassById(id);
            if (classInfo == null) {
                return Result.error("班级不存在");
            }
            return Result.success(classInfo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据教师ID获取班级列表
     * @param teacherId 教师ID
     * @return 班级列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Class>> getClassesByTeacherId(@PathVariable Long teacherId) {
        try {
            List<Class> classes = classService.getClassesByTeacherId(teacherId);
            return Result.success(classes);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据课程ID获取班级列表
     * @param courseId 课程ID
     * @return 班级列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<Class>> getClassesByCourseId(@PathVariable Long courseId) {
        try {
            List<Class> classes = classService.getClassesByCourseId(courseId);
            return Result.success(classes);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据课程ID + 教师ID获取班级列表
     * 解决“同一门课多个老师”时按课程ID查询拿错班级的问题
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return 班级列表
     */
    @GetMapping("/course/{courseId}/teacher/{teacherId}")
    public Result<List<Class>> getClassesByCourseIdAndTeacherId(
        @PathVariable Long courseId,
        @PathVariable Long teacherId
    ) {
        try {
            List<Class> classes = classService.getClassesByCourseIdAndTeacherId(courseId, teacherId);
            return Result.success(classes);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有班级
     * @return 班级列表
     */
    @GetMapping("/list")
    public Result<List<Class>> getAllClasses() {
        try {
            List<Class> classes = classService.getAllClasses();
            return Result.success(classes);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新班级信息
     * @param classInfo 班级信息
     * @return 更新后的班级信息
     */
    @PutMapping("/update")
    public Result<Class> updateClass(@RequestBody Class classInfo) {
        try {
            Class updatedClass = classService.updateClass(classInfo);
            return Result.success("更新成功", updatedClass);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除班级
     * @param id 班级ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteClass(@PathVariable Long id) {
        try {
            classService.deleteClass(id);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}

