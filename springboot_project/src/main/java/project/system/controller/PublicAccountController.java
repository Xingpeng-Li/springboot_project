package project.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.system.common.utils.RequestUtil;
import project.system.domain.Post;
import project.system.domain.PublicAccount;
import project.system.domain.User;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.mapper.PostMapper;
import project.system.mapper.PublicAccountMapper;
import project.system.response.CommonReturnType;
import project.system.response.response.TokenInfoResponse;
import project.system.service.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/*
@author WL
@CreateDate 2020-7-21
@update 2020-7-22 7-23
@description 公众号相关api
*/
@RestController
@RequestMapping("/publicAccount")
@Api(tags = "公众号相关api")
public class PublicAccountController extends BaseController {

    @Resource
    private LoginService loginService;

    @Resource
    private TokenService tokenService;

    @Resource
    private PublicAccountMapper publicAccountMapper;

    @Resource
    private UserService userService;

    @Resource
    private AccountSubscribeService accountSubscribeService;

    @Resource
    private PostMapper postMapper;

    @Resource
    private PublicAccountService publicAccountService;

    @GetMapping("/createPersonalAccount")
    @ApiOperation("创建个人公众号")
    public CommonReturnType createPersonalAccount(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                int userId = Integer.parseInt(tokenInfoResponse.getUserId());
                String name = request.getParameter("name");//公众号名字
                PublicAccount publicAccount1 = publicAccountMapper.selectByName(name);
                if (publicAccount1 != null) {
                    throw new BusinessException(EmBusinessError.PUBLIC_ACCOUNT_ALREADY_EXISTS);
                }
                String brief = request.getParameter("brief");//公众号简介
                PublicAccount publicAccount = new PublicAccount();
                publicAccount.setPublicaccountType("person");//设置公众号为个人公众号
                publicAccount.setPublicaccountOwner(userId);//公众号所属着id为用户id
                publicAccount.setPublicaccountName(name);
                publicAccount.setPublicaccountBrief(brief);
                publicAccountService.addCompanyPublicAccount(publicAccount);
                return CommonReturnType.create(null);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @GetMapping("/createCompanyAccount")
    @ApiOperation("创建公司公众号")
    public CommonReturnType createCompanyAccount(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                int userId = Integer.parseInt(tokenInfoResponse.getUserId());
                User user = userService.getUserById(userId);
                boolean isCompanyAdmin = userService.isCompanyAdmin(userId);
                if (isCompanyAdmin) {
                    String name = request.getParameter("name");//公众号名字
                    PublicAccount publicAccount1 = publicAccountMapper.selectByName(name);
                    if (publicAccount1 != null) {
                        throw new BusinessException(EmBusinessError.PUBLIC_ACCOUNT_ALREADY_EXISTS);
                    }
                    String brief = request.getParameter("brief");//公众号简介
                    PublicAccount publicAccount = new PublicAccount();
                    publicAccount.setPublicaccountType("company");//设置公众号为企业公众号
                    publicAccount.setPublicaccountOwner(user.getCompanyId());//公众号所属着id为企业id
                    publicAccount.setPublicaccountName(name);
                    publicAccount.setPublicaccountBrief(brief);
                    publicAccountService.addCompanyPublicAccount(publicAccount);
                    return CommonReturnType.create(null);
                } else {
                    throw new BusinessException(EmBusinessError.USER_WITHOUT_AUTHORITY);
                }
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @GetMapping("/createPost")
    @ApiOperation("判断是否有写文章的权限")
    public CommonReturnType createPost(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                int userId = Integer.parseInt(tokenInfoResponse.getUserId());
                User user = userService.getUserById(userId);
                Integer publicAccountId = Integer.parseInt(request.getParameter("id"));
                PublicAccount publicAccount = publicAccountMapper.selectByPrimaryKey(publicAccountId);
                //如果是个人公众号
                if (publicAccount.getPublicaccountType().equals("person")) {
                    List<PublicAccount> publicAccounts = publicAccountMapper.selectByUserId(userId);
                    boolean isMine = publicAccounts.stream().anyMatch(account -> publicAccountId.equals(account.getPublicaccountId()));
                    return CommonReturnType.create(isMine);
                } else {//否则是企业公众号
                    return CommonReturnType.create(userService.isCompanyAdmin(userId) && publicAccount.getPublicaccountOwner().equals(user.getCompanyId()));
                }
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }


    @GetMapping("/personalPublicAccounts")
    @ApiOperation("查询个人所有公众号")
    public CommonReturnType myPublicAccounts(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                List<PublicAccount> publicAccounts = publicAccountMapper.selectByUserId(Integer.parseInt(tokenInfoResponse.getUserId()));
                return CommonReturnType.create(publicAccounts);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @GetMapping("/companyPublicAccounts")
    @ApiOperation("查询企业所有公众号")
    public CommonReturnType companyPublicAccounts(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                User user = userService.getUserById(Integer.parseInt(tokenInfoResponse.getUserId()));
                List<PublicAccount> publicAccounts = publicAccountMapper.selectByCompanyId(user.getCompanyId());
                return CommonReturnType.create(publicAccounts);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @GetMapping("/allSubscribePublicAccounts")
    @ApiOperation("查询个人订阅的所有公众号")
    public CommonReturnType allSubscribePublicAccounts(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                List<PublicAccount> publicAccounts = accountSubscribeService.selectByUserId(Integer.parseInt(tokenInfoResponse.getUserId()));
                return CommonReturnType.create(publicAccounts);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @GetMapping("/search")
    @ApiOperation("根据关键字搜索公众号")
    @ApiImplicitParam(name = "key", value = "搜索关键字")
    public CommonReturnType search(HttpServletRequest request, @RequestParam("key") String key) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                List<PublicAccount> publicAccounts = publicAccountMapper.selectByKey(key);
                return CommonReturnType.create(publicAccounts);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @GetMapping("/getAllPosts")
    @ApiOperation("查询公众号的所有文章")
    @ApiImplicitParam(name = "id", value = "公众号id")
    public CommonReturnType getAllPosts(HttpServletRequest request) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                System.out.println(request.getParameter("id"));
                int id = Integer.parseInt(request.getParameter("id"));
                List<Post> posts = postMapper.selectByPublicAccountId(id);
                return CommonReturnType.create(posts);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }

    @GetMapping("/subscribe")
    @ApiOperation("订阅公众号")
    @ApiImplicitParam(name = "id", value = "公众号id")
    public CommonReturnType subscribe(HttpServletRequest request, @RequestParam("id") Integer id) {
        String token = RequestUtil.getCookievalue(request);
        if (StringUtils.isNotBlank(token)) {
            TokenInfoResponse tokenInfoResponse = loginService.checkLogin(token);
            if (Objects.nonNull(tokenInfoResponse) && tokenInfoResponse.getIsLogin() && !tokenService.isExpiration(token)) {
                publicAccountService.subscribe(Integer.parseInt(tokenInfoResponse.getUserId()), id);
                return CommonReturnType.create(null);
            } else {
                throw new BusinessException(EmBusinessError.UNLOGIN);
            }
        } else {
            throw new BusinessException(EmBusinessError.UNLOGIN);
        }
    }
}