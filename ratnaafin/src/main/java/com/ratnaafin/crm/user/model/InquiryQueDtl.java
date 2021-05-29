package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "CRM_INQUIRY_QUE_DTL")
@Entity(name = "Crm_inquiry_que_dtl")

public class InquiryQueDtl {

    @Id
    @Column(name = "TRAN_CD")
    private long tran_cd;

    @Column(name = "SR_CD")
    private long sr_cd;

    @Column(name = "LABLE")
    private String lable;

    @Column(name = "KEY_VALUE")
    private String key_value;

    public long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public long getSr_cd() {
        return sr_cd;
    }

    public void setSr_cd(long sr_cd) {
        this.sr_cd = sr_cd;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getKey_value() {
        return key_value;
    }

    public void setKey_value(String key_value) {
        this.key_value = key_value;
    }
}
