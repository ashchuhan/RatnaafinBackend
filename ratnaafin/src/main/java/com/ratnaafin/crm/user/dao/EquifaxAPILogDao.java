package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.EquifaxAPILog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Blob;
import java.util.Date;
import java.util.List;

public interface EquifaxAPILogDao extends CrudRepository<EquifaxAPILog,String> {
    @Query("select u from Equifax_api_log_new u where u.link_sent_status='P' and u.otp_verify = 'N'")
    List<EquifaxAPILog> findEquifaxPendingLinkRecord();

    @Query("select u from Equifax_api_log_new u where u.token_id = ?1")
    EquifaxAPILog findEquifaxDetailByTokenId(String tokenID);

    @Modifying
    @Query("update Equifax_api_log_new u set u.error_desc = ?7, u.error_cd=?6, u.last_modified_date = ?5, u.res_data = ?4, u.status=?3, u.req_status=?2 where u.token_id = ?1" )
    void updateEquifaxAPILog(String token_id,String req_status,String res_status, String res_data, Date modifyDate,String errorCode, String errorDesc);

    @Modifying
    @Query("update Equifax_api_log_new u set u.status = ?2, u.link_sent_status = ?3, u.error_desc = ?4, u.shorted_link = ?5, u.remarks = ?6,u.link_sent_date = ?7 where u.token_id = ?1" )
    void updateEqfxOTPLinkStatus(String tokenID, String status,String linkStatus, String errorDesc,String shortedURL,String remarks,Date sentDate);

    @Modifying
    @Query("update Equifax_api_log_new u set u.report_data = ?1, u.last_modified_date = ?2 where u.token_id = ?3")
    void updateEquifaxReport(Blob reportData, Date modifyDate, String tokenID);

    @Modifying
    @Query("delete from Equifax_api_log_new u where u.token_id = ?1")
    void deleteEquifaxDetailByTokenId(String tokenID);

}
