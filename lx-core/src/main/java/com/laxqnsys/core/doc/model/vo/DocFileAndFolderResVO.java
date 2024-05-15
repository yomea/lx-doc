package com.laxqnsys.core.doc.model.vo;

import java.util.List;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 20:42
 */
@Data
public class DocFileAndFolderResVO {

    private List<DocFileFolderResVO> folderList;

    private List<DocFileResVO> fileList;
}
