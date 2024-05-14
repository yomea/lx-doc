package com.laxqnsys.core.sys.controller;


import com.laxqnsys.common.model.ResponseResult;
import com.laxqnsys.core.sys.model.vo.SysUserConfigVO;
import com.laxqnsys.core.sys.ao.SysUserConfigAO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 系统-用户配置 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/userConfig")
public class SysUserConfigController {

    @Autowired
    private SysUserConfigAO sysUserConfigAO;

    @GetMapping("/getUserConfig")
    public ResponseResult<List<SysUserConfigVO>> getUserConfig() {
        return ResponseResult.ok(sysUserConfigAO.getUserConfig());
    }

    @PostMapping("/updateUserConfig")
    public ResponseResult<Void> updateUserConfig(@RequestBody @Validated SysUserConfigVO sysUserConfigVO) {
        sysUserConfigAO.saveOrUpdateUserConfig(sysUserConfigVO);
        return ResponseResult.ok();
    }
}
