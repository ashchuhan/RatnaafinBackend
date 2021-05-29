package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.Date;
/*SELECT USERID, TRAN_DT, REF_INQUIRY_ID, TRANSACTION_ID, REQ_STATUS,
  URL_RES, STATUS, WEBHOOK_RES, WEBHOOK_STATUS, ZIPFILE, XLSFILE,
  JSON_DATA, DOWNLOAD_STATUS
FROM CRM_GSTINFO_REQRES_DTL

*/
@Table(name = "LOS_PERFIOS_REQRES_DTL")
@Entity(name = "Los_perfios_reqres_dtl")
public class PerfiosReqResDtl {
    @Id
    @Column(name = "USERID")
    private String userid;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "REF_TRAN_CD")
    private long ref_tran_cd;

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

    @Column(name = "ZIPFILE")
    private Blob zipfile;

    @Column(name = "XLSFILE")
    private Blob xlsfile;

    @Column(name = "JSON_DATA")
    private String json_data;

    @Column(name = "DOWNLOAD_STATUS")
    private String download_status = "P";

    @Column(name = "REQUEST_TYPE")
    private  String request_type;

    @Column(name = "REMARKS")
    private  String remarks;

    @Column(name = "REF_SR_CD")
    private  Long ref_sr_cd;

    @Column(name = "INITIATED_REQ")
    private  String initiated_req;

    @Column(name = "ENTITY_TYPE")
    private String entity_type;

    @Column(name = "BANK_LINE_ID")
    private  Long bank_line_id;
    
    @Column(name = "LAST_UPDATE_DT")
    private  Date last_update_dt = new Date();
    
    public String getDownload_status() {
        return download_status;
    }

    public void setDownload_status(String download_status) {
        this.download_status = download_status;
    }

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

    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Long getRef_sr_cd() { return ref_sr_cd;  }

    public void setRef_sr_cd(Long ref_sr_cd) { this.ref_sr_cd = ref_sr_cd;}

    public String getInitiated_req() {
        return initiated_req;
    }

    public void setInitiated_req(String initiated_req) {
        this.initiated_req = initiated_req;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

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
}
