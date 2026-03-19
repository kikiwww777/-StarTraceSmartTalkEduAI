package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.CourseMaterial;

import java.util.List;

/**
 * 课程课件/文章 Mapper
 */
@Mapper
public interface CourseMaterialMapper {

    CourseMaterial selectById(@Param("id") Long id);

    List<CourseMaterial> selectByGroupId(@Param("groupId") Long groupId);

    int insert(CourseMaterial material);

    int update(CourseMaterial material);

    int deleteById(@Param("id") Long id);
}


