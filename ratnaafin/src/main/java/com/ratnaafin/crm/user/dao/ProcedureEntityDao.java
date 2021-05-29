package com.ratnaafin.crm.user.dao;

import org.springframework.data.repository.CrudRepository;
import com.ratnaafin.crm.user.model.ProcedureEntity;

public interface ProcedureEntityDao extends CrudRepository<ProcedureEntity, Long>{
//	@Procedure(name="test")
//	void isOnlyTest(@Param("A_INPUT") String A_INPUT);
}
