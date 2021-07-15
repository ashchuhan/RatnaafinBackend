package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;

@Table(name = "los_lead_termsheet_dtl")
@Entity(name = "los_lead_termsheet_dtl")
public class LeadTermSheetDtl {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence", sequenceName = "los_lead_termsheet_dtl_seq",initialValue = 1,allocationSize = 1)
    @Column(name = "TRAN_CD")
    private Long tran_cd;

    @Column(name = "branch_id")
    private Long branch_id;

    @Column(name = "lead_cd")
    private Long lead_cd;

    @Column(name = "termsheet_file")
    private byte[] termsheet_file;

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

    public Long getLead_cd() {
        return lead_cd;
    }

    public void setLead_cd(Long lead_cd) {
        this.lead_cd = lead_cd;
    }

    public byte[] getTermsheet_file() {
        return termsheet_file;
    }

    public void setTermsheet_file(byte[] termsheet_file) {
        this.termsheet_file = termsheet_file;
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
