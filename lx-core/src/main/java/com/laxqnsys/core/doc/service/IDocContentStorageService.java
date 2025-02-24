package com.laxqnsys.core.doc.service;

import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuzhenhong
 * @date 2025/2/20 9:43
 */
public interface IDocContentStorageService {

    Integer storageType();

    boolean create(DocFileFolder file, String content);

    boolean update(DocFileFolder file, String content);

    boolean delete(Long id);

    void download(Long fileId, HttpServletResponse response);
}
