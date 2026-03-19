package com.eduai.Controller;

import com.eduai.Service.TemplateService;
import com.eduai.pojo.Result;
import com.eduai.pojo.entity.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 教学模板控制器
 */
@RestController
@RequestMapping("/api/templates")
public class TemplateController {
    
    @Autowired
    private TemplateService templateService;
    
    /**
     * 根据ID获取模板详情
     * @param id 模板ID
     * @return 模板信息
     */
    @GetMapping("/{id}")
    public Result<Template> getTemplateById(@PathVariable String id) {
        try {
            Template template = templateService.getTemplateById(id);
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
     * 根据分类和关键词获取模板列表
     * @param category 分类ID（可选，默认为'all'）
     * @param keyword 搜索关键词（可选）
     * @return 模板列表
     */
    @GetMapping("/list")
    public Result<List<Template>> getTemplates(
            @RequestParam(required = false, defaultValue = "all") String category,
            @RequestParam(required = false) String keyword) {
        try {
            List<Template> templates = templateService.getTemplatesByCategoryAndKeyword(category, keyword);
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
    @GetMapping("/all")
    public Result<List<Template>> getAllTemplates() {
        try {
            List<Template> templates = templateService.getAllTemplates();
            return Result.success(templates);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 按评分降序获取模板
     * @param limit 限制数量（可选）
     * @return 模板列表
     */
    @GetMapping("/top-rating")
    public Result<List<Template>> getTemplatesByRating(
            @RequestParam(required = false) Integer limit) {
        try {
            List<Template> templates = templateService.getTemplatesByRatingDesc(limit);
            return Result.success(templates);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 按使用次数降序获取模板
     * @param limit 限制数量（可选）
     * @return 模板列表
     */
    @GetMapping("/top-usage")
    public Result<List<Template>> getTemplatesByUsage(
            @RequestParam(required = false) Integer limit) {
        try {
            List<Template> templates = templateService.getTemplatesByUsageCountDesc(limit);
            return Result.success(templates);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建模板
     * @param template 模板信息
     * @return 创建的模板信息
     */
    @PostMapping("/create")
    public Result<Template> createTemplate(@RequestBody Template template) {
        try {
            Template created = templateService.createTemplate(template);
            return Result.success("创建成功", created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新模板信息
     * @param template 模板信息
     * @return 更新后的模板信息
     */
    @PutMapping("/update")
    public Result<Template> updateTemplate(@RequestBody Template template) {
        try {
            Template updated = templateService.updateTemplate(template);
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
    public Result<Void> deleteTemplate(@PathVariable String id) {
        try {
            templateService.deleteTemplate(id);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 增加使用次数（用户使用模板时调用）
     * @param id 模板ID
     * @return 操作结果
     */
    @PostMapping("/{id}/use")
    public Result<Void> incrementUsageCount(@PathVariable String id) {
        try {
            templateService.incrementUsageCount(id);
            return Result.success("操作成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新评分
     * @param id 模板ID
     * @param rating 新评分
     * @return 操作结果
     */
    @PutMapping("/{id}/rating")
    public Result<Void> updateRating(
            @PathVariable String id,
            @RequestParam BigDecimal rating) {
        try {
            templateService.updateRating(id, rating);
            return Result.success("更新成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}


