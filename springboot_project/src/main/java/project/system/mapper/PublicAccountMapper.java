package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.PublicAccount;

import java.util.List;

/*
@author WL
@CreateDate 2020-7-21
@update
@description 公众号持久化操作
*/
@Mapper
public interface PublicAccountMapper {
    int deleteByPrimaryKey(Integer publicaccountId);

    int insert(PublicAccount record);

    int insertSelective(PublicAccount record);

    PublicAccount selectByPrimaryKey(Integer publicaccountId);

    int updateByPrimaryKeySelective(PublicAccount record);

    int updateByPrimaryKey(PublicAccount record);

    List<PublicAccount> selectByUserId(Integer userId);

    List<PublicAccount> selectByCompanyId(Integer companyId);

    //公众号的名字不能重复
   PublicAccount selectByName(String name);

   //根据关键字查询公众号
    List<PublicAccount> selectByKey(String key);

}
