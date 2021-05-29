package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.DocUploadDtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DocUploadDtlDao extends CrudRepository<DocUploadDtl,Long> {

    DocUploadDtl save(DocUploadDtl docUploadDtl);

    @Modifying
    @Query("update Crm_los_document_upload_dtl u set u.status = ?3 ,u.remarks = ?4 where u.inquiry_id = ?1 and u.doc_id = ?2")
    int updateDocStatus(Long inquiryId,Long docID,String status,String remarks);

    @Query("select distinct(u.status) from Crm_los_document_upload_dtl u where  u.inquiry_id = ?1 and u.doc_id = ?2")
    String getDocumentStatus(Long inquiryId,Long docID);

    @Query("select count(*) from Crm_los_document_upload_dtl u where  u.inquiry_id = ?1 and u.doc_id = ?2 ")
    int getDocumentCnt(Long inquiryId,Long docID);

    @Modifying
    @Query("delete from Crm_los_document_upload_dtl u where  u.inquiry_id = ?1 and u.doc_id = ?2")
    void deleteDocument(Long inquiryId,Long docID);

    @Modifying
    @Query("delete from Crm_los_document_upload_dtl u where  u.lead_id = ?1 and u.doc_id = ?2")
    void deleteDocumentByLeadID(Long lead_id,Long doc_id);
//
//    @Query("select u from Crm_los_document_upload_dtl u where  u.inquiry_id = ?1 and u.doc_id = ?2 and u.status='Y'")
//    List<DocUploadDtl> getDocListByDocId(long inquiryId, long docID);

    @Query("select u from Crm_los_document_upload_dtl u where  u.lead_id = ?1 and u.doc_id = ?4 and u.sr_cd = ?2 and u.entity_type=?3 and u.status='Y'")
    List<DocUploadDtl> getDocListByDocId(long leadId,long srId, String entityType,long docID);



}
