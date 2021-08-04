package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;

@Table(name = "los_lead_sanction_dtl")
@Entity(name = "los_lead_sanction_dtl")
public class LeadSanctionDtl{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence",sequenceName = "los_lead_sanction_dtl_seq",initialValue = 1,allocationSize = 1)
    @Column(name = "tran_cd")
    private Long tran_cd;

    @Column(name = "branch_id")
    private Long branch_id;

    @Column(name = "lead_tran_cd")
    private Long lead_tran_cd;

    @Column(name = "sanction_file")
    private byte[] sanction_file;

    @Column(name = "last_entered_by")
    private String last_entered_by;

    @Column(name = "last_modified_date")
    private Date last_modified_date = new Date();

    public Long getTran_cd() {
        return tran_cd;
    }

    public void setTran_cd(Long tran_cd) {
        this.tran_cd = tran_cd;
    }

    public Long getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(Long branch_id) {
        this.branch_id = branch_id;
    }

    public Long getLead_tran_cd() {
        return lead_tran_cd;
    }

    public void setLead_tran_cd(Long lead_tran_cd) {
        this.lead_tran_cd = lead_tran_cd;
    }

    public byte[] getSanction_file() {
        return sanction_file;
    }

    public void setSanction_file(byte[] sanction_file) {
        this.sanction_file = sanction_file;
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
}
