package com.ratnaafin.crm.user.model;

import java.io.Serializable;

public class BranchMasterID implements Serializable {
	private String comp_cd;
    private String branch_cd;

    public String getComp_cd() {
        return comp_cd;
    }

    public void setComp_cd(String comp_cd) {
        this.comp_cd = comp_cd;
    }

    public String getBranch_cd() {
        return branch_cd;
    }

    public void setBranch_cd(String branch_cd) {
        this.branch_cd = branch_cd;
    }	
}
