package com.ratnaafin.crm.middleware.controller;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.ratnaafin.crm.common.exception.UserNotAuthorizedException;
import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.controller.UserController;
import com.ratnaafin.crm.user.dto.CRMAppDto;
import com.ratnaafin.crm.user.dto.PerfiosReqResDto;
import com.ratnaafin.crm.user.dto.URLConfigDto;
import com.ratnaafin.crm.user.model.CrmMiscMst;
import com.ratnaafin.crm.user.model.DocUploadBlobDtl;
import com.ratnaafin.crm.user.model.DocUploadDtl;
import com.ratnaafin.crm.user.model.LosCorpositoryAPI;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/middleware")
@Controller
public class MiddlewareController {
    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String g_error_msg = "Something went wrong please try again.";
    private String g_status = null;
    private String g_err_cd = null;
    private String g_err_title = null;
    private String g_err_dtl = null;
    private String g_application = "middleware";
    public  String user_error = "Error(u):";
    public  String sys_error = "Error(e):";

    private String userError(String msg){ return user_error+msg; }
    private String sysError(String msg){ return sys_error+msg; }

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{action}", produces = { "application/json","application/json" })
    public String funcInternalFetcher(	@PathVariable(name = "module") String module,
                                          @PathVariable(name = "action") String action,
                                          @RequestHeader(name = "signature") String signature,
                                          @RequestBody String requestData)
    {
        String userName = g_application, result = null;
        module = module.toLowerCase();
        action = action.toLowerCase();
        if(!userService.isMiddlewareRequest(signature)){
            throw new UserNotAuthorizedException("99", "-99", "Not Authorized", "Not Authorized", "Not Authorized");
        }
        switch (module + "/" + action)
        {
            case "null":
                result = "";
                break;
            default:
                result = funcCallAPIOne(g_application, module, action, requestData, userName, "", "", "");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{action}/{event}", produces = { "application/json","application/json" })
    public String funcInternalFetcher(	@PathVariable(name = "module") String module,
                                          @PathVariable(name = "action") String action,
                                          @PathVariable(name = "event") String event,
                                          @RequestHeader(name = "signature") String signature,
                                          @RequestBody String requestData)
    {
        String userName = g_application, result = null, perfiosTransactionId=null;
        module = module.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        if(!userService.isMiddlewareRequest(signature)){
            throw new UserNotAuthorizedException("99", "-99", "Not Authorized", "Not Authorized", "Not Authorized");
        }
        try {
            perfiosTransactionId = new JSONObject(requestData).getJSONObject("request_data").getString("perfiosTransactionId");
            perfiosTransactionId = null;
        }catch(JSONException e) {
            perfiosTransactionId = null;
        }

        switch (module + "/" + action + "/" + event)
        {
            case "lead/gstupload/startupload":
                Utility.print("middleware starting"+action);
                result = "0";//funcGstStatementUpload(g_application,module,action,event,requestData,userName);
                break;
            case "lead/itrupload/startupload":
                Utility.print("middleware starting"+action);
                result = "1";//funcItStatementUpload(g_application,module,action,event,requestData,userName);
                break;
            case "lead/statementupload/startupload":
                Utility.print("middleware starting"+action);
                result = "2";//funcBankStatementUpload(g_application,module,action,event,requestData,userName);
                break; //lead/statementupload/startupload
            case "lead/corpository/financial":
                Utility.print("middleware starting"+action);
                result = funcGetFinancialDetail(g_application,module,action,event,requestData,userName);
                break;
            case "lead/cam/generate":
                result = "success";//funcGenerateCAM(g_application,module,action,event,requestData,userName);
                break; //CAM Generate
            default:
                result = funcCallAPITwo(g_application, module, action, event, requestData, userName, "", "", "");
        }
        if(perfiosTransactionId!=null) {
            if(result.equalsIgnoreCase("success")) {
                userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, "P", null, null, null, null, "P", "Request is in process");
            }else {
                String flag=null,remarks=null;
                flag    = result.indexOf("Error(u)")>=0 ? "F" : "E";
                remarks = result;
                userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, flag, null, null, null, null, "F", remarks);
            }
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{moduleCategory}/{action}/{event}/{subevent}", produces = {"application/json", "application/json" })
    public String funcInternalFetcher(	@PathVariable(name = "module") String module,
                                          @PathVariable(name = "moduleCategory") String moduleCategory,
                                          @PathVariable(name = "action") String action,
                                          @PathVariable(name = "event") String event,
                                          @PathVariable(name = "subevent") String subEvent,
                                          @RequestHeader(name = "signature") String signature,
                                          @RequestBody String requestData)
    {
        String userName = g_application, result = null;
        module = module.toLowerCase();
        moduleCategory = moduleCategory.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        subEvent = subEvent.toLowerCase();
        if(!userService.isMiddlewareRequest(signature)){
            throw new UserNotAuthorizedException("99", "-99", "Not Authorized", "Not Authorized", "Not Authorized");
        }
        switch (module + "/" + moduleCategory + "/" + action + "/" + event + "/" + subEvent)
        {
            case "null":
                result = "null";
                break;
            default:
                result = funcCallAPIThree(g_application, module, moduleCategory, action, event, requestData, userName,subEvent, "", "");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{moduleCategory}/{action}/{event}/{subevent}/{subevent1}", produces = {"application/json", "application/json" })
    public String funcInternalFetcher(	@PathVariable(name = "module") String module,
                                          @PathVariable(name = "moduleCategory") String moduleCategory,
                                          @PathVariable(name = "action") String action,
                                          @PathVariable(name = "event") String event,
                                          @PathVariable(name = "subevent") String subEvent,
                                          @PathVariable(name = "subevent1") String subEvent1,
                                          @RequestHeader(name = "signature") String signature,
                                          @RequestBody String requestData)
    {
        String userName = g_application, result = null;
        module = module.toLowerCase();
        moduleCategory = moduleCategory.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        subEvent = subEvent.toLowerCase();
        subEvent1 = subEvent1.toLowerCase();
        if(!userService.isMiddlewareRequest(signature)){
            throw new UserNotAuthorizedException("99", "-99", "Not Authorized", "Not Authorized", "Not Authorized");
        }
        switch (module + "/" + moduleCategory + "/" + action + "/" + event + "/" + subEvent + "/" + subEvent1) {
            case "null":
                result = "null";
                break;
            default:
                result = funcCallAPIThree(g_application, module, moduleCategory, action, event, requestData, userName,
                        subEvent, subEvent1, "");
        }
        return result;
    }

    private String funcCallAPIOne(String application, String module, String action, String requestData, String userName,
                                  String param1, String param2, String param3) {
        Connection connection = null;
        CallableStatement cs = null;
        String result = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            cs = connection.prepareCall("{ call PROC_CALL_API_ONE(?,?,?,?,?,?,?,?,?) }");
            cs.setString(1, application);
            cs.setString(2, module);
            cs.setString(3, action);
            cs.setString(4, requestData);
            cs.setString(5, userName);
            cs.setString(6, param1);
            cs.setString(7, param2);
            cs.setString(8, param3);
            cs.registerOutParameter(9, 2005);
            cs.execute();
            final Clob clob_data = cs.getClob(9);
            result = clob_data.getSubString(1L, (int) clob_data.length());
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
        return result;
    }

    private String funcCallAPITwo(String application, String module, String action, String event, String requestData,
                                  String userName, String param1, String param2, String param3) {
        Connection connection = null;
        CallableStatement cs = null;
        String result = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            cs = connection.prepareCall("{ call PROC_CALL_API_TWO(?,?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, application);
            cs.setString(2, module);
            cs.setString(3, action);
            cs.setString(4, event);
            cs.setString(5, requestData);
            cs.setString(6, userName);
            cs.setString(7, param1);
            cs.setString(8, param2);
            cs.setString(9, param3);
            cs.registerOutParameter(10, 2005);
            cs.execute();
            final Clob clob_data = cs.getClob(10);
            result = clob_data.getSubString(1L, (int) clob_data.length());
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
        return result;
    }

    private String funcCallAPIThree(String application, String module, String module_category, String action,
                                    String event, String requestData, String userName, String param1, String param2, String param3) {
        Connection connection = null;
        CallableStatement cs = null;
        String result = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            cs = connection.prepareCall("{ call PROC_CALL_API_THREE(?,?,?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, application);
            cs.setString(2, module);
            cs.setString(3, module_category);
            cs.setString(4, action);
            cs.setString(5, event);
            cs.setString(6, requestData);
            cs.setString(7, userName);
            cs.setString(8, param1);
            cs.setString(9, param2);
            cs.setString(10, param3);
            cs.registerOutParameter(11, 2005);
            cs.execute();
            final Clob clob_data = cs.getClob(11);
            result = clob_data.getSubString(1L, (int) clob_data.length());
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
        return result;
    }

    /**********middleware calling functions**************/
    //##PERFIOS##//
    //1.1) gst document status check
    public String funcCheckGstUploadStatus(String perfiostransactionId) {
        JSONObject jsonObject;
        String message = null;
        String title = "Transaction Status Check";
        if (perfiostransactionId.isEmpty()) {
            return userError(title+":transactionId not found.");
        }

        URLConfigDto urlConfigDto = userService.findURLDtlByID(19);
        if(urlConfigDto.getUserid()==null){
            return userError(title+":"+"URL Configuration Not Found.");
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
    //1.2) gst document upload process
    public  String funcGstStatementUpload(String application, String module, String action, String event,String requestdata, String userName){
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
        String      channel, result, perfiosTransactionId,initiatedRequest=null,processFor=null;
        String      patterndate = "yyyyMMdd";
        String      patterntime = "HHmmssSSS";
        Blob blobfile;
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patterndate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patterntime);
        channel =null;  result = null; perfiosTransactionId = null;
        int 	failCount = 0;
        /*end declaration*/

        module = module;
        action = action+"/"+event;

        //read requestdata
        if(requestdata.isEmpty()){return userError("Request Body Empty.");}
        try {
            jsonObject  = new JSONObject(requestdata);
            channel     = jsonObject.getString("channel");
            if(jsonObject.getJSONObject("request_data").has("perfiosTransactionId")){
                perfiosTransactionId   = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
                return  userError("Action Not Found!");
            }
            if (channel==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("Channel Not Found!");
            }
            if(perfiosTransactionId==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"perfiosTransactionId Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("perfiosTransactionId Not Found!");
            }
            userService.saveJsonLog(channel,"req",action,requestdata,userName,null);
        }catch (JSONException e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }catch(Exception e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }
        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid()==null){
            userService.getJsonError("-99","Initial GST Upload Transaction Not Found","Initial Transaction Not found.","Initial Transaction Not Found.","99",channel,action,requestdata,userName,module,"U");
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
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestdata,userName,module,"U");
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
            return userError("Error while getting Initiated Request."+e.getMessage());
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
        if(processFor.equals("ALL")){
            List<CrmMiscMst> docTypeList = userService.findByCategory("GST_DOC_TYPE");

            if(docTypeList==null||docTypeList.size()<=0){
                userService.getJsonError("-99","Master Document Type Not Found.(GST)",g_error_msg,"GST Document Type not in Master Table.","99",channel,action,requestdata,userName,module,"U");
                return userError("GST Document Type not in Master Table.");
            }
            for(CrmMiscMst crmMiscMst:docTypeList){
                docId = Long.parseLong(crmMiscMst.getData_value());
                List<DocUploadDtl> docList = userService.getDocListByDocId(refId, serialNo, "L", docId);
                if (docList == null || docList.isEmpty()) {
                    continue;
//					userService.getJsonError("-99", "Not Found Any Document", g_error_msg, "Document Not Found.", "99", channel, action, requestdata, userName, module, "U");
//					return userError("Not found any verified documents for this transaction.");
                }
                Utility.print("Document upload process starting...");
                try {
                    String docType = null, resultOut = null;
                    String fileName = "", errorCode = "", errorMessage = "", req_status = "", doc_status = ""/*(U(upload)|F(failed)|R(reject)|P(process)|S(uploded and processed))*/;
                    Boolean successBool = false;
                    Blob blobdata = null;
                    JSONObject jsonDocStatus = new JSONObject();
                    for (DocUploadDtl docUploadDtl : docList) {
                        cnt += 1;
                        docUUID = docUploadDtl.getDoc_uuid();

                        DocUploadBlobDtl docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                        if (docUploadBlobDtl == null || docUploadBlobDtl.getUuid() == null) {
                            userService.getJsonError("-99", "Uploading Document Not found.", g_error_msg, "Document Not found for UUID:" + docUUID, "99", channel, action, requestdata, userName, module, "U");
                            return userError("Document Not found for UUID:" + docUUID);
                        }
                        if (docUploadBlobDtl.getDocContentType() == null) {
                            userService.getJsonError("-99", "Uploading Document Type not found.", g_error_msg, "Document Type Not found for UUID:" + docUUID, "99", channel, action, requestdata, userName, module, "U");
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
                                userService.getJsonError("-99", "Error-IOException", g_error_msg, e.getMessage(), "99", channel, action, requestdata, userName, module, "E");
                                return sysError(e.getMessage());
                            } catch (JSONException e) {
                                userService.getJsonError("-99", "JSON Error", g_error_msg, e.getMessage(), "99", channel, action, requestdata, userName, module, "E");
                                return sysError(e.getMessage());
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
                            userService.getJsonError("-99", "GST Data Upload Error", g_error_msg, "Fetching Data found null or failed to get inputstream", "99", channel, action, userName, userName, module, "U");
                            return userError("Fetching Data found null or failed to get inputstream for UUID:" + docUUID);
                        }
                    }
                } catch (SQLException e) {
                    userService.getJsonError("-99", "Error-SQLException", g_error_msg, e.getMessage(), "99", channel, action, userName, userName, module, "E");
                    return sysError(e.getMessage());
                } catch (FileNotFoundException e) {
                    userService.getJsonError("-99", "Error-FileNotFoundException", g_error_msg, e.getMessage(), "99", channel, action, userName, userName, module, "E");
                    return sysError(e.getMessage());
                }
            }
        }else{
            List<DocUploadDtl> docList = userService.getDocListByDocId(refId, serialNo, "L", docId);
            if (docList == null || docList.isEmpty()) {
                userService.getJsonError("-99", "Not Found Any Document", g_error_msg, "Document Not Found.", "99", channel, action, requestdata, userName, module, "U");
                return userError("Not found any verified documents for this transaction.");
            }
            Utility.print("Document upload process starting...");
            try {
                String docType = null, resultOut = null;
                String fileName = "", errorCode = "", errorMessage = "", req_status = "", doc_status = ""/*(U(upload)|F(failed)|R(reject)|P(process)|S(uploded and processed))*/;
                Boolean successBool = false;
                Blob blobdata = null;
                JSONObject jsonDocStatus = new JSONObject();
                for (DocUploadDtl docUploadDtl : docList) {
                    cnt += 1;
                    docUUID = docUploadDtl.getDoc_uuid();

                    DocUploadBlobDtl docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                    if (docUploadBlobDtl == null || docUploadBlobDtl.getUuid() == null) {
                        userService.getJsonError("-99", "Uploading Document Not found.", g_error_msg, "Document Not found for UUID:" + docUUID, "99", channel, action, requestdata, userName, module, "U");
                        return userError("Document Not found for UUID:" + docUUID);
                    }
                    if (docUploadBlobDtl.getDocContentType() == null) {
                        userService.getJsonError("-99", "Uploading Document Type not found.", g_error_msg, "Document Type Not found for UUID:" + docUUID, "99", channel, action, requestdata, userName, module, "U");
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
                            userService.getJsonError("-99", "Error-IOException", g_error_msg, e.getMessage(), "99", channel, action, requestdata, userName, module, "E");
                            return sysError(e.getMessage());
                        } catch (JSONException e) {
                            userService.getJsonError("-99", "JSON Error", g_error_msg, e.getMessage(), "99", channel, action, requestdata, userName, module, "E");
                            return sysError(e.getMessage());
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
                        userService.getJsonError("-99", "GST Data Upload Error", g_error_msg, "Fetching Data found null or failed to get inputstream", "99", channel, action, userName, userName, module, "U");
                        return userError("Fetching Data found null or failed to get inputstream for UUID:" + docUUID);
                    }
                }
            } catch (SQLException e) {
                userService.getJsonError("-99", "Error-SQLException", g_error_msg, e.getMessage(), "99", channel, action, userName, userName, module, "E");
                return sysError(e.getMessage());
            } catch (FileNotFoundException e) {
                userService.getJsonError("-99", "Error-FileNotFoundException", g_error_msg, e.getMessage(), "99", channel, action, userName, userName, module, "E");
                return sysError(e.getMessage());
            }
        }
        if (allFailed) {
            /*NOTE: Update transaction status F because all file failed to upload*/
            userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, "F", null, null, null, null, "F", "0 DOCUMENT(S) UPLOADED");
            return userError(remarks);
        }
        return funcGstStatementStartProcess(application, module, action, "processupload", requestdata, userName);
    }
    //1.3) gst uploaded document start process for report
    public String funcGstStatementStartProcess(String application,String module,String action,String event,String requestdata,String userName){
        //declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String     channel, result, perfiosTransactionId;
        channel =  result = perfiosTransactionId = null;
        /*end declaration*/

        module = module;
        action = action+"/"+event;
        //read requestdata
        if(requestdata.isEmpty()){return "Request Body Empty.";}
        try {
            jsonObject  = new JSONObject(requestdata);
            channel     = jsonObject.getString("channel");
            if(jsonObject.getJSONObject("request_data").has("perfiosTransactionId")){
                perfiosTransactionId   = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
                return userError("Action Not Found!");
            }
            if (channel==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("Channel Not Found!");
            }
            if(perfiosTransactionId==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"perfiosTransactionId Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("perfiosTransactionId Not Found!");
            }
            userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
        }catch (JSONException e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }catch(Exception e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }
        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid()==null){
            userService.getJsonError("-99","Initial GST Upload Transaction Not Found","Initial Transaction Not found.","Initial Transaction Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("Initial GST Upload Transaction Not Found");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            userService.getJsonError("-99","Request to Process failed transaction.","Transaction Status Found Failed, Unable to process.","Not Found Any Document to be process.","99",channel,action,requestdata,userName,module,"U");
            return userError("Transaction Status Found Failed, Unable to process.");
        }

        //get url dtl
        URLConfigDto urlConfigDto = userService.findURLDtlByID(24);
        if(urlConfigDto.getUrl() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("URL Not Found.");
        }
        if(urlConfigDto.getUserid() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("URL userID Not Found-24");
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
//				return jsonObject2.toString();
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
                userService.getJsonError("-99","Error in GST Statement Process.",message,code+": "+message,"99",channel,action,requestdata,userName,module,"U");
                return userError(code+": "+message);
            }
        }catch (IOException e){
            userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            e.printStackTrace();
            return sysError(e.getMessage());
        } catch (Exception e){
            userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            e.printStackTrace();
            return sysError(e.getMessage());
        }
    }

    //2.1 itr document status check
    public String funcCheckItUploadStatus(String perfiosTransactionId) {
        //declaration//
        String message = null, title = "Transaction Status Check";
        JSONObject jsonObject;
        //validations
        if (perfiosTransactionId.isEmpty()) {
            return userError(title+":"+"transactionId not found.");
        }

        URLConfigDto urlConfigDto = userService.findURLDtlByID(28);
        if(urlConfigDto.getUserid()==null){
            return userError(title+":"+"URL Configuration Not Found.");
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
    //2.2) itr document upload process
    public String funcItStatementUpload(String application,String module,String action,String event,String requestdata,String userName){
        //declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        String      channel=null, result=null, perfiosTransactionId=null,entityType=null;
        long        refId=0, serialNo=0;
        int 		failCount = 0;
        /*end declaration*/
        module = module;
        action = action+"/"+event;
        //read requestdata
        if(requestdata.isEmpty()){return userError("Request Body Empty.");}
        try {
            jsonObject  = new JSONObject(requestdata);
            channel     = jsonObject.getString("channel");
            if(jsonObject.getJSONObject("request_data").has("perfiosTransactionId")){
                perfiosTransactionId   = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
                return userError("Action Not Found!");
            }
            if (channel==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("Channel Not Found!");
            }
            if(perfiosTransactionId==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"perfiosTransactionId Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("perfiosTransactionId Not Found!");
            }
            userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
        }catch (JSONException e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }catch(Exception e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }


        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid() == null){
            userService.getJsonError("-99","Initial ITR Upload Transaction Not Found","Initial ITR Upload Transaction Not found.","Initial ITR Upload Transaction Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("Initial ITR Upload Transaction Not Found.");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            userService.getJsonError("-99","Request to Process failed transaction.","Transaction Status Found Failed, Unable to process.","Transaction Status is in failed status.","99",channel,action,requestdata,userName,module,"U");
            return userError("Transaction Status Found Failed, Unable to process.");

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
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestdata,userName,module,"U");
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
                            userService.getJsonError("-99","Uploading Document Data Not found.",g_error_msg,"Document Data Not found for UUID:"+docUUID,"99",channel,action,requestdata,userName,module,"U");
                            return userError("Document's details are not found for UUID:"+docUUID);
                        }
                        if(docUploadBlobDtl.getDocContentType()==null){
                            userService.getJsonError("-99","Uploading Document Content Type not found.",g_error_msg,"Document Content Type Not found for UUID:"+docUUID,"99",channel,action,requestdata,userName,module,"U");
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
                                userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
                                return sysError(e.getMessage());
                            }catch(JSONException e){
                                userService.getJsonError("-99","JSON Error",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
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
                            userService.getJsonError("-99","Data Upload Error",g_error_msg,"Fetching Data found null or failed to get inputStream","99",channel,action,userName,userName,module,"U");
                            return userError("Fetching document data found null or failed to get inputStream");
                        }
                    }
                }catch(SQLException e){
                    userService.getJsonError("-99","Error-SQLException",g_error_msg,e.getMessage(),"99",channel,action,userName,userName,module,"E");
                    return sysError(e.getMessage());
                }catch (FileNotFoundException e){
                    userService.getJsonError("-99","Error-FileNotFoundException",g_error_msg,e.getMessage(),"99",channel,action,userName,userName,module,"E");
                    return sysError(e.getMessage());
                }
            }//end loop (CrmDocumentMst)
            if(allFailed){
                /*NOTE: Update transaction status F because all file failed to upload*/
                userService.updatePerfiosWebhookStatus(perfiosTransactionId,null,"F",null,null,null,null,"F","0 DOCUMENT(S) UPLOADED");
                return userError(remarks);
            }
            return  funcItStatementStartProcess(application,module,action,"processupload",requestdata,userName);
        }else{
            userService.getJsonError("-99","Master Document Type Not Found.(ITR)",g_error_msg,"ITR Document Type not in Master Table.","99",channel,action,requestdata,userName,module,"U");
            return userError("ITR Document Type not in Master Table.");
        }

    }

    //2.3) itr uploaded document start process for report
    public String funcItStatementStartProcess(String application,String module,String action,String event,String requestdata,String userName){
        //declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String      channel, result, perfiosTransactionId,title;
        channel =null;  result =null; perfiosTransactionId = null; title = "Start Process:";
        /*end declaration*/
        module = module;
        action = action+"/"+event;
        //read requestdata
        if(requestdata.isEmpty()){return userError("Request Body Empty.");}
        try {
            jsonObject  = new JSONObject(requestdata);
            channel     = jsonObject.getString("channel");
            if(jsonObject.getJSONObject("request_data").has("perfiosTransactionId")){
                perfiosTransactionId   = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
                return userError(title+"Action Not Found");
            }
            if (channel==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError(title+"Channel Not Found");
            }
            if(perfiosTransactionId==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"perfiosTransactionId Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError(title+"perfiosTransactionId Not Found");
            }
            userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
        }catch (JSONException e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(title+e.getMessage());
        }catch(Exception e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(title+e.getMessage());
        }
        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid()==null){
            userService.getJsonError("-99","Initial ITR Upload Transaction Not Found","Initiate Transaction Not found.","Initiate Transaction Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError(title+"Initial ITR Upload Transaction Not Found");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            userService.getJsonError("-99","Request to Process failed transaction.","Transaction Status Found Failed, Unable to process.","Not Found Any Document to be process.","99",channel,action,requestdata,userName,module,"U");
            return userError(title+"Transaction Status Found Failed, Unable to process.");
        }

        //get url dtl
        int urlConfigID = 29;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(urlConfigID);
        if(urlConfigDto.getUrl() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError(title+"URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestdata,userName,module,"U");
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
                userService.getJsonError("-99","Error in GST Statement Process.",message,code+": "+message,"99",channel,action,requestdata,userName,module,"U");
                return userError(title+message);
            }
        }catch (IOException e){
            userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(title+e.getMessage());
        }catch (Exception e){
            userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(title+e.getMessage());
        }
    }

    //3.1) bank document check status
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
    //3.2)bank document upload process
    public String funcBankStatementUpload(String application,String module,String action,String event,String requestdata,String userName){
        //declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String      channel, result, perfiosTransactionId,initiatedRequest=null,entityType=null;
        channel = null; result =null; perfiosTransactionId = null;
        long refId=0,serialNo=0, bankLineID=0;
        /*end declaration*/
        module = module;
        action = action+"/"+event;
        //read requestdata
        if(requestdata.isEmpty()){return userError("Request Body Empty.");}
        try {
            jsonObject  = new JSONObject(requestdata);
            channel     = jsonObject.getString("channel");
            if(jsonObject.getJSONObject("request_data").has("perfiosTransactionId")){
                perfiosTransactionId   = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
                return userError("Action Not Found!");
            }
            if (channel==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("Channel Not Found!");
            }
            if(perfiosTransactionId==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"perfiosTransactionId Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("perfiosTransactionId Not Found!");
            }
            userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
        }catch (JSONException e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }catch(Exception e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }
        //get transaction detail
        int urlConfigID = 32;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(urlConfigID);
        Utility.print("vendorID");
        Utility.print(urlConfigDto.getUserid());
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getUserid()==null){
            userService.getJsonError("-99","Initial Statement Transaction Not Found","Initial Statement Transaction Not found.","Initial Statement Transaction Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("Initial Bank Statement Upload Transaction Not Found");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            userService.getJsonError("-99","Request to Process failed transaction.","Transaction Status Found Failed, Unable to process.","Transaction Status is in failed status.","99",channel,action,requestdata,userName,module,"U");
            return userError("Transaction Status Found Failed, Unable to process.");
        }

        //check valid status
        String statusCheck  = funcStatementUploadStatus(perfiosTransactionId,perfiosReqResDto.getUserid());
        if(!statusCheck.equals("success")){
            return statusCheck;
        }
        //get url dtl
        if(urlConfigDto.getUrl() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestdata,userName,module,"U");
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
//                            userService.getJsonError("-99","Uploading Document Data Not found.",g_error_msg,"Document Data Not found for UUID:"+docUUID,"99",channel,action,requestdata,userName,module,"U");
//                            return userError("Document's details are not found for UUID:"+docUUID);
                            continue;
                        }
                        if(docUploadBlobDtl.getDocContentType()==null){
                            userService.getJsonError("-99","Uploading Document Content Type not found.",g_error_msg,"Document Content Type Not found for UUID:"+docUUID,"99",channel,action,requestdata,userName,module,"U");
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
                                    userService.getJsonError("-99","Error while Executing callingDBObject",g_error_msg,error_msg,"99",channel,action,requestdata,userName,module,"E");
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
                                userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,null,"E");
                                return sysError(e.getMessage());
                            }catch(JSONException e){
                                userService.getJsonError("-99","JSON Error",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
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
                            userService.getJsonError("-99","Data Upload Error",g_error_msg,"Fetching Data found null or failed to get inputStream","99",channel,action,userName,userName,module,"U");
                            return userError("Fetching Data found null or failed to get inputStream for UUID:"+docUUID);
                        }
                    }
                }catch(SQLException e){
                    userService.getJsonError("-99","Error-SQLException",g_error_msg,e.getMessage(),"99",channel,action,userName,userName,module,"E");
                    return sysError(e.getMessage());
                }catch (FileNotFoundException e){
                    userService.getJsonError("-99","Error-FileNotFoundException",g_error_msg,e.getMessage(),"99",channel,action,userName,userName,module,"E");
                    return sysError(e.getMessage());
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
            return  funcBankStatementStartProcess(application,module,action,"processupload",requestdata,userName);
        }else{
            userService.getJsonError("-99","Master Document Type Not Found.(Statement)",g_error_msg,"Statement Document Type not in Master Table.","99",channel,action,requestdata,userName,module,"U");
            return userError("Statement Document Types are not found in Master Table.");
        }
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
                    int rowUpdate = userController.updateGstDocumentStatus(perfiosTransactionId,"STMT_CANCEL_TRANSACTION",jsonObject.toString());
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
    //3.5) bank uploaded document start process for report
    public String funcBankStatementStartProcess(String application,String module,String action,String event,String requestdata,String userName){
        //declaration
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String     channel, result, perfiosTransactionId;
        channel = null; result = null; perfiosTransactionId = null;
        /*end declaration*/
        module = module;
        action = action+"/"+event;

        //read requestdata
        if(requestdata.isEmpty()){return userError("Request Body Empty.");}
        try {
            jsonObject  = new JSONObject(requestdata);
            channel     = jsonObject.getString("channel");
            if(jsonObject.getJSONObject("request_data").has("perfiosTransactionId")){
                perfiosTransactionId   = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
                return userError("Action not found!");
            }
            if (channel==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("Channel Not Found!");
            }
            if(perfiosTransactionId==null){
                userService.getJsonError("-99","Request Error!",g_error_msg,"perfiosTransactionId Not Found!","99",channel,action,requestdata,userName,module,"U");
                return userError("perfiosTransactionId Not Found!");
            }
            userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
        }catch (JSONException e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }catch(Exception e){
            userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }
        //get transaction detail
        PerfiosReqResDto perfiosReqResDto =  userService.findByPerfiosTransactionID(perfiosTransactionId);
        if(perfiosReqResDto.getTransaction_id() == null){
            userService.getJsonError("-99","Initial Bank Statement Upload Transaction Not Found","Initial Transaction Not found.","Initial Transaction Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("Initial Bank Statement Upload Transaction Not Found");
        }
        if(perfiosReqResDto.getUserid() == null){
            userService.getJsonError("-99","Initial Bank Statement Upload Transaction Not Found","Initial Transaction Not found.","Initial Transaction Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("Initial Bank Statement Upload Transaction Not Found");
        }else if(perfiosReqResDto.getStatus().equals("F")){
            userService.getJsonError("-99","Request to Process failed transaction.","Transaction Status Found Failed, Unable to process.","Transaction Status is Failed Due to: "+perfiosReqResDto.getRemarks(),"99",channel,action,requestdata,userName,null,"U");
            return userError("Transaction Status Found Failed, Unable to process.");
        }

        //get url dtl
        int urlConfigID = 33;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(urlConfigID);
        if(urlConfigDto.getUrl() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestdata,userName,module,"U");
            return userError("URL Not Found.("+urlConfigID+")");
        }
        if(urlConfigDto.getUserid() == null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestdata,userName,module,"U");
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
                userService.getJsonError("-99","Error while Executing callingDBObject",g_error_msg,error_msg,"99",channel,action,requestdata,userName,module,"E");
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
                userService.getJsonError("-99","Error in Bank Statement Process.",xmlResponseMessage,xmlErrorCode+": "+xmlResponseMessage,"99",channel,action,requestdata,userName,module,"U");
                return userError(xmlErrorCode+": "+xmlResponseMessage);
            }
        }catch (IOException e){
            userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }catch (Exception e){
            userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            return sysError(e.getMessage());
        }
    }

    //##CORPOSITORY##//
    //1) get financial detail
    public String funcGetFinancialDetail(String application, String module, String action, String event, String requestData, String userName){
        JSONObject jsonObject =null,jsonObject1=null,jsonTokenData=null;
        JSONObject jsonURLResponse = new JSONObject();
        final String requestType = "financial_detail";
        String filter=null, sendURL,result,channel="W",tokenResult=null,
                authTokenID=null,v_tag=null,enitityName=null,data=null,
                resStatus=null;
        long authUserID = 0;
        long refID=0,companyID=0,transactionID=0;
        String companyName = null,webhookURL=null;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(39);

        LosCorpositoryAPI losCorpositoryAPI = new LosCorpositoryAPI();

        action = action+"/"+event;
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
            jsonObject  = new JSONObject(requestData);
            jsonObject1 = new JSONObject();
            if(jsonObject.has("channel")){
                v_tag = "channel";
                channel = jsonObject.getString(v_tag);
            }else{
                channel = "W";
            }
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
                return userService.getJsonError("-99","Data Error","refID not found","refID not found","99",channel,action,requestData,userName,module,"U");
            }
            if(companyID<=0){
                return userService.getJsonError("-99","Data Error","companyID not found","companyID not found","99",channel,action,requestData,userName,module,"U");
            }
            if(transactionID<=0){
                return userService.getJsonError("-99","Data Error","transactionID not found","transactionID not found","99",channel,action,requestData,userName,module,"U");
            }
        }catch (JSONException e){
            return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage()+" tag:"+v_tag,"99",channel,action,requestData,userName,module,"E");
        }catch(Exception e){
            return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }

        //other data requirements
        if(urlConfigDto==null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Details Not Found.","99",channel,action,requestData,userName,module,"U");
        }

        sendURL = urlConfigDto.getUrl();
        Utility.print("send url:"+sendURL);
        if((sendURL==null || sendURL.isEmpty())){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestData,userName,module,"U");
        }
        //GET ACTIVE TOKEN
        tokenResult = userController.getCorpositoryTokenDetail_v2(userName);
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
            e.printStackTrace();
            return userService.getJsonError("-99","Error while fetching Corpository token details",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
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
                if(jsonURLResponse.getLong("status")==1){
                    resStatus = "S";
                    jsonObject1.put("status","0");
                    jsonObject1.put("response_data",jsonURLResponse);
                }else{
                    resStatus = "F";
                    jsonObject1 =  new JSONObject(
                            userService.getJsonError("-99","Error while fetching financial detail",g_error_msg,result,"99",channel,action,requestData,userName,module,"U")
                    );
                }
            }else {
                resStatus = "F";
                jsonObject1 =  new JSONObject(
                        userService.getJsonError("-99","Error while fetching financial detail",g_error_msg,result,"99",channel,action,requestData,userName,module,"U")
                );
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
//            return jsonObject1.toString();
            return resStatus.equalsIgnoreCase("S") ? "success":jsonObject1.toString();
        }catch(JSONException e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
        catch (Exception e) {
            e.printStackTrace();
            return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
    }
	/*
	public String funcGenerateCAM(String application,String module,String action,String event,String requestdata,String userName) {
		if(requestdata.isEmpty()){return userError("Request Body Empty.");}
		JSONObject jsonObject = new JSONObject();
		String channel=null;
		Long leadId = null, serialNo = null;
		try {
			jsonObject  = new JSONObject(requestdata);
			System.out.println("1");
			channel     = jsonObject.getString("channel");
			System.out.println("2");
			leadId   = jsonObject.getJSONObject("request_data").getLong("refID");
			System.out.println("3");
			serialNo = jsonObject.getJSONObject("request_data").getLong("serialNo");
			System.out.println("4");
			if (channel==null){
				System.out.print("5");
				return userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
			}
			if(leadId == null){
				System.out.println("6");
				return userService.getJsonError("-99","Request Error!",g_error_msg,"Lead ID Not Found","99",channel,action,requestdata,userName,module,"U");
			}
			if(serialNo == null){
				System.out.println("7");
				return userService.getJsonError("-99","Request Error!",g_error_msg,"Serial No Not Found","99",channel,action,requestdata,userName,module,"U");
			}
		}catch (JSONException e){
			System.out.println("8"+e.getMessage());
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
		}catch(Exception e){
			System.out.println("9"+e.getMessage());
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
		}
		userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
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
			System.out.println("Fiel Path:"+filepath.getAbsolutePath());
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

}
