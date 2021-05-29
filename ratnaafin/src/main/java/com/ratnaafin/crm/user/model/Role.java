package com.ratnaafin.crm.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ROLE")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "rolesequence")
	@SequenceGenerator(name = "rolesequence", sequenceName = "seq_user_role_id",initialValue = 1,allocationSize = 1)
	@Column(name = "id")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "name", unique=true, nullable=false)
	private String name;

	public Role() {
	}

	public Role(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		System.out.println("ID : "+id);
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		System.out.println("name : "+name);
		this.name = name;
	}

	@Override
	public String toString() {
		return "Role{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}
