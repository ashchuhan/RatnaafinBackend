package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Crm_login_mst")
@Table(name = "CRM_LOGIN_MST")
public class CRMLogin {
    @Id
    @Column(name = "user_id", unique=true, nullable=false)
    private long user_id;

    @Column(name = "mobile")
    private  String mobile;

    @Column(name = "e_mail_id")
    private  String e_mail_id;

    @Column(name = "flag")
    private  String flag;

    @Column(name = "user_password")
    private  String user_password;

    @Column(name = "active")
    private  String active;

    @Column(name = "tran_dt")
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
