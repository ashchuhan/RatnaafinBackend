package com.ratnaafin.crm.user.dto;

import com.sun.istack.NotNull;

import java.util.Date;

public class SecurityOTPHdrDto {

    @NotNull
    private long id;

    @NotNull
    private long sr_cd;

    private String tran_type;
    private String user_name;
    private String contact2;
    private Date send_date = new Date();
    private String sent_machine_nm;
    private String sent_otp;
    private String recv_otp;
    private Date recv_date = new Date();
    private String status;
    private String device_ip;
    private long expiry_sec;
    private long block_count;
    private long block_hour;
    private long failed_count;
    private String email_id;
    private  long  sms_trig_id;
    private String email_status;
    private String otp_status;

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

    public long getFailed_count() {
        return failed_count;
    }

    public void setFailed_count(long failed_count) {
        this.failed_count = failed_count;
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

}
