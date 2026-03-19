package com.eduai.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.eduai.pojo.entity.User;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据学号查询用户
     * @param studentId 学号
     * @return 用户信息
     */
    User selectByStudentId(@Param("studentId") String studentId);
    
    /**
     * 根据学号和密码查询用户
     * @param studentId 学号
     * @param password 密码
     * @return 用户信息
     */
    User selectByStudentIdAndPassword(@Param("studentId") String studentId, @Param("password") String password);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 插入新用户
     * @param user 用户信息
     * @return 影响的行数
     */
    int insert(User user);

    /**
     * 根据角色查询用户列表
     * @param role 角色
     * @return 用户列表
     */
    List<User> selectByRole(@Param("role") String role);
    
    /**
     * 根据角色和学科查询用户列表
     * @param role 角色
     * @param subject 学科
     * @return 用户列表
     */
    List<User> selectByRoleAndSubject(@Param("role") String role, @Param("subject") String subject);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 影响的行数
     */
    int update(User user);
    
    /**
     * 更新密码
     * @param id 用户ID
     * @param password 新密码
     * @return 影响的行数
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}




