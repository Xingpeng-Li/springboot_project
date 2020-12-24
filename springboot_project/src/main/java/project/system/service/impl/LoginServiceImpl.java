package project.system.service.impl;

import org.springframework.stereotype.Service;
import project.system.response.request.LoginRequest;
import project.system.response.response.TokenInfoResponse;
import project.system.service.LoginService;
import project.system.service.manager.SimpleCoreManager;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private SimpleCoreManager simpleCoreManager;

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
