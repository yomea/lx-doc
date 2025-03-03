package com.laxqnsys.core.doc.service;

import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
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

    void downloadFileContent(DocFileFolder docFileFolder, HttpServletResponse response);

}
