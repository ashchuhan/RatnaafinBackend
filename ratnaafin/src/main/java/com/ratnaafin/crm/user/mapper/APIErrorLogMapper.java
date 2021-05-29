package com.ratnaafin.crm.user.mapper;

import com.ratnaafin.crm.user.dto.APIErrorLogDto;
import com.ratnaafin.crm.user.model.APIErrorLog;

import java.util.ArrayList;
import java.util.List;

public class APIErrorLogMapper {
    public static APIErrorLog convertDto(APIErrorLogDto apiErrorLogDto){
        APIErrorLog errorLog = new APIErrorLog();
        if(apiErrorLogDto != null) {
            errorLog.setId(apiErrorLogDto.getId());
            errorLog.setAction(apiErrorLogDto.getAction());
            errorLog.setChannel(apiErrorLogDto.getChannel());
            errorLog.setTran_dt(apiErrorLogDto.getTran_dt());
            errorLog.setError_msg(apiErrorLogDto.getError_msg());
            errorLog.setRequest_data(apiErrorLogDto.getRequest_data());
            errorLog.setModule(apiErrorLogDto.getModule());
        }
        return errorLog;
    }
    public static APIErrorLogDto convertToDto(APIErrorLog errorLog){
        APIErrorLogDto apiErrorLogDto = new APIErrorLogDto();
        if(errorLog != null) {
            apiErrorLogDto.setId(errorLog.getId());
            apiErrorLogDto.setTran_dt(errorLog.getTran_dt());
            apiErrorLogDto.setAction(errorLog.getAction());
            apiErrorLogDto.setChannel(errorLog.getChannel());
            apiErrorLogDto.setError_msg(errorLog.getError_msg());
            apiErrorLogDto.setRequest_data(errorLog.getRequest_data());
            apiErrorLogDto.setModule(errorLog.getModule());
        }
        return apiErrorLogDto;
    }

    public static List<APIErrorLogDto> convertAppListToDtoList(List<? extends APIErrorLog> AppList) {
        List<APIErrorLogDto> dtoList = null;
        if (AppList != null) {
            dtoList = new ArrayList<>();
            for (int i = 0; i < AppList.size(); i++) {
                dtoList.add(convertToDto((APIErrorLog) AppList.get(i)));
            }
        }
        return dtoList;
    }
}
