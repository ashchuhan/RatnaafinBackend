package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "Crm_app_base_mst")
@Table(name = "CRM_APP_BASE_MST")
public class CRMApp_mst {
    @Id
    @Column(name = "tran_cd", unique=true, nullable=false)
    private long tran_cd;

    @Column(name = "a")
    private String a;

    @Column(name = "b")
    private String b;

    public long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}
