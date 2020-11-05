package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.Application;

@Mapper
public interface ApplicationMapper {
    int deleteByPrimaryKey(Integer applicationId);

    int insert(Application record);

    int insertSelective(Application record);

    Application selectByPrimaryKey(Integer applicationId);

    int updateByPrimaryKeySelective(Application record);

    int updateByPrimaryKey(Application record);
}
