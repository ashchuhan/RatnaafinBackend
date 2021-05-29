package com.ratnaafin.crm.user.mapper;

import java.util.ArrayList;
import java.util.List;
import com.ratnaafin.crm.user.dto.UserDto;
import com.ratnaafin.crm.user.model.User_master;

public class UserMapper {
	public static User_master convertDtoToUser(UserDto userDto){
		User_master user = new User_master();		
		if(userDto != null) {
			user.setId(userDto.getId());
			user.setAge(userDto.getAge());
			user.setPassword(userDto.getPassword());
			user.setFirst_name(userDto.getFirst_name());
			user.setLast_name(userDto.getLast_name());
			user.setLogin_attempt(userDto.isLogin_attempt());
			user.setIs_active(userDto.isIs_active());
			user.setRole(RoleMapper.convertDtoListToRoleList(userDto.getRole()));
			user.setUser_name(userDto.getUser_name());
			user.setPassword(userDto.getPassword());
			user.setCreate_date(userDto.getCreate_date());
			user.setEmail(userDto.getEmail());
			user.setPhone_no(userDto.getPhone_no());
			user.setUpdate_date(userDto.getUpdate_date());	
			user.setFlag(userDto.getFlag());
            user.setUser_id(userDto.getUser_id());
		}		
		return user;
	}	
	public static UserDto convertUserToDto(User_master user){
		UserDto userDto = new UserDto();		
		if(user != null) {
			userDto.setId(user.getId());
			userDto.setAge(user.getAge());
			//userDto.setPassword(user.getPassword());
			userDto.setFirst_name(user.getFirst_name());
			userDto.setLast_name(user.getLast_name());
			userDto.setLogin_attempt(user.isLogin_attempt());
			userDto.setIs_active(user.isIs_active());
			userDto.setRole(RoleMapper.convertRoleListToDtoList(user.getRole()));
			userDto.setUser_name(user.getUser_name());
			userDto.setCreate_date(user.getCreate_date());
			userDto.setEmail(user.getEmail());
			userDto.setPhone_no(user.getPhone_no());
			userDto.setUpdate_date(user.getUpdate_date());
			userDto.setFlag(user.getFlag());
            userDto.setUser_id(user.getUser_id());
		}		
		return userDto;		
	}
	
	public static List<UserDto> convertUserListToDtoList(List<? extends User_master> userList) {
        List<UserDto> dtoList = null;
        if (userList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < userList.size(); i++) {
                dtoList.add(convertUserToDto((User_master) userList.get(i)));
            }
        }
        return dtoList;
    }
}
