package project.system.common.utils;

import project.system.common.constant.RequestConstant;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
@author wzq
@CreateDate 2020-11-09
@update 2020-11-17 DKR 封装获取taskId的函数
@description request工具类，目前只要用于用户登录方面
*/
public class RequestUtil {
    //从cookie中获取taskId字段
    public static Long getTaskIdCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("taskId")) {
                    return Long.parseLong(cookie.getValue());
                }
            }
        }
        return null;
    }
    public static String getCookievalue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(RequestConstant.TOKEN)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void setCookieValue(String key, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, value);
        response.addCookie(cookie);
    }

    //删除cookie,方法为重新建立同名立即删除类型的Cookie
    public static void clearCookie(String key, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void clearAllCookie(String key, HttpServletResponse response, HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }
}
