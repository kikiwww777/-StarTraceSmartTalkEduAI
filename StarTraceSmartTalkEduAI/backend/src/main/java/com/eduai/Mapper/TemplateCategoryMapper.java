package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.TemplateCategory;
import java.util.List;

/**
 * 模板分类Mapper接口
 */
@Mapper
public interface TemplateCategoryMapper {
    
    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类信息
     */
    TemplateCategory selectById(@Param("id") String id);
    
    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<TemplateCategory> selectAll();
    
    /**
     * 插入新分类
     * @param category 分类信息
     * @return 影响的行数
     */
    int insert(TemplateCategory category);
    
    /**
     * 更新分类信息
     * @param category 分类信息
     * @return 影响的行数
     */
    int update(TemplateCategory category);
    
    /**
     * 根据ID删除分类
     * @param id 分类ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") String id);
}



























































