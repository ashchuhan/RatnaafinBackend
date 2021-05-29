package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.Date;

@Table(name = "CRM_UNIQUEID_REQRES_DTL")
@Entity(name = "Crm_uniqueid_reqres_dtl")
public class UniqueID_dtl {
    @Id
    @Column(name = "USERID")
    private String userid;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "REF_INQUIRY_ID")
    private long ref_inquiry_id;

    @Column(name = "TRANSACTION_ID")
    private String transaction_id;

    @Column(name = "REQ_STATUS")
    private String req_status;

    @Column(name = "URL_RES")
    private String url_res;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "WEBHOOK_RES")
    private String webhook_res;

    @Column(name = "WEBHOOK_STATUS")
    private String webhook_status;

    @Column(name = "IMG")
    private Blob img;

    @Column(name = "XMLFILE")
    private Blob xmlfile;

    @Column(name = "DOWNLOAD_STATUS")
    private String download_status = "P";

    @Column(name = "LEAD_ID")
    private Long lead_id;

    @Column(name = "SR_CD")
    private Long sr_cd;

    @Column(name = "ENTITY_TYPE")
    private String entity_type;

    @Column(name = "INITIATED_REQ")
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

    public Long getLead_id() {
        return lead_id;
    }

    public void setLead_id(Long lead_id) {
        this.lead_id = lead_id;
    }

    public Long getSr_cd() {
        return sr_cd;
    }

    public void setSr_cd(Long sr_cd) {
        this.sr_cd = sr_cd;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public String getInitiated_req() {
        return initiated_req;
    }

    public void setInitiated_req(String initiated_req) {
        this.initiated_req = initiated_req;
    }
}
