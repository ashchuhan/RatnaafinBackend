package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.OtpApiDtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OtpApiDtlDao extends CrudRepository<OtpApiDtl,Long> {

   // OtpApiDtl save(OtpApiDtl otpApiDtl);

    @Modifying
    @Query("update Crm_otp_api_reqres_dtl u set u.otp_verify = ?2 , u.otp_ver_res_data = ?3 where u.ref_uid = ?1")
    void updateOTPVerifyFlag(String transaction_id, String otp_verify,String verifyResponseData);


}
