package com.laxqnsys.core.doc.controller;


import com.laxqnsys.common.model.ResponseResult;
import com.laxqnsys.core.doc.ao.DocCollectFolderAO;
import com.laxqnsys.core.doc.model.vo.DocCollectReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileResVO;
import java.util.List;
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
 * 文档-文件收藏夹 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/api")
public class DocCollectFolderController {

    @Autowired
    private DocCollectFolderAO docCollectFolderAO;

    @GetMapping(value = "/getCollectFileList")
    public ResponseResult<List<DocFileResVO>> getCollectFileList(@RequestParam(defaultValue = "") String name) {
        List<DocFileResVO> resVOs = docCollectFolderAO.getCollectFileList(name);
        return ResponseResult.ok(resVOs);
    }

    @PostMapping(value = "/cancelCollect")
    public ResponseResult<Void> cancelCollect(@RequestBody @Validated DocCollectReqVO reqVO) {
        docCollectFolderAO.cancelCollect(reqVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/collect")
    public ResponseResult<Void> collect(@RequestBody @Validated DocCollectReqVO reqVO) {
        docCollectFolderAO.collect(reqVO);
        return ResponseResult.ok();
    }
}
