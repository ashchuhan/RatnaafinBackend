package com.ratnaafin.crm.common.rabbitmq.Equifax.CustomException;

public class ServiceErrorException extends  Exception{
    public ServiceErrorException(String errorMessage){
        super(errorMessage);
    }
}
