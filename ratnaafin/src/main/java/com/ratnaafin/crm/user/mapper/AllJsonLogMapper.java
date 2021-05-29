package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.AllJsonLogDto;
import com.ratnaafin.crm.user.model.AllJsonLog;

import java.util.ArrayList;
import java.util.List;

public class AllJsonLogMapper {
    public static AllJsonLog convertDto(AllJsonLogDto jsonLogDto){
        AllJsonLog allJsonLog = new AllJsonLog();
        if(jsonLogDto != null) {
            allJsonLog.setId(jsonLogDto.getId());
            allJsonLog.setTran_dt(jsonLogDto.getTran_dt());
            allJsonLog.setAction(jsonLogDto.getAction());
            allJsonLog.setChannel(jsonLogDto.getChannel());
            allJsonLog.setFlag(jsonLogDto.getFlag());
            allJsonLog.setRequest_data(jsonLogDto.getRequest_data());
            allJsonLog.setUnique_id(jsonLogDto.getUnique_id());
            allJsonLog.setModule(jsonLogDto.getModule());
        }
        return allJsonLog;
    }
    public static AllJsonLogDto convertToDto(AllJsonLog allJsonLog){
        AllJsonLogDto jsonLogDto = new AllJsonLogDto();
        if(allJsonLog != null) {
            jsonLogDto.setId(allJsonLog.getId());
            jsonLogDto.setTran_dt(allJsonLog.getTran_dt());
            jsonLogDto.setAction(allJsonLog.getAction());
            jsonLogDto.setChannel(allJsonLog.getChannel());
            jsonLogDto.setFlag(allJsonLog.getFlag());
            jsonLogDto.setRequest_data(allJsonLog.getRequest_data());
            jsonLogDto.setUnique_id(allJsonLog.getUnique_id());
            jsonLogDto.setModule(allJsonLog.getModule());
        }
        return jsonLogDto;
    }

    public static List<AllJsonLogDto> convertAppListToDtoList(List<? extends AllJsonLog> AppList) {
        List<AllJsonLogDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertToDto((AllJsonLog) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
