package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "CRM_OTP_API_REQRES_DTL")
@Entity(name = "Crm_otp_api_reqres_dtl")
public class OtpApiDtl {

    @Column(name = "REF_INQUIRY_ID")
    private long ref_inquiry_id;

    @Id
    @Column(name = "REF_UID")
    private String ref_uid;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "TRANSACTION_ID")
    private String transaction_id ;

    @Column(name = "OTP_SENT_STATUS")
    private String otp_sent_status ;

    @Column(name = "OTP_SENT_RES_DATA")
    private String otp_sent_res_data ;

    @Column(name = "OTP_VERIFY")
    private String otp_verify ;

    @Column(name = "OTP_LENGTH")
    private String otp_length;

    @Column(name = "OTP_EXPIRY")
    private String otp_expiry;

    @Column(name = "OTP_VER_STATUS")
    private String otp_ver_status;

    @Column(name = "OTP_VER_RES_DATA")
    private String otp_ver_res_data;

    @Column(name = "EMAIL_OR_MOBILE_FLAG")
    private String email_or_mobile_flag;

    public long getRef_inquiry_id() {
        return ref_inquiry_id;
    }

    public void setRef_inquiry_id(long ref_inquiry_id) {
        this.ref_inquiry_id = ref_inquiry_id;
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

    public String getEmail_or_mobile_flag() {
        return email_or_mobile_flag;
    }

    public void setEmail_or_mobile_flag(String email_or_mobile_flag) {
        this.email_or_mobile_flag = email_or_mobile_flag;
    }
}
