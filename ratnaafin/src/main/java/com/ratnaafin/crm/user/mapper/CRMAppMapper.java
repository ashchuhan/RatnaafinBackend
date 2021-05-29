package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.CRMAppDto;
import com.ratnaafin.crm.user.model.CRMApp_mst;

import java.util.ArrayList;
import java.util.List;

public class CRMAppMapper {
    public static CRMApp_mst convertDtoToApp(CRMAppDto AppDto){
        CRMApp_mst crmAppMst = new CRMApp_mst();

        if(AppDto != null) {
            crmAppMst.setTran_cd(AppDto.getTran_cd());
            crmAppMst.setA(AppDto.getA());
            crmAppMst.setB(AppDto.getB());
        }
        return crmAppMst;
    }
    public static CRMAppDto convertAppToDto(CRMApp_mst crmAppMst){
        CRMAppDto AppDto = new CRMAppDto();
        if(crmAppMst != null) {
            AppDto.setTran_cd(crmAppMst.getTran_cd());
            AppDto.setA(crmAppMst.getA());
            AppDto.setB(crmAppMst.getB());
        }
        return AppDto;
    }

    public static List<CRMAppDto> convertAppListToDtoList(List<? extends CRMApp_mst> AppList) {
        List<CRMAppDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertAppToDto((CRMApp_mst) AppList.get(i)));
            }
        }
        return dtoList;
    }
}

