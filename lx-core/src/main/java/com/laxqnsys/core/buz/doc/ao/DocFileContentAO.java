package com.laxqnsys.core.buz.doc.ao;

import com.laxqnsys.core.buz.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileCopyReqVO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileCreateReqVO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileDelReqVO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileMoveReqVO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileUpdateReqVO;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:29
 */
public interface DocFileContentAO {


    void downloadFileContent(Long id, HttpServletResponse response);

    DocFileContentResVO getFileBaseInfo(Long id);

    @Deprecated
    DocFileContentResVO getFileContent(Long id);

    DocFileContentResVO createFile(DocFileCreateReqVO createReqVO);

    void updateFile(DocFileUpdateReqVO updateReqVO);

    void moveFile(DocFileMoveReqVO reqVO);

    void copyFile(DocFileCopyReqVO reqVO);

    void deleteFile(DocFileDelReqVO reqVO);
}
