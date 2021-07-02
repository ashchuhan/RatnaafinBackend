package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.PerfiosReqResDtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Blob;
import java.util.Date;
import java.util.List;

public interface PerfiosReqResDao extends CrudRepository<PerfiosReqResDtl,Long> {
    @Query("select u from Los_perfios_reqres_dtl u where u.transaction_id = ?1")
    PerfiosReqResDtl findByPerfiosTransactionID(String transactionID);

    @Query("select u from Los_perfios_reqres_dtl u where\n" +
            "u.status = 'I' and\n"+
            "u.request_type <> 'GST_INFO'")
    List<PerfiosReqResDtl> findPendingDocumentProcess();

    @Modifying
    @Query("update Los_perfios_reqres_dtl u set " +
            "u.last_modified_date = ?10, u.remarks = ?9,u.download_status = ?8, u.json_data = ?7, " +
            "u.xlsfile = ?6, u.zipfile=?5, u.webhook_res = ?4, u.status = ?3, " +
            "u.webhook_status = ?2     where u.transaction_id = ?1")
            void updatePerfiosWebhookStatus(String transactionID,
                                            String webhookStatus,
                                            String status,
                                            String webhookRes,
                                            Blob zipFile,
                                            Blob xlsFile,
                                            String jsonFile,
                                            String downloadStatus, String remarks, Date modifyDate);
}

