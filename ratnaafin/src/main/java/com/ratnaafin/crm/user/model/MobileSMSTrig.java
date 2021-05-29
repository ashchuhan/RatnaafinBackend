package com.ratnaafin.crm.user.model;


import javax.persistence.*;

@Table(name = "Mobile_sms_trig_mst")
@Entity(name = "Mobile_sms_trig_mst")
public class MobileSMSTrig{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence", sequenceName = "seq_json_log",initialValue = 1,allocationSize = 1)
    @Column(name = "TRIG_CD")
    private long id;

    @Column(name = "TRIG_NM")
    private String trig_nm;

    @Column(name = "SEND_SERVICE_CD")
    private long send_service_cd;

    @Column(name = "MSG_FOOTER")
    private String msg_footer;

    @Column(name = "ACTIVE")
    private String actvie;

    @Column(name = "USER_MSG_TXT")
    private String user_msg_txt;

    @Column(name = "ACCT_LEN")
    private long acct_len;

    @Column(name = "EXPIRY_SEC")
    private long expiry_sec;

    @Column(name = "BLOCK_COUNT")
    private long block_count;

    @Column(name = "BLOCK_HOUR")
    private long block_hour;

    @Column(name = "TRAN_TYPE")
    private String tran_type;

    @Column(name = "USER_EMAIL_TXT")
    private String user_email_txt;

    @Column(name = "CUSTOMER_CARE")
    private String customer_car;

    @Column(name = "EMAIL_SUBJECT")
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

    public long getBlock_count() { return block_count;}

    public void setBlock_count(long block_count) {this.block_count = block_count;}

    public long getBlock_hour() {    return block_hour;}

    public void setBlock_hour(long block_hour) {this.block_hour = block_hour;}

    public String getTran_type() {    return tran_type;}

    public void setTran_type(String tran_type) {    this.tran_type = tran_type;}

    public String getUser_email_txt() {    return user_email_txt;}

    public void setUser_email_txt(String user_email_txt) {    this.user_email_txt = user_email_txt;}

    public String getCustomer_car() {    return customer_car;}

    public void setCustomer_car(String customer_car) {    this.customer_car = customer_car;}

    public String getEmail_subject() {    return email_subject;}

    public void setEmail_subject(String email_subject) {    this.email_subject = email_subject;}

}
