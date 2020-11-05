package com.project.service;

import com.project.domain.Post;

/*
@author WL
@CreateDate 2020-7-22
@update
@description
*/
public interface PostService {

    //查询文章详情
    Post getPostDetail(Integer postId);
}
