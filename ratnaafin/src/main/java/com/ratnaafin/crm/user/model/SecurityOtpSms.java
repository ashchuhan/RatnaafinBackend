package com.ratnaafin.crm.user.model;


import javax.persistence.*;
import java.util.Date;

@Table(name = "Security_otp_sms")
@Entity(name = "Security_otp_sms")

public class SecurityOtpSms {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence", sequenceName = "seq_json_log",initialValue = 1,allocationSize = 1)
    @Column(name = "TRAN_CD")
    private long id;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "OTP_TYPE")
    private String otp_type ;

    @Column(name = "MOBILE_NO")
    private String mobile_no ;

    @Column(name = "SMS_URL")
    private String sms_url ;

    @Column(name = "STATUS")
    private String status ;

    @Column(name = "SENT_TIME")
    private Date sent_time=new Date();

    @Column(name = "API_RESP")
    private String api_reps ;

    @Column(name = "SEND_SERVICE_CD")
    private long send_service_cd;

    @Column(name = "EMAIL_ID")
    private String email_id ;

    @Column(name = "EMAIL_URL")
    private String email_url ;

    @Column(name = "EMAIL_STATUS")
    private String email_status ;

    @Column(name = "EMAIL_SUBJECT")
    private String email_subject ;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getEmail_url() {
        return email_url;
    }

    public void setEmail_url(String email_url) {
        this.email_url = email_url;
    }

    public String getEmail_status() { return email_status;   }

    public void setEmail_status(String email_status) { this.email_status = email_status;    }

    public String getEmail_subject() {return email_subject;    }

    public void setEmail_subject(String email_subject) {this.email_subject = email_subject;    }

}
