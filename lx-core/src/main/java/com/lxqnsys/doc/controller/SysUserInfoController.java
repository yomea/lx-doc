package com.lxqnsys.doc.controller;


import cn.hutool.system.UserInfo;
import com.laxqnsys.common.model.ResponseResult;
import com.lxqnsys.doc.ao.SysUserInfoAO;
import com.lxqnsys.doc.model.vo.UserInfoVO;
import com.lxqnsys.doc.model.vo.UserLoginVO;
import com.lxqnsys.doc.model.vo.UserRegisterVO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统-用户信息 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/user")
public class SysUserInfoController {

    @Autowired
    private SysUserInfoAO sysUserInfoAO;

    @PostMapping("/register")
    public ResponseResult<Void> register(@RequestBody @Validated UserRegisterVO userRegisterVO) {
        sysUserInfoAO.register(userRegisterVO);
        return ResponseResult.ok();
    }

    @PostMapping("/login")
    public ResponseResult<Void> login(@RequestBody @Validated UserLoginVO userLoginVO, HttpServletResponse response) {
        sysUserInfoAO.login(userLoginVO, response);
        return ResponseResult.ok();
    }

    @GetMapping("/logout")
    public ResponseResult<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        sysUserInfoAO.logout(request, response);
        return ResponseResult.ok();
    }

    @GetMapping("/getUserInfo")
    public ResponseResult<UserInfoVO> getUserInfo() {
        UserInfoVO userInfoVO = sysUserInfoAO.getUserInfo();
        return ResponseResult.ok(userInfoVO);
    }

    @PostMapping("/updateUserInfo")
    public ResponseResult<Void> updateUserInfo(@RequestBody UserInfoVO userInfoVO) {
        sysUserInfoAO.updateUserInfo(userInfoVO);
        return ResponseResult.ok();
    }

}
