package com.eduai.Controller;

import com.eduai.Service.TemplateCategoryService;
import com.eduai.pojo.Result;
import com.eduai.pojo.entity.TemplateCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模板分类控制器
 */
@RestController
@RequestMapping("/api/template-category")
public class TemplateCategoryController {
    
    @Autowired
    private TemplateCategoryService templateCategoryService;
    
    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @return 分类信息
     */
    @GetMapping("/{id}")
    public Result<TemplateCategory> getCategoryById(@PathVariable String id) {
        try {
            TemplateCategory category = templateCategoryService.getCategoryById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result<List<TemplateCategory>> getAllCategories() {
        try {
            List<TemplateCategory> categories = templateCategoryService.getAllCategories();
            return Result.success(categories);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建的分类信息
     */
    @PostMapping("/create")
    public Result<TemplateCategory> createCategory(@RequestBody TemplateCategory category) {
        try {
            TemplateCategory created = templateCategoryService.createCategory(category);
            return Result.success("创建成功", created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新分类信息
     * @param category 分类信息
     * @return 更新后的分类信息
     */
    @PutMapping("/update")
    public Result<TemplateCategory> updateCategory(@RequestBody TemplateCategory category) {
        try {
            TemplateCategory updated = templateCategoryService.updateCategory(category);
            return Result.success("更新成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable String id) {
        try {
            templateCategoryService.deleteCategory(id);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}



























































