package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "Security_otp_hdr")
@Entity(name = "Security_otp_hdr")
public class SecurityOTPHdr {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence", sequenceName = "seq_json_log",initialValue = 1,allocationSize = 1)
    @Column(name = "TRAN_CD")
    private long id;

    @Column(name = "SR_CD")
    private long sr_cd;

    @Column(name = "TRAN_TYPE")
    private String tran_type;

    @Column(name = "USER_NAME")
    private String user_name;

    @Column(name = "CONTACT2")
    private String contact2;

    @Column(name = "SENT_DATE")
    private Date send_date = new Date();

    @Column(name = "SENT_MACHINE_NM")
    private String sent_machine_nm;

    @Column(name = "SENT_OTP")
    private String sent_otp;

    @Column(name = "RECV_OTP")
    private String recv_otp;

    @Column(name = "RECV_DATE")
    private Date recv_date = new Date();

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DEVICE_IP")
    private String device_ip;

    @Column(name = "EXPIRY_SEC")
    private long expiry_sec;

    @Column(name = "BLOCK_COUNT")
    private long block_count;

    @Column(name = "BLOCK_HOUR")
    private long block_hour;

    @Column(name = "EMAIL_ID")
    private String email_id;

    @Column(name = "SMS_TRIG_ID")
    private long sms_trig_id;

    @Column(name = "EMAIL_STATUS")
    private String email_status;

    @Column(name = "OTP_STATUS")
    private String otp_status;

    /*
    @Column(name = "FAILED_COUNT")
    private long failed_count;
*/
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSr_cd() {
        return sr_cd;
    }

    public void setSr_cd(long sr_cd) {
        this.sr_cd = sr_cd;
    }

    public String getTran_type() {
        return tran_type;
    }

    public void setTran_type(String tran_type) {
        this.tran_type = tran_type;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getContact2() {
        return contact2;
    }

    public void setContact2(String contact2) {
        this.contact2 = contact2;
    }

    public Date getSend_date() {
        return send_date;
    }

    public void setSend_date(Date send_date) {
        this.send_date = send_date;
    }

    public String getSent_machine_nm() {
        return sent_machine_nm;
    }

    public void setSent_machine_nm(String sent_machine_nm) {
        this.sent_machine_nm = sent_machine_nm;
    }

    public String getSent_otp() {
        return sent_otp;
    }

    public void setSent_otp(String sent_otp) {
        this.sent_otp = sent_otp;
    }

    public String getRecv_otp() {
        return recv_otp;
    }

    public void setRecv_otp(String recv_otp) {
        this.recv_otp = recv_otp;
    }

    public Date getRecv_date() {
        return recv_date;
    }

    public void setRecv_date(Date recv_date) {
        this.recv_date = recv_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDevice_ip() {
        return device_ip;
    }

    public void setDevice_ip(String device_ip) {
        this.device_ip = device_ip;
    }

    public long getExpiry_sec() {
        return expiry_sec;
    }

    public void setExpiry_sec(long expiry_sec) {
        this.expiry_sec = expiry_sec;
    }

    public long getBlock_count() {
        return block_count;
    }

    public void setBlock_count(long block_count) {
        this.block_count = block_count;
    }

    public long getBlock_hour() {
        return block_hour;
    }

    public void setBlock_hour(long block_hour) {
        this.block_hour = block_hour;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public long getSms_trig_id() {
        return sms_trig_id;
    }

    public void setSms_trig_id(long sms_trig_id) {
        this.sms_trig_id = sms_trig_id;
    }

    public String getEmail_status() {
        return email_status;
    }

    public void setEmail_status(String email_status) {
        this.email_status = email_status;
    }

    public String getOtp_status() {
        return otp_status;
    }

    public void setOtp_status(String otp_status) {
        this.otp_status = otp_status;
    }
    /*
    public long getFailed_count() {
        return failed_count;
    }

    public void setFailed_count(long failed_count) {
        this.failed_count = failed_count;
    }*/


}
