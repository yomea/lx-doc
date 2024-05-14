package com.laxqnsys.core.doc.ao;

import com.laxqnsys.core.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.FileFolderQueryVO;
import java.util.List;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:35
 */
public interface DocFileFolderAO {

    List<DocFileFolderResVO> getFolderTree(FileFolderQueryVO queryVO);

    DocFileAndFolderResVO getFolderAndFileList(FileFolderQueryVO queryVO);
}
