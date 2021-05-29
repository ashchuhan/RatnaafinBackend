package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.UniqueIDDtlDto;
import com.ratnaafin.crm.user.model.UniqueID_dtl;

import java.util.ArrayList;
import java.util.List;

public class UniqueIDDtlMapper {
    public static UniqueID_dtl convertDto(UniqueIDDtlDto uniqueIDDtlDto){
        UniqueID_dtl uniqueID_dtl = new UniqueID_dtl();
        if(uniqueIDDtlDto != null) {
            uniqueID_dtl.setUserid(uniqueIDDtlDto.getUserid());
            uniqueID_dtl.setTran_dt(uniqueIDDtlDto.getTran_dt());
            uniqueID_dtl.setRef_inquiry_id(uniqueIDDtlDto.getRef_inquiry_id());
            uniqueID_dtl.setTransaction_id(uniqueIDDtlDto.getTransaction_id());
            uniqueID_dtl.setReq_status(uniqueIDDtlDto.getReq_status());
            uniqueID_dtl.setUrl_res(uniqueIDDtlDto.getUrl_res());
            uniqueID_dtl.setStatus(uniqueIDDtlDto.getStatus());
            uniqueID_dtl.setWebhook_res(uniqueIDDtlDto.getWebhook_res());
            uniqueID_dtl.setWebhook_status(uniqueIDDtlDto.getWebhook_status());
            uniqueID_dtl.setImg(uniqueIDDtlDto.getImg());
            uniqueID_dtl.setXmlfile(uniqueIDDtlDto.getXmlfile());
            uniqueID_dtl.setDownload_status(uniqueIDDtlDto.getDownload_status());
            uniqueID_dtl.setInitiated_req(uniqueIDDtlDto.getInitiated_req());
        }
        return uniqueID_dtl;
    }
    public static UniqueIDDtlDto convertToDto(UniqueID_dtl uniqueID_dtl){
        UniqueIDDtlDto uniqueIDDtlDto = new UniqueIDDtlDto();
        if(uniqueID_dtl != null) {
            uniqueIDDtlDto.setUserid(uniqueID_dtl.getUserid());
            uniqueIDDtlDto.setTran_dt(uniqueID_dtl.getTran_dt());
            uniqueIDDtlDto.setRef_inquiry_id(uniqueID_dtl.getRef_inquiry_id());
            uniqueIDDtlDto.setTransaction_id(uniqueID_dtl.getTransaction_id());
            uniqueIDDtlDto.setReq_status(uniqueID_dtl.getReq_status());
            uniqueIDDtlDto.setUrl_res(uniqueID_dtl.getUrl_res());
            uniqueIDDtlDto.setStatus(uniqueID_dtl.getStatus());
            uniqueIDDtlDto.setWebhook_res(uniqueID_dtl.getWebhook_res());
            uniqueIDDtlDto.setWebhook_status(uniqueID_dtl.getWebhook_status());
            uniqueIDDtlDto.setImg(uniqueID_dtl.getImg());
            uniqueIDDtlDto.setXmlfile(uniqueID_dtl.getXmlfile());
            uniqueIDDtlDto.setDownload_status(uniqueID_dtl.getDownload_status());
            uniqueIDDtlDto.setInitiated_req(uniqueID_dtl.getInitiated_req());
        }
        return uniqueIDDtlDto;
    }

    public static List<UniqueIDDtlDto> convertAppListToDtoList(List<? extends UniqueID_dtl> AppList) {
        List<UniqueIDDtlDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertToDto((UniqueID_dtl) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
