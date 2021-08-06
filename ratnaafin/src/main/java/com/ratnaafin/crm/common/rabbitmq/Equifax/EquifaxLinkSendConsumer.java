package com.ratnaafin.crm.common.rabbitmq.Equifax;

import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.dto.CRMAppDto;
import com.ratnaafin.crm.user.dto.URLConfigDto;
import com.ratnaafin.crm.user.model.EquifaxAPILog;
import com.ratnaafin.crm.user.model.SysParaMst;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

@Component
public class EquifaxLinkSendConsumer {
    @Autowired
    private UserService userService;
    private String g_error_msg = "Something went wrong please try again.";
    private final static String paraError = "error: Parameter value not found for code: <code>";
    private final static String configError = "error: URL configuration value not found for code: <code>";
    private String remarks=null,additionalRemarks=null;

    @RabbitListener(queues = EquifaxRabbitMQConfig.EQFX_LINK_SEND_Q)
    public void equifaxLinkSendQ_Consumer(String payload) throws Exception {
        Utility.print("equifaxLinkSendQ_Consumer");
        JSONObject jsonObject = new JSONObject(payload);
        String tokenID=null,link=null;
        try{
           tokenID = jsonObject.getString("tokenID");
           link    = jsonObject.getString("link");
           sendEquifaxConsentMessage(tokenID,link);
        }catch (Exception e){
            remarks = g_error_msg;
            additionalRemarks = "Error in queue(equifaxLinkSendQ_Consumer):"+e.getMessage()==null?"":e.getMessage();
            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
        }
    }

