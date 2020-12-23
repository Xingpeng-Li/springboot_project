package project.system.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import project.system.common.utils.RequestUtil;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.ImportUserService;
import project.system.service.LoginService;
import project.system.service.TokenService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/*
 * @author zws
 * @description 实现导入通讯录逻辑功能
 * @create 2020/11/18 20:51
 * @update 2020/11/28 20:51
 * @param
 * @return
 **/
@Controller
public class ImportUserController extends BaseController {

    @Resource
    ImportUserService importUserService;

    @Resource
    TokenService tokenService;

    @Resource
    LoginService loginService;

    @ApiOperation("批量导入部门成员接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 10004,message = "文件格式错误"),
            @ApiResponse(code = 10005,message = "性别参数不合法"),
            @ApiResponse(code = 20001,message = "手机号有误"),
            @ApiResponse(code = 20009,message = "所属公司不存在"),
            @ApiResponse(code = 20010,message = "所属企业不存在"),
    })
    @PostMapping("/dept/import")
    public CommonReturnType importToDB(MultipartFile file, HttpServletRequest request) throws IOException {
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            importUserService.importUser(file, Integer.parseInt(userId));
            return CommonReturnType.create("导入成功");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }
}
