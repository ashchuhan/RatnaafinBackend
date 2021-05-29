package com.ratnaafin.crm.user.dao;
import org.springframework.data.repository.CrudRepository;

import com.ratnaafin.crm.user.model.LoginDetails;

public interface LoginDetailsDao extends CrudRepository<LoginDetails, Long>{

}
