package com.ratnaafin.crm.user.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ratnaafin.crm.user.model.Client;

public interface ClientDao extends CrudRepository<Client, Long>{
	@Query("select u from Oauth_client_details u where u.client_id = ?1")
	Client findByClientName(String clientname);
}
