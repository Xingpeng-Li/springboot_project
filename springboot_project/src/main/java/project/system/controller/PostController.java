package project.system.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.system.common.utils.RequestUtil;
import project.system.domain.Post;
import project.system.domain.PublicAccount;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.PublicAccountMapper;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static java.lang.Integer.parseInt;

/*
@author WL
@CreateDate 2020-7-21
@update 2020-7-22
@description 公众号文章相关api
*/
@RestController
@Api(tags = "公众号文章相关api")
@RequestMapping("/post")
public class PostController extends BaseController {

    @Resource
    private LoginService loginService;

    @Resource
    private TokenService tokenService;

    @Resource
    private PostService postService;

    @Resource
    private PublicAccountService publicAccountService;

    @Resource
    private UploadService uploadService;

    @PostMapping("/postDetail")
    @ApiOperation("根据公众号文章id查看文章详情")
    @ApiImplicitParam(name = "postId", value = "公众号文章id")
    public CommonReturnType postDetail(HttpServletRequest request) {
        Integer postId = parseInt(request.getParameter("postId"));
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                Post post = postService.getPostDetail(postId);
                return CommonReturnType.create(post);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @PostMapping("/createPost")
    @ApiOperation("写文章")
    public CommonReturnType createPost(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                String postTitle = request.getParameter("postTitle");
                String postBody = request.getParameter("postBody");
                String postBrief = request.getParameter("postBrief");
                Integer publicAccountId = parseInt(request.getParameter("publicAccountId"));
                PublicAccount publicAccount = publicAccountService.selectByPrimaryKey(publicAccountId);
                if(publicAccount==null){
                    throw new BusinessException(EmBusinessError.PUBLIC_ACCOUNT_NOT_EXISTS);
                }
                Post post = new Post();
                post.setPostBody(postBody);
                post.setPostTitle(postTitle);
                post.setPostBrief(postBrief);
                post.setPublicaccountId(publicAccountId);
                post.setPublicaccountName(publicAccount.getPublicaccountName());
                uploadService.uploadFie(file,post);
                return CommonReturnType.create(null);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }
}
