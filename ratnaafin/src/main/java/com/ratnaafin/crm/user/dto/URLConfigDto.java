package com.ratnaafin.crm.user.dto;

import java.util.Date;

public class URLConfigDto {
    private long id;
    private String url;
    private String userid;
    private String key;
    private String country_cd;
    private String language;
    private String active;
    private Date expiry_dt;
    private String smtp_server;
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
    public String getSmtp_server() {return smtp_server;  }

    public void setSmtp_server(String smtp_server) {this.smtp_server = smtp_server; }

    public String getSmtp_port() { return smtp_port;  }

    public void setSmtp_port(String smtp_port) { this.smtp_port = smtp_port;    }
}
