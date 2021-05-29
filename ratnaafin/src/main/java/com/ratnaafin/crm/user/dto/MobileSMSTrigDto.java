package com.ratnaafin.crm.user.dto;

import com.sun.istack.NotNull;

public class MobileSMSTrigDto {

    @NotNull
    private long id;

    @NotNull
    private String trig_nm;

    private long send_service_cd;
    private String msg_footer;

    @NotNull
    private String actvie;

    private String user_msg_txt;
    private long acct_len;
    private long expiry_sec;
    private long bloc_count;
    private long block_count;
    private String tran_type;
    private String user_email_txt;
    private String customer_car;
    private String email_subject;

    public long getId() { return id;}

    public void setId(long id) {    this.id = id;}

    public String getTrig_nm() { return trig_nm;}

    public void setTrig_nm(String trig_nm) {this.trig_nm = trig_nm;}

    public long getSend_service_cd() {return send_service_cd;}

    public void setSend_service_cd(long send_service_cd) {    this.send_service_cd = send_service_cd;}

    public String getMsg_footer() {    return msg_footer;}

    public void setMsg_footer(String msg_footer) {    this.msg_footer = msg_footer;}

    public String getActvie() {    return actvie;}

    public void setActvie(String actvie) {    this.actvie = actvie;}

    public String getUser_msg_txt() {    return user_msg_txt;}

    public void setUser_msg_txt(String user_msg_txt) {    this.user_msg_txt = user_msg_txt;}

    public long getAcct_len() {    return acct_len;}

    public void setAcct_len(long acct_len) {this.acct_len = acct_len;}

    public long getExpiry_sec() {  return expiry_sec;}

    public void setExpiry_sec(long expiry_sec) {this.expiry_sec = expiry_sec;}

    public long getBloc_count() {    return bloc_count;}

    public void setBloc_count(long bloc_count) {this.bloc_count = bloc_count;}

    public long getBlock_count() {return block_count;}

    public void setBlock_count(long block_count) {  this.block_count = block_count;}

    public String getTran_type() {    return tran_type;}

    public void setTran_type(String tran_type) {    this.tran_type = tran_type;}

    public String getUser_email_txt() {    return user_email_txt;}

    public void setUser_email_txt(String user_email_txt) {    this.user_email_txt = user_email_txt;}

    public String getCustomer_car() {    return customer_car;}

    public void setCustomer_car(String customer_car) {    this.customer_car = customer_car;}

    public String getEmail_subject() {    return email_subject;}

    public void setEmail_subject(String email_subject) {    this.email_subject = email_subject;}
    
}
