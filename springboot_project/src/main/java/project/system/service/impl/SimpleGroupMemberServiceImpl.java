package com.project.service.impl;

import com.project.error.BusinessException;
import com.project.response.request.GroupRequest;
import com.project.response.response.GroupResponse;
import com.project.service.GroupMemberService;
import com.project.service.manager.SimpleCoreManager;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

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
