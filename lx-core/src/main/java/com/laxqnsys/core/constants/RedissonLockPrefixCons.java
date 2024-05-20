package com.laxqnsys.core.constants;

/**
 * @author wuzhenhong
 * @date 2024/5/14 13:31
 */
public class RedissonLockPrefixCons {

    public static final String USER_REGISTER = "USER_REGISTER";
    public static final String USER_PREFIX = "USER_ID:${userInfoBO.id}";
    public static final String USER_UPDATE = "USER_UPDATE:" + USER_PREFIX;
    public static final String USER_CONFIG_SAVE_OR_UPDATE = "USER_CONFIG_SAVE_OR_UPDATE:" + USER_PREFIX;
    public static final String MOVE_FOLDER = "MOVE_FOLDER:FOLDER_ID:";
    public static final String DEL_FOLDER = "DEL_FOLDER:FOLDER_ID:";
}
