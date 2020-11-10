package project.system.controller;


import io.openvidu.java.client.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import project.system.common.utils.RequestUtil;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.response.CommonReturnType;
import project.system.response.request.GroupRequest;
import project.system.response.request.OpenviduRequest;
import project.system.response.response.GroupResponse;
import project.system.response.response.OpenviduResponse;
import project.system.response.response.TokenInfoResponse;
import project.system.service.GroupMemberService;
import project.system.service.LoginService;
import project.system.service.manager.SimpleCoreManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/*
@author WL
@CreateDate 2020-7-10
@update
@description Openvidu控制类，实现视频相关api
*/
@Api(tags = "Openvidu相关接口")
@Slf4j
@RestController
@RequestMapping("/ov")
public class OpenviduController extends BaseController {

	@Resource
    private LoginService loginService;

	@Resource
    private GroupMemberService groupMemberService;

	@Resource
    private SimpleCoreManager simpleCoreManager;

	@Resource
    private OpenVidu openVidu;

	@ApiOperation("初始化接口")
	@PostMapping("/init")
	public CommonReturnType addUser(@RequestBody OpenviduRequest openviduRequest, HttpServletRequest request) {
		TokenInfoResponse tokenInfoResponse = checkUserLogged(request);
		if (Objects.isNull(tokenInfoResponse)) {
			throw new BusinessException(EmBusinessError.UNLOGIN);
		}
		OpenviduResponse openviduResponse = new OpenviduResponse();
		try {
			String groupUniqueId = openviduRequest.getGroupUniqueId();
			if (StringUtils.isBlank(groupUniqueId)) {
				throw new BusinessException(EmBusinessError.NORMAL_ERR);
			}
			GroupRequest groupRequest = new GroupRequest();
			groupRequest.setGroupUniqueId(groupUniqueId);
			GroupResponse groupMemberList = groupMemberService.getGroupInfo(groupRequest);
			if (Objects.isNull(groupMemberList)) {
				throw new BusinessException(EmBusinessError.NORMAL_ERR);
			}

			OpenViduRole role = tokenInfoResponse.getRole();
			if (Objects.isNull(role)) {
				role = OpenViduRole.PUBLISHER;
			}
			// 该用户连接到视频通话时要传递给其他用户的可选数据
			String serverData = tokenInfoResponse.makeServerData();
			// 通过serverData传递用户信息和指定角色
			TokenOptions tokenOptions = new TokenOptions.Builder().data(serverData).role(role).build();
			// 获取（sessionId不存在则生成新的）OpenVidu Session
			// 增强配置（录制类型：手动还是自动；录制输出形式：每个视频独立还是聚合）
			// 强制会话id等于群id
			SessionProperties properties = new SessionProperties.Builder()
					.recordingMode(RecordingMode.MANUAL)
					.defaultOutputMode(Recording.OutputMode.INDIVIDUAL)
					.customSessionId(groupUniqueId)
					.build();
			Session session = simpleCoreManager.getOpenviduSession(properties);
			//System.out.println(session.getSessionId());
			// 通过刚产生的令牌tokenOptions生成新的token(实质上是传递用户信息，并返回会议服里的唯一token)
			String openviduToken = session.generateToken(tokenOptions);
			simpleCoreManager.addOpenviduToken(tokenInfoResponse.getToken(), openviduToken);
			openviduResponse.setOpenViduToken(openviduToken);
		} catch (BusinessException e1) {
			log.error("用户初始化Openvidu获取openViduToken失败",e1);
			throw new BusinessException(e1);
        } catch (Exception e) {
			log.error("用户初始化Openvidu获取openViduToken失败",e);
			throw new BusinessException(EmBusinessError.SYSTEM_ERR);
		}
		return CommonReturnType.create(openviduResponse);
	}

	@ApiOperation("退出视频会话")
	@GetMapping("/leave")
	public CommonReturnType removeUser(HttpServletRequest request, HttpServletResponse response) {
		TokenInfoResponse tokenInfoResponse = checkUserLogged(request);
		if(Objects.isNull(tokenInfoResponse)||!tokenInfoResponse.getIsLogin()){
			throw new BusinessException(EmBusinessError.UNLOGIN);
		}
		simpleCoreManager.removeToken(tokenInfoResponse.getToken());
		log.info("{} 离开了视频会议" , tokenInfoResponse.getUserName());
		return CommonReturnType.create(null);
	}

	private TokenInfoResponse checkUserLogged(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if ( StringUtils.isNotBlank(token) ) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin()) {
                return tokenInfoResponse;
            }
        }
        return null;
	}

	@ApiOperation("录制接口")
	@PostMapping("/record")
	public CommonReturnType record(@RequestBody OpenviduRequest openviduRequest, HttpServletRequest request) {
		TokenInfoResponse tokenInfoResponse = checkUserLogged(request);
		if (Objects.isNull(tokenInfoResponse) || Objects.isNull(tokenInfoResponse.getGroupUniqueId())) {
			throw new BusinessException(EmBusinessError.NORMAL_ERR);
		}

		if (openviduRequest.getState()) {
			log.info("{} 启动录制 {}" , tokenInfoResponse.getUserName(), tokenInfoResponse.getGroupUniqueId());
			String recordName = tokenInfoResponse.getGroupUniqueId().concat("_").concat(tokenInfoResponse.getUserId())
					.concat("_").concat(RandomStringUtils.randomAlphanumeric(6));
			RecordingProperties properties = new RecordingProperties.Builder()
					.outputMode(Recording.OutputMode.INDIVIDUAL)
					.name(recordName)
					.build();
			Session session = simpleCoreManager.getOpenviduSessionById(tokenInfoResponse.getGroupUniqueId());
			if (Objects.nonNull(session)) {
				try {
					Recording recording = openVidu.startRecording(session.getSessionId(), properties);
					simpleCoreManager.addOpenviduRecord(tokenInfoResponse.getGroupUniqueId(), recording.getId());
				} catch (OpenViduJavaClientException | OpenViduHttpException e) {
					e.printStackTrace();
					throw new BusinessException(EmBusinessError.SYSTEM_ERR);
				}
			}
		} else {
			log.info("{} 停止录制 {}" , tokenInfoResponse.getUserName(), tokenInfoResponse.getGroupUniqueId());
			String recordingId = simpleCoreManager.getOpenviduRecordings().get(tokenInfoResponse.getGroupUniqueId());
			if (Objects.nonNull(recordingId)) {
				try {
					openVidu.stopRecording(recordingId);
				} catch (OpenViduJavaClientException | OpenViduHttpException e) {
					e.printStackTrace();
					throw new BusinessException(EmBusinessError.SYSTEM_ERR);
				}
			}
		}
		return CommonReturnType.create(null);
	}
}
