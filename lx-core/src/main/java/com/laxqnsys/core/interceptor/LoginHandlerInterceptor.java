package com.laxqnsys.core.interceptor;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.constants.CommonCons;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.manager.service.UserLoginManager;
import com.laxqnsys.core.sys.model.bo.UserInfoBO;
import com.laxqnsys.core.util.web.WebUtil;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author wuzhenhong
 * @date 2024/5/14 8:48
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {

    private List<String> whiteUrlList = Lists.newArrayList();
    private List<String> blackUrlList = Lists.newArrayList();
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    private UserLoginManager userLoginManager;

    public LoginHandlerInterceptor(UserLoginManager userLoginManager) {
        this.userLoginManager = userLoginManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        String uri = request.getRequestURI();
        boolean match = whiteUrlList.stream().anyMatch(url -> antPathMatcher.match(url, uri));
        if (match) {
            return true;
        }
        match = blackUrlList.stream().anyMatch(url -> antPathMatcher.match(url, uri));
        if (!match) {
            return true;
        }
        String token = WebUtil.getCookie(request, CommonCons.TOKEN_KEY);
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCodeEnum.UN_LOGIN.getCode(), ErrorCodeEnum.UN_LOGIN.getDesc());
        }
        String userJsonInfo = userLoginManager.get(token);
        if (!StringUtils.hasText(userJsonInfo)) {
            throw new BusinessException(ErrorCodeEnum.UN_LOGIN.getCode(), ErrorCodeEnum.UN_LOGIN.getDesc());
        }
        UserInfoBO userInfoBO = JSONUtil.toBean(userJsonInfo, UserInfoBO.class);
        LoginContext.setUserInfo(userInfoBO);
        // 续期
        userLoginManager.expire(token, CommonCons.LOGIN_EXPIRE_SECONDS);
        String key = CommonCons.LOGIN_USER_TOKE_KEY + LoginContext.getUserId();
        userLoginManager.expire(key, CommonCons.LOGIN_TOKEN_EXPIRE_SECONDS);
        return true;
    }

    public void addWhiteUrl(String url) {
        whiteUrlList.add(url);
    }

    public void addBlackUrl(String url) {
        blackUrlList.add(url);
    }
}
