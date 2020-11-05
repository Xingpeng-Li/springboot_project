package project.system.service;


import project.system.error.BusinessException;
import project.system.response.request.GroupRequest;
import project.system.response.response.GroupResponse;

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
