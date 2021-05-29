package com.ratnaafin.crm.user.dto;

import java.util.Date;

public class CRMUserMstDto {
    private Long user_id;

    private Date tran_dt;

    private String salutation;

    private String first_name;

    private String middle_name;

    private String last_name;

    private String gender;

    private String birth_dt;

    private String married_flag;

    private  String mobile;

    private  String e_mail_id;

    private String location;

    private String city;

    private String state;

    private String postal_cd;

    private String country;

    private  String flag;

    private  String user_password;

    private  String active;

    private Date last_login_dt;
    
    private String comp_cd;
    
    private String branch_cd;

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

	public Date getLast_login_dt() {
        return last_login_dt;
    }

    public void setLast_login_dt(Date last_login_dt) {
        this.last_login_dt = last_login_dt;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
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

    public String getMarried_flag() {
        return married_flag;
    }

    public void setMarried_flag(String married_flag) {
        this.married_flag = married_flag;
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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
