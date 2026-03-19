package com.eduai.Controller;

import jakarta.servlet.http.HttpServletRequest;
import com.eduai.Service.CourseMaterialService;
import com.eduai.pojo.Result;
import com.eduai.pojo.dto.CourseMaterialDTO;
import com.eduai.pojo.dto.UploadFileResult;
import com.eduai.pojo.entity.CourseMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程课件/文章接口（与前端 teacherMaterialApi 对齐）
 */
@RestController
@RequestMapping("/api")
public class CourseMaterialController {

    @Autowired
    private CourseMaterialService courseMaterialService;

    /**
     * 列表：GET /api/course/group/{groupId}/materials
     */
    @GetMapping("/course/group/{groupId}/materials")
    public Result<List<CourseMaterial>> listByGroup(@PathVariable Long groupId) {
        try {
            return Result.success(courseMaterialService.listByGroupId(groupId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 新增：POST /api/course/group/{groupId}/materials
     */
    @PostMapping("/course/group/{groupId}/materials")
    public Result<CourseMaterial> create(
            @PathVariable Long groupId,
            @RequestBody CourseMaterialDTO dto,
            @RequestParam(required = false) Long uploaderId) {
        try {
            CourseMaterial created = courseMaterialService.create(groupId, dto, uploaderId);
            return Result.success("创建成功", created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新：PUT /api/course/material/{materialId}
     */
    @PutMapping("/course/material/{materialId}")
    public Result<CourseMaterial> update(@PathVariable Long materialId, @RequestBody CourseMaterialDTO dto) {
        try {
            CourseMaterial updated = courseMaterialService.update(materialId, dto);
            return Result.success("更新成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除：DELETE /api/course/material/{materialId}
     */
    @DeleteMapping("/course/material/{materialId}")
    public Result<Void> delete(@PathVariable Long materialId) {
        try {
            courseMaterialService.delete(materialId);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 上传文件：POST /api/course/material/upload
     * 返回：{ url: "http(s)://host:port/uploads/course-material/xxx.ext", filename: "xxx.ext" }
     */
    @PostMapping("/course/material/upload")
    public Result<UploadFileResult> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String relativeUrl = courseMaterialService.uploadFile(file);
            String absoluteUrl = toAbsoluteUrl(request, relativeUrl);

            UploadFileResult resp = new UploadFileResult();
            resp.setUrl(absoluteUrl);
            resp.setFilename(file != null ? file.getOriginalFilename() : null);
            return Result.success("上传成功", resp);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    private String toAbsoluteUrl(HttpServletRequest request, String url) {
        if (url == null || url.isEmpty()) return url;
        if (url.startsWith("http://") || url.startsWith("https://")) return url;
        if (!url.startsWith("/")) url = "/" + url;

        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        boolean defaultPort = ("http".equalsIgnoreCase(scheme) && port == 80)
                || ("https".equalsIgnoreCase(scheme) && port == 443);
        String base = defaultPort ? (scheme + "://" + host) : (scheme + "://" + host + ":" + port);
        return base + url;
    }
}


