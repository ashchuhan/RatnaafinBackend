package com.ratnaafin.crm.user.model;

import com.sun.istack.NotNull;

import java.io.Serializable;
public class CrmMiscMstId implements Serializable {
    @NotNull
    String category_cd;

    @NotNull
    String data_value;

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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

