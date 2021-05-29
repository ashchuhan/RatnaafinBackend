package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.OtpApiDtlDto;
import com.ratnaafin.crm.user.model.OtpApiDtl;

public class OtpAPiDtlMapper {

    public static OtpApiDtlDto convertToDto(OtpApiDtl otpApiDtl){
        OtpApiDtlDto otpApiDtlDto = new OtpApiDtlDto();
        if(otpApiDtlDto != null) {
            otpApiDtlDto.setRef_inquiry_id(otpApiDtl.getRef_inquiry_id());
            otpApiDtlDto.setRef_uid(otpApiDtl.getRef_uid());
            otpApiDtlDto.setTran_dt(otpApiDtl.getTran_dt());
            otpApiDtlDto.setTransaction_id(otpApiDtl.getTransaction_id());
            otpApiDtlDto.setOtp_sent_status(otpApiDtl.getOtp_sent_status());
            otpApiDtlDto.setOtp_sent_res_data(otpApiDtl.getOtp_sent_res_data());
            otpApiDtlDto.setOtp_verify(otpApiDtl.getOtp_verify());
            otpApiDtlDto.setOtp_expiry(otpApiDtl.getOtp_expiry());
            otpApiDtlDto.setOtp_length(otpApiDtl.getOtp_length());
            otpApiDtlDto.setOtp_ver_status(otpApiDtl.getOtp_ver_status());
            otpApiDtlDto.setOtp_ver_res_data(otpApiDtl.getOtp_ver_res_data());
        }
        return otpApiDtlDto;
    }
}
