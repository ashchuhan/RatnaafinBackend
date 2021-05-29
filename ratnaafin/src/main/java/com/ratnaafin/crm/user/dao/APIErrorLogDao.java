package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.APIErrorLog;
import org.springframework.data.repository.CrudRepository;

public interface APIErrorLogDao  extends CrudRepository<APIErrorLog, Long> {
}
