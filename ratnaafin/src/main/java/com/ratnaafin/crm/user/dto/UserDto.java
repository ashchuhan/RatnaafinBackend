package com.ratnaafin.crm.user.dto;

import java.util.Date;
import java.util.List;

import com.sun.istack.NotNull;

public class UserDto {
	
	private long id;

	private long age;
	
	@NotNull    
	private String first_name;

    @NotNull   
	private String last_name;
	
    private boolean login_attempt;
    
    private boolean is_active=true;
    
    @NotNull
    private String password;
    
    @NotNull  
	private String user_name;
    
    private Date create_date=new Date();
    
    @NotNull
    private String email;
	
    @NotNull
    private String phone_no;
	
	private Date update_date=new Date();
	
	private String flag;

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

	private List<RoleDto> role;

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

	public List<RoleDto> getRole() {
		return role;
	}

	public void setRole(List<RoleDto> role) {
		this.role = role;
	}	  
}
