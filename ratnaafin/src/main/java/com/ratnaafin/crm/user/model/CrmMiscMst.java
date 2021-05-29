package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "Crm_misc_mst")
@Entity(name = "Crm_misc_mst")
@IdClass(CrmMiscMstId.class)
public class CrmMiscMst {
    @Id
    @Column(name = "category_cd")
    private String category_cd;
    @Id
    @Column(name = "data_value")
    private  String data_value;
    @Column(name = "display_value")
    private  String display_value;
    @Column(name = "variant1")
    private String variant1;
    @Column(name = "sort_order")
    private Long sort_order;
    @Column(name = "entered_by")
    private String entered_by;
    @Column(name = "entered_date")
    private Date entered_date;
    @Column(name = "last_entered_by")
    private  String last_entered_by;
    @Column(name = "last_modified_date")
    private  Date last_modified_date;
    @Column(name = "machine_nm")
    private  String machine_nm;
    @Column(name = "last_machine_nm")
    private  String last_machine_nm;


    public String getCategory_cd() {
        return category_cd;
    }

    public void setCategory_cd(String category_cd) {
        this.category_cd = category_cd;
    }

    public String getData_value() {
        return data_value;
    }

    public void setData_value(String data_value) {
        this.data_value = data_value;
    }

    public String getDisplay_value() {
        return display_value;
    }

    public void setDisplay_value(String display_value) {
        this.display_value = display_value;
    }

    public String getVariant1() {
        return variant1;
    }

    public void setVariant1(String variant1) {
        this.variant1 = variant1;
    }

    public Long getSort_order() {
        return sort_order;
    }

    public void setSort_order(Long sort_order) {
        this.sort_order = sort_order;
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
}
