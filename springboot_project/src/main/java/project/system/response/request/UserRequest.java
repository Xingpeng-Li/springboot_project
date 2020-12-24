package project.system.response.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
@author zws
@CreateDate 2020-10-09
@update
@description 会议成员相关参数
*/
@ApiModel
@Data
public class UserRequest {
    @ApiModelProperty(value = "用户帐号")
    String userId;

    @ApiModelProperty(value = "用户姓名")
    String userName;

}
