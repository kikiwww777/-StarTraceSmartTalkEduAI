package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.LearningTemplate;
import java.util.List;

/**
 * 学习模板Mapper接口
 */
@Mapper
public interface LearningTemplateMapper {
    
    /**
     * 根据ID查询模板
     * @param id 模板ID
     * @return 模板信息
     */
    LearningTemplate selectById(@Param("id") Long id);
    
    /**
     * 根据教师ID查询模板列表
     * @param teacherId 教师ID
     * @return 模板列表
     */
    List<LearningTemplate> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 根据课程ID查询模板列表
     * @param courseId 课程ID
     * @return 模板列表
     */
    List<LearningTemplate> selectByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据班级ID查询模板列表
     * @param classId 班级ID
     * @return 模板列表
     */
    List<LearningTemplate> selectByClassId(@Param("classId") Long classId);
    
    /**
     * 根据教师ID和课程ID查询模板列表
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @return 模板列表
     */
    List<LearningTemplate> selectByTeacherIdAndCourseId(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    
    /**
     * 查询所有模板
     * @return 模板列表
     */
    List<LearningTemplate> selectAll();
    
    /**
     * 插入新模板
     * @param template 模板信息
     * @return 影响的行数
     */
    int insert(LearningTemplate template);
    
    /**
     * 更新模板信息
     * @param template 模板信息
     * @return 影响的行数
     */
    int update(LearningTemplate template);
    
    /**
     * 根据ID删除模板
     * @param id 模板ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 增加下载量
     * @param id 模板ID
     * @return 影响的行数
     */
    int incrementDownloadCount(@Param("id") Long id);
}

