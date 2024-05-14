package com.laxqnsys.core.constants;

import java.time.format.DateTimeFormatter;

/**
 * @author wuzhenhong
 * @date 2024/5/14 9:11
 */
public class CommonCons {

    public static final String TOKEN_KEY = "lxDocToken";

    public static final String AES_KEY = "23423jAFsdf323ls";

    public static final Integer LOGIN_EXPIRE_SECONDS = 30 * 60;
    public static final Integer LOGIN_TOKEN_EXPIRE_SECONDS = 40 * 60;

    public static final String LOGIN_USER_TOKE_KEY = "LOGIN_USER_TOKE_KEY:USER_ID:";

    public static final DateTimeFormatter YYYY_MM_SS_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
