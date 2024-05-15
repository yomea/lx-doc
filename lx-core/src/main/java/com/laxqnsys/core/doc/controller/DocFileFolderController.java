package com.laxqnsys.core.doc.controller;


import com.laxqnsys.common.model.ResponseResult;
import com.laxqnsys.core.doc.ao.DocFileFolderAO;
import com.laxqnsys.core.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.FileFolderQueryVO;
import com.laxqnsys.core.sys.model.vo.SysAttachmentVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 文档-文件夹 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@RestController
@RequestMapping("")
public class DocFileFolderController {

    @Autowired
    private DocFileFolderAO docFileFolderAO;

    @GetMapping(value = "/getFolderTree")
    public ResponseResult<List<DocFileFolderResVO>> getFolderTree(@RequestParam  Long folderId) {
        List<DocFileFolderResVO> resVOS = docFileFolderAO.getFolderTree(folderId);
        return ResponseResult.ok(resVOS);
    }

    @GetMapping(value = "/getFolderAndFileList")
    public ResponseResult<DocFileAndFolderResVO> getFolderAndFileList(FileFolderQueryVO queryVO) {
        DocFileAndFolderResVO resVO = docFileFolderAO.getFolderAndFileList(queryVO);
        return ResponseResult.ok(resVO);
    }

    @PostMapping(value = "/searchFolderAndFile")
    public ResponseResult<DocFileAndFolderResVO> searchFolderAndFile(@RequestBody FileFolderQueryVO queryVO) {
        DocFileAndFolderResVO resVO = docFileFolderAO.searchFolderAndFile(queryVO);
        return ResponseResult.ok(resVO);
    }
}
