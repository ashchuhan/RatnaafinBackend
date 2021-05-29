package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.CRMApp_mst;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CRMAppDao extends CrudRepository<CRMApp_mst,Long> {
    @Query("select u from Crm_app_base_mst u where u.tran_cd = ?1")
    CRMApp_mst findAppByID(long id);
}
