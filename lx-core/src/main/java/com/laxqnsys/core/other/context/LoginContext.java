package com.laxqnsys.core.other.context;

import com.laxqnsys.core.buz.sys.model.bo.UserInfoBO;
import java.util.Objects;

/**
 * @author wuzhenhong
 * @date 2024/5/14 10:19
 */
public class LoginContext {

    private static final ThreadLocal<UserInfoBO> USER_INFO = new ThreadLocal<>();

    public static void setUserInfo(UserInfoBO userInfoBO) {
        USER_INFO.set(userInfoBO);
    }

    public static UserInfoBO getUserInfo() {
        return USER_INFO.get();
    }

    public static Long getUserId() {
        UserInfoBO userInfoBO = LoginContext.getUserInfo();
        return Objects.isNull(userInfoBO) ? null : userInfoBO.getId();
    }

    public static String getAccount() {
        UserInfoBO userInfoBO = LoginContext.getUserInfo();
        return Objects.isNull(userInfoBO) ? null : userInfoBO.getAccount();
    }
}
