package com.eduai.Service.impl;

import com.eduai.Mapper.CourseMapper;
import com.eduai.Mapper.LearningTemplateMapper;
import com.eduai.Mapper.UserMapper;
import com.eduai.Mapper.ClassMapper;
import com.eduai.Service.LearningTemplateService;
import com.eduai.pojo.dto.CreateLearningTemplateDTO;
import com.eduai.pojo.entity.LearningTemplate;
import com.eduai.pojo.entity.Course;
import com.eduai.pojo.entity.User;
import com.eduai.pojo.entity.Class;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学习模板服务实现类
 */
@Service
public class LearningTemplateServiceImpl implements LearningTemplateService {
    
    @Autowired
    private LearningTemplateMapper learningTemplateMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private ClassMapper classMapper;
    
    @Override
    @Transactional
    public LearningTemplate createTemplate(CreateLearningTemplateDTO dto) {
        if (dto == null) {
            throw new RuntimeException("模板信息不能为空");
        }
        
        if (dto.getTemplateName() == null || dto.getTemplateName().trim().isEmpty()) {
            throw new RuntimeException("模板名称不能为空");
        }
        
        if (dto.getJsonContent() == null || dto.getJsonContent().trim().isEmpty()) {
            throw new RuntimeException("JSON模板内容不能为空");
        }
        
        if (dto.getTeacherId() == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        
        if (dto.getCourseId() == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        
        // 检查教师是否存在
        User teacher = userMapper.selectById(dto.getTeacherId());
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        
        // 检查课程是否存在
        Course course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 如果提供了班级ID，检查班级是否存在
        if (dto.getClassId() != null) {
            Class classInfo = classMapper.selectById(dto.getClassId());
            if (classInfo == null) {
                throw new RuntimeException("班级不存在");
            }
        }
        
        // 验证JSON格式
        try {
            // 简单验证：尝试解析JSON字符串
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objectMapper.readTree(dto.getJsonContent());
        } catch (Exception e) {
            throw new RuntimeException("JSON格式不正确：" + e.getMessage());
        }
        
        // 创建模板对象
        LearningTemplate template = new LearningTemplate();
        template.setTemplateName(dto.getTemplateName().trim());
        template.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : null);
        template.setImageUrl(dto.getImageUrl());
        template.setJsonContent(dto.getJsonContent());
        template.setTeacherId(dto.getTeacherId());
        template.setCourseId(dto.getCourseId());
        template.setClassId(dto.getClassId());
        template.setDownloadCount(0);
        template.setStatus(1); // 默认正常状态
        
        LocalDateTime now = LocalDateTime.now();
        template.setCreatedAt(now);
        template.setUpdatedAt(now);
        
        int result = learningTemplateMapper.insert(template);
        if (result <= 0) {
            throw new RuntimeException("创建模板失败");
        }
        
        // 加载关联信息
        loadAssociatedInfo(template);
        
        return template;
    }
    
    @Override
    public LearningTemplate getTemplateById(Long id) {
        if (id == null) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        LearningTemplate template = learningTemplateMapper.selectById(id);
        if (template != null) {
            loadAssociatedInfo(template);
        }
        
        return template;
    }
    
    @Override
    public List<LearningTemplate> getTemplatesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        
        List<LearningTemplate> templates = learningTemplateMapper.selectByTeacherId(teacherId);
        templates.forEach(this::loadAssociatedInfo);
        return templates;
    }
    
