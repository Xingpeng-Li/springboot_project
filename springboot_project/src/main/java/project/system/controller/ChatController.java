package project.system.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import project.system.common.utils.RequestUtil;
import project.system.domain.SocketMessage;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.UserMapper;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.LoginService;
import project.system.service.SocketMessageService;
import project.system.ws.utils.SocketMessageUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/*
@author WL
@CreateDate 2020-7-15
@update 2020-7-16
@description 提供聊天相关api
*/
@RestController
public class ChatController extends BaseController{

    @Resource
    private LoginService loginService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private SocketMessageService socketMessageService;

    @Resource
    private SocketMessageUtil socketMessageUtil;

     /*
     * 判断用户是否登录，若登录返回用户相关信息
     * */
    @GetMapping("/chat/join")
    public CommonReturnType joinChat(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                Map<String, Object> hm = new HashMap<>();
                hm.put("token", tokenInfoResponse);
                return CommonReturnType.create(hm);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    /*
     * 提供聊天记录，分为群聊和私聊的聊天记录
     * */
    @PostMapping("/chatRoom/group/message")
    public CommonReturnType getSocketMessageByDate(@RequestBody JSONObject jsonObject) {
        System.out.println(jsonObject);
        Date date = jsonObject.getDate("date");
        String launchId = jsonObject.getString("launchId");
        String receiveId = jsonObject.getString("receiveId");
        Integer companyId=jsonObject.getInteger("companyId");
        Integer deptId=jsonObject.getInteger("deptId");
        System.out.println(date);
        List<SocketMessage> list;
        if (launchId != null && receiveId != null) {
            list = socketMessageService.getSocketMessageByUser(launchId, receiveId, date);
        } else {
            list = socketMessageService.getSocketMessageByGroup(date,deptId,companyId);
        }
        return CommonReturnType.create(list.stream().map(socketMessageUtil::convertFromSocketMessage).collect(Collectors.toList()));
    }

    /*
     * 获取部门联系人
     * */
    @GetMapping("/chatRoom/users/relevant/{userId}")
    public CommonReturnType getSocketUserRelevant(@PathVariable String userId) {
        User user = userMapper.selectByPrimaryKey(Integer.parseInt(userId));
        User[] users = userMapper.selectByCompanyAndDept(user.getCompanyId(),user.getDeptId());
        return CommonReturnType.create(Arrays.stream(users).filter(t -> !t.getUserId().equals(Integer.parseInt(userId))).collect(Collectors.toList()));
    }
}
