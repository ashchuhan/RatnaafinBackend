package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.Date;

@Table(name = "CRM_LEAD_CAM_DTL")
@Entity(name = "Crm_lead_cam_dtl")
public class CRMCAMDtl {
    @Id
    @Column(name = "TRAN_CD")
    private long tran_cd;

    @Column(name = "SR_CD")
    private long sr_cd;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CAM_DATA")
    private Blob cam_data;

    @Column(name = "LAST_MODIFIED_DATE")
    private Date last_modified_date;
    
    @Column(name = "LAST_ENTERED_BY")
    private String last_entered_by;

    @Column(name = "LAST_MACHINE_NM")
    private String last_machine_nm;

    @Column(name = "REMARKS")
    private String remarks;

    public String getLast_entered_by() {
        return last_entered_by;
    }

    public void setLast_entered_by(String last_entered_by) {
        this.last_entered_by = last_entered_by;
    }

    public String getLast_machine_nm() {
        return last_machine_nm;
    }

    public long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public long getSr_cd() {
        return sr_cd;
    }

    public void setSr_cd(long sr_cd) {
        this.sr_cd = sr_cd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Blob getCam_data() {
        return cam_data;
    }

    public void setCam_data(Blob cam_data) {
        this.cam_data = cam_data;
    }

    public Date getLast_modified_date() {
        return last_modified_date;
    }

    public void setLast_modified_date(Date last_modified_date) {
        this.last_modified_date = last_modified_date;
    }

    public void setLast_machine_nm(String last_machine_nm) {
        this.last_machine_nm = last_machine_nm;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
