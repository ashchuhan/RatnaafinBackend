package com.ratnaafin.crm.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	private String status;
	private String error_cd;
	private String error_title;
	private String error_msg;
	private String error_detail;
	public UserAlreadyExistException(String status,String error_cd,String error_title,String error_msg,String error_detail) {
		super(error_msg);
		this.status = status;
		this.error_cd = error_cd;
		this.error_title = error_title;
		this.error_msg = error_msg;
		this.error_detail = error_detail;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError_cd() {
		return error_cd;
	}
	public void setError_cd(String error_cd) {
		this.error_cd = error_cd;
	}
	public String getError_title() {
		return error_title;
	}
	public void setError_title(String error_title) {
		this.error_title = error_title;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	public String getError_detail() {
		return error_detail;
	}
	public void setError_detail(String error_detail) {
		this.error_detail = error_detail;
	}
}
