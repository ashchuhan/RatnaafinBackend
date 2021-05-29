package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.DocUploadBlobDtl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DocUploadBlobDao  extends CrudRepository<DocUploadBlobDtl,Long> {
    DocUploadBlobDtl save(DocUploadBlobDtl docUploadBlobDtl);
    
    @Query("select u from Crm_los_document_lob_dtl u where u.uuid = ?1")
    DocUploadBlobDtl findDocByUUID(String uuid);

    @Query("select u from Crm_los_document_lob_dtl u where u.uuid = ?1 and u.bank_line_id = ?2")
    DocUploadBlobDtl findDocByUUIDBankID(String uuid,Long bankLineID);

}
