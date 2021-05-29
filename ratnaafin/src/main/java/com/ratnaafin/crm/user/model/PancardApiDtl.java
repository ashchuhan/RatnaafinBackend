package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "CRM_PANCARD_REQRES_DTL")
@Entity(name = "Crm_pancard_reqres_dtl")
public class PancardApiDtl {

    @Column(name = "REF_INQUIRY_ID")
    private long ref_inquiry_id;

    @Id
    @Column(name = "REF_UID")
    private String ref_uid;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "TRANSACTION_ID")
    private String transaction_id;

    @Column(name = "RESPONSE_STATUS")
    private String response_status;

    @Column(name = "RESPONSE_DATA")
    private String response_data;

    public long getRef_inquiry_id() {
        return ref_inquiry_id;
    }

    public void setRef_inquiry_id(long ref_inquiry_id) {
        this.ref_inquiry_id = ref_inquiry_id;
    }

    public String getRef_uid() {
        return ref_uid;
    }

    public void setRef_uid(String ref_uid) {
        this.ref_uid = ref_uid;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getResponse_status() {
        return response_status;
    }

    public void setResponse_status(String response_status) {
        this.response_status = response_status;
    }

    public String getResponse_data() {
        return response_data;
    }

    public void setResponse_data(String response_data) {
        this.response_data = response_data;
    }
}
