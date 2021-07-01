package com.ratnaafin.crm.user.dto;

import java.sql.Blob;
import java.util.Date;

public class PerfiosReqResDto {
    private String userid;

    private Date tran_dt;

    private long ref_tran_cd;

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

    private  long ref_sr_cd;

    private String initiated_req;

    private String entity_type;

    private  Long bank_line_id;
    
    private Date last_update_dt;

    private String entered_by;

    private String last_entered_by;

    private Date entered_date;

    private Date last_modified_date;

    private String machine_nm;

    private String last_machine_nm;

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

    public long getRef_tran_cd() {
        return ref_tran_cd;
    }

    public void setRef_tran_cd(long ref_tran_cd) {
        this.ref_tran_cd = ref_tran_cd;
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

    public String getRemarks() { return remarks; }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getRef_sr_cd() {
        return ref_sr_cd;
    }

    public void setRef_sr_cd(long ref_sr_cd) {
        this.ref_sr_cd = ref_sr_cd;
    }

    public String getInitiated_req() {
        return initiated_req;
    }

    public void setInitiated_req(String initiated_req) {
        this.initiated_req = initiated_req;
    }

    public String getEntity_type() { return entity_type; }

    public void setEntity_type(String entity_type) { this.entity_type = entity_type; }

    public Long getBank_line_id() {
        return bank_line_id;
    }

    public void setBank_line_id(Long bank_line_id) {
        this.bank_line_id = bank_line_id;
    }
    
    public Date getLast_update_dt() {
        return last_update_dt;
    }

    public void setLast_update_dt(Date last_update_dt) {
        this.last_update_dt = last_update_dt;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    public String getLast_entered_by() {
        return last_entered_by;
    }

    public void setLast_entered_by(String last_entered_by) {
        this.last_entered_by = last_entered_by;
    }

    public Date getEntered_date() {
        return entered_date;
    }

    public void setEntered_date(Date entered_date) {
        this.entered_date = entered_date;
    }

    public Date getLast_modified_date() {
        return last_modified_date;
    }

    public void setLast_modified_date(Date last_modified_date) {
        this.last_modified_date = last_modified_date;
    }

    public String getMachine_nm() {
        return machine_nm;
    }

    public void setMachine_nm(String machine_nm) {
        this.machine_nm = machine_nm;
    }

    public String getLast_machine_nm() {
        return last_machine_nm;
    }

    public void setLast_machine_nm(String last_machine_nm) {
        this.last_machine_nm = last_machine_nm;
    }
}

