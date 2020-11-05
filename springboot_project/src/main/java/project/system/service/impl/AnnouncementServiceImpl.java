package com.project.service.impl;

import com.project.common.utils.TencentCosClient;
import com.project.domain.Announcement;
import com.project.domain.User;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.AnnouncementMapper;
import com.project.mapper.UserMapper;
import com.project.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 @author:李星鹏
 @createDate:2020/7/21
 @description:发送公告、接收公告控制类
 */

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    UserMapper userMapper;
    @Resource
    AnnouncementMapper announcementMapper;
    @Resource
    TencentCosClient tencentCosClient;

    @Override
    public List<Map<String,Object>> getAnnouncement(String type, Integer userId) {
        List<Map<String,Object>> announcements = new ArrayList<>();
        List<Announcement> announcementList;
        //sender为0表示公告为系统公告
        if("system".equals(type)){
            announcementList = announcementMapper.getSystemAnnouncement();
            for(Announcement announcement: announcementList){
                Map<String, Object> map = new HashMap<>();
                map.put("announcementId",announcement.getAnnouncementId());
                map.put("announcementTitle",announcement.getAnnouncementTitle());
                map.put("announcementBody",announcement.getAnnouncementBody());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String announcementTime = format.format(announcement.getAnnouncementDate());
                map.put("announcementTime",announcementTime);
                map.put("announcementFile",announcement.getAnnouncementUrl());
                announcements.add(map);
            }
        }
        else{
            User user = userMapper.selectByPrimaryKey(userId);
            //根据公告类型设置sender
            Integer sender = "company".equals(type) ? user.getCompanyId():user.getDeptId();
            announcementList = announcementMapper.getAnnouncement(type, sender);
            for(Announcement announcement: announcementList){
                Map<String, Object> map = new HashMap<>();
                map.put("announcementId",announcement.getAnnouncementId());
                map.put("announcementTitle",announcement.getAnnouncementTitle());
                map.put("announcementBody",announcement.getAnnouncementBody());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String announcementTime = format.format(announcement.getAnnouncementDate());
                map.put("announcementTime",announcementTime);
                map.put("announcementFile",announcement.getAnnouncementUrl());
                announcements.add(map);
            }
        }
        return announcements;
    }

    //发送公告
    @Override
    public void sendAnnouncement(String type, String title, String body, Integer userId, MultipartFile file) {
        User user = userMapper.selectByPrimaryKey(userId);

        //非企业管理员不能发送企业公告
        if("company".equals(type) && !("admin").equals(user.getUserPosition())){
            throw new BusinessException(EmBusinessError.USER_WITHOUT_AUTHORITY);
        }
        //员工不能发送部门公告
        if("dept".equals(type) && !("admin").equals(user.getUserPosition()) && !("master".equals(user.getUserPosition()))){
            throw new BusinessException(EmBusinessError.USER_WITHOUT_AUTHORITY);
        }
        Announcement announcement = new Announcement();
        announcement.setAnnouncementTitle(title);
        announcement.setAnnouncementBody(body);
        announcement.setAnnouncementType(type);
        //若发送的是企业公告，则senderId为企业id;若发送的是部门公告，则senderId为部门id;
        Integer sender;
        if("company".equals(type)){
            sender = user.getCompanyId();
        }
        else {
            sender = user.getDeptId();
        }
        announcement.setAnnouncementSender(sender);
        //如果需要发送附件，则将附件上传到服务器并将url存入数据库
        String url = "";
        if(file != null){
            url = tencentCosClient.uploadCloudFile(file,type + sender,file.getOriginalFilename());
        }
        announcement.setAnnouncementUrl(url);
        announcementMapper.insertSelective(announcement);
    }
}
