package com.ratnaafin.crm.user.dto;

public class BranchMasterDto {
	private String comp_cd;
    private String branch_cd;
    private String branch_nm;

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

    public String getBranch_nm() {
        return branch_nm;
    }

    public void setBranch_nm(String branch_nm) {
        this.branch_nm = branch_nm;
    }
}
