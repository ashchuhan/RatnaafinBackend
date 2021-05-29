package com.ratnaafin.crm.user.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ratnaafin.crm.user.model.BranchMaster;

@Repository
public interface BranchMasterDao extends CrudRepository<BranchMaster,Long> {
    @Query("select u from Branch_mst u where u.comp_cd=?1 and u.branch_cd=?2")
    BranchMaster func_find_branch_nm_by_branch_cd(String comp_cd,String branch_cd);
}