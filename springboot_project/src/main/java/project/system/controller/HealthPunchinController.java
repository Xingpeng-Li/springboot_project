package project.system.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import project.system.common.utils.RequestUtil;
import project.system.domain.HealthPunchin;
import project.system.domain.Notification;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.HealthPunchinMapper;
import project.system.mapper.NotificationMapper;
import project.system.mapper.UserMapper;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.HealthService;
import project.system.service.LoginService;
import project.system.service.TokenService;
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
 @createDate:2020/7/10
 @update：2020/7/14
 @description:增加统计和提醒功能，修改打卡和报表中的权限和添加字段的问题

 @update：2020/7/15
 @description:权限控制

 API:
 /health/punchin--浏览器提示导出“Health报表.xlsx”
   health.html--进入自己设计的一个前端表格界面，填入相应信息之后点击确认，会将填入的健康打卡数据保存到数据库中
   然后跳转/user/health界面显示跳转成功
 */

@RestController
public class HealthPunchinController extends BaseController {
    @Resource
    HealthPunchinMapper healthPunchinMapper;
    @Resource
    UserMapper userMapper;
    @Resource
    HealthService healthService;
    @Resource
    UserService userService;
    @Resource
    private TokenService tokenService;
    @Resource
    private LoginService loginService;
    @Resource
    NotificationMapper notificationMapper;


