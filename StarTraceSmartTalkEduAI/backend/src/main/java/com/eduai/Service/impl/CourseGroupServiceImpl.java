package com.eduai.Service.impl;

import com.eduai.Mapper.CourseGroupMapper;
import com.eduai.Mapper.CourseMapper;
import com.eduai.Mapper.UserMapper;
import com.eduai.Service.CourseGroupService;
import com.eduai.pojo.dto.CourseGroupDTO;
import com.eduai.pojo.entity.Course;
import com.eduai.pojo.entity.CourseGroup;
import com.eduai.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程组服务实现类
 */
@Service
public class CourseGroupServiceImpl implements CourseGroupService {
    
    @Autowired
    private CourseGroupMapper courseGroupMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public CourseGroup getCourseGroupById(Long id) {
        if (id == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        CourseGroup courseGroup = courseGroupMapper.selectById(id);
        if (courseGroup != null) {
            // 加载关联的课程信息
            if (courseGroup.getCourseId() != null) {
                Course course = courseMapper.selectById(courseGroup.getCourseId());
                courseGroup.setCourse(course);
            }
            // 加载关联的教师信息
            if (courseGroup.getTeacherId() != null) {
                User teacher = userMapper.selectById(courseGroup.getTeacherId());
                if (teacher != null) {
                    teacher.setPassword(null); // 移除密码
                }
                courseGroup.setTeacher(teacher);
            }
        }
        return courseGroup;
    }
    
    @Override
    public List<CourseGroup> getCourseGroupsByTeacherId(Long teacherId, String semester) {
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        List<CourseGroup> courseGroups = courseGroupMapper.selectByTeacherId(teacherId, semester);
        // 加载关联的课程信息
        return courseGroups.stream().map(group -> {
            if (group.getCourseId() != null) {
                Course course = courseMapper.selectById(group.getCourseId());
                group.setCourse(course);
            }
            return group;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<CourseGroup> getCourseGroupsBySemester(String semester) {
        if (semester == null || semester.trim().isEmpty()) {
            throw new RuntimeException("学期不能为空");
        }
        List<CourseGroup> courseGroups = courseGroupMapper.selectBySemester(semester);
        // 加载关联的课程信息
        return courseGroups.stream().map(group -> {
            if (group.getCourseId() != null) {
                Course course = courseMapper.selectById(group.getCourseId());
                group.setCourse(course);
            }
            return group;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CourseGroup createCourseGroup(CourseGroupDTO courseGroupDTO) {
        if (courseGroupDTO == null) {
            throw new RuntimeException("课程组信息不能为空");
        }
        
        if (courseGroupDTO.getCourseId() == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        
        if (courseGroupDTO.getTeacherId() == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        
        if (courseGroupDTO.getSemester() == null || courseGroupDTO.getSemester().trim().isEmpty()) {
            throw new RuntimeException("学期不能为空");
        }
        
        // 检查课程是否存在
        Course course = courseMapper.selectById(courseGroupDTO.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 检查教师是否存在
        User teacher = userMapper.selectById(courseGroupDTO.getTeacherId());
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        
        // 创建课程组
        CourseGroup courseGroup = new CourseGroup();
        courseGroup.setCourseId(courseGroupDTO.getCourseId());
        courseGroup.setTeacherId(courseGroupDTO.getTeacherId());
        courseGroup.setSemester(courseGroupDTO.getSemester().trim());
        courseGroup.setMaxStudents(courseGroupDTO.getMaxStudents() != null ? courseGroupDTO.getMaxStudents() : 50);
        courseGroup.setCurrentStudents(0);
        courseGroup.setStatus(1); // 默认正常状态
        
        LocalDateTime now = LocalDateTime.now();
        courseGroup.setCreatedAt(now);
        courseGroup.setUpdatedAt(now);
        
        int result = courseGroupMapper.insert(courseGroup);
        if (result <= 0) {
            throw new RuntimeException("创建课程组失败");
        }
        
        // 加载关联信息
        courseGroup.setCourse(course);
        teacher.setPassword(null);
        courseGroup.setTeacher(teacher);
        
        return courseGroup;
    }
    
    @Override
    @Transactional
    public CourseGroup updateCourseGroup(CourseGroup courseGroup) {
        if (courseGroup == null || courseGroup.getId() == null) {
            throw new RuntimeException("课程组信息或ID不能为空");
        }
        
        CourseGroup existingGroup = courseGroupMapper.selectById(courseGroup.getId());
        if (existingGroup == null) {
            throw new RuntimeException("课程组不存在");
        }
        
        courseGroup.setUpdatedAt(LocalDateTime.now());
        int result = courseGroupMapper.update(courseGroup);
        if (result <= 0) {
            throw new RuntimeException("更新课程组失败");
        }
        
        return getCourseGroupById(courseGroup.getId());
    }
    
    @Override
    @Transactional
    public void deleteCourseGroup(Long id) {
        if (id == null) {
            throw new RuntimeException("课程组ID不能为空");
        }
        
        CourseGroup courseGroup = courseGroupMapper.selectById(id);
        if (courseGroup == null) {
            throw new RuntimeException("课程组不存在");
        }
        
        int result = courseGroupMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除课程组失败");
        }
    }
}



