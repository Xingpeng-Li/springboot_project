package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.Company;

@Mapper
public interface CompanyMapper {
    int deleteByPrimaryKey(Integer companyId);

    int insert(Company record);

    int insertSelective(Company record);

    Company selectByPrimaryKey(Integer companyId);

    int updateByPrimaryKeySelective(Company record);

    int updateByPrimaryKey(Company record);

    Company selectByName(String name);

    //新添加--李星鹏
    Company selectByCode(String code);
}
