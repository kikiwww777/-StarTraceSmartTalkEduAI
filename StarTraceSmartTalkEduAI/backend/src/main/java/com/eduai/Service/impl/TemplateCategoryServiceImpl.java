package com.eduai.Service.impl;

import com.eduai.Mapper.TemplateCategoryMapper;
import com.eduai.Mapper.TemplateMapper;
import com.eduai.Service.TemplateCategoryService;
import com.eduai.pojo.entity.TemplateCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 模板分类服务实现类
 */
@Service
public class TemplateCategoryServiceImpl implements TemplateCategoryService {
    
    @Autowired
    private TemplateCategoryMapper templateCategoryMapper;
    
    @Autowired
    private TemplateMapper templateMapper;
    
    @Override
    public TemplateCategory getCategoryById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("分类ID不能为空");
        }
        
        return templateCategoryMapper.selectById(id);
    }
    
    @Override
    public List<TemplateCategory> getAllCategories() {
        return templateCategoryMapper.selectAll();
    }
    
    @Override
    @Transactional
    public TemplateCategory createCategory(TemplateCategory category) {
        if (category == null) {
            throw new RuntimeException("分类信息不能为空");
        }
        
        if (category.getId() == null || category.getId().trim().isEmpty()) {
            throw new RuntimeException("分类ID不能为空");
        }
        
        if (category.getLabel() == null || category.getLabel().trim().isEmpty()) {
            throw new RuntimeException("分类标签不能为空");
        }
        
        // 检查分类ID是否已存在
        TemplateCategory existing = templateCategoryMapper.selectById(category.getId());
        if (existing != null) {
            throw new RuntimeException("分类ID已存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        
        int result = templateCategoryMapper.insert(category);
        if (result <= 0) {
            throw new RuntimeException("创建分类失败");
        }
        
        return category;
    }
    
    @Override
    @Transactional
    public TemplateCategory updateCategory(TemplateCategory category) {
        if (category == null) {
            throw new RuntimeException("分类信息不能为空");
        }
        
        if (category.getId() == null || category.getId().trim().isEmpty()) {
            throw new RuntimeException("分类ID不能为空");
        }
        
        // 检查分类是否存在
        TemplateCategory existing = templateCategoryMapper.selectById(category.getId());
        if (existing == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 如果更新了标签，检查是否为空
        if (category.getLabel() != null && category.getLabel().trim().isEmpty()) {
            throw new RuntimeException("分类标签不能为空");
        }
        
        category.setUpdatedAt(LocalDateTime.now());
        
        int result = templateCategoryMapper.update(category);
        if (result <= 0) {
            throw new RuntimeException("更新分类失败");
        }
        
        // 重新加载分类信息
        return templateCategoryMapper.selectById(category.getId());
    }
    
    @Override
    @Transactional
    public void deleteCategory(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("分类ID不能为空");
        }
        
        // 检查分类是否存在
        TemplateCategory existing = templateCategoryMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查是否有模板使用此分类
        List<com.eduai.pojo.entity.Template> templates = templateMapper.selectByCategory(id);
        if (templates != null && !templates.isEmpty()) {
            throw new RuntimeException("该分类下存在模板，无法删除");
        }
        
        int result = templateCategoryMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除分类失败");
        }
    }
}



























































