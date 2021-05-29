package com.ratnaafin.crm.user.dto;

import java.util.Date;

public class PancardApiDtlDto {

    private String userid;
    private Date tran_dt = new Date();
    private long ref_inquiry_id;
    private String pancard_no;
    private String url_res;
    private String status;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public long getRef_inquiry_id() {
        return ref_inquiry_id;
    }

    public void setRef_inquiry_id(long ref_inquiry_id) {
        this.ref_inquiry_id = ref_inquiry_id;
    }

    public String getPancard_no() {
        return pancard_no;
    }

    public void setPancard_no(String pancard_no) {
        this.pancard_no = pancard_no;
    }

    public String getUrl_res() {
        return url_res;
    }

    public void setUrl_res(String url_res) {
        this.url_res = url_res;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
