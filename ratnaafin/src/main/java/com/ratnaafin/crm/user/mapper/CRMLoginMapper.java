package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.CRMLoginDto;
import com.ratnaafin.crm.user.model.CRMLogin;

import java.util.ArrayList;
import java.util.List;

public class CRMLoginMapper {
    public static CRMLogin convertDtoToCRMLogin(CRMLoginDto crmLoginDto){
        CRMLogin crmLogin = new CRMLogin();

        if(crmLoginDto != null) {
            crmLogin.setUser_id(crmLoginDto.getUser_id());
            crmLogin.setE_mail_id(crmLoginDto.getE_mail_id());
            crmLogin.setMobile(crmLoginDto.getMobile());
            crmLogin.setUser_password(crmLoginDto.getUser_password());
            crmLogin.setActive(crmLoginDto.getActive());
            crmLogin.setFlag(crmLoginDto.getFlag());
            crmLogin.setTran_dt(crmLoginDto.getTran_dt());
        }
        return crmLogin;
    }
    public static CRMLoginDto convertCRMLoginToDto(CRMLogin crmLogin){
        CRMLoginDto crmLoginDto = new CRMLoginDto();
        if(crmLogin != null) {
            crmLoginDto.setUser_id(crmLogin.getUser_id());
            crmLoginDto.setE_mail_id(crmLogin.getE_mail_id());
            crmLoginDto.setMobile(crmLogin.getMobile());
            crmLoginDto.setUser_password(crmLogin.getUser_password());
            crmLoginDto.setActive(crmLogin.getActive());
            crmLoginDto.setFlag(crmLogin.getFlag());
            crmLoginDto.setTran_dt(crmLogin.getTran_dt());
        }
        return crmLoginDto;
    }

    public static List<CRMLoginDto> convertCRMLoginListToDtoList(List<? extends CRMLogin> LoginList) {
        List<CRMLoginDto> dtoList = null;
        if (LoginList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < LoginList.size(); i++) {
                dtoList.add(convertCRMLoginToDto((CRMLogin) LoginList.get(i)));
            }
        }
        return dtoList;
    }
}
