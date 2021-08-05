package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.Date;

@Table(name = "equifax_api_log_new")
@Entity(name = "Equifax_api_log_new")
public class EquifaxAPILog {

    @Id
    @Column(name = "TOKEN_ID")
    private String token_id;

    @Column(name = "REQ_STATUS")
    private String req_status;

    @Column(name = "REQ_DATA")
    private String req_data;

    @Column(name = "RES_DATA")
    private String res_data;

    @Column(name = "STATUS")
    private String status;

    @Column(name ="REQ_TYPE")
    private String req_type;

    @Column(name = "MOBILE")
    private String mobile;

    @Column(name = "INQUIRY_CD")
    private Long inquiry_cd;

    @Column(name = "REF_TRAN_CD")
    private Long ref_tran_cd;

    @Column(name = "REF_SR_CD")
    private Long ref_sr_cd;

    @Column(name = "ENTITY_TYPE")
    private String entity_type;

    @Column(name = "ERROR_CD")
    private String error_cd;

    @Column(name = "ERROR_DESC")
    private String error_desc;

    @Column(name = "ENTERED_BY")
    private String entered_by  = "SYS";

    @Column(name = "ENTERED_DATE")
    private Date entered_date = new Date();

    @Column(name = "MACHINE_NM")
    private String machine_nm = "SYS";

    @Column(name = "LAST_ENTERED_BY")
    private String last_entered_by = "SYS";

    @Column(name = "LAST_MODIFIED_DATE")
    private Date last_modified_date = new Date();

    @Column(name = "LAST_MACHINE_NM")
    private String last_machine_nm = "SYS";

    @Column(name = "REFERENCE_FIELD")
    private String reference_field;

    @Column(name = "OTP_VERIFY")
    private String otp_verify;

    @Column(name = "CONSENT")
    private String consent;

    @Column(name = "TOKEN_EXPIRE_TIME")
    private Date token_expire_time;

    @Column(name = "LINK_SENT_STATUS")
    private String link_sent_status;

    @Column(name = "LINK_SENT_MOBILE")
    private String link_sent_mobile;

    @Column(name = "LINK_SENT_DATE")
    private Date link_sent_date;

    @Column(name = "SHORTED_LINK")
    private String shorted_link;

    @Column(name = "REPORT_DATA")
    private Blob report_data;

    @Column(name = "REMARKS")
    private String remarks;


    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getReq_status() {
        return req_status;
    }

    public void setReq_status(String req_status) {
        this.req_status = req_status;
    }

    public String getReq_data() {
        return req_data;
    }

    public void setReq_data(String req_data) {
        this.req_data = req_data;
    }

    public String getRes_data() {
        return res_data;
    }

    public void setRes_data(String res_data) {
        this.res_data = res_data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReq_type() {
        return req_type;
    }

    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getInquiry_cd() {
        return inquiry_cd;
    }

    public void setInquiry_cd(Long inquiry_cd) {
        this.inquiry_cd = inquiry_cd;
    }

    public Long getRef_tran_cd() {
        return ref_tran_cd;
    }

    public void setRef_tran_cd(Long ref_tran_cd) {
        this.ref_tran_cd = ref_tran_cd;
    }

    public Long getRef_sr_cd() {
        return ref_sr_cd;
    }

    public void setRef_sr_cd(Long ref_sr_cd) {
        this.ref_sr_cd = ref_sr_cd;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public String getError_cd() {
        return error_cd;
    }

    public void setError_cd(String error_cd) {
        this.error_cd = error_cd;
    }

    public String getError_desc() {
        return error_desc;
    }

    public void setError_desc(String error_desc) {
        this.error_desc = error_desc;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    public Date getEntered_date() {
        return entered_date;
    }

    public void setEntered_date(Date entered_date) {
        this.entered_date = entered_date;
    }

    public String getMachine_nm() {
        return machine_nm;
    }

    public void setMachine_nm(String machine_nm) {
        this.machine_nm = machine_nm;
    }

    public String getLast_entered_by() {
        return last_entered_by;
    }

    public void setLast_entered_by(String last_entered_by) {
        this.last_entered_by = last_entered_by;
    }

    public Date getLast_modified_date() {
        return last_modified_date;
    }

    public void setLast_modified_date(Date last_modified_date) {
        this.last_modified_date = last_modified_date;
    }

    public String getLast_machine_nm() {
        return last_machine_nm;
    }

    public void setLast_machine_nm(String last_machine_nm) {
        this.last_machine_nm = last_machine_nm;
    }

    public String getReference_field() {
        return reference_field;
    }

    public void setReference_field(String reference_field) {
        this.reference_field = reference_field;
    }

    public String getOtp_verify() {
        return otp_verify;
    }

    public void setOtp_verify(String otp_verify) {
        this.otp_verify = otp_verify;
    }

    public String getConsent() {
        return consent;
    }

    public void setConsent(String consent) {
        this.consent = consent;
    }

    public Date getToken_expire_time() {
        return token_expire_time;
    }

    public void setToken_expire_time(Date token_expire_time) {
        this.token_expire_time = token_expire_time;
    }

    public String getLink_sent_status() {
        return link_sent_status;
    }

    public void setLink_sent_status(String link_sent_status) {
        this.link_sent_status = link_sent_status;
    }

    public String getLink_sent_mobile() {
        return link_sent_mobile;
    }

    public void setLink_sent_mobile(String link_sent_mobile) {
        this.link_sent_mobile = link_sent_mobile;
    }

    public Date getLink_sent_date() {
        return link_sent_date;
    }

    public void setLink_sent_date(Date link_sent_date) {
        this.link_sent_date = link_sent_date;
    }

    public String getShorted_link() {
        return shorted_link;
    }

    public void setShorted_link(String shorted_link) {
        this.shorted_link = shorted_link;
    }

    public Blob getReport_data() {
        return report_data;
    }

    public void setReport_data(Blob report_data) {
        this.report_data = report_data;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
