package project.system.service.impl;


import lombok.Builder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.system.domain.Company;
import project.system.domain.User;
import project.system.mapper.CompanyMapper;
import project.system.mapper.DeptMapper;
import project.system.mapper.UserMapper;
import project.system.service.UserService;
import project.system.view.UserDetailView;
import project.system.view.UserView;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/*
@author DKR
@CreateDate 2020-7-09
@update
@description 对UserService接口的具体实现
*/
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;
    @Resource
    CompanyMapper companyMapper;
    @Resource
    DeptMapper deptMapper;
    @Override
    public boolean PhoneNumberExist(String phoneNumber){
        User user=userMapper.getUserByPhoneNumber(phoneNumber);//判断电话号码是否已经被注册
        return user != null;
    }
    //根据电话号码获取用户
    @Override
    public User getUserByPhoneNumber(String phoneNumber){
        return userMapper.getUserByPhoneNumber(phoneNumber);
    }
    //把用户插入数据库
    @Override
    public boolean InsertUser(User user)
    {
        return userMapper.insertSelective(user) == 1;
    }
    //登录验证函数
    @Override
    public User verifyUser(User user){
        return userMapper.getUserByPhoneNumberAndPassword(user);
    }
    //通过Id查询用户
    @Override
    public User getUserById(Integer userId)
    {
        return userMapper.selectByPrimaryKey(userId);
    }

    //获取部门通讯录
    @Override
    public List<UserView> getMyDeptContacts(Integer userId){
        User user=userMapper.selectByPrimaryKey(userId);
        return userMapper.getDeptContact(user.getDeptId(),userId).stream().
                map(this::convertFromUserToUserView).collect(Collectors.toList());
    }

    //获取公司通讯录
    @Override
    public List<UserView> getMyCompContacts(Integer userId){
        User user=userMapper.selectByPrimaryKey(userId);
        return userMapper.getCompContact(user.getCompanyId(),userId).stream().
                map(this::convertFromUserToUserView).collect(Collectors.toList());
    }
//获取没有加入部门的人
    @Override
    public List<UserView> getMyNoDeptUsers(Integer userId){
        User user=userMapper.selectByPrimaryKey(userId);
        return userMapper.getNoDeptUsers(userId,user.getCompanyId()).stream().
                map(this::convertFromUserToUserView).collect(Collectors.toList());
    }
    @Override
    public boolean isCompanyAdmin(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        return "admin".equals(user.getUserPosition());
    }

    @Override
    public boolean isDeptMaster(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        return "master".equals(user.getUserPosition());
    }

    @Override
    public User selectByPrimaryKey(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public int updateByPrimaryKeySelective(User record) {
        return userMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public UserView convertFromUserToUserView(User user){
        if(user==null)
            return null;
        else {
            UserView userView = new UserView();
            BeanUtils.copyProperties(user, userView);
            if(user.getCompanyId()!=0)
            {
                userView.setCompanyName(companyMapper.selectByPrimaryKey(user.getCompanyId()).//获取公司
                    getCompanyName());//获取公司名
            }else {userView.setCompanyName("无公司");}
            if(user.getDeptId()!=0)
            {
                userView.setDeptName(deptMapper.selectByPrimaryKey(user.getDeptId()).//获取部门
                        getDeptName());//获取部门名
            }
            else {userView.setDeptName("无部门");}
            return userView;
        }
    }
    @Override
    public UserDetailView getUserInfo(Integer userId){
       return convertFromUserToDetailView(userMapper.selectByPrimaryKey(userId));
    }
    @Override
    public  boolean modifyMyInfo(User user)
    {
        return userMapper.updateByPrimaryKeySelective(user)==1;
    }
    private UserDetailView convertFromUserToDetailView(User user){
        if(user==null)
            return null;
        UserDetailView userDetailView=new UserDetailView();
        BeanUtils.copyProperties(user,userDetailView);if(user.getCompanyId()!=0)
        {
            Company company=companyMapper.selectByPrimaryKey(user.getCompanyId());
            userDetailView.setCompanyName(company.//获取公司
                    getCompanyName());//获取公司名
            userDetailView.setCompanyInvitationCode(company.getCompanyInviteCode());
        }else {userDetailView.setCompanyName("无公司");}
        if(user.getDeptId()!=0)
        {
            userDetailView.setDeptName(deptMapper.selectByPrimaryKey(user.getDeptId()).//获取部门
                    getDeptName());//获取部门名
        }
        else {userDetailView.setDeptName("无部门");}
        return userDetailView;
    }
}