    public  void sendEquifaxConsentMessage(String tokenID,String shortedLink){
        Utility.print("###-equifaxLinkSendQ_Consumer/sendEquifaxConsentMessage-###");
        Utility.print("tokenID:"+tokenID);
        Utility.print("shortedLink:"+shortedLink);

        String apiActive=null,mobile=null,reqName=null,data=null,messageData=null,status=null,
                apiJsonReq=null,linkSentStatus=null,remarks=null,additionalRemarks=null;

        SysParaMst    sysParaMst;
        EquifaxAPILog equifaxAPILog = userService.findEquifaxDetailByTokenId(tokenID);
        URLConfigDto  urlConfigDto  = null;
        CRMAppDto     crmAppDto     = userService.findAppByID(1);

        mobile  = equifaxAPILog.getMobile();
        reqName = equifaxAPILog.getReq_type();
        status  = equifaxAPILog.getStatus();
        data    = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),mobile);

        //Invoid: Equifax Consent Link send API Active
        sysParaMst = userService.getParaVal("9999","9999",25);
        if(sysParaMst==null){
            apiActive="Y";
        }else {
            apiActive = sysParaMst.getPara_value();
        }
        if(apiActive.equalsIgnoreCase("Y") && status.equalsIgnoreCase("X")){
            Utility.print("sendLinkProcess2");
            int configCD = 0;
            String apiURL=null,apiKey=null,templateID=null;
            int configCDMessageData=0;

            if(reqName.equalsIgnoreCase("retail")){
                //getting user-sending-link
                configCD = 46;
                configCDMessageData = 26;
            }else{
                configCD = 48;
                configCDMessageData = 28;
            }
            urlConfigDto = userService.findURLDtlByID(configCD);
            if(urlConfigDto==null){
                remarks = "Configuration error";
                additionalRemarks = configError.replace("<code>",String.valueOf(configCD));
                userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                return;
            }
            apiURL       = urlConfigDto.getUrl();
            apiKey       = urlConfigDto.getKey();
            templateID	 = urlConfigDto.getSmtp_port();
            if(apiURL==null||apiURL.isEmpty()){
                remarks = "Configuration error";
                additionalRemarks = configError.replace("<code>",String.valueOf(configCD))+"|apiURL";
                userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                return;
            }
            if(apiKey==null||apiKey.isEmpty()){
                remarks = "Configuration error";
                additionalRemarks = configError.replace("<code>",String.valueOf(configCD))+"|apiKey";
                userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                return;
                /*throw new ConfigurationErrorException("UrlConfig",configCD,"apiKey not found");*/
            }
            if(templateID==null||templateID.isEmpty()){
                remarks = "Configuration error";
                additionalRemarks = configError.replace("<code>",String.valueOf(configCD))+"|templateID";
                userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                return;
                /*throw new ConfigurationErrorException("UrlConfig",configCD,"templateID not found");*/
            }
            sysParaMst =  userService.getParaVal("9999","9999",configCDMessageData);
            messageData = sysParaMst.getPara_value();
            if(messageData==null||messageData.trim().isEmpty()){
                remarks = "Configuration error";
                additionalRemarks = paraError.replace("<code>",String.valueOf(configCDMessageData))+"|messageData";
                userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                return;
                /*throw new ConfigurationErrorException("sysParaMst",configCDMessageData,"messageData not found");*/
            }
            messageData  = messageData.replace("<link>",shortedLink);
            Utility.print("customer messageData is:"+messageData);

            try{
                String apiResult = null;
                int httpStatus=0;
                URL obj = new URL(apiURL);
                /*//temporary
                if(apiURL!=null){
                    additionalRemarks = "skip calling";
                    userService.updateEqfxOTPLinkStatus(tokenID,"P","S","test",shortedLink,additionalRemarks);
                    return;
                }*/
                HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

                conn.setRequestMethod("POST");
                conn.addRequestProperty("content-Type", "application/json");
                conn.addRequestProperty("authKey",apiKey);
                conn.setDoOutput(true);
                JSONObject requestJson = new JSONObject();
                requestJson.put("mobile",data);
                requestJson.put("message",messageData);
                requestJson.put("templateId",templateID);
                apiJsonReq = requestJson.toString();
                Utility.print("API request data:\n"+apiJsonReq);
                OutputStream os = conn.getOutputStream();
                os.write(requestJson.toString().getBytes());
                os.flush();
                os.close();
                //get response body of api request
                apiResult = Utility.getURLResponse(conn);
                httpStatus = conn.getResponseCode();
                Utility.print("Link send API response:"+apiResult);
                String responseStatus = null,responseMessage=null,responseTransactionID=null;
                JSONObject jsonObject = null;
                if(httpStatus==conn.HTTP_OK){
                    try {
                        jsonObject = new JSONObject(apiResult);
                        //start: read key values
                        if(jsonObject.has("status")){
                            responseStatus = jsonObject.getString("status");
                        }else{
                            responseStatus = "status_not_found";
                        }
                        if(jsonObject.has("message")){
                            responseMessage = jsonObject.getString("message");
                        }
                        if(jsonObject.has("transactionId")){
                            responseTransactionID = jsonObject.getString("transactionId");
                        }
                        //end: read key values
                        if (responseStatus.equals("200") && responseMessage.equalsIgnoreCase("success")){
                            status = "P";
                            linkSentStatus = "S";
                            remarks = "Link has been sent";
                        }else{
                            status = "F";
                            linkSentStatus = "F";
                            remarks = responseMessage;
                            additionalRemarks = remarks;
                        }
                        userService.updateEqfxOTPLinkStatus(tokenID,status,linkSentStatus,remarks,shortedLink,additionalRemarks);
                    }catch (JSONException e){
                        remarks = g_error_msg;
                        additionalRemarks = httpStatus+":Error while reading api response/"+e.getMessage();
                        userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                        e.printStackTrace();
                    }
                }else{
                    try{
                        jsonObject = new JSONObject(apiResult);
                        //start: read key values
                        if(jsonObject.has("status")){
                            responseStatus = jsonObject.getString("status");
                        }
                        if(jsonObject.has("message")){
                            responseMessage = jsonObject.getString("message");
                        }
                    }catch (Exception e){
                        responseMessage = g_error_msg;
                        additionalRemarks = e.getMessage()+"/"+apiResult.substring(0,2999);
                        e.printStackTrace();
                    }
                    remarks = responseMessage;
                    additionalRemarks = additionalRemarks==null?remarks:additionalRemarks;
                    userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,shortedLink,additionalRemarks);
                }
                //insert api log
                HashMap inParam = new HashMap(),outParam;
                inParam.put("tokenID",tokenID);
                inParam.put("requestType",reqName);
                inParam.put("requestData",apiJsonReq);
                inParam.put("responseData",apiResult);
                inParam.put("transactionID",responseTransactionID);
                inParam.put("messageCategory","03");
                userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);
            }catch (Exception e) {
                remarks = g_error_msg;
                additionalRemarks = "Error in queue(equifaxLinkSendQ_Consumer):"+e.getMessage()==null?"":e.getMessage();
                userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
            }
        }
    }//re-pull
}