    //将数据库中healthpunchin表中的数据输出为"Health报表.xlsx"excle文件
    //判断了是企业管理员还是部门管理员
    @RequestMapping(value = "/health/punchin", method = RequestMethod.GET)
    public CommonReturnType healthExcel(HttpServletResponse response, HttpServletRequest request) {
        List<HealthPunchin> healthPunchinList = Arrays.asList(healthPunchinMapper.selectAll());
        //获取一个空表
        List<HealthPunchin> healthPunchinListSelect = new ArrayList<HealthPunchin>();
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
                for(HealthPunchin healthPunchin: healthPunchinList){
                    if(healthPunchin.getPunchinDate().getDate() == day && healthPunchin.getUserId() == user1.getUserId()){
                        healthPunchinListSelect.add(healthPunchin);
                    }
                }
            }
        }else if(userService.isDeptMaster(userId)){
            //如果是部门管理员则只能导入本部门今日的打卡情况
            userList = Arrays.asList(userMapper.selectByDeptId(user.getDeptId()));
            for(User user1:userList){
                for(HealthPunchin healthPunchin: healthPunchinList ){
                    if((healthPunchin.getPunchinDate().getDate() == day) && (healthPunchin.getUserId() == user1.getUserId()))
                        healthPunchinListSelect.add(healthPunchin);
                }
            }
        }else{
            throw new BusinessException(EmBusinessError.UNAUTHORIZED);
        }
        XSSFWorkbook wb = healthService.show(healthPunchinListSelect);
        String fileName = "Health报表.xlsx";
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

    //进行健康打卡，将网页中的数据存储到数据库中
    @GetMapping(value = "/user/health")
    public Object health(HttpServletRequest request) {
        //从前端获取打卡参数
        String usertemperature = request.getParameter("usertemperature");
        String usercity = request.getParameter("usercity");
        String userprovince = request.getParameter("userprovince");
        String HealthStatus = request.getParameter("HealthStatus");
        String ContactSuspectedCase = request.getParameter("ContactSuspectedCase");
        String PunchinNote = request.getParameter("PunchinNote");
        //后续需要添加异常处理
        HealthPunchin healthPunchin = new HealthPunchin();
        //后续需要从前端获取username或者user_id

        healthPunchin.setUserBodyTemp(Double.parseDouble(usertemperature));
        healthPunchin.setCity(usercity);
        healthPunchin.setProvince(userprovince);
        healthPunchin.setUserHealthStatus(HealthStatus);
        healthPunchin.setContactSuspectedCase(ContactSuspectedCase);
        healthPunchin.setPunchinNote(PunchinNote);

        //获取当前时间
        Date date = new Date();
        //返回的时间形式为2020-07-10 22:45:36.484
        Timestamp timestamp = new Timestamp(date.getTime());
        healthPunchin.setPunchinDate(timestamp);

        //isExpiration(String token)
        //从cookie中获取token的值，进而获取用户userId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                String userId = tokenInfoResponse.getUserId();

                //判断用户一日之内是否打过卡
                List<HealthPunchin> healthPunchinList = Arrays.asList(healthPunchinMapper.selectByUserID(Integer.parseInt(userId)));
                //如果一日之内已经打过卡，则删除今天的打卡记录，然后再插入最新的记录
                if(healthPunchinList.size()>0){
                    for(HealthPunchin punchin:healthPunchinList){
                        if(punchin.getPunchinDate().getDate() == timestamp.getDate())
                            //删除今天的打卡记录
                            healthPunchinMapper.deleteByPrimaryKey(punchin.getPunchinId());
                    }
                }
                healthPunchin.setUserId(Integer.parseInt(userId));
                //插入最新的打卡记录
                healthPunchinMapper.insert(healthPunchin);
                return CommonReturnType.create(null);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    //健康打卡人数统计
    //返回一日之内打卡情况人数统计
    @RequestMapping("health/count")
    public CommonReturnType getDeptUser(HttpServletRequest request){
        int userId;
        int companyId;
        Map<String, Integer> healthCountingMap = new HashMap<>();
        List<HealthPunchin> healthPunchinList;
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
        companyId = user.getCompanyId();
        //获取同一企业所有用户信息
        userList = userMapper.selectByCompanyId(companyId);
        int n = userList.size();
        int punchin = 0;
        int notPunchin;
        for(User user1:userList){
            healthPunchinList = Arrays.asList(healthPunchinMapper.selectByUserID(user1.getUserId()));
            for(HealthPunchin healthPunchin: healthPunchinList){
                if(healthPunchin.getPunchinDate().getDate() == day)
                    punchin = punchin + 1;
            }
        }

        notPunchin = n - punchin;
        healthCountingMap.put("已打卡",punchin);
        healthCountingMap.put("未打卡",notPunchin);
        healthCountingMap.put("总人数",n);

        return CommonReturnType.create(healthCountingMap);
    }

    //健康打卡提醒(管理员向所有人发出)
    @RequestMapping("health/notification")
    public CommonReturnType sendHealthPunchinNotification(HttpServletRequest request){
        //HealthPunchin healthPunchin = new HealthPunchin();

        Timestamp timestamp = new Timestamp((new Date()).getTime());
        //判断用户是否打过卡
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                String userId = tokenInfoResponse.getUserId();

                User user = userMapper.selectByPrimaryKey(Integer.parseInt(userId));

                //如果是企业管理员，才可以一键发送提醒
                if(user.getCompanyId() != null && userService.isCompanyAdmin(Integer.parseInt(userId))){
                    List<User> userList = userMapper.selectByCompanyId(user.getCompanyId());
                    //对每个用户发送考勤打卡提醒
                    for(User user1:userList) {
                        //判断用户一日之内是否打过卡
                        List<HealthPunchin> healthPunchinList = Arrays.asList(healthPunchinMapper.selectByUserID(user1.getUserId()));
                        if (healthPunchinList.size() > 0) {
                            for (HealthPunchin punchin : healthPunchinList) {
                                if (punchin.getPunchinDate().getDate() == timestamp.getDate()) {
                                    //不设置提醒打卡
                                    //return CommonReturnType.create(null);
                                    break;
                                }
                            }
                        }else if(Integer.parseInt(userId) != user1.getUserId()){
                            Notification notification = new Notification();
                            notification.setNotificationReceiverId(user1.getUserId());
                            //id为0表示系统发送的消息
                            notification.setNotificationSenderId(user.getUserId());
                            notification.setNotificationType("健康打卡");
                            notification.setNotificationChecked("否");
                            notification.setNotificationTime(timestamp);
                            notification.setNotificationBody("您还未完成今日的健康打卡，请立即前往打卡！");
                            //插入到消息数据库中
                            notificationMapper.insertSelective(notification);
                        }
                    }
                }else{
                    throw new BusinessException(EmBusinessError.UNAUTHORIZED);
                }
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        }
        return CommonReturnType.create(null);
    }

    //系统自动发送提醒
    public CommonReturnType sendHealthPunchinNotificationBySystem(HttpServletRequest request){
        Timestamp timestamp = new Timestamp((new Date()).getTime());
        if(timestamp.getHours() >= 11){
            //判断用户是否打过卡
            List<User> userList = userMapper.selectAll();
            //对每个用户发送考勤打卡提醒
            for(User user1:userList) {
                //判断用户一日之内是否打过卡
                List<HealthPunchin> healthPunchinList = Arrays.asList(healthPunchinMapper.selectByUserID(user1.getUserId()));
                if (healthPunchinList.size() > 0) {
                    for (HealthPunchin punchin : healthPunchinList) {
                        if (punchin.getPunchinDate().getDate() == timestamp.getDate()) {
                            //不设置提醒打卡
                            //return CommonReturnType.create(null);
                            break;
                        }
                    }
                }else {
                    Notification notification = new Notification();
                    notification.setNotificationReceiverId(user1.getUserId());
                    //id为0表示系统发送的消息
                    notification.setNotificationSenderId(0);
                    notification.setNotificationType("健康打卡");
                    notification.setNotificationChecked("否");
                    notification.setNotificationTime(timestamp);
                    notification.setNotificationBody("您还未完成今日的健康打卡，请立即前往打卡！");
                    //插入到消息数据库中
                    notificationMapper.insertSelective(notification);
                }
            }
        }
        return CommonReturnType.create(null);
    }


}
