package com.project.response.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
@author WL
@CreateDate 2020-7-09
@update
@description 密码登录时的登录参数
*/
@ApiModel
@Data
public class LoginRequest {
    @ApiModelProperty(value = "帐号")
    String username;
    @ApiModelProperty(value = "密码")
    String password;
    @ApiModelProperty(value = "会话令牌", hidden=true)
    String token;
}
