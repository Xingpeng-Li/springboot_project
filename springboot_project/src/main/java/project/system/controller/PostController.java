package project.system.controller;

import com.project.common.utils.RequestUtil;
import com.project.domain.Post;
import com.project.domain.PublicAccount;
import com.project.error.BusinessException;
import com.project.error.EmBusinessError;
import com.project.mapper.PublicAccountMapper;
import com.project.response.CommonReturnType;
import com.project.response.response.TokenInfoResponse;
import com.project.service.LoginService;
import com.project.service.PostService;
import com.project.service.TokenService;
import com.project.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

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
    private PublicAccountMapper publicAccountMapper;

    @Resource
    private UploadService uploadService;

    @GetMapping("/postDetail")
    @ApiOperation("根据公众号文章id查看文章详情")
    @ApiImplicitParam(name = "postId", value = "公众号文章id")
    public CommonReturnType postDetail(HttpServletRequest request, @RequestParam("postId") Integer postId) {
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
    public CommonReturnType postDetail(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                String postTitle = request.getParameter("postTitle");
                String postBody = request.getParameter("postBody");
                String postBrief = request.getParameter("postBrief");
                Integer publicAccountId = Integer.parseInt(request.getParameter("publicAccountId"));
                PublicAccount publicAccount = publicAccountMapper.selectByPrimaryKey(publicAccountId);
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
