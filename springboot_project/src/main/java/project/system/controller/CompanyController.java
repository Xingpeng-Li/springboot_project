package project.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.system.common.utils.RequestUtil;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.UserMapper;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.CompanyService;
import project.system.service.LoginService;
import project.system.service.TokenService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/*
 @author:李星鹏
 @createDate:2020/7/10
 @description:创建企业、添加部门Controller

 @update:2020/7/11
 @description:修改了返回值为CommonReturnType

 @update:2020/7/15
 @description:增加了管理员权限
 */

@Controller
public class CompanyController extends BaseController {

    @Resource
    CompanyService companyService;

    @Resource
    TokenService tokenService;

    @Resource
    LoginService loginService;

    @Resource
    UserMapper userMapper;

    @ApiOperation("返回员工职位接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
    })
    //返回用户的职位
    @GetMapping(value ="/company/authority")
    public CommonReturnType haveAuthority(HttpServletRequest request){
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            User user = userMapper.selectByPrimaryKey(Integer.parseInt(userId));
            return CommonReturnType.create(user.getUserPosition());
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("创建公司接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 50001,message = "该企业已经存在"),
    })
    @GetMapping(value = "/company/add")
    public CommonReturnType addCompany(HttpServletRequest request){
        String companyname = request.getParameter("companyname");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            String inviteCode = companyService.createCompany(companyname,Integer.parseInt(userId));
            return CommonReturnType.create("创建成功,企业邀请码是"+inviteCode);
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("加入公司接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 20009,message = "所属企业不存在"),
    })
    @GetMapping("/company/join")
    public CommonReturnType joinCompany(HttpServletRequest request){
        String companycode = request.getParameter("companycode");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            String companyName = companyService.joinCompany(companycode,Integer.parseInt(userId));
            return CommonReturnType.create("成功加入"+companyName+"企业");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("创建部门接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 20013,message = "用户没有权限"),
            @ApiResponse(code = 20009,message = "所属企业不存在"),
            @ApiResponse(code = 50002,message = "该部门已经存在"),
    })
    @GetMapping("/company/addDeptToDB")
    public CommonReturnType addDeptToDB(HttpServletRequest request){
        String deptname = request.getParameter("deptname");
        String masterPhoneNumber = request.getParameter("masterPhoneNumber");
        String userIds = request.getParameter("userIds");

        //获得所有选中的userIds
        String[] userIdList = userIds.split(",");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            companyService.addDept(Integer.parseInt(userId),deptname,masterPhoneNumber,userIdList);
            return CommonReturnType.create("添加成功");
        }
        else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("查看部门成员接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
    })
    //返回一个部门中所有的人员信息
    @GetMapping("/company/dept")
    public CommonReturnType getDeptUser(HttpServletRequest request){
        List<Map<String,String>> mapList;

        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            mapList = companyService.getDeptUser(Integer.parseInt(userId));
        }else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
        return CommonReturnType.create(mapList);
    }

    @ApiOperation("删除部门成员接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 20013,message = "用户没有权限"),
    })
    //删除一个部门成员
    @GetMapping("/company/deleteDeptUser")
    public CommonReturnType deleteDeptUser(HttpServletRequest request){
        String deleteUserId = request.getParameter("deleteUserId");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            //只有部门管理员或者企业管理员能删除成员
            User master = userMapper.selectByPrimaryKey(Integer.parseInt(userId));
            User deleteUser = userMapper.selectByPrimaryKey(Integer.parseInt(deleteUserId));
            if(!"master".equals(master.getUserPosition()) && !"admin".equals(master.getUserPosition())){
                throw new BusinessException(EmBusinessError.USER_WITHOUT_AUTHORITY);
            }
            deleteUser.setDeptId(0);
            userMapper.updateByPrimaryKey(deleteUser);
            return CommonReturnType.create("删除成功");
        }else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation("删除企业成员接口")
    @ApiResponses({
            @ApiResponse(code = 200,message = "success"),
            @ApiResponse(code = 1,message = "用户未登录"),
            @ApiResponse(code = 1,message = "登录已过期"),
            @ApiResponse(code = 20013,message = "用户没有权限"),
    })
    //删除一个企业成员
    @GetMapping("/company/deleteCompanyUser")
    public CommonReturnType deleteCompanyUser(HttpServletRequest request){
        String deleteUserId = request.getParameter("deleteUserId");
        //通过token获取UserId
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token) && !tokenService.isExpiration(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            String userId = tokenInfoResponse.getUserId();
            //只有企业管理员能删除
            User master = userMapper.selectByPrimaryKey(Integer.parseInt(userId));
            User deleteUser = userMapper.selectByPrimaryKey(Integer.parseInt(deleteUserId));
            if(!"admin".equals(master.getUserPosition())){
                throw new BusinessException(EmBusinessError.USER_WITHOUT_AUTHORITY);
            }
            deleteUser.setCompanyId(0);
            //同时也需要将其从部门中移除
            deleteUser.setDeptId(0);
            userMapper.updateByPrimaryKey(deleteUser);
            return CommonReturnType.create("删除成功");
        }else{
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }
}
