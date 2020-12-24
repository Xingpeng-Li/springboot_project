package project.system.response.response;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
@author 李星鹏
@CreateDate 2020-10-09
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
    @ApiModelProperty(value = "部门ID")
    Integer deptId;
    @ApiModelProperty(value = "公司ID")
    Integer companyId;
    @ApiModelProperty(value = "是否已经加入公司")
    Boolean haveJoinedCompany;
}
