package project.system.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.util.StringUtils;
/*
@author 王征权
@CreateDate 2020-10-09
@update 2020-10-09 验证电话号码格式函数
@description 验证参数的类
*/
public class Validator {
    private static Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;
        }

        Matcher matcher = MOBILE_PATTERN.matcher(mobile);
        return matcher.matches();
    }
}
