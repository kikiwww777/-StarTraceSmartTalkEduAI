package com.eduai.Service.impl;

import com.eduai.Mapper.CourseMaterialMapper;
import com.eduai.Service.CourseMaterialService;
import com.eduai.pojo.dto.CourseMaterialDTO;
import com.eduai.pojo.entity.CourseMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourseMaterialServiceImpl implements CourseMaterialService {

    @Autowired
    private CourseMaterialMapper courseMaterialMapper;

    @Value("${sim.upload.material-dir:./uploads/course-material}")
    private String materialUploadDir;

    @Override
    public List<CourseMaterial> listByGroupId(Long groupId) {
        if (groupId == null) throw new RuntimeException("groupId不能为空");
        return courseMaterialMapper.selectByGroupId(groupId);
    }

    @Override
    public CourseMaterial create(Long groupId, CourseMaterialDTO dto, Long uploaderId) {
        if (groupId == null) throw new RuntimeException("groupId不能为空");
        if (dto == null) throw new RuntimeException("内容不能为空");

        String title = dto.getTitle() == null ? "" : dto.getTitle().trim();
        if (title.isEmpty()) throw new RuntimeException("标题不能为空");
        if (title.length() > 200) throw new RuntimeException("标题过长");

        String type = dto.getType() == null ? "" : dto.getType().trim().toUpperCase();
        if (type.isEmpty()) throw new RuntimeException("类型不能为空");

        // 前端约定：LINK 必须有 url；FILE 建议有 url（上传后回填）
        String url = dto.getUrl() == null ? null : dto.getUrl().trim();
        if ("LINK".equals(type) && (url == null || url.isEmpty())) {
            throw new RuntimeException("外链类型需要填写链接");
        }
        if ("FILE".equals(type) && (url == null || url.isEmpty())) {
            throw new RuntimeException("文件类型需要先上传文件或填写文件链接");
        }
        if (url != null && !url.isEmpty()
                && !(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/"))) {
            throw new RuntimeException("链接格式不正确（需以 http(s):// 开头，或为站内相对路径）");
        }

        CourseMaterial material = new CourseMaterial();
        material.setGroupId(groupId);
        material.setUploaderId(uploaderId);
        material.setTitle(title);
        material.setType(type);
        material.setContent(dto.getContent());
        material.setUrl(url);
        LocalDateTime now = LocalDateTime.now();
        material.setCreatedAt(now);
        material.setUpdatedAt(now);

        int rows = courseMaterialMapper.insert(material);
        if (rows <= 0) throw new RuntimeException("创建失败");
        return material;
    }

    @Override
    public CourseMaterial update(Long materialId, CourseMaterialDTO dto) {
        if (materialId == null) throw new RuntimeException("materialId不能为空");
        if (dto == null) throw new RuntimeException("内容不能为空");

        CourseMaterial existing = courseMaterialMapper.selectById(materialId);
        if (existing == null) throw new RuntimeException("内容不存在");

        String title = dto.getTitle() == null ? "" : dto.getTitle().trim();
        if (title.isEmpty()) throw new RuntimeException("标题不能为空");
        if (title.length() > 200) throw new RuntimeException("标题过长");

        String type = dto.getType() == null ? "" : dto.getType().trim().toUpperCase();
        if (type.isEmpty()) throw new RuntimeException("类型不能为空");

        String url = dto.getUrl() == null ? null : dto.getUrl().trim();
        if ("LINK".equals(type) && (url == null || url.isEmpty())) {
            throw new RuntimeException("外链类型需要填写链接");
        }
        if ("FILE".equals(type) && (url == null || url.isEmpty())) {
            throw new RuntimeException("文件类型需要先上传文件或填写文件链接");
        }
        if (url != null && !url.isEmpty()
                && !(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/"))) {
            throw new RuntimeException("链接格式不正确（需以 http(s):// 开头，或为站内相对路径）");
        }

        CourseMaterial update = new CourseMaterial();
        update.setId(materialId);
        update.setTitle(title);
        update.setType(type);
        update.setContent(dto.getContent());
        update.setUrl(url);
        update.setUpdatedAt(LocalDateTime.now());

        int rows = courseMaterialMapper.update(update);
        if (rows <= 0) throw new RuntimeException("更新失败");
        return courseMaterialMapper.selectById(materialId);
    }

    @Override
    public void delete(Long materialId) {
        if (materialId == null) throw new RuntimeException("materialId不能为空");
        CourseMaterial existing = courseMaterialMapper.selectById(materialId);
        if (existing == null) throw new RuntimeException("内容不存在");
        int rows = courseMaterialMapper.deleteById(materialId);
        if (rows <= 0) throw new RuntimeException("删除失败");
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }

        // 文件大小限制：20MB
        long maxSize = 20 * 1024 * 1024; // 20MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件大小超过限制，最大允许20MB");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
        // 关键点：把相对路径规范化为绝对路径，避免 Windows/Tomcat 临时工作目录导致找不到路径
        Path dir = Paths.get(materialUploadDir).toAbsolutePath().normalize();
        Path dest = dir.resolve(storedName).normalize();
        try {
            Files.createDirectories(dest.getParent());
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败：" + e.getMessage());
        }

        // 注意：WebMvcConfiguration 已把 /uploads/** 映射到 ${sim.upload.base-dir}
        // materialUploadDir 默认是 ./uploads/course-material，因此可访问路径固定为：
        // /uploads/course-material/{storedName}
        return "/uploads/course-material/" + storedName;
    }
}


