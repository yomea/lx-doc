package com.laxqnsys.core.buz.sys.controller;


import com.laxqnsys.common.model.ResponseResult;
import com.laxqnsys.core.buz.sys.ao.SysUserConfigAO;
import com.laxqnsys.core.buz.sys.model.vo.SysUserConfigReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/api")
public class SysUserConfigController {

    @Autowired
    private SysUserConfigAO sysUserConfigAO;

    @GetMapping("/getUserConfig")
    public ResponseResult<String> getUserConfig(@RequestParam(value = "configType") String configType) {
        return ResponseResult.ok(sysUserConfigAO.getUserConfig(configType));
    }

    @PostMapping("/updateUserConfig")
    public ResponseResult<Void> updateUserConfig(@RequestBody @Validated SysUserConfigReqVO sysUserConfigVO) {
        sysUserConfigAO.saveOrUpdateUserConfig(sysUserConfigVO);
        return ResponseResult.ok();
    }
}
