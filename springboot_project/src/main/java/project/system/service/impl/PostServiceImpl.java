package com.project.service.impl;

import com.project.domain.Post;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.PostMapper;
import com.project.service.PostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/*
@author WL
@CreateDate 2020-7-21
@update
@description 公众号文章业务逻辑操作
*/
@Service
public class PostServiceImpl implements PostService {
    @Resource
    private PostMapper postMapper;

    @Override
    public Post getPostDetail(Integer postId) {
        Post post = postMapper.selectByPrimaryKey(postId);
        if(post==null){
            throw new BusinessException(EmBusinessError.POST_NOT_EXISTS);
        }
        return post;
    }
}
