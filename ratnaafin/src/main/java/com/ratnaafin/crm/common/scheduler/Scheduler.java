package com.ratnaafin.crm.common.scheduler;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.ratnaafin.crm.admin.controller.AdminController;
import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.controller.UserController;
import com.ratnaafin.crm.user.dto.CRMAppDto;
import com.ratnaafin.crm.user.dto.PerfiosReqResDto;
import com.ratnaafin.crm.user.dto.URLConfigDto;
import com.ratnaafin.crm.user.model.*;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Component
public class Scheduler {
    Utility utility = new Utility();
    private String g_error_msg = "Something went wrong please try again.";
    private  String user_error = "Error(u):";
    private  String sys_error  = "Error(e):";
    private  String userError(String msg){ return user_error+msg; }
    private  String sysError(String msg){ return sys_error+msg; }

    public static final String CAM_FILE_PATH = Utility.LOCAL_PATH+Utility.SEPERATOR+"CAM"+Utility.SEPERATOR;
    private final String className = this.getClass().getSimpleName();
    public static String CAM_DATA = "N";

    //for logger name
    private static String logger = "schedulers";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

//    @Scheduled(fixedDelay = 120000, initialDelay = 300000)
//    public void fixedDelaySch() {
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
////        Date now = new Date();
////        String strDate = sdf.format(now);
//        String JsonData = null;
//        JsonData = funcGetIntiatedRequestData();
//        if (JsonData != null) {
//            long leadId , serialNo, amountIn;
//            String enteredBy = null;
//            try {
//                BrowserContext context;
//                Playwright playwright = Playwright.create();
//                Browser browser = playwright.chromium().launch();
//                context = browser.newContext();
//                Page page = context.newPage();
//                JSONArray jsonArray = new JSONArray(JsonData);
//                for (int i = 0;i<jsonArray.length();i++){
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    leadId = jsonObject.getLong("refID");
//                    serialNo = jsonObject.getLong("serialNo");
//                    enteredBy = jsonObject.getString("enteredBy");
//                    amountIn = jsonObject.getLong("amountIn");
//                    if (leadId > 0  && serialNo > 0){
//                        try {
//                            page.navigate("https://ratnaafin.aiplservices.com/middleware/lead/"+leadId+"?amountIn="+amountIn);
//                            page.waitForTimeout(29000);
//                            Page.PdfOptions options = new Page.PdfOptions();
//                            options.format="A4";
//                            options.displayHeaderFooter = true;
//                            options.printBackground = true;
//                            //options.withDisplayHeaderFooter(true);
//                            //options.withPath(Paths.get("page.pdf"));
//                            String fileName = CAM_FILE_PATH+userService.getuniqueId()+"_Lead_"+leadId+"_CAM.pdf";
//                            File filepath = new File(fileName);
//                            FileInputStream fileInputStream = null;
//                            //options.setPath(Paths.get(filepath.getAbsolutePath()));
//                            //options.withPath(Paths.get(filepath.getAbsolutePath()));
//                            //System.out.println("File Path:"+filepath.getAbsolutePath());
//                            options.path = Paths.get(filepath.getAbsolutePath());
//                            page.pdf(options);
//                            fileInputStream = new FileInputStream(filepath);
//                            Blob blob = null;
//                            Utility utility = new Utility();
//                            blob = utility.getBlobData(fileInputStream);
//                            if (blob != null) {
//                                userService.updateCAMStatus(serialNo,leadId,blob,new Date(),"S",enteredBy);
//                            }else {
//                                userService.updateCAMStatus(serialNo,leadId,blob,new Date(),"F",enteredBy);
//                            }
//                            if(filepath.delete()) {
//                                System.out.println("File Deleted");
//                            }else{
//                                filepath.deleteOnExit();
//                                System.out.println("File not Deleted");
//                            }
//                        }catch (Exception e){
//                            System.out.println("Internal Exception"+e.getMessage()+" cause "+e.getCause().getMessage());
//                        }
//                    }
//                }
//                page.close();
//                context.close();
//            }catch (JSONException e){
//                System.out.println("JSONException"+e.getMessage());
//            }catch (Exception e){
//                System.out.println("Outer Exception"+e.getMessage()+" cause "+e.getCause().getMessage());
//            }
//            //System.out.println("End task" + new Date());
//        }
//        System.gc();
//    }

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
//    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
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
        //test
        apiActive = "N";
        Utility.print("(equifax)total rows:"+rows.size());
        Utility.print("(equifax)link send api flag:"+apiActive);

