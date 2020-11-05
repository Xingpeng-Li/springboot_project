package com.project.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

/*
@author WL
@CreateDate 2020-7-09
@update
@description String工具类
*/
public class RandomStringUtil {

    public static String createToken() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    public static String createGroupUniqueId() {
        return RandomStringUtils.randomAlphanumeric(6);
    }
}
