package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.InquiryMstDto;
import com.ratnaafin.crm.user.model.Inquiry_mst;

import java.util.ArrayList;
import java.util.List;

public class InquiryMstMapper {
    public static Inquiry_mst convertDto(InquiryMstDto inquiryMstDto){
        Inquiry_mst inquiryMst = new Inquiry_mst();
        if(inquiryMstDto != null) {
            inquiryMst.setTran_cd(inquiryMstDto.getTran_cd());
            inquiryMst.setTran_dt(inquiryMstDto.getTran_dt());
            inquiryMst.setCustomer_type(inquiryMstDto.getCustomer_type());
            inquiryMst.setSalutation(inquiryMstDto.getSalutation());
            inquiryMst.setFirst_name(inquiryMstDto.getFirst_name());
            inquiryMst.setMiddle_name(inquiryMstDto.getMiddle_name());
            inquiryMst.setLast_name(inquiryMstDto.getLast_name());
            inquiryMst.setGender(inquiryMstDto.getGender());
            inquiryMst.setBirth_dt(inquiryMstDto.getBirth_dt());
            inquiryMst.setMobile(inquiryMstDto.getMobile());
            inquiryMst.setE_mail_id(inquiryMstDto.getE_mail_id());
            inquiryMst.setLandmark(inquiryMstDto.getLandmark());
            inquiryMst.setPostal_cd(inquiryMstDto.getPostal_cd());
            inquiryMst.setLocation(inquiryMstDto.getLocation());
            inquiryMst.setCity(inquiryMstDto.getCity());
            inquiryMst.setDistrict(inquiryMstDto.getDistrict());
            inquiryMst.setState(inquiryMstDto.getState());
            inquiryMst.setCountry(inquiryMstDto.getCountry());
            inquiryMst.setDesire_loan_amt(inquiryMstDto.getDesire_loan_amt());
            inquiryMst.setSub_product(inquiryMstDto.getSub_product());
            inquiryMst.setNext_action_dt(inquiryMstDto.getNext_action_dt());
            inquiryMst.setStatus(inquiryMstDto.getStatus());
            inquiryMst.setConfirmed(inquiryMstDto.getConfirmed());
            inquiryMst.setDev_or_cont(inquiryMstDto.getDev_or_cont());
            inquiryMst.setEntered_by(inquiryMstDto.getEntered_by());
            inquiryMst.setEntered_date(inquiryMstDto.getEntered_date());
            inquiryMst.setLast_modified_date(inquiryMstDto.getLast_modified_date());
            inquiryMst.setMachine_nm(inquiryMstDto.getMachine_nm());
            inquiryMst.setLast_machine_nm(inquiryMstDto.getLast_machine_nm());
            inquiryMst.setLead_generate(inquiryMstDto.getLead_generate());
            inquiryMst.setFill_que(inquiryMstDto.getFill_que());
            inquiryMstDto.setTeam_lead_id(inquiryMst.getTeam_lead_id());
            inquiryMstDto.setTeam_member_id(inquiryMst.getTeam_member_id());
            inquiryMstDto.setPriority(inquiryMst.getPriority());
        }
        return inquiryMst;
    }
    public static InquiryMstDto convertToDto(Inquiry_mst inquiryMst){
        InquiryMstDto inquiryMstDto = new InquiryMstDto();
        if(inquiryMst != null) {
            inquiryMstDto.setTran_cd(inquiryMst.getTran_cd());
            inquiryMstDto.setTran_dt(inquiryMst.getTran_dt());
            inquiryMstDto.setCustomer_type(inquiryMst.getCustomer_type());
            inquiryMstDto.setSalutation(inquiryMst.getSalutation());
            inquiryMstDto.setFirst_name(inquiryMst.getFirst_name());
            inquiryMstDto.setMiddle_name(inquiryMst.getMiddle_name());
            inquiryMstDto.setLast_name(inquiryMst.getLast_name());
            inquiryMstDto.setGender(inquiryMst.getGender());
            inquiryMstDto.setBirth_dt(inquiryMst.getBirth_dt());
            inquiryMstDto.setMobile(inquiryMst.getMobile());
            inquiryMstDto.setE_mail_id(inquiryMst.getE_mail_id());
            inquiryMstDto.setLandmark(inquiryMst.getLandmark());
            inquiryMstDto.setPostal_cd(inquiryMst.getPostal_cd());
            inquiryMstDto.setLocation(inquiryMst.getLocation());
            inquiryMstDto.setCity(inquiryMst.getCity());
            inquiryMstDto.setDistrict(inquiryMst.getDistrict());
            inquiryMstDto.setState(inquiryMst.getState());
            inquiryMstDto.setCountry(inquiryMst.getCountry());
            inquiryMstDto.setDesire_loan_amt(inquiryMst.getDesire_loan_amt());
            inquiryMstDto.setSub_product(inquiryMst.getSub_product());
            inquiryMstDto.setNext_action_dt(inquiryMst.getNext_action_dt());
            inquiryMstDto.setStatus(inquiryMst.getStatus());
            inquiryMstDto.setConfirmed(inquiryMst.getConfirmed());
            inquiryMstDto.setDev_or_cont(inquiryMst.getDev_or_cont());
            inquiryMstDto.setEntered_by(inquiryMst.getEntered_by());
            inquiryMstDto.setEntered_date(inquiryMst.getEntered_date());
            inquiryMstDto.setLast_modified_date(inquiryMst.getLast_modified_date());
            inquiryMstDto.setMachine_nm(inquiryMst.getMachine_nm());
            inquiryMstDto.setLast_machine_nm(inquiryMst.getLast_machine_nm());
            inquiryMstDto.setLead_generate(inquiryMst.getLead_generate());
            inquiryMstDto.setFill_que(inquiryMst.getFill_que());
        }
        return inquiryMstDto;
    }

    public static List<InquiryMstDto> convertAppListToDtoList(List<? extends Inquiry_mst> AppList) {
        List<InquiryMstDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertToDto((Inquiry_mst) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
