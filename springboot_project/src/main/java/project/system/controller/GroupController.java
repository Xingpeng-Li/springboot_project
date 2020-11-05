package project.system.controller;

import com.project.common.utils.RequestUtil;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.response.CommonReturnType;
import com.project.response.request.GroupRequest;
import com.project.response.response.GroupResponse;
import com.project.response.response.TokenInfoResponse;
import com.project.service.GroupMemberService;
import com.project.service.LoginService;
import com.project.service.manager.SimpleCoreManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "/joinVideo", method = RequestMethod.POST)
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
    @RequestMapping(value = "/join/{guid}", method = {RequestMethod.POST})
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
