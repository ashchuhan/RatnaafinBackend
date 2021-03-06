package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name="los_corpository_apis_hdr")
@Entity(name="Los_corp_apis_hdr")
public class LosCorpositoryAPI {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence", sequenceName = "corpository_login_seq",initialValue = 1,allocationSize = 1)
    @Column(name = "tran_cd")
    private Long tran_cd;

    @Column(name="tran_dt")
    private Date tran_dt = new Date();

    @Column(name = "request_type")
    private String request_type;

    @Column(name="initiated_req1")
    private String initiated_req1;

    @Column(name="initiated_req2")
    private String initiated_req2;

    @Column(name = "status")
    private String status;

    @Column(name="api_res")
    private String api_res;

    @Column(name = "ref_tran_cd")
    private  Long ref_tran_cd;

    @Column(name = "ref_sr_cd")
    private Long ref_Sr_cd;

    @Column(name = "entity_type")
    private String entity_type;

    @Column(name = "remarks")
    private String remarks;

    public Long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(Long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getInitiated_req1() {
        return initiated_req1;
    }

    public void setInitiated_req1(String initiated_req1) {
        this.initiated_req1 = initiated_req1;
    }

    public String getInitiated_req2() {
        return initiated_req2;
    }

    public void setInitiated_req2(String initiated_req2) {
        this.initiated_req2 = initiated_req2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApi_res() {
        return api_res;
    }

    public void setApi_res(String api_res) {
        this.api_res = api_res;
    }

    public Long getRef_tran_cd() {
        return ref_tran_cd;
    }

    public void setRef_tran_cd(Long ref_tran_cd) {
        this.ref_tran_cd = ref_tran_cd;
    }

    public Long getRef_Sr_cd() {
        return ref_Sr_cd;
    }

    public void setRef_Sr_cd(Long ref_Sr_cd) {
        this.ref_Sr_cd = ref_Sr_cd;
    }

    public String getEntity_type() {
        return entity_type;
    }

    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


}
