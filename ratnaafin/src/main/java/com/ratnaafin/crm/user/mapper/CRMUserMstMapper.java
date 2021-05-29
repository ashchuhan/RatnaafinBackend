package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.CRMUserMstDto;
import com.ratnaafin.crm.user.model.CRMUserMst;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

public class CRMUserMstMapper {
    public static CRMUserMst convertDto(CRMUserMstDto crmUserMstDto){
        CRMUserMst crmUserMst = new CRMUserMst();
        if(crmUserMstDto != null) {
            crmUserMst.setUser_id(crmUserMstDto.getUser_id());
            crmUserMst.setTran_dt(crmUserMstDto.getTran_dt());
            crmUserMst.setSalutation(crmUserMstDto.getSalutation());
            crmUserMst.setFirst_name(crmUserMstDto.getFirst_name());
            crmUserMst.setMiddle_name(crmUserMstDto.getMiddle_name());
            crmUserMst.setLast_name(crmUserMstDto.getLast_name());
            crmUserMst.setGender(crmUserMstDto.getGender());
            crmUserMst.setBirth_dt(crmUserMstDto.getBirth_dt());
            crmUserMst.setMarried_flag(crmUserMstDto.getMarried_flag());
            crmUserMst.setMobile(crmUserMstDto.getMobile());
            crmUserMst.setE_mail_id(crmUserMstDto.getE_mail_id());
            crmUserMst.setLocation(crmUserMstDto.getLocation());
            crmUserMst.setCity(crmUserMstDto.getCity());
            crmUserMst.setState(crmUserMstDto.getState());
            crmUserMst.setPostal_cd(crmUserMstDto.getPostal_cd());
            crmUserMst.setCountry(crmUserMstDto.getCountry());
            crmUserMst.setFlag(crmUserMstDto.getFlag());
            crmUserMst.setUser_password(crmUserMstDto.getUser_password());
            crmUserMst.setActive(crmUserMstDto.getActive());
            crmUserMst.setLast_login_dt(crmUserMstDto.getLast_login_dt());
            crmUserMst.setComp_cd(crmUserMstDto.getComp_cd());
            crmUserMst.setBranch_cd(crmUserMstDto.getBranch_cd());
        }
        return crmUserMst;
    }
    public static CRMUserMstDto convertToDto(CRMUserMst crmUserMst){
        CRMUserMstDto crmUserMstDto = new CRMUserMstDto();
        if(crmUserMst != null) {
            crmUserMstDto.setUser_id(crmUserMst.getUser_id());
            crmUserMstDto.setTran_dt(crmUserMst.getTran_dt());
            crmUserMstDto.setSalutation(crmUserMst.getSalutation());
            crmUserMstDto.setFirst_name(crmUserMst.getFirst_name());
            crmUserMstDto.setMiddle_name(crmUserMst.getMiddle_name());
            crmUserMstDto.setLast_name(crmUserMst.getLast_name());
            crmUserMstDto.setGender(crmUserMst.getGender());
            crmUserMstDto.setBirth_dt(crmUserMst.getBirth_dt());
            crmUserMstDto.setMarried_flag(crmUserMst.getMarried_flag());
            crmUserMstDto.setMobile(crmUserMst.getMobile());
            crmUserMstDto.setE_mail_id(crmUserMst.getE_mail_id());
            crmUserMstDto.setLocation(crmUserMst.getLocation());
            crmUserMstDto.setCity(crmUserMst.getCity());
            crmUserMstDto.setState(crmUserMst.getState());
            crmUserMstDto.setPostal_cd(crmUserMst.getPostal_cd());
            crmUserMstDto.setCountry(crmUserMst.getCountry());
            crmUserMstDto.setFlag(crmUserMst.getFlag());
            crmUserMstDto.setUser_password(crmUserMst.getUser_password());
            crmUserMstDto.setActive(crmUserMst.getActive());
            crmUserMstDto.setLast_login_dt(crmUserMst.getLast_login_dt());
            crmUserMstDto.setComp_cd(crmUserMst.getComp_cd());
            crmUserMstDto.setBranch_cd(crmUserMst.getBranch_cd());
        }
        return crmUserMstDto;
    }

    public static List<CRMUserMstDto> convertAppListToDtoList(List<? extends CRMUserMst> AppList) {
        List<CRMUserMstDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertToDto((CRMUserMst) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
