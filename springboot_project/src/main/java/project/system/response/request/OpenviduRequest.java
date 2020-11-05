package com.project.response.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/*
@author WL
@CreateDate 2020-7-09
@update
@description 视频会议相关参数
*/
@ApiModel
@Data
public class OpenviduRequest {
    @ApiModelProperty(value = "会议唯一编码")
    String groupUniqueId;
    @ApiModelProperty(value = "录制启动停止")
    Boolean state;
}
