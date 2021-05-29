package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.URLConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface URLConfigDao extends CrudRepository<URLConfig,Long> {
    @Query("select u from Crm_msg_mail_url_config u where u.id = ?1 and u.active = ?2")
    URLConfig findURLById(long id,String active);
}
