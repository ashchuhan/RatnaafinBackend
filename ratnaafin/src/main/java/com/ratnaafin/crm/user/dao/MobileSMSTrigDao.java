package com.ratnaafin.crm.user.dao;


import com.ratnaafin.crm.user.model.MobileSMSTrig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileSMSTrigDao extends CrudRepository<MobileSMSTrig ,Long> {

    @Query("select u from Mobile_sms_trig_mst u where u.id = ?1")
    MobileSMSTrig findById(long id);

}
