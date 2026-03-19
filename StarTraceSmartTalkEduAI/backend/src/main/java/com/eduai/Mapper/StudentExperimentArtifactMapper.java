package com.eduai.Mapper;

import com.eduai.pojo.entity.StudentExperimentArtifact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生实验产物 Mapper
 */
@Mapper
public interface StudentExperimentArtifactMapper {

    int insert(StudentExperimentArtifact artifact);

    List<StudentExperimentArtifact> selectByAttemptId(@Param("attemptId") Long attemptId);
}









































