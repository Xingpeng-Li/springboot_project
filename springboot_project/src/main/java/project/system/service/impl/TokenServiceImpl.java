package project.system.service.impl;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.system.common.utils.TokenUtil;
import project.system.error.BusinessException;
import project.system.error.EmBusinessError;
import project.system.service.TokenService;

import javax.annotation.Resource;
import java.util.Date;
@Service
@Transactional
public class TokenServiceImpl implements TokenService {
    @Resource
    TokenUtil tokenUtil;
    //调用jwt框架生成token
    @Override
    public  String createToken(Integer userId) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, tokenUtil.getSecret())//token生成算法
                .setIssuer(tokenUtil.getIss())//注入签名
                .setSubject(userId.toString())//注入userId
                .setIssuedAt(new Date())//注入签发时间
                .setExpiration(new Date(System.currentTimeMillis() + tokenUtil.getExpire()*1000))//设置过期时间
                .compact();
       //
    }
    //根据获取用户Id
    @Override
    public  Integer getUserId(String token){
        return Integer.parseInt(getTokenBody(token).getSubject());
    }
    // 判断token是否已过期
    @Override
    public  boolean isExpiration(String token){
       try{ Claims claims=Jwts.parser().setSigningKey(tokenUtil.getSecret()).parseClaimsJws(token).getBody();
               return false;
       }
       catch (ExpiredJwtException e){
           System.out.println(e.getMessage());
           throw new BusinessException(EmBusinessError.USER_LOGIN_EXPIRED);
       }
    }
    private Claims getTokenBody(String token){
        return Jwts.parser()
                .setSigningKey(tokenUtil.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }
}
