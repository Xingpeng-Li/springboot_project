package com.project.service.impl;

import com.project.domain.Post;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.PostMapper;
import com.project.service.UploadService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/*
@author WL
@CreateDate 2020-7-16
@update 2020-7-17
@description 具体实现发送图片和文件的相关操作
*/
@Service
public class UploadServiceImpl implements UploadService {

    @Value("${tx.cos.secretId}")
    private String accessKey;

    @Value("${tx.cos.secretKey}")
    private String secretKey;

    @Value("${tx.cos.region}")
    private String bucket;

    @Value("${tx.cos.bucketName}")
    private String bucketName;

    @Value("${tx.cos.path}")
    private String path;

    @Value("${tx.cos.prefix}")
    private String qianzhui;

    @Resource
    private PostMapper postMapper;

    @Override
    public String uploadImage(MultipartFile file) {
        // 支持的文件类型
        List<String> suffixes = Arrays.asList("image/png", "image/jpeg", "image/jpg", "image/gif", "image/webp", "image/bmp");
        String filePath = "/chat/images/";
        return upload(file, suffixes, filePath);
    }

    public String uploadFile(MultipartFile file) {
        // 支持的文件类型
        List<String> suffixes = Arrays.asList(
                "text/html",
                "text/plain",
                "application/javascript",
                "text/css",
                "application/x-zip-compressed",
                "application/zip",
                "application/gzip",
                "application/pdf",
                "application/vnd.android.package-archive",
                "application/msword",
                "application/octet-stream",
                "application/x-msdownload",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        String filePath = "/chat/files/";
        return upload(file, suffixes, filePath);
    }

    @Override
    public void uploadFie(MultipartFile file, Post post) {
        // 支持的文件类型
        List<String> suffixes = Arrays.asList("image/png", "image/jpeg", "image/jpg", "image/gif", "image/webp", "image/bmp");
        String filePath = "/post/images/";
        String imgUrl= upload(file, suffixes, filePath);
        post.setPostImage(imgUrl);
        int affectedRows = postMapper.insertSelective(post);
        if(affectedRows==0)
            throw new BusinessException(EmBusinessError.DB_ERROR);
    }

    public String upload(MultipartFile file, List<String> suffixes, String filePath) {
        COSClient cosclient = null;
        try {
            // 1、图片文件信息校验
            // 1)校验文件类型
            String type = file.getContentType(); //获取文件格式
            System.out.println("文件类型：" + type);
            if (!suffixes.contains(type)) {
                System.out.println("文件类型不匹配");
                throw new BusinessException(EmBusinessError.FILE_TYPE_ERROR);
            }

            // 2)校验图片内容
            if ("/telecommuting/chat/images/".equals(filePath)) {
                BufferedImage image = null;
                image = ImageIO.read(file.getInputStream());
                if (image == null) {
                    System.out.println("上传失败，文件内容不符合要求");
                    throw new BusinessException(EmBusinessError.FILE_CONTENT_ERROR);
                }
            }

            String oldFileName = file.getOriginalFilename();
            String postfix = Objects.requireNonNull(oldFileName).substring(oldFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + postfix;

            COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
            ClientConfig clientConfig = new ClientConfig(new Region(bucket));
            cosclient = new COSClient(cred, clientConfig);
            String bucketName = this.bucketName;
            File localFile = null;

            localFile = File.createTempFile("temp", null);
            file.transferTo(localFile);
            String key = this.qianzhui + filePath + newFileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            return this.path + key;
        } catch (IOException e) {
            throw new BusinessException(EmBusinessError.FILE_UPLOAD_ERROR);
        } finally {
            if (cosclient != null) {
                cosclient.shutdown();
            }
        }
    }
}
