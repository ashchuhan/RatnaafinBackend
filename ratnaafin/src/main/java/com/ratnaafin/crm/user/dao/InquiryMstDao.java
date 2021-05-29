package com.ratnaafin.crm.user.dao;

import com.ratnaafin.crm.user.model.Inquiry_mst;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface InquiryMstDao extends CrudRepository<Inquiry_mst,Long> {
    @Query("select u from Crm_inquiry_mst u where u.tran_cd = ?1")
    Inquiry_mst findByInquiryID(long id);
    
    @Modifying
    @Query("update Crm_inquiry_mst u set u.team_lead_id = ?2 where u.tran_cd = ?1")
    int assignTeamLead(long inquiryId,long teamLeadID);

    @Modifying
    @Query("update Crm_inquiry_mst u set u.team_member_id = ?2 , u.status = ?3 where u.tran_cd = ?1")
    int assignTeamMember(long inquiryId,long teamMemberID,String status);

    @Modifying
    @Query("update Crm_inquiry_mst u set u.status = ?2 where u.tran_cd = ?1")
    int updateInquiryStatus(long inquiryId,String status);

    @Modifying
    @Query("update Crm_inquiry_mst u set u.priority = ?2 , u.lead_generate = ?3 where u.tran_cd = ?1")
    int updateInquiryPriority(long inquiryId,String priority,String leadGenerate);

}
