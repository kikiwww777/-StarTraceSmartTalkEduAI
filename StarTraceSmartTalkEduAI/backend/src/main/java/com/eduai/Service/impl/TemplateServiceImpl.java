package com.eduai.Service.impl;

import com.eduai.Mapper.TemplateCategoryMapper;
import com.eduai.Mapper.TemplateMapper;
import com.eduai.Service.TemplateService;
import com.eduai.pojo.entity.Template;
import com.eduai.pojo.entity.TemplateCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 教学模板服务实现类
 */
@Service
public class TemplateServiceImpl implements TemplateService {
    
    @Autowired
    private TemplateMapper templateMapper;
    
    @Autowired
    private TemplateCategoryMapper templateCategoryMapper;
    
    @Override
    public Template getTemplateById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        Template template = templateMapper.selectById(id);
        if (template != null) {
            loadCategoryInfo(template);
        }
        
        return template;
    }
    
    @Override
    public List<Template> getTemplatesByCategoryAndKeyword(String category, String keyword) {
        List<Template> templates;
        
        if ((category == null || category.trim().isEmpty() || "all".equals(category)) 
            && (keyword == null || keyword.trim().isEmpty())) {
            // 查询所有模板
            templates = templateMapper.selectAll();
        } else if (category == null || category.trim().isEmpty() || "all".equals(category)) {
            // 只按关键词搜索
            templates = templateMapper.selectByKeyword(keyword);
        } else if (keyword == null || keyword.trim().isEmpty()) {
            // 只按分类查询
            templates = templateMapper.selectByCategory(category);
        } else {
            // 按分类和关键词查询
            templates = templateMapper.selectByCategoryAndKeyword(category, keyword);
        }
        
        templates.forEach(this::loadCategoryInfo);
        return templates;
    }
    
    @Override
    public List<Template> getAllTemplates() {
        List<Template> templates = templateMapper.selectAll();
        templates.forEach(this::loadCategoryInfo);
        return templates;
    }
    
    @Override
    public List<Template> getTemplatesByRatingDesc(Integer limit) {
        List<Template> templates = templateMapper.selectByRatingDesc(limit);
        templates.forEach(this::loadCategoryInfo);
        return templates;
    }
    
    @Override
    public List<Template> getTemplatesByUsageCountDesc(Integer limit) {
        List<Template> templates = templateMapper.selectByUsageCountDesc(limit);
        templates.forEach(this::loadCategoryInfo);
        return templates;
    }
    
    @Override
    @Transactional
    public Template createTemplate(Template template) {
        if (template == null) {
            throw new RuntimeException("模板信息不能为空");
        }
        
        if (template.getId() == null || template.getId().trim().isEmpty()) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        if (template.getTitle() == null || template.getTitle().trim().isEmpty()) {
            throw new RuntimeException("模板标题不能为空");
        }
        
        if (template.getCategory() == null || template.getCategory().trim().isEmpty()) {
            throw new RuntimeException("模板分类不能为空");
        }
        
        // 检查分类是否存在
        TemplateCategory category = templateCategoryMapper.selectById(template.getCategory());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 检查模板ID是否已存在
        Template existing = templateMapper.selectById(template.getId());
        if (existing != null) {
            throw new RuntimeException("模板ID已存在");
        }
        
        // 设置默认值
        if (template.getUsageCount() == null) {
            template.setUsageCount(0);
        }
        if (template.getRating() == null) {
            template.setRating(BigDecimal.ZERO);
        }
        if (template.getColor() == null || template.getColor().trim().isEmpty()) {
            template.setColor("#2563eb");
        }
        
        LocalDateTime now = LocalDateTime.now();
        template.setCreatedAt(now);
        template.setUpdatedAt(now);
        
        int result = templateMapper.insert(template);
        if (result <= 0) {
            throw new RuntimeException("创建模板失败");
        }
        
        loadCategoryInfo(template);
        return template;
    }
    
    @Override
    @Transactional
    public Template updateTemplate(Template template) {
        if (template == null) {
            throw new RuntimeException("模板信息不能为空");
        }
        
        if (template.getId() == null || template.getId().trim().isEmpty()) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        // 检查模板是否存在
        Template existing = templateMapper.selectById(template.getId());
        if (existing == null) {
            throw new RuntimeException("模板不存在");
        }
        
        // 如果更新了标题，检查是否为空
        if (template.getTitle() != null && template.getTitle().trim().isEmpty()) {
            throw new RuntimeException("模板标题不能为空");
        }
        
        // 如果更新了分类，检查分类是否存在
        if (template.getCategory() != null && !template.getCategory().trim().isEmpty()) {
            TemplateCategory category = templateCategoryMapper.selectById(template.getCategory());
            if (category == null) {
                throw new RuntimeException("分类不存在");
            }
        }
        
        template.setUpdatedAt(LocalDateTime.now());
        
        int result = templateMapper.update(template);
        if (result <= 0) {
            throw new RuntimeException("更新模板失败");
        }
        
        // 重新加载模板信息
        Template updated = templateMapper.selectById(template.getId());
        loadCategoryInfo(updated);
        
        return updated;
    }
    
    @Override
    @Transactional
    public void deleteTemplate(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        Template existing = templateMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("模板不存在");
        }
        
        int result = templateMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除模板失败");
        }
    }
    
    @Override
    @Transactional
    public void incrementUsageCount(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        Template existing = templateMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("模板不存在");
        }
        
        int result = templateMapper.incrementUsageCount(id);
        if (result <= 0) {
            throw new RuntimeException("增加使用次数失败");
        }
    }
    
    @Override
    @Transactional
    public void updateRating(String id, BigDecimal rating) {
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        if (rating == null) {
            throw new RuntimeException("评分不能为空");
        }
        
        if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5")) > 0) {
            throw new RuntimeException("评分必须在0-5分之间");
        }
        
        Template existing = templateMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("模板不存在");
        }
        
        int result = templateMapper.updateRating(id, rating);
        if (result <= 0) {
            throw new RuntimeException("更新评分失败");
        }
    }
    
    /**
     * 加载分类信息
     */
    private void loadCategoryInfo(Template template) {
        if (template == null || template.getCategory() == null) {
            return;
        }
        
        TemplateCategory category = templateCategoryMapper.selectById(template.getCategory());
        template.setCategoryInfo(category);
    }
}



























































