package com.project.service.impl;

import com.project.common.utils.TencentCosClient;
import com.project.domain.Cloudfile;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.CloudfileMapper;
import com.project.service.CloudFileService;
import com.project.view.CloudFileView;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/*
@author DKR
@CreateDate 2020-7-14
@update 2020-7-14 实现上传、复制、删除、分页获取文件列表业务逻辑
        2020-7-16 修复了一些bug,实现了获取全部文件列表、搜索文件的业务逻辑
@description 云空间文件服务类
*/
@Service
public class CloudFileServiceImpl implements CloudFileService {
    @Resource
    TencentCosClient tencentCosClient;
    @Resource
    CloudfileMapper cloudfileMapper;
    @Override
    //上传文件
    public boolean uploadFile(MultipartFile file, Integer userId){
        Cloudfile cloudfile=new Cloudfile();
        if(file==null){
            throw new BusinessException(EmBusinessError.FILE_TRANSFER_FAIL);//文件为空
        }
        //设置cloudFile对象的属性值
        cloudfile.setFileName(file.getOriginalFilename());
        cloudfile.setFileSize((double)file.getSize());
        String suffix=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        System.out.println(suffix);
        cloudfile.setFileSuffix(suffix);
        //调用对象存储上传接口
        String url=tencentCosClient.uploadCloudFile(file,userId.toString(),file.getOriginalFilename());
        cloudfile.setFileUrl(url);
        cloudfile.setUserId(userId);
        if(cloudfileMapper.insertSelective(cloudfile)==1)//数据库插入成功,文件上传成功
        return  true;
        else
            throw new BusinessException(EmBusinessError.DB_ERROR);//抛出数据库异常
    }
    //文件复制
    @Override
    public  boolean copyFile(Cloudfile cloudfile, Integer userId){
        if(tencentCosClient.copyCloudFile(cloudfile,userId))
            return true;//文件复制成功，返回真
        else return false;//文件复制失败，返回假
    }
    @Override
    //分页获取文件
    public List<CloudFileView> getMyFiles(Integer userId, Integer pageNumber, Integer pageSize){
        Integer offset = (pageNumber - 1) * pageSize;//设置offset
        return cloudfileMapper.getCloudFilesLimited(userId,offset,pageSize).stream().
                map(this::convertFromCloudFileToView).collect(Collectors.toList());
    }
    @Override
    //根据Id获取文件
    public  Cloudfile getFileById(Integer fileId){
        return cloudfileMapper.selectByPrimaryKey(fileId);
    }
    @Override
    //删除文件
    public  boolean deleteFile(Integer fileId)
    {
        //找到数据库中的记录
        Cloudfile cloudfile=cloudfileMapper.selectByPrimaryKey(fileId);
        if(tencentCosClient.deleteFile(cloudfile))//调用接口，删除存储桶中的记录
        {
            if(cloudfileMapper.deleteByPrimaryKey(fileId)==1)//删除数据库里的记录
            return true;//删除成功
        else return false;//删除失败
        }
        else throw new BusinessException(EmBusinessError.FILE_DELETE_FAIL);//对象存储删除失败
    }
    @Override
    //获取用户的所有云空间文件目录
    public List<CloudFileView> getAllFiles(Integer userId){
        return cloudfileMapper.queryAllFiles(userId).stream().
                map(this::convertFromCloudFileToView).collect(Collectors.toList());
    }
    @Override
    //获取搜索云空间文件结果
    public List<CloudFileView> searchFiles(String key, Integer userId){
        return cloudfileMapper.searchFileByKey(key,userId).stream().map(
                this::convertFromCloudFileToView).collect(Collectors.toList());
    }
    private CloudFileView convertFromCloudFileToView(Cloudfile cloudfile)
    {
        if(cloudfile==null)
            return null;
        CloudFileView cloudFileView=new CloudFileView();
        BeanUtils.copyProperties(cloudfile,cloudFileView);
        SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cloudFileView.setFileUploadTime(time.format(cloudfile.getFileUploadTime()));
        System.out.println(cloudFileView.getFileUploadTime());
        return cloudFileView;
    }
}