        if(rows.size()>=0 && apiActive.equals("Y")){
            String apiURLRtl=null,apiURLComm=null,apiKeyRtl=null,apiKeyComm=null,templateIDRtl=null,templateIDComm=null,additionalRemarks=null;
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
                shortURL    = equifaxAPILog.getShorted_link();
                errorRemarks  = "(eqfx)id:"+tokenID;
                //decrypt data
                data = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),mobile);
                Utility.print("for:"+reqName);
                Utility.print("data:"+data);

                //send link
                if(reqName.equals("RETAIL")){
                    //link detail <link string>
                    internalLink = internalLink.replace("<tokenID>",tokenID);
                    if(shortURL==null||shortURL.trim().isEmpty()){
                        shortURL = userService.getShortURL(tokenID,internalLink);
                        if(shortURL.equals("0")){
                            additionalRemarks = "Error in Service:URL shortener";
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",additionalRemarks,null,additionalRemarks);
                            continue;
                        }
                    }

                    messageRtl = messageRtl.replace("<link>",shortURL);
                    Utility.print("message will be:\n"+messageRtl);
                    try {
                        if(apiURLRtl==null|| apiURLRtl.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL not found for CODE("+configCDRtl+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            additionalRemarks = errorMessage;
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(apiKeyRtl==null|| apiKeyRtl.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL key found for CODE("+configCDRtl+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            additionalRemarks = errorMessage;
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(templateIDRtl==null|| templateIDRtl.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL Required templateId not found for CODE("+configCDRtl+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            additionalRemarks = errorMessage;
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        try{
                            String apiResult = null,status=null;
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
                            Utility.print("API response>:"+apiResult);
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
                                    }else{
                                        status = "F";
                                        linkSentStatus = "F";
                                        remarks = responseMessage;
                                    }
                                    additionalRemarks = remarks;
                                    userService.updateEqfxOTPLinkStatus(tokenID,status,linkSentStatus,remarks,shortURL,additionalRemarks);
                                }catch (JSONException e){
                                    errorFlag = "E";
                                    errorMessage = "JSONException:"+e.getMessage();
                                    inParam = new HashMap();
                                    inParam.put("error_msg",errorMessage);
                                    inParam.put("remarks",errorRemarks);
                                    inParam.put("obj_name",objectName);
                                    inParam.put("error_flag",errorFlag);
                                    additionalRemarks = errorMessage;
                                    userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                                    outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                    e.printStackTrace();
                                    continue;
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
                                    additionalRemarks = e.getMessage()+"/"+apiResult.substring(0,3999);
                                    responseMessage = g_error_msg;
                                }
                                //Update "otp_verification_dtl"
                                remarks = responseMessage;
                                userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,shortURL,additionalRemarks);
                            }
                            //insert api log
                            inParam = new HashMap();
                            inParam.put("tokenID",tokenID);
                            inParam.put("requestType",reqName);
                            inParam.put("requestData",apiJsonReq);
                            inParam.put("responseData",apiResult);
                            inParam.put("transactionID",responseTransactionID);
                            inParam.put("messageCategory","03");
                            outParam = userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);
                        }catch (Exception e) {
                            Utility.print("API Calling failed:"+e.getMessage());
                            errorFlag = "E";
                            errorMessage = "Exception:"+e.getMessage();
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            additionalRemarks = errorMessage;
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            e.printStackTrace();
                            continue;
                        }
                    }catch (Exception e) {
                        errorFlag = "E";
                        errorMessage = "Exception:"+e.getMessage();
                        inParam = new HashMap();
                        inParam.put("error_msg",errorMessage);
                        inParam.put("remarks",errorRemarks);
                        inParam.put("obj_name",objectName);
                        inParam.put("error_flag",errorFlag);
                        additionalRemarks = errorMessage;
                        userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                        outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                        continue;
                    }
                }
                else if(reqName.equals("COMMERCIAL"))
                {
                    //link detail <link string>
                    internalLink = internalLink.replace("<tokenID>",tokenID);
                    if(shortURL==null||shortURL.trim().isEmpty()){
                        shortURL = userService.getShortURL(tokenID,internalLink);
                        if(shortURL.equals("0")){
                            //Update "otp_verification_dtl"
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F","Error in Service:URL shortener",null,additionalRemarks);
                            continue;
                        }
                    }
                    messageComm = messageComm.replace("<link>",shortURL);
                    Utility.print("message will be:\n"+messageComm);
                    try {
                        if(apiURLComm==null|| apiURLComm.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL not found for CODE("+configCDComm+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            additionalRemarks = errorMessage;
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(apiKeyComm==null|| apiKeyComm.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL key found for CODE("+configCDComm+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            additionalRemarks = errorMessage;
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(templateIDComm==null|| templateIDComm.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL Required templateId not found for CODE("+configCDComm+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            additionalRemarks = errorMessage;
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        try{
                            String apiResult = null,status=null;
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
                            String responseStatus = null,responseMessage=null,responseTransactionID=null;
                            JSONObject jsonObject = null;
                            if(httpStatus==conn.HTTP_OK){
                                try {
                                    jsonObject = new JSONObject(apiResult);
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

                                    if (responseStatus.equals("200") && responseMessage.equalsIgnoreCase("success")){
                                        status = "P";
                                        linkSentStatus = "S";
                                    }else{
                                        status = "F";
                                        linkSentStatus = "F";
                                        remarks = responseMessage;
                                    }
                                    additionalRemarks = remarks;
                                    userService.updateEqfxOTPLinkStatus(tokenID,status,linkSentStatus,remarks,shortURL,additionalRemarks);
                                }catch (JSONException e){
                                    errorFlag = "E";
                                    errorMessage = "JSONException:"+e.getMessage();
                                    inParam = new HashMap();
                                    inParam.put("error_msg",errorMessage);
                                    inParam.put("remarks",errorRemarks);
                                    inParam.put("obj_name",objectName);
                                    inParam.put("error_flag",errorFlag);
                                    additionalRemarks = errorMessage;
                                    userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                                    outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                    e.printStackTrace();
                                    continue;
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
                                    additionalRemarks = e.getMessage()+"/"+apiResult.substring(0,3999);
                                    responseMessage = g_error_msg;
                                }
                                //Update "otp_verification_dtl"
                                remarks = responseMessage;
                                userService.updateEqfxOTPLinkStatus(tokenID,"F","F",remarks,shortURL,additionalRemarks);
                            }
                            //insert api log
                            inParam = new HashMap();
                            inParam.put("tokenID",tokenID);
                            inParam.put("requestType",reqName);
                            inParam.put("requestData",apiJsonReq);
                            inParam.put("responseData",apiResult);
                            inParam.put("transactionID",responseTransactionID);
                            inParam.put("messageCategory","04");
                            outParam = userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);

                        }catch (Exception e) {
                            Utility.print("API Calling failed:"+e.getMessage());
                            errorFlag = "E";
                            errorMessage = "Exception:"+e.getMessage();
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            additionalRemarks = errorMessage;
                            userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            e.printStackTrace();
                            continue;
                        }
                    }catch (Exception e) {
                        errorFlag = "E";
                        errorMessage = "Exception:"+e.getMessage();
                        inParam = new HashMap();
                        inParam.put("error_msg",errorMessage);
                        inParam.put("remarks",errorRemarks);
                        inParam.put("obj_name",objectName);
                        inParam.put("error_flag",errorFlag);
                        additionalRemarks = errorMessage;
                        userService.updateEqfxOTPLinkStatus(tokenID,"F","F",g_error_msg,shortURL,additionalRemarks);
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
                requestType  = otpVerificationDtl.getReq_type();
                tokenID      = otpVerificationDtl.getToken_id();
                mobile       = otpVerificationDtl.getOtp_sent_mobile();
                email        = otpVerificationDtl.getOtp_sent_email();
                shortURL     = otpVerificationDtl.getShorted_link();
                data        = mobile;//requestType.equals("EMAIL")?email:mobile;
                //decrypt data
                data = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),data);
                Utility.print(requestType+":"+data);
                //send link
                if(requestType.equals("SMS") && !mobile.isEmpty()){
                    //send sms on mobile for verification
                    //invoid mobile sms link active
                    errorRemarks = "(sms):"+tokenID;
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
                    if(shortURL==null||shortURL.trim().isEmpty()){
                        shortURL = userService.getShortURL(tokenID,internalLink);
                        if(shortURL.equals("0")){
                            Utility.print("failed to short url");
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F","Error in Service:URL shortener",null);
                            continue;
                        }
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
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(apiKey==null|| apiKey.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL key found for CODE("+configCD+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(templateID==null|| templateID.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL Required templateId not found for CODE("+configCD+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
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
                                        linkSentStatus = "S";
                                    }else{
                                        linkSentStatus = "F";
                                        remarks = responseMessage;
                                    }
                                    //Update "otp_verification_dtl"
                                    userService.updateOTPLinkSentStatus(tokenID,linkSentStatus,remarks,shortURL);
                                }catch (JSONException e){
                                    errorFlag = "E";
                                    errorMessage = "JSONException:"+e.getMessage();
                                    inParam = new HashMap();
                                    inParam.put("error_msg",errorMessage);
                                    inParam.put("remarks",errorRemarks);
                                    inParam.put("obj_name",objectName);
                                    inParam.put("error_flag",errorFlag);
                                    //Update "otp_verification_dtl"
                                    userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                                    outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                    e.printStackTrace();
                                    continue;
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
                                    responseMessage = "";
                                }
                                remarks = responseMessage==null||responseMessage.isEmpty()?apiResult.substring(0,3999):responseMessage;
                                //Update "otp_verification_dtl"
                                userService.updateOTPLinkSentStatus(tokenID,"F",remarks,shortURL);
                            }
                            //insert api log
                            inParam = new HashMap();
                            inParam.put("tokenID",tokenID);
                            inParam.put("requestType",requestType);
                            inParam.put("requestData",apiJsonReq);
                            inParam.put("responseData",apiResult);
                            inParam.put("transactionID",responseTransactionID);
                            inParam.put("messageCategory","01");
                            outParam = userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);
                        }catch (Exception e) {
                            Utility.print("API Calling failed:"+e.getMessage());
                            errorFlag = "E";
                            errorMessage = "Exception:"+e.getMessage();
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            e.printStackTrace();
                            continue;
                        }
                    }catch (Exception e) {
                        errorFlag = "E";
                        errorMessage = "Exception:"+e.getMessage();
                        inParam = new HashMap();
                        inParam.put("error_msg",errorMessage);
                        inParam.put("remarks",errorRemarks);
                        inParam.put("obj_name",objectName);
                        inParam.put("error_flag",errorFlag);
                        //Update "otp_verification_dtl"
                        userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                        outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                        continue;
                    }
                }else if(requestType.equals("EMAIL") && !mobile.isEmpty()){
                    //for send email for email verification
                    errorRemarks = "(email):"+tokenID;
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
                    if(shortURL==null||shortURL.trim().isEmpty()){
                        shortURL = userService.getShortURL(tokenID,internalLink);
                        if(shortURL.equals("0")){
                            Utility.print("failed to short url");
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F","Error in Service:URL shortener",null);
                            continue;
                        }
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
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(apiKey==null|| apiKey.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL key found for CODE("+configCD+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            continue;
                        }
                        if(templateID==null|| templateID.isEmpty()) {
                            errorFlag = "E";
                            errorMessage = "URL Required templateId not found for CODE("+configCD+")";
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
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
                            JSONObject jsonObject = null;
                            String responseStatus = null,responseMessage=null,responseTransactionID=null;
                            if(httpStatus==conn.HTTP_OK){
                                try {
                                    jsonObject = new JSONObject(apiResult);
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
                                    if (responseStatus.equals("200") && responseMessage.equalsIgnoreCase("success")){
                                        linkSentStatus = "S";
                                    }else{
                                        linkSentStatus = "F";
                                        remarks = responseMessage;
                                    }
                                    //Update "otp_verification_dtl"
                                    userService.updateOTPLinkSentStatus(tokenID,linkSentStatus,remarks,shortURL);
                                }catch (JSONException e){
                                    errorFlag = "E";
                                    errorMessage = "JSONException:"+e.getMessage();
                                    inParam = new HashMap();
                                    inParam.put("error_msg",errorMessage);
                                    inParam.put("remarks",errorRemarks);
                                    inParam.put("obj_name",objectName);
                                    inParam.put("error_flag",errorFlag);
                                    //Update "otp_verification_dtl"
                                    userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                                    outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                                    e.printStackTrace();
                                    continue;
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
                                    responseMessage = "";
                                }
                                remarks = responseMessage==null||responseMessage.isEmpty()?apiResult.substring(0,3999):responseMessage;
                                //Update "otp_verification_dtl"
                                userService.updateOTPLinkSentStatus(tokenID,"F",remarks,shortURL);
                            }
                            //insert api log
                            inParam = new HashMap();
                            inParam.put("tokenID",tokenID);
                            inParam.put("requestType",requestType);
                            inParam.put("requestData",apiJsonReq);
                            inParam.put("responseData",apiResult);
                            inParam.put("transactionID",responseTransactionID);
                            inParam.put("messageCategory","02");
                            outParam = userService.callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);

                        }catch (Exception e) {
                            Utility.print("API Calling failed:"+e.getMessage());
                            errorFlag = "E";
                            errorMessage = "Exception:"+e.getMessage();
                            inParam = new HashMap();
                            inParam.put("error_msg",errorMessage);
                            inParam.put("remarks",errorRemarks);
                            inParam.put("obj_name",objectName);
                            inParam.put("error_flag",errorFlag);
                            //Update "otp_verification_dtl"
                            userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                            outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                            e.printStackTrace();
                            continue;
                        }
                    }catch (Exception e) {
                        errorFlag = "E";
                        errorMessage = "Exception:"+e.getMessage();
                        inParam = new HashMap();
                        inParam.put("error_msg",errorMessage);
                        inParam.put("remarks",errorRemarks);
                        inParam.put("obj_name",objectName);
                        inParam.put("error_flag",errorFlag);
                        //Update "otp_verification_dtl"
                        userService.updateOTPLinkSentStatus(tokenID,"F",g_error_msg,shortURL);
                        outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
                        continue;
                    }
                }
            }//end for loop
        }
    }//end of method

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public void webhookActivityProcess(){
        AdminController adminController = new AdminController();
        String objectName = this.getClass().getSimpleName()+".java",
        errorFlag = null,errorMessage=null,errorRemarks=null,webhookData=null,
        webhookName=null,result=null,processStatus=null,webhookStatus=null;

        Long webhookTranCd;

        HashMap inParam=null,outParam=null;
        List<ApiWebhookActivity> rows = userService.findWebhookProcess();
        Utility.print("Pending Webhook rows:"+rows.size());
        //utility.generateLog("info","rows("+rows.size()+") fetch by findPendingOTPLinkDetail",logger);
        if(rows.size()>0){
            try {
                for (ApiWebhookActivity apiWebhookActivity : rows) {
                    webhookTranCd = apiWebhookActivity.getTran_cd();
                    webhookData = apiWebhookActivity.getWebhook_data();
                    webhookName = apiWebhookActivity.getWebhook_nm();
                    Utility.print("Webhook Data:" + webhookData);
                    Utility.print("Webhook Name:" + webhookName);

                    switch (webhookName) {
                        case "gstinfowebhook":
                            result = funcGstInfoWebhook(webhookData);
                            break;
                        case "gstuploadwebhook":
                            result = funcGstUploadWebhook(webhookData);
                            break;
                        case "itruploadwebhook":
                            result = funcItrUploadWebhook(webhookData);
                            break;
                        case "statementwebhook":
                            result = funcStatementWebhook(webhookData);
                            break;
                        case "placeorderwebhook":
                            result = funcPlaceOrderWebhook(webhookData);
                            break;
                        default:
                            continue;
                    }
                    try {
                        Utility.print("webhook process result:"+result);
                        JSONObject jsonObject = new JSONObject(result);
                        processStatus = jsonObject.optString("status");
                        if(jsonObject.has("webhook")){
                            webhookStatus = jsonObject.optString("webhook");
                        }else{
                            webhookStatus = "success";
                        }
                        processStatus = processStatus.equals("failed") ? "F" : "S";
                        webhookStatus = processStatus.equals("failed") ? "F" : "S";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        processStatus = "F";
                        webhookStatus = "S";
                    }
                    userService.updateWebhookProcess(webhookTranCd, processStatus, result, webhookStatus, "process completed");
                }
            }catch (NullPointerException e){
                e.printStackTrace();
                errorFlag = "E";
                errorMessage = "Exception:"+e.getMessage();
                errorRemarks = "webhookActivityProcess()";
                inParam = new HashMap();
                inParam.put("error_msg",errorMessage);
                inParam.put("remarks",errorRemarks);
                inParam.put("obj_name",objectName);
                inParam.put("error_flag",errorFlag);
                userService.callingDBObject("procedure","proc_insert_error_log",inParam);
            }
            catch (Exception e){
                e.printStackTrace();
                errorFlag = "E";
                errorMessage = "Exception:"+e.getMessage();
                errorRemarks = "webhookActivityProcess()";
                inParam = new HashMap();
                inParam.put("error_msg",errorMessage);
                inParam.put("remarks",errorRemarks);
                inParam.put("obj_name",objectName);
                inParam.put("error_flag",errorFlag);
                userService.callingDBObject("procedure","proc_insert_error_log",inParam);
            }
        }
    }//end of method

    /**Webhook process utilities added on: 25/06/2021**/
        //GST Info Webhook
        public String funcGstInfoWebhook(String requestdata){
            String perfiosTransactionId = null,status = null,errorCode=null,message=null,
                    processLocation = className+"/funcGstInfoWebhook()";
            Map<String,String> data = userService.parseUrlFragment(requestdata);

            perfiosTransactionId = data.get("perfiosTransactionId");
            status = data.get("status");
            if(data.containsKey("errorCode")){
                errorCode = data.get("errorCode");
            }
            if(data.containsKey("message")){
                message = data.get("message");
            }
            if((!perfiosTransactionId.isEmpty()) && (!status.isEmpty())){
                if ((status.equals("COMPLETED")))
                {
                    return gstInfoRetrieve(perfiosTransactionId,11,requestdata);
                }
            }else {
                userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",errorCode+":"+message);
                return "{\"status\":\"failed\",\n" +
                        "\"webhook\":\"failed\",\n" +
                        "\"location\":\""+processLocation+"\",\n" +
                        "\"errorTitle\":\"field value not found\",\n" +
                        "\"errorDesc\":\"(A)error:perfiosTransactionId not found\"\n" +
                        "}";
            }
            return "{\"status\":\"success\"}";
        }

        //GST Upload Webhook>>import from AdminController
        public String funcGstUploadWebhook(String requestdata){
            Utility.print("executing:funcGstUploadWebhook()");
            //processResponse structure
            String processResponse=utility.getWebhookProcessStructure();
            String processStatus="success",processLoc=className+"/funcGstUploadWebhook()",processTitle="",processDesc="",processWebhook="success";

            String perfiosTransactionId = null,status = null,errorCode=null,message=null,result=null;
            /*requestdata
                txnId:      Perfios Transaction Id
                status:     Status of the transaction. See below for possible values (COMPLETED|ERROR|REPORT_GENERATION_FAILED)
                errorCode:  Perfios Transaction error code
                message:    Perfios Transaction status message
            */
            /**WEBHOOK FORMAT**/
            /*perfiosTransactionId=PGTFGOQZIM1DSYN8WSNOZ
             * &status=COMPLETED
             * &errorCode=
             * &message=
             * &clientTransactionId=ratnaaFinGSTUpload*/

            try{
                Map<String,String> data = userService.parseUrlFragment(requestdata);
                perfiosTransactionId = data.get("perfiosTransactionId");
                status = data.get("status");
                if(data.containsKey("errorCode")){
                    errorCode = data.get("errorCode");
                }
                if(data.containsKey("message")){
                    message = data.get("message");
                }

                if((!perfiosTransactionId.isEmpty()) ){
                    String fetchStatus;
                    fetchStatus = fetchDocumentStatus(perfiosTransactionId,19,"GST_ITR_UPLOAD_STATUS");
                    Utility.print(fetchStatus);
                    if(status.equals("COMPLETED")){
                        Utility.print("going to fetch document");
                        return gstInfoRetrieve(perfiosTransactionId,20,requestdata);
                    }else {
                        userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",status+":"+errorCode+":"+message);
                    }
                }else{
                    processStatus = "failed";
                    processWebhook = "failed";
                    processTitle  = "field value not found";
                    processDesc   = "(A)error:perfiosTransactionId not found";
                }
            }catch (Exception e){
                Utility.print("Exception:"+e.getMessage());
                processStatus = "failed";
                processTitle  = "Exception";
                processDesc   = "(B)error:"+e.getMessage();
                e.printStackTrace();
            }
            if(processStatus.equalsIgnoreCase("failed")){
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<webhook>",processWebhook);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                result = processResponse;
            }else{
                result = "{\"status\":\"success\"}";
            }
            return result;
        }

        //ITR Upload Webhook>>import from AdminController
        public String funcItrUploadWebhook(String requestdata){
            //processResponse structure
            String processResponse=utility.getWebhookProcessStructure();
            String processStatus="success",processLoc=className+"/funcItrUploadWebhook()",processTitle="",processDesc="",processWebhook="success";
            String perfiosTransactionId = null,status = null, errorCode=null,message=null,result=null,
            processLocation = className+"/funcItrUploadWebhook()";
            /**Request Data**/
//          txnId:      Perfios Transaction Id
//          status:     Status of the transaction. See below for possible values (COMPLETED|ERROR|REPORT_GENERATION_FAILED)
//          errorCode:  Perfios Transaction error code
//          message:    Perfios Transaction status message

            /**WEBHOOK FORMAT**/
            /*clientTransactionId=ratnaaFin_It_upload
             * &errorCode=ERROR_TIMED_OUT
             * &message=Transaction+Expired+due+to+inactivity
             * &perfiosTransactionId=PIETLFJNBGT3ADUWJRH48
             * &status=EXPIRED*/
            try{
                Map<String,String> data = userService.parseUrlFragment(requestdata);
                perfiosTransactionId = data.get("perfiosTransactionId");
                status = data.get("status");
                if(data.containsKey("errorCode")){
                    errorCode = data.get("errorCode");
                }
                if(data.containsKey("message")){
                    message = data.get("message");
                }
                if((!perfiosTransactionId.isEmpty())){
                    String fetchStatus;
                    fetchStatus = fetchDocumentStatus(perfiosTransactionId,28,"GST_ITR_UPLOAD_STATUS");
                    if(status.equals("COMPLETED")){
                        return gstInfoRetrieve(perfiosTransactionId,30,requestdata);
                    }else{
                        //if(status.equals("ERROR") || status.equals("REPORT_GENERATION_FAILED")||status.equals("CANCELLED")||status.equals("REJECTED")){
                        userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",status+":"+errorCode+":"+message);
                    }
                }else{
                    processStatus = "failed";
                    processWebhook = "failed";
                    processTitle  = "field value not found";
                    processDesc   = "(A)error:perfiosTransactionId not found";
                }
            }catch (Exception e){
                processStatus = "failed";
                processTitle  = "Exception";
                processDesc   = "(B)error:"+e.getMessage();
            }
            if(processStatus.equalsIgnoreCase("failed")){
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<webhook>",processStatus);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                result = processResponse;
            }else{
                result = "{\"status\":\"success\"}";
            }
            return result;
        }

        //Statement Upload Webhook>>import from AdminController
        public String funcStatementWebhook(String requestdata) {
            //processResponse structure
            String processResponse=utility.getWebhookProcessStructure();
            String processStatus="success",processLoc=className+"/funcStatementWebhook()",processTitle="",processDesc="",
            processWebhook  = "success";

            Utility utility = new Utility();
            String perfiosTransactionId = null, status = null, clientTransactionId = null, userStatus = null,errorCode=null,message=null;
            JSONObject jsonObject = new JSONObject();

            /*requestdata
                perfiosTransactionId   :Perfios Transaction Id
                clientTransactionId    :Client-specified transaction identifier
                status                 :Status of the transaction. See below for possible values
                errorCode              :Error code associated with the transaction. See below for possible values
                errorMessage           :Error message associated with the transaction
            */
            /*sample response*/
            /*
            * {
                "errorMessage": "",
                "errorCode": "E_NO_ERROR",
                "clientTransactionId": "18195221602a7d446-0571-4d86-add6-20d252f7117020210417",
                "perfiosTransactionId": "LTY21618664373633",
                "status": "COMPLETED"
                }*/
            try{
                jsonObject = new JSONObject(requestdata);
                perfiosTransactionId = jsonObject.getString("perfiosTransactionId");
                clientTransactionId  = jsonObject.getString("clientTransactionId");
                status				 = jsonObject.getString("status");
                if(jsonObject.has("userStatus")){
                    userStatus = jsonObject.getString("userStatus");
                }
                if(jsonObject.has("errorCode")){
                    errorCode =jsonObject.getString("errorCode");
                }
                if(jsonObject.has("errorMessage")){
                    message = jsonObject.getString("errorMessage");
                }else if(jsonObject.has("message")){
                    message = jsonObject.getString("message");
                }
            }catch (JSONException e){
                processStatus = "failed";
                processWebhook = "failed";
                processTitle  = "Exception";
                processDesc   = "(A)error:"+e.getMessage();
            }
            if(perfiosTransactionId==null || perfiosTransactionId.isEmpty()){
                processStatus = "failed";
                processWebhook = "failed";
                processTitle  = "field value not found";
                processDesc   = "(B)perfiosTransactionId is missing";
            }
            if(processStatus.equalsIgnoreCase("failed")){
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<webhook>",processWebhook);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                return  processResponse;
            }
            if (status!=null && status.equalsIgnoreCase("COMPLETED")) {
                return retrieveStatementReport(perfiosTransactionId, clientTransactionId, requestdata);
            }else{
                userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",status+":"+errorCode+":"+message);
            }
            return "{\"status\":\"success\"}";
        }

        //Corpository Placeorder Webhook>>import from AdminController
        //place order webhook
        public String funcPlaceOrderWebhook(String requestData){
            Utility utility = new Utility();
            //processResponse structure
            String refTranId=null,compId=null,transactionId=null,createJson=null,status=null,webhookType=null,msg=null,
                result=null;
            String processResponse=utility.getWebhookProcessStructure();
            String processStatus="success",processLoc=className+"/funcPlaceOrderWebhook()",processTitle="",processDesc="",processWebhook="success";
            try{
                JSONObject jsonObject = new JSONObject(requestData);
                webhookType = String.valueOf(jsonObject.getLong("type"));
                refTranId   = String.valueOf(jsonObject.getLong("reference-id"));
                status      = String.valueOf(jsonObject.getLong("status"));
                msg         = jsonObject.getString("message");
            }catch (JSONException e){
                processStatus = "failed";
                processWebhook = processStatus;
                processTitle  = "JSONException";
                processDesc   = "(A)error:"+e.getMessage();
            }catch (Exception e){
                processStatus = "failed";
                processWebhook = processStatus;
                processTitle  = "Exception";
                processDesc   = "(B)error:"+e.getMessage();
            }
            if(processStatus.equalsIgnoreCase("failed")){
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<webhook>",processStatus);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                return  processResponse;
            }
            return  processPlaceOrderWebhook(requestData,refTranId,status,webhookType,msg);
        }

    /**<<Used by Webhook functions**/
        //report retrieve function: clone from UserController
        public String gstInfoRetrieve(String ls_transaction_id, int urlID, String ls_webhookres) {
            Utility utility = new Utility();
            String ls_download_status = null;
            String ls_action = "gstinfoRetrieve", ls_channel = "W";
            String lstatus = null, ls_url = null;
            URLConfigDto urlConfigDto = userService.findURLDtlByID(urlID);
            //processResponse structure
            String processResponse=utility.getWebhookProcessStructure();
            String processStatus="success",processLoc=className+"/gstInfoRetrieve()",processTitle="",processDesc="";
            ResponseEntity<String> result = null;
            try {
                if (!urlConfigDto.getUserid().isEmpty()) {
                    try {
                        PerfiosReqResDtl perfiosReqResDtl = new PerfiosReqResDtl();
                        RestTemplate restTemplate = new RestTemplate();
                        // URl for retrive transaction information
                        ls_url = urlConfigDto.getUrl() + urlConfigDto.getUserid() + "/" + ls_transaction_id;
                        Utility.print("1.Generated URL:" + ls_url);
                        // call
                        result = restTemplate.getForEntity(ls_url, String.class);
                        Utility.print("2.API called successfully");
                        lstatus = result.getStatusCode().toString();
                        Utility.print("3.API call Status:" + lstatus);
                        if (result.getStatusCode().equals(HttpStatus.OK)) {
                            Utility.print("3. API status OK");
                            Blob xlsFileData = null, zipFileData = null;
                            String jsonFileData = null;
                            ls_download_status = "P";
                            try {
                                URL zipurl = new URL(ls_url);

                                Utility.print("5. saving Zip file");
                                utility.saveZipFile(zipurl, ls_transaction_id + ".zip");
                                // get zip file
                                Utility.print("6. get Zip file data");
                                zipFileData = utility.getBlobData(zipurl.openStream());
                                // get json file
                                Utility.print("7. get json file data");
                                jsonFileData = utility.getZipClobData(".json");
                                // get xls file
                                Utility.print("8. get xls file data");
                                xlsFileData = utility.getZipBlobData(".xlsx");
                                if (zipFileData != null || xlsFileData != null || jsonFileData != null) {
                                    ls_download_status = "S";
                                }
                                utility.deleteFile();
                                Utility.print("9. saving file data: for tranID: " + ls_transaction_id);
                                userService.updatePerfiosWebhookStatus(ls_transaction_id, lstatus, "S", ls_webhookres,
                                        zipFileData, xlsFileData, jsonFileData, ls_download_status, "SUCCESS");
                            } catch (MalformedURLException e) {
                                processStatus = "failed";
                                processTitle  = "MalformedURLException";
                                processDesc   = "(A)url:"+ls_url+"|error:"+e.getMessage();
                            } catch (IOException e) {
                                processStatus = "failed";
                                processTitle  = "IOException";
                                processDesc   = "(B)url:"+ls_url+"|error:"+e.getMessage();
                            } catch (Exception e) {
                                processStatus = "failed";
                                processTitle  = "Exception";
                                processDesc   = "(C)url:"+ls_url+"|error:"+e.getMessage();
                            }
                        }else{
                            Utility.print("4.API call Status:" + lstatus);
                            userService.updatePerfiosWebhookStatus(ls_transaction_id, lstatus, "F", ls_webhookres, null,
                                    null, null, "F", "error(e):RETRIEVE TIME ERROR/fetch-url:"+ls_url);
                            processStatus = "failed";
                            processTitle  = "Response Status:"+result.getStatusCode();
                            processDesc   = "(D)url:"+ls_url;
                        }
                    }catch (Exception e) {
                        processStatus = "failed";
                        processTitle  = "Exception";
                        processDesc   = "(E)url:"+ls_url+"|error:"+e.getMessage();
                    }
                }else{
                    processStatus = "failed";
                    processTitle  = "URL Configuration Not Found";
                    processDesc   = "(F)urlId:"+urlID;
                }
            }catch (Exception e){
                e.printStackTrace();
                processStatus = "failed";
                processTitle  = "Exception";
                processDesc   = "(G)url:"+ls_url+"|error:"+e.getMessage();
            }
            if(processStatus.equalsIgnoreCase("failed")){
                userService.updatePerfiosWebhookStatus(ls_transaction_id, lstatus, "E", ls_webhookres, null,
                        null, null, "F", "error(e):"+processDesc);
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                return  processResponse;
            }else{
                return "{\"status\":\"success\"}";
            }
        }

        // fetch and update gst upload status at the time of webHook : import from UserController
        public String fetchDocumentStatus(String perfiosTransactionId, int urlID, String caseStr) {
            Utility.print("executing:fetchDocumentStatus()");
            JSONObject jsonObject, jsonObject1, jsonObject2;
            URLConfigDto urlConfigDto = userService.findURLDtlByID(urlID);
            Utility utility = new Utility();
            String sendURL, urlResponse;
            int rowUpdate = 0;
            if (urlConfigDto.getUserid() == null) {
                return "URL UserID not found for:" + urlID;
            } else if (urlConfigDto.getUrl() == null) {
                return "URL string not found for:" + urlID;
            }
            sendURL = urlConfigDto.getUrl() + urlConfigDto.getUserid() + "/" + perfiosTransactionId;
            try {
                URL url = new URL(sendURL);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("content-Type", "application/json");
                conn.addRequestProperty("cache-control", "no-cache");
                urlResponse = utility.getURLResponse(conn);
                jsonObject1 = new JSONObject(urlResponse);
                System.out.println("Response Code:" + conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    /*
                     * { message: COMPLETED, status : true, statementsStatus: [ {fileName:
                     * XYZ.pdf,status: COMPLETED,message: }, {fileName: ABC.pdf,status: REJECTED,
                     * message: The uploaded file is not a valid GST return file.}] }
                     */

                    // update gst document status
                    rowUpdate = updateGstDocumentStatus(perfiosTransactionId, caseStr, jsonObject1.toString());
                    return "" + rowUpdate + " row(s) updated.";
                } else {
                    return "failed due to HTTP Status code:" + conn.getResponseCode();
                }
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        // update document status: clone from UserController
        public int updateGstDocumentStatus(String transactionId, String caseStr, String statusResponsedata) {
            Connection connection;
            CallableStatement cs;
            try {
                int record = 0;
                connection = jdbcTemplate.getDataSource().getConnection();
                connection.setAutoCommit(false);
                cs = connection.prepareCall("{ call PACK_DOCUMENT.proc_update_gstupload_document(?,?,?,?,?) }");
                Utility.print("TranID:" + transactionId);
                Utility.print("statusResponse:" + statusResponsedata);

                cs.setString(1, transactionId);
                cs.setString(2, caseStr);
                cs.setInt(3, 14);
                cs.setString(4, statusResponsedata);

                cs.registerOutParameter(5, 2005);
                cs.execute();
                final String returnCount = cs.getString(5);
                connection.close();
                cs.close();
                record = Integer.parseInt(returnCount);
                return record;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        //retrieve statement method
        //used by : funcStatementWebhook()
        public String retrieveStatementReport(String perfiosTransactionId, String txnId, String ls_webhookres) {
            Utility utility = new Utility();
            //processResponse structure
            String processResponse=utility.getWebhookProcessStructure();
            String processStatus="success",processLoc=className+"/retrieveStatementReport()",processTitle="",processDesc="";
            String downloadStatus = null;
            String action = "statementReportRetrieve", channel = "W";
            String httpStatus = null, sendURL1 = null, sendURL2 = null;
            Blob zipBlobdata;
            int configCd = 36;
            URLConfigDto urlConfigDto = userService.findURLDtlByID(configCd);
            Utility.print("2");

            String payloadJson = "<payload><apiVersion>2.1</apiVersion>" + "<perfiosTransactionId>" + perfiosTransactionId
                    + "</perfiosTransactionId>" + "<reportType>json</reportType>" + "<txnId>" + txnId + "</txnId>"
                    + "<vendorId>" + urlConfigDto.getUserid() + "</vendorId>" + "</payload>";

            String payloadXlsx = "<payload><apiVersion>2.1</apiVersion>" + "<perfiosTransactionId>" + perfiosTransactionId
                    + "</perfiosTransactionId>" + "<reportType>xlsx</reportType>" + "<txnId>" + txnId + "</txnId>"
                    + "<vendorId>" + urlConfigDto.getUserid() + "</vendorId>" + "</payload>";

            ResponseEntity<String> result = null;
            try {
                if (!urlConfigDto.getUserid().isEmpty()){
                    // URl for retrieve transaction information
                    sendURL1 = urlConfigDto.getUrl();// + urlConfigDto.getUserid() + "/" + ls_transaction_id;
                    sendURL2 = sendURL1;
                    Utility.print("1. generated URL:" + sendURL1);
                    PerfiosReqResDtl perfiosReqResDtl = new PerfiosReqResDtl();

                    // condensed payload
                    payloadJson = payloadJson.replaceAll("\n", "");
                    payloadXlsx = payloadXlsx.replaceAll("\n", "");

                    String digJson, digXlsx, signatureJson, signatureXlsx;

                    digJson = Utility.makeDigest(payloadJson);
                    digXlsx = Utility.makeDigest(payloadXlsx);

                    signatureJson = Utility.perfiosDataEncryptPvt(digJson);
                    signatureXlsx = Utility.perfiosDataEncryptPvt(digXlsx);

                    Utility.print("6");

                    InputStream filestream = null,xlsStream,jsonStream;
                    byte[] filecontents;
                    ByteArrayOutputStream fileoutput = new ByteArrayOutputStream();
                    byte[] filebuffer = new byte[1024];
                    int filecount;

                    Utility.print("3. API status OK");
                    Blob jsonFileBlob = null, xlsxFileBlob;
                    String jsonClobData = null;
                    downloadStatus = "P";
                    try {
                        sendURL1 = sendURL1 + "?signature=" + signatureJson + "&payload=" + payloadJson;
                        sendURL2 = sendURL2 + "?signature=" + signatureXlsx + "&payload=" + payloadXlsx;

                        URL JsonURL = new URL(sendURL1);
                        URL XlsxURL = new URL(sendURL2);

                        Utility.print("JSON URL:" + sendURL1);
                        Utility.print("XLSX URL:" + sendURL2);

                        Utility.print("7. get json file data");
                        //json file downloading
                        filestream   = JsonURL.openStream();
                        jsonStream   = filestream;
                        jsonClobData = utility.getClobData(filestream);

                        //xlsx file downloading
                        filestream   = XlsxURL.openStream();
                        xlsStream  = filestream;
                        while ((filecount = filestream.read(filebuffer)) != -1) {
                            fileoutput.write(filebuffer, 0, filecount);
                        }
                        filecontents = fileoutput.toByteArray();
                        xlsxFileBlob = new SerialBlob(filecontents);
                        if (jsonClobData != null && xlsxFileBlob != null) {
                            downloadStatus = "S";
                            File zipfile = new File(Utility.LOCAL_PATH+Utility.SEPERATOR+"temp.zip");
                            FileOutputStream fos = new FileOutputStream(zipfile);
                            ZipOutputStream zos = new ZipOutputStream(fos);
                            InputStream zin =new FileInputStream(zipfile);
    //                        try {
    //                            Utility.writeToZipFile(jsonStream,"analyzedReport.json",zos);
    //                            Utility.writeToZipFile(xlsStream,"analyzedReport.xlsx",zos);
    //                            zipBlobdata = utility.getBlobData(zin);
    //                        }finally{
    //                            zos.close();
    //                            fos.close();
    //                            zin.close();
    //                            if(zipfile.delete()){Utility.print("temp.zip file deleted.");
    //                            }else{Utility.print("temp.zip file failed to delete");}
    //                        }
                            userService.updatePerfiosWebhookStatus(perfiosTransactionId, "200 OK", "S", ls_webhookres, null,
                                    xlsxFileBlob, jsonClobData, downloadStatus, "SUCCESS");
                        } else {
                            downloadStatus = "F";
                            userService.updatePerfiosWebhookStatus(perfiosTransactionId, httpStatus, downloadStatus, ls_webhookres,
                                    null, null, null, downloadStatus,
                                    "No Download found for JsonURL:" + JsonURL + "XlsxURL:" + XlsxURL);
                        }
                    } catch (MalformedURLException e) {
                        processStatus = "failed";
                        processTitle  = "MalformedURLException";
                        processDesc   = "(A)error:"+e.getMessage();
                    } catch (IOException e) {
                        processStatus = "failed";
                        processTitle  = "IOException";
                        processDesc   = "(B)error:"+e.getMessage();
                    } catch (Exception e) {
                        processStatus = "failed";
                        processTitle  = "Exception";
                        processDesc   = "(C)error:"+e.getMessage();
                    }
                } else {
                    processStatus = "failed";
                    processTitle  = "URL Configuration Not Found";
                    processDesc   = "(D)urlId:"+configCd+"|userId is missing";
                }
            }catch (Exception e) {
                processStatus = "failed";
                processTitle  = "Exception";
                processDesc   = "(E)error:"+e.getMessage();
            }
            if(processStatus.equalsIgnoreCase("failed")){
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                return  processResponse;
            }else{
                return "{\"status\":\"success\"}";
            }
        }
   /**Webhook process end**/

   /**perfios document upload process**/
   @Scheduled(fixedDelay = 30000, initialDelay = 30000)
   public void perfiosDocumentUploadProcess(){
       String transactoinId=null,requestType=null,result=null;
       List<PerfiosReqResDtl> rows = userService.findPendingDocumentProcess();
       Utility.print("Pending Documents rows:"+rows.size());
       //utility.generateLog("info","rows("+rows.size()+") fetch by findPendingOTPLinkDetail",logger);
       if(rows.size()>0){
           try {
               for (PerfiosReqResDtl perfiosReqResDtl : rows){
                   requestType   = perfiosReqResDtl.getRequest_type();
                   transactoinId = perfiosReqResDtl.getTransaction_id();
                   Utility.print("request-type:"+requestType);
                   Utility.print("TransactionID:"+transactoinId);
                   switch (requestType.toLowerCase()){
                       case "gst_upload":
                           result = funcGstStatementUpload(transactoinId);
                           break;
                       case "itr_upload":
                           result = funcItStatementUpload(transactoinId);
                           break;
                       case "stmt_upload":
                           result = funcBankStatementUpload(transactoinId);
                           break;
                       default:
                           break;
                    }
                    Utility.print("outer result:"+result);
                   if(result.equalsIgnoreCase("success")) {
                       userService.updatePerfiosWebhookStatus(transactoinId, null, "P", null, null, null, null, "P", "Request is in process");
                   }else {
                       String flag=null,remarks=null;
                       flag    = result.indexOf("Error(u)")>=0 ? "F" : "E";
                       remarks = result;
                       userService.updatePerfiosWebhookStatus(transactoinId, null, flag, null, null, null, null, "F", remarks);
                   }
               }
           }catch (Exception e){
               e.printStackTrace();
           }
       }
   }

   /**GST document upload**/
    //1.1) gst document upload process
    public  String funcGstStatementUpload(String transactionId){
       //declaration
       Connection connection;
       CallableStatement cs;
       Utility utility = new Utility();
       JSONObject jsonObject,jsonObject1,jsonObject2;
       jsonObject  = new JSONObject();
       jsonObject1 = new JSONObject();
       jsonObject2 = new JSONObject();
       long docId = 14; //for gst pdf documnent
       String userID,docUUID; long refId=0, serialNo=0; int cnt = 0;
       String      action = "gstUpload",channel="W", userName= "",module = "GST document upload",
               result, perfiosTransactionId,initiatedRequest=null,processFor=null,
               requestData = "{\"perfiosTransactionId\":\""+transactionId+"\"}";
       String      patterndate = "yyyyMMdd";
       String      patterntime = "HHmmssSSS";
       Blob blobfile;
       SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patterndate);
       SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patterntime);
       channel ="W";  result = null; perfiosTransactionId = null;
       int 	failCount = 0;
       /*end declaration*/
       userService.saveJsonLog(channel,"req",action,requestData,userName,module);
       perfiosTransactionId   = transactionId;
       if(perfiosTransactionId==null||perfiosTransactionId.isEmpty()){
           return userError("perfiosTransactionId Not Found!");
       }

       //get transaction detail
       PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
       if(perfiosReqResDto.getUserid()==null){
           return userError("Initial GST Upload Transaction Not Found");
       }

       //checking for document ready for upload//
       String statusCheck = funcCheckGstUploadStatus(perfiosTransactionId);
       if(!statusCheck.equals("success")){
           return statusCheck;
       }

       //get url dtl
       int urlConfigID =21;
       URLConfigDto urlConfigDto = userService.findURLDtlByID(urlConfigID);
       if(urlConfigDto.getUrl() == null){
           return userError("URL Not Found.("+urlConfigID+")");
       }
       if(urlConfigDto.getUserid() == null){
           return userError("URL userID Not Found.("+urlConfigID+")");
       }
       String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid()+"/"+perfiosTransactionId;
       Utility.print("Request Sending URL generated:"+sendURL);
       //get blob file data
       refId 	 = perfiosReqResDto.getRef_tran_cd();
       serialNo = perfiosReqResDto.getRef_sr_cd();
       initiatedRequest = perfiosReqResDto.getInitiated_req();
       try {
           jsonObject2	= new JSONObject(initiatedRequest);
           if(jsonObject2.getJSONObject("request_data").has("processFor")){
               processFor	= jsonObject2.getJSONObject("request_data").getString("processFor");
           }else{
               processFor="ALL";
           }
       }catch (JSONException e){
           return sysError("Error while getting Initiated Request."+e.getMessage());
       }
       if(processFor==null || processFor.isEmpty()){
           return  userError("Initiate Request Parameter missing:processFor found empty");
       }
       if(processFor.equals("GSTR1")){
           docId = 14;
       }else if(processFor.equals("GSTR3")){
           docId = 15;
       }
       Boolean allFailed = true;
       String remarks = null;
       JSONObject jsonDocStatus= new JSONObject();
       if(processFor.equals("ALL")){
           List<CrmMiscMst> docTypeList = userService.findByCategory("GST_DOC_TYPE");

           if(docTypeList==null||docTypeList.size()<=0){
               return sysError("GST Document Type(GST_DOC_TYPE) not in Master Table.");
           }
           for(CrmMiscMst crmMiscMst:docTypeList){
               docId = Long.parseLong(crmMiscMst.getData_value());
               List<DocUploadDtl> docList = userService.getDocListByDocId(refId, serialNo, "L", docId);
               if (docList == null || docList.isEmpty()) {
                   continue;
               }
               Utility.print("Document upload process starting...");
               try {
                   String docType = null, resultOut = null;
                   String fileName = "", errorCode = "", errorMessage = "", req_status = "", doc_status = ""/*(U(upload)|F(failed)|R(reject)|P(process)|S(uploded and processed))*/;
                   Boolean successBool = false;
                   Blob blobdata = null;
                   jsonDocStatus = new JSONObject();
                   for (DocUploadDtl docUploadDtl : docList) {
                       cnt += 1;
                       docUUID = docUploadDtl.getDoc_uuid();

                       DocUploadBlobDtl docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                       if (docUploadBlobDtl == null || docUploadBlobDtl.getUuid() == null) {
                           return userError("Document Not found for UUID:" + docUUID);
                       }
                       if (docUploadBlobDtl.getDocContentType() == null) {
                           return userError("Document Content Type Not found for UUID:" + docUUID);
                       }
                       Utility.print("Document upload process started..!");
                       Utility.print(docUploadBlobDtl.getDoc_name());
                       blobdata = new SerialBlob(docUploadBlobDtl.getData());
                       docType = docUploadBlobDtl.getDocContentType();
                       String docTypeExt = docType.substring(docType.indexOf('/') + 1);
                       if (blobdata.getBinaryStream() != null) {
                           fileName = "file" + cnt;
                           File sendFile = utility.blobToFileConverter(blobdata, fileName, "." + docTypeExt);
                           fileName = sendFile.getName();
                           //attaching file to sendURL
                           try {
                               Utility.print("File type is>:" + docType);
                               utility.initiateMultipart(sendURL, "UTF-8");
                               utility.addFilePart("file", sendFile);
                               resultOut = utility.finish();
                               utility.deleteFile();
                               Utility.print(resultOut);
                               jsonObject1 = new JSONObject(resultOut);
                               successBool = jsonObject1.getBoolean("success");
                               if (utility.getHttpConn().getResponseCode() == 200 && successBool) {
                                   /*{ success: true,transactionId: PCPTSTTO09IM3PR3VUTDP}*/
                                   allFailed = false;
                                   Utility.print("SUCCESS RESPONSE");
                                   req_status = "true";
                                   doc_status = "U"; //uploaded
                                   jsonObject2 = new JSONObject();
                                   jsonObject2.put("status", "0");
                                   jsonObject2.put("response_data", jsonObject1);
                                   userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                                   result = jsonObject2.toString();
                               } else {
							/*{
							errorCode: InsecureFileType,
							errorMessage: Client tried to upload an insecure file type (executable, DLL, JAR, etc.),
							success: false
							}*/
                                   failCount = failCount + 1;
                                   req_status = "false";
                                   doc_status = "F"; //upload  failed
                                   errorMessage = jsonObject1.getString("message");
                                   errorCode = jsonObject1.getString("code");
                                   remarks = errorCode + ":" + errorMessage;
                               }
                               jsonDocStatus.put(fileName, jsonObject1);
                               Utility.print(result);
                           } catch (IOException e) {
                               userService.getJsonError("-99", "Error-IOException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
                               return sysError("IOException:"+e.getMessage());
                           } catch (JSONException e) {
                               userService.getJsonError("-99", "JSON Error", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
                               return sysError("JSONException:"+e.getMessage());
                           }
                           //save each file status
                           connection = jdbcTemplate.getDataSource().getConnection();
                           connection.setAutoCommit(false);
                           cs = connection.prepareCall("{ call pack_document.proc_insert_gstupload_document(?,?,?,?,?,?,?,?,?,?,?,?) }");
                           cs.setString(1, perfiosReqResDto.getUserid());
                           cs.setString(2, perfiosTransactionId);
                           cs.setString(3, String.valueOf(docId));
                           cs.setString(4, docUUID);
                           cs.setString(5, req_status);
                           cs.setString(6, docType);
                           cs.setString(7, fileName);
                           cs.setString(8, doc_status);
                           cs.setString(9, resultOut);
                           cs.setString(10, remarks);
                           cs.setString(11, perfiosReqResDto.getEntered_by());



                           cs.registerOutParameter(12, 2005);
                           cs.execute();
                           final String returnData = cs.getString(12);
                           connection.close();
                           cs.close();
                           Utility.print(returnData);
                           if (failCount > 0) {
                               return userError("failed to upload:docUUID:" + docUUID + "," + remarks);
                           }
                       } else {
                           return sysError("Invalid Document for UUID:" + docUUID);
                       }
                   }
               } catch (SQLException e){
                   userService.getJsonError("-99", "Error-SQLException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
                   return sysError("SQLException:"+e.getMessage());
               } catch (FileNotFoundException e){
                   userService.getJsonError("-99", "Error-FileNotFoundException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
                   return sysError("FileNotFoundException:"+e.getMessage());
               }
           }

       }else{
           List<DocUploadDtl> docList = userService.getDocListByDocId(refId, serialNo, "L", docId);
           if (docList == null || docList.isEmpty()) {
               return sysError("Not found any verified documents for this transaction.");
           }
           Utility.print("Document upload process starting...");
           try {
               String docType = null, resultOut = null;
               String fileName = "", errorCode = "", errorMessage = "", req_status = "", doc_status = ""/*(U(upload)|F(failed)|R(reject)|P(process)|S(uploded and processed))*/;
               Boolean successBool = false;
               Blob blobdata = null;
               jsonDocStatus = new JSONObject();
               for (DocUploadDtl docUploadDtl : docList) {
                   cnt += 1;
                   docUUID = docUploadDtl.getDoc_uuid();

                   DocUploadBlobDtl docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                   if (docUploadBlobDtl == null || docUploadBlobDtl.getUuid() == null) {
                       return userError("Document Not found for UUID:" + docUUID);
                   }
                   if (docUploadBlobDtl.getDocContentType() == null) {
                       return userError("Document Content Type Not found for UUID:" + docUUID);
                   }
                   Utility.print("Document upload process started..!");
                   Utility.print(docUploadBlobDtl.getDoc_name());
                   blobdata = new SerialBlob(docUploadBlobDtl.getData());
                   docType = docUploadBlobDtl.getDocContentType();
                   String docTypeExt = docType.substring(docType.indexOf('/') + 1);
                   if (blobdata.getBinaryStream() != null) {
                       fileName = "file" + cnt;
                       File sendFile = utility.blobToFileConverter(blobdata, fileName, "." + docTypeExt);
                       fileName = sendFile.getName();
                       //attaching file to sendURL
                       try {
                           Utility.print("File type is>:" + docType);
                           utility.initiateMultipart(sendURL, "UTF-8");
                           utility.addFilePart("file", sendFile);
                           resultOut = utility.finish();
                           utility.deleteFile();
                           Utility.print(resultOut);
                           jsonObject1 = new JSONObject(resultOut);
                           successBool = jsonObject1.getBoolean("success");
                           if (utility.getHttpConn().getResponseCode() == 200 && successBool) {
                               /*{ success: true,transactionId: PCPTSTTO09IM3PR3VUTDP}*/
                               allFailed = false;
                               Utility.print("SUCCESS RESPONSE");
                               req_status = "true";
                               doc_status = "U"; //uploaded
                               jsonObject2 = new JSONObject();
                               jsonObject2.put("status", "0");
                               jsonObject2.put("response_data", jsonObject1);
                               userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                               result = jsonObject2.toString();
                           } else {
							/*{
							errorCode: InsecureFileType,
							errorMessage: Client tried to upload an insecure file type (executable, DLL, JAR, etc.),
							success: false
							}*/
                               failCount = failCount + 1;
                               req_status = "false";
                               doc_status = "F"; //upload  failed
                               errorMessage = jsonObject1.getString("message");
                               errorCode = jsonObject1.getString("code");
                               remarks = errorCode + ":" + errorMessage;
                           }
                           jsonDocStatus.put(fileName, jsonObject1);
                           Utility.print(result);
                       } catch (IOException e) {
                           userService.getJsonError("-99", "Error-IOException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
                           return sysError("IOException:"+e.getMessage());
                       } catch (JSONException e) {
                           userService.getJsonError("-99", "Error-JsonException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
                           return sysError("JSONException:"+e.getMessage());
                       }
                       //save each file status
                       connection = jdbcTemplate.getDataSource().getConnection();
                       connection.setAutoCommit(false);
                       cs = connection.prepareCall("{ call pack_document.proc_insert_gstupload_document(?,?,?,?,?,?,?,?,?,?,?,?) }");
                       cs.setString(1, perfiosReqResDto.getUserid());
                       cs.setString(2, perfiosTransactionId);
                       cs.setString(3, String.valueOf(docId));
                       cs.setString(4, docUUID);
                       cs.setString(5, req_status);
                       cs.setString(6, docType);
                       cs.setString(7, fileName);
                       cs.setString(8, doc_status);
                       cs.setString(9, resultOut);
                       cs.setString(10, remarks);
                       cs.setString(11, perfiosReqResDto.getEntered_by());



                       cs.registerOutParameter(12, 2005);
                       cs.execute();
                       final String returnData = cs.getString(12);
                       connection.close();
                       cs.close();
                       Utility.print(returnData);
                       if (failCount > 0) {
                           return userError("failed to upload:docUUID:" + docUUID + "," + remarks);
                       }
                   } else {
                       return userError("Invalid Document for UUID:" + docUUID);
                   }
               }
           } catch (SQLException e) {
               userService.getJsonError("-99", "Error-SQLException", g_error_msg, e.getMessage()==null?"NULL":e.getMessage(), "99", channel, action, userName, userName, module, "E");
               return sysError("SQLException:"+e.getMessage());
           } catch (FileNotFoundException e) {
               userService.getJsonError("-99", "Error-FileNotFoundException", g_error_msg, e.getMessage(), "99", channel, action, userName, userName, module, "E");
               return sysError("FileNotFoundException:"+e.getMessage());
           }
       }
       if (allFailed) {
           /*NOTE: Update transaction status F because all file failed to upload*/
           userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, "F", null, null, null, null, "F", "0 DOCUMENT(S) UPLOADED");
           return userError(remarks);
       }
       return funcGstStatementStartProcess(perfiosTransactionId);
   }

    //1.2) gst document status check
    public String funcCheckGstUploadStatus(String transactionId) {
        JSONObject jsonObject;
        String message = null,perfiostransactionId =transactionId ;
        String title = "Transaction Status Check";
        if (perfiostransactionId.isEmpty()) {
            return userError(title+":transactionId not found.");
        }
        int configCd = 19;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(configCd);
        if(urlConfigDto.getUserid()==null){
            return userError(title+":"+"URL Configuration Not Foundf for id:"+configCd);
        }

        try{
            Utility utility = new Utility();
            String ls_url,result,ls_response;
            ls_url = urlConfigDto.getUrl() + urlConfigDto.getUserid() + "/" + perfiostransactionId;
            Utility.print("Gst Statement Upload generated URL:" + ls_url);
            URL url = new URL(ls_url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("cache-control", "no-cache");
            result = utility.getURLResponse(conn);
            jsonObject= new JSONObject(result);
            System.out.println("Response Code:"+conn.getResponseCode());
            if(jsonObject.has("message")){
                message = jsonObject.getString("message");
            }
            if(conn.getResponseCode()==200){
				/*message parameter value will be following
					COMPLETED:                          Transaction completed successfully
					ERROR:                              There was an error processing the transaction
					INITIATED:                          Perfios transaction was initiated
					PROCESSING:                         GST data parsing is in progress
					REPORT_GENERATION_READY:            GST data parsing complete, ready for report generation
					REPORT_GENERATION_FAILED:           GST Report generation failed
					TRANSACTION_COMPLETE_CALLBACK_FAILED: Transaction complete callback failed
					ERROR_SERVER_SHUTDOWN:              Status updated on account of server shutdown
				*/
                if(message.equals("INITIATED")){
                    return "success";
                }
            }
            return userError(title+":"+message);
        }catch (Exception e) {
            return sysError(e.getMessage());
        }
    }

    //1.3) gst uploaded document start process for report
    public String funcGstStatementStartProcess(String transactionId){
        //declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String     action = "gstUploadProcess",channel="W", result, perfiosTransactionId,userName="",module="GST document upload process",
            requestData = "{\"perfiosTransactionId\":\""+transactionId+"\"}";
        channel =  result = perfiosTransactionId = null;
        /*end declaration*/

        //read requestdata
        perfiosTransactionId = transactionId;
        if(perfiosTransactionId==null||perfiosTransactionId.isEmpty()){
            return userError("perfiosTransactionId Not Found!");
        }

        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid()==null){
            return userError("Initial GST Upload Transaction Not Found");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            return userError("Transaction is alrady in failed status, Unable to process.");
        }

        //get url dtl
        int configCd = 24;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(configCd);
        if(urlConfigDto.getUrl() == null){
            return sysError("URL Not Found for id:"+configCd);
        }
        if(urlConfigDto.getUserid() == null){
            return userError("URL userID Not Found for id:"+configCd);
        }
        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid()+"/"+perfiosTransactionId;
        Utility.print("Request Sending URL generated:"+sendURL);
        try{
            URL url = new URL(sendURL);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("cache-control", "no-cache");
            result = utility.getURLResponse(conn);
            jsonObject1= new JSONObject(result);
            System.out.println("Response Code:"+conn.getResponseCode());
            Utility.print("==RESPONSE==");
            utility.print(result);
            if(conn.getResponseCode()==200){
			/*
			{success: ”true”,transactionId: ”PCPTSTTO09IM3PR3VUTDP”}
			*/
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
                return  "success";
            }else{
			/*{
				errorCode: ”TransactionIdNotFound”,
				errorMessage: ”We could not find the Perfios Transaction Id referred to by the client”,
				success: ”false”}
			   */
                String message=null,code=null;
                if(jsonObject1.has("code")){
                    code = jsonObject1.getString("code");
                }
                if(jsonObject1.has("message")){
                    message = jsonObject1.getString("message");
                }
                userService.getJsonError("-99","Error in GST Statement Process.",message,code+": "+message,"99",channel,action,requestData,userName,module,"U");
                return userError(code+": "+message);
            }
        }catch (IOException e){
            return sysError("IOException:"+e.getMessage());
        } catch (Exception e){
            return sysError("Exception:"+e.getMessage());
        }
    }

   /**ITR document**/
    //2.1) itr document upload process
    public String funcItStatementUpload(String transactionId){
        //declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        String      channel="W", userName="", result=null, perfiosTransactionId=null,entityType=null,
        requestData = "{\"perfiosTransactionId\":\""+transactionId+"\"}";
        long        refId=0, serialNo=0;
        int 		failCount = 0;
        /*end declaration*/
        String module = "ITR Document Upload",action = "itrUpload";
        //read requestdata

        userService.saveJsonLog(channel,"req",action,requestData,userName,module);
        perfiosTransactionId   = transactionId;
        if(perfiosTransactionId==null){
            return userError("perfiosTransactionId Not Found!");
        }

        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid() == null){
            return userError("Initial ITR Upload Transaction Not Found.");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            return userError("Transaction is already in failed status, Unable to process.");
        }

        //checking for document ready for upload//
        String statusCheck = funcCheckItUploadStatus(perfiosTransactionId);
        if(!statusCheck.equals("success")){
            return statusCheck;
        }

        //get url dtl
        int urlConfigID = 27;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(urlConfigID);
        if(urlConfigDto.getUrl() == null){
            return userError("URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            return userError("URL userID Not Found.("+urlConfigID+")");
        }
        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid()+"/"+perfiosTransactionId;
        Utility.print("Request Sending URL generated:"+sendURL);
        //get blob file data
        refId 		= perfiosReqResDto.getRef_tran_cd();
        serialNo	= perfiosReqResDto.getRef_sr_cd();
        entityType  = perfiosReqResDto.getEntity_type();
        List<CrmMiscMst> docTypeList = /*userService.getDocMstListByDocType("ITR");*/userService.findByCategory("ITR_DOC_TYPE");
        long docId = 0;
        int docMstCount = 0; int docTypeListSize = docTypeList.size();
        Utility.print("document mst row count for ITR:"+docTypeListSize); //expect:6 from crm_misc_mst
        if(docTypeListSize>0){
            //loop for Document Mst traverse for doc type = ITR
            String docType,resultOut,fileName,errorCode,errorMessage,req_status,doc_status,docUUID,docPwd,docTitle,remarks=null;
            Boolean successBool=false;
            Blob blobdata = null;
            Boolean allFailed = true;
            CRMAppDto crmObjPII = userService.findAppByID(3);
            //phase:1
            JSONObject jsonDocStatus = new JSONObject();
            for(CrmMiscMst crmMiscMst:docTypeList){
                docId        = /*crmMiscMst.getTran_cd();*/Long.parseLong(crmMiscMst.getData_value());
                docTitle     = /*crmMiscMst.getDoc_title();*/crmMiscMst.getDisplay_value();
                docMstCount  = docMstCount+1;
                Utility.print("loop count:"+docMstCount);
                Utility.print("docID:"+docId);

                List<DocUploadDtl> docList = userService.getDocListByDocId(refId,serialNo,entityType,docId); //1044-19
                if(docList.isEmpty() || docList.size()<=0){
                    Utility.print("No row in document_Dtl for:"+refId+"-"+serialNo+"-"+docId);
                    if(remarks==null){
                        remarks = "Not found any verified documents for this transaction.";
                    }
                    continue;
                }
                try{
                    int cnt = 0;
                    for(DocUploadDtl docUploadDtl:docList){
                        cnt+=1;
                        docUUID = docUploadDtl.getDoc_uuid();
                        DocUploadBlobDtl docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                        if(docUploadBlobDtl==null){
                            return userError("Document's details are not found for UUID:"+docUUID);
                        }
                        if(docUploadBlobDtl.getDocContentType()==null){
                            return userError("Document Content Type Not found for UUID:"+docUUID);
                        }

                        //Getting Document meta data
                        blobdata    = new SerialBlob(docUploadBlobDtl.getData());
                        docType     = docUploadBlobDtl.getDocContentType();
                        docPwd      = docUploadBlobDtl.getPassword();
                        if(docPwd==null || docPwd.isEmpty()){
                            docPwd = "";
                        }else{
                            docPwd = userService.func_get_data_val(crmObjPII.getA(),crmObjPII.getB(),docPwd);
                            docPwd = Utility.perfiosDataEncryptPub(docPwd);
                        }

                        String docTypeExt = docType.substring(docType.indexOf('/')+1);
                        if(blobdata.getBinaryStream()!=null){
                            Utility.print("Document upload process started..!");
                            Utility.print("Document Name:"+docUploadBlobDtl.getDoc_name());

                            fileName = "file"+docId+""+cnt; //expect: file19-1<randomStamps>
                            File sendFile = utility.blobToFileConverter(blobdata,fileName,"."+docTypeExt);
                            fileName = sendFile.getName();
                            //attaching file to sendURL
                            try{
                                Utility.print("File Generated to be Upload:"+fileName);
                                utility.initiateMultipart(sendURL,"UTF-8");
                                utility.addFormField("password",docPwd);
                                utility.addFormField("uploadFileType",docTitle);
                                utility.addFormField("financialYear","");
                                utility.addFilePart("file",sendFile);
                                Utility.print("uploadFileType:"+docTitle);

                                resultOut =utility.finish();
                                utility.deleteFile();
                                Utility.print(resultOut);
                                jsonObject1 = new JSONObject(resultOut);
                                successBool = jsonObject1.getBoolean("success");
                                if(utility.getHttpConn().getResponseCode()==200 && successBool){
                                    /*{ success: true,transactionId: PCPTSTTO09IM3PR3VUTDP}*/
                                    allFailed = false;
                                    Utility.print("SUCCESS RESPONSE");
                                    req_status    = "true";
                                    doc_status = "U"; //uploaded 14-9(4), 14-11(1)
                                    remarks = "Successfully uploaded";
                                }else{
							/*{
							errorCode: InsecureFileType,
							errorMessage: Client tried to upload an insecure file type (executable, DLL, JAR, etc.),
							success: false
							}*/
                                    failCount	  = failCount+1;
                                    req_status    = "false";
                                    doc_status = "F"; //upload  failed
                                    errorMessage  = jsonObject1.getString("message");
                                    errorCode     = jsonObject1.getString("code");
                                    remarks       = errorCode+":"+errorMessage;
                                    /*result = userService.getJsonError("-99","GST Statements Upload Failed.",errorMessage,errorMessage,"99",channel,action,requestdata,userName,null,"U");
                                     */
                                }
                                jsonDocStatus.put(fileName,jsonObject1);
                            }catch (IOException e){
                                userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
                                return sysError(e.getMessage());
                            }catch(JSONException e){
                                userService.getJsonError("-99","JSON Error",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
                                return sysError(e.getMessage());
                            }
                            //save each file status
                            connection = jdbcTemplate.getDataSource().getConnection();
                            connection.setAutoCommit(false);
                            cs = connection.prepareCall("{ call PACK_DOCUMENT.proc_insert_gstupload_document(?,?,?,?,?,?,?,?,?,?,?,?) }");
                            cs.setString(1, perfiosReqResDto.getUserid());
                            cs.setString(2, perfiosTransactionId);
                            cs.setString(3, String.valueOf(docId));
                            cs.setString(4, docUUID);
                            cs.setString(5, req_status);
                            cs.setString(6, docType);
                            cs.setString(7,fileName);
                            cs.setString(8, doc_status);
                            cs.setString(9, resultOut);
                            cs.setString(10, remarks);
                            cs.setString(11,perfiosReqResDto.getEntered_by());


                            cs.registerOutParameter(12, 2005);
                            cs.execute();
                            //either 0 or 1: fail or success
                            final String returnData = cs.getString(12);
                            connection.close();
                            cs.close();
                            if(returnData.equals("1")){Utility.print("File Response saved successfully.");
                            }else{Utility.print("File Response failed to save.");
                            }

                            if(failCount>0){
                                return userError("failed to upload:docUUID:"+docUUID+","+remarks);
                            }
                        }else{
                            return sysError("Invalid Document for UUID:" + docUUID);
                        }
                    }
                }catch(SQLException e){
                    userService.getJsonError("-99","Error-SQLException",g_error_msg,e.getMessage(),"99",channel,action,userName,userName,module,"E");
                    return sysError("SQLException:"+e.getMessage());
                }catch (FileNotFoundException e){
                    userService.getJsonError("-99","Error-FileNotFoundException",g_error_msg,e.getMessage(),"99",channel,action,userName,userName,module,"E");
                    return sysError("FileNotFoundException:"+e.getMessage());
                }
            }//end loop (CrmDocumentMst)
            if(allFailed){
                /*NOTE: Update transaction status F because all file failed to upload*/
                userService.updatePerfiosWebhookStatus(perfiosTransactionId,null,"F",null,null,null,null,"F","0 DOCUMENT(S) UPLOADED");
                return userError(remarks);
            }
            try{
                jsonObject = new JSONObject();
                jsonObject.put("status","0");
                jsonObject.put("response_data",jsonDocStatus);
                userService.saveJsonLog(channel,"res",action,jsonObject.toString(),userName,module);
            }catch (JSONException e){
                //
            }
            return  funcItStatementStartProcess(transactionId);
        }else{
            return userError("ITR Document Type(ITR_DIC_TYPE) not in Master Table.");
        }

    }

    //2.2 itr document status check
    public String funcCheckItUploadStatus(String transactionId) {
        //declaration//
        String message = null, title = "Transaction Status Check",perfiosTransactionId = transactionId;
        JSONObject jsonObject;
        //validations
        if (perfiosTransactionId.isEmpty()) {
            return userError(title+":"+"transactionId not found.");
        }

        int configCd = 28;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(configCd);
        if(urlConfigDto.getUserid()==null){
            return userError(title+":"+"URL Configuration Not Found for id:"+configCd);
        }

        try {
            Utility utility = new Utility();
            String ls_url,result,ls_response;
            ls_url = urlConfigDto.getUrl() + urlConfigDto.getUserid() + "/" + perfiosTransactionId;
            URL url = new URL(ls_url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("cache-control", "no-cache");
            result = utility.getURLResponse(conn);
            jsonObject= new JSONObject(result);
            System.out.println("Response Code:"+conn.getResponseCode());
            if (jsonObject.has("transactionStatus")){
                message = jsonObject.getString("transactionStatus");
            }else if(jsonObject.has("message")){
                message = jsonObject.getString("message");
            }
            if(conn.getResponseCode()==200){
		/*{
			"clientperfiosTransactionId": "ratnaaFin_It_upload",
			"errorCode": null,
			"message": null,
			"statementsStatus": [
				{"fileName": "file1618804804349448909151.pdf","fileType": "ITR_PDF", "message": null, "status": "READY_FOR_PROCESSING"},
				{"fileName": "file1612980834279608532210.pdf","fileType": "ITR_PDF", "message": null, "status": "READY_FOR_PROCESSING"}
			],
			"perfiosTransactionId": "PIJOC3CVZUKG2KTPCGFCM",
			"transactionStatus": "INITIATED"
		}*/
		/*message parameter value will be following
			COMPLETED:                          Transaction completed successfully
			ERROR:                              There was an error processing the transaction
			INITIATED:                          Perfios transaction was initiated
			PROCESSING:                         GST data parsing is in progress
			REPORT_GENERATION_READY:            GST data parsing complete, ready for report generation
			REPORT_GENERATION_FAILED:           GST Report generation failed
			TRANSACTION_COMPLETE_CALLBACK_FAILED: Transaction complete callback failed
			ERROR_SERVER_SHUTDOWN:              Status updated on account of server shutdown
		*/
                if(message.equals("INITIATED")){
                    Utility.print("status response:"+jsonObject.toString());
                    return "success";
                }
            }
            return userError(title+":"+message);
        }catch (Exception e) {
            return  sysError(e.getMessage());
        }
    }

    //2.3) itr uploaded document start process for report
    public String funcItStatementStartProcess(String transactionId){
        //declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String      channel="W", userName="",result, perfiosTransactionId,title;
        channel =null;  result =null; perfiosTransactionId = null; title = "Start Process:";
        /*end declaration*/
        String module = "ITR Document Process",action = "itrUploadProcess",
                requestData = "{\"perfiosTransactionId\":\""+transactionId+"\"}";

        //read requestdata
        perfiosTransactionId   = transactionId;
        if(perfiosTransactionId==null){
            return userError(title+"perfiosTransactionId Not Found");
        }
        userService.saveJsonLog(channel,"req",action,requestData,userName,module);
        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid()==null){
            return userError(title+"Initial ITR Upload Transaction Not Found");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            return userError(title+"Transaction already in failed status, Unable to process.");
        }

        //get url dtl
        int urlConfigID = 29;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(urlConfigID);
        if(urlConfigDto.getUrl() == null){
            return userError(title+"URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            return userError(title+"URL userID Not Found.("+urlConfigID+")");
        }
        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid()+"/"+perfiosTransactionId;
        Utility.print("Request Sending URL generated:"+sendURL);
        try{
            URL url = new URL(sendURL);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("cache-control", "no-cache");
            result = utility.getURLResponse(conn);
            jsonObject1= new JSONObject(result);
            System.out.println("Response Code:"+conn.getResponseCode());
            Utility.print("==RESPONSE==");
            utility.print(result);
            if(conn.getResponseCode()==200){
                /*
                {success: ”true”,transactionId: ”PCPTSTTO09IM3PR3VUTDP”}
                */
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
                return "success";
            }else{
                /*{
                    code: ”TransactionIdNotFound”,
                    message: ”We could not find the Perfios Transaction Id referred to by the client”,
                    success: ”false”}
                */
                String message=null,code=null;
                if(jsonObject1.has("code")){
                    code = jsonObject1.getString("code");
                }
                if(jsonObject1.has("message")){
                    message = jsonObject1.getString("message");
                }
                userService.getJsonError("-99","Error in GST Statement Process.",message,code+": "+message,"99",channel,action,requestData,userName,module,"U");
                return userError(title+message);
            }
        }catch (IOException e){
            userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            return sysError(title+e.getMessage());
        }catch (Exception e){
            userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            return sysError(title+e.getMessage());
        }
    }

    /**Bank Document**/
    //3.1)bank document upload process
    public String funcBankStatementUpload(String transactionId){
        //declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String      channel="W", result=null,userName="", perfiosTransactionId,initiatedRequest=null,entityType=null;
        long refId=0,serialNo=0, bankLineID=0;
        /*end declaration*/
        String module = "Bank Statement upload",action = "statementUpload",
                requestData = "{\"perfiosTransactionId\":\""+transactionId+"\"}";
        //read requestData
        userService.saveJsonLog(channel,"req",action,requestData,userName,module);
        perfiosTransactionId = transactionId;
        if(perfiosTransactionId==null){
            return userError("perfiosTransactionId Not Found!");
        }
        //get transaction detail
        int urlConfigID = 32;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(urlConfigID);
        Utility.print("vendorID");
        Utility.print(urlConfigDto.getUserid());
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid()==null){
            return userError("Initial Bank Statement Upload Transaction Not Found");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            return userError("Transaction Status Found Failed, Unable to process.");
        }

        //check valid status
        String statusCheck  = funcStatementUploadStatus(perfiosTransactionId,perfiosReqResDto.getUserid());
        if(!statusCheck.equals("success")){
            return statusCheck;
        }
        //get url dtl
        if(urlConfigDto.getUrl() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestData,userName,module,"U");
            return userError("URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            return userError("URL userID Not Found.("+urlConfigID+")");
        }
        String sendURL = urlConfigDto.getUrl() ; //+ "" + urlConfigDto.getUserid()+"/"+perfiosTransactionId;
        Utility.print("Request Sending URL generated:"+sendURL);
        //get blob file data
        refId 	 			= perfiosReqResDto.getRef_tran_cd();
        serialNo 			= perfiosReqResDto.getRef_sr_cd();
        entityType          = perfiosReqResDto.getEntity_type();
        initiatedRequest	= perfiosReqResDto.getInitiated_req();

        try {
            jsonObject2	= new JSONObject(initiatedRequest);
            bankLineID	= jsonObject2.getJSONObject("request_data").getLong("bankLineID");
        }catch (JSONException e){
            return userError("Error while getting Initiated Request."+e.getMessage());
        }
        List<CrmMiscMst> docTypeList = userService.findByCategory("BANK_DOC_TYPE");//userService.getDocMstListByDocType("STMT");
        long docId = 0;
        int docMstSize = docTypeList.size();
        Utility.print("document mst row count for Statement:"+docMstSize); //expect:5 from CRM_DOCCUMENT_MST

        int docMstCount = 0;
        if(docMstSize>0){
            //loop for Document Mst traverse for doc type = STMT
            String docType,resultOut,fileName,errorCode,errorMessage,req_status,doc_status,docUUID,docPwd,docTitle,docName;
            String  remarks =null;
            Boolean successBool=false;
            Blob blobdata = null;
            Boolean allFailed = true;
            CRMAppDto crmObjPII = userService.findAppByID(3);
            //phase:1
            JSONObject jsonDocStatus = new JSONObject();
            for(CrmMiscMst crmMiscMst:docTypeList){

                docId       = Long.parseLong(crmMiscMst.getData_value()); //crmMiscMst.getTran_cd();
                docTitle     = crmMiscMst.getDisplay_value(); //crmMiscMst.getDoc_title();
                docMstCount = docMstCount+1;
                List<DocUploadDtl> docList = userService.getDocListByDocId(refId,serialNo,entityType,docId); //1044-19
                if(docList.isEmpty() || docList.size()<=0){
                    Utility.print("No row in document_Dtl for:"+refId+"-"+docId);
                    if(remarks==null){
                        remarks = "Not found any verified documents for this transaction.";
                    }
                    continue;
                }
                try{
                    int cnt = 0;
                    int failCount = 0;
                    for(DocUploadDtl docUploadDtl:docList){
                        cnt+=1;
                        docUUID = docUploadDtl.getDoc_uuid();
                        DocUploadBlobDtl docUploadBlobDtl = userService.findDocByUUIDBankID(docUUID,bankLineID);
                        if(docUploadBlobDtl==null || docUploadBlobDtl.getUuid()==null){
//                            userService.getJsonError("-99","Uploading Document Data Not found.",g_error_msg,"Document Data Not found for UUID:"+docUUID,"99",channel,action,requestData,userName,module,"U");
//                            return userError("Document's details are not found for UUID:"+docUUID);
                            continue;
                        }
                        if(docUploadBlobDtl.getDocContentType()==null){
                            return userError("Document Content Type Not found for UUID:\"+docUUID");
                        }

                        //Getting Document meta data
                        blobdata    = new SerialBlob(docUploadBlobDtl.getData());
                        docType     = docUploadBlobDtl.getDocContentType();
                        docPwd      = docUploadBlobDtl.getPassword();
                        docName		= docUploadBlobDtl.getDoc_name();

                        if(docPwd==null || docPwd.isEmpty()){
                            docPwd = "";
                        }else{
                            docPwd = userService.func_get_data_val(crmObjPII.getA(),crmObjPII.getB(),docPwd);
                            docPwd = Utility.perfiosBankPwdEncrypt(docPwd);
                        }

                        String docTypeExt = docType.substring(docType.indexOf('/')+1);
                        if(blobdata.getBinaryStream()!=null){
                            Utility.print("Document upload process started..!");
                            Utility.print("Document Name:"+docUploadBlobDtl.getDoc_name());

                            fileName = "file"+docId+""+cnt; //expect: file<docId><cnt><randomStamps>
                            File sendFile = utility.blobToFileConverter(blobdata,fileName,"."+docTypeExt);

                            fileName = sendFile.getName();
                            //attaching file to sendURL
                            Utility.print(perfiosReqResDto.getUserid());
                            Utility.print(perfiosTransactionId);
                            try{
                                Utility.print("File Generated to be Upload:"+fileName);
                                utility.initiateMultipart(sendURL,"UTF-8");
                                utility.addFormField("perfiosTransactionId",perfiosTransactionId);
                                utility.addFormField("vendorId",urlConfigDto.getUserid());
                                utility.addFormField("password",docPwd);
                                utility.addFilePart("file",sendFile);
                                resultOut =utility.finish();
                                utility.deleteFile();
                                Utility.print(resultOut);

                                //resultOut = "<?xml version=1.0 encoding=UTF-8?>\n<Success>\n\t<statementId>1234</statementId>\n\t<accounts>\n        <account>\n            <accountPattern>11111111</accountPattern>\n            <transactionStartDate>2016-06-27</transactionStartDate>\n            <transactionEndDate>2016-09-22</transactionEndDate>\n        </account>\n\t\t<account>\n\t\t\t<accountPattern>22222222</accountPattern>\n\t\t\t<transactionStartDate>2016-08-10</transactionStartDate>\n\t\t\t<transactionEndDate>2016-08-11</transactionEndDate>\n\t\t</account>\n\t</accounts>\n</Success>";
                                //resultOut = "<?xml version=1.0 encoding=UTF-8?>\n<Accepted>\n\t<statementId>1234</statementId>\n</Accepted>";
							/*resultOut = "<Error>\n" +
									"<code>CannotProcessFile</code>\n" +
									"<message>We could not process the statement file uploaded by the Client</message>\n" +
									"<statementErrorCode>E_DATE_RANGE</statementErrorCode>\n" +
									"</Error>";
							*/
                                //read xml response data

                                HashMap outParam= new HashMap(), inParam = new HashMap();
                                String error_msg;

                                inParam.put("perfiosTransactionId",perfiosTransactionId);
                                inParam.put("xmlResponse",resultOut);

                                outParam    =   userService.callingDBObject("procedure","pack_document.proc_readxml_stmt_upload_response",inParam);
                                if(outParam.containsKey("error")){
                                    error_msg = (String) outParam.get("error");
                                    userService.getJsonError("-99","Error while Executing callingDBObject",g_error_msg,error_msg,"99",channel,action,requestData,userName,module,"E");
                                    return sysError("Error while Executing callingDBObject:"+error_msg);
                                }
                                /** 2) get outParam values **/
                                String responseStatus,outJson;
                                responseStatus   = (String)outParam.get("responseStatus");
                                outJson          = (String)outParam.get("outJson");
                                jsonObject1      = new JSONObject(outJson);
                                if(responseStatus.equals("accepted") || responseStatus.equals("success")){
                                    allFailed = false;
                                    Utility.print("SUCCESS RESPONSE");
                                    req_status    = "true";
                                    doc_status = "U"; //uploaded 14-9(4), 14-11(1)
                                    remarks		  = "accepted";

                                }else{
                                    Utility.print("Fail to upload");
                                    failCount +=1;

                                    req_status    = "false";
                                    doc_status    = "F"; //upload  failed
                                    if(jsonObject1.has("code")){
                                        remarks = jsonObject1.getString("code");
                                    }
                                    if(jsonObject1.has("statementErrorCode") ){
                                        if(!jsonObject1.getString("statementErrorCode").isEmpty()){
                                            remarks = remarks+":"+jsonObject1.getString("statementErrorCode");
                                        }
                                    }
                                    if(jsonObject1.has("message")){
                                        remarks = remarks+":"+jsonObject1.getString("message");
                                    }
                                    if(!allFailed){
                                        String cancelStatus = funcCancelTransaction(perfiosTransactionId);
                                        if(cancelStatus.equals("success")){
                                            System.out.println("Transaction Cancelled Successfully.");
                                        }else{
                                            System.out.println(cancelStatus);
                                        }
                                    }
                                }
                                jsonDocStatus.put(fileName,jsonObject1);
                            }catch (IOException e){
                                userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,null,"E");
                                return sysError(e.getMessage());
                            }catch(JSONException e){
                                userService.getJsonError("-99","JSON Error",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
                                return sysError(e.getMessage());
                            }
                            //save each file status
                            connection = jdbcTemplate.getDataSource().getConnection();
                            connection.setAutoCommit(false);
                            cs = connection.prepareCall("{ call PACK_DOCUMENT.proc_insert_gstupload_document(?,?,?,?,?,?,?,?,?,?,?,?) }");
                            cs.setString(1, perfiosReqResDto.getUserid());
                            cs.setString(2, perfiosTransactionId);
                            cs.setString(3, String.valueOf(docId));
                            cs.setString(4, docUUID);
                            cs.setString(5, req_status);
                            cs.setString(6, docType);
                            cs.setString(7,fileName);
                            cs.setString(8, doc_status);
                            cs.setString(9, resultOut);
                            cs.setString(10,remarks);
                            cs.setString(11,perfiosReqResDto.getEntered_by());

                            cs.registerOutParameter(12, 2005);
                            cs.execute();
                            //either 0 or 1: fail or success
                            final String returnData = cs.getString(12);
                            if(returnData.equals("1")){
                                Utility.print("File Response saved successfully.");
                            }else{
                                Utility.print("File Response failed to save.");
                            }
                            connection.close();
                            cs.close();
                            if(failCount>0){
                                return userError("failed to upload:docUUID:"+docUUID+", "+remarks);
                            }
                        }else{
                            return userError("Invalid Document for UUID:"+docUUID);
                        }
                    }
                }catch(SQLException e){
                    return sysError("SQLException:"+e.getMessage());
                }catch (FileNotFoundException e){
                    return sysError("FileNotFoundException:"+e.getMessage());
                }
            }//end loop (CrmDocumentMst)
            if(allFailed){
                /*NOTE: Update transaction status F because all file failed to upload*/
                userService.updatePerfiosWebhookStatus(perfiosTransactionId,null,"F",null,null,null,null,"F","0 DOCUMENT(S) UPLOADED");
                return userError(remarks);
            }else{
                userService.updatePerfiosWebhookStatus(perfiosTransactionId,null,"P",null,null,null,null,"P",remarks);
            }
//			return "success";
            return  funcBankStatementStartProcess(transactionId);
        }else{
            userService.getJsonError("-99","Master Document Type Not Found.(Statement)",g_error_msg,"Statement Document Type not in Master Table.","99",channel,action,requestData,userName,module,"U");
            return userError("Statement Document Types(BANK_DOC_TYPE) are not found in Master Table.");
        }
    }

    //3.2) bank uploaded document start process for report
    public String funcBankStatementStartProcess(String transactionId){
        //declaration
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String     channel="W", userName="",result, perfiosTransactionId;
        /*end declaration*/
        String module = "Bank Document Process",action = "statementUploadProcess",
                requestData = "{\"perfiosTransactionId\":\""+transactionId+"\"}";

        //read requestData
        perfiosTransactionId   = transactionId;
        if(perfiosTransactionId==null){
            return userError("perfiosTransactionId Not Found!");
        }
        userService.saveJsonLog(channel,"req",action,requestData,userName,module);

        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getTransaction_id() == null||perfiosReqResDto.getUserid() == null){
            return userError("Initial Bank Statement Upload Transaction Not Found");
        }
        if(perfiosReqResDto.getStatus().equals("F")){
            return userError("Transaction Status Found Failed, Unable to process.");
        }

        //get url dtl
        int urlConfigID = 33;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(urlConfigID);
        if(urlConfigDto.getUrl() == null){
            return userError("URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            return userError("URL userID Not Found.("+urlConfigID+")");
        }
        String sendURL = urlConfigDto.getUrl() ; //+ "" + urlConfigDto.getUserid()+"/"+perfiosTransactionId;
        Utility.print("Request Sending URL generated:"+sendURL);
        /*sample payload*/
		/*
		<?xml version="1.0" encoding="UTF-8"?>
		<payload>
		<apiVersion>2.1</apiVersion>
		<perfiosTransactionId>ZZZZ1234567890123</perfiosTransactionId>
		<vendorId>vendorId</vendorId>
		</payload>
		*/
        String payload = "<payload><apiVersion>2.1</apiVersion>"+
                "<perfiosTransactionId>"+perfiosTransactionId+"</perfiosTransactionId>" +
                "<vendorId>"+urlConfigDto.getUserid()+"</vendorId>"+
                "</payload>";
        try{
            payload = payload.replaceAll("\n","");
            Utility.print("==condensed payload==");
            Utility.print(payload);
            String dig,signature, mappingData;

            dig       = Utility.makeDigest(payload);
            signature = Utility.perfiosDataEncryptPvt(dig);

            HashMap<String, String> requestMapping = new HashMap<String, String>();
            requestMapping.put("payload",payload);
            requestMapping.put("signature",signature);
            byte[] postData =Utility.keyValueMappingString(requestMapping).getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;
            URL url = new URL(sendURL);
            Utility.print("send URL: "+sendURL);
            HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);
            try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write( postData );
            }

            Utility.print("Response code:"+conn.getResponseCode());
            result = Utility.getURLResponse(conn);
            Utility.print(result);

                    /*##############possible response############*/
            /*[e-pdf success]
                <?xml version=”1.0” encoding=”UTF-8”?>
                <Success>
                <message>Transaction completed successfully</message>
                </Success>
            */

            /*--[scanned accepted]
                <?xml version=”1.0” encoding=”UTF-8”?>
                <Accepted>
                <message>Transaction submitted for processing</message>
                </Accepted>
            */

            /*--[error]
            <?xml version=”1.0” encoding=”UTF-8”?>
            <Error>
            <code>MethodNotAllowed</code>
            <message>This API supports only POST</message>
            </Error>
            */
            /*##########################################*/
            /** Read XML response **/
            HashMap inParam = new HashMap(); HashMap outParam = new HashMap();
            String sqlstmt,xmlErrorCode=null,xmlResponseMessage,xmlResponseStatus,xmlPerfiosTranId;
            sqlstmt = "select  existsnode(value(hdr_data),'/Accepted') as is_accepted, "+
                    "existsnode(value(hdr_data),'/Success') as is_success, "+
                    "existsnode(value(hdr_data),'/Error') as is_error, "+
                    "extractvalue(value(hdr_data),'/Error/code/text()') as err_cd, "+
                    "extractvalue(value(hdr_data),'/Error/code/text()') as err_msg, "+
                    "extractvalue(value(hdr_data),'/Accepted/message/text()') as accepted_msg, "+
                    "extractvalue(value(hdr_data),'/Success/message/text()') as success_msg "+
                    "from table(xmlsequence(extract(xmltype('"+result+"'),'*')))hdr_data";

            //set parameter
            inParam.put("sql",sqlstmt.replaceAll("\n",""));
            //execute statement
            outParam = userService.callingDBObject("sql","XMLResponseProcessUpload",inParam);

            if(outParam.containsKey("error")){
                String error_msg = (String) outParam.get("error");
                userService.getJsonError("-99","Error while Executing callingDBObject",g_error_msg,error_msg,"99",channel,action,requestData,userName,module,"E");
                return sysError(error_msg);
            }
            xmlPerfiosTranId    = perfiosTransactionId;
            xmlResponseStatus	= (String) outParam.get("xmlResponseStatus"); /*success|error|accepted*/
            xmlResponseMessage  = (String) outParam.get("xmlResponseMessage");
            if(xmlResponseStatus.equals("error")){
                xmlErrorCode = (String) outParam.get("xmlErrorCode");
            }
            Utility.print("TranId :"+xmlPerfiosTranId);
            Utility.print("Status :"+xmlResponseStatus);
            Utility.print("ErrCode:"+xmlErrorCode);
            Utility.print("Msg    :"+xmlResponseMessage);
            /**End read xml response**/
            if(xmlResponseStatus.equals("success") || xmlResponseStatus.equals("accepted"))
            {
			/*<Accepted><message>Transaction submitted for processing</message></Accepted> OR
			  <Success><message>Transaction completed successfully</message></Success>
			 */
//				jsonObject2.put("status", "0");
//				jsonObject1.put("status",xmlResponseStatus);
//				jsonObject1.put("message",xmlResponseMessage);
//				jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
//				return jsonObject2.toString();
                return "success";
            }else{
			/*<?xml version=”1.0” encoding=”UTF-8”?>
			   <Error>
				<code>MethodNotAllowed</code>
				<message>This API supports only POST</message>
			   </Error>*/
                userService.getJsonError("-99","Error in Bank Statement Process.",xmlResponseMessage,xmlErrorCode+": "+xmlResponseMessage,"99",channel,action,requestData,userName,module,"U");
                return userError(xmlErrorCode+": "+xmlResponseMessage);
            }
        }catch (IOException e){
            userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            return sysError(e.getMessage());
        }catch (Exception e){
            userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            return sysError(e.getMessage());
        }
    }

    //3.3) bank document check status
    public String funcStatementUploadStatus(String perfiosTransactionId, String userId) {
        //declaration//
        String message = null, title = "Transaction Status Check";
        JSONObject jsonObject, jsonObject1, jsonObject2;
        //validations
        if (perfiosTransactionId.isEmpty()) {
            return userError(title+":"+"transactionId not found.");
        }

        URLConfigDto urlConfigDto = userService.findURLDtlByID(34);
        if(urlConfigDto.getUserid()==null){
            return userError(title+":"+"URL Configuration Not Found.");
        }
        if(urlConfigDto.getUrl() == null){
            return userError(title+":"+"URL Not Found.");
        }
        if(urlConfigDto.getKey()== null){
            return userError(title+":"+"URL Key Not Found.");
        }

        //urlConfigDto.getKey()
        String payload = "<payload><apiVersion>2.1</apiVersion>"+
                "<txnId>"+userId+"</txnId>" +
                "<vendorId>"+urlConfigDto.getUserid()+"</vendorId>"+
                "</payload>";
        try {
            jsonObject2 = new JSONObject();
            Utility utility = new Utility();
            String sendURL,result;
            sendURL = urlConfigDto.getUrl(); // + urlConfigDto.getUserid() + "/" + transactionId;
            Utility.print("Statement Upload Status generated URL:" + sendURL);

            payload = payload.replaceAll("\n","");
            Utility.print("==condensed payload==");
            Utility.print(payload);
            String dig,signature, mappingData;

            dig       = Utility.makeDigest(payload);
            signature = Utility.perfiosDataEncryptPvt(dig);

            HashMap<String, String> requestMapping = new HashMap<String, String>();
            requestMapping.put("payload",payload);
            requestMapping.put("signature",signature);
            byte[] postData =Utility.keyValueMappingString(requestMapping).getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;
            URL url = new URL(sendURL);
            Utility.print("send URL: "+sendURL);
            HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);
            try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write( postData );
            }

            Utility.print("Response code:"+conn.getResponseCode());
            result = Utility.getURLResponse(conn);
            Utility.print(result);
            try{
                jsonObject = new JSONObject(Utility.xmlToJson(result));
                if(jsonObject.has("Status")){
                    String status = null,files = null,reason=null;
                    status      = jsonObject.getJSONObject("Status").getJSONObject("Part").getString("status");
                    reason		= jsonObject.getJSONObject("Status").getJSONObject("Part").optString("reason");
                    reason		= reason==null?"":", "+reason;

                    if(status.equals("pending")){
                        return "success";
                    }else{
                        return userError(title+":"+status+reason);
                    }
                }else {
                    if(jsonObject.has("message")){
                        return userError(title+":"+jsonObject.getString("message"));
                    }
                }
            }catch(JSONException e) {
                return sysError("JsonException:"+e.getMessage());
            }
        }catch (Exception e) {
            return sysError(e.getMessage());
        }
        return jsonObject.toString();
    }

    //3.4) cancel transaction when upload is partial
    public String funcCancelTransaction(String perfiosTransactionId) {
       //declaration//
        String message = null;
        JSONObject jsonObject, jsonObject1, jsonObject2;
        //end declaration//

        //validations
        if(perfiosTransactionId.isEmpty()){
            return userError("Cancel Transaction:transactionId Not found");
        }

        int urlConfigId = 35;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(35);
        if(urlConfigDto.getUrl() == null){
            return userError("Cancel Transaction:URL Not Found.("+urlConfigId+")");
        }
        if(urlConfigDto.getUserid()==null){
            return userError("URL Configuration Not Found.("+urlConfigId+")");
        }

        //urlConfigDto.getKey()
        String payload = "<payload><apiVersion>2.1</apiVersion>"+
                "<perfiosTransactionId>"+perfiosTransactionId+"</perfiosTransactionId>" +
                "<vendorId>"+urlConfigDto.getUserid()+"</vendorId>"+
                "</payload>";
        try {
            jsonObject2 = new JSONObject();
            Utility utility = new Utility();
            String sendURL,result;
            sendURL = urlConfigDto.getUrl(); // + urlConfigDto.getUserid() + "/" + transactionId;
            Utility.print("generated URL:" + sendURL);

            payload = payload.replaceAll("\n","");
            Utility.print("==condensed payload==");
            Utility.print(payload);
            String dig,signature, mappingData;

            dig       = Utility.makeDigest(payload);
            signature = Utility.perfiosDataEncryptPvt(dig);

            HashMap<String, String> requestMapping = new HashMap<String, String>();
            requestMapping.put("payload",payload);
            requestMapping.put("signature",signature);
            byte[] postData =Utility.keyValueMappingString(requestMapping).getBytes( StandardCharsets.UTF_8 );
            int postDataLength = postData.length;
            URL url = new URL(sendURL);
            Utility.print("send URL: "+sendURL);
            HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);
            try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write( postData );
            }

            Utility.print("Response code:"+conn.getResponseCode());
            result = Utility.getURLResponse(conn);
            try{
                jsonObject = new JSONObject(Utility.xmlToJson(result));
                /*
				{"Success":{"message":"some_message"}}
				{"Error":{"code":"MethodNotAllowed","message":"This API supports only POST"}}
				*/
                if(jsonObject.has("Success")){
                    int rowUpdate = updateGstDocumentStatus(perfiosTransactionId,"STMT_CANCEL_TRANSACTION",jsonObject.toString());
                    return  "success";
                }else if(jsonObject.has("Error")){
                    return userError("Cancel Transaction:"+jsonObject.getJSONObject("Error").getString("message"));
                }else{
                    return userError("Cancel Transaction:"+result);
                }
            }catch(JSONException e) {
                return sysError("jsonException:"+e.getMessage());
            }
        }catch (Exception e) {
            return sysError(e.getMessage());
        }
    }

    //##CORPOSITORY##//
    //1) save webhook data
    public String processPlaceOrderWebhook(String webhookData,String transactionId,String status,String webhookType,String msg){
        long refId=0;
        String compId=null,createJson=null,result=null;
        String processResponse=utility.getWebhookProcessStructure();
        String processStatus="success",processLoc=className+"/processPlaceOrderWebhook()",processTitle="",processDesc="",processWebhook="success";
        Connection connection;
        CallableStatement cs;
        try{
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            cs = connection.prepareCall("{ call PACK_CORPOSITORY.PROC_UPDATE_CORPOSITORY_WEBHOOK(?) }");
            cs.setString(1, webhookData);
            cs.execute();
            connection.close();
            cs.close();
        }catch (SQLException e){
            processStatus = "failed";
            processTitle  = "SQLException";
            processDesc   = "(A)error:"+e.getMessage();
        }catch (Exception e){
            processStatus = "failed";
            processTitle  = "Exception";
            processDesc   = "(B)error:"+e.getMessage();
        }
        if(processStatus.equalsIgnoreCase("failed")){
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return  processResponse;
        }
        if(webhookType.equals("10") && status.equals("1") && msg.equalsIgnoreCase("success")){
            /**fetch financial detail**/
            try{
                String sql = "select a.request_type, a.company_id, a.company_name, a.transaction_id,a.status," +
                        "b.ref_tran_cd,b.ref_sr_cd from los_corpository_apis_dtl a, los_corpository_apis_hdr b " +
                        "where a.tran_cd = b.tran_cd " +
                        "and a.request_type = 'creditOrder' " +
                        "and a.status = 'S' " +
                        "and a.webhook_json is not null and a.transaction_id = ?1"+
                        "and not exists(select * from los_corpository_apis_hdr c " +
                        "where c.ref_tran_cd = b.ref_tran_cd " +
                        "and c.ref_sr_cd = b.ref_sr_cd " +
                        "and c.request_type = 'financial_detail' " +
                        "and c.status = 'S')";
                sql = sql.replace("?1",transactionId);
                connection = jdbcTemplate.getDataSource().getConnection();
                connection.setAutoCommit(false);
                try(Statement stmt = connection.createStatement()){
                    ResultSet rs = stmt.executeQuery(sql);
                    while (rs.next()){
                        refId          = rs.getLong("ref_tran_cd");
                        compId         = rs.getString("company_id");
                        createJson     = "{\"request_data\":{ \"refID\":\""+refId+"\",\"companyID\":\""+compId+"\",\"transactionID\":\""+transactionId+"\"}}";
                        Utility.print("createJson:"+new JSONObject(createJson).toString());
                        return funcGetFinancialDetail(createJson);
                    }
                    result = "{\"status\":\"success\"}";
                }catch (Exception e){
                    processStatus = "failed";
                    processTitle  = "Exception";
                    processDesc   = "(C)error:"+e.getMessage();
                }
                if(processStatus.equalsIgnoreCase("failed")){
                    processResponse = processResponse.replace("<status>",processStatus);
                    processResponse = processResponse.replace("<webhook>",processWebhook);
                    processResponse = processResponse.replace("<location>",processLoc);
                    processResponse = processResponse.replace("<title>",processTitle);
                    processResponse = processResponse.replace("<desc>",processDesc);
                    result =   processResponse;
                }
                return  result;
            }catch (SQLException e) {
                processStatus = "failed";
                processTitle  = "Exception";
                processDesc   = "(D)error:"+e.getMessage();
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<webhook>",processWebhook);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                return processResponse;
            }
        }
        return "{\"status\":\"success\"}";
    }
    //2) get financial detail
    public String funcGetFinancialDetail(String requestData){
        Utility.print("funcGetFinancialDetail");
        JSONObject jsonObject =null,jsonObject1=null,jsonTokenData=null;
        JSONObject jsonURLResponse = new JSONObject();
        String processResponse=utility.getWebhookProcessStructure();
        String processStatus="success",processLoc=className+"/funcGetFinancialDetail()",processTitle="",processDesc="",processWebhook="success";

        final String requestType = "financial_detail",module = "financial",action ="getFinancialData",userName="";

        String filter=null, sendURL,result,channel="W",tokenResult=null,
                authTokenID=null,v_tag=null,enitityName=null,data=null,
                resStatus=null;
        long authUserID = 0;
        long refID=0,companyID=0,transactionID=0;
        String companyName = null,webhookURL=null;
        int configCd = 39;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(configCd);

        LosCorpositoryAPI losCorpositoryAPI = new LosCorpositoryAPI();

        userService.saveJsonLog(channel,"req",action,requestData,userName,module);
        /*read requestdata*/
        /*
		{
       	"api_auth_token": "fb757b13218a749de02ac9e4f1696fa8",
    	"user_id": 121,
    	"company_ids":[77372],
    	"reference_id":21284
		}
	 	*/
        try{
            jsonObject1 = new JSONObject();
            jsonObject  = new JSONObject(requestData);
            channel = "W";
            jsonObject = jsonObject.getJSONObject("request_data");
            if(jsonObject.has("refID")){
                v_tag = "refID";
                refID = jsonObject.getLong(v_tag);
            }
            if(jsonObject.has("companyID")){
                v_tag = "companyID";
                companyID = jsonObject.getLong(v_tag);
            }
            if(jsonObject.has("transactionID")){
                v_tag         = "transactionID";
                transactionID = jsonObject.getLong(v_tag);
            }
            if(refID<=0){
                userService.getJsonError("-99","Data Error","refID not found","refID not found","99",channel,action,requestData,userName,module,"U");
                processStatus = "failed";
                processTitle  = "parameter not found";
                processDesc   = "(A)error:refID is missing";
            }
            if(companyID<=0){
                userService.getJsonError("-99","Data Error","companyID not found","companyID not found","99",channel,action,requestData,userName,module,"U");
                processStatus = "failed";
                processTitle  = "parameter not found";
                processDesc   = "(B)error:companyID is missing";
            }
            if(transactionID<=0){
                userService.getJsonError("-99","Data Error","transactionID not found","transactionID not found","99",channel,action,requestData,userName,module,"U");
                processStatus = "failed";
                processTitle  = "parameter not found";
                processDesc   = "(C)error:transactionID is missing";
            }
        }catch (JSONException e){
            userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage()+" tag:"+v_tag,"99",channel,action,requestData,userName,module,"E");
            processStatus = "failed";
            processTitle  = "JSONException";
            processDesc   = "(D)error:"+e.getMessage();
        }catch(Exception e){
            userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            processStatus = "failed";
            processTitle  = "Exception";
            processDesc   = "(E)error:"+e.getMessage();
        }
        if(processStatus.equalsIgnoreCase("failed")){
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }

        //other data requirements
        if(urlConfigDto==null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Details Not Found.","99",channel,action,requestData,userName,module,"U");
            processStatus = "failed";
            processTitle  = "Url Configuration not found";
            processDesc   = "(F)error:Detail not found for id:"+configCd;
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }

        sendURL = urlConfigDto.getUrl();
        Utility.print("send url:"+sendURL);
        if((sendURL==null || sendURL.isEmpty())){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestData,userName,module,"U");
            processStatus = "failed";
            processTitle  = "Url Configuration not found";
            processDesc   = "(G)error:sendURL not found for id:"+configCd;
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }
        //GET ACTIVE TOKEN
        tokenResult = getCorpositoryTokenDetail_v2(userName);
        try{
            jsonTokenData = new JSONObject(tokenResult);
            if(jsonTokenData.getString("status").equals("0")){
                jsonTokenData = jsonTokenData.getJSONObject("response_data").getJSONObject("data");
                authTokenID   = jsonTokenData.getString("api_auth_token");
                authUserID    = jsonTokenData.getLong("user_id");
            }else{
                //if getting some error
                return  tokenResult;
            }
        }catch (JSONException e){
            userService.getJsonError("-99","Error while fetching Corpository token details",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            processStatus = "failed";
            processTitle  = "JSONException";
            processDesc   = "(H)error:"+e.getMessage();
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }
        String createJson = "{\n" +
                "\"api_auth_token\": \""+authTokenID+"\",\n"+
                "\"user_id\": \""+authUserID+"\",\n"+
                "\"company_ids\":["+companyID+"],\n" +
                "\"reference_id\":"+transactionID+"\n" +
                "}";
        try{
            URL postURL = new URL(sendURL);
            HttpsURLConnection conn = (HttpsURLConnection)postURL.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();

            os.write(createJson.getBytes());
            os.flush();
            os.close();
            result = Utility.getURLResponse(conn);
            userService.saveJsonLog(channel,"res",action,result,userName,module);
            Utility.print("Response Code  :"+conn.getResponseCode());
            Utility.print("Response Result:"+result);
            try{
                jsonURLResponse = new JSONObject(result);
            }catch (JSONException e){
                //avoid error
            }
            if(conn.getResponseCode()==conn.HTTP_OK){
                long dataStatus = jsonURLResponse.getLong("status");
                if(dataStatus==1){
                    resStatus = "S";
                    jsonObject1.put("status","0");
                    jsonObject1.put("response_data",jsonURLResponse);
                }else{
                    resStatus = "F";
                    userService.getJsonError("-99","Error while fetching financial detail",g_error_msg,result,"99",channel,action,requestData,userName,module,"U");
                    processStatus = "failed";
                    processTitle  = "API response data field status is invalid, expected is:1";
                    processDesc   = "(I)error:api result>>"+result;
                }
            }else {
                resStatus = "F";
                userService.getJsonError("-99","Error while fetching financial detail",g_error_msg,result,"99",channel,action,requestData,userName,module,"U");
                processStatus = "failed";
                processTitle  = "API HTTP status:"+conn.getResponseCode();
                processDesc   = "(J)error:api result>>"+result.replace("\"","");
            }
            losCorpositoryAPI.setRequest_type(requestType);
            losCorpositoryAPI.setInitiated_req1(requestData);
            losCorpositoryAPI.setInitiated_req2(createJson);
            losCorpositoryAPI.setStatus(resStatus);
            losCorpositoryAPI.setApi_res(result);
            losCorpositoryAPI.setRef_tran_cd(refID);
            losCorpositoryAPI.setRef_Sr_cd(Long.valueOf(1));
            losCorpositoryAPI.setEntity_type("L");
            losCorpositoryAPI.setRemarks(resStatus.equals("S")?"success":"failed");
            userService.saveCorpositoryAPI(losCorpositoryAPI);
        }catch(JSONException e){
            userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            processStatus = "failed";
            processTitle  = "JSONException";
            processDesc   = "(K)error:"+e.getMessage();
        }
        catch (Exception e) {
            userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            processStatus = "failed";
            processTitle  = "Exception";
            processDesc   = "(L)error:"+e.getMessage();
        }
        if(processStatus.equalsIgnoreCase("failed")){
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }
        return "{\"status\":\"success\"}";
    }

    public String getCorpositoryTokenDetail_v2(String userName){
        String processResponse=utility.getWebhookProcessStructure();
        String processStatus="success",processLoc=className+"/getCorpositoryTokenDetail_v2()",processTitle="",processDesc="",processWebhook="success";

        final String requestType = "login", channel="W",
                module      = "lead/external", action="corpository/login",
                loginUserID = userService.getLoginUserID(userName);
        JSONObject jsonObject  = new JSONObject();
        JSONObject jsonURLResponse = new JSONObject();
        LosCorpositoryAPI losCorpositoryAPI = new LosCorpositoryAPI();
        String sendURL = null,loginID = null,loginPassword = null,resStatus=null,data=null;
        String requestData = "fetching token";
        int configCd = 38;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(38);

        if(urlConfigDto==null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Details Not Found.","99",channel,action,requestData,userName,module,"U");
            processStatus = "failed";
            processTitle  = "Url Configuration not found.";
            processDesc   = "(A)error:Detail not found for id:"+configCd;
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }
        sendURL = urlConfigDto.getUrl();
        Utility.print("send url:"+sendURL);
        if((sendURL==null || sendURL.isEmpty())){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestData,userName,module,"U");
            processStatus = "failed";
            processTitle  = "Url Configuration not found.";
            processDesc   = "(B)error:sendURL not found for id:"+configCd;
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }
        loginID = urlConfigDto.getUserid();
        if(loginID==null || loginID.isEmpty()){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestData,userName,module,"U");
            processStatus = "failed";
            processTitle  = "Url Configuration not found.";
            processDesc   = "(C)error:Userid not found for id:"+configCd;
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }
        loginPassword = urlConfigDto.getKey();
        if(loginPassword==null || loginPassword.isEmpty()){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL key Not Found.","99",channel,action,requestData,userName,module,"U");
            processStatus = "failed";
            processTitle  = "Url Configuration not found.";
            processDesc   = "(D)error:User key not found for id:"+configCd;
            processResponse = processResponse.replace("<status>",processStatus);
            processResponse = processResponse.replace("<webhook>",processWebhook);
            processResponse = processResponse.replace("<location>",processLoc);
            processResponse = processResponse.replace("<title>",processTitle);
            processResponse = processResponse.replace("<desc>",processDesc);
            return processResponse;
        }

        data = getCorpositoryTokenDetail();
        Utility.print("Token Search Result From DB:"+data);
        if(data.equalsIgnoreCase("not_found")){
            try {
                String url,result,response,createJson;
                createJson = "{\"request\": \"login\",\"para\": {\"user-id\": \""+loginID+"\",\"password\": \""+loginPassword+"\"}}";
                requestData = createJson;
                userService.saveJsonLog(channel,"req",action,createJson,userName,module);
                Utility.print("Request Json:"+createJson);
                Utility.print("send URL:"+sendURL);
                URL postURL = new URL(sendURL);
                HttpsURLConnection conn = (HttpsURLConnection) postURL.openConnection();
                conn.setRequestMethod("POST");
                conn.addRequestProperty("content-Type", "application/json");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();

                os.write(createJson.getBytes());
                os.flush();
                os.close();
                result = Utility.getURLResponse(conn);
                userService.saveJsonLog(channel,"res",action,result,userName,module);
                Utility.print("Response Code  :"+conn.getResponseCode());
                Utility.print("Response Result:"+result);

                try{
                    jsonURLResponse = new JSONObject(result);
                }catch (JSONException e){
                    //avoind error
                }
                if(conn.getResponseCode()==conn.HTTP_OK){
                    if(jsonURLResponse.getLong("status")==1){
                        resStatus = "S";
                        jsonObject.put("status","0");
                        jsonObject.put("response_data",jsonURLResponse);
                    }else{
                        resStatus = "F";
                        userService.getJsonError("-99","Error response in login(corpository)",g_error_msg,result,"99",channel,action,requestData,userName,module,"U");
                        processStatus = "failed";
                        processTitle  = "API Invalid status,expected status:1";
                        processDesc   = "(E)error: api result>>"+result;
                        processResponse = processResponse.replace("<status>",processStatus);
                        processResponse = processResponse.replace("<webhook>",processWebhook);
                        processResponse = processResponse.replace("<location>",processLoc);
                        processResponse = processResponse.replace("<title>",processTitle);
                        processResponse = processResponse.replace("<desc>",processDesc);
                    }
                    losCorpositoryAPI.setRequest_type(requestType);
                    losCorpositoryAPI.setInitiated_req2(createJson);
                    losCorpositoryAPI.setStatus(resStatus);
                    losCorpositoryAPI.setApi_res(result);
                    losCorpositoryAPI.setRef_tran_cd(null);
                    losCorpositoryAPI.setRef_Sr_cd(null);
                    losCorpositoryAPI.setEntity_type(null);
                    losCorpositoryAPI.setRemarks(resStatus.equals("S")?"success":"failed");
                    losCorpositoryAPI.setEntered_by(loginUserID);
                    losCorpositoryAPI.setLast_entered_by(loginUserID);
                    userService.saveCorpositoryAPI(losCorpositoryAPI);
                    if(processStatus.equalsIgnoreCase("failed")){
                        return processResponse;
                    }
                    return jsonObject.toString();
                }else{
                    resStatus = "F";
                    userService.getJsonError("-99","Response Status:"+conn.getResponseCode(),g_error_msg,result,"99",channel,action,requestData,userName,module,"U");
                    losCorpositoryAPI.setRequest_type(requestType);
//                    losCorpositoryAPI.setInitiated_req1(requestData);
                    losCorpositoryAPI.setInitiated_req2(createJson);
                    losCorpositoryAPI.setStatus(resStatus);
                    losCorpositoryAPI.setApi_res(result);
                    losCorpositoryAPI.setRef_tran_cd(null);
                    losCorpositoryAPI.setRef_Sr_cd(null);
                    losCorpositoryAPI.setEntity_type(null);
                    losCorpositoryAPI.setRemarks(requestType+" failed");
                    losCorpositoryAPI.setEntered_by(loginUserID);
                    losCorpositoryAPI.setLast_entered_by(loginUserID);
                    userService.saveCorpositoryAPI(losCorpositoryAPI);
                    processStatus = "failed";
                    processTitle  = "API HTTP status is:"+conn.getResponseCode();
                    processDesc   = "(F)error: api result>>"+result;
                    processResponse = processResponse.replace("<status>",processStatus);
                    processResponse = processResponse.replace("<webhook>",processWebhook);
                    processResponse = processResponse.replace("<location>",processLoc);
                    processResponse = processResponse.replace("<title>",processTitle);
                    processResponse = processResponse.replace("<desc>",processDesc);
                    return  processResponse;
                }
            }catch(JSONException e){
                userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
                processStatus = "failed";
                processTitle  = "JSONException";
                processDesc   = "(G)error:"+e.getMessage();
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<webhook>",processWebhook);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                return processResponse;
            }catch (Exception e) {
                userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
                processStatus = "failed";
                processTitle  = "Exception";
                processDesc   = "(H)error:"+e.getMessage();
                processResponse = processResponse.replace("<status>",processStatus);
                processResponse = processResponse.replace("<webhook>",processWebhook);
                processResponse = processResponse.replace("<location>",processLoc);
                processResponse = processResponse.replace("<title>",processTitle);
                processResponse = processResponse.replace("<desc>",processDesc);
                return  processResponse;
            }
        }else{
            return  data;
        }

    }

    public String getCorpositoryTokenDetail(){
        HashMap outParam= new HashMap();
        String result=null;
        String flag=null;
        outParam   = userService.callingDBObject("procedure","pack_corpository.proc_get_active_token_detail",outParam);
        flag       = (String)outParam.get("flag");
        result       = (String)outParam.get("result");
        if(flag.equalsIgnoreCase("success")){
            return  result;
        }else{
            return flag;
        }
    }
}
