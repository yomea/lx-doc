package com.lxqnsys.doc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxqnsys.doc.dao.entity.DocFileFolder;
import com.lxqnsys.doc.dao.mapper.DocFileFolderMapper;
import com.lxqnsys.doc.service.IDocFileFolderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文档-文件夹 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@Service
public class DocFileFolderServiceImpl extends ServiceImpl<DocFileFolderMapper, DocFileFolder> implements
    IDocFileFolderService {

}
