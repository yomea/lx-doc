package com.laxqnsys.core.other.util.web;

import com.laxqnsys.core.other.constants.CommonCons;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuzhenhong
 * @date 2024/5/14 14:13
 */
public class WebUtil {

    public static String getCookie(HttpServletRequest request, String cookieKey) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieKey)) {
                return cookie.getValue();
            }
        }
        return "";
    }

    public static void saveCookie(HttpServletResponse response, String token, Integer loginExpireSeconds) {

        Cookie cookie = new Cookie(CommonCons.TOKEN_KEY, token);
        cookie.setMaxAge(loginExpireSeconds);
        cookie.setPath("/");
        // 防止XSS攻击读取Cookie
        cookie.setHttpOnly(true);
        // HTTPS环境下防止Cookie被窃听
        // cookie.setSecure(true);
        response.addCookie(cookie);
    }

}
