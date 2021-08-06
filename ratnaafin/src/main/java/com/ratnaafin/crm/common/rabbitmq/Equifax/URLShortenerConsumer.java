package com.ratnaafin.crm.common.rabbitmq.Equifax;

import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.model.EquifaxAPILog;
import com.ratnaafin.crm.user.model.SysParaMst;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONObject;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class URLShortenerConsumer {
    @Autowired
    private UserService userService;
    @Autowired
    private AmqpTemplate amqpTemplate;
    private String g_error_msg = "Something went wrong please try again.";
    private String paraError = "error: Parameter value not found for code: <code>";
    private String configError = "error: URL configuration value not found for code: <code>";

    @RabbitListener(queues = EquifaxRabbitMQConfig.URL_SHORTENER_QUEUE)
    public void urlShortener_QConsumer(String payload) throws Exception {
       Utility.print("urlShortener_QConsumer received request");
       JSONObject jsonObject = new JSONObject(payload),jsonObjectSend=new JSONObject();
       String requestFor = null;
       requestFor = jsonObject.getString("requestFor");
       Utility.print("urlShortener_QConsumer data1:"+requestFor);

       if(requestFor.equalsIgnoreCase("equifax")){
           String tokenID = null, shortURL=null,remarks=null,additionalRemarks=null;
           tokenID =  jsonObject.getString("tokenID");
           Utility.print("urlShortener_QConsumer data2:"+tokenID);
           try{
               SysParaMst sysParaMst;
               EquifaxAPILog equifaxAPILog = userService.findEquifaxDetailByTokenId(tokenID);
               int configCD = 203;
               String internalLink=null;
               sysParaMst = userService.getParaVal("9999","9999",configCD);
               if(sysParaMst==null){
                   remarks = "Missing configuration";
                   additionalRemarks = paraError.replace("<code>",String.valueOf(configCD));
                   userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                   return;
                   /*throw new ConfigurationErrorException("sysParaMst",configCD,"Internal Link");*/
               }
               internalLink = sysParaMst.getPara_value();
               if(internalLink==null||internalLink.trim().isEmpty()){
                   remarks = "Missing configuration";
                   additionalRemarks = paraError.replace("<code>",String.valueOf(configCD));
                   userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                   return;
                   /*throw new ConfigurationErrorException("sysParaMst",configCD,"Internal Link");*/
               }else{
                   internalLink = internalLink.replace("<tokenID>",tokenID);
                   Utility.print("actual internal link:"+internalLink);
               }
               shortURL = userService.getShortURL(tokenID,internalLink);
               if(shortURL.equals("0")){
                   Utility.print("Error in Service:URL shortener");
                   remarks = "Error in URL shortener service";
                   additionalRemarks = remarks;
                   userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
                   return;
                   /*throw new ServiceErrorException("URL Shortener API failed to execute");*/
               }
               try{
                   jsonObjectSend.put("tokenID",tokenID);
                   jsonObjectSend.put("link",shortURL);
                   amqpTemplate.convertAndSend(EquifaxRabbitMQConfig.EQFX_LINK_SEND_AGENT,EquifaxRabbitMQConfig.EQFX_LINK_SEND_KEY,jsonObjectSend.toString());
                }catch (Exception e){
                   remarks = g_error_msg;
                   additionalRemarks = "Error in queue(urlShortener_QConsumer)"+e.getMessage()==null?"":e.getMessage();
                   userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
               }
//               userService.updateEqfxOTPLinkStatus(tokenID,"P","P","Link is ready to send",shortURL);
           }catch (Exception e){
               remarks = g_error_msg;
               additionalRemarks = "Error in queue(urlShortener_QConsumer)"+e.getMessage()==null?"":e.getMessage();
               userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,null,additionalRemarks);
               e.printStackTrace();
           }
       }
    }
}//re-pull kl
