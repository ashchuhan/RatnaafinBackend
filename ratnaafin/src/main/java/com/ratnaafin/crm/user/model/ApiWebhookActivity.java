package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "API_WEBHOOK_ACTIVITY")
@Entity(name = "api_webhook_activity")
public class ApiWebhookActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence", sequenceName = "api_webhook_activity_seq",initialValue = 1,allocationSize = 1)
    @Column(name = "tran_cd")
    private Long tran_cd;

    @Column(name = "tran_dt")
    private Date tran_dt = new Date();

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "webhook_nm")
    private String webhook_nm;

    @Column(name = "ref_trx_id")
    private String ref_trx_id;

    @Column(name = "webhook_data")
    private String webhook_data;

    @Column(name = "webhook_flag")
    private String webhook_flag;

    @Column(name = "process_status")
    private String process_status;

    @Column(name = "process_response")
    private String process_response;

    @Column(name = "last_modified_date")
    private Date last_modified_date = new Date();

    @Column(name = "remarks")
    private String remarks;


    public Long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(Long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getWebhook_nm() {
        return webhook_nm;
    }

    public void setWebhook_nm(String webhook_nm) {
        this.webhook_nm = webhook_nm;
    }

    public String getRef_trx_id() {
        return ref_trx_id;
    }

    public void setRef_trx_id(String ref_trx_id) {
        this.ref_trx_id = ref_trx_id;
    }

    public String getWebhook_data() {
        return webhook_data;
    }

    public void setWebhook_data(String webhook_data) {
        this.webhook_data = webhook_data;
    }

    public String getWebhook_flag() {
        return webhook_flag;
    }

    public void setWebhook_flag(String webhook_flag) {
        this.webhook_flag = webhook_flag;
    }

    public String getProcess_status() {
        return process_status;
    }

    public void setProcess_status(String process_status) {
        this.process_status = process_status;
    }

    public String getProcess_response() {
        return process_response;
    }

    public void setProcess_response(String process_response) {
        this.process_response = process_response;
    }

    public Date getLast_modified_date() {
        return last_modified_date;
    }

    public void setLast_modified_date(Date last_modified_date) {
        this.last_modified_date = last_modified_date;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
