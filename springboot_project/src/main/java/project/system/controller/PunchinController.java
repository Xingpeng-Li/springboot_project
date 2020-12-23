package project.system.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import project.system.common.utils.RequestUtil;
import project.system.domain.Notification;
import project.system.domain.Punchin;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.NotificationMapper;
import project.system.mapper.PunchinMapper;
import project.system.mapper.UserMapper;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.LoginService;
import project.system.service.PunchinService;
import project.system.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

/*
 @author:徐必成
 @createDate:2020/12/8
 @update：
 @description:和考勤打卡有关的功能都在这里面实现
 */

@RestController
public class PunchinController extends BaseController {
    @Resource
    PunchinService punchinService;
    @Resource
    LoginService loginService;
    @Resource
    UserMapper userMapper;
    @Resource
    PunchinMapper punchinMapper;
    @Resource
    NotificationMapper notificationMapper;
    @Resource
    UserService userService;

    //考勤打卡
    @RequestMapping("user/punchin")
    public Object punchin(HttpServletRequest request){
        //获取用户信息
        Punchin punchinResult = new Punchin();
        int userId;
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                userId = Integer.parseInt(tokenInfoResponse.getUserId());
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }

        Punchin punchin = punchinService.IsPunched(userId);
        Timestamp timestamp = new Timestamp((new Date()).getTime());
        //如果已经打过卡，则显示今日已打卡；
        if(punchin != null){
            throw new BusinessException(EmBusinessError.ALREADY_PUNCHIN);
        }else{
            //如果没打卡那么就添加打卡记录
            punchinResult.setUserId(userId);
            punchinResult.setPuchinTime(timestamp);
            punchinMapper.insertSelective(punchinResult);
            return CommonReturnType.create(null);
        }
    }

    //考勤打卡提醒
    @RequestMapping("punchin/notification")
    public CommonReturnType sendPunchinNotification(HttpServletRequest request){
        //获取用户信息
        int userId;
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                userId = Integer.parseInt(tokenInfoResponse.getUserId());
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }

        Timestamp timestamp = new Timestamp((new Date()).getTime());
        User user = userMapper.selectByPrimaryKey(userId);
        if(user.getCompanyId() != null && userService.isCompanyAdmin(userId)) {
            List<User> userList = userMapper.selectByCompanyId(user.getCompanyId());
            for (User user1 : userList) {
                Punchin punchin = punchinService.IsPunched(user1.getUserId());
                //如果未打卡则发送打卡消息
                if (punchin == null && userId != user1.getUserId()) {
                    Notification notification = new Notification();
                    notification.setNotificationSenderId(userId);
                    notification.setNotificationReceiverId(user1.getUserId());
                    notification.setNotificationTime(timestamp);
                    notification.setNotificationType("考勤打卡");
                    notification.setNotificationChecked("否");
                    notification.setNotificationBody("您今日尚未完成考勤打卡，请立即前往打卡!");

                    notificationMapper.insert(notification);
                }
            }
        }
        return CommonReturnType.create(null);
    }

    //考勤打卡统计,只有企业管理员有权限查看
    @RequestMapping("punchin/count")
    public CommonReturnType getPunchinCount(HttpServletRequest request){
        int userId;
        int companyId;
        Map<String, Integer> punchinCountingMap = new HashMap<>();
        List<Punchin> punchinList;
        List<User> userList;

        //获取当前日期年月日中的日
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH);

        //获取当前用户所属企业
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                userId = Integer.parseInt(tokenInfoResponse.getUserId());
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if(user.getUserPosition().equals("admin")){
            companyId = user.getCompanyId();
            //获取同一企业所有用户信息
            userList = userMapper.selectByCompanyId(companyId);
            int n = userList.size();
            int punchin = 0;
            int notPunchin;
            for(User user1:userList){
                punchinList = Arrays.asList(punchinMapper.selectByUserId(user1.getUserId()));
                for(Punchin punchin1: punchinList){
                    if(punchin1.getPuchinTime().getDate() == day)
                        punchin = punchin + 1;
                }
            }

            notPunchin = n - punchin;
            punchinCountingMap.put("已打卡",punchin);
            punchinCountingMap.put("未打卡",notPunchin);
            punchinCountingMap.put("总人数",n);

            return CommonReturnType.create(punchinCountingMap);
        }else{
            throw new BusinessException(EmBusinessError.UNAUTHORIZED);
        }

    }

    //系统自动定时向未打卡的用户发送提醒
    public CommonReturnType sendPunchinNotificationBySystem(HttpServletRequest request){
        List<User> userList = userMapper.selectAll();
        for(User user:userList){
            Punchin punchin = punchinService.IsPunched(user.getUserId());
            Timestamp timestamp = new Timestamp((new Date()).getTime());

            if(timestamp.getHours() >= 11){
                //如果未打卡则发送打卡消息
                if(punchin == null){
                    Notification notification = new Notification();
                    notification.setNotificationSenderId(0);
                    notification.setNotificationReceiverId(user.getUserId());
                    notification.setNotificationTime(timestamp);
                    notification.setNotificationType("考勤打卡");
                    notification.setNotificationChecked("否");
                    notification.setNotificationBody("您今日尚未完成考勤打卡，请立即前往打卡!");

                    notificationMapper.insert(notification);
                }
            }
        }

        return CommonReturnType.create(null);
    }

    //将数据库中punchin表中的数据输出为"Punchin报表.xlsx"excle文件
    //判断了是企业管理员还是部门管理员
    @RequestMapping(value = "/punchin", method = RequestMethod.GET)
    public CommonReturnType healthExcel(HttpServletResponse response, HttpServletRequest request) {
        List<Punchin> punchinList = punchinMapper.selectAll();
        //获取一个空表
        List<Punchin> punchinListSelect = new ArrayList<Punchin>();
        List<User> userList;
        //判断是否是企业管理员或部门管理员
        int userId;
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                userId = Integer.parseInt(tokenInfoResponse.getUserId());
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }

        User user = userMapper.selectByPrimaryKey(userId);

        //获取当前日期年月日中的日
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH);

        //如果是企业管理员,则可以导出本日的本企业所有员工的打卡记录
        if(userService.isCompanyAdmin(userId)){
            //获取同一公司的所有员工列表
            userList = userMapper.selectByCompanyId((user.getCompanyId()));
            for(User user1:userList){
                for(Punchin punchin: punchinList){
                    if(punchin.getPuchinTime().getDate() == day && punchin.getUserId() == user1.getUserId()){
                        punchinListSelect.add(punchin);
                    }
                }
            }
        }else if(userService.isDeptMaster(userId)){
            //如果是部门管理员则只能导入本部门今日的打卡情况
            userList = Arrays.asList(userMapper.selectByDeptId(user.getDeptId()));
            for(User user1:userList){
                for(Punchin punchin: punchinList ){
                    if((punchin.getPuchinTime().getDate() == day) && (punchin.getUserId() == user1.getUserId()))
                        punchinListSelect.add(punchin);
                }
            }
        }else{
            throw new BusinessException(EmBusinessError.UNAUTHORIZED);
        }
        XSSFWorkbook wb = punchinService.show(punchinListSelect);
        String fileName = "Punchin报表.xlsx";
        OutputStream outputStream = null;
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");

            //设置ContentType请求信息格式
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);

            outputStream = response.getOutputStream();
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return CommonReturnType.create(null);
    }
}
