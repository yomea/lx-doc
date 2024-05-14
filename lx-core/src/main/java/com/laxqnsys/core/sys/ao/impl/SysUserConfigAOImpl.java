package com.laxqnsys.core.sys.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.model.vo.SysUserConfigVO;
import com.laxqnsys.core.enums.DelStatusEnum;
import com.laxqnsys.core.sys.ao.SysUserConfigAO;
import com.laxqnsys.core.sys.dao.entity.SysUserConfig;
import com.laxqnsys.core.sys.service.ISysUserConfigService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wuzhenhong
 * @date 2024/5/14 16:33
 */
@Service
public class SysUserConfigAOImpl implements SysUserConfigAO {

    @Autowired
    private ISysUserConfigService sysUserConfigService;


    @Override
    public List<SysUserConfigVO> getUserConfig() {
        Long userId = LoginContext.getUserId();
        List<SysUserConfig> sysUserConfigList = sysUserConfigService.list(Wrappers.<SysUserConfig>lambdaQuery()
            .eq(SysUserConfig::getUserId, userId)
            .eq(SysUserConfig::getStatus, DelStatusEnum.NORMAL.getStatus()));
        return sysUserConfigList.stream().map(config -> {
            SysUserConfigVO configVO = new SysUserConfigVO();
            BeanUtils.copyProperties(config, configVO);
            return configVO;
        }).collect(Collectors.toList());
    }

    @Override
    public void saveOrUpdateUserConfig(SysUserConfigVO sysUserConfigVO) {
        Long id = sysUserConfigVO.getId();
        Long userId = LoginContext.getUserId();
        if (Objects.isNull(id)) {
            SysUserConfig sysUserConfig = new SysUserConfig();
            sysUserConfig.setBussinessType(StringUtils.isBlank(sysUserConfigVO.getBussinessType())
                ? "lx-doc" : sysUserConfigVO.getBussinessType());
            sysUserConfig.setLayoutType(sysUserConfigVO.getLayoutType());
            sysUserConfig.setRule(sysUserConfigVO.getRule());
            sysUserConfig.setUserId(userId);
            sysUserConfig.setVersion(0);
            sysUserConfig.setCreateAt(LocalDateTime.now());
            sysUserConfig.setUpdateAt(LocalDateTime.now());
            sysUserConfig.setStatus(DelStatusEnum.NORMAL.getStatus());
            sysUserConfigService.save(sysUserConfig);
        } else {
            SysUserConfig sysUserConfig = sysUserConfigService.getById(id);
            if (Objects.isNull(sysUserConfig)) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("id为%s的用户配置不存在！", userId));
            }
            SysUserConfig update = new SysUserConfig();
            update.setId(id);
            update.setLayoutType(sysUserConfigVO.getLayoutType());
            update.setRule(sysUserConfigVO.getRule());
            sysUserConfigService.updateById(update);
        }
    }
}
