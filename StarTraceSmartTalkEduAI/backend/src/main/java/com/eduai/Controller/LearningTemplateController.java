package com.eduai.Controller;

import com.eduai.Service.LearningTemplateService;
import com.eduai.pojo.Result;
import com.eduai.pojo.dto.CreateLearningTemplateDTO;
import com.eduai.pojo.entity.LearningTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习模板控制器
 */
@RestController
@RequestMapping("/api/template")
public class LearningTemplateController {
    
    @Autowired
    private LearningTemplateService learningTemplateService;
    
    /**
     * 创建学习模板
     * @param dto 模板信息DTO
     * @return 创建的模板信息
     */
    @PostMapping("/create")
    public Result<LearningTemplate> createTemplate(@RequestBody CreateLearningTemplateDTO dto) {
        try {
            LearningTemplate template = learningTemplateService.createTemplate(dto);
            return Result.success("创建成功", template);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取模板详情
     * @param id 模板ID
     * @return 模板信息
     */
    @GetMapping("/{id}")
    public Result<LearningTemplate> getTemplateById(@PathVariable Long id) {
        try {
            LearningTemplate template = learningTemplateService.getTemplateById(id);
            if (template == null) {
                return Result.error("模板不存在");
            }
            return Result.success(template);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据教师ID获取模板列表
     * @param teacherId 教师ID
     * @return 模板列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<LearningTemplate>> getTemplatesByTeacherId(@PathVariable Long teacherId) {
        try {
            List<LearningTemplate> templates = learningTemplateService.getTemplatesByTeacherId(teacherId);
            return Result.success(templates);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据课程ID获取模板列表
     * @param courseId 课程ID
     * @return 模板列表
     */
    @GetMapping("/course/{courseId}")
    public Result<List<LearningTemplate>> getTemplatesByCourseId(@PathVariable Long courseId) {
        try {
            List<LearningTemplate> templates = learningTemplateService.getTemplatesByCourseId(courseId);
            return Result.success(templates);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据班级ID获取模板列表
     * @param classId 班级ID
     * @return 模板列表
     */
    @GetMapping("/class/{classId}")
    public Result<List<LearningTemplate>> getTemplatesByClassId(@PathVariable Long classId) {
        try {
            List<LearningTemplate> templates = learningTemplateService.getTemplatesByClassId(classId);
            return Result.success(templates);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据教师ID和课程ID获取模板列表
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @return 模板列表
     */
    @GetMapping("/teacher/{teacherId}/course/{courseId}")
    public Result<List<LearningTemplate>> getTemplatesByTeacherIdAndCourseId(
            @PathVariable Long teacherId,
            @PathVariable Long courseId) {
        try {
            List<LearningTemplate> templates = learningTemplateService.getTemplatesByTeacherIdAndCourseId(teacherId, courseId);
            return Result.success(templates);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有模板
     * @return 模板列表
     */
    @GetMapping("/list")
    public Result<List<LearningTemplate>> getAllTemplates() {
        try {
            List<LearningTemplate> templates = learningTemplateService.getAllTemplates();
            return Result.success(templates);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新模板信息
     * @param template 模板信息
     * @return 更新后的模板信息
     */
    @PutMapping("/update")
    public Result<LearningTemplate> updateTemplate(@RequestBody LearningTemplate template) {
        try {
            LearningTemplate updated = learningTemplateService.updateTemplate(template);
            return Result.success("更新成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除模板
     * @param id 模板ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        try {
            learningTemplateService.deleteTemplate(id);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 增加下载量（学生下载模板时调用）
     * @param id 模板ID
     * @return 操作结果
     */
    @PostMapping("/{id}/download")
    public Result<Void> incrementDownloadCount(@PathVariable Long id) {
        try {
            learningTemplateService.incrementDownloadCount(id);
            return Result.success("下载成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
}

