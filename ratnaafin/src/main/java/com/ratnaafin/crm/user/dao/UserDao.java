package com.ratnaafin.crm.user.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ratnaafin.crm.user.model.User_master;

@Repository
public interface UserDao extends CrudRepository<User_master, Long>{
	@Query("select u from User_master u where u.user_name = ?1 and u.is_active = ?2")
	User_master findByUserName(String username, boolean active);
	
    @Modifying
    @Query("update User_master u set u.login_attempt = ?2 where u.user_name = ?1")
	void updateLoginAttempt(String userName, boolean loginAttempt);
	
    @Modifying
    @Query("update User_master u set u.is_active = 0 where u.id = ?1")
    void delete(long id);
	
    @Modifying
    @Query("update User_master u set u.password = ?2 where u.id = ?1")
	void updatePassword(long id, String bPassword);
    /*
    @Modifying
    @Query("update User_master u set u.last_token_val = ?2 where u.user_name = ?1")
    void updateTokenValue(String userName, String tokenValue);

    @Query("select u from User_master u where u.last_token_val = ?1 and u.is_active = ?2")
    User_master findByTokenValue(String token, boolean active);*/
}
