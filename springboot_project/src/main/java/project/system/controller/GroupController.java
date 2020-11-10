package project.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import project.system.common.utils.RequestUtil;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.request.GroupRequest;
import project.system.response.response.GroupResponse;
import project.system.response.response.TokenInfoResponse;
import project.system.service.GroupMemberService;
import project.system.service.LoginService;
import project.system.service.manager.SimpleCoreManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/*
@author WL
@CreateDate 2020-7-10
@update
@description 会议控制类
*/
@Api(tags = "会议相关接口")
@RestController
@RequestMapping(value = "/group")
public class GroupController extends BaseController {
    @Resource
    private SimpleCoreManager simpleCoreManager;

    @Resource
    private LoginService loginService;

    @Resource
    private GroupMemberService groupMemberService;

    private static int currentValue = 100000000;

    /*
     * 点击会议按钮，跳转到加入会议界面，判断是否登录，若登录返回登录信息
     * */
    @GetMapping(value = "/joinVideo")
    public CommonReturnType joinVideo(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                return CommonReturnType.create(tokenInfoResponse);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @ApiOperation(value = "加入会议接口", notes = "绑定用户与当前会议的关系")
    @ApiImplicitParam(name = "guid", value = "会议唯一编号", required = true)
    @GetMapping(value = "/join/{guid}")
    public CommonReturnType join(@PathVariable(name = "guid") String groupUniqueId, HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                try {
                    //说明是加入会议
                    if (!"null".equals(groupUniqueId)) {
                        if (simpleCoreManager.getReadOnlyGroupRefMember().containsKey(groupUniqueId)) {
                            groupMemberService.relateTokenWithGroup(token, groupUniqueId);
                        } else {
                            throw new BusinessException(EmBusinessError.GROUP_NOT_EXISTS);
                        }
                    } else {
                        //否则是创建会议
                        groupUniqueId = generateGroupId();
                        groupMemberService.relateTokenWithGroup(token, groupUniqueId);
                    }
                } catch (BusinessException e) {
                    throw new BusinessException(EmBusinessError.UNKNOWN_ERROR);
                }
                GroupRequest groupRequest = new GroupRequest();
                groupRequest.setGroupUniqueId(groupUniqueId);
                GroupResponse groupResult = groupMemberService.getGroupInfo(groupRequest);
                return CommonReturnType.create(groupResult);
            }
        }
        throw new BusinessException(EmBusinessError.UNLOGIN);
    }

    //产生会议Id
    private String generateGroupId() {
        currentValue = (currentValue + 1 - 100000000) % 900000000 + 100000000;
        return String.valueOf(currentValue);
    }
}
