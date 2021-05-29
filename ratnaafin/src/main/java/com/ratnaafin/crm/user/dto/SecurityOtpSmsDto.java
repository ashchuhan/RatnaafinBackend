package com.ratnaafin.crm.user.dto;

import com.sun.istack.NotNull;

import java.util.Date;

public class SecurityOtpSmsDto {
    @NotNull
    private long tran_cd;

    @NotNull
    private Date tran_dt=new Date();

    @NotNull
    private String otp_type ;
    private String mobile_no ;
    private String sms_url ;
    private String status ;
    private Date sent_time=new Date();
    private String api_reps ;
    private long send_service_cd;
    private String email_id ;
    private String email_url ;
    private String email_status ;
    private String email_subject ;

    public long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public String getOtp_type() {
        return otp_type;
    }

    public void setOtp_type(String otp_type) {
        this.otp_type = otp_type;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getSms_url() {
        return sms_url;
    }

    public void setSms_url(String sms_url) {
        this.sms_url = sms_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSent_time() {
        return sent_time;
    }

    public void setSent_time(Date sent_time) {
        this.sent_time = sent_time;
    }

    public String getApi_reps() {
        return api_reps;
    }

    public void setApi_reps(String api_reps) {
        this.api_reps = api_reps;
    }

    public long getSend_service_cd() {
        return send_service_cd;
    }

    public void setSend_service_cd(long send_service_cd) {
        this.send_service_cd = send_service_cd;
    }

    public String getEmail_id() { return email_id;  }

    public void setEmail_id(String email_id) {this.email_id = email_id;  }

    public String getEmail_url() {return email_url; }

    public void setEmail_url(String email_url) { this.email_url = email_url;}

    public String getEmail_status() {return email_status;}

    public void setEmail_status(String email_status) {this.email_status = email_status;}

    public String getEmail_subject() {return email_subject;}

    public void setEmail_subject(String email_subject) {this.email_subject = email_subject;}

}
