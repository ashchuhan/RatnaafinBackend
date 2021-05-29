package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.sql.Clob;
import java.util.Date;

@Table(name = "ALL_JSON_LOG")
@Entity(name = "All_json_log")
public class AllJsonLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence", sequenceName = "seq_json_log",initialValue = 1,allocationSize = 1)
    @Column(name = "TRAN_CD")
    private long id;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "ACTION")
    private String action;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "REQUEST_DATA")
    private String request_data;

    @Column(name = "FLAG")
    private String flag;

    @Column(name = "REQ_UNIQUE_ID")
    private String unique_id;
    
    @Column(name = "MODULE")
    private String module;

    public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTran_dt() {
        return tran_dt;
    }

    public void setTran_dt(Date tran_dt) {
        this.tran_dt = tran_dt;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRequest_data() {
        return request_data;
    }

    public void setRequest_data(String request_data) {
        this.request_data = request_data;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }
}
