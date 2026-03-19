package com.eduai.Service;

import com.eduai.pojo.entity.TemplateCategory;
import java.util.List;

/**
 * 模板分类服务接口
 */
public interface TemplateCategoryService {
    
    /**
     * 根据ID获取分类详情
     * @param id 分类ID
     * @return 分类信息
     */
    TemplateCategory getCategoryById(String id);
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<TemplateCategory> getAllCategories();
    
    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建的分类信息
     */
    TemplateCategory createCategory(TemplateCategory category);
    
    /**
     * 更新分类信息
     * @param category 分类信息
     * @return 更新后的分类信息
     */
    TemplateCategory updateCategory(TemplateCategory category);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(String id);
}



























































