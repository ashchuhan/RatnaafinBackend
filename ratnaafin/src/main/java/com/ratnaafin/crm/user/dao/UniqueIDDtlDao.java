package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.UniqueID_dtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Blob;

public interface UniqueIDDtlDao extends CrudRepository<UniqueID_dtl,Long> {
    @Query("select u from Crm_uniqueid_reqres_dtl u where u.transaction_id = ?1")
    UniqueID_dtl findByTransactionID(String transactionID);

    @Modifying
    @Query("update Crm_uniqueid_reqres_dtl u set u.download_status = ?7, u.xmlfile = ?6, u.img = ?5, u.webhook_res = ?4,u.status = ?3,u.webhook_status = ?2 where u.transaction_id = ?1 and u.status = 'P'")
    void updateWebhookStatus(String transactionID, String webhookStatus, String status, String webhookRes, Blob img, Blob xmlfile,String downloadStatus);
}
