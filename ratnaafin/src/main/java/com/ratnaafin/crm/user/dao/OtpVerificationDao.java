package com.ratnaafin.crm.user.dao;
import com.ratnaafin.crm.user.model.DocUploadBlobDtl;
import com.ratnaafin.crm.user.model.OtpVerificationDtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OtpVerificationDao extends CrudRepository<OtpVerificationDtl,String>  {
//    OtpVerificationDtl save(OtpVerificationDtl otpVerificationDtl);

    @Query("select u from otp_verification_dtl u where u.token_id = ?1")
    OtpVerificationDtl findOTPDetailByTokenID(String tokenID);

    @Query("select u from otp_verification_dtl u where u.link_sent_status = 'P' and u.req_type <> 'EQFX_SMS'")
    List<OtpVerificationDtl> findPendingOTPLinkDetail();

    @Modifying
    @Query("update otp_verification_dtl u set u.link_sent_status = ?2, u.link_sent_date = ?3, u.remarks = ?4  where u.token_id = ?1")
    void updateOTPLinkSentStatus(String token_id, String status, Date sentDate, String remarks);

}
