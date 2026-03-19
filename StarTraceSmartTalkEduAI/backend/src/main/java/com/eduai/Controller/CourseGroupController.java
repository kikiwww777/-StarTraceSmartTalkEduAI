package com.eduai.Controller;

import com.eduai.Service.CourseGroupService;
import com.eduai.Service.StudentCourseRelationService;
import com.eduai.pojo.Result;
import com.eduai.pojo.dto.CourseGroupDTO;
import com.eduai.pojo.dto.InviteStudentDTO;
import com.eduai.pojo.entity.CourseGroup;
import com.eduai.pojo.entity.StudentCourseRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 课程组控制器
 */
@RestController
@RequestMapping("/api/course/group")
public class CourseGroupController {
    
    @Autowired
    private CourseGroupService courseGroupService;
    
    @Autowired
    private StudentCourseRelationService studentCourseRelationService;
    
    /**
     * 创建课程组
     * @param courseGroupDTO 课程组DTO
     * @return 创建的课程组信息
     */
    @PostMapping("/create")
    public Result<CourseGroup> createCourseGroup(@RequestBody CourseGroupDTO courseGroupDTO) {
        try {
            CourseGroup courseGroup = courseGroupService.createCourseGroup(courseGroupDTO);
            return Result.success("创建成功", courseGroup);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取课程组详情
     * @param id 课程组ID
     * @return 课程组信息
     */
    @GetMapping("/{id}")
    public Result<CourseGroup> getCourseGroupById(@PathVariable Long id) {
        try {
            CourseGroup courseGroup = courseGroupService.getCourseGroupById(id);
            if (courseGroup == null) {
                return Result.error("课程组不存在");
            }
            return Result.success(courseGroup);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据学期获取课程组列表
     * @param semester 学期（URL编码）
     * @return 课程组列表
     */
    @GetMapping("/semester/{semester}")
    public Result<List<CourseGroup>> getCourseGroupsBySemester(@PathVariable String semester) {
        try {
            // URL解码
            String decodedSemester = URLDecoder.decode(semester, StandardCharsets.UTF_8);
            List<CourseGroup> courseGroups = courseGroupService.getCourseGroupsBySemester(decodedSemester);
            return Result.success(courseGroups);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据教师ID获取课程组列表
     * @param teacherId 教师ID
     * @param semester 学期（可选）
     * @return 课程组列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<CourseGroup>> getCourseGroupsByTeacherId(
            @PathVariable Long teacherId,
            @RequestParam(required = false) String semester) {
        try {
            List<CourseGroup> courseGroups = courseGroupService.getCourseGroupsByTeacherId(teacherId, semester);
            return Result.success(courseGroups);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新课程组
     * @param courseGroup 课程组信息
     * @return 更新后的课程组信息
     */
    @PutMapping("/update")
    public Result<CourseGroup> updateCourseGroup(@RequestBody CourseGroup courseGroup) {
        try {
            CourseGroup updatedGroup = courseGroupService.updateCourseGroup(courseGroup);
            return Result.success("更新成功", updatedGroup);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除课程组
     * @param id 课程组ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourseGroup(@PathVariable Long id) {
        try {
            courseGroupService.deleteCourseGroup(id);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 邀请学生加入课程
     * @param inviteDTO 邀请DTO
     * @return 创建的关系信息
     */
    @PostMapping("/invite")
    public Result<StudentCourseRelation> inviteStudent(@RequestBody InviteStudentDTO inviteDTO) {
        try {
            StudentCourseRelation relation = studentCourseRelationService.inviteStudent(inviteDTO);
            return Result.success("邀请成功", relation);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("邀请失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取课程组的学生列表
     * @param groupId 课程组ID
     * @return 学生选课列表
     */
    @GetMapping("/{groupId}/students")
    public Result<List<StudentCourseRelation>> getCourseGroupStudents(@PathVariable Long groupId) {
        try {
            List<StudentCourseRelation> relations = studentCourseRelationService.getStudentsByGroupId(groupId);
            return Result.success(relations);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 老师将某学生从课程中移除（本质为将选课状态置为 DROPPED）
     * POST /api/course/group/{groupId}/students/{studentId}/drop
     */
    @PostMapping("/{groupId}/students/{studentId}/drop")
    public Result<Void> dropStudentFromGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        try {
            studentCourseRelationService.dropCourse(studentId, groupId);
            return Result.success("移除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("移除失败：" + e.getMessage());
        }
    }

    /**
     * 老师给已选课学生打分（打完分自动置为 COMPLETED）
     * POST /api/course/group/{groupId}/students/{studentId}/grade?grade=90
     */
    @PostMapping("/{groupId}/students/{studentId}/grade")
    public Result<StudentCourseRelation> gradeStudent(
            @PathVariable Long groupId,
            @PathVariable Long studentId,
            @RequestParam BigDecimal grade
    ) {
        try {
            StudentCourseRelation updated = studentCourseRelationService.gradeStudent(groupId, studentId, grade);
            return Result.success("打分成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("打分失败：" + e.getMessage());
        }
    }

    /**
     * 老师将学生从课程中移除（物理删除选课关系）
     * DELETE /api/course/group/{groupId}/students/{studentId}
     */
    @DeleteMapping("/{groupId}/students/{studentId}")
    public Result<Void> removeStudentFromGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        try {
            studentCourseRelationService.removeStudentFromGroup(groupId, studentId);
            return Result.success("移除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("移除失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取课程组的申请列表（按状态筛选）
     * GET /api/course/group/{groupId}/applications?status=ENROLLING
     */
    @GetMapping("/{groupId}/applications")
    public Result<List<StudentCourseRelation>> getApplications(
            @PathVariable Long groupId,
            @RequestParam(required = false) String status) {
        try {
            List<StudentCourseRelation> relations = studentCourseRelationService.getStudentsByGroupIdAndStatus(groupId, status);
            return Result.success(relations);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 老师审核学生选课申请（通过）
     * POST /api/course/group/{groupId}/students/{studentId}/approve
     */
    @PostMapping("/{groupId}/students/{studentId}/approve")
    public Result<StudentCourseRelation> approveEnrollment(
            @PathVariable Long groupId,
            @PathVariable Long studentId) {
        try {
            StudentCourseRelation relation = studentCourseRelationService.approveEnrollment(groupId, studentId);
            return Result.success("审核通过", relation);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("审核失败：" + e.getMessage());
        }
    }
    
    /**
     * 老师审核学生选课申请（拒绝）
     * POST /api/course/group/{groupId}/students/{studentId}/reject
     */
    @PostMapping("/{groupId}/students/{studentId}/reject")
    public Result<Void> rejectEnrollment(
            @PathVariable Long groupId,
            @PathVariable Long studentId) {
        try {
            studentCourseRelationService.rejectEnrollment(groupId, studentId);
            return Result.success("已拒绝申请");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("拒绝失败：" + e.getMessage());
        }
    }
}


