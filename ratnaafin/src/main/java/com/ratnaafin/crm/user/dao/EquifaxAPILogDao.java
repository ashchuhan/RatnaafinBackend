package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.DocUploadDtl;
import com.ratnaafin.crm.user.model.EquifaxAPILog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface EquifaxAPILogDao extends CrudRepository<EquifaxAPILog,String> {
    @Query("select u from Equifax_api_log_new u where u.status='I' and u.otp_verify='S' and u.consent='Y'")
    List<EquifaxAPILog> getEquifaxPendingRecords();

    @Query("select u from Equifax_api_log_new u where u.token_id = ?1")
    EquifaxAPILog findEquifaxDetailByTokenId(String tokenID);

    @Modifying
    @Query("update Equifax_api_log_new u set u.error_desc = ?7, u.error_cd=?6, u.last_modified_date = ?5, u.res_data = ?4, u.status=?3, u.req_status=?2 where u.token_id = ?1" )
    void updateEquifaxAPILog(String token_id,String req_status,String res_status, String res_data, Date modifyDate,String errorCode, String errorDesc);

    @Modifying
    @Query("update Equifax_api_log_new u set u.link_sent_status = ?2, u.link_sent_date=?3 where u.token_id = ?1" )
    void equifaxOTPLinkSentStatus(String token_id, String status, Date sentDate);
}
