package com.ratnaafin.crm.common.rabbitmq.Equifax;

import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class EquifaxDataSetConsumer {
    @Autowired
    UserService userService;
    @RabbitListener(queues = EquifaxRabbitMQConfig.EQFX_DATA_SET_Q)
    public void equifaxDataSet_QConsumer(String tokenID){
        Utility.print("tokenID:"+tokenID);
        String dbResult=null,dbError=null;
        HashMap inParam = new HashMap(),outParam;
        inParam.put("action","set_credit_score");
        inParam.put("jsonReqData","{\"tokenID\":\""+tokenID+"\"}");
        outParam = userService.callingDBObject("procedure","proc_calling_db_objects",inParam);
        if(outParam.containsKey("result")){
            dbResult = (String)outParam.get("result");
            Utility.print("result:"+dbResult);
        }
        if(outParam.containsKey("error")){
            dbError = (String)outParam.get("error");
            Utility.print("error:"+dbError);
        }
    }
}
