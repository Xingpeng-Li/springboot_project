package com.project.response.response;

import lombok.Data;

import java.util.List;

/*
@author WL
@CreateDate 2020-7-09
@update
@description 会议详情
*/
@Data
public class GroupResponse {
    /**
     * 会议唯一码
     */
    String groupUniqueId;

    /**
     * 参加会议成员信息
     */
    List<UserResponse> userResponseList;
}
