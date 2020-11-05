package project.system.response.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
@author WL
@CreateDate 2020-7-09
@update
@description 会议参数
*/
@ApiModel
@Data
public class GroupRequest {
    @ApiModelProperty(value = "群唯一编码")
    String groupUniqueId;
    @ApiModelProperty(value = "群名称")
    String groupName;
}
