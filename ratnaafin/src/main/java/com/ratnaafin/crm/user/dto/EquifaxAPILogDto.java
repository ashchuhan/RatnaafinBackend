package com.ratnaafin.crm.user.dto;

import java.util.Date;

public class EquifaxAPILogDto {
    private String comp_cd;
    private String branch_cd;
    private String tran_cd;
    private String req_data;
    private String res_data;
    private String status;
    private String mobile;
    private long ref_id;
    private String req_status;
    private String error_cd;
    private String error_desc;
    private Date entered_Date;
    private String entered_by;
    private String machine_nm;
    private String last_entered_by;
    private Date last_modified_Date;
    private String last_machine_nm;

    public String getComp_cd() {
        return comp_cd;
    }

    public void setComp_cd(String comp_cd) {
        this.comp_cd = comp_cd;
    }

    public String getBranch_cd() {
        return branch_cd;
    }

    public void setBranch_cd(String branch_cd) {
        this.branch_cd = branch_cd;
    }

    public String getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(String tran_cd) {
        this.tran_cd = tran_cd;
    }

    public String getReq_data() {
        return req_data;
    }

    public void setReq_data(String req_data) {
        this.req_data = req_data;
    }

    public String getRes_data() {
        return res_data;
    }

    public void setRes_data(String res_data) {
        this.res_data = res_data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getRef_id() {
        return ref_id;
    }

    public void setRef_id(long ref_id) {
        this.ref_id = ref_id;
    }

    public String getReq_status() {
        return req_status;
    }

    public void setReq_status(String req_status) {
        this.req_status = req_status;
    }

    public String getError_cd() {
        return error_cd;
    }

    public void setError_cd(String error_cd) {
        this.error_cd = error_cd;
    }

    public String getError_desc() {
        return error_desc;
    }

    public void setError_desc(String error_desc) {
        this.error_desc = error_desc;
    }

    public Date getEntered_Date() {
        return entered_Date;
    }

    public void setEntered_Date(Date entered_Date) {
        this.entered_Date = entered_Date;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    public String getMachine_nm() {
        return machine_nm;
    }

    public void setMachine_nm(String machine_nm) {
        this.machine_nm = machine_nm;
    }

    public String getLast_entered_by() {
        return last_entered_by;
    }

    public void setLast_entered_by(String last_entered_by) {
        this.last_entered_by = last_entered_by;
    }

    public Date getLast_modified_Date() {
        return last_modified_Date;
    }

    public void setLast_modified_Date(Date last_modified_Date) {
        this.last_modified_Date = last_modified_Date;
    }

    public String getLast_machine_nm() {
        return last_machine_nm;
    }

    public void setLast_machine_nm(String last_machine_nm) {
        this.last_machine_nm = last_machine_nm;
    }
}
