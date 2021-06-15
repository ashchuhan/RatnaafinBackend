package com.ratnaafin.crm.common.scheduler;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.dto.CRMAppDto;
import com.ratnaafin.crm.user.dto.URLConfigDto;
import com.ratnaafin.crm.user.model.EquifaxAPILog;
import com.ratnaafin.crm.user.model.OtpVerificationDtl;
import com.ratnaafin.crm.user.model.SysParaMst;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class Scheduler {
    Utility utility = new Utility();
    public static final String CAM_FILE_PATH = Utility.LOCAL_PATH+Utility.SEPERATOR+"CAM"+Utility.SEPERATOR;

    public static String CAM_DATA = "N";

    //for logger name
    private static String logger = "schedulers";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Scheduled(fixedDelay = 120000, initialDelay = 300000)
    public void fixedDelaySch() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//        Date now = new Date();
//        String strDate = sdf.format(now);
        String JsonData = null;
        JsonData = funcGetIntiatedRequestData();
        if (JsonData != null) {
            long leadId , serialNo;
            String enteredBy = null;
            try {
                BrowserContext context;
                Playwright playwright = Playwright.create();
                Browser browser = playwright.chromium().launch();
                context = browser.newContext();
                Page page = context.newPage();
                JSONArray jsonArray = new JSONArray(JsonData);
                for (int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    leadId = jsonObject.getLong("refID");
                    serialNo = jsonObject.getLong("serialNo");
                    enteredBy = jsonObject.getString("enteredBy");
                    if (leadId > 0  && serialNo > 0){
                        try {
                            page.navigate("https://ratnaafin.aiplservices.com/middleware/lead/"+leadId);
                            page.waitForTimeout(29000);
                            Page.PdfOptions options = new Page.PdfOptions();
                            options.format="A4";
                            options.displayHeaderFooter = true;
                            options.printBackground = true;
                            //options.withDisplayHeaderFooter(true);
                            //options.withPath(Paths.get("page.pdf"));
                            String fileName = CAM_FILE_PATH+userService.getuniqueId()+"_Lead_"+leadId+"_CAM.pdf";
                            File filepath = new File(fileName);
                            FileInputStream fileInputStream = null;
                            //options.setPath(Paths.get(filepath.getAbsolutePath()));
                            //options.withPath(Paths.get(filepath.getAbsolutePath()));
                            //System.out.println("File Path:"+filepath.getAbsolutePath());
                            options.path = Paths.get(filepath.getAbsolutePath());
                            page.pdf(options);
                            fileInputStream = new FileInputStream(filepath);
                            Blob blob = null;
                            Utility utility = new Utility();
                            blob = utility.getBlobData(fileInputStream);
                            if (blob != null) {
                                userService.updateCAMStatus(serialNo,leadId,blob,new Date(),"S",enteredBy);
                            }else {
                                userService.updateCAMStatus(serialNo,leadId,blob,new Date(),"F",enteredBy);
                            }
                            if(filepath.delete()) {
                                System.out.println("File Deleted");
                            }else{
                                filepath.deleteOnExit();
                                System.out.println("File not Deleted");
                            }
                        }catch (Exception e){
                            System.out.println("Internal Exception"+e.getMessage()+" cause "+e.getCause().getMessage());
                        }
                    }
                }
                page.close();
                context.close();
            }catch (JSONException e){
                System.out.println("JSONException"+e.getMessage());
            }catch (Exception e){
                System.out.println("Outer Exception"+e.getMessage()+" cause "+e.getCause().getMessage());
            }
            //System.out.println("End task" + new Date());
        }
        System.gc();
    }

    public String funcGetIntiatedRequestData(){
        Connection connection = null;
        CallableStatement cs = null;
        String ls_return = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            cs = connection.prepareCall("{ call PACK_CAM.proc_initiated_cam_for_pdf_record(?,?) }");
            cs.registerOutParameter(1, Types.VARCHAR);
            cs.registerOutParameter(2, 2005);
            cs.execute();
            CAM_DATA = cs.getString(1);
            final Clob clob_data = cs.getClob(2);
            if (clob_data != null){
                ls_return = clob_data.getSubString(1L, (int) clob_data.length());
            }
            cs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException ex2) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex3) {
                }
            }
        }
        if (cs != null) {
            try {
                cs.close();
            } catch (SQLException ex4) {
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex5) {
            }
        }
        return ls_return;//hello
    }
    /*
    public String funcGenerateCAM(long leadID,long serialNo,long enteredBy) {
        try {
            BrowserContext context;
            Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch();
            context = browser.newContext();
            Page page = context.newPage();
            page.navigate("http://whatsmyuseragent.org/");
            page.waitForTimeout(50000);
            Page.PdfOptions options = new Page.PdfOptions();
            options.format="A4";
            options.displayHeaderFooter = true;
            //options.withDisplayHeaderFooter(true);
            //options.withPath(Paths.get("page.pdf"));
            String fileName = userService.getuniqueId()+"_Lead_"+leadId+"_CAM.pdf";
            File filepath = new File(fileName);
            FileInputStream fileInputStream = null;
            //options.setPath(Paths.get(filepath.getAbsolutePath()));
            //options.withPath(Paths.get(filepath.getAbsolutePath()));
            System.out.println("File Path:"+filepath.getAbsolutePath());
            options.path = Paths.get(filepath.getAbsolutePath());
            page.pdf(options);
            page.close();
            fileInputStream = new FileInputStream(filepath);
            Blob blob = null;
            Utility utility = new Utility();
            blob = utility.getBlobData(fileInputStream);
            if (blob != null) {
                userService.updateCAMStatus(serialNo,leadId,blob,new Date(),"S");
            }else {
                userService.updateCAMStatus(serialNo,leadId,blob,new Date(),"F");
            }
            if(filepath.delete()) {
                System.out.println("File Deleted");
            }else{
                filepath.deleteOnExit();
                System.out.println("File not Deleted");
            }
        }catch (Exception e){
            System.out.println("12:"+e.getMessage());
            return userService.getJsonError("-99","Error!",g_error_msg,"Error: "+e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
        return "success";
    }*/

    //added by sanjay on date: 11/06/2021
    //change: sending sms/email and equifax consent link to customer
    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public  void equifaxConsentMessageScheduler(){
        //utility.generateLog("info","start: equifaxConsentMessageScheduler",logger);
        String shortURL=null;
        String objectName = this.getClass().getSimpleName()+".java",errorFlag=null,errorMessage=null,errorRemarks=null;
        HashMap inParam=null,outParam=null;

        String apiActive=null,tokenID=null,mobile=null,entityType=null,reqName=null,data=null,messageRtl=null,messageComm=null,
                internalLink = null,apiJsonReq=null,linkSentStatus=null,remarks=null;

        List<EquifaxAPILog> rows = userService.findEquifaxPendingLinkRecord();
        CRMAppDto crmAppDto = userService.findAppByID(1);
        //Invoid: Eqfx Consent Link send API Active
        SysParaMst sysParaMst_sms_link_active = userService.getParaVal("9999","9999",25);
        SysParaMst sysParaMst_link_detail_rtl = null, sysParaMst_link_detail_comm = null,sysParaMst_internal_link=null;
        sysParaMst_internal_link = userService.getParaVal("9999","9999",203);
        internalLink = sysParaMst_internal_link.getPara_value();
        if(sysParaMst_sms_link_active==null){
            apiActive="Y";
        }else {
            apiActive = sysParaMst_sms_link_active.getPara_value();
            apiActive = apiActive == null ? "N" : apiActive;
        }
        Utility.print("(equifax)total rows:"+rows.size());
        Utility.print("(equifax)link send api flag:"+apiActive);

        if(rows.size()>=0 && apiActive.equals("Y")){
            String apiURLRtl=null,apiURLComm=null,apiKeyRtl=null,apiKeyComm=null,templateIDRtl=null,templateIDComm=null;
            int configCDRtl = 46,configCDComm = 48;

            sysParaMst_link_detail_rtl = userService.getParaVal("9999","9999",26);
            messageRtl = sysParaMst_link_detail_rtl.getPara_value();
            Utility.print("messageRtl: "+messageRtl);
            sysParaMst_link_detail_comm = userService.getParaVal("9999","9999",28);
            messageComm = sysParaMst_link_detail_comm.getPara_value();
            Utility.print("messageComm: "+messageComm);


            URLConfigDto urlConfigDtoRtl  = userService.findURLDtlByID(configCDRtl);
            apiURLRtl       = urlConfigDtoRtl.getUrl();
            apiKeyRtl       = urlConfigDtoRtl.getKey();
            templateIDRtl	= urlConfigDtoRtl.getSmtp_port();

            URLConfigDto urlConfigDtoComm = userService.findURLDtlByID(configCDComm);
            apiURLComm      = urlConfigDtoComm.getUrl();
            apiKeyComm      = urlConfigDtoComm.getKey();
            templateIDComm	= urlConfigDtoComm.getSmtp_port();

            for(EquifaxAPILog equifaxAPILog:rows){
                tokenID     = equifaxAPILog.getToken_id();
                mobile      = equifaxAPILog.getLink_sent_mobile();
                entityType  = equifaxAPILog.getEntity_type();
                reqName     = equifaxAPILog.getReq_type();

                //decrypt data
                data = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),mobile);
                Utility.print("for:"+reqName);
                Utility.print("data:"+data);

                //send link
                if(reqName.equals("RETAIL")){
                    //link detail <link string>
                    internalLink = internalLink.replace("<tokenID>",tokenID);
                    shortURL = userService.getShortURL(tokenID,internalLink);
                    if(shortURL.equals("0")){
                        continue;
                    }
                    messageRtl = messageRtl.replace("<link>",shortURL);
                    Utility.print("message will be:\n"+messageRtl);
                    try {
                        if(apiURLRtl==null|| apiURLRtl.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL not found for CODE("+configCDRtl+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(apiKeyRtl==null|| apiKeyRtl.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL key found for CODE("+configCDRtl+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(templateIDRtl==null|| templateIDRtl.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL Required templateId not found for CODE("+configCDRtl+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        try{
                            String apiResult = null;
                            int httpStatus=0;
                            URL obj = new URL(apiURLRtl);
                            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

                            conn.setRequestMethod("POST");
                            conn.addRequestProperty("content-Type", "application/json");
                            conn.addRequestProperty("authKey",apiKeyRtl);
                            conn.setDoOutput(true);
                            JSONObject requestJson = new JSONObject();
                            requestJson.put("mobile",data);
                            requestJson.put("message",messageRtl);
                            requestJson.put("templateId",templateIDRtl);
                            apiJsonReq = requestJson.toString();
                            Utility.print("API request data:\n"+apiJsonReq);
                            OutputStream os = conn.getOutputStream();
                            os.write(requestJson.toString().getBytes());
                            os.flush();
                            os.close();
                            //get response body of api request
                            apiResult = Utility.getURLResponse(conn);
                            httpStatus = conn.getResponseCode();
                            Utility.print("API response:"+apiResult);
                            if(httpStatus==conn.HTTP_OK){
                                String responseStatus = null,responseMessage=null,responseTransactionID=null;
                                try {
                                    JSONObject jsonObject = new JSONObject(apiResult);
                                    //start: read key values
                                    if(jsonObject.has("status")){
                                        responseStatus = jsonObject.getString("status");
                                    }else{
                                        responseStatus = "Status Not found";
                                    }
                                    if(jsonObject.has("message")){
                                        responseMessage = jsonObject.getString("message");
                                    }
                                    if(jsonObject.has("transactionId")){
                                        responseTransactionID = jsonObject.getString("transactionId");
                                    }
                                    //end: read key values
                                    //insert api log
                                    inParam = new HashMap();
                                    inParam.put("tokenID",tokenID);
                                    inParam.put("requestType",reqName);
                                    inParam.put("requestData",apiJsonReq);
                                    inParam.put("responseData",apiResult);
                                    inParam.put("transactionID",responseTransactionID);
                                    inParam.put("messageCategory","03");
                                    outParam = userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);

                                    if (responseStatus.equals("200") && responseMessage.equalsIgnoreCase("success")){
                                        linkSentStatus = "S";
                                    }else{
                                        linkSentStatus = "F";
                                        remarks = responseMessage;
                                    }
                                    //Update "otp_verification_dtl"
                                    userService.updateEqfxOTPLinkStatus(tokenID,linkSentStatus,remarks);
                                }catch (JSONException e){
                                    errorFlag = "E";
                                    errorMessage = "JSONException:"+e.getMessage();
                                    errorRemarks = errorMessage;
                                    inParam = new HashMap();
                                    inParam.put("error_msg",errorMessage);
                                    inParam.put("remarks",errorRemarks);
                                    inParam.put("obj_name",objectName);
                                    inParam.put("error_flag",errorFlag);
                                    outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                    e.printStackTrace();
                                    continue;
                                }
                            }else{
                                errorFlag = "U";
                                errorMessage = "API HTTP Status:"+httpStatus;
                                errorRemarks = errorMessage;
                                inParam = new HashMap();
                                inParam.put("error_msg",errorMessage);
                                inParam.put("remarks",errorRemarks);
                                inParam.put("obj_name",objectName);
                                inParam.put("error_flag",errorFlag);
                                outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                continue;
                            }
                        }catch (Exception e) {
                            Utility.print("API Calling failed:"+e.getMessage());
                            errorFlag = "E";
                            errorMessage = "Exception:"+e.getMessage();
                            errorRemarks = "Error while calling SMS send API";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            e.printStackTrace();
                            continue;
                        }
                    }catch (Exception e) {
                        errorFlag = "E";
                        errorMessage = "Exception:"+e.getMessage();
                        errorRemarks = "Error while getting URL detail(s) for SMS send API";
                        inParam = new HashMap();
                        inParam.put("error_msg",errorMessage);
                        inParam.put("remarks",errorRemarks);
                        inParam.put("obj_name",objectName);
                        inParam.put("error_flag",errorFlag);
                        outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                        continue;
                    }
                }
                else if(reqName.equals("COMMERCIAL"))
                {
                    //link detail <link string>
                    internalLink = internalLink.replace("<tokenID>",tokenID);
                    shortURL = userService.getShortURL(tokenID,internalLink);
                    if(shortURL.equals("0")){
                        continue;
                    }
                    messageComm = messageComm.replace("<link>",shortURL);
                    Utility.print("message will be:\n"+messageComm);
                    try {
                        if(apiURLComm==null|| apiURLComm.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL not found for CODE("+configCDComm+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(apiKeyComm==null|| apiKeyComm.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL key found for CODE("+configCDComm+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(templateIDComm==null|| templateIDComm.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL Required templateId not found for CODE("+configCDComm+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        try{
                            String apiResult = null;
                            int httpStatus=0;
                            URL obj = new URL(apiURLComm);
                            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

                            conn.setRequestMethod("POST");
                            conn.addRequestProperty("content-Type", "application/json");
                            conn.addRequestProperty("authKey",apiKeyComm);
                            conn.setDoOutput(true);
                            JSONObject requestJson = new JSONObject();
                            requestJson.put("mobile",data);
                            requestJson.put("message",messageComm);
                            requestJson.put("templateId",templateIDComm);
                            apiJsonReq = requestJson.toString();
                            Utility.print("API request data:\n"+apiJsonReq);
                            OutputStream os = conn.getOutputStream();
                            os.write(requestJson.toString().getBytes());
                            os.flush();
                            os.close();
                            //get response body of api request
                            apiResult = Utility.getURLResponse(conn);
                            httpStatus = conn.getResponseCode();
                            Utility.print("API response:"+apiResult);
                            if(httpStatus==conn.HTTP_OK){
                                String responseStatus = null,responseMessage=null,responseTransactionID=null;
                                try {
                                    JSONObject jsonObject = new JSONObject(apiResult);
                                    //start: read key values
                                    if(jsonObject.has("status")){
                                        responseStatus = jsonObject.getString("status");
                                    }else{
                                        responseStatus = "Status Not found";
                                    }
                                    if(jsonObject.has("message")){
                                        responseMessage = jsonObject.getString("message");
                                    }
                                    if(jsonObject.has("transactionId")){
                                        responseTransactionID = jsonObject.getString("transactionId");
                                    }
                                    //end: read key values
                                    //insert api log
                                    inParam = new HashMap();
                                    inParam.put("tokenID",tokenID);
                                    inParam.put("requestType",reqName);
                                    inParam.put("requestData",apiJsonReq);
                                    inParam.put("responseData",apiResult);
                                    inParam.put("transactionID",responseTransactionID);
                                    inParam.put("messageCategory","04");
                                    outParam = userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);

                                    if (responseStatus.equals("200") && responseMessage.equalsIgnoreCase("success")){
                                        linkSentStatus = "S";
                                    }else{
                                        linkSentStatus = "F";
                                        remarks = responseMessage;
                                    }
                                    //Update "otp_verification_dtl"
                                    userService.updateEqfxOTPLinkStatus(tokenID,linkSentStatus,remarks);
                                }catch (JSONException e){
                                    errorFlag = "E";
                                    errorMessage = "JSONException:"+e.getMessage();
                                    errorRemarks = errorMessage;
                                    inParam = new HashMap();
                                    inParam.put("error_msg",errorMessage);
                                    inParam.put("remarks",errorRemarks);
                                    inParam.put("obj_name",objectName);
                                    inParam.put("error_flag",errorFlag);
                                    outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                    e.printStackTrace();
                                    continue;
                                }
                            }else{
                                errorFlag = "U";
                                errorMessage = "API HTTP Status:"+httpStatus;
                                errorRemarks = errorMessage;
                                inParam = new HashMap();
                                inParam.put("error_msg",errorMessage);
                                inParam.put("remarks",errorRemarks);
                                inParam.put("obj_name",objectName);
                                inParam.put("error_flag",errorFlag);
                                outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                continue;
                            }
                        }catch (Exception e) {
                            Utility.print("API Calling failed:"+e.getMessage());
                            errorFlag = "E";
                            errorMessage = "Exception:"+e.getMessage();
                            errorRemarks = "Error while calling SMS send API";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            e.printStackTrace();
                            continue;
                        }
                    }catch (Exception e) {
                        errorFlag = "E";
                        errorMessage = "Exception:"+e.getMessage();
                        errorRemarks = "Error while getting URL detail(s) for SMS send API";
                        inParam = new HashMap();
                        inParam.put("error_msg",errorMessage);
                        inParam.put("remarks",errorRemarks);
                        inParam.put("obj_name",objectName);
                        inParam.put("error_flag",errorFlag);
                        outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                        continue;
                    }
                }

            }//end for loop
        }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public void smsEmailSendScheduler(){
        //utility.generateLog("info","start:smsEmailSendScheduler",logger);
        String shortURL=null;
        String objectName = this.getClass().getSimpleName()+".java",errorFlag=null,errorMessage=null,errorRemarks=null;
        HashMap inParam=null,outParam=null;
        List<OtpVerificationDtl> rows = userService.findPendingOTPLinkDetail();
        CRMAppDto crmAppDto = userService.findAppByID(1);
        String mobile=null,email=null,requestType=null,tokenID=null,data=null,apiActive=null,message=null,internalLink=null;
        String apiJsonReq=null,linkSentStatus = null,remarks=null;
        Utility.print("row:"+rows.size());
        //utility.generateLog("info","rows("+rows.size()+") fetch by findPendingOTPLinkDetail",logger);
        if(rows.size()>=0){
            for(OtpVerificationDtl otpVerificationDtl:rows){
                requestType = otpVerificationDtl.getReq_type();
                tokenID     = otpVerificationDtl.getToken_id();
                mobile      = otpVerificationDtl.getOtp_sent_mobile();
                email       = otpVerificationDtl.getOtp_sent_email();
                data        = mobile;//requestType.equals("EMAIL")?email:mobile;
                //decrypt data
                data = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),data);
                Utility.print(requestType+":"+data);
                //send link
                if(requestType.equals("SMS") && !mobile.isEmpty()){
                    //send sms on mobile for verification
                    //invoid mobile sms link active
                    SysParaMst sysParaMst_sms_link_active = userService.getParaVal("9999","9999",23);
                    SysParaMst sysParaMst_link_detail = null, sysParaMst_internal_link=null;
                    if(sysParaMst_sms_link_active==null){
                        apiActive="Y";
                    }else {
                        apiActive = sysParaMst_sms_link_active.getPara_value();
                        apiActive = apiActive == null ? "N" : apiActive;
                    }
                    if(apiActive.equals("N")){
                        continue;
                    }
                    //link detail <message string>
                    sysParaMst_link_detail = userService.getParaVal("9999","9999",22);
                    message = sysParaMst_link_detail.getPara_value();
                    //link detail <link string>
                    sysParaMst_internal_link = userService.getParaVal("9999","9999",201);
                    internalLink = sysParaMst_internal_link.getPara_value();
                    internalLink = internalLink.replace("<tokenID>",tokenID);
                    shortURL = userService.getShortURL(tokenID,internalLink);
                    if(shortURL.equals("0")){
                        continue;
                    }
                    message = message.replace("<link>",shortURL);
                    Utility.print("mobile message will be:\n"+message);
                    try {
                        String apiURL=null,apiKey=null,templateID=null;
                        int configCD = 45; //Invoid: sms send API (for OTP)

                        URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
                        apiURL = urlConfigDto.getUrl();
                        apiKey = urlConfigDto.getKey();
                        templateID	= urlConfigDto.getSmtp_port();
                        if(apiURL==null|| apiURL.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL not found for CODE("+configCD+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(apiKey==null|| apiKey.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL key found for CODE("+configCD+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(templateID==null|| templateID.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL Required templateId not found for CODE("+configCD+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        try{
                            String apiResult = null;
                            int httpStatus=0;
                            URL obj = new URL(apiURL);
                            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

                            conn.setRequestMethod("POST");
                            conn.addRequestProperty("content-Type", "application/json");
                            conn.addRequestProperty("authKey",apiKey);
                            conn.setDoOutput(true);
                            JSONObject requestJson = new JSONObject();
                            requestJson.put("mobile",data);
                            requestJson.put("message",message);
                            requestJson.put("templateId",templateID);
                            apiJsonReq = requestJson.toString();
                            Utility.print("Mobile verification sms API request data:\n"+apiJsonReq);
                            OutputStream os = conn.getOutputStream();
                            os.write(requestJson.toString().getBytes());
                            os.flush();
                            os.close();
                            //get response body of api request
                            apiResult = Utility.getURLResponse(conn);
                            httpStatus = conn.getResponseCode();
                            Utility.print("API response:"+apiResult);
                            if(httpStatus==conn.HTTP_OK){
                                String responseStatus = null,responseMessage=null,responseTransactionID=null;
                                try {
                                    JSONObject jsonObject = new JSONObject(apiResult);
                                    //start: read key values
                                    if(jsonObject.has("status")){
                                        responseStatus = jsonObject.getString("status");
                                    }else{
                                        responseStatus = "Status Not found";
                                    }
                                    if(jsonObject.has("message")){
                                        responseMessage = jsonObject.getString("message");
                                    }
                                    if(jsonObject.has("transactionId")){
                                        responseTransactionID = jsonObject.getString("transactionId");
                                    }
                                    //end: read key values
                                    //insert api log
                                    inParam = new HashMap();
                                    inParam.put("tokenID",tokenID);
                                    inParam.put("requestType",requestType);
                                    inParam.put("requestData",apiJsonReq);
                                    inParam.put("responseData",apiResult);
                                    inParam.put("transactionID",responseTransactionID);
                                    inParam.put("messageCategory","01");

                                    outParam = userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);

                                    if (responseStatus.equals("200") && responseMessage.equalsIgnoreCase("success")){
                                        linkSentStatus = "S";
                                    }else{
                                        linkSentStatus = "F";
                                        remarks = responseMessage;
                                    }
                                    //Update "otp_verification_dtl"
                                    userService.updateOTPLinkSentStatus(tokenID,linkSentStatus,remarks);
                                }catch (JSONException e){
                                    errorFlag = "E";
                                    errorMessage = "JSONException:"+e.getMessage();
                                    errorRemarks = errorMessage;
                                    inParam = new HashMap();
                                    inParam.put("error_msg",errorMessage);
                                    inParam.put("remarks",errorRemarks);
                                    inParam.put("obj_name",objectName);
                                    inParam.put("error_flag",errorFlag);
                                    outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                    e.printStackTrace();
                                    continue;
                                }
                            }else{
                                errorFlag = "U";
                                errorMessage = "SMS Send API HTTP Status:"+httpStatus;
                                errorRemarks = errorMessage;
                                inParam = new HashMap();
                                inParam.put("error_msg",errorMessage);
                                inParam.put("remarks",errorRemarks);
                                inParam.put("obj_name",objectName);
                                inParam.put("error_flag",errorFlag);
                                outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                continue;
                            }
                        }catch (Exception e) {
                            Utility.print("API Calling failed:"+e.getMessage());
                            errorFlag = "E";
                            errorMessage = "Exception:"+e.getMessage();
                            errorRemarks = "Error while calling SMS send API";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            e.printStackTrace();
                            continue;
                        }
                    }catch (Exception e) {
                        errorFlag = "E";
                        errorMessage = "Exception:"+e.getMessage();
                        errorRemarks = "Error while getting URL detail(s) for SMS send API";
                        inParam = new HashMap();
                        inParam.put("error_msg",errorMessage);
                        inParam.put("remarks",errorRemarks);
                        inParam.put("obj_name",objectName);
                        inParam.put("error_flag",errorFlag);
                        outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                        continue;
                    }
                }else if(requestType.equals("EMAIL") && !mobile.isEmpty())
                {
                    //for send email for email verification
                    SysParaMst sysParaMst_link_active = userService.getParaVal("9999","9999",24);
                    SysParaMst sysParaMst_link_detail = null, sysParaMst_internal_link=null;
                    if(sysParaMst_link_active==null){
                        apiActive="Y";
                    }else {
                        apiActive = sysParaMst_link_active.getPara_value();
                        apiActive = apiActive == null ? "N" : apiActive;
                    }
                    if(apiActive.equals("N")){
                        Utility.print("Email send API service is temporary disabled");
                        continue;
                    }
                    //link detail <message string>
                    sysParaMst_link_detail = userService.getParaVal("9999","9999",27);
                    message = sysParaMst_link_detail.getPara_value();
                    //link detail <link string>
                    sysParaMst_internal_link = userService.getParaVal("9999","9999",202);
                    internalLink = sysParaMst_internal_link.getPara_value();
                    internalLink = internalLink.replace("<tokenID>",tokenID);
                    shortURL = userService.getShortURL(tokenID,internalLink);
                    if(shortURL.equals("0")){
                        continue;
                    }
                    message = message.replace("<link>",shortURL);
                    try {
                        String apiURL=null,apiKey=null,templateID=null;
                        int configCD = 47; //Invoid: sms send API(email-verify)

                        URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
                        apiURL = urlConfigDto.getUrl();
                        apiKey = urlConfigDto.getKey();
                        templateID	= urlConfigDto.getSmtp_port();
                        if(apiURL==null|| apiURL.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL not found for CODE("+configCD+")";
                            errorRemarks = errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(apiKey==null|| apiKey.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL key found for CODE("+configCD+")";
                            errorRemarks = requestType+"|"+errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(templateID==null|| templateID.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL Required templateId not found for CODE("+configCD+")";
                            errorRemarks = requestType+"|"+errorMessage;
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        try{
                            String apiResult = null;
                            int httpStatus=0;
                            URL obj = new URL(apiURL);
                            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

                            conn.setRequestMethod("POST");
                            conn.addRequestProperty("content-Type", "application/json");
                            conn.addRequestProperty("authKey",apiKey);
                            conn.setDoOutput(true);
                            JSONObject requestJson = new JSONObject();
                            requestJson.put("mobile",data);
                            requestJson.put("message",message);
                            requestJson.put("templateId",templateID);
                            apiJsonReq = requestJson.toString();
                            Utility.print("Email verification API request data:\n"+apiJsonReq);
                            OutputStream os = conn.getOutputStream();
                            os.write(requestJson.toString().getBytes());
                            os.flush();
                            os.close();
                            //get response body of api request
                            apiResult = Utility.getURLResponse(conn);
                            httpStatus = conn.getResponseCode();
                            Utility.print("API response:"+apiResult);
                            if(httpStatus==conn.HTTP_OK){
                                String responseStatus = null,responseMessage=null,responseTransactionID=null;
                                try {
                                    JSONObject jsonObject = new JSONObject(apiResult);
                                    //start: read key values
                                    if(jsonObject.has("status")){
                                        responseStatus = jsonObject.getString("status");
                                    }else{
                                        responseStatus = "Status Not found";
                                    }
                                    if(jsonObject.has("message")){
                                        responseMessage = jsonObject.getString("message");
                                    }
                                    if(jsonObject.has("transactionId")){
                                        responseTransactionID = jsonObject.getString("transactionId");
                                    }
                                    //end: read key values
                                    //insert api log
                                    inParam = new HashMap();
                                    inParam.put("tokenID",tokenID);
                                    inParam.put("requestType",requestType);
                                    inParam.put("requestData",apiJsonReq);
                                    inParam.put("responseData",apiResult);
                                    inParam.put("transactionID",responseTransactionID);
                                    inParam.put("messageCategory","02");
                                    outParam = userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);

                                    if (responseStatus.equals("200") && responseMessage.equalsIgnoreCase("success")){
                                        linkSentStatus = "S";
                                    }else{
                                        linkSentStatus = "F";
                                        remarks = responseMessage;
                                    }
                                    //Update "otp_verification_dtl"
                                    userService.updateOTPLinkSentStatus(tokenID,linkSentStatus,remarks);
                                }catch (JSONException e){
                                    errorFlag = "E";
                                    errorMessage = "JSONException:"+e.getMessage();
                                    errorRemarks = requestType+"|"+errorMessage;
                                    inParam = new HashMap();
                                    inParam.put("error_msg",errorMessage);
                                    inParam.put("remarks",errorRemarks);
                                    inParam.put("obj_name",objectName);
                                    inParam.put("error_flag",errorFlag);
                                    outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                    e.printStackTrace();
                                    continue;
                                }
                            }else{
                                errorFlag = "U";
                                errorMessage = "Email Send API HTTP Status:"+httpStatus;
                                errorRemarks = requestType+"|"+errorMessage;
                                inParam = new HashMap();
                                inParam.put("error_msg",errorMessage);
                                inParam.put("remarks",errorRemarks);
                                inParam.put("obj_name",objectName);
                                inParam.put("error_flag",errorFlag);
                                outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                continue;
                            }
                        }catch (Exception e) {
                            Utility.print("API Calling failed:"+e.getMessage());
                            errorFlag = "E";
                            errorMessage = "Exception:"+e.getMessage();
                            errorRemarks = requestType+"|Error while calling SMS send API";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            e.printStackTrace();
                            continue;
                        }
                    }catch (Exception e) {
                        errorFlag = "E";
                        errorMessage = "Exception:"+e.getMessage();
                        errorRemarks = requestType+"|Error while getting URL detail(s) for Email send API";
                        inParam = new HashMap();
                        inParam.put("error_msg",errorMessage);
                        inParam.put("remarks",errorRemarks);
                        inParam.put("obj_name",objectName);
                        inParam.put("error_flag",errorFlag);
                        outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                        continue;
                    }
                }

            }//end for loop
        }
    }//end of method
}
