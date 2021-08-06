package com.ratnaafin.crm.common.rabbitmq.Equifax.CustomException;

public class ConfigurationErrorException extends Exception {
    public ConfigurationErrorException(String fromEntity,int configCode,String desc){
        super("Configuration not found in:"+fromEntity.toUpperCase()+" for code: "+configCode+"/Desc: "+desc);
    }//re-pull
}
