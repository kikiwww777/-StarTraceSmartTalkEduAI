package com.eduai.Service.impl;

import com.eduai.Mapper.ClassMapper;
import com.eduai.Mapper.CourseGroupMapper;
import com.eduai.Mapper.CourseMapper;
import com.eduai.Mapper.UserMapper;
import com.eduai.Service.ClassService;
import com.eduai.pojo.entity.Class;
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
 * 班级服务实现类
 */
@Service
public class ClassServiceImpl implements ClassService {
    
    @Autowired
    private ClassMapper classMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CourseGroupMapper courseGroupMapper;
    
    @Override
    @Transactional
    public Class createClass(Class classInfo) {
        if (classInfo == null) {
            throw new RuntimeException("班级信息不能为空");
        }
        
        if (classInfo.getClassName() == null || classInfo.getClassName().trim().isEmpty()) {
            throw new RuntimeException("班级名称不能为空");
        }
        
        if (classInfo.getTeacherId() == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        
        if (classInfo.getCourseId() == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        
        // 检查教师是否存在
        User teacher = userMapper.selectById(classInfo.getTeacherId());
        if (teacher == null) {
            throw new RuntimeException("教师不存在");
        }
        
        // 检查课程是否存在
        Course course = courseMapper.selectById(classInfo.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 检查班级代码是否重复
        if (classInfo.getClassCode() != null && !classInfo.getClassCode().trim().isEmpty()) {
            Class existingClass = classMapper.selectByClassCode(classInfo.getClassCode().trim());
            if (existingClass != null) {
                throw new RuntimeException("班级代码已存在");
            }
            classInfo.setClassCode(classInfo.getClassCode().trim());
        }
        
        // 设置默认值
        if (classInfo.getStatus() == null) {
            classInfo.setStatus(1);
        }
        
        LocalDateTime now = LocalDateTime.now();
        classInfo.setCreatedAt(now);
        classInfo.setUpdatedAt(now);
        
        int result = classMapper.insert(classInfo);
        if (result <= 0) {
            throw new RuntimeException("创建班级失败");
        }
        
        // 加载关联信息
        loadAssociatedInfo(classInfo);
        
        return classInfo;
    }
    
    @Override
    public Class getClassById(Long id) {
        if (id == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        Class classInfo = classMapper.selectById(id);
        if (classInfo != null) {
            loadAssociatedInfo(classInfo);
        }
        return classInfo;
    }
    
    @Override
    public List<Class> getClassesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        List<Class> classes = classMapper.selectByTeacherId(teacherId);
        return classes.stream().map(this::loadAssociatedInfo).collect(Collectors.toList());
    }
    
    @Override
    public List<Class> getClassesByCourseId(Long courseId) {
        if (courseId == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        List<Class> classes = classMapper.selectByCourseId(courseId);
        return classes.stream().map(this::loadAssociatedInfo).collect(Collectors.toList());
    }

    @Override
    public List<Class> getClassesByCourseIdAndTeacherId(Long courseId, Long teacherId) {
        if (courseId == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        if (teacherId == null) {
            throw new RuntimeException("教师ID不能为空");
        }
        List<Class> classes = classMapper.selectByCourseIdAndTeacherId(courseId, teacherId);
        return classes.stream().map(this::loadAssociatedInfo).collect(Collectors.toList());
    }
    
    @Override
    public List<Class> getAllClasses() {
        List<Class> classes = classMapper.selectAll();
        return classes.stream().map(this::loadAssociatedInfo).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Class updateClass(Class classInfo) {
        if (classInfo == null) {
            throw new RuntimeException("班级信息不能为空");
        }
        
        if (classInfo.getId() == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        
        // 检查班级是否存在
        Class existingClass = classMapper.selectById(classInfo.getId());
        if (existingClass == null) {
            throw new RuntimeException("班级不存在");
        }
        
        // 检查班级代码是否重复（如果修改了班级代码）
        if (classInfo.getClassCode() != null && !classInfo.getClassCode().trim().isEmpty()) {
            Class classWithSameCode = classMapper.selectByClassCode(classInfo.getClassCode().trim());
            if (classWithSameCode != null && !classWithSameCode.getId().equals(classInfo.getId())) {
                throw new RuntimeException("班级代码已存在");
            }
            classInfo.setClassCode(classInfo.getClassCode().trim());
        }
        
        // 如果修改了教师或课程，需要验证
        if (classInfo.getTeacherId() != null) {
            User teacher = userMapper.selectById(classInfo.getTeacherId());
            if (teacher == null) {
                throw new RuntimeException("教师不存在");
            }
        }
        
        if (classInfo.getCourseId() != null) {
            Course course = courseMapper.selectById(classInfo.getCourseId());
            if (course == null) {
                throw new RuntimeException("课程不存在");
            }
        }
        
        classInfo.setUpdatedAt(LocalDateTime.now());
        
        int result = classMapper.update(classInfo);
        if (result <= 0) {
            throw new RuntimeException("更新失败");
        }
        
        // 返回更新后的班级信息
        return getClassById(classInfo.getId());
    }
    
    @Override
    @Transactional
    public void deleteClass(Long id) {
        if (id == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        
        Class classInfo = classMapper.selectById(id);
        if (classInfo == null) {
            throw new RuntimeException("班级不存在");
        }
        
        int result = classMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除失败");
        }
    }
    
    /**
     * 加载关联信息（教师、课程、课程组）
     * 课程组信息用于获取开课时间、学期等详细信息
     */
    private Class loadAssociatedInfo(Class classInfo) {
        // 加载教师信息
        if (classInfo.getTeacherId() != null) {
            User teacher = userMapper.selectById(classInfo.getTeacherId());
            if (teacher != null) {
                teacher.setPassword(null);
            }
            classInfo.setTeacher(teacher);
        }
        
        // 加载课程信息
        if (classInfo.getCourseId() != null) {
            Course course = courseMapper.selectById(classInfo.getCourseId());
            classInfo.setCourse(course);
            
            // 加载关联的课程组信息（用于获取开课时间、学期等）
            if (classInfo.getTeacherId() != null) {
                // 直接在 SQL 层取“该老师该课程”的最新课程组，避免 Java 侧遍历筛选导致拼错
                CourseGroup matchedGroup = courseGroupMapper.selectLatestByCourseIdAndTeacherId(
                    classInfo.getCourseId(),
                    classInfo.getTeacherId()
                );
                
                if (matchedGroup != null) {
                    // 加载课程组的关联信息
                    if (matchedGroup.getCourseId() != null) {
                        Course courseGroupCourse = courseMapper.selectById(matchedGroup.getCourseId());
                        matchedGroup.setCourse(courseGroupCourse);
                    }
                    if (matchedGroup.getTeacherId() != null) {
                        User courseGroupTeacher = userMapper.selectById(matchedGroup.getTeacherId());
                        if (courseGroupTeacher != null) {
                            courseGroupTeacher.setPassword(null);
                        }
                        matchedGroup.setTeacher(courseGroupTeacher);
                    }
                    classInfo.setCourseGroup(matchedGroup);
                }
            }
        }
        
        return classInfo;
    }
}

