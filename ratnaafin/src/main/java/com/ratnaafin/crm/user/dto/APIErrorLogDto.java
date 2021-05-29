package com.ratnaafin.crm.user.dto;

import java.sql.Clob;
import java.util.Date;

public class APIErrorLogDto {
    private long id;
    private Date tran_dt;
    private String error_msg;
    private String action;
    private String channel;
    private String request_data;
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
}
