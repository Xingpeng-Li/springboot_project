package com.project.service.impl;

import com.project.common.utils.TencentCosClient;
import com.project.domain.Notification;
import com.project.domain.User;
import com.project.domain.WorkReport;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.NotificationMapper;
import com.project.mapper.UserMapper;
import com.project.mapper.WorkReportMapper;
import com.project.service.ReportService;
import com.tencentcloudapi.billing.v20180709.models.BusinessSummaryOverviewItem;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static com.project.common.utils.WaterMarkUtil.addWatermarkToWord;

/*
 @author:李星鹏
 @createDate:2020/7/14
 @description:发送工作报告、批阅报告
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    UserMapper userMapper;
    @Resource
    WorkReportMapper workReportMapper;
    @Resource
    NotificationMapper notificationMapper;
    @Resource
    TencentCosClient tencentCosClient;

    @Override
    public void sendReport(MultipartFile mFile,String addWatermark, String type, Integer reporter, String approverPhoneNumber, String secondApproverPhoneNumber) throws IOException {
        //文件名：发送者姓名-type-时间.doc
        User reporterUser = userMapper.selectByPrimaryKey(reporter);

        //生成文件名中的时间
        Calendar now = Calendar.getInstance();
        String time = "" + now.get(Calendar.YEAR) + (now.get(Calendar.MONTH)+1)
                + now.get(Calendar.DAY_OF_MONTH) + " " + now.get(Calendar.HOUR_OF_DAY)
                + now.get(Calendar.MINUTE);

        //获取文件后缀
        String filename = mFile.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf(".")+1);
        filename = reporterUser.getUserName()+"-"+type+"-"+time+"."+suffix;

        //用户没有按照模板上传文件
        if(!"docx".equals(suffix)){
            throw new BusinessException(EmBusinessError.FILE_ERROR);
        }
        String url;
        //添加水印并上传
        if("0".equals(addWatermark)){
            File file = addWatermarkToWord(mFile,reporterUser.getUserName());
            //上传文件
            url = tencentCosClient.uploadWatermarkFile(file, reporter.toString(), filename);
            //String url = tencentCosClient.uploadCloudFile(mFile,);
            // 会在本地产生临时文件，用完后需要删除
            if (file.exists()) {
                file.delete();
            }
        }
        else {
            url = tencentCosClient.uploadCloudFile(mFile, reporter.toString(), filename);
        }

        Integer approver = userMapper.getUserByPhoneNumber(approverPhoneNumber).getUserId();
        Integer secondApprover = userMapper.getUserByPhoneNumber(secondApproverPhoneNumber).getUserId();
        WorkReport report = new WorkReport();
        report.setReporterId(reporter);
        report.setReportApprover(approver);
        report.setReportSecondApprover(secondApprover);
        report.setReportType(type);
        report.setReportUrl(url);
        Timestamp timestamp = new Timestamp(new Date().getTime());
        report.setReportTime(timestamp);
        workReportMapper.insert(report);

        //提醒批阅人
        Notification notification = new Notification();
        notification.setNotificationSenderId(reporter);
        notification.setNotificationReceiverId(approver);
        notification.setNotificationType("待批阅报告");
        notification.setNotificationChecked("否");
        notification.setNotificationBody(url);
        notification.setNotificationTime(timestamp);
        notificationMapper.insert(notification);

        //提醒抄送人
        Notification notification2 = new Notification();
        notification2.setNotificationSenderId(reporter);
        notification2.setNotificationReceiverId(secondApprover);
        notification2.setNotificationType("待查看报告");
        notification2.setNotificationChecked("否");
        notification2.setNotificationBody(url);
        notification2.setNotificationTime(timestamp);
        notificationMapper.insert(notification2);

    }

    @Override
    public String getReportUrl(Integer notificationId) {
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);
        //用户点击下载报告后就将消息设置为已阅
        notification.setNotificationChecked("是");
        notificationMapper.updateByPrimaryKey(notification);
        return notification.getNotificationBody();
    }

    @Override
    public void returnReport(MultipartFile mFile, Integer notificationId) throws IOException {

        Timestamp timestamp = new Timestamp(new Date().getTime());

        //提醒发送者
        Notification notification = notificationMapper.selectByPrimaryKey(notificationId);
        Notification notification2 = new Notification();

        //获取文件后缀
        String originalFilename = mFile.getOriginalFilename();
        String filename = originalFilename.substring(0,originalFilename.lastIndexOf("."));
        filename = filename + "-return";
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //文件上传
        String url = tencentCosClient.uploadCloudFile(mFile,notification.getNotificationReceiverId().toString(),filename + "." + suffix);

        notification2.setNotificationSenderId(notification.getNotificationReceiverId());
        notification2.setNotificationReceiverId(notification.getNotificationSenderId());
        notification2.setNotificationType("已批阅报告");
        notification2.setNotificationChecked("否");
        notification2.setNotificationBody(url);
        notification2.setNotificationTime(timestamp);
        notificationMapper.insert(notification2);

    }

//    private void inputStreamToFile(InputStream ins, File file) throws IOException{
//        OutputStream os = new FileOutputStream(file);
//        int bytesRead = 0;
//        byte[] buffer = new byte[8192];
//        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
//            os.write(buffer, 0, bytesRead);
//        }
//        os.close();
//        ins.close();
//    }
}
