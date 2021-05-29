package com.ratnaafin.crm.user.model;

import javax.persistence.*;
import java.sql.Clob;
import java.util.Date;

@Table(name = "API_ERROR_LOG")
@Entity(name = "Api_error_log")
public class APIErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "Transequence")
    @SequenceGenerator(name = "Transequence", sequenceName = "seq_api_error",initialValue = 1,allocationSize = 1)
    @Column(name = "TRAN_CD")
    private long id;

    @Column(name = "TRAN_DT")
    private Date tran_dt = new Date();

    @Column(name = "ERROR_MSG")
    private String error_msg;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "REQUEST_DATA")
    private String request_data;
    
    @Column(name = "REQ_UNIQUE_ID")
    private String request_unique_id;
    
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

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
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
    public String getRequest_unique_id() { return request_unique_id;}

    public void setRequest_unique_id(String request_unique_id) { this.request_unique_id = request_unique_id;}

}
