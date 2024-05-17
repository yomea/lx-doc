package com.laxqnsys.core.doc.ao;

import com.laxqnsys.core.doc.model.vo.DocCollectReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileResVO;
import java.util.List;

/**
 * @author wuzhenhong
 * @date 2024/5/17 15:53
 */
public interface DocCollectFolderAO {

    List<DocFileResVO> getCollectFileList(String name);

    void cancelCollect(DocCollectReqVO reqVO);

    void collect(DocCollectReqVO reqVO);
}
