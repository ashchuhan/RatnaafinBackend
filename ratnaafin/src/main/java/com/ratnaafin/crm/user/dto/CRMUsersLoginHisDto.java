package com.ratnaafin.crm.user.dto;

import com.ratnaafin.crm.user.model.CRMUserMst;

import java.util.Date;

public class CRMUsersLoginHisDto {
    private Long user_id;

    private CRMUserMst crmUserMst;

    private String ref_uid;

    private Date tran_dt = new Date();

    private String transaction_id;

    private String otp_sent_status;

    private String otp_sent_res_data;

    private String otp_verify;

    private String otp_length;

    private String otp_expiry;

    private String otp_ver_status;

    private String otp_ver_res_data;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public CRMUserMst getCrmUserMst() {
        return crmUserMst;
    }

    public void setCrmUserMst(CRMUserMst crmUserMst) {
        this.crmUserMst = crmUserMst;
    }

    public String getRef_uid() {
        return ref_uid;
    }

    public void setRef_uid(String ref_uid) {
        this.ref_uid = ref_uid;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOtp_sent_status() {
        return otp_sent_status;
    }

    public void setOtp_sent_status(String otp_sent_status) {
        this.otp_sent_status = otp_sent_status;
    }

    public String getOtp_sent_res_data() {
        return otp_sent_res_data;
    }

    public void setOtp_sent_res_data(String otp_sent_res_data) {
        this.otp_sent_res_data = otp_sent_res_data;
    }

    public String getOtp_verify() {
        return otp_verify;
    }

    public void setOtp_verify(String otp_verify) {
        this.otp_verify = otp_verify;
    }

    public String getOtp_length() {
        return otp_length;
    }

    public void setOtp_length(String otp_length) {
        this.otp_length = otp_length;
    }

    public String getOtp_expiry() {
        return otp_expiry;
    }

    public void setOtp_expiry(String otp_expiry) {
        this.otp_expiry = otp_expiry;
    }

    public String getOtp_ver_status() {
        return otp_ver_status;
    }

    public void setOtp_ver_status(String otp_ver_status) {
        this.otp_ver_status = otp_ver_status;
    }

    public String getOtp_ver_res_data() {
        return otp_ver_res_data;
    }

    public void setOtp_ver_res_data(String otp_ver_res_data) {
        this.otp_ver_res_data = otp_ver_res_data;
    }
}
