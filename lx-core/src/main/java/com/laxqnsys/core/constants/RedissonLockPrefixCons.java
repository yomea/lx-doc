package com.laxqnsys.core.constants;

/**
 * @author wuzhenhong
 * @date 2024/5/14 13:31
 */
public class RedissonLockPrefixCons {

    public static final String USER_REGISTER = "USER_REGISTER";
    public static final String USER_UPDATE = "USER_UPDATE:USER_ID:${userInfoBO.id}";
}
