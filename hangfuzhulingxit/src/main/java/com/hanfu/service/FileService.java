package com.hanfu.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     * @param file 文件
     * @return 访问URL
     */
    String upload(MultipartFile file);
}
