package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.Class;
import java.util.List;

/**
 * 班级Mapper接口
 */
@Mapper
public interface ClassMapper {
    
    /**
     * 根据ID查询班级
     * @param id 班级ID
     * @return 班级信息
     */
    Class selectById(@Param("id") Long id);
    
    /**
     * 根据班级代码查询班级
     * @param classCode 班级代码
     * @return 班级信息
     */
    Class selectByClassCode(@Param("classCode") String classCode);
    
    /**
     * 根据教师ID查询班级列表
     * @param teacherId 教师ID
     * @return 班级列表
     */
    List<Class> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 根据课程ID查询班级列表
     * @param courseId 课程ID
     * @return 班级列表
     */
    List<Class> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据课程ID + 教师ID查询班级列表
     * 用于避免“同一门课多个老师”时按 courseId 查询到错误班级
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @return 班级列表
     */
    List<Class> selectByCourseIdAndTeacherId(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);
    
    /**
     * 查询所有班级
     * @return 班级列表
     */
    List<Class> selectAll();
    
    /**
     * 插入新班级
     * @param classInfo 班级信息
     * @return 影响的行数
     */
    int insert(Class classInfo);
    
    /**
     * 更新班级信息
     * @param classInfo 班级信息
     * @return 影响的行数
     */
    int update(Class classInfo);
    
    /**
     * 根据ID删除班级
     * @param id 班级ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id);
}

