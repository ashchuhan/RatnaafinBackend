package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessorOrder;

@Table(name = "crm_lead_mst")
@Entity(name = "Crm_lead_mst")
public class CrmLeadMst {
    @Id
    @Column(name="tran_cd")
    private Long tran_cd;

    @Column(name="comp_cd")
    private String comp_cd;

    @Column(name="branch_cd")
    private  String branch_cd;

    @Column(name="inquiry_tran_cd")
    private Long inquiry_tran_cd;

    @Column(name = "inactive_reason_cd")
    private  Long inactive_reason_cd;

    @Column(name = "priority")
    private String priority;

    @Column(name="source_cd")
    private  String source_cd;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "confirmed")
    private  String confirmed;

    @Column(name = "entered_by")
    private  String entered_by;

    @Column(name = "entered_date")
    private  String entered_date;

    @Column(name = "entered_comp_cd")
    private String entered_comp_cd;

    @Column(name = "entered_branch_cd")
    private  String entered_branch_cd;

    @Column(name = "last_entered_by")
    private String last_entered_by;

    @Column(name = "last_modified_date")
    private  String last_modified_date;

    @Column(name = "machine_nm")
    private  String machine_nm;

    @Column(name = "last_machine_nm")
    private  String last_machine_nm;

    @Column(name = "generation_dt")
    private  String generation_dt;

    @Column(name = "category_id")
    private  String category_id;

    @Column(name = "product_cd")
    private  String product_cd;

    @Column(name = "sub_product1")
    private String sub_product1;

    @Column(name = "sub_product2")
    private  String sub_product2;

    @Column(name = "empl_cd")
    private  String empl_cd;

    @Column(name = "active_flag")
    private String active_flag;

    @Column(name = "loan_amt")
    private float loan_amt;

    @Column(name = "dev_or_cont")
    private  String dev_or_cont;

    @Column(name = "fill_question")
    private  String fill_question;

    @Column(name = "stage_cd")
    private  Long stage_cd;

    @Column(name = "sub_stage_cd")
    private Long sub_stage_cd;

    public Long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(Long tran_cd) {
        this.tran_cd = tran_cd;
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

    public Long getInquiry_tran_cd() {
        return inquiry_tran_cd;
    }

    public void setInquiry_tran_cd(Long inquiry_tran_cd) {
        this.inquiry_tran_cd = inquiry_tran_cd;
    }

    public Long getInactive_reason_cd() {
        return inactive_reason_cd;
    }

    public void setInactive_reason_cd(Long inactive_reason_cd) {
        this.inactive_reason_cd = inactive_reason_cd;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSource_cd() {
        return source_cd;
    }

    public void setSource_cd(String source_cd) {
        this.source_cd = source_cd;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    public String getEntered_date() {
        return entered_date;
    }

    public void setEntered_date(String entered_date) {
        this.entered_date = entered_date;
    }

    public String getEntered_comp_cd() {
        return entered_comp_cd;
    }

    public void setEntered_comp_cd(String entered_comp_cd) {
        this.entered_comp_cd = entered_comp_cd;
    }

    public String getEntered_branch_cd() {
        return entered_branch_cd;
    }

    public void setEntered_branch_cd(String entered_branch_cd) {
        this.entered_branch_cd = entered_branch_cd;
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

    public String getGeneration_dt() {
        return generation_dt;
    }

    public void setGeneration_dt(String generation_dt) {
        this.generation_dt = generation_dt;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getProduct_cd() {
        return product_cd;
    }

    public void setProduct_cd(String product_cd) {
        this.product_cd = product_cd;
    }

    public String getSub_product1() {
        return sub_product1;
    }

    public void setSub_product1(String sub_product1) {
        this.sub_product1 = sub_product1;
    }

    public String getSub_product2() {
        return sub_product2;
    }

    public void setSub_product2(String sub_product2) {
        this.sub_product2 = sub_product2;
    }

    public String getEmpl_cd() {
        return empl_cd;
    }

    public void setEmpl_cd(String empl_cd) {
        this.empl_cd = empl_cd;
    }

    public String getActive_flag() {
        return active_flag;
    }

    public void setActive_flag(String active_flag) {
        this.active_flag = active_flag;
    }

    public float getLoan_amt() {
        return loan_amt;
    }

    public void setLoan_amt(float loan_amt) {
        this.loan_amt = loan_amt;
    }

    public String getDev_or_cont() {
        return dev_or_cont;
    }

    public void setDev_or_cont(String dev_or_cont) {
        this.dev_or_cont = dev_or_cont;
    }

    public String getFill_question() {
        return fill_question;
    }

    public void setFill_question(String fill_question) {
        this.fill_question = fill_question;
    }

    public Long getStage_cd() {
        return stage_cd;
    }

    public void setStage_cd(Long stage_cd) {
        this.stage_cd = stage_cd;
    }

    public Long getSub_stage_cd() {
        return sub_stage_cd;
    }

    public void setSub_stage_cd(Long sub_stage_cd) {
        this.sub_stage_cd = sub_stage_cd;
    }
}
