package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.Template;
import java.util.List;

/**
 * 教学模板Mapper接口
 */
@Mapper
public interface TemplateMapper {
    
    /**
     * 根据ID查询模板
     * @param id 模板ID
     * @return 模板信息
     */
    Template selectById(@Param("id") String id);
    
    /**
     * 根据分类ID查询模板列表
     * @param category 分类ID
     * @return 模板列表
     */
    List<Template> selectByCategory(@Param("category") String category);
    
    /**
     * 根据关键词搜索模板（标题或描述）
     * @param keyword 关键词
     * @return 模板列表
     */
    List<Template> selectByKeyword(@Param("keyword") String keyword);
    
    /**
     * 根据分类和关键词查询模板列表
     * @param category 分类ID（可为null，表示全部）
     * @param keyword 关键词（可为null）
     * @return 模板列表
     */
    List<Template> selectByCategoryAndKeyword(@Param("category") String category, @Param("keyword") String keyword);
    
    /**
     * 查询所有模板
     * @return 模板列表
     */
    List<Template> selectAll();
    
    /**
     * 按评分降序查询模板
     * @param limit 限制数量
     * @return 模板列表
     */
    List<Template> selectByRatingDesc(@Param("limit") Integer limit);
    
    /**
     * 按使用次数降序查询模板
     * @param limit 限制数量
     * @return 模板列表
     */
    List<Template> selectByUsageCountDesc(@Param("limit") Integer limit);
    
    /**
     * 插入新模板
     * @param template 模板信息
     * @return 影响的行数
     */
    int insert(Template template);
    
    /**
     * 更新模板信息
     * @param template 模板信息
     * @return 影响的行数
     */
    int update(Template template);
    
    /**
     * 根据ID删除模板
     * @param id 模板ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 增加使用次数
     * @param id 模板ID
     * @return 影响的行数
     */
    int incrementUsageCount(@Param("id") String id);
    
    /**
     * 更新评分
     * @param id 模板ID
     * @param rating 新评分
     * @return 影响的行数
     */
    int updateRating(@Param("id") String id, @Param("rating") java.math.BigDecimal rating);
}



























































