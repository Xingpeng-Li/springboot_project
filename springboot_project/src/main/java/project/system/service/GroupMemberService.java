package com.project.service;

import com.project.error.BusinessException;
import com.project.response.request.GroupRequest;
import com.project.response.response.GroupResponse;

/*
@author WL
@CreateDate 2020-7-09
@update
@description 会议成员相关接口
*/
public interface GroupMemberService {

    GroupResponse getGroupInfo(GroupRequest groupRequest);

    void relateTokenWithGroup(String token, String groupUniqueId) throws BusinessException;
}
