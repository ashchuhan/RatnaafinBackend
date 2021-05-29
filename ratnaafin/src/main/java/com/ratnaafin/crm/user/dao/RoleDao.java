package com.ratnaafin.crm.user.dao;

import org.springframework.data.repository.CrudRepository;

import com.ratnaafin.crm.user.model.Role;

public interface RoleDao extends CrudRepository<Role, Long>{
	Role findByName(String rolename);
}
