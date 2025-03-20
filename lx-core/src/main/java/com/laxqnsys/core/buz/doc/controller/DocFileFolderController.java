package com.laxqnsys.core.buz.doc.controller;


import com.laxqnsys.common.model.ResponseResult;
import com.laxqnsys.core.buz.doc.model.vo.DocSynthFileFolderResVO;
import com.laxqnsys.core.buz.doc.ao.DocFileFolderAO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.buz.doc.model.vo.FileFolderCopyVO;
import com.laxqnsys.core.buz.doc.model.vo.FileFolderCreateVO;
import com.laxqnsys.core.buz.doc.model.vo.FileFolderDelVO;
import com.laxqnsys.core.buz.doc.model.vo.FileFolderMoveVO;
import com.laxqnsys.core.buz.doc.model.vo.FileFolderQueryVO;
import com.laxqnsys.core.buz.doc.model.vo.FileFolderUpdateVO;
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
 * 文档-文件夹 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@RestController
@RequestMapping("/api")
public class DocFileFolderController {

    @Autowired
    private DocFileFolderAO docFileFolderAO;

    @GetMapping(value = "/getFolderTree")
    public ResponseResult<List<DocFileFolderResVO>> getFolderTree(@RequestParam Long folderId) {
        List<DocFileFolderResVO> resVOS = docFileFolderAO.getFolderTree(folderId);
        return ResponseResult.ok(resVOS);
    }

    @GetMapping(value = "/getAllFolderTree")
    public ResponseResult<List<DocSynthFileFolderResVO>> getAllFolderTree() {
        List<DocSynthFileFolderResVO> resVOS = docFileFolderAO.getAllFolderTree();
        return ResponseResult.ok(resVOS);
    }

    @GetMapping(value = "/getFolderPath")
    public ResponseResult<List<DocFileFolderResVO>> getFolderPath(@RequestParam Long folderId) {
        List<DocFileFolderResVO> resVOS = docFileFolderAO.getFolderPath(folderId);
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

    @PostMapping(value = "/crateFolder")
    public ResponseResult<DocFileFolderResVO> crateFolder(@RequestBody @Validated FileFolderCreateVO createVO) {
        DocFileFolderResVO resVO = docFileFolderAO.crateFolder(createVO);
        return ResponseResult.ok(resVO);
    }

    @PostMapping(value = "/updateFolder")
    public ResponseResult<Void> updateFolder(@RequestBody @Validated FileFolderUpdateVO updateVO) {
        docFileFolderAO.updateFolder(updateVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/deleteFolder")
    public ResponseResult<Void> deleteFolder(@RequestBody @Validated FileFolderDelVO delVO) {
        docFileFolderAO.deleteFolder(delVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/moveFolder")
    public ResponseResult<Void> moveFolder(@RequestBody @Validated FileFolderMoveVO moveVO) {
        docFileFolderAO.moveFolder(moveVO);
        return ResponseResult.ok();
    }

    @PostMapping(value = "/copyFolder")
    public ResponseResult<Void> copyFolder(@RequestBody @Validated FileFolderCopyVO copyVO) {
        docFileFolderAO.copyFolder(copyVO);
        return ResponseResult.ok();
    }
}
