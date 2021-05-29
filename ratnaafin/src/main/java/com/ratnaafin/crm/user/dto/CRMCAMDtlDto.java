package com.ratnaafin.crm.user.dto;

import java.sql.Blob;
import java.util.Date;

public class CRMCAMDtlDto {
    private long tran_cd;
    private long sr_cd;
    private String status;
    private Blob cam_data;
    private Date last_modified_date;

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
}
