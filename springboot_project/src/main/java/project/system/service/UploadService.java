package com.project.service;

import com.project.domain.Post;
import org.springframework.web.multipart.MultipartFile;

/*
@author WL
@CreateDate 2020-7-16
@update
@description 上传文件/图片接口
*/
public interface UploadService {
    String uploadImage(MultipartFile file);

    String uploadFile(MultipartFile file);

    void uploadFie(MultipartFile file, Post post);
}
