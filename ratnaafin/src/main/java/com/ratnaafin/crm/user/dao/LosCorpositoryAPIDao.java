package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.DocUploadBlobDtl;
import com.ratnaafin.crm.user.model.LosCorpositoryAPI;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LosCorpositoryAPIDao extends CrudRepository<LosCorpositoryAPI, Long> {

    LosCorpositoryAPI save(LosCorpositoryAPI losCorpositoryAPI);

    @Query("select u from Los_corp_apis_hdr u where u.tran_cd = ?1")
    LosCorpositoryAPI findCorpRequestByID(String uuid);

}
