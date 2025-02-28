package com.laxqnsys.core.doc.service.impl;

import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.doc.service.IDocFileContentStorageService;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author wuzhenhong
 * @date 2025/2/28 16:17
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "lx.doc.storage", name = "type", havingValue = "oss")
public class OssDocFileContentStorageServiceImpl implements IDocFileContentStorageService {

    @Override
    public boolean create(DocFileFolder fileFolder, Runnable afterSuccess) {
        return false;
    }

    @Override
    public boolean update(DocFileFolder fileFolder, Runnable afterSuccess) {
        return false;
    }

    @Override
    public boolean delete(DocFileFolder docFileFolder) {
        return false;
    }

    @Override
    public DocFileContentResVO getFileContent(DocFileFolder docFileFolder) {
        return null;
    }

    @Override
    public void downloadFileContent(DocFileFolder docFileFolder, HttpServletResponse response) {

    }

    @Override
    public boolean copy(List<DocFileFolder> fileFolders, Runnable afterSuccess) {
        return false;
    }
}
