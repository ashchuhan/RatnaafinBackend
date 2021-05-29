package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.CRMLogin;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CRMLoginDao extends CrudRepository<CRMLogin, Long> {
    @Query("select u from Crm_login_mst u where u.mobile = ?1 and u.flag = ?2")
    CRMLogin findBymobileno(String mobile, String flag);

    @Query("select u from Crm_login_mst u where u.e_mail_id = ?1 and u.flag = ?2")
    CRMLogin findByemailid(String email, String flag);
    
    @Modifying
    @Query("update Crm_login_mst u set u.user_password = ?2  where u.mobile = ?1")
    void crmSetLoginPassword(String mobile,String user_password);
}
