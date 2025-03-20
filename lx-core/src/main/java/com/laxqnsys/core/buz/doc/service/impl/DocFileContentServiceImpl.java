package com.laxqnsys.core.buz.doc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laxqnsys.core.buz.doc.dao.entity.DocFileContent;
import com.laxqnsys.core.buz.doc.dao.mapper.DocFileContentMapper;
import com.laxqnsys.core.buz.doc.model.dto.DocFileCopyDTO;
import com.laxqnsys.core.buz.doc.service.IDocFileContentService;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文档-文件内容 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@Service
public class DocFileContentServiceImpl extends ServiceImpl<DocFileContentMapper, DocFileContent> implements
    IDocFileContentService {

    @Override
    public int copyByFileIdList(List<DocFileCopyDTO> copyList, Long userId) {
        return super.baseMapper.copyByFileIdList(copyList, userId);
    }
}
