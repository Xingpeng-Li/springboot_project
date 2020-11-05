package com.project.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
/*
@author DKR
@CreateDate 2020-7-09
@update 2020-7-09 实现二次加密动态加盐的Md5加密
@description 对密码进行加密的类
*/
public class Md5Utils {

   public static String md5(String src){
      return DigestUtils.md5Hex(src);
   }

    private static final String SALT = "1a2b3c4d";

    /**
     * 第一次MD5加密，用于网络传输
     */
    public static String inputPassToFormPass(String inputPass){
        //在md5加密前先打乱密码
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    /**
     * 第二次MD5加密，用于存储到数据库
     */
    public static String formPassToDBPass(String formPass, String salt) {
        String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 合并
     */
    public static String inputPassToDBPass(String input, String saltDB){
        String formPass = inputPassToFormPass(input);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

}
