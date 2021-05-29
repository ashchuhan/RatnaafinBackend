package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.CrmDocumentMst;
import com.ratnaafin.crm.user.model.DocUploadBlobDtl;
import com.ratnaafin.crm.user.model.DocUploadDtl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CrmDocumentMstDao extends CrudRepository<CrmDocumentMst, Long>{
    CrmDocumentMst save(CrmDocumentMst crmDocumentMst);
    @Query("select u from Crm_document_mst u where u.tran_cd = ?1")
    DocUploadBlobDtl findDocMstByID(long tran_cd);

    @Query("select u from Crm_document_mst u where  u.doc_type = ?1")
    List<CrmDocumentMst> getDocMstListByDocType(String docType);
}