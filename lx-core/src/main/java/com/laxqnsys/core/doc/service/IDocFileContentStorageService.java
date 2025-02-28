package com.laxqnsys.core.doc.service;

import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
 * 文档内容存储服务
 * @author wuzhenhong
 * @date 2025/2/28 14:31
 */
public interface IDocFileContentStorageService {

    boolean create(DocFileFolder fileFolder, Runnable afterSuccess);

    boolean update(DocFileFolder fileFolder, Runnable afterSuccess);

    boolean delete(DocFileFolder docFileFolder);

    DocFileContentResVO getFileContent(DocFileFolder docFileFolder);

    void downloadFileContent(DocFileFolder docFileFolder, HttpServletResponse response);

    boolean copy(List<DocFileFolder> fileFolders, Runnable afterSuccess);
}
