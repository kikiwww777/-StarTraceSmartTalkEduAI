package com.eduai.Service;

import com.eduai.pojo.dto.CourseGroupDTO;
import com.eduai.pojo.entity.CourseGroup;
import java.util.List;

/**
 * 课程组服务接口
 */
public interface CourseGroupService {
    
    /**
     * 根据ID查询课程组（包含关联信息）
     * @param id 课程组ID
     * @return 课程组信息
     */
    CourseGroup getCourseGroupById(Long id);
    
    /**
     * 根据教师ID查询课程组列表
     * @param teacherId 教师ID
     * @param semester 学期（可选）
     * @return 课程组列表
     */
    List<CourseGroup> getCourseGroupsByTeacherId(Long teacherId, String semester);
    
    /**
     * 根据学期查询课程组列表
     * @param semester 学期
     * @return 课程组列表
     */
    List<CourseGroup> getCourseGroupsBySemester(String semester);
    
    /**
     * 创建课程组
     * @param courseGroupDTO 课程组DTO
     * @return 创建的课程组信息
     */
    CourseGroup createCourseGroup(CourseGroupDTO courseGroupDTO);
    
    /**
     * 更新课程组
     * @param courseGroup 课程组信息
     * @return 更新后的课程组信息
     */
    CourseGroup updateCourseGroup(CourseGroup courseGroup);
    
    /**
     * 删除课程组
     * @param id 课程组ID
     */
    void deleteCourseGroup(Long id);
}



