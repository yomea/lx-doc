package com.laxqnsys.core.doc.ao;

import com.laxqnsys.core.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.FileFolderCopyVO;
import com.laxqnsys.core.doc.model.vo.FileFolderCreateVO;
import com.laxqnsys.core.doc.model.vo.FileFolderDelVO;
import com.laxqnsys.core.doc.model.vo.FileFolderMoveVO;
import com.laxqnsys.core.doc.model.vo.FileFolderQueryVO;
import com.laxqnsys.core.doc.model.vo.FileFolderUpdateVO;
import java.util.List;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:35
 */
public interface DocFileFolderAO {

    List<DocFileFolderResVO> getFolderTree(Long folderId);

    DocFileAndFolderResVO getFolderAndFileList(FileFolderQueryVO queryVO);

    DocFileAndFolderResVO searchFolderAndFile(FileFolderQueryVO queryVO);

    DocFileFolderResVO crateFolder(FileFolderCreateVO createVO);

    void updateFolder(FileFolderUpdateVO updateVO);

    void deleteFolder(FileFolderDelVO delVO);

    void moveFolder(FileFolderMoveVO moveVO);

    List<DocFileFolderResVO> getFolderPath(Long folderId);

    void copyFolder(FileFolderCopyVO copyVO);
}
