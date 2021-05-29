package com.ratnaafin.crm.user.mapper;

import java.util.ArrayList;
import java.util.List;

import com.ratnaafin.crm.user.dto.RoleDto;
import com.ratnaafin.crm.user.model.Role;

public class RoleMapper {
	public static Role convertDtoToRole(RoleDto roleDto){
		Role role = new Role();
		
		if(roleDto != null) {
			role.setId(roleDto.getId());
			role.setName(roleDto.getName());
		}
		
		return role;
	}
	
	public static RoleDto convertRoleToDto(Role role){
		RoleDto roleDto = new RoleDto();
		
		if(role != null) {
			roleDto.setId(role.getId());
			roleDto.setName(role.getName());
		}
		
		return roleDto;
		
	}
	
	public static List<RoleDto> convertRoleListToDtoList(List<? extends Role> roleList) {
        List<RoleDto> dtoList = null;
        if (roleList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < roleList.size(); i++) {
                dtoList.add(convertRoleToDto((Role) roleList.get(i)));
            }
        }
        return dtoList;
    }
	
	public static List<Role> convertDtoListToRoleList(List<? extends RoleDto> dtoList) {
        List<Role> roleList = null;
        if (dtoList != null) {
            roleList = new ArrayList<>();
            for (int i = 0; i < dtoList.size(); i++) {
                roleList.add(convertDtoToRole((RoleDto) dtoList.get(i)));
            }
        }
        return roleList;
    }
}
