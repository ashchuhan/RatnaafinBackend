package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "crm_los_document_upload_dtl")
@Entity(name = "Crm_los_document_upload_dtl")
@IdClass(DocuploadDtlId.class)
public class DocUploadDtl {

    @Id
    @Column(name = "INQUIRY_ID")
    private Long inquiry_id;

    @Column(name = "LEAD_ID")
    private Long lead_id;

    @Column(name = "DOC_ID")
    private Long doc_id;

    @Id
    @Column(name = "DOC_UUID")
    private String doc_uuid;

    @Column(name = "DOC_UPLOAD_DT")
    private Date doc_upoload_dt = new Date();

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ENTERED_BY")
    private String entred_by;

    @Column(name = "ENTERED_DT")
    private Date entered_dt = new Date();

    @Column(name = "MACHINE_NM")
    private String machine_nm;

    @Column(name = "LAST_ENTERED_BY")
    private String last_entred_by;

    @Column(name = "LAST_MODIFIED_DT")
    private Date last_modified_dt = new Date();

    @Column(name = "LAST_MACHINE_NM")
    private String last_machine_nm;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "SR_CD")
    private long sr_cd;

    @Column(name = "ENTITY_TYPE")
    private  String entity_type;

    @Column(name = "API_FLAG")
    private String api_flag;

    @Column(name = "DOC_CATEGORY_NM")
    private String doc_category_nm;



    public Long get_inquiry_id() {
        return inquiry_id;
    }

    public void setInquiry_id(Long inquiry_id) {
        this.inquiry_id = inquiry_id;
    }

    public Long getLead_id() {
        return lead_id;
    }

    public void setLead_id(Long lead_id) {
        this.lead_id = lead_id;
    }

    public Long getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Long doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_uuid() {
        return doc_uuid;
    }

    public void setDoc_uuid(String doc_uuid) {
        this.doc_uuid = doc_uuid;
    }

    public Date getDoc_upoload_dt() {
        return doc_upoload_dt;
    }

    public void setDoc_upoload_dt(Date doc_upoload_dt) {
        this.doc_upoload_dt = doc_upoload_dt;
    }

    public Long getInquiry_id() {
        return inquiry_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntred_by() {
        return entred_by;
    }

    public void setEntred_by(String entred_by) {
        this.entred_by = entred_by;
    }

    public Date getEntered_dt() {
        return entered_dt;
    }

    public void setEntered_dt(Date entered_dt) {
        this.entered_dt = entered_dt;
    }

    public String getMachine_nm() {
        return machine_nm;
    }

    public void setMachine_nm(String machine_nm) {
        this.machine_nm = machine_nm;
    }

    public String getLast_entred_by() {
        return last_entred_by;
    }

    public void setLast_entred_by(String last_entred_by) {
        this.last_entred_by = last_entred_by;
    }

    public Date getLast_modified_dt() {
        return last_modified_dt;
    }

    public void setLast_modified_dt(Date last_modified_dt) {
        this.last_modified_dt = last_modified_dt;
    }

    public String getLast_machine_nm() {
        return last_machine_nm;
    }

    public void setLast_machine_nm(String last_machine_nm) {
        this.last_machine_nm = last_machine_nm;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getSr_cd() {
        return sr_cd;
    }

    public void setSr_cd(long sr_cd) {
        this.sr_cd = sr_cd;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public String getApi_flag() {
        return api_flag;
    }

    public void setApi_flag(String api_flag) {
        this.api_flag = api_flag;
    }

    public String getDoc_category_nm() {
        return doc_category_nm;
    }

    public void setDoc_category_nm(String doc_category_nm) {
        this.doc_category_nm = doc_category_nm;
    }
}
