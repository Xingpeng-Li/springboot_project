package com.project.service;

import com.project.domain.Cloudfile;
import com.project.view.CloudFileView;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
/*
@author DKR
@CreateDate 2020-7-14
@update 2020-7-14 实现上传、复制、删除、分页获取文件列表业务逻辑
        2020-7-16 修复了一些bug,实现了获取全部文件列表、搜索文件的业务逻辑
@description 云空间文件服务类
*/
public interface CloudFileService {
    boolean uploadFile(MultipartFile file, Integer userId);
    boolean copyFile(Cloudfile cloudfile, Integer userId);
    List<CloudFileView> getMyFiles(Integer userId, Integer pageNumber, Integer pageSize);
    Cloudfile getFileById(Integer fileId);
    boolean deleteFile(Integer fileId);
    List<CloudFileView> getAllFiles(Integer userId);
    List<CloudFileView> searchFiles(String key, Integer userId);
}
