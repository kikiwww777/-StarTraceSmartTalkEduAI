package com.eduai.Service;

import com.eduai.pojo.entity.Template;
import java.math.BigDecimal;
import java.util.List;

/**
 * 教学模板服务接口
 */
public interface TemplateService {
    
    /**
     * 根据ID获取模板详情
     * @param id 模板ID
     * @return 模板信息
     */
    Template getTemplateById(String id);
    
    /**
     * 根据分类ID获取模板列表
     * @param category 分类ID（可为null或'all'表示全部）
     * @param keyword 搜索关键词（可为null）
     * @return 模板列表
     */
    List<Template> getTemplatesByCategoryAndKeyword(String category, String keyword);
    
    /**
     * 获取所有模板
     * @return 模板列表
     */
    List<Template> getAllTemplates();
    
    /**
     * 按评分降序获取模板
     * @param limit 限制数量（可为null表示不限制）
     * @return 模板列表
     */
    List<Template> getTemplatesByRatingDesc(Integer limit);
    
    /**
     * 按使用次数降序获取模板
     * @param limit 限制数量（可为null表示不限制）
     * @return 模板列表
     */
    List<Template> getTemplatesByUsageCountDesc(Integer limit);
    
    /**
     * 创建模板
     * @param template 模板信息
     * @return 创建的模板信息
     */
    Template createTemplate(Template template);
    
    /**
     * 更新模板信息
     * @param template 模板信息
     * @return 更新后的模板信息
     */
    Template updateTemplate(Template template);
    
    /**
     * 删除模板
     * @param id 模板ID
     */
    void deleteTemplate(String id);
    
    /**
     * 增加使用次数
     * @param id 模板ID
     */
    void incrementUsageCount(String id);
    
    /**
     * 更新评分
     * @param id 模板ID
     * @param rating 新评分
     */
    void updateRating(String id, BigDecimal rating);
}



























































