package com.laxqnsys.core.sys.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadBO {

    private String url;

    private Long size;

    // other field info
}
