package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.RoleDto;
import com.ratnaafin.crm.user.dto.URLConfigDto;
import com.ratnaafin.crm.user.model.Role;
import com.ratnaafin.crm.user.model.URLConfig;

import java.util.ArrayList;
import java.util.List;

public class URLConfigMapper {
    public static URLConfig convertDto(URLConfigDto urlConfigDto){
        URLConfig urlConfig = new URLConfig();
        if(urlConfigDto != null) {
            urlConfig.setId(urlConfigDto.getId());
            urlConfig.setUserid(urlConfigDto.getUserid());
            urlConfig.setUrl(urlConfigDto.getUrl());
            urlConfig.setKey(urlConfigDto.getKey());
            urlConfig.setLanguage(urlConfigDto.getLanguage());
            urlConfig.setCountry_cd(urlConfigDto.getCountry_cd());
            urlConfig.setActive(urlConfigDto.getActive());
            urlConfig.setExpiry_dt(urlConfigDto.getExpiry_dt());
            urlConfig.setSmtp_server(urlConfigDto.getSmtp_server());
            urlConfig.setSmtp_port(urlConfigDto.getSmtp_port());
        }
        return urlConfig;
    }

    public static URLConfigDto convertToDto(URLConfig urlConfig){
        URLConfigDto urlConfigDto = new URLConfigDto();
        if(urlConfig != null) {
            urlConfigDto.setId(urlConfig.getId());
            urlConfigDto.setUserid(urlConfig.getUserid());
            urlConfigDto.setUrl(urlConfig.getUrl());
            urlConfigDto.setKey(urlConfig.getKey());
            urlConfigDto.setLanguage(urlConfig.getLanguage());
            urlConfigDto.setCountry_cd(urlConfig.getCountry_cd());
            urlConfigDto.setActive(urlConfig.getActive());
            urlConfigDto.setExpiry_dt(urlConfig.getExpiry_dt());
            urlConfigDto.setSmtp_server(urlConfig.getSmtp_server());
            urlConfigDto.setSmtp_port(urlConfig.getSmtp_port());
        }
        return urlConfigDto;
    }

    public static List<URLConfigDto> convertListToDto(List<? extends URLConfig> roleList) {
        List<URLConfigDto> dtoList = null;
        if (roleList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < roleList.size(); i++) {
                dtoList.add(convertToDto((URLConfig) roleList.get(i)));
            }
        }
        return dtoList;
    }

    public static List<URLConfig> convertDtoToList(List<? extends URLConfigDto> dtoList) {
        List<URLConfig> roleList = null;
        if (dtoList != null) {
            roleList = new ArrayList<>();
            for (int i = 0; i < dtoList.size(); i++) {
                roleList.add(convertDto((URLConfigDto) dtoList.get(i)));
            }
        }
        return roleList;
    }

}
