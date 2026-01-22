package com.hanfu.service.impl;

import com.hanfu.common.BusinessException;
import com.hanfu.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传服务实现
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 获取原始文件名和扩展名
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new BusinessException("文件名无效");
        }
        String ext = originalName.substring(originalName.lastIndexOf("."));
        
        // 验证文件类型
        String[] allowedTypes = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
        boolean allowed = false;
        for (String type : allowedTypes) {
            if (ext.toLowerCase().equals(type)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new BusinessException("只支持 jpg/jpeg/png/gif/webp 格式的图片");
        }
        
        // 生成新文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + ext;
        
        // 创建目录
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 保存文件
        File dest = new File(uploadPath + newFileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
        
        // 返回访问路径
        return "/uploads/" + newFileName;
    }
}
