package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "Crm_document_mst")
@Entity(name = "Crm_document_mst")
public class CrmDocumentMst {
    @Id
    @Column(name="tran_cd")
    private long tran_cd;
    @Column(name="doc_description")
    private  String doc_desc;
    @Column(name="doc_title")
    private String doc_title;
    @Column(name = "doc_type")
    private String doc_type;
    @Column(name="entered_by")
    private String entered_by;
    @Column(name="entered_dt")
    private Date entered_dt;
    @Column(name = "machine_nm")
    private String machine_nm;
    @Column(name="last_entered_by")
    private String last_entered_by;
    @Column(name = "last_modified_dt")
    private Date last_modified_dt;
    @Column(name = "last_machine_nm")
    private String last_machine_nm;
    @Column(name = "entity_type")
    private String enity_type;
    @Column(name = "api_flag")
    private String api_flag;

    public long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public String getDoc_desc() {
        return doc_desc;
    }

    public void setDoc_desc(String doc_desc) {
        this.doc_desc = doc_desc;
    }

    public String getDoc_title() {
        return doc_title;
    }

    public void setDoc_title(String doc_title) {
        this.doc_title = doc_title;
    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String doc_type) {
        this.doc_type = doc_type;
    }

    public Date getEntered_dt() {
        return entered_dt;
    }

    public void setEntered_dt(Date entered_dt) {
        this.entered_dt = entered_dt;
    }

    public String getEnity_type() {
        return enity_type;
    }

    public void setEnity_type(String enity_type) {
        this.enity_type = enity_type;
    }

    public String getApi_flag() {
        return api_flag;
    }

    public void setApi_flag(String api_flag) {
        this.api_flag = api_flag;
    }


}
