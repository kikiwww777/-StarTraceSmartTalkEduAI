package com.eduai.Service;

import com.eduai.pojo.dto.LoginDTO;
import com.eduai.pojo.dto.RegisterDTO;
import com.eduai.pojo.entity.User;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return 用户信息
     */
    User login(LoginDTO loginDTO);
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 用户信息
     */
    User register(RegisterDTO registerDTO);
    
    /**
     * 根据学号查询用户
     * @param studentId 学号
     * @return 用户信息
     */
    User getUserByStudentId(String studentId);

    /**
     * 根据用户ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(Long id);

    /**
     * 查询所有教师
     * @return 教师列表
     */
    List<User> listTeachers();
    
    /**
     * 更新用户信息
     * @param user 用户信息（必须包含id）
     * @return 更新后的用户信息
     */
    User updateUser(User user);
}




