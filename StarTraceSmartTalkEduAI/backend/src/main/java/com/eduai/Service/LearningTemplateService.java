package com.eduai.Service;

import com.eduai.pojo.dto.CreateLearningTemplateDTO;
import com.eduai.pojo.entity.LearningTemplate;
import java.util.List;

/**
 * 学习模板服务接口
 */
public interface LearningTemplateService {
    
    /**
     * 创建学习模板
     * @param dto 模板信息DTO
     * @return 创建的模板信息
     */
    LearningTemplate createTemplate(CreateLearningTemplateDTO dto);
    
    /**
     * 根据ID获取模板详情
     * @param id 模板ID
     * @return 模板信息
     */
    LearningTemplate getTemplateById(Long id);
    
    /**
     * 根据教师ID获取模板列表
     * @param teacherId 教师ID
     * @return 模板列表
     */
    List<LearningTemplate> getTemplatesByTeacherId(Long teacherId);
    
    /**
     * 根据课程ID获取模板列表
     * @param courseId 课程ID
     * @return 模板列表
     */
    List<LearningTemplate> getTemplatesByCourseId(Long courseId);
    
    /**
     * 根据班级ID获取模板列表
     * @param classId 班级ID
     * @return 模板列表
     */
    List<LearningTemplate> getTemplatesByClassId(Long classId);
    
    /**
     * 根据教师ID和课程ID获取模板列表
     * @param teacherId 教师ID
     * @param courseId 课程ID
     * @return 模板列表
     */
    List<LearningTemplate> getTemplatesByTeacherIdAndCourseId(Long teacherId, Long courseId);
    
    /**
     * 获取所有模板
     * @return 模板列表
     */
    List<LearningTemplate> getAllTemplates();
    
    /**
     * 更新模板信息
     * @param template 模板信息
     * @return 更新后的模板信息
     */
    LearningTemplate updateTemplate(LearningTemplate template);
    
    /**
     * 删除模板
     * @param id 模板ID
     */
    void deleteTemplate(Long id);
    
    /**
     * 增加下载量
     * @param id 模板ID
     */
    void incrementDownloadCount(Long id);
}

