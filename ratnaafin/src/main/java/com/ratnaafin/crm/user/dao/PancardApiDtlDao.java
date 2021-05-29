package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.PancardApiDtl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PancardApiDtlDao extends CrudRepository<PancardApiDtl, Long> {
    //@Query("select u from Crm_pancard_reqres_dtl u where u.ref_inquiry_id = ?1 and u.pancard_no= ?2")
    //PancardApiDtl findByPancardNo(String ref_inquiry_id,String pancard_no);

    PancardApiDtl save(PancardApiDtl pancardApiDtl);

    @Query("select u from Crm_pancard_reqres_dtl u where u.ref_inquiry_id = ?1 ")
    List<PancardApiDtl> findPancardNoByRefId(long ref_inquiry_id);

    @Query("select count(*) from Crm_pancard_reqres_dtl where ref_inquiry_id = ?1 and pancard_no=?2")
    int getpancardCount(long ref_inquiry_id,String pancard_no);

    //@Query("select max(u.sr_cd) from Crm_pancard_reqres_dtl u where u.ref_inquiry_id = ?1")
    //int getPanApiMaxSrcd(Long ref_inquiry_id);

}
