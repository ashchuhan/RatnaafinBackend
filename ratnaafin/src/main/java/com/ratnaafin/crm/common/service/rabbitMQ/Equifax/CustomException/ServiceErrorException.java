package com.ratnaafin.crm.common.service.rabbitMQ.Equifax.CustomException;

public class ServiceErrorException extends  Exception{
    public ServiceErrorException(String errorMessage){
        super(errorMessage);
    }
}
