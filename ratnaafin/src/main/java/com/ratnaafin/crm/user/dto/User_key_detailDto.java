package com.ratnaafin.crm.user.dto;

import java.util.Date;

public class User_key_detailDto {
	private long id;
	
	private long user_id;
	
	private String public_key;
	
	private String private_key;
	
	private String mac_add;
	
	private String ip_add;
	
	private String host_name;
	
	private String os_name;
	
	private Date last_usage_date=new Date();
	
	private String ServerPublic_key;

	public String getPublic_key() {
		return public_key;
	}

	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	public String getPrivate_key() {
		return private_key;
	}

	public void setPrivate_key(String private_key) {
		this.private_key = private_key;
	}

	public String getMac_add() {
		return mac_add;
	}

	public void setMac_add(String mac_add) {
		this.mac_add = mac_add;
	}

	public String getIp_add() {
		return ip_add;
	}

	public void setIp_add(String ip_add) {
		this.ip_add = ip_add;
	}

	public String getHost_name() {
		return host_name;
	}

	public void setHost_name(String host_name) {
		this.host_name = host_name;
	}

	public String getOs_name() {
		return os_name;
	}

	public void setOs_name(String os_name) {
		this.os_name = os_name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getServerPublic_key() {
		return ServerPublic_key;
	}

	public void setServerPublic_key(String serverPublic_key) {
		ServerPublic_key = serverPublic_key;
	}

	public Date getLast_usage_date() {
		return last_usage_date;
	}

	public void setLast_usage_date(Date last_usage_date) {
		this.last_usage_date = last_usage_date;
	}

}
