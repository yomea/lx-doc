package com.laxqnsys.core.buz.doc.controller;


import com.laxqnsys.common.model.ResponseResult;
import com.laxqnsys.core.buz.doc.ao.DocRecycleAO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.buz.doc.model.vo.DocRecycleReqVO;
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
 * 文档-回收站 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-17
 */
@RestController
@RequestMapping("/api")
public class DocRecycleController {

    @Autowired
    private DocRecycleAO docRecycleAO;

    @GetMapping(value = "/getRecycleFolderAndFileList")
    public ResponseResult<DocFileAndFolderResVO> getRecycleFolderAndFileList(@RequestParam(defaultValue = "") String name) {
        DocFileAndFolderResVO resVO = docRecycleAO.getRecycleFolderAndFileList(name);
        return ResponseResult.ok(resVO);
    }

    @PostMapping(value = "/restore")
    public ResponseResult<Void> restore(@RequestBody @Validated DocRecycleReqVO reqVO) {
        docRecycleAO.restore(reqVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/completelyDelete")
    public ResponseResult<Void> completelyDelete(@RequestBody @Validated DocRecycleReqVO reqVO) {
        docRecycleAO.completelyDelete(reqVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/emptyRecycle")
    public ResponseResult<Void> emptyRecycle() {
        docRecycleAO.emptyRecycle();
        return ResponseResult.ok();
    }

}
