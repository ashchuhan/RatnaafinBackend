package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.LeadSanctionDtl;
import com.ratnaafin.crm.user.model.LeadTermSheetDtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Blob;
import java.util.Date;

public interface LeadTermSheetDao extends CrudRepository<LeadTermSheetDtl,Long> {
    @Modifying
    @Query("update los_lead_termsheet_dtl u set u.termsheet_file = ?2, u.last_entered_by = ?3, u.last_modified_date = ?4 where u.tran_cd = ?1")
    void updateTermsheetFile(Long tranCD, byte[] file, String userName, Date modifyDate);

    @Query("select u from los_lead_termsheet_dtl u where u.tran_cd = ?1")
    LeadTermSheetDtl findTermSheetDtlById(Long id);
}
