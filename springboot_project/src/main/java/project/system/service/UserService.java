package project.system.service;

import project.system.domain.User;
import project.system.vo.UserDetailVo;
import project.system.vo.UserVo;

import java.util.List;

/*
@author zws
@CreateDate 2020-11-28
@update  2020-11-28 添加判断电话号码是否注册函数和用户的注册函数
         2020-11-29 添加登录时的验证函数
@description 提供与User类相关的服务
*/
public interface UserService {
    boolean PhoneNumberExist(String phoneNumber);

    User getUserByPhoneNumber(String phoneNumber);
    boolean InsertUser(User user);
    User verifyUser(User user);
    User getUserById(Integer userId);
    UserDetailVo getUserInfo(Integer userId);
    List<UserVo> getMyDeptContacts(Integer userId);
    List<UserVo> getMyCompContacts(Integer userId);
    List<UserVo> getMyNoDeptUsers(Integer userId);
    UserVo convertFromUserToUserView(User user);
    boolean modifyMyInfo(User user);
    //判断是否是企业管理员
    boolean isCompanyAdmin(Integer userId);
    //判断是否是部门管理员
    boolean isDeptMaster(Integer userId);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);
}
