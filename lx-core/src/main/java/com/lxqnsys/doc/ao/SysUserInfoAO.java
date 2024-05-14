package com.lxqnsys.doc.ao;

import com.lxqnsys.doc.model.vo.UserInfoUpdateVO;
import com.lxqnsys.doc.model.vo.UserInfoVO;
import com.lxqnsys.doc.model.vo.UserLoginVO;
import com.lxqnsys.doc.model.vo.UserPwdModifyVO;
import com.lxqnsys.doc.model.vo.UserRegisterVO;
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
