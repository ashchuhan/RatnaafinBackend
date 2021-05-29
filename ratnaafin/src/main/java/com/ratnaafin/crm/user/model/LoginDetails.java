package com.ratnaafin.crm.user.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "LOGIN_DETAIL")
public class LoginDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "userloginsequence")
	@SequenceGenerator(name = "userloginsequence", sequenceName = "seq_login_dtl_id",initialValue = 1,allocationSize = 1)
	@Column(name = "ID")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "user_name")
	private String user_name;

	@Column(name = "mac_add", length = 40)
	private String mac_add;

	@Column(name = "ip_add", length = 50)
	private String ip_add;

	@Column(name = "login_time")
	//@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	private Date login_time;
	
	@Column(name = "host_name")
	private String host_name;
	
	@Column(name = "os_name")
	private String os_name;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
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

	public Date getLogin_time() {
		return login_time;
	}

	public void setLogin_time(Date login_time) {
		this.login_time = login_time;
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
}
