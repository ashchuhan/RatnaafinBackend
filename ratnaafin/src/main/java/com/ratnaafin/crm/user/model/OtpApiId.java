package com.ratnaafin.crm.user.model;

import com.sun.istack.NotNull;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class OtpApiId implements Serializable {

    @NotNull
    long ref_inquiry_id;

    @NotNull
    long sr_cd;

    public OtpApiId()
    {

    }

    public OtpApiId(long ref_inquiry_id, long sr_cd) {
        this.ref_inquiry_id = ref_inquiry_id;
        this.sr_cd = sr_cd;
    }

    public Long getRef_inquiry_id() {
        return ref_inquiry_id;
    }

    public void setRef_inquiry_id(long ref_inquiry_id) {
        this.ref_inquiry_id = ref_inquiry_id;
    }

    public long getSr_cd() {
        return sr_cd;
    }

    public void setSr_cd(long sr_cd) {
        this.sr_cd = sr_cd;
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
