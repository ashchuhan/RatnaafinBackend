package com.ratnaafin.crm.user.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.ratnaafin.crm.user.model.CrmMiscMst;

import java.util.List;

public interface CrmMiscMstDao extends CrudRepository<CrmMiscMst,String> {
    @Query("select u from Crm_misc_mst u where u.category_cd = ?1")
    List<CrmMiscMst> findByCategory(String categoryCode);
}
