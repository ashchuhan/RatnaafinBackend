package com.ratnaafin.crm.user.dto;

import java.sql.Blob;
import java.util.Date;

public class UniqueIDDtlDto {
    private String userid;

    private Date tran_dt;

    private long ref_inquiry_id;

    private String transaction_id;

    private String req_status;

    private String url_res;

    private String status;

    private String webhook_res;

    private String webhook_status;

    private Blob img;

    private Blob xmlfile;

    private String download_status;

    private Long lead_cd;

    private Long sr_cd;

    private String entityType;

    private String initiated_req;

    public String getDownload_status() {
        return download_status;
    }

    public void setDownload_status(String download_status) {
        this.download_status = download_status;
    }

    public Blob getImg() {
        return img;
    }

    public void setImg(Blob img) {
        this.img = img;
    }

    public Blob getXmlfile() {
        return xmlfile;
    }

    public void setXmlfile(Blob xmlfile) {
        this.xmlfile = xmlfile;
    }

    public String getWebhook_status() {
        return webhook_status;
    }

    public void setWebhook_status(String webhook_status) {
        this.webhook_status = webhook_status;
    }

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

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getReq_status() {
        return req_status;
    }

    public void setReq_status(String req_status) {
        this.req_status = req_status;
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

    public String getWebhook_res() {
        return webhook_res;
    }

    public void setWebhook_res(String webhook_res) {
        this.webhook_res = webhook_res;
    }

    public Long getLead_cd() {
        return lead_cd;
    }

    public void setLead_cd(Long lead_cd) {
        this.lead_cd = lead_cd;
    }

    public Long getSr_cd() {
        return sr_cd;
    }

    public void setSr_cd(Long sr_cd) {
        this.sr_cd = sr_cd;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getInitiated_req() {
        return initiated_req;
    }

    public void setInitiated_req(String initiated_req) {
        this.initiated_req = initiated_req;
    }


}
