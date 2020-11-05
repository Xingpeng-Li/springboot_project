package project.system.service;


import org.springframework.web.multipart.MultipartFile;
import project.system.domain.Post;

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
