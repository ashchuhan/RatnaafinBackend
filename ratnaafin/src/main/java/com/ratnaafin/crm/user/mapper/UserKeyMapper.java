package com.ratnaafin.crm.user.mapper;

import java.util.ArrayList;
import java.util.List;

import com.ratnaafin.crm.user.dto.User_key_detailDto;
import com.ratnaafin.crm.user.model.User_key_detail;

public class UserKeyMapper {
	public static User_key_detail convertDtoToUserKey(User_key_detailDto userkeyDto){
		User_key_detail userkeydetail = new User_key_detail();
		if(userkeyDto != null) {
			userkeydetail.setId(userkeyDto.getId());
			userkeydetail.setUser_id(userkeyDto.getUser_id());
			userkeydetail.setPublic_key(userkeyDto.getPublic_key());
			userkeydetail.setPrivate_key(userkeyDto.getPrivate_key());
			userkeydetail.setIp_add(userkeyDto.getIp_add());
			userkeydetail.setMac_add(userkeyDto.getMac_add());
			userkeydetail.setHost_name(userkeyDto.getHost_name());
			userkeydetail.setOs_name(userkeyDto.getOs_name());
			userkeydetail.setLast_usage_date(userkeyDto.getLast_usage_date());
		}		
		return userkeydetail;
	}	
	public static User_key_detailDto convertUserKeyToDto(User_key_detail userkeydetail){
		User_key_detailDto userkeyDto = new User_key_detailDto();		
		if(userkeydetail != null) {
			userkeyDto.setId(userkeydetail.getId());
			userkeyDto.setUser_id(userkeydetail.getUser_id());
			userkeyDto.setPublic_key(userkeydetail.getPublic_key());
			userkeyDto.setPrivate_key(userkeydetail.getPrivate_key());
			userkeyDto.setIp_add(userkeydetail.getIp_add());
			userkeyDto.setMac_add(userkeydetail.getMac_add());
			userkeyDto.setHost_name(userkeydetail.getHost_name());
			userkeyDto.setOs_name(userkeydetail.getOs_name());
			userkeyDto.setLast_usage_date(userkeydetail.getLast_usage_date());
		}		
		return userkeyDto;		
	}
	
	public static List<User_key_detailDto> convertUserListToDtoList(List<? extends User_key_detail> userList) {
        List<User_key_detailDto> dtoList = null;
        if (userList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < userList.size(); i++) {
                dtoList.add(convertUserKeyToDto((User_key_detail) userList.get(i)));
            }
        }
        return dtoList;
    }
}
