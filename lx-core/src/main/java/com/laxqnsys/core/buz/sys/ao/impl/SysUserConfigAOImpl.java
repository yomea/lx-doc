package com.laxqnsys.core.buz.sys.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.other.aspect.lock.ConcurrentLock;
import com.laxqnsys.core.buz.sys.dao.entity.SysUserConfig;
import com.laxqnsys.core.other.constants.RedissonLockPrefixCons;
import com.laxqnsys.core.other.context.LoginContext;
import com.laxqnsys.core.other.enums.DelStatusEnum;
import com.laxqnsys.core.buz.sys.ao.SysUserConfigAO;
import com.laxqnsys.core.buz.sys.model.vo.SysUserConfigReqVO;
import com.laxqnsys.core.buz.sys.service.ISysUserConfigService;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/14 16:33
 */
@Service
public class SysUserConfigAOImpl implements SysUserConfigAO {

    @Autowired
    private ISysUserConfigService sysUserConfigService;


    @Override
    public String getUserConfig(String configType) {
        if (!StringUtils.hasText(configType)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "configType必传！");
        }
        Long userId = LoginContext.getUserId();
        SysUserConfig sysUserConfig = sysUserConfigService.getOne(Wrappers.<SysUserConfig>lambdaQuery()
            .eq(SysUserConfig::getUserId, userId)
            .eq(SysUserConfig::getConfigType, configType)
            .eq(SysUserConfig::getStatus, DelStatusEnum.NORMAL.getStatus())
            .last("limit 1"));
        if (Objects.isNull(sysUserConfig)) {
            return null;
        }
        return sysUserConfig.getConfigContent();
    }

    @Override
    @ConcurrentLock(key = RedissonLockPrefixCons.USER_CONFIG_SAVE_OR_UPDATE
        + ":${sysUserConfigVO.configType}", expire = 2)
    public void saveOrUpdateUserConfig(SysUserConfigReqVO sysUserConfigVO) {
        String configType = sysUserConfigVO.getConfigType();
        Long userId = LoginContext.getUserId();
        SysUserConfig sysUserConfig = sysUserConfigService.getOne(Wrappers.<SysUserConfig>lambdaQuery()
            .eq(SysUserConfig::getUserId, userId)
            .eq(SysUserConfig::getConfigType, configType)
            .eq(SysUserConfig::getStatus, DelStatusEnum.NORMAL.getStatus())
            .last("limit 1"));
        if (Objects.isNull(sysUserConfig)) {
            sysUserConfig = new SysUserConfig();
            sysUserConfig.setConfigType(configType);
            sysUserConfig.setConfigContent(sysUserConfigVO.getConfigContent());
            sysUserConfig.setUserId(userId);
            sysUserConfig.setVersion(0);
            sysUserConfig.setCreateAt(LocalDateTime.now());
            sysUserConfig.setUpdateAt(LocalDateTime.now());
            sysUserConfig.setStatus(DelStatusEnum.NORMAL.getStatus());
            sysUserConfigService.save(sysUserConfig);
        } else {
            SysUserConfig update = new SysUserConfig();
            update.setId(sysUserConfig.getId());
            update.setConfigContent(sysUserConfigVO.getConfigContent());
            sysUserConfigService.updateById(update);
        }
    }
}
