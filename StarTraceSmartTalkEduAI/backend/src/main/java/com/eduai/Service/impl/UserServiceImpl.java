package com.eduai.Service.impl;

import com.eduai.Mapper.UserMapper;
import com.eduai.Service.UserService;
import com.eduai.pojo.dto.LoginDTO;
import com.eduai.pojo.dto.RegisterDTO;
import com.eduai.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User login(LoginDTO loginDTO) {
        if (loginDTO == null || loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            throw new RuntimeException("学号或密码不能为空");
        }
        
        // 根据学号查询用户
        User user = userMapper.selectByStudentId(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!user.getPassword().equals(loginDTO.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 登录成功，返回用户信息（注意：实际应用中应该移除密码字段）
        user.setPassword(null);
        return user;
    }
    
    @Override
    public User register(RegisterDTO registerDTO) {
        // 参数校验
        if (registerDTO == null) {
            throw new RuntimeException("注册信息不能为空");
        }
        
        if (registerDTO.getStudentId() == null || registerDTO.getStudentId().trim().isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }
        
        if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        
        // 校验密码长度（至少6位）
        if (registerDTO.getPassword().length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }
        
        // 校验角色（如果提供）
        String role = registerDTO.getRole();
        if (role != null && !role.trim().isEmpty()) {
            if (!role.equals("student") && !role.equals("teacher") && !role.equals("admin")) {
                throw new RuntimeException("用户角色无效，必须是 student、teacher 或 admin");
            }
        } else {
            // 默认为学生
            role = "student";
        }
        
        // 检查学号是否已存在
        User existingUser = userMapper.selectByStudentId(registerDTO.getStudentId().trim());
        if (existingUser != null) {
            throw new RuntimeException("学号已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setStudentId(registerDTO.getStudentId().trim());
        user.setPassword(registerDTO.getPassword()); // 注意：实际应用中应该使用BCrypt等加密
        user.setRole(role);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // 插入用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new RuntimeException("注册失败，请稍后重试");
        }
        
        // 返回用户信息（移除密码）
        user.setPassword(null);
        return user;
    }
    
    @Override
    public User getUserByStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }
        return userMapper.selectByStudentId(studentId);
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return userMapper.selectById(id);
    }

    @Override
    public List<User> listTeachers() {
        List<User> teachers = userMapper.selectByRole("teacher");
        if (teachers != null) {
            teachers.forEach(u -> u.setPassword(null));
        }
        return teachers;
    }
    
    @Override
    public User updateUser(User user) {
        if (user == null) {
            throw new RuntimeException("用户信息不能为空");
        }
        
        if (user.getId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        // 检查用户是否存在
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 设置更新时间
        user.setUpdatedAt(LocalDateTime.now());
        
        // 更新用户信息（不更新密码、学号、角色、创建时间等敏感字段）
        int result = userMapper.update(user);
        if (result <= 0) {
            throw new RuntimeException("更新失败，请稍后重试");
        }
        
        // 返回更新后的用户信息
        User updatedUser = userMapper.selectById(user.getId());
        if (updatedUser != null) {
            updatedUser.setPassword(null);
        }
        return updatedUser;
    }
}




