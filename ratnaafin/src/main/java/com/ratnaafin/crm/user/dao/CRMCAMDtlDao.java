package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.CRMCAMDtl;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Blob;
import java.util.Date;

public interface CRMCAMDtlDao extends CrudRepository<CRMCAMDtl,Long> {
    @Query("select u from Crm_lead_cam_dtl u where u.tran_cd = ?1 and u.sr_cd = ?2")
    CRMCAMDtl getCRMCAMData(long leadID,long serialNo);

    @Modifying
    @Query("update Crm_lead_cam_dtl u set u.last_machine_nm='SERVER',u.last_entered_by = ?6,u.status = ?5, u.last_modified_date = ?4, u.cam_data = ?3 where u.tran_cd = ?2 and u.sr_cd = ?1")
    void updateCAMStatus(long serialNo, long leadID, Blob camData, Date modifiedDate, String Status,String enteredBy);
}
