package com.eduai.Mapper;

import com.eduai.pojo.entity.StudentExperimentEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生实验事件流 Mapper
 */
@Mapper
public interface StudentExperimentEventMapper {

    int insert(StudentExperimentEvent event);

    List<StudentExperimentEvent> selectByAttemptId(@Param("attemptId") Long attemptId);
}









































