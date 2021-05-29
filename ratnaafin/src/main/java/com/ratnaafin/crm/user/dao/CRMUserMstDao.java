package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.CRMUserMst;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CRMUserMstDao extends CrudRepository<CRMUserMst, Long> {
    @Query("select u from Crm_users_mst u where u.mobile = ?1")
    CRMUserMst findBymobileno(String mobile);

    @Query("select u from Crm_users_mst u where u.e_mail_id = ?1 and u.flag = ?2")
    CRMUserMst findByemailid(String email, String flag);

    @Modifying
    @Query("update Crm_users_mst u set u.user_password = ?2  where u.mobile = ?1 and u.active = 'Y'")
    void crmSetLoginPassword(String mobile,String user_password);
}
