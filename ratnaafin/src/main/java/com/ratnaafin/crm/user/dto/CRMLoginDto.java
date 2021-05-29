package com.ratnaafin.crm.user.dto;

public class CRMLoginDto {
    private long user_id;
    private  String mobile;
    private  String e_mail_id;
    private  String flag;
    private  String user_password;
    private  String active;
    private  String tran_dt;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getE_mail_id() {
        return e_mail_id;
    }

    public void setE_mail_id(String e_mail_id) {
        this.e_mail_id = e_mail_id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(String tran_dt) {
        this.tran_dt = tran_dt;
    }
}
