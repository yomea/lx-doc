package com.laxqnsys.core.sys.ao.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.common.util.AESUtil;
import com.laxqnsys.core.constants.CommonCons;
import com.laxqnsys.core.constants.RedissonLockPrefixCons;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.enums.UserStatusEnum;
import com.laxqnsys.core.manager.service.UserLoginManager;
import com.laxqnsys.core.sys.ao.SysUserInfoAO;
import com.laxqnsys.core.sys.dao.entity.SysUserInfo;
import com.laxqnsys.core.sys.model.bo.UserInfoBO;
import com.laxqnsys.core.sys.model.vo.UserInfoUpdateVO;
import com.laxqnsys.core.sys.model.vo.UserInfoVO;
import com.laxqnsys.core.sys.model.vo.UserLoginVO;
import com.laxqnsys.core.sys.model.vo.UserPwdModifyVO;
import com.laxqnsys.core.sys.model.vo.UserRegisterVO;
import com.laxqnsys.core.sys.service.ISysFileUploadService;
import com.laxqnsys.core.sys.service.ISysUserInfoService;
import com.laxqnsys.core.util.web.WebUtil;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/14 11:07
 */
@Slf4j
@Service
public class SysUserInfoAOImpl implements SysUserInfoAO {

    private static final Object OBJECT = new Object();
    private static final Map<String, Object> LOCK = new ConcurrentHashMap<>();

    @Autowired
    private ISysFileUploadService sysFileUploadService;

    @Autowired
    private ISysUserInfoService sysUserInfoService;

    @Autowired
    private UserLoginManager userLoginManager;

