package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "crm_pincode_api_reqres_dtl")
@Entity(name = "Crm_pincode_reqres_dtl")
public class CRMPincodeApiDtl {

    @Id
    @Column(name = "REF_UID")
    private String ref_uid;

    @Column(name = "REF_INQUIRY_ID")
    private Long ref_inquiry_id;

    @Column(name = "TRANSACTION_ID")
    private String transactionID;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "REQ_STATUS")
    private String resStatus;

    @Column(name = "RES_DATA")
    private String resData;

    public String getRef_uid() {
        return ref_uid;
    }

    public void setRef_uid(String ref_uid) {
        this.ref_uid = ref_uid;
    }

    public Long getRef_inquiry_id() {
        return ref_inquiry_id;
    }

    public void setRef_inquiry_id(Long ref_inquiry_id) {
        this.ref_inquiry_id = ref_inquiry_id;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public String getResStatus() {
        return resStatus;
    }

    public void setResStatus(String resStatus) {
        this.resStatus = resStatus;
    }

    public String getResData() {
        return resData;
    }

    public void setResData(String resData) {
        this.resData = resData;
    }
}