    @Override
    public List<LearningTemplate> getTemplatesByCourseId(Long courseId) {
        if (courseId == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        
        List<LearningTemplate> templates = learningTemplateMapper.selectByCourseId(courseId);
        templates.forEach(this::loadAssociatedInfo);
        return templates;
    }
    
    @Override
    public List<LearningTemplate> getTemplatesByClassId(Long classId) {
        if (classId == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        
        List<LearningTemplate> templates = learningTemplateMapper.selectByClassId(classId);
        templates.forEach(this::loadAssociatedInfo);
        return templates;
    }
    
    @Override
    public List<LearningTemplate> getTemplatesByTeacherIdAndCourseId(Long teacherId, Long courseId) {
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        if (courseId == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        
        List<LearningTemplate> templates = learningTemplateMapper.selectByTeacherIdAndCourseId(teacherId, courseId);
        templates.forEach(this::loadAssociatedInfo);
        return templates;
    }
    
    @Override
    public List<LearningTemplate> getAllTemplates() {
        List<LearningTemplate> templates = learningTemplateMapper.selectAll();
        templates.forEach(this::loadAssociatedInfo);
        return templates;
    }
    
    @Override
    @Transactional
    public LearningTemplate updateTemplate(LearningTemplate template) {
        if (template == null) {
            throw new RuntimeException("模板信息不能为空");
        }
        
        if (template.getId() == null) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        // 检查模板是否存在
        LearningTemplate existing = learningTemplateMapper.selectById(template.getId());
        if (existing == null) {
            throw new RuntimeException("模板不存在");
        }
        
        // 如果更新了模板名称，检查是否为空
        if (template.getTemplateName() != null && template.getTemplateName().trim().isEmpty()) {
            throw new RuntimeException("模板名称不能为空");
        }
        
        // 如果更新了JSON内容，验证格式
        if (template.getJsonContent() != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                objectMapper.readTree(template.getJsonContent());
            } catch (Exception e) {
                throw new RuntimeException("JSON格式不正确：" + e.getMessage());
            }
        }
        
        // 如果更新了教师ID，检查教师是否存在
        if (template.getTeacherId() != null) {
            User teacher = userMapper.selectById(template.getTeacherId());
            if (teacher == null) {
                throw new RuntimeException("教师不存在");
            }
        }
        
        // 如果更新了课程ID，检查课程是否存在
        if (template.getCourseId() != null) {
            Course course = courseMapper.selectById(template.getCourseId());
            if (course == null) {
                throw new RuntimeException("课程不存在");
            }
        }
        
        // 如果更新了班级ID，检查班级是否存在
        if (template.getClassId() != null) {
            Class classInfo = classMapper.selectById(template.getClassId());
            if (classInfo == null) {
                throw new RuntimeException("班级不存在");
            }
        }
        
        template.setUpdatedAt(LocalDateTime.now());
        
        int result = learningTemplateMapper.update(template);
        if (result <= 0) {
            throw new RuntimeException("更新模板失败");
        }
        
        // 重新加载模板信息
        LearningTemplate updated = learningTemplateMapper.selectById(template.getId());
        loadAssociatedInfo(updated);
        
        return updated;
    }
    
    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        if (id == null) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        LearningTemplate existing = learningTemplateMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("模板不存在");
        }
        
        int result = learningTemplateMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除模板失败");
        }
    }
    
    @Override
    @Transactional
    public void incrementDownloadCount(Long id) {
        if (id == null) {
            throw new RuntimeException("模板ID不能为空");
        }
        
        LearningTemplate existing = learningTemplateMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("模板不存在");
        }
        
        int result = learningTemplateMapper.incrementDownloadCount(id);
        if (result <= 0) {
            throw new RuntimeException("增加下载量失败");
        }
    }
    
    /**
     * 加载关联信息（教师、课程、班级）
     */
    private void loadAssociatedInfo(LearningTemplate template) {
        if (template == null) {
            return;
        }
        
        // 加载教师信息
        if (template.getTeacherId() != null) {
            User teacher = userMapper.selectById(template.getTeacherId());
            template.setTeacher(teacher);
        }
        
        // 加载课程信息
        if (template.getCourseId() != null) {
            Course course = courseMapper.selectById(template.getCourseId());
            template.setCourse(course);
        }
        
        // 加载班级信息
        if (template.getClassId() != null) {
            Class classInfo = classMapper.selectById(template.getClassId());
            template.setClassInfo(classInfo);
        }
    }
}

