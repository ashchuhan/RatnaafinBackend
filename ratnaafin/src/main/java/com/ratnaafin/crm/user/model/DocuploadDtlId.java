package com.ratnaafin.crm.user.model;

import com.sun.istack.NotNull;

import java.io.Serializable;

public class DocuploadDtlId implements Serializable {

    @NotNull
    long inquiry_id;

    @NotNull
    String doc_uuid;

    public long getInquiry_id() {
        return inquiry_id;
    }

    public void setInquiry_id(long inquiry_id) {
        this.inquiry_id = inquiry_id;
    }

    public String getDoc_uuid() {
        return doc_uuid;
    }

    public void setDoc_uuid(String doc_uuid) {
        this.doc_uuid = doc_uuid;
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
