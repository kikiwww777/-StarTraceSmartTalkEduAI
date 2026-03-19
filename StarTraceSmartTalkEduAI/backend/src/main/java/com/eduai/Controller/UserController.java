package com.eduai.Controller;

import com.eduai.Service.UserService;
import com.eduai.pojo.Result;
import com.eduai.pojo.dto.LoginDTO;
import com.eduai.pojo.dto.RegisterDTO;
import com.eduai.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册接口
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterDTO registerDTO) {
        try {
            User user = userService.register(registerDTO);
            return Result.success("注册成功", user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }
    
    /**
     * 用户登录接口
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.login(loginDTO);
            return Result.success("登录成功", user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据学号查询用户信息
     * @param studentId 学号
     * @return 用户信息
     */
    @GetMapping("/{studentId}")
    public Result<User> getUserByStudentId(@PathVariable String studentId) {
        try {
            User user = userService.getUserByStudentId(studentId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            // 移除密码信息
            user.setPassword(null);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询用户信息（用于前端“查看学生信息”）
     * 注意：该路径需避开 /{studentId} 的路由冲突
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/by-id/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            user.setPassword(null);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有教师
     * @return 教师列表
     */
    @GetMapping("/teachers")
    public Result<List<User>> listTeachers() {
        try {
            List<User> teachers = userService.listTeachers();
            return Result.success(teachers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户个人信息
     * @param id 用户ID（从token或session中获取，这里通过路径参数传递）
     * @return 用户信息
     */
    @GetMapping("/profile/{id}")
    public Result<User> getProfile(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            // 移除密码信息
            user.setPassword(null);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新用户个人信息
     * @param user 用户信息（必须包含id）
     * @return 更新后的用户信息
     */
    @PutMapping("/profile/update")
    public Result<User> updateProfile(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            return Result.success("更新成功", updatedUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}




