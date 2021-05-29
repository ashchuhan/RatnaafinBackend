package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.PancardApiDtlDto;
import com.ratnaafin.crm.user.model.PancardApiDtl;

public class PacardApiDtlMapper {
    public static PancardApiDtlDto convertToDto(PancardApiDtl pancardApiDtl){
        PancardApiDtlDto pancardApiDtlDto = new PancardApiDtlDto();
       /* if(pancardApiDtlDto != null) {
            pancardApiDtlDto.setTran_dt(pancardApiDtl.getTran_dt());
            pancardApiDtlDto.setRef_inquiry_id(pancardApiDtl.getRef_inquiry_id());
            pancardApiDtlDto.setPancard_no(pancardApiDtl.getPancard_no());
            pancardApiDtlDto.setUrl_res(pancardApiDtl.getUrl_res());
            pancardApiDtlDto.setStatus(pancardApiDtl.getStatus());
        }*/
        return pancardApiDtlDto;
    }
}
