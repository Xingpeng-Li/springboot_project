package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.HealthPunchin;

@Mapper
public interface HealthPunchinMapper {
    int deleteByPrimaryKey(Integer punchinId);

    int insert(HealthPunchin record);

    int insertSelective(HealthPunchin record);

    HealthPunchin selectByPrimaryKey(Integer punchinId);

    int updateByPrimaryKeySelective(HealthPunchin record);

    int updateByPrimaryKey(HealthPunchin record);

    HealthPunchin[] selectAll();

    //新添加——徐必成
    HealthPunchin[] selectByUserID(int userId);
}
