package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.SysParaMst;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysParaMstDao extends CrudRepository<SysParaMst , Long> {
    @Query("select u from Sys_para_mst u where u.id = ?3 and comp_cd =?1 and branch_cd = ?2")
    SysParaMst getParaVal(String comp_cd , String branch_cd , long para_cd);


}
