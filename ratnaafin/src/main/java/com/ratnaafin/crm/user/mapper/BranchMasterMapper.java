package com.ratnaafin.crm.user.mapper;

import java.util.ArrayList;
import java.util.List;

import com.ratnaafin.crm.user.dto.BranchMasterDto;
import com.ratnaafin.crm.user.model.BranchMaster;

public class BranchMasterMapper {
	public static BranchMaster convertDtoToApp(BranchMasterDto branchMasterDto){
        BranchMaster branchMaster = new BranchMaster();

        if(branchMasterDto != null) {
            branchMaster.setComp_cd(branchMasterDto.getComp_cd());
            branchMaster.setBranch_cd(branchMasterDto.getBranch_cd());
            branchMaster.setBranch_nm(branchMasterDto.getBranch_nm());
        }
        return branchMaster;
    }
    public static BranchMasterDto convertAppToDto(BranchMaster branchMaster){
        BranchMasterDto branchMasterDto = new BranchMasterDto();
        if(branchMaster != null) {
            branchMasterDto.setComp_cd(branchMaster.getComp_cd());
            branchMasterDto.setBranch_cd(branchMaster.getBranch_cd());
            branchMasterDto.setBranch_nm(branchMaster.getBranch_nm());
        }
        return branchMasterDto;
    }

    public static List<BranchMasterDto> convertAppListToDtoList(List<? extends BranchMaster> AppList) {
        List<BranchMasterDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertAppToDto((BranchMaster) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
