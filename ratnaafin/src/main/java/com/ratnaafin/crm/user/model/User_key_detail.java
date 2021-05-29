package com.ratnaafin.crm.user.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity(name = "User_key_detail")
@Table(name = "USER_KEY_DETAIL")
public class User_key_detail {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "userkeysequence")
	@SequenceGenerator(name = "userkeysequence", sequenceName = "seq_user_key_detail",initialValue = 1,allocationSize = 1)
	@Column(name = "ID")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "USER_ID")
	private long user_id;
	
	@Column(name = "PUBLIC_KEY")
	private String public_key;
	
	@Column(name = "PRIVATE_KEY")
	private String private_key;
	
	@Column(name = "MAC_ADD")
	private String mac_add;
	
	@Column(name = "IP_ADD")
	private String ip_add;
	
	@Column(name = "HOST_NAME")
	private String host_name;
	
	@Column(name = "OS_NAME")
	private String os_name;
	
	@Column(name = "LAST_USAGE_DATE")
	private Date last_usage_date;

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

	public Date getLast_usage_date() {
		return last_usage_date;
	}

	public void setLast_usage_date(Date last_usage_date) {
		this.last_usage_date = last_usage_date;
	}
}
