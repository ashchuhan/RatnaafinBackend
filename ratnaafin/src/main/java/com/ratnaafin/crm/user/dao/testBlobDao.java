package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.testBlob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface testBlobDao extends CrudRepository<testBlob,String> {

    testBlob save(testBlob testBlob);

    @Query("select u from Test_blob u where u.id = ?1")
    testBlob findByDocumentID(String id);
}
