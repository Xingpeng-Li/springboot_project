package com.project.mapper;

import com.project.domain.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
@author WL
@CreateDate 2020-7-21
@update 2020-7-22
@description 公众号文章持久化操作
*/
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
