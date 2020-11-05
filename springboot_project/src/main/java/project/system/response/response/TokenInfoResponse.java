package com.project.response.response;

import com.alibaba.fastjson.JSONObject;
import io.openvidu.java.client.OpenViduRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
@author WL
@CreateDate 2020-7-09
@update
@description 登录结果信息
*/
@ApiModel
@Data
public class TokenInfoResponse {
    @ApiModelProperty(value = "用户ID")
    String userId;
    @ApiModelProperty(value = "用户姓名")
    String userName;
    @ApiModelProperty(value = "令牌", hidden = true)
    String token;
    @ApiModelProperty(value = "用户手机号")
    String telephone;
    @ApiModelProperty(value = "登录状态")
    Boolean isLogin;
    @ApiModelProperty(value = "视频角色，在我们这个网站里，默认是PUBLISHER")
    OpenViduRole role;
    @ApiModelProperty(value = "关联会议唯一编码，默认一个用户同时只能参加一个会议")
    String groupUniqueId;
    @ApiModelProperty(value = "视频会话令牌")
    String openviduToken;
    @ApiModelProperty(value = "部门ID")
    Integer deptId;
    @ApiModelProperty(value = "公司ID")
    Integer companyId;
    @ApiModelProperty(value = "是否已经加入公司")
    Boolean haveJoinedCompany;

    /**
     * 用于openvidu客户端之间共享的信息
     */
    public String makeServerData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberUniqueId",this.userId);
        jsonObject.put("memberNickname",this.userName);
        jsonObject.put("memberToken",this.token);
        return jsonObject.toJSONString();
    }
}
