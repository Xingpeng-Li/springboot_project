package project.system.service;


import project.system.domain.Post;

import java.util.List;

/*
@author WL
@CreateDate 2020-7-22
@update
@description
*/
public interface PostService {

    //查询文章详情
    Post getPostDetail(Integer postId);

    List<Post> selectByPublicAccountId(Integer id);

    void deletePost(Integer postId);
}
