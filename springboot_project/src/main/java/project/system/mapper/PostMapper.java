package project.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.system.domain.Post;

import java.util.List;

@Mapper
public interface PostMapper {
    int deleteByPrimaryKey(Integer postId);

    int insert(Post record);

    int insertSelective(Post record);

    Post selectByPrimaryKey(Integer postId);

    int updateByPrimaryKeySelective(Post record);

    int updateByPrimaryKey(Post record);

    //根据公众号id查询所有该公众号所有帖子
    List<Post> selectByPublicAccountId(Integer id);

}
