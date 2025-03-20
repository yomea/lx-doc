package com.laxqnsys.core.buz.sys.ao;

import com.laxqnsys.core.buz.sys.model.vo.SysUserConfigReqVO;

/**
 * @author wuzhenhong
 * @date 2024/5/14 16:33
 */
public interface SysUserConfigAO {

    String getUserConfig(String configType);

    void saveOrUpdateUserConfig(SysUserConfigReqVO sysUserConfigVO);
}
