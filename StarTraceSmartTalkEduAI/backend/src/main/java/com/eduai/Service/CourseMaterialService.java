package com.eduai.Service;

import com.eduai.pojo.dto.CourseMaterialDTO;
import com.eduai.pojo.entity.CourseMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程课件/文章服务
 */
public interface CourseMaterialService {

    List<CourseMaterial> listByGroupId(Long groupId);

    CourseMaterial create(Long groupId, CourseMaterialDTO dto, Long uploaderId);

    CourseMaterial update(Long materialId, CourseMaterialDTO dto);

    void delete(Long materialId);

    /**
     * 上传本地文件，返回可访问 URL（http(s)开头）
     */
    String uploadFile(MultipartFile file);
}


