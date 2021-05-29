package com.ratnaafin.crm.user.model;

import java.io.Serializable;

public class CRMUsersLoginHisID implements Serializable {
    private Long user_id;
    private String ref_uid;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getRef_uid() {
        return ref_uid;
    }

    public void setRef_uid(String ref_uid) {
        this.ref_uid = ref_uid;
    }
}
