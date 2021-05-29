package com.ratnaafin.crm.user.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ratnaafin.crm.user.model.User_key_detail;

@Repository
public interface UserKeyDao extends CrudRepository<User_key_detail, Long>{
	@Query("select u from User_key_detail u where u.user_id = ?1")
	User_key_detail findKeyByUserId(long user_id);
	
	@Modifying
	@Query("update User_key_detail u set u.last_usage_date = ?2 where u.id = ?1")
	void updateusagedate(long id, Date usagedate);
}
