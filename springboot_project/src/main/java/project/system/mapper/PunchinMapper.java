package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.Punchin;

import java.util.List;

@Mapper
public interface PunchinMapper {
    int deleteByPrimaryKey(Integer punchinId);

    int insert(Punchin record);

    int insertSelective(Punchin record);

    Punchin selectByPrimaryKey(Integer punchinId);

    int updateByPrimaryKeySelective(Punchin record);

    int updateByPrimaryKey(Punchin record);

    //新添加——徐必成
    Punchin[] selectByUserId(int userId);

    List<Punchin> selectAll();
}
