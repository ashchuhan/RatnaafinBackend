package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.CRMUsersLoginHisDto;
import com.ratnaafin.crm.user.model.CRMUsersLoginHis;

import java.util.ArrayList;
import java.util.List;

public class CRMUsersLoginHisMapper {
    public static CRMUsersLoginHis convertDtoTo(CRMUsersLoginHisDto crmUsersLoginHisDto){
        CRMUsersLoginHis crmUsersLoginHis = new CRMUsersLoginHis();
        if(crmUsersLoginHisDto != null) {
            crmUsersLoginHis.setUser_id(crmUsersLoginHisDto.getUser_id());
            crmUsersLoginHis.setCrmUserMst(crmUsersLoginHisDto.getCrmUserMst());
            crmUsersLoginHis.setTran_dt(crmUsersLoginHisDto.getTran_dt());
            crmUsersLoginHis.setTransaction_id(crmUsersLoginHisDto.getTransaction_id());
            crmUsersLoginHis.setOtp_sent_status(crmUsersLoginHisDto.getOtp_sent_status());
            crmUsersLoginHis.setOtp_sent_res_data(crmUsersLoginHisDto.getOtp_sent_res_data());
            crmUsersLoginHis.setOtp_verify(crmUsersLoginHisDto.getOtp_verify());
            crmUsersLoginHis.setOtp_length(crmUsersLoginHisDto.getOtp_length());
            crmUsersLoginHis.setOtp_expiry(crmUsersLoginHisDto.getOtp_expiry());
            crmUsersLoginHis.setOtp_ver_status(crmUsersLoginHisDto.getOtp_ver_status());
            crmUsersLoginHis.setOtp_ver_res_data(crmUsersLoginHisDto.getOtp_ver_res_data());
        }
        return crmUsersLoginHis;
    }
    public static CRMUsersLoginHisDto convertToDto(CRMUsersLoginHis crmUsersLoginHis){
        CRMUsersLoginHisDto crmUsersLoginHisDto = new CRMUsersLoginHisDto();
        if(crmUsersLoginHis != null) {
            crmUsersLoginHisDto.setUser_id(crmUsersLoginHis.getUser_id());
            crmUsersLoginHisDto.setCrmUserMst(crmUsersLoginHis.getCrmUserMst());
            crmUsersLoginHisDto.setTran_dt(crmUsersLoginHis.getTran_dt());
            crmUsersLoginHisDto.setTransaction_id(crmUsersLoginHis.getTransaction_id());
            crmUsersLoginHisDto.setOtp_sent_status(crmUsersLoginHis.getOtp_sent_status());
            crmUsersLoginHisDto.setOtp_sent_res_data(crmUsersLoginHis.getOtp_sent_res_data());
            crmUsersLoginHisDto.setOtp_verify(crmUsersLoginHis.getOtp_verify());
            crmUsersLoginHisDto.setOtp_length(crmUsersLoginHis.getOtp_length());
            crmUsersLoginHisDto.setOtp_expiry(crmUsersLoginHis.getOtp_expiry());
            crmUsersLoginHisDto.setOtp_ver_status(crmUsersLoginHis.getOtp_ver_status());
            crmUsersLoginHisDto.setOtp_ver_res_data(crmUsersLoginHis.getOtp_ver_res_data());
        }
        return crmUsersLoginHisDto;
    }

    public static List<CRMUsersLoginHisDto> convertCRMLoginListToDtoList(List<? extends CRMUsersLoginHis> LoginList) {
        List<CRMUsersLoginHisDto> dtoList = null;
        if (LoginList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < LoginList.size(); i++) {
                dtoList.add(convertToDto((CRMUsersLoginHis) LoginList.get(i)));
            }
        }
        return dtoList;
    }
}
