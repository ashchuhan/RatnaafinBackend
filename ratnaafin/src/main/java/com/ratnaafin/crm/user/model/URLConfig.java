package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "CRM_MSG_MAIL_URL_CONFIG")
@Entity(name = "Crm_msg_mail_url_config")
public class URLConfig {
    @Id
    @Column(name = "TRAN_CD")
    private long id;

    @Column(name = "APIURL")
    private String url;

    @Column(name = "APIUSERNM")
    private String userid;

    @Column(name = "APIKEY")
    private String key;

    @Column(name = "COUNTRY_CODE")
    private String country_cd;

    @Column(name = "LOCAL_LANGUAGE")
    private String language;

    @Column(name = "ACTIVE")
    private String active;

    @Column(name = "EXPIRY_DT")
    private Date expiry_dt;
    
    @Column(name = "SMTP_SERVER")
    private String smtp_server;

    @Column(name = "SMTP_PORT")
    private String smtp_port;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCountry_cd() {
        return country_cd;
    }

    public void setCountry_cd(String country_cd) {
        this.country_cd = country_cd;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Date getExpiry_dt() {
        return expiry_dt;
    }

    public void setExpiry_dt(Date expiry_dt) {
        this.expiry_dt = expiry_dt;
    }
    public String getSmtp_server() { return smtp_server;}

    public void setSmtp_server(String smtp_server) {this.smtp_server = smtp_server;}

    public String getSmtp_port() {return smtp_port;}

    public void setSmtp_port(String smtp_port) {this.smtp_port = smtp_port;}
}
