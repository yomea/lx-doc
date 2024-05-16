package com.laxqnsys.core.doc.controller;


import com.laxqnsys.common.model.ResponseResult;
import com.laxqnsys.core.doc.ao.DocFileContentAO;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.doc.model.vo.DocFileCopyReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileCreateReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileDelReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileMoveReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileUpdateReqVO;
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
 * 文档-文件内容 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@RestController
@RequestMapping("")
public class DocFileContentController {

    @Autowired
    private DocFileContentAO docFileContentAO;

    @GetMapping(value = "/getFileContent")
    public ResponseResult<DocFileContentResVO> getFileContent(@RequestParam Long id) {
        DocFileContentResVO resVO = docFileContentAO.getFileContent(id);
        return ResponseResult.ok(resVO);
    }

    @PostMapping(value = "/createFile")
    public ResponseResult<DocFileContentResVO> createFile(@RequestBody @Validated DocFileCreateReqVO createReqVO) {
        DocFileContentResVO resVO = docFileContentAO.createFile(createReqVO);
        return ResponseResult.ok(resVO);
    }

    @PostMapping(value = "/updateFile")
    public ResponseResult<Void> updateFile(@RequestBody @Validated DocFileUpdateReqVO updateReqVO) {
        docFileContentAO.updateFile(updateReqVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/moveFile")
    public ResponseResult<Void> moveFile(@RequestBody @Validated DocFileMoveReqVO reqVO) {
        docFileContentAO.moveFile(reqVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/copyFile")
    public ResponseResult<Void> copyFile(@RequestBody @Validated DocFileCopyReqVO reqVO) {
        docFileContentAO.copyFile(reqVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/deleteFile")
    public ResponseResult<Void> deleteFile(@RequestBody @Validated DocFileDelReqVO reqVO) {
        docFileContentAO.deleteFile(reqVO);
        return ResponseResult.ok();
    }

}
