package com.laxqnsys.core.sys.ao;

import com.laxqnsys.core.sys.model.vo.SysUserConfigVO;
import java.util.List;

/**
 * @author wuzhenhong
 * @date 2024/5/14 16:33
 */
public interface SysUserConfigAO {

    List<SysUserConfigVO> getUserConfig();

    void saveOrUpdateUserConfig(SysUserConfigVO sysUserConfigVO);
}
