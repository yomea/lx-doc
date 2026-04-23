package com.laxqnsys.core.buz.doc.service;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.buz.doc.dao.entity.DocFileFolder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.commons.codec.binary.Hex;
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
            // 内容未发生变化，不更新文件内容（使用哈希比较，避免加载整个文件到内存）
            if (this.isContentUnchanged(fileFolder, content)) {
                success = true;
            } else {
                // 增加文件版本
                fileFolder.setVersion(fileFolder.getVersion() + 1);
                success = this.update(fileFolder);
            }
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

    /**
     * 使用哈希比较判断内容是否发生变化（流式计算，不占用大量内存）
     */
    protected boolean isContentUnchanged(DocFileFolder fileFolder, String newContent) {
        try {
            String fileHash = this.computeFileHash(fileFolder);
            if (Objects.isNull(fileHash)) {
                return false;
            }
            String newHash = this.computeContentHash(newContent);
            return fileHash.equals(newHash);
        } catch (Exception e) {
            // 计算失败时，保守处理，执行更新
            return false;
        }
    }

    /**
     * 计算字符串内容的MD5哈希值
     */
    protected String computeContentHash(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(digest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "计算内容哈希失败！", e);
        }
    }

    public abstract boolean create(DocFileFolder fileFolder);

    public abstract boolean copy(List<DocFileFolder> fileFolders);

    public abstract boolean update(DocFileFolder fileFolder);
}
