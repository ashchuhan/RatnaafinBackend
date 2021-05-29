package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.SecurityOtpSms;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityOtpSmsDao extends CrudRepository<SecurityOtpSms, Long> {

    @Query("select u from Security_otp_sms u where u.id = ?1")
    SecurityOtpSms findById(long id);

    @Modifying
    @Query("update Security_otp_sms u set u.status = ?2 where u.id = ?1")
    void updateOtpStatus(long id, String status);

}
