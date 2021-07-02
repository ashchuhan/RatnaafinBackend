package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.ApiWebhookActivity;
import com.ratnaafin.crm.user.model.OtpVerificationDtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Blob;
import java.util.Date;
import java.util.List;

public interface ApiWebhookActivityDao extends CrudRepository<ApiWebhookActivity, Long> {
    ApiWebhookActivity save(ApiWebhookActivity apiWebhookActivity);

    @Query("select u from api_webhook_activity u where u.process_status = 'P'")
    List<ApiWebhookActivity> findWebhookProcess();

    @Modifying
    @Query("update api_webhook_activity u set " +
             "u.last_modified_date = ?6, " +
             "u.remarks = ?5," +
             "u.webhook_flag = ?4, " +
             "u.process_response = ?3, " +
             "u.process_status = ?2  " +
          "where u.tran_cd = ?1")
    void updateWebhookProcess(Long tranCd,String processStatus,String processResponse,String webhookFlag,String remarks,Date lastModifyDate);
}
