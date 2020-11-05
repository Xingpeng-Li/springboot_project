package project.system.response.response;

import lombok.Data;

/*
@author WL
@CreateDate 2020-7-09
@update
@description 会议成员信息
*/
@Data
public class UserResponse {

    /**
     * 用户ID
     */
    String userId;

    /**
     * 用户姓名
     */
    String userName;
}
