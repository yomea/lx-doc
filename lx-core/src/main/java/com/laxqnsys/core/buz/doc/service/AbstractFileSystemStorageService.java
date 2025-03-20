package com.laxqnsys.core.buz.doc.service;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.buz.doc.dao.entity.DocFileFolder;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.util.StringUtils;

/**
 * 抽象的文件系统存储服务
 * @author wuzhenhong
 * @date 2025/3/3 11:05
 */
public abstract class AbstractFileSystemStorageService implements IDocFileContentStorageService {

    @Override
    public boolean create(DocFileFolder fileFolder, Supplier<Boolean> afterSuccess) {
        boolean success = this.create(fileFolder);
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档文件创建失败！");
        }
        success = afterSuccess.get();
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档文件创建失败！");
        }
        return true;
    }

    @Override
    public boolean copy(List<DocFileFolder> fileFolders, Supplier<Boolean> afterSuccess) {
        boolean success = this.copy(fileFolders);
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档文件复制失败！");
        }
        success = afterSuccess.get();
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档文件复制失败！");
        }
        return true;
    }

    @Override
    public boolean update(DocFileFolder fileFolder, Supplier<Boolean> afterSuccess) {
        String content = fileFolder.getContent();
        boolean success;
        // 磁盘存储，有内容才存储，没有内容不需要实际做存储的操作
        if(StringUtils.hasText(content)) {
            // 增加文件版本
            fileFolder.setVersion(fileFolder.getVersion() + 1);
            success = this.update(fileFolder);
        } else {
            success = true;
        }
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档文件更新失败！");
        }
        success = afterSuccess.get();
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档文件更新失败！");
        }
        return true;
    }

    public abstract boolean create(DocFileFolder fileFolder);

    public abstract boolean copy(List<DocFileFolder> fileFolders);

    public abstract boolean update(DocFileFolder fileFolder);
}
