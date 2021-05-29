package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.CRMCAMDtlDto;
import com.ratnaafin.crm.user.model.CRMCAMDtl;

import java.util.ArrayList;
import java.util.List;

public class CRMCAMDtlMapper {
    public static CRMCAMDtl convertDto(CRMCAMDtlDto crmcamDtlDto){
        CRMCAMDtl crmcamDtl = new CRMCAMDtl();
        if(crmcamDtlDto != null) {
            crmcamDtl.setTran_cd(crmcamDtlDto.getTran_cd());
            crmcamDtl.setSr_cd(crmcamDtlDto.getSr_cd());
            crmcamDtl.setStatus(crmcamDtlDto.getStatus());
            crmcamDtl.setLast_modified_date(crmcamDtlDto.getLast_modified_date());
            crmcamDtl.setCam_data(crmcamDtlDto.getCam_data());
        }
        return crmcamDtl;
    }
    public static CRMCAMDtlDto convertToDto(CRMCAMDtl crmcamDtl){
        CRMCAMDtlDto crmcamDtlDto = new CRMCAMDtlDto();
        if(crmcamDtl != null) {
            crmcamDtlDto.setTran_cd(crmcamDtl.getTran_cd());
            crmcamDtlDto.setSr_cd(crmcamDtl.getSr_cd());
            crmcamDtlDto.setStatus(crmcamDtl.getStatus());
            crmcamDtlDto.setLast_modified_date(crmcamDtl.getLast_modified_date());
            crmcamDtlDto.setCam_data(crmcamDtl.getCam_data());
        }
        return crmcamDtlDto;
    }

    public static List<CRMCAMDtlDto> convertAppListToDtoList(List<? extends CRMCAMDtl> AppList) {
        List<CRMCAMDtlDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertToDto((CRMCAMDtl) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
