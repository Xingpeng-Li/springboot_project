package project.system.service.impl;



import org.springframework.stereotype.Service;
import project.system.response.request.LoginRequest;
import project.system.response.response.TokenInfoResponse;
import project.system.service.LoginService;
import project.system.service.manager.SimpleCoreManager;

import javax.annotation.Resource;
import java.util.Objects;

/*
@author WL
@CreateDate 2020-7-10
@update
@description 登录接口简单实现，与视频会议相关
*/
@Service
public class SimpleLoginServiceImpl implements LoginService {

    @Resource
    private SimpleCoreManager simpleCoreManager;

    //清除浏览器缓存
    @Override
    public TokenInfoResponse logout(LoginRequest loginParam) {
        if (Objects.nonNull(loginParam) && Objects.nonNull(loginParam.getToken())) {
            simpleCoreManager.removeToken(loginParam.getToken());
        }
        return null;
    }

    //检查用户是否登录
    @Override
    public TokenInfoResponse checkLogin(String token) {
        if (Objects.isNull(token)) {
            return null;
        }
        return simpleCoreManager.getReadOnlyTokenInfo().get(token);
    }

    //用户登录信息写入服务器缓存
    @Override
    public void setLoginResult(String token, TokenInfoResponse tokenInfoResponse) {
        simpleCoreManager.addTokenInfo(token, tokenInfoResponse);
    }
}
