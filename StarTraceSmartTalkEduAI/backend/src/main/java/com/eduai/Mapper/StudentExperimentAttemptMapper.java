package com.eduai.Mapper;

import com.eduai.pojo.entity.StudentExperimentAttempt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生实验尝试记录 Mapper
 */
@Mapper
public interface StudentExperimentAttemptMapper {

    StudentExperimentAttempt selectById(@Param("id") Long id);

    /**
     * 查询某个学生对某个模板的最大 attempt_no
     */
    Integer selectMaxAttemptNo(@Param("studentId") Long studentId,
                               @Param("templateId") Long templateId);

    int insert(StudentExperimentAttempt attempt);

    int update(StudentExperimentAttempt attempt);

    List<StudentExperimentAttempt> selectByStudentAndTemplate(@Param("studentId") Long studentId,
                                                              @Param("templateId") Long templateId);
}









































