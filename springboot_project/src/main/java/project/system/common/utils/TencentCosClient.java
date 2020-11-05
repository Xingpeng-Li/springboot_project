package com.project.common.utils;
/*
@author DKR
@CreateDate 2020-7-14
@update 2020-7-14 DKR 实现文件的上传，复制，删除
        2020-7-15 DKR 修改文件上传接口的参数，修复异常处理
        2020-7-23 DKR 添加水印文件上传接口
@description 与腾讯云对象存储服务对接的类
*/
import com.project.domain.Cloudfile;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.CloudfileMapper;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.CopyObjectRequest;
import com.qcloud.cos.model.CopyObjectResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.File;
@Component
public class TencentCosClient {
    @Value("${tx.cos.secretId}")
    private String secretId;
    @Value("${tx.cos.secretKey}")
    private String secretKey;
    @Value("${tx.cos.bucketName}")
    private String bucketName;
    @Value("${tx.cos.region}")
    private String region;
    @Value("${tx.cos.path}")
    private String path;
    @Value("${tx.cos.prefix}")
    private String prefix;
    @Resource
    CloudfileMapper cloudfileMapper;
    //文件上传函数
    public String uploadCloudFile(MultipartFile file, String userId,String filename)
    {
        //腾讯云对象存储配置
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient=new COSClient(cred,clientConfig);
        String bucketName = this.bucketName;
        try {
            //将multipartFile转换为file
            File tempFile=File.createTempFile("temp",null);
            file.transferTo(tempFile);
            //设置对象的key值，为每个用户单独建立文件目录
            String key = prefix+"/"+userId+"/"+filename;
            //上传文件到存储桶
            PutObjectResult putObjectResult = cosClient.putObject(bucketName, key, tempFile);
            //String etag = putObjectResult.getETag();  // 获取文件的 etaG
            //System.out.println(etag);
            return path+key;
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new BusinessException(EmBusinessError.FILE_UPLOAD_FAIL);//抛出上传异常
        }
        finally {
            cosClient.shutdown();//关闭客户端对象
        }
    }
    public String uploadWatermarkFile(File file,String middleName,String fileName)
    {
        //腾讯云对象存储配置
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient=new COSClient(cred,clientConfig);
        String bucketName = this.bucketName;
        try {
            //设置对象的key值，单独建立文件目录
            String key = prefix+"/"+middleName+"/"+fileName;
            //上传文件到存储桶
            PutObjectResult putObjectResult = cosClient.putObject(bucketName, key, file);
            //String etag = putObjectResult.getETag();  // 获取文件的 etaG
            //System.out.println(etag);
            return path+key;
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw  new BusinessException(EmBusinessError.FILE_UPLOAD_FAIL);//抛出上传异常
        }
        finally {
            cosClient.shutdown();//关闭客户端对象
        }
    }
    //对象复制函数
    public boolean copyCloudFile(Cloudfile cloudfile, Integer userId){
        //腾讯云对象存储配置
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient=new COSClient(cred,clientConfig);
        //获取要复制的文件的对象key值
        String srcKey = prefix+"/"+cloudfile.getUserId()+"/"+cloudfile.getFileName();
        //设置保存到用户自己目录下的对象的key值
        String destKey=prefix+"/"+userId+"/"+cloudfile.getFileName();
       try{
           //调用接口，复制对象
           CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, srcKey, bucketName, destKey);
           CopyObjectResult copyObjectResult = cosClient.copyObject(copyObjectRequest);
          // System.out.println(copyObjectResult.getETag());
          //在数据库里添加记录
           cloudfile.setUserId(userId);
           cloudfile.setFileUrl(path+destKey);
           cloudfile.setFileUploadTime(null);
           cloudfile.setFileId(null);
           if(cloudfileMapper.insertSelective(cloudfile)==1)//添加成功
           return true;
           else throw new BusinessException(EmBusinessError.DB_ERROR);//添加失败，返回数据库错误
       }
       catch (CosClientException e)
       {
           e.getMessage();
           throw new BusinessException(EmBusinessError.FILE_COPY_FAIL);//接口错误，抛出文件复制异常
       }
       finally {
           cosClient.shutdown();//关闭客户端对象
       }
    }
    public boolean deleteFile(Cloudfile cloudfile) {
        //腾讯云对象存储配置
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient = new COSClient(cred, clientConfig);
        try {
            //删除对象
            cosClient.deleteObject(bucketName, prefix + "/" + cloudfile.getUserId() + "/" + cloudfile.getFileName());
            return true;
        } catch (CosClientException e) {
            System.out.println(e.getMessage());//删除失败
            return false;
        }finally {
            cosClient.shutdown();//关闭客户端对象
        }
    }
/*    public boolean deleteSpeechFile(String filename) {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient = new COSClient(cred, clientConfig);
        try {
            cosClient.deleteObject(bucketName, prefix + "/0/" +filename );
            return true;
        } catch (CosClientException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }*/
}
