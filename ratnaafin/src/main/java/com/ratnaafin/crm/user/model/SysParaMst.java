package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "Sys_para_mst")
@Entity(name = "Sys_para_mst")
public class SysParaMst {

    @Id
    @Column(name = "PARA_CD")
    private long id;

    @Column(name = "comp_cd")
    String comp_cd;

    @Column(name = "branch_cd")
    String branch_cd;

    @Column(name = "PARA_NM")
    private String para_name;

    @Column(name = "DATATYPE_CD")
    private String datatype_cd;

    @Column(name = "PARA_VALUE")
    private String para_value;

    @Column(name = "DOC_CD")
    private String doc_cd;

    @Column(name = "ENTERED_BY")
    private String entered_by ;

    @Column(name = "LAST_ENTERED_BY")
    private String last_entered_by;

    @Column(name = "ENTERED_DATE")
    private Date entered_date = new Date();

    @Column(name = "LAST_MODIFIED_DATE")
    private Date last_modified_date = new Date();

    @Column(name = "MACHINE_NM")
    private String machine_nm;

    @Column(name = "LAST_MACHINE_NM")
    private String last_machine_nm;

    @Column(name = "CONFIRMED")
    private String confirmed ;

    @Column(name = "VERIFIED_BY")
    private String verified_by;

    @Column(name = "VERIFIED_DATE")
    private Date verified_date = new Date();

    @Column(name = "VERIFIED_MACHINE_NM")
    private String verified_machine_nm;

    @Column(name = "OLD_VALUE")
    private String old_value ;

    @Column(name = "OLD_DATATYPE_CD")
    private String old_datatype;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComp_cd() {
        return comp_cd;
    }

    public void setComp_cd(String comp_cd) {
        this.comp_cd = comp_cd;
    }

    public String getBranch_cd() {
        return branch_cd;
    }

    public void setBranch_cd(String branch_cd) {
        this.branch_cd = branch_cd;
    }

    public String getPara_name() {
        return para_name;
    }

    public void setPara_name(String para_name) {
        this.para_name = para_name;
    }

    public String getDatatype_cd() {
        return datatype_cd;
    }

    public void setDatatype_cd(String datatype_cd) {
        this.datatype_cd = datatype_cd;
    }

    public String getPara_value() {
        return para_value;
    }

    public void setPara_value(String para_value) {
        this.para_value = para_value;
    }

    public String getDoc_cd() {
        return doc_cd;
    }

    public void setDoc_cd(String doc_cd) {
        this.doc_cd = doc_cd;
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

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getVerified_by() {
        return verified_by;
    }

    public void setVerified_by(String verified_by) {
        this.verified_by = verified_by;
    }

    public Date getVerified_date() {
        return verified_date;
    }

    public void setVerified_date(Date verified_date) {
        this.verified_date = verified_date;
    }

    public String getVerified_machine_nm() {
        return verified_machine_nm;
    }

    public void setVerified_machine_nm(String verified_machine_nm) {
        this.verified_machine_nm = verified_machine_nm;
    }

    public String getOld_value() {
        return old_value;
    }

    public void setOld_value(String old_value) {
        this.old_value = old_value;
    }

    public String getOld_datatype() {
        return old_datatype;
    }

    public void setOld_datatype(String old_datatype) {
        this.old_datatype = old_datatype;
    }
}
