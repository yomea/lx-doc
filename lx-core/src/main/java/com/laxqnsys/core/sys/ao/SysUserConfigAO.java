package com.laxqnsys.core.sys.ao;

import com.laxqnsys.core.sys.model.vo.SysUserConfigQueryVO;
import com.laxqnsys.core.sys.model.vo.SysUserConfigReqVO;

/**
 * @author wuzhenhong
 * @date 2024/5/14 16:33
 */
public interface SysUserConfigAO {

    String getUserConfig(String configType);

    void saveOrUpdateUserConfig(SysUserConfigReqVO sysUserConfigVO);
}
