package com.laxqnsys.core.doc.model.vo;

import java.util.List;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:23
 */
@Data
public class DocFileFolderResVO extends DocFileFolderBaseResVO {

    private boolean leaf;

    private List<DocFileFolderBaseResVO> path;
}
