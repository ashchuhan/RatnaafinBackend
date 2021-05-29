package com.ratnaafin.crm.user.model;
import javax.persistence.*;
import java.util.Date;

@Table(name = "otp_verification_dtl")
@Entity(name = "otp_verification_dtl")
public class OtpVerificationDtl {
    @Id
    @Column(name = "token_id")
    private  String token_id;

    @Column(name = "tran_dt")
    private String tran_dt;

    @Column(name = "inquiry_tran_cd")
    private Long inquiry_tran_cd;

    @Column(name = "ref_tran_cd")
    private Long ref_tran_cd;

    @Column(name = "ref_sr_cd")
    private Long ref_sr_cd;

    @Column(name = "entity_type")
    private String entity_type;

    @Column(name = "link_sent_status")
    private String link_sent_status;

    @Column(name = "req_type")
    private String req_type;

    @Column(name = "otp_sent_mobile")
    private String otp_sent_mobile;

    @Column(name = "otp_sent_email")
    private String otp_sent_email;

    @Column(name = "verify")
    private String verify;

    @Column(name = "expire_time")
    private Date expire_time;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "entered_by")
    private String entered_by;

    @Column(name = "entered_date")
    private Date entered_date = new Date();

    @Column(name = "last_entered_by")
    private String last_entered_by;

    @Column(name = "last_modified_date")
    private String last_modified_date;

    @Column(name = "machine_nm")
    private String machine_nm;

    @Column(name = "last_machine_nm")
    private String last_machine_nm;

    @Column(name = "link_sent_date")
    private Date link_sent_date;

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(String tran_dt) {
        this.tran_dt = tran_dt;
    }

    public Long getInquiry_tran_cd() {
        return inquiry_tran_cd;
    }

    public void setInquiry_tran_cd(Long inquiry_tran_cd) {
        this.inquiry_tran_cd = inquiry_tran_cd;
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

    public String getLink_sent_status() {
        return link_sent_status;
    }

    public void setLink_sent_status(String link_sent_status) {
        this.link_sent_status = link_sent_status;
    }

    public String getReq_type() {
        return req_type;
    }

    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }

    public String getOtp_sent_mobile() {
        return otp_sent_mobile;
    }

    public void setOtp_sent_mobile(String otp_sent_mobile) {
        this.otp_sent_mobile = otp_sent_mobile;
    }

    public String getOtp_sent_email() {
        return otp_sent_email;
    }

    public void setOtp_sent_email(String otp_sent_email) {
        this.otp_sent_email = otp_sent_email;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public Date getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(Date expire_time) {
        this.expire_time = expire_time;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getLast_entered_by() {
        return last_entered_by;
    }

    public void setLast_entered_by(String last_entered_by) {
        this.last_entered_by = last_entered_by;
    }

    public String getLast_modified_date() {
        return last_modified_date;
    }

    public void setLast_modified_date(String last_modified_date) {
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

    public Date getlink_sent_date() {
        return link_sent_date;
    }

    public void setlink_sent_date(Date link_sent_date) {
        this.link_sent_date = link_sent_date;
    }
}
