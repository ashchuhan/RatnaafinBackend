package com.ratnaafin.crm.user.dto;

import java.sql.Blob;
import java.util.Date;

public class GstInfoDtlDto {
    private String userid;

    private Date tran_dt;

    private long ref_inquiry_id;

    private String transaction_id;

    private String req_status;

    private String url_res;

    private String status;

    private String webhook_res;

    private String webhook_status;

    private Blob zipfile;

    private Blob xlsfile;

    private String json_data;

    private  String request_type;

    private String remarks;


    public Blob getZipfile() {
        return zipfile;
    }

    public void setZipfile(Blob zipfile) {
        this.zipfile = zipfile;
    }

    public Blob getXlsfile() {
        return xlsfile;
    }

    public void setXlsfile(Blob xlsfile) {
        this.xlsfile = xlsfile;
    }

    public String getJson_data() {
        return json_data;
    }

    public void setJson_data(String json_data) {
        this.json_data = json_data;
    }

    private String download_status;

    public String getDownload_status() {
        return download_status;
    }

    public void setDownload_status(String download_status) {
        this.download_status = download_status;
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

    public String getRequest_type() { return request_type; }

    public void setRequest_type(String request_type) { this.request_type = request_type; }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
