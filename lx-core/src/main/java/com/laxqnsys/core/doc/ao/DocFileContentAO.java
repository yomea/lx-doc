package com.laxqnsys.core.doc.ao;

import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.doc.model.vo.DocFileCopyReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileCreateReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileDelReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileMoveReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileUpdateReqVO;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:29
 */
public interface DocFileContentAO {

    DocFileContentResVO getFileContent(Long id);

    DocFileContentResVO createFile(DocFileCreateReqVO createReqVO);

    void updateFile(DocFileUpdateReqVO updateReqVO);

    void moveFile(DocFileMoveReqVO reqVO);

    void copyFile(DocFileCopyReqVO reqVO);

    void deleteFile(DocFileDelReqVO reqVO);
}
