package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.SecurityOtpSmsDto;
import com.ratnaafin.crm.user.model.SecurityOtpSms;

public class SecurityOtpSmsMapper {

    public static SecurityOtpSmsDto convertToDto(SecurityOtpSms securityOtpSms) {
        SecurityOtpSmsDto securityOtpSmsDto = new SecurityOtpSmsDto();
        if(securityOtpSms != null)
        {
            securityOtpSmsDto.setTran_cd(securityOtpSms.getId());
            securityOtpSmsDto.setTran_dt(securityOtpSms.getTran_dt());
            securityOtpSmsDto.setOtp_type(securityOtpSms.getOtp_type());
            securityOtpSmsDto.setMobile_no(securityOtpSms.getMobile_no());
            securityOtpSmsDto.setSms_url(securityOtpSms.getSms_url());
            securityOtpSmsDto.setStatus(securityOtpSms.getStatus());
            securityOtpSmsDto.setSent_time(securityOtpSms.getSent_time());
            securityOtpSmsDto.setApi_reps(securityOtpSms.getApi_reps());
            securityOtpSmsDto.setSend_service_cd(securityOtpSms.getSend_service_cd());
            securityOtpSmsDto.setEmail_id(securityOtpSms.getEmail_id());
            securityOtpSmsDto.setEmail_url(securityOtpSms.getSms_url());
            securityOtpSmsDto.setEmail_status(securityOtpSms.getEmail_status());
            securityOtpSmsDto.setEmail_subject(securityOtpSms.getEmail_subject());
        }
        return securityOtpSmsDto;
    }
}
