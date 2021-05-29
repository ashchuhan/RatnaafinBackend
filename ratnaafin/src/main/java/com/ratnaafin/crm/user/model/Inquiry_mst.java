package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "CRM_INQUIRY_MST")
@Entity(name = "Crm_inquiry_mst")
public class Inquiry_mst {
    @Id
    @Column(name = "TRAN_CD")
    private long tran_cd;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "CUSTOMER_TYPE")
    private String customer_type;

    @Column(name = "SALUTATION")
    private String salutation;

    @Column(name = "FIRST_NAME")
    private String first_name;

    @Column(name = "MIDDLE_NAME")
    private String middle_name;

    @Column(name = "LAST_NAME")
    private String last_name;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "BIRTH_DT")
    private String birth_dt;

    @Column(name = "MOBILE")
    private String mobile;

    @Column(name = "E_MAIL_ID")
    private String e_mail_id;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STATE")
    private String state;

    @Column(name = "POSTAL_CD")
    private String postal_cd;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "DESIRE_LOAN_AMT")
    private float desire_loan_amt;

    @Column(name = "SUB_PRODUCT1")
    private String sub_product;

    @Column(name = "NEXT_ACTION_DT")
    private Date next_action_dt;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CONFIRMED")
    private String confirmed;

    @Column(name = "DEV_OR_CONT")
    private String dev_or_cont;

    @Column(name = "ENTERED_BY")
    private String entered_by;

    @Column(name = "ENTERED_DATE")
    private Date entered_date;

    @Column(name = "LAST_ENTERED_BY")
    private String last_entered_by;

    @Column(name = "LAST_MODIFIED_DATE")
    private Date last_modified_date;

    @Column(name = "MACHINE_NM")
    private String machine_nm;

    @Column(name = "LAST_MACHINE_NM")
    private String last_machine_nm;

    @Column(name = "LANDMARK")
    private String landmark;

    @Column(name = "DISTRICT")
    private String district;

    @Column(name = "LEAD_GENERATE")
    private String lead_generate;

    @Column(name = "FILL_QUE")
    private String fill_que;

    @Column(name = "TEAM_LEAD_ID")
    private Long team_lead_id;

    @Column(name = "TEAM_MEMBER_ID")
    private Long team_member_id;

    @Column(name = "PRIORITY")
    private String priority;

    public long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public String getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(String customer_type) {
        this.customer_type = customer_type;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth_dt() {
        return birth_dt;
    }

    public void setBirth_dt(String birth_dt) {
        this.birth_dt = birth_dt;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getE_mail_id() {
        return e_mail_id;
    }

    public void setE_mail_id(String e_mail_id) {
        this.e_mail_id = e_mail_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal_cd() {
        return postal_cd;
    }

    public void setPostal_cd(String postal_cd) {
        this.postal_cd = postal_cd;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getDesire_loan_amt() {
        return desire_loan_amt;
    }

    public void setDesire_loan_amt(float desire_loan_amt) {
        this.desire_loan_amt = desire_loan_amt;
    }

    public String getSub_product() {
        return sub_product;
    }

    public void setSub_product(String sub_product) {
        this.sub_product = sub_product;
    }

    public Date getNext_action_dt() {
        return next_action_dt;
    }

    public void setNext_action_dt(Date next_action_dt) {
        this.next_action_dt = next_action_dt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getDev_or_cont() {
        return dev_or_cont;
    }

    public void setDev_or_cont(String dev_or_cont) {
        this.dev_or_cont = dev_or_cont;
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

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLead_generate() {
        return lead_generate;
    }

    public void setLead_generate(String lead_generate) {
        this.lead_generate = lead_generate;
    }

    public String getFill_que() {
        return fill_que;
    }

    public void setFill_que(String fill_que) {
        this.fill_que = fill_que;
    }

    public Long getTeam_lead_id() {
        return team_lead_id;
    }

    public void setTeam_lead_id(Long team_lead_id) {
        this.team_lead_id = team_lead_id;
    }

    public Long getTeam_member_id() {
        return team_member_id;
    }

    public void setTeam_member_id(Long team_member_id) {
        this.team_member_id = team_member_id;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
