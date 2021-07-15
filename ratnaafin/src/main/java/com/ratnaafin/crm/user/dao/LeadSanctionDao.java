package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.LeadSanctionDtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface LeadSanctionDao extends CrudRepository<LeadSanctionDtl,Long> {
    @Modifying
    @Query("update los_lead_sanction_dtl u set u.sanction_file = ?2, u.last_entered_by = ?3, u.last_modified_date = ?4 where u.tran_cd = ?1")
    void updateSanctionFile(Long tran_cd, byte[] file, String userName, Date modifyDate);
}
