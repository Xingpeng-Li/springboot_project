package project.system.service.impl;


import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import project.system.error.BusinessException;
import project.system.response.request.GroupRequest;
import project.system.response.response.GroupResponse;
import project.system.service.GroupMemberService;
import project.system.service.manager.SimpleCoreManager;

import javax.annotation.Resource;
import java.util.Objects;

/*
@author WL
@CreateDate 2020-7-10
@update
@description 会议成员接口简单实现
*/
@Primary
@Service
public class SimpleGroupMemberServiceImpl implements GroupMemberService {

    @Resource
    private SimpleCoreManager simpleCoreManager;

    @Override
    public GroupResponse getGroupInfo(GroupRequest groupRequest) {
        if (Objects.isNull(groupRequest)
                || Objects.isNull(groupRequest.getGroupUniqueId())) {
            return null;
        }
        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setGroupUniqueId(groupRequest.getGroupUniqueId());
        return groupResponse;
    }

    @Override
    public void relateTokenWithGroup(String token, String groupUniqueId) throws BusinessException {
        Objects.requireNonNull(token, "TOKEN不允许空");
        Objects.requireNonNull(groupUniqueId, "群id不允许空");
        simpleCoreManager.addGroupMember(groupUniqueId, token);
    }
}
