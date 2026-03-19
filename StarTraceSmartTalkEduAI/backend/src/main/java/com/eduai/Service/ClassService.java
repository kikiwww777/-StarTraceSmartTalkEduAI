package com.eduai.Service;

import com.eduai.pojo.entity.Class;
import java.util.List;

/**
 * 班级服务接口
 */
public interface ClassService {
    
    /**
     * 创建班级
     * @param classInfo 班级信息
     * @return 创建的班级信息
     */
    Class createClass(Class classInfo);
    
    /**
     * 根据ID获取班级详情（包含关联的课程组信息）
     * @param id 班级ID
     * @return 班级信息
     */
    Class getClassById(Long id);
    
    /**
     * 根据教师ID获取班级列表
     * @param teacherId 教师ID
     * @return 班级列表
     */
    List<Class> getClassesByTeacherId(Long teacherId);
    
    /**
     * 根据课程ID获取班级列表
     * @param courseId 课程ID
     * @return 班级列表
     */
    List<Class> getClassesByCourseId(Long courseId);

    /**
     * 根据课程ID + 教师ID获取班级列表
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return 班级列表
     */
    List<Class> getClassesByCourseIdAndTeacherId(Long courseId, Long teacherId);
    
    /**
     * 获取所有班级
     * @return 班级列表
     */
    List<Class> getAllClasses();
    
    /**
     * 更新班级信息
     * @param classInfo 班级信息
     * @return 更新后的班级信息
     */
    Class updateClass(Class classInfo);
    
    /**
     * 删除班级
     * @param id 班级ID
     */
    void deleteClass(Long id);
}

