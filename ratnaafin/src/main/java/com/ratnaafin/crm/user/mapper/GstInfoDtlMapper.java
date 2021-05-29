package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.GstInfoDtlDto;
import com.ratnaafin.crm.user.model.GstInfo_dtl;

import java.util.ArrayList;
import java.util.List;

public class GstInfoDtlMapper {
    public static GstInfo_dtl convertDto(GstInfoDtlDto gstInfoDtlDto){
        GstInfo_dtl  gstInfo_dtl    = new GstInfo_dtl();
        if(gstInfoDtlDto != null) {
            gstInfo_dtl.setUserid(gstInfoDtlDto.getUserid());
            gstInfo_dtl.setTran_dt(gstInfoDtlDto.getTran_dt());
            gstInfo_dtl.setRef_inquiry_id(gstInfoDtlDto.getRef_inquiry_id());
            gstInfo_dtl.setTransaction_id(gstInfoDtlDto.getTransaction_id());
            gstInfo_dtl.setReq_status(gstInfoDtlDto.getReq_status());
            gstInfo_dtl.setUrl_res(gstInfoDtlDto.getUrl_res());
            gstInfo_dtl.setStatus(gstInfoDtlDto.getStatus());
            gstInfo_dtl.setWebhook_res(gstInfoDtlDto.getWebhook_res());
            gstInfo_dtl.setWebhook_status(gstInfoDtlDto.getWebhook_status());
            gstInfo_dtl.setZipfile(gstInfoDtlDto.getZipfile());
            gstInfo_dtl.setXlsfile(gstInfoDtlDto.getXlsfile());
            gstInfo_dtl.setJson_data(gstInfoDtlDto.getJson_data());
            gstInfo_dtl.setDownload_status(gstInfoDtlDto.getDownload_status());
            gstInfo_dtl.setRequest_type(gstInfoDtlDto.getRequest_type());
            gstInfo_dtl.setRemarks(gstInfoDtlDto.getRemarks());
            
        }
        return gstInfo_dtl;
    }
    public static GstInfoDtlDto convertToDto(GstInfo_dtl gstInfo_dtl){
        GstInfoDtlDto gstInfoDtlDto = new GstInfoDtlDto();
        if(gstInfo_dtl != null) {
            gstInfoDtlDto.setUserid(gstInfo_dtl.getUserid());
            gstInfoDtlDto.setTran_dt(gstInfo_dtl.getTran_dt());
            gstInfoDtlDto.setRef_inquiry_id(gstInfo_dtl.getRef_inquiry_id());
            gstInfoDtlDto.setTransaction_id(gstInfo_dtl.getTransaction_id());
            gstInfoDtlDto.setReq_status(gstInfo_dtl.getReq_status());
            gstInfoDtlDto.setUrl_res(gstInfo_dtl.getUrl_res());
            gstInfoDtlDto.setStatus(gstInfo_dtl.getStatus());
            gstInfoDtlDto.setWebhook_res(gstInfo_dtl.getWebhook_res());
            gstInfoDtlDto.setWebhook_status(gstInfo_dtl.getWebhook_status());
            gstInfoDtlDto.setZipfile(gstInfo_dtl.getZipfile());
            gstInfoDtlDto.setXlsfile(gstInfo_dtl.getXlsfile());
            gstInfoDtlDto.setJson_data(gstInfo_dtl.getJson_data());
            gstInfoDtlDto.setDownload_status(gstInfo_dtl.getDownload_status());
            gstInfoDtlDto.setRequest_type(gstInfo_dtl.getRequest_type());
            gstInfoDtlDto.setRemarks(gstInfoDtlDto.getRemarks());
        }
        return gstInfoDtlDto;
    }

    public static List<GstInfoDtlDto> convertAppListToDtoList(List<? extends GstInfo_dtl> AppList) {
        List<GstInfoDtlDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertToDto((GstInfo_dtl) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
