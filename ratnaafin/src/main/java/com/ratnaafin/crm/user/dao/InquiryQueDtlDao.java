package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.InquiryQueDtl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface InquiryQueDtlDao  extends CrudRepository<InquiryQueDtl,Long> {

    @Query("Select u.key_value from Crm_inquiry_que_dtl u where u.tran_cd = ?1 and u.lable = ?2")
    String getInquiryQueKeyValue(Long ref_inquiry_id,String lable);

}
