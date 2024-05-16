package com.laxqnsys.core.doc.model.dto;

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

}
