package com.laxqnsys.core.buz.sys.ao;

import com.laxqnsys.core.buz.sys.model.vo.UserInfoUpdateVO;
import com.laxqnsys.core.buz.sys.model.vo.UserInfoVO;
import com.laxqnsys.core.buz.sys.model.vo.UserLoginVO;
import com.laxqnsys.core.buz.sys.model.vo.UserPwdModifyVO;
import com.laxqnsys.core.buz.sys.model.vo.UserRegisterVO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuzhenhong
 * @date 2024/5/14 11:07
 */
public interface SysUserInfoAO {

    void register(UserRegisterVO userRegisterVO);

    void login(UserLoginVO userLoginVO, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);

    UserInfoVO getUserInfo();

    void updateUserInfo(UserInfoUpdateVO userInfoUpdateVO);

    void changePassword(UserPwdModifyVO userPwdModifyVO);
}
