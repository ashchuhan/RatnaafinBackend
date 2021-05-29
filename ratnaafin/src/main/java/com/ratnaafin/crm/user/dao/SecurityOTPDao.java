package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.SecurityOTPHdr;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityOTPDao extends CrudRepository<SecurityOTPHdr ,Long> {

    @Query("select u from Security_otp_hdr u where u.id = ?1")
    SecurityOTPHdr findById(long id);

    @Modifying
    @Query("update Security_otp_hdr u set u.otp_status = ?2 where u.id = ?1")
    void updateOtpStatus(long id, String status);

    @Modifying
    @Query("update Security_otp_hdr u set u.email_status = ?2 where u.id = ?1")
    void updateEmailStatus(long id, String status);

}
