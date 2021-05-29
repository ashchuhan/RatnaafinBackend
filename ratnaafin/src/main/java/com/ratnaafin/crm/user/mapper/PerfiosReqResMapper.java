package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.PerfiosReqResDto;
import com.ratnaafin.crm.user.model.PerfiosReqResDtl;
import java.util.ArrayList;
import java.util.List;

public class PerfiosReqResMapper {
    public static PerfiosReqResDtl convertDto(PerfiosReqResDto perfiosReqResDto){
        PerfiosReqResDtl  perfiosReqResDtl    = new PerfiosReqResDtl();
        if(perfiosReqResDto != null) {
            perfiosReqResDtl.setUserid(perfiosReqResDto.getUserid());
            perfiosReqResDtl.setTran_dt(perfiosReqResDto.getTran_dt());
            perfiosReqResDtl.setRef_tran_cd(perfiosReqResDto.getRef_tran_cd());
            perfiosReqResDtl.setRef_sr_cd(perfiosReqResDto.getRef_sr_cd());
            perfiosReqResDtl.setTransaction_id(perfiosReqResDto.getTransaction_id());
            perfiosReqResDtl.setReq_status(perfiosReqResDto.getReq_status());
            perfiosReqResDtl.setUrl_res(perfiosReqResDto.getUrl_res());
            perfiosReqResDtl.setStatus(perfiosReqResDto.getStatus());
            perfiosReqResDtl.setWebhook_res(perfiosReqResDto.getWebhook_res());
            perfiosReqResDtl.setWebhook_status(perfiosReqResDto.getWebhook_status());
            perfiosReqResDtl.setZipfile(perfiosReqResDto.getZipfile());
            perfiosReqResDtl.setXlsfile(perfiosReqResDto.getXlsfile());
            perfiosReqResDtl.setJson_data(perfiosReqResDto.getJson_data());
            perfiosReqResDtl.setDownload_status(perfiosReqResDto.getDownload_status());
            perfiosReqResDtl.setRequest_type(perfiosReqResDto.getRequest_type());
            perfiosReqResDtl.setRemarks(perfiosReqResDto.getRemarks());
            perfiosReqResDtl.setInitiated_req(perfiosReqResDto.getInitiated_req());
            perfiosReqResDtl.setEntity_type(perfiosReqResDto.getEntity_type());
            perfiosReqResDtl.setBank_line_id(perfiosReqResDto.getBank_line_id());
            perfiosReqResDtl.setLast_update_dt(perfiosReqResDto.getLast_update_dt());
        }
        return perfiosReqResDtl;
    }
    public static PerfiosReqResDto convertToDto(PerfiosReqResDtl perfiosReqResDtl){
        PerfiosReqResDto perfiosReqResDto = new PerfiosReqResDto();
        if(perfiosReqResDtl != null) {
            perfiosReqResDto.setUserid(perfiosReqResDtl.getUserid());
            perfiosReqResDto.setTran_dt(perfiosReqResDtl.getTran_dt());
            perfiosReqResDto.setRef_tran_cd(perfiosReqResDtl.getRef_tran_cd());
            perfiosReqResDto.setRef_sr_cd(perfiosReqResDtl.getRef_sr_cd());
            perfiosReqResDto.setTransaction_id(perfiosReqResDtl.getTransaction_id());
            perfiosReqResDto.setReq_status(perfiosReqResDtl.getReq_status());
            perfiosReqResDto.setUrl_res(perfiosReqResDtl.getUrl_res());
            perfiosReqResDto.setStatus(perfiosReqResDtl.getStatus());
            perfiosReqResDto.setWebhook_res(perfiosReqResDtl.getWebhook_res());
            perfiosReqResDto.setWebhook_status(perfiosReqResDtl.getWebhook_status());
            perfiosReqResDto.setZipfile(perfiosReqResDtl.getZipfile());
            perfiosReqResDto.setXlsfile(perfiosReqResDtl.getXlsfile());
            perfiosReqResDto.setJson_data(perfiosReqResDtl.getJson_data());
            perfiosReqResDto.setDownload_status(perfiosReqResDtl.getDownload_status());
            perfiosReqResDto.setRequest_type(perfiosReqResDtl.getRequest_type());
            perfiosReqResDto.setRemarks(perfiosReqResDtl.getRemarks());
            perfiosReqResDto.setInitiated_req(perfiosReqResDtl.getInitiated_req());
            perfiosReqResDto.setEntity_type(perfiosReqResDtl.getEntity_type());
            perfiosReqResDto.setBank_line_id(perfiosReqResDtl.getBank_line_id());
            perfiosReqResDto.setLast_update_dt(perfiosReqResDtl.getLast_update_dt());

        }
        return perfiosReqResDto;
    }

    public static List<PerfiosReqResDto> convertAppListToDtoList(List<? extends PerfiosReqResDtl> AppList) {
        List<PerfiosReqResDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertToDto((PerfiosReqResDtl) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