    @Override
    public void register(UserRegisterVO userRegisterVO) {

        long count = sysUserInfoService.count(Wrappers.<SysUserInfo>lambdaQuery()
            .eq(SysUserInfo::getAccount, userRegisterVO.getAccount()));
        if (count > 0L) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("名为%s的账户已存在，请设置其他的账户名！", userRegisterVO.getAccount()));
        }

        String lockKey = RedissonLockPrefixCons.USER_REGISTER + "_" + userRegisterVO.getAccount();
        while (LOCK.putIfAbsent(lockKey, OBJECT) != null) {
            Thread.yield();
        }
        try {
            long c = sysUserInfoService.count(Wrappers.<SysUserInfo>lambdaQuery()
                .eq(SysUserInfo::getAccount, userRegisterVO.getAccount()));
            if (c > 0L) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("名为%s的账户已存在，请设置其他的账户名！", userRegisterVO.getAccount()));
            }
            SysUserInfo userInfo = new SysUserInfo();
            userInfo.setAccount(userRegisterVO.getAccount());
            String pwd = AESUtil.encrypt(userRegisterVO.getPassword(), CommonCons.AES_KEY);
            userInfo.setPassword(pwd);
            userInfo.setCreateAt(LocalDateTime.now());
            userInfo.setVersion(0);
            userInfo.setUpdateAt(LocalDateTime.now());
            userInfo.setStatus(UserStatusEnum.NORMAL.getStatus());
            sysUserInfoService.save(userInfo);
        } finally {
            LOCK.remove(lockKey);
        }

    }

    @Override
    public void login(UserLoginVO userLoginVO, HttpServletResponse response) {

        String password = userLoginVO.getPassword();
        String pwd = AESUtil.encrypt(password, CommonCons.AES_KEY);
        SysUserInfo userInfo = sysUserInfoService.getOne(Wrappers.<SysUserInfo>lambdaQuery()
            .eq(SysUserInfo::getAccount, userLoginVO.getAccount())
            .eq(SysUserInfo::getPassword, pwd));
        if (Objects.isNull(userInfo)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "用户名或者密码错误！");
        }

        this.userStatusCheck(userInfo);

        // 踢人
        String key = this.downOldLogin(userInfo.getId());

        String token = UUID.randomUUID().toString().replace("-", "");
        UserInfoBO userInfoBO = new UserInfoBO();
        userInfoBO.setAccount(userInfo.getAccount());
        userInfoBO.setId(userInfo.getId());
        userLoginManager.set(token, JSONUtil.toJsonStr(userInfoBO), CommonCons.LOGIN_EXPIRE_SECONDS);
        userLoginManager.set(key, token, CommonCons.LOGIN_TOKEN_EXPIRE_SECONDS);
        WebUtil.saveCookie(response, token, CommonCons.LOGIN_EXPIRE_SECONDS);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = WebUtil.getCookie(request, CommonCons.TOKEN_KEY);
        if (!StringUtils.hasText(token)) {
            return;
        }
        WebUtil.saveCookie(response, token, 0);
        userLoginManager.delete(token);
        String key = CommonCons.LOGIN_USER_TOKE_KEY + LoginContext.getUserId();
        userLoginManager.delete(key);
    }

    @Override
    public UserInfoVO getUserInfo() {
        // 获取当前登录人信息
        Long id = LoginContext.getUserId();
        if (Objects.isNull(id)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "获取当前登录人信息失败！");
        }
        SysUserInfo userInfo = sysUserInfoService.getById(id);
        if (Objects.isNull(userInfo)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("未获取到id为%s的登录人信息！", id));
        }

        this.userStatusCheck(userInfo);

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(userInfo.getId());
        userInfoVO.setAccount(userInfo.getAccount());
        userInfoVO.setUserName(userInfo.getUserName());
        userInfoVO.setAvatar(userInfo.getAvatar());
        userInfoVO.setCreateAt(userInfo.getCreateAt());
        return userInfoVO;
    }

    @Override
    public void updateUserInfo(UserInfoUpdateVO userInfoUpdateVO) {
        Long userId = LoginContext.getUserId();
        SysUserInfo userInfo = sysUserInfoService.getById(userId);
        if (Objects.isNull(userInfo)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("未获取到id为%s的登录人信息！", userId));
        }
        this.userStatusCheck(userInfo);
        // 没有变动，不需要操作更新，如果要操作的字段比较多的时候，需要改成注解的方式标注做比较
        if (Objects.equals(userInfo.getUserName(), userInfoUpdateVO.getUserName())
            && Objects.equals(userInfo.getAvatar(), userInfoUpdateVO.getAvatar())) {
            return;
        }
        SysUserInfo update = new SysUserInfo();
        update.setId(userId);
        update.setUserName(userInfoUpdateVO.getUserName());
        update.setAvatar(userInfoUpdateVO.getAvatar());
        boolean success = sysUserInfoService.updateById(update);
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "更新用户信息失败！");
        }
        try {
            // 删除老的头像图片不是必须的步骤，即使出错也不要影响主流程
            // 如果出错可以通过发送邮件等报错信息去提示管理员处理
            String newAvatar = userInfoUpdateVO.getAvatar();
            String oldAvatar = userInfo.getAvatar();
            if (StringUtils.hasText(newAvatar)
                && StringUtils.hasText(oldAvatar)
                && !newAvatar.equals(oldAvatar)) {
                // 删除老的头像附件，节省空间
                sysFileUploadService.delete(oldAvatar);
            }
        } catch (Exception e) {
            log.error("删除老的头像失败！", e);
        }
    }

    @Override
    public void changePassword(UserPwdModifyVO userPwdModifyVO) {
        Long userId = LoginContext.getUserId();
        SysUserInfo sysUserInfo = sysUserInfoService.getById(userId);
        if (Objects.isNull(sysUserInfo)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("未获取到id为%s的登录人信息！", userId));
        }
        String oldPassword = userPwdModifyVO.getOldPassword();
        String password = sysUserInfo.getPassword();
        if (!password.equals(AESUtil.encrypt(oldPassword, CommonCons.AES_KEY))) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "原秘密输入不正确！");
        }
        String newPassword = userPwdModifyVO.getNewPassword();
        String newPwd = AESUtil.encrypt(newPassword, CommonCons.AES_KEY);
        SysUserInfo update = new SysUserInfo();
        update.setId(userId);
        update.setPassword(newPwd);
        sysUserInfoService.updateById(update);
    }

    private String downOldLogin(Long userId) {

        String key = CommonCons.LOGIN_USER_TOKE_KEY + userId;
        String oldToken = userLoginManager.get(key);
        if (StringUtils.hasText(oldToken)) {
            // 踢掉其他的登录信息
            userLoginManager.delete(oldToken);
        }
        return key;
    }

    private void userStatusCheck(SysUserInfo userInfo) {

        if (UserStatusEnum.DISABLED.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "当前用户已被禁用！");
        }

        if (UserStatusEnum.DELETE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "当前用户已注销！");
        }
    }
}
