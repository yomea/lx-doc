package com.laxqnsys.core.doc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.laxqnsys.core.doc.dao.entity.DocFileContent;
import com.laxqnsys.core.doc.model.dto.DocFileCopyDTO;
import java.util.List;

/**
 * <p>
 * 文档-文件内容 服务类
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
public interface IDocFileContentService extends IService<DocFileContent> {

    int copyByFileIdList(List<DocFileCopyDTO> copyList, Long userId);
}
