package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import project.system.domain.User;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User getUserByPhoneNumber(String userPhonenumber);
    User getUserByPhoneNumberAndPassword(User user);

    User selectByName(String userName);
    User[] selectByDeptId(int deptId);

    List<User> selectByCompanyId(Integer companyId);

    User[] selectByCompanyAndDept(@Param("companyId") Integer companyId, @Param("deptId") Integer deptId);

    //@author zws 获取用户的部门通讯录
    List<User> getDeptContact(Integer deptId, Integer userId);
    //@author zws 获取用户的企业通讯录
    List<User> getCompContact(Integer companyId, Integer userId);
    //@author zws 获取企业下没有加入部门的用户
    List<User> getNoDeptUsers(Integer userId, Integer companyId);
    List<User> selectAll();
//    User[] selectbycompanyid(int companyId);
}
