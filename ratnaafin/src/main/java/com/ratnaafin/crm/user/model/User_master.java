package com.ratnaafin.crm.user.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;

@Entity
@Table(name = "USER_MASTER")
public class User_master {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "usersequence")
	@SequenceGenerator(name = "usersequence", sequenceName = "seq_user_master_id",initialValue = 1,allocationSize = 1)
	@Column(name = "ID")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "age")
	private long age;
	
	@Column(name = "first_name")
	private String first_name;

	@Column(name = "last_name")
	private String last_name;
	
	@Column(name = "login_attempt")
	private boolean login_attempt;

	@Column(name = "is_active")
	private boolean is_active = true;
	
	@Column(name = "password")
	@NotNull
	private String password;
	
	@Column(name = "user_name", unique=true)
	@NotNull
	private String user_name;
	
	@Column(name = "create_date")
	private Date create_date;

	@Column(name = "email")
	private String email;

	@Column(name = "phone_no")
	private String phone_no;

	@Column(name = "update_date")
	private Date update_date;
	
	@Column(name = "flag")
    private String flag;

    @Column(name = "user_id")
    private String user_id;

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	@ManyToMany(fetch = FetchType.EAGER/*, cascade = { CascadeType.MERGE, CascadeType.PERSIST }*/)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	
	private List<Role> role;

	@Override
	public String toString() {
		return "USER_MASTER{" + "id=" + id + ", first_name='" + first_name + '\'' + ", last_name='" + last_name + '\''
				+ ", user_name='" + user_name + '\'' + ", password='" + "*********" + '\'' + ", role=" + role + '}';
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public boolean isLogin_attempt() {
		return login_attempt;
	}

	public void setLogin_attempt(boolean login_attempt) {
		this.login_attempt = login_attempt;
	}

	public boolean isIs_active() {
		return is_active;
	}

	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}

	public Date getUpdate_date() {
		return update_date;
	}

	public void setUpdate_date(Date update_date) {
		this.update_date = update_date;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}
	
}
