package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.CrmLeadMst;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrmLeadMstDao extends CrudRepository<CrmLeadMst,Long> {
    @Query("select u from Crm_lead_mst u where u.tran_cd = ?1 and u.active_flag = 'Y'")
    CrmLeadMst findLeadByID(long tran_cd);
}
/*
public interface CrmDocumentMstDao extends CrudRepository<CrmDocumentMst, Long>{
    CrmDocumentMst save(CrmDocumentMst crmDocumentMst);
    @Query("select u from Crm_document_mst u where u.tran_cd = ?1")
    DocUploadBlobDtl findDocMstByID(long tran_cd);

    @Query("select u from Crm_document_mst u where  u.doc_type = ?1")
    List<CrmDocumentMst> getDocMstListByDocType(String docType);
}
* */