package com.laxqnsys.core.buz.doc.service;

import com.laxqnsys.core.buz.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.buz.doc.model.vo.DocFileContentResVO;
import java.util.List;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;

/**
 * 文档内容存储服务
 * @author wuzhenhong
 * @date 2025/2/28 14:31
 */
public interface IDocFileContentStorageService {

    boolean create(DocFileFolder fileFolder, Supplier<Boolean> afterSuccess);

    boolean copy(List<DocFileFolder> fileFolders, Supplier<Boolean> afterSuccess);

    boolean update(DocFileFolder fileFolder, Supplier<Boolean> afterSuccess);

    boolean delete(DocFileFolder docFileFolder);

    @Deprecated
    default DocFileContentResVO getFileContent(DocFileFolder docFileFolder) {
        throw new UnsupportedOperationException("该接口不再支持使用，避免读取大量数据到内存，造成gc压力！");
    }

    /**
     * 计算文件的哈希值（流式计算，避免将整个文件加载到内存）
     * @param docFileFolder 文件信息
     * @return 文件内容的MD5哈希值，如果文件不存在返回null
     */
    default String computeFileHash(DocFileFolder docFileFolder) {
        throw new UnsupportedOperationException("子类必须实现该方法");
    }

    void downloadFileContent(DocFileFolder docFileFolder, HttpServletResponse response);

}
