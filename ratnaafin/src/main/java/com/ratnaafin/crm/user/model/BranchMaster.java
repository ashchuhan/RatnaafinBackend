package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Table(name = "BRANCH_MST")
@Entity(name = "Branch_mst")
@IdClass(BranchMasterID.class)
public class BranchMaster {
    @Id
    @Column(name = "comp_cd")
    private String comp_cd;

    @Id
    @Column(name = "branch_cd")
    private String branch_cd;

    @Column(name = "branch_nm")
    private String branch_nm;

    public String getBranch_nm() {
        return branch_nm;
    }

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

    public void setBranch_nm(String branch_nm) {
        this.branch_nm = branch_nm;
    }
}
