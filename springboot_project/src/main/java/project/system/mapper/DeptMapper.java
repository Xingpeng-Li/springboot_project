package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.Dept;

@Mapper
public interface DeptMapper {
    int deleteByPrimaryKey(Integer deptId);

    int insert(Dept record);

    int insertSelective(Dept record);

    Dept selectByPrimaryKey(Integer deptId);

    Dept selectByName(String name);

    int updateByPrimaryKeySelective(Dept record);

    int updateByPrimaryKey(Dept record);

    Dept[] selectByCompanyId(Integer companyId);
}
