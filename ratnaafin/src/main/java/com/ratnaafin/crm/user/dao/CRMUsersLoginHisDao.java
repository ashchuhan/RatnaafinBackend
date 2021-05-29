package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.CRMUsersLoginHis;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CRMUsersLoginHisDao extends CrudRepository<CRMUsersLoginHis,Long> {
    @Query("select u from Crm_users_login_history u where u.ref_uid=?1")
    CRMUsersLoginHis findByRefId(String ref_id);

    @Modifying
    @Query("update Crm_users_login_history u set u.otp_verify =?1,u.otp_ver_status=?2,u.otp_ver_res_data=?3 where u.ref_uid=?4")
    void updateResponseStatus(String otpFlag,String otpStatus,String otpResData,String refId);
}

