package project.system.service.impl;


import org.springframework.stereotype.Service;
import project.system.domain.Post;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.PostMapper;
import project.system.service.PostService;

import javax.annotation.Resource;
import java.util.List;

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

    @Override
    public List<Post> selectByPublicAccountId(Integer id) {
        return postMapper.selectByPublicAccountId(id);
    }
}
