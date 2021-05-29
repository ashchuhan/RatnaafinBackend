package com.ratnaafin.crm.common.exception;

public class ErrorDetails {
	private String status;
	private Error_data error_data;
	public ErrorDetails(String status,String error_cd,String error_title,String error_msg,String error_detail) {
		super();
		this.status = status;
		error_data = new Error_data(error_cd, error_title, error_msg, error_detail);
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Error_data getError_data() {
		return error_data;
	}
	public void setError_data(Error_data error_data) {
		this.error_data = error_data;
	}
	public class Error_data{
		private String error_cd;
		private String error_title;
		private String error_msg;
		private String error_detail;
		public Error_data(String error_cd,String error_title,String error_msg,String error_detail) {
			this.error_cd = error_cd;
			this.error_title = error_title;
			this.error_msg = error_msg;
			this.error_detail = error_detail;
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
}
