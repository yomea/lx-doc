package com.laxqnsys.core.buz.doc.model.dto;

import java.io.File;
import java.io.Serializable;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/16 10:14
 */
@Data
public class DocFileCopyDTO implements Serializable {

    private Long oldFileId;
    private Long newFileId;
    private File oldFile;
    private File newFile;
}
