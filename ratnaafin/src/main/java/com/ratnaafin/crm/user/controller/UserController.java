package com.ratnaafin.crm.user.controller;

import com.ratnaafin.crm.common.exception.TokenNotValidException;
import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.dto.CRMAppDto;
import com.ratnaafin.crm.user.dto.CRMCAMDtlDto;
import com.ratnaafin.crm.user.dto.PerfiosReqResDto;
import com.ratnaafin.crm.user.dto.URLConfigDto;
import com.ratnaafin.crm.user.dto.UniqueIDDtlDto;
import com.ratnaafin.crm.user.model.*;
import com.ratnaafin.crm.user.service.UserService;
import com.ratnaafin.crm.user.service.impl.UserServiceImpl;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/los")
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    private final String WEBHOOK_DOMAIN = "https://digix.aiplsolution.in/ratnaafin/los/webhooks";
    private String g_error_msg = "Something went wrong please try again.";
    private String g_status = null;
    private String g_err_cd = null;
    private String g_err_title = null;
    private String g_err_msg = g_error_msg;
    private String g_err_dtl = null;
    public static String gs_user = null;
    private String g_application = "los";
    private String className = this.getClass().getSimpleName();

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{moduleCategory}/{action}/{event}/{subevent}/{subevent1}", produces = {
            "application/json", "application/json" })
    public String funcInternalFetcher(@PathVariable(name = "module") String module,
                                      @PathVariable(name = "moduleCategory") String moduleCategory, @PathVariable(name = "action") String action,
                                      @PathVariable(name = "event") String event, @PathVariable(name = "subevent") String subEvent,
                                      @PathVariable(name = "subevent1") String subEvent1, @RequestBody String requestData,
                                      OAuth2Authentication authentication) {
        String userName = "null", result;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        module = module.toLowerCase();
        moduleCategory = moduleCategory.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        subEvent = subEvent.toLowerCase();
        subEvent1 = subEvent1.toLowerCase();
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

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{moduleCategory}/{action}/{event}/{subevent}", produces = {
            "application/json", "application/json" })
    public String funcInternalFetcher(@PathVariable(name = "module") String module,
                                      @PathVariable(name = "moduleCategory") String moduleCategory, @PathVariable(name = "action") String action,
                                      @PathVariable(name = "event") String event, @PathVariable(name = "subevent") String subEvent,
                                      @RequestBody String requestData, OAuth2Authentication authentication) {
        String userName = "null", result = null;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        module = module.toLowerCase();
        moduleCategory = moduleCategory.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        subEvent = subEvent.toLowerCase();
        switch (module + "/" + moduleCategory + "/" + action + "/" + event + "/" + subEvent) {
            case "null":
                result = "null";
                break;
            default:
                result = funcCallAPIThree(g_application, module, moduleCategory, action, event, requestData, userName,
                        subEvent, "", "");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{action}/{event}", produces = { "application/json",
            "application/json" })
    public String funcInternalFetcher(@PathVariable(name = "module") String module,
                                      @PathVariable(name = "action") String action, @PathVariable(name = "event") String event,
                                      @RequestBody String requestData, OAuth2Authentication authentication) {
        String userName = "null", result = null;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        module = module.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        switch (module + "/" + action + "/" + event) {
            case "inquiry/document/verify":
                result = funcVerifyDocument(g_application, module, action, event, requestData, userName, "", "", "");
                break;
            case "inquiry/document/reject":
                result = funcRejectDocument(g_application, module, action, event, requestData, userName, "", "", "");
                break;
            case "inquiry/document/delete":
                result = funcDeleteDocument(g_application, module, action, event, requestData, userName, "", "", "");
                break;
            case "lead/gstinfo/gstinfowebhook":
                // By Sanjay dt:15/02/2021 [GST Info]
                result = "Not Found"; // moved to admin controller
                break;
            case "lead/gstupload/startupload":
                // By Sanjay dt:15/02/2021 [GST Upload]
                result = funcGstStatementUpload(g_application, module, action, event, requestData, userName);
                break;
            case "lead/gstupload/processupload":
                // By Sanjay dt:15/02/2021 [GST Upload]
                result = funcGstStatementProcess(g_application, module, action, event, requestData, userName);
                break;
            case "lead/itrupload/startupload":
                // By Sanjay dt:15/02/2021 [IT Upload]
                result = funcItStatementUpload(g_application, module, action, event, requestData, userName);
                break;
            case "lead/itrupload/processupload":
                // By Sanjay dt:15/02/2021 [IT Upload]
                result = funcItStatementProcess(g_application, module, action, event, requestData, userName);
                break;
            case "lead/statementupload/startupload":
                // By Sanjay dt:15/02/2021 [STMT Upload]
                result = funcStartStatementUpload(g_application, module, action, event, requestData, userName);
                break;
            case "lead/statementupload/processupload":
                // By Sanjay dt:15/02/2021 [STMT Upload]
                result = funcStatementProcess(g_application, module, action, event, requestData, userName);
                break;
            case "lead/statementupload/getreport":
                // By Sanjay dt:15/02/2021 [STMT Upload]
                result = funcGetStatementReport(g_application, module, action, event, requestData, userName);
                break;
            case "lead/statementupload/statementwebhook":
                // By Sanjay dt:15/02/2021 [STMT Upload]
                result = "Not Found"; // moved to admin controller
                break;
            default:
                result = funcCallAPITwo(g_application, module, action, event, requestData, userName, "", "", "");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{action}", produces = { "application/json",
            "application/json" })
    public String funcInternalFetcher(@PathVariable(name = "module") String module,
                                      @PathVariable(name = "action") String action, @RequestBody String requestData,
                                      OAuth2Authentication authentication) {
        String userName = "null", result = null;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        module = module.toLowerCase();
        action = action.toLowerCase();
        switch (module + "/" + action) {
            case "null":
                result = "";
                break;
            default:
                result = funcCallAPIOne(g_application, module, action, requestData, userName, "", "", "");
        }
        return result;
    }

    /************************************
     * DOWNLOAD DOCUMENT - USING GET
     *****************************************/
    @RequestMapping(method = RequestMethod.GET, value="/lead/external/{third_party}/data/download",produces = { "application/json" })
    public  void thirdPartyDocDownloadProcess(@PathVariable(name = "third_party") String third_party,
                                              @RequestParam List<String> docUUID,
                                              @RequestParam String tokenID,
                                              HttpServletResponse response) throws IOException {

        String userName = "null",result = null;
        User user = (User) userService.readAuth(tokenID).getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        String module = "lead";
        String moduleCategory = "document";
        String action = third_party;
        String event = "data";
        String subEvent = "download";
        String requestData = null;
        requestData =  "{\"request_data\": {\"docUUID\": "+new JSONArray(docUUID)+"}}\n";
        if(action.equalsIgnoreCase("perfios")){
            response = perfiosdocumentDownload(module,moduleCategory,userName,action,event,response,requestData,subEvent,null);
        }else{
            response = null;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value="/lead/document/aadhar/{fileType}/download",produces = { "application/json" })
    public  void aadharDataProcess(@PathVariable(name = "fileType") String fileType,
                                   @RequestParam List<String> transactionID,
                                   @RequestParam String tokenID,
                                   @RequestParam String download,
                                   HttpServletResponse response) throws IOException {
        String userName = "null",result = null;
        User user = (User) userService.readAuth(tokenID).getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        String module = "lead";
        String moduleCategory = "document";
        String action = "aadhar";
        String event = fileType;
        String subEvent = "download";
        String requestData = null;
        requestData =  "{\"request_data\": {\"transactionID\": "+new JSONArray(transactionID)+"}}\n";
        response = aadharDataDownload(module,moduleCategory,userName,action,event,response,fileType,download,requestData,subEvent,null);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lead/management/document/{document_type}/data/download")
    public void managementDocDownloadProcess(@PathVariable(name = "document_type") String document_type,
                                             @RequestParam List<String> docUUID, @RequestParam String tokenID, HttpServletResponse response)
            throws IOException {
        String userName = "null", result = null;
        User user = (User) userService.readAuth(tokenID).getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        String module = "lead";
        String moduleCategory = "management";
        String action = "document";
        String event = document_type;
        String subEvent = "data";
        String subEvent1 = "download";
        String requestData = "{\"request_data\": {\"docUUID\": " + new org.json.JSONArray(docUUID) + "}}\n";
        response = documentDownload(module, moduleCategory, userName, action, event, response, requestData, subEvent,
                subEvent1);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lead/document/{document_type}/data/download")
    public void legalDocDownloadProcess(@PathVariable(name = "document_type") String document_type,
                                        @RequestParam List<String> docUUID, @RequestParam String tokenID, HttpServletResponse response)
            throws IOException {

        String userName = "null", result = null;
        User user = (User) userService.readAuth(tokenID).getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        String module = "lead";
        String moduleCategory = "document";
        String action = document_type;
        String event = "data";
        String subEvent = "download";

        String requestData = "{\"request_data\": {\"docUUID\": " + new org.json.JSONArray(docUUID) + "}}\n";
        response = documentDownload(module, moduleCategory, userName, action, event, response, requestData, subEvent,
                null);
    }

    /********************
     * postRequest - FOR ZIP FILE DOWNLOAD
     ****************************/
    // 1) Management zip download process
    @RequestMapping(method = RequestMethod.POST, value = "/lead/management/document/{document_type}/data/preview")
    public void managementDocPreview(@PathVariable(name = "document_type") String document_type,
                                     @RequestBody String requestData, HttpServletResponse response, OAuth2Authentication authentication)
            throws IOException {
        String userName = "null", result = null;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        String module = "lead";
        String moduleCategory = "management";
        String action = "document";
        String event = document_type;
        String subEvent = "data";
        String subEvent1 = "preview";
        response = documentDownload(module, moduleCategory, userName, action, event, response, requestData, subEvent,
                subEvent1);
    }

    // 2) Legal zip Download process
    @RequestMapping(method = RequestMethod.POST, value = "/lead/document/{document_type}/data/preview")
    public void legalDocPreview(@PathVariable(name = "document_type") String document_type,
                                @RequestBody String requestData, HttpServletResponse response, OAuth2Authentication authentication)
            throws IOException {
        String userName = "null", result = null;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        String module = "lead";
        String moduleCategory = "document";
        String action = document_type;
        String event = "data";
        String subEvent = "preview";
        response = documentDownload(module, moduleCategory, userName, action, event, response, requestData, subEvent,
                null);
    }

    /***********************
     * FOR MULTIPART FORM DATA
     *****************************************/
    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{moduleCategory}/{action}/{event}/{subEvent}/{subEvent1}", produces = {
            "application/json",
            "application/json" }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public String funcInternalFetcher(@PathVariable(name = "module") String module,
                                      @PathVariable(name = "moduleCategory") String moduleCategory, @PathVariable(name = "action") String action,
                                      @PathVariable(name = "event") String event, @PathVariable(name = "subEvent") String subEvent,
                                      @PathVariable(name = "subEvent1") String subEvent1, @RequestParam(value = "refID") String refID,
                                      @RequestParam(value = "serialNo") String serialNo, @RequestParam("blob") List<MultipartFile> files,
                                      @RequestParam(value = "metaData") String metaData, @RequestParam(value = "categoryCD",required = false) String categoryCD,
                                      OAuth2Authentication authentication) {
        String requestData = "MULTIPART_FORM_DATA", response = null, channel = "W",uploadCategory=null;;
        String userName = "null", result = null,entityType = "I";
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        module = module.toLowerCase();
        moduleCategory = moduleCategory.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        subEvent = subEvent.toLowerCase();
        subEvent1 = subEvent1.toLowerCase();

        switch (module + "/" + moduleCategory + "/" + action + "/" + event + "/" + subEvent + "/" + subEvent1) {
            case "lead/management/document/bank/data/post":
            case "lead/management/document/itr/data/post":
            case "lead/management/document/gst/data/post":
            case "lead/management/document/kyc/data/post":
            case "lead/management/document/other/data/post":
                uploadCategory = event;
                if (serialNo == null || serialNo.isEmpty()) {
                    result = userService.getJsonError("-99", "Parameter serialNo not value is missing", g_error_msg,
                            "serialNo found null.", "99", channel, action, requestData, userName, module, "U");
                } else {
                    result = funcDocUploadProcess(module, moduleCategory, action, event, subEvent, subEvent1, userName,
                            Long.parseLong(refID), Long.parseLong(serialNo), files, metaData, uploadCategory,entityType,categoryCD);
                    // result =
                    // funcBankDocumentUpload(module,moduleCategory,action,event,subEvent,userName,refID,srID,files,metaData);
                }
                break;
            default:
                result = "no_case";// funcCallAPIThree(g_application,module,moduleCategory,action,event,requestData,userName,subEvent,"","");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{moduleCategory}/{action}/{event}/{subEvent}", produces = {
            "application/json",
            "application/json" }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public String funcInternalFetcher(@PathVariable(name = "module") String module,
                                      @PathVariable(name = "moduleCategory") String moduleCategory, @PathVariable(name = "action") String action,
                                      @PathVariable(name = "event") String event, @PathVariable(name = "subEvent") String subEvent,
                                      @RequestParam(value = "refID") String refID,
                                      @RequestParam(required = false, value = "serialNo") String serialNo,
                                      // @RequestParam Optional<String> srID,
                                      @RequestParam("blob") List<MultipartFile> files, @RequestParam(value = "metaData") String metaData,
                                      @RequestParam(name="categoryCD", required = false) String categoryCD,
                                      OAuth2Authentication authentication) {
        String userName = "null", result = null,entityType = "L";
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        module = module.toLowerCase();
        moduleCategory = moduleCategory.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        subEvent = subEvent.toLowerCase();
        // result =
        // funcBankDocumentUpload(module,moduleCategory,action,event,null,userName,refID,srID,files,metaData);
        switch (module + "/" + moduleCategory + "/" + action + "/" + event + "/" + subEvent) {
            case "lead/document/bank/data/post":
            case "lead/document/itr/data/post":
            case "lead/document/gst/data/post":
            case "lead/document/kyc/data/post":
            case "lead/document/other/data/post":
                Utility.print("sanjay:1");
                if (serialNo == null || serialNo.isEmpty()) {
                    serialNo = "1";
                }
                result = funcDocUploadProcess(module, moduleCategory, action, event, subEvent, null, userName, Long.parseLong(refID),
                        Long.parseLong(serialNo), files, metaData, action,entityType,categoryCD);
                break;
            default:
                result = "no_case";
        }
        return result;
    }
    //common file upload process
    @RequestMapping(method = RequestMethod.POST, value = "/{module}/{moduleCategory}/{action}/{event}", produces = {
            "application/json",
            "application/json" }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public String funcInternalFetcher(@PathVariable(name = "module") String module,
                                      @PathVariable(name = "moduleCategory") String moduleCategory, @PathVariable(name = "action") String action,
                                      @PathVariable(name = "event") String event,
                                      @RequestParam(value = "id") String id,
                                      @RequestParam("file") List<MultipartFile> files,
                                      @RequestParam(value = "metaData",required = false) String metaData,
                                      OAuth2Authentication authentication) {
        String userName = "null", result = null,entityType = "L";
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        module = module.toLowerCase();
        moduleCategory = moduleCategory.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();
        metaData = metaData==null||metaData.isEmpty()?"{}":metaData;
        // funcBankDocumentUpload(module,moduleCategory,action,event,null,userName,refID,srID,files,metaData);
        switch (module + "/" + moduleCategory + "/" + action + "/" + event) {
            case "lead/document/sanction/upload":
            case "lead/document/termsheet/upload":
                result = funcCommonFileUpload(module, moduleCategory, action, event, userName, Long.parseLong(id),
                        files, metaData);
                break;
            default:
                result = "no_case";
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/{module}/{moduleCategory}/{action}/{event}",produces = {"application/json","application/json"})
    public String funcInternalFetcher(	@PathVariable(name = "module") String module,
                                          @PathVariable(name = "moduleCategory") String moduleCategory,
                                          @PathVariable(name = "action") String action,
                                          @PathVariable(name = "event") String event,
                                          @RequestBody String requestData,
                                          OAuth2Authentication authentication){
        String userName = null,result = null;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        userName = user.getUsername();
        module = module.toLowerCase();
        moduleCategory = moduleCategory.toLowerCase();
        action = action.toLowerCase();
        event = event.toLowerCase();

        switch (module+"/"+moduleCategory+"/"+action+"/"+event)
        {
            case "lead/external/gstinfo/initiate":
                result = funcInitiateGstInfo(g_application,module,moduleCategory,action,event,requestData,userName);
                break;
            case "lead/external/gstInfo/status":
                result = funcGstInfoStatus(g_application,module,moduleCategory,action,event,requestData,userName);
                break;
            case "lead/external/gstupload/initiate":
                result = funcInitiateGstUpload(g_application,module,moduleCategory,action,event,requestData,userName);
                break;
            case "lead/external/gstupload/status":
                result = funcGstStatementUploadStatus(g_application,module,moduleCategory,action,event,requestData,userName);
                break;
            case "lead/external/itrupload/initiate":
                result = funcInitiateItUpload(g_application,module,moduleCategory,action,event,requestData,userName);
                break;
            case "lead/external/itrupload/status":
                result = funcItStatementUploadStatus(g_application, module,moduleCategory, action,event, requestData, userName);
                break;
            case "lead/external/bankupload/initiate":
                result = funcInitiateStatementUpload(g_application, module,moduleCategory,action,event, requestData, userName);
                break;
            case "lead/external/bankupload/status":
                result = funcStatementUploadStatus(g_application, module,moduleCategory, action,event, requestData, userName);
                break;
            case "lead/external/bankupload/canceltransaction":
                result = funcCancelTransaction(g_application, module, moduleCategory,action,event, requestData, userName);
                break;
            case "lead/external/corpository/login":
                result = funcInitiateCorpositoryLogin(g_application, module, moduleCategory,action,event, requestData, userName);
                break;
            case "lead/external/corpository/companysearch":
                result = funcInitiateCompanySearch(g_application, module, moduleCategory,action,event, requestData, userName);
                break;
            case "lead/external/corpository/initiate"://"lead/external/corpository/creditorder":
                result = funcInitiateCreditOrder(g_application, module, moduleCategory,action,event, requestData, userName);
                break;
            case "lead/external/corpository/financial":
                result = funcGetFinancialDetail(g_application, module, moduleCategory,action,event, requestData, userName);
                break;
            case "lead/external/aadhar/initiate":
                result = funcInitiateAadhar(module, moduleCategory,action,event, requestData, userName);
                break;
            default:
                Utility.print(module+"/"+moduleCategory+"/"+action+"/"+event);
                result = funcCallAPIThree(g_application,module,moduleCategory,action,event,requestData,userName,"","","");
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

    public String funcVerifyDocument(String application, String module, String action, String event, String requestData,
                                     String userName, String param1, String param2, String param3) {
        CRMAppDto crmAppDtoreq = userService.findAppByID(5);
        String ls_action = null, ls_channel = null, ls_request = null, ls_response = null;
        Long ll_inquiry_id = null, ll_doc_id = null;
        String ls_remarks = null;
        if (requestData.isEmpty()) {
            return "request body can't be empty";
        }
        // store request
        ls_request = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), requestData);
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(requestData);
            ls_channel = jsonObject.getString("channel");
            ll_inquiry_id = jsonObject.getJSONObject("request_data").getLong("refID");
            ll_doc_id = jsonObject.getJSONObject("request_data").getLong("docID");
            ls_remarks = jsonObject.getJSONObject("request_data").getString("comment");

        } catch (JSONException e) {
            return userService.getJsonError("-99", g_err_title, g_err_msg, e.getMessage(), "99", "W", action,
                    ls_request, userName, "inquiry", "E");
        }
        if (ls_channel.isEmpty()) {
            return userService.getJsonError("-99", "Error2!", g_err_msg, "Channel Code not Found.", "99", "W", action,
                    ls_request, userName, "inquiry", "U");
        }
        if (ll_inquiry_id == null) {
            return userService.getJsonError("-99", "Error3!", g_err_msg, "Refrence ID not Found.", "99", "W", action,
                    ls_request, userName, "inquiry", "U");
        }
        if (ll_doc_id == null) {
            return userService.getJsonError("-99", "Error!", g_err_msg, "Document ID not Found.", "99", "W", action,
                    ls_request, userName, "inquiry", "U");
        }
        userService.saveJsonLog(ls_channel, "req", action, ls_request, userName, module);
        int docCount = userService.getDocumentCnt(ll_inquiry_id, ll_doc_id);
        if (docCount > 0) {
            int li_row = userService.updateDocStatus(ll_inquiry_id, ll_doc_id, "V", ls_remarks);
            if (li_row > 0) {
                try {
                    jsonObject2.put("status", "0");
                    jsonObject1.put("message", "Document Verified sucessfully");
                    jsonObject2.put("response_data", jsonObject1);
                    ls_response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(),
                            jsonObject2.toString());
                    userService.saveJsonLog(ls_channel, "res", action, ls_response, userName, module);
                    return jsonObject2.toString();
                } catch (JSONException e) {
                    return userService.getJsonError("-99", "Error!", g_err_msg, e.getMessage(), "99", "W", action,
                            ls_request, userName, "inquiry", "E");
                }
            } else {
                return userService.getJsonError("-99", "Error!", g_err_msg, "Error while update Document Verification",
                        "99", "W", action, ls_request, userName, "inquiry", "U");
            }
        } else {
            return userService.getJsonError("-99", "Error!", g_err_msg, "Documents not Found for this refID", "99", "W",
                    "document upload", ls_request, userName, "inquiry", "U");
        }
    }

    public String funcRejectDocument(String application, String module, String action, String event, String requestData,
                                     String userName, String param1, String param2, String param3) {
        CRMAppDto crmAppDtoreq = userService.findAppByID(5);
        String ls_action = null, ls_channel = null, ls_request = null, ls_response = null;
        Long ll_inquiry_id = null, ll_doc_id = null;
        String ls_remarks = null;
        if (requestData.isEmpty()) {
            return "request body can't be empty";
        }
        // store request
        ls_request = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), requestData);
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(requestData);
            ls_channel = jsonObject.getString("channel");
            ll_inquiry_id = jsonObject.getJSONObject("request_data").getLong("refID");
            ll_doc_id = jsonObject.getJSONObject("request_data").getLong("docID");
            ls_remarks = jsonObject.getJSONObject("request_data").getString("comment");

        } catch (JSONException e) {
            return userService.getJsonError("-99", g_err_title, g_err_msg, e.getMessage(), "99", ls_channel, action,
                    ls_request, userName, "inquiry", "E");
        }
        if (ls_channel.isEmpty()) {
            return userService.getJsonError("-99", "Error2!", g_err_msg, "Channel Code not Found.", "99", ls_channel,
                    action, ls_request, userName, "inquiry", "U");
        }
        if (ll_inquiry_id == null) {
            return userService.getJsonError("-99", "Error3!", g_err_msg, "Refrence ID not Found.", "99", ls_channel,
                    action, ls_request, userName, "inquiry", "U");
        }
        if (ll_doc_id == null) {
            return userService.getJsonError("-99", "Error!", g_err_msg, "Document ID not Found.", "99", ls_channel,
                    action, ls_request, userName, "inquiry", "U");
        }
        userService.saveJsonLog(ls_channel, "req", action, ls_request, userName, module);
        int docCount = userService.getDocumentCnt(ll_inquiry_id, ll_doc_id);
        if (docCount > 0) {
            int li_row = userService.updateDocStatus(ll_inquiry_id, ll_doc_id, "R", ls_remarks);
            if (li_row > 0) {
                try {
                    jsonObject2.put("status", "0");
                    jsonObject1.put("message", "Document is Rejected");
                    jsonObject2.put("response_data", jsonObject1);
                    ls_response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(),
                            jsonObject2.toString());
                    userService.saveJsonLog(ls_channel, "res", action, ls_response, userName, module);
                    return jsonObject2.toString();
                } catch (JSONException e) {
                    return userService.getJsonError("-99", "Error!", g_err_msg, e.getMessage(), "99", ls_channel,
                            action, ls_request, userName, "inquiry", "E");
                }
            } else {
                return userService.getJsonError("-99", "Error!", g_err_msg, "Error while in Document Rejection", "99",
                        "W", "document reject", ls_request, userName, "inquiry", "U");
            }
        } else {
            return userService.getJsonError("-99", "Error!", g_err_msg, "Documents not Found for this refID", "99", "W",
                    "document upload", ls_request, userName, "inquiry", "U");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/inquiry/document/upload", produces = { "application/json",
            "application/json" }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE })
    public String funcDocumentUpload(@RequestParam("file") List<MultipartFile> files,
                                     @RequestParam(value = "refID", required = true) String refID,
                                     @RequestParam(value = "docID", required = true) String docID, OAuth2Authentication authentication) {
        String ls_request = null, ls_response = null;
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        String userName = user.getUsername();
        CRMAppDto crmAppDtoreq = userService.findAppByID(5);
        ls_request = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(),
                "document upload " + refID);
        userService.saveJsonLog("W", "req", "document upload", ls_request, userName, "inquiry");
        try {
            Long ll_inquiryId = null;
            JSONObject jsonObject1 = null;
            JSONObject jsonObject2 = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            String ls_uuid = null;
            String ls_delete_status = userService.deleteDocument(Long.parseLong(refID), Long.parseLong(docID));
            JSONObject json = new JSONObject(ls_delete_status);
            String ls_status = json.getString("status");
            if (ls_status.equals("0")) {
                for (MultipartFile mimeMultipart : files) {
                    jsonObject1 = new JSONObject();
                    DocUploadDtl docUploadDtl = new DocUploadDtl();
                    DocUploadBlobDtl docUploadBlobDtl = new DocUploadBlobDtl();
                    docUploadDtl.setDoc_id(Long.parseLong(docID));
                    docUploadDtl.setDoc_upoload_dt(new Date());
                    docUploadDtl.setInquiry_id(Long.parseLong(refID));
                    docUploadDtl.setStatus("P");
                    UUID uuid = UUID.randomUUID();
                    ls_uuid = uuid.toString();
                    try {
                        String ls_encode_uuid = Base64.encodeBase64String(ls_uuid.getBytes());
                        docUploadBlobDtl.setUuid(ls_encode_uuid);
                        docUploadBlobDtl.setData(mimeMultipart.getBytes());
                        docUploadBlobDtl.setDoc_name(mimeMultipart.getOriginalFilename());
                        docUploadBlobDtl.setDoc_size(mimeMultipart.getSize());
                        docUploadBlobDtl.setDocContentType(mimeMultipart.getContentType());
                        userService.saveDocumentLob(docUploadBlobDtl);
                        docUploadDtl.setDoc_uuid(ls_encode_uuid);
                        userService.saveDocument(docUploadDtl);
                        jsonObject1.put("message", mimeMultipart.getOriginalFilename() + "doc uploaded sucessfully");
                        jsonArray.put(jsonObject1);
                    } catch (Exception e) {
                        try {
                            jsonObject2.put("status", "99");
                            jsonObject1.put("message", "Error while " + mimeMultipart.getOriginalFilename());
                            jsonObject2.put("response_data", jsonObject1);
                        } catch (JSONException jsonException) {
                            return userService.getJsonError("-99", "Error!", g_err_msg, e.getMessage(), "99", "W",
                                    "document upload", ls_request, user.getUsername(), "inquiry", "E");
                        }
                    }
                }
                try {
                    jsonObject2.put("status", "0");
                    jsonObject2.put("response_data", jsonArray);
                    userService.saveJsonLog("W", "res", "document upload", userService.func_get_result_val(
                            crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString()), userName, "inquiry");
                    return jsonObject2.toString();
                } catch (JSONException e) {
                    return userService.getJsonError("-99", "Error!", g_err_msg, e.getMessage(), "99", "W",
                            "document upload", ls_request, user.getUsername(), "inquiry", "E");
                }
            } else {
                return json.toString();
            }
        } catch (Exception e) {
            return userService.getJsonError("-99", "Error!", g_err_msg, e.getMessage(), "99", "W", "document upload",
                    ls_request, user.getUsername(), "inquiry", "E");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/inquiry/document/download", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> download(@RequestParam String docUUID, @RequestParam(name = "token") String token) {
        String ls_request = null, userName = null;
        if ((!docUUID.isEmpty()) && (!token.isEmpty())) {
            try {
                User user = (User) userService.readAuth(token).getUserAuthentication().getPrincipal();
                userName = user.getUsername();
            } catch (NullPointerException e) {
                throw new TokenNotValidException("99", "-99", "Token not valid.", "Token not valid.",
                        "Token not valid.");
            } catch (Exception e) {
                throw new TokenNotValidException("99", "-99", "Token not valid.", "Token not valid.",
                        "Token not valid.");
            }
            CRMAppDto crmAppDtoreq = userService.findAppByID(5);
            ls_request = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(),
                    "document upload " + docUUID);
            userService.saveJsonLog("W", "req", "document download", ls_request, userName, "inquiry");

            DocUploadBlobDtl docUploadBlobDtl = userService.findDocByUUID(docUUID);
            // store request
            try {
                userService.saveJsonLog("W", "res", "document download", "download sucessfully", userName, "inquiry");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + docUploadBlobDtl.getDoc_name() + "\"")
                        .contentType(MediaType.valueOf(docUploadBlobDtl.getDocContentType()))
                        .body(docUploadBlobDtl.getData());
            } catch (Exception e) {
                throw new TokenNotValidException("99", "-99", "Error!.", e.getMessage(),
                        "Error while download file " + docUploadBlobDtl.getDoc_name());
            }
        }
        return null;
    }

    public String funcDeleteDocument(String application, String module, String action, String event, String requestData,
                                     String userName, String param1, String param2, String param3) {

        CRMAppDto crmAppDtoreq = userService.findAppByID(5);
        String ls_action = null, ls_channel = null, ls_request = null, ls_response = null;
        Long ll_inquiry_id = null, ll_doc_id = null;
        String ls_doc_status = null;
        if (requestData.isEmpty()) {
            return "request body can't be empty";
        }
        // store request
        ls_request = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), requestData);
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(requestData);
            ls_action = jsonObject.getString("action");
            ls_channel = jsonObject.getString("channel");
            ll_inquiry_id = jsonObject.getJSONObject("request_data").getLong("refID");
            ll_doc_id = jsonObject.getJSONObject("request_data").getLong("docID");

        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_err_msg, e.getMessage(), "99", "W", "document upload",
                    ls_request, userName, "inquiry", "E");
        }
        if (action.isEmpty()) {
            return userService.getJsonError("-99", "Error!", g_err_msg, "Action Code not Found", "99", "W",
                    "document upload", ls_request, userName, "inquiry", "U");
        }
        if (ls_channel.isEmpty()) {
            return userService.getJsonError("-99", "Error!", g_err_msg, "Channel Code not Found.", "99", "W", action,
                    ls_request, userName, "inquiry", "U");
        }
        if (ll_inquiry_id == null) {
            return userService.getJsonError("-99", "Error!", g_err_msg, "Refrence ID not Found.", "99", "W", action,
                    ls_request, userName, "inquiry", "U");
        }
        if (ll_doc_id == null) {
            return userService.getJsonError("-99", "Error!", g_err_msg, "Document ID not Found.", "99", "W", action,
                    ls_request, userName, "inquiry", "U");
        }
        userService.saveJsonLog(ls_channel, "req", action, ls_request, userName, module);
        try {
            try {
                userService.deleteDocument(ll_inquiry_id, ll_doc_id);
                jsonObject2.put("status", "0");
                jsonObject1.put("message", "documents are removed successfully");
                jsonObject2.put("response_data", jsonObject1);
                ls_response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(),
                        jsonObject2.toString());
                userService.saveJsonLog(ls_channel, "res", action, ls_response, userName, module);
                return jsonObject2.toString();
            } catch (Exception jsonException) {
                return userService.getJsonError("-99", "Error!", g_err_msg, jsonException.getMessage(), "99", "W",
                        action, ls_request, userName, "inquiry", "E");
            }
        } catch (Exception e) {
            return userService.getJsonError("-99", "Error!", g_err_msg, e.getMessage(), "99", "W", action, ls_request,
                    userName, "inquiry", "E");
        }
    }

    /*****************************************
     * GST INFO
     ***************************************************************/
    public String funcInitiateGstInfo(String application,String module,String moduleCategory,String action,String event,String requestdata,String userName) {
        /* declaration */
        JSONObject  jsonObject,jsonObject1,jsonObjectReq,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObjectReq = new JSONObject();
        jsonObject2 = new JSONObject();
        HashMap  outParam= new HashMap(), inParam = new HashMap();
        String data = null,loginUserId = userService.getLoginUserID(userName);
        String channel = null,gstNumber=null,result;
        String      patterndate = "yyyyMMdd";
        String      patterntime = "HHmmssSSS";
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patterndate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patterntime);
        long refId=0, serialNo = 1;
        String entityType="L",reInitiate="N",completeURL = null;
        /*end declaration*/

        module = module+"/"+moduleCategory;
        action = action+"/"+event;

        /*read requestdata*/
        if(requestdata.isEmpty()){return "Request Body Empty.";}
        //check api allow//
        int para_cd = 7;
        SysParaMst sysParaMst = userService.getParaVal("9999","9999",para_cd);
        if(sysParaMst.getPara_value().equals("N")){
            return userService.getJsonError("99","Service Closed","Service has been disabled temporary,Please try after sometime.","Service Closed with code:"+para_cd,"99",channel,action,requestdata,userName,module,"U");
        }
        /*{"request_data":{"refID":"8",gstNumber=""}}*/
        try {
            jsonObject     = new JSONObject(requestdata);
            jsonObjectReq  = jsonObject.getJSONObject("request_data");
            channel        = jsonObject.getString("channel");
            if (jsonObjectReq.has("gstNumber")){
                gstNumber   =jsonObjectReq.getString("gstNumber");
            }
            if(jsonObjectReq.has("refID")){
                refId   = jsonObjectReq.getLong("refID");
                Utility.print("redID:"+refId);
            }
            if (jsonObjectReq.has("reInitiate")){
                reInitiate  = jsonObjectReq.getString("reInitiate");
            }
        }catch (JSONException e){
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
        if (action.isEmpty()){
            return userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
        }
        if (channel.isEmpty()){
            return userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
        }
        userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
        if (refId <=0) {
            return userService.getJsonError("-99","Request Error!","refId Not Found.","refId Not Found.","99",channel,action,requestdata,userName,module,"U");
        }

        if(entityType.indexOf("L")<0 && entityType.indexOf("I")<0){
            return userService.getJsonError("-99","Request Error!","Invalid entityType.","Invalid entityType.","99",channel,action,requestdata,userName,module,"U");
        }
        if(gstNumber==null){
            data = userService.getGstNumberById(refId);
            if(data==null){
                return userService.getJsonError("-99","Request Error!","GST Number Not Found.","GST Number Not Found.","99",channel,action,requestdata,userName,module,"U");
            }else if(data.contains("error")){
                return userService.getJsonError("-99","Error!",g_error_msg,data,"99",channel,action,requestdata,userName,module,"E");
            }else{
                gstNumber = data;
            }
            if (gstNumber==null){
                return userService.getJsonError("-99","Request Error!","GST Number Not Found.","GST Number Not Found.","99",channel,action,requestdata,userName,module,"U");
            }
        }


        //check initiate request
        //if(!reInitiate.equals("Y")){
        result = funcCheckPerfiosInitiateRequest(refId,serialNo,entityType,null,"GST_INFO");
        if(!result.equals("success")){
            return  result;
        }
        //}
        /*get API Details*/
        URLConfigDto urlConfigDto = userService.findURLDtlByID(10);
        if(urlConfigDto.getKey().isEmpty()){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL User key Not Found.","99",channel,action,requestdata,userName,module,"U");
        }
        completeURL = userService.func_get_base_url("los/webhooks")+urlConfigDto.getSmtp_server();
        System.out.println("Complete URL:"+completeURL);

        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid();
        String date = simpleDateFormatDate.format(new Date());
        String time = simpleDateFormatTime.format(new Date());
        UUID uuid = UUID.randomUUID();
        String ls_userID = time+uuid.toString()+date;

        //set GST details
        PerfiosReqResDtl perfiosReqResDtl = new PerfiosReqResDtl();
        perfiosReqResDtl.setRef_tran_cd(refId);
        perfiosReqResDtl.setRef_sr_cd(serialNo);
        perfiosReqResDtl.setUserid(ls_userID);
        perfiosReqResDtl.setStatus("I");
        perfiosReqResDtl.setRequest_type("GST_INFO");
        perfiosReqResDtl.setInitiated_req(requestdata);
        perfiosReqResDtl.setEntity_type(entityType);
        perfiosReqResDtl.setEntered_by(loginUserId);
        perfiosReqResDtl.setLast_entered_by(loginUserId);


        String requestjson = "{\"clientTransactionId\":\""+urlConfigDto.getKey()+"\",\"gstNumbers\":[\""+gstNumber+"\"],\"transactionCompleteUrl\":\""+completeURL+"\"}";
        System.out.println("Request JSON:"+requestjson);
        try {
            URL obj = new URL(sendURL);
            Boolean lbstatus = null;
            String transactionId = null;
            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("signature", getSignature(requestjson));
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(requestjson.getBytes());
            os.flush();
            os.close();
            //get response body of api request
            result = Utility.getURLResponse(conn);
            perfiosReqResDtl.setUrl_res(result);
            jsonObject1 = new JSONObject(result);
            if (conn.getResponseCode() == 200) {
                lbstatus = jsonObject1.getBoolean("success");
                if (lbstatus) {
                    transactionId = jsonObject1.getString("transactionId");
                    perfiosReqResDtl.setTransaction_id(transactionId);
                    perfiosReqResDtl.setReq_status("true");
                    userService.savePerfiosReqResDtl(perfiosReqResDtl);

                    jsonObject2.put("status", "0");
                    jsonObject2.put("response_data", jsonObject1);
                    userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, null);
                    return jsonObject2.toString();
                }else {
                    String message, code;
                    /*{code: InvalidGSTIN,message: Invalid GSTIN. A valid GSTIN looks like this 29ABCDE1234F1ZW,success: false}*/
                    perfiosReqResDtl = null;
                    message = jsonObject1.getString("message");
                    code = jsonObject1.getString("code");
                    return userService.getJsonError("-99", "GST Info Request Failed.", message, code + ":" + message, "99", channel, action, requestdata, userName, module, "U");
                }
            }else{
                if(jsonObject1.has("code")){
                    String message, code;
                    /*{code: InvalidGSTIN,message: Invalid GSTIN. A valid GSTIN looks like this 29ABCDE1234F1ZW,success: false}*/
                    message = jsonObject1.getString("message");
                    code = jsonObject1.getString("code");
                    return userService.getJsonError("-99", "GST Info Request Failed.", message, code + ":" + message, "99", channel, action, requestdata, userName, module, "U");
                }else{
                    return userService.getJsonError("-99", "GST Info Request Failed!", g_error_msg, result, "99", channel, action, requestdata, userName, module, "U");
                }
            }
        }catch(IOException e){
            return userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }catch (JSONException e){
            return userService.getJsonError("-99","Error- JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
        catch(Exception e){
            return userService.getJsonError("-99","Error- Exception",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
    }

    public String funcGstInfoStatus(String application,String module,String moduleCategory,String action,String event,String requestdata,String userName){
        String httpStatus = null, responseMessage = null, perfiosTransactoinId = null, channel=null,result;
        JSONObject jsonObject, jsonObject1;
        //read request data
        try {
            jsonObject 				= new JSONObject(requestdata);
            perfiosTransactoinId 	= jsonObject.getJSONObject("request_data").getString("perfiosTransactoinId");
            channel 		  		= jsonObject.getString("channel");
        } catch (JSONException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestdata, userName, module, "E");
        }

        module = module+"/"+moduleCategory;
        action = action+"/"+event;
        //validations
        if(perfiosTransactoinId.isEmpty()) {
            return userService.getJsonError("-99", "Error in GST Info Status.", "transactoinId Not Found.", "transactoinId Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if(channel.isEmpty()){
            return userService.getJsonError("-99", "Error in GST Info Status.", g_error_msg, "Channel Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        //save request
        userService.saveJsonLog(channel, "req", action, requestdata, userName, module);

        URLConfigDto urlConfigDto = userService.findURLDtlByID(13);
        PerfiosReqResDto perfiosReqResDto = userService.findByPerfiosTransactionID(perfiosTransactoinId);
        //dto validation
        if(urlConfigDto.getUserid()==null){
            return userService.getJsonError("-99", "URL Configuration Not Found.", g_error_msg, "URL detail Not Found.", "99", channel, action, requestdata, userName, module, "E");
        }

        if(perfiosReqResDto.getUserid()==null){
            return userService.getJsonError("-99","Transaction Not found at our side.","Transaction Not Found at our side.","Transaction Not Found.","99",channel,action,requestdata,userName,module,"U");
        }
        //check status first in db
        if (perfiosReqResDto.getStatus().equals("S")) {
            responseMessage = "{\"status\":\"0\",\"response_data\":{\"message\":\"\",\"status\": true}}";
            userService.saveJsonLog(channel, "res", action, responseMessage, userName, module);
            return responseMessage;
        }else if(perfiosReqResDto.getStatus().equals("F")){
            return userService.getJsonError("-99","Unable to process request.","Transaction Status Found Failed.","Transaction Status is Failed.","99",channel,action,requestdata,userName,module,"U");
        }else if(perfiosReqResDto.getStatus().equals("E")){
            return userService.getJsonError("-99","Unable to process request.","Transaction has error.","Transaction has error.","99",channel,action,requestdata,userName,module,"U");
        }
        try {
            String ls_url = urlConfigDto.getUrl() + urlConfigDto.getUserid() + "/" + perfiosTransactoinId;
            /*http*/
            URL obj = new URL(ls_url);
            HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("content-Type", "application/json");
            result = Utility.getURLResponse(conn);
            jsonObject = new JSONObject(result);
            httpStatus = String.valueOf(conn.getResponseCode());
            Utility.print("2. API call Status:" + httpStatus);

            if (conn.getResponseCode()==200) {
                jsonObject1 = new JSONObject();
                jsonObject1.put("status", "0");
                jsonObject1.put("response_data", jsonObject);
                userService.saveJsonLog(channel, "res", action, jsonObject1.toString(), userName, null);
                if(jsonObject.getString("message").equals("COMPLETED")){
                    responseMessage = gstInfoRetrieve(perfiosTransactoinId,11,null);
                    System.out.println("Data Retrieve Status:"+responseMessage);
                }else{
                    updateGstDocumentStatus(perfiosTransactoinId,"GST_INFO_STATUS",result);
                }
                return jsonObject1.toString();
            }else {
                updateGstDocumentStatus(perfiosTransactoinId,"GST_INFO_STATUS",result);
                return userService.getJsonError("-99", "GST Info Status API Error!", g_error_msg, result, "99", channel, action, requestdata, userName, module, "E");
            }
        }catch (JSONException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Error- JSONException", g_error_msg, e.getMessage(), "-99", channel, action, requestdata, userName, module, "E");
        } catch (Exception e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Error- Exception", g_error_msg, e.getMessage(), "-99", channel, action, requestdata, userName, module, "E");

        }
    }

    public String funcGstInfoWebhook(String requestdata) {
        String perfiosTransactionId = null, status = null;

        Map<String, String> data = userService.parseUrlFragment(requestdata);
        perfiosTransactionId = data.get("perfiosTransactionId");
        status = data.get("status");

        if ((!perfiosTransactionId.isEmpty()) && (!status.isEmpty())) {
            if ((status.equals("COMPLETED"))) {
                return gstInfoRetrieve(perfiosTransactionId, 11, requestdata);
            }
        }
        return "";
    }

    public String gstInfoRetrieve(String ls_transaction_id, int urlID, String ls_webhookres) {
        Utility utility = new Utility();
        String ls_download_status = null;
        String ls_action = "gstinfoRetrieve", ls_channel = "W";
        String lstatus = null, ls_url = null,processResponse=utility.getWebhookProcessStructure();
        URLConfigDto urlConfigDto = userService.findURLDtlByID(urlID);
        //processResponse structure
        String processStatus="success",processLoc=className+"/gstInfoRetrieve()",processTitle="",processDesc="";
        ResponseEntity<String> result = null;
        try {
            if (!urlConfigDto.getUserid().isEmpty()) {
                try {
                    PerfiosReqResDtl perfiosReqResDtl = new PerfiosReqResDtl();
                    RestTemplate restTemplate = new RestTemplate();
                    // URl for retrive transaction information
                    ls_url = urlConfigDto.getUrl() + urlConfigDto.getUserid() + "/" + ls_transaction_id;
                    Utility.print("1. generated URL:" + ls_url);
                    // call
                    result = restTemplate.getForEntity(ls_url, String.class);
                    lstatus = result.getStatusCode().toString();
                    Utility.print("2. API call Status:" + lstatus);
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
                            System.out.println("Result : MalformedURLException");
                            userService.getJsonError("99", "Error: MalformedURLException.", g_error_msg, e.getMessage(),
                                    "-99", ls_channel, ls_action, ls_webhookres, null, null, "E");
                            processStatus = "failed";
                            processTitle  = "MalformedURLException";
                            processDesc   = "(A)url:"+ls_url+"|error:"+e.getMessage();
                        } catch (IOException e) {
                            System.out.println("Result : IOException");
                            userService.getJsonError("99", "Error: IOException.", g_error_msg, e.getMessage(), "-99",
                                    ls_channel, ls_action, ls_webhookres, null, null, "E");
                            processStatus = "failed";
                            processTitle  = "IOException";
                            processDesc   = "(B)url:"+ls_url+"|error:"+e.getMessage();
                        } catch (Exception e) {
                            System.out.println("Result : Exception");
                            userService.getJsonError("99", "Error: Exception.", g_error_msg, e.getMessage(), "-99",
                                    ls_channel, ls_action, ls_webhookres, null, null, "E");
                            processStatus = "failed";
                            processTitle  = "Exception";
                            processDesc   = "(C)url:"+ls_url+"|error:"+e.getMessage();
                        }
                    }else{
                        userService.updatePerfiosWebhookStatus(ls_transaction_id, lstatus, "F", ls_webhookres, null,
                                null, null, "F", "RETRIEVE TIME ERROR/fetch-url:"+ls_url);
                        processStatus = "failed";
                        processTitle  = "Response Status:"+result.getStatusCode();
                        processDesc   = "(D)url:"+ls_url;
                    }
                }catch (Exception e) {
                    userService.getJsonError("99", "Error: IOException.", g_error_msg, e.getMessage(), "-99",
                            ls_channel, ls_action, ls_webhookres, null, null, "E");
                    processStatus = "failed";
                    processTitle  = "Exception";
                    processDesc   = "(E)url:"+ls_url+"|error:"+e.getMessage();
                }
            }else{
                userService.getJsonError("-99", "URL Configuration Not Found.", g_error_msg,
                        "URL Configuration Not Found.", "99", "W", ls_action, ls_webhookres, null, null, "U");
                processStatus = "failed";
                processTitle  = "URL Configuration Not Found";
                processDesc   = "(F)urlId:"+urlID;
            }
        }catch (Exception e){
            userService.getJsonError("-99", "Error: Exception", g_error_msg, e.getMessage(), "99", "W", ls_action,
                    ls_webhookres, null, null, "E");
            processStatus = "failed";
            processTitle  = "Exception";
            processDesc   = "(G)url:"+ls_url+"|error:"+e.getMessage();
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

    public static String getSignature(String requestdata) {
        String org_key = "1de846bd-56fb-491e-ba39-3f05e45b66ab";
        try {
            String BeforeSignature = "PERFIOS-HMACSHA256" + " " + requestdata;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_Organisationkey = new SecretKeySpec(org_key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_Organisationkey);
            String Signature = org.apache.commons.codec.binary.Base64
                    .encodeBase64String(sha256_HMAC.doFinal(BeforeSignature.getBytes()));
            System.out.println("Signature : " + Signature);
            return Signature;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /*****************************************
     * GST UPLOAD
     ***************************************************************/
    // gst initiate upload document
    public String funcInitiateGstUpload(String application,String module,String moduleCategory,String action,String event,String requestdata,String userName){
        /*declaration */
        final String loginUserId = userService.getLoginUserID(userName);
        Utility utility = new Utility();
        HashMap  outParam= new HashMap(), inParam = new HashMap();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        final String dataFetchMode = "STATEMENT_UPLOAD";
        final String redirectURL = "https://www.google.com/";
        long refId=0,serialNo = 1;
        String entityType = "L", reInitiate ="N",processFor=null;
        String      data=null,channel=null, gstNumber=null, result=null, periodFrom=null, periodTo=null;
        String      encryptgstNumber=null, encryptusername=null, completeURL=null, productType="", additionalParam="";
        String      patterndate = "yyyyMMdd";
        String      patterntime = "HHmmssSSS";
        String      toYear="",toMonth="";
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patterndate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patterntime);


        module = module+"/"+moduleCategory;
        action = action+"/"+event;
        /*read requestdata*/
        if(requestdata.isEmpty()){return "Request Body Empty.";}
        //check api allow//
        int sys_para_service_enable = 8, sys_para_product_type = 11;
        SysParaMst sysParaMst = userService.getParaVal("9999","9999",sys_para_service_enable);
        if(sysParaMst.getPara_value().equals("N")){
            return userService.getJsonError("99","Service Closed","Service has been disabled temporary,Please try after sometime.","Service Closed with code:"+sys_para_service_enable,"99",channel,action,requestdata,userName,module,"U");
        }
        sysParaMst = userService.getParaVal("9999","9999",sys_para_product_type);
        productType = sysParaMst.getPara_value();
        try {
            jsonObject  = new JSONObject(requestdata);
            channel     = jsonObject.getString("channel");
            if (action==null){
                return userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
            }
            if (channel==null){
                return userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
            }
            //get request_data jsonObject
            jsonObject  = jsonObject.getJSONObject("request_data");
            Utility.print("request_data:"+jsonObject.toString());
            if(jsonObject.has("refID")){
                refId   = jsonObject.getLong("refID");
            }
            if (jsonObject.has("reInitiate")){
                reInitiate  = jsonObject.getString("reInitiate");
            }

            if(jsonObject.has("periodFrom")){
                periodFrom   = jsonObject.getString("periodFrom");
            }
            if(jsonObject.has("periodTo")){
                periodTo   = jsonObject.getString("periodTo");
            }
            if(jsonObject.has("processFor")){
                processFor = jsonObject.getString("processFor");
            }

            userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
            if (refId<=0){
                return userService.getJsonError("-99","Request Error!","refID Not Found.","refID Not Found.","99",channel,action,requestdata,userName,module,"U");
            }

            if(periodFrom == null || periodFrom.isEmpty() ){
                return userService.getJsonError("-99","Request Error!","periodFrom Not Found.","periodFrom Not Found.","99",channel,action,requestdata,userName,module,"U");
            }else{
                /**old**/
                    //actual get : 2021-04-31T18:30:00.000Z
                    //need to set: 042021 [mmYYYY]
                    //2020-12-31T18:30:00.000Z
                    //toYear  = periodFrom.substring(0,4);
                    //toMonth = periodFrom.substring(5,7);
                    //periodFrom  = toMonth+toYear;
                /**old**/
                //new actual get: DY MON DD YYYY HH24:MI:SS" +0530"
                //need to set: 042021 [mmYYYY]
                periodFrom = userService.getDateFormattedString(periodFrom,"MMyyyy");
            }
            if(periodTo == null || periodTo.isEmpty() ){
                return userService.getJsonError("-99","Request Error!","periodTo Not Found.","periodTo Not Found.","99",channel,action,requestdata,userName,module,"U");
            }else {
                /**old**/
                    //actual get : 2021-04-31T18:30:00.000Z
                    //need to set: 042021 [mmYYYY]
                    //toYear  = periodTo.substring(0,4);
                    //toMonth = periodTo.substring(5,7);
                    //periodTo = toMonth+toYear;
                /**old**/
                periodTo = userService.getDateFormattedString(periodTo,"MMyyyy");
            }
            if(entityType.indexOf("L")<0 && entityType.indexOf("I")<0){
                return userService.getJsonError("-99","Request Error!","Invalid entityType.","Invalid entityType.","99",channel,action,requestdata,userName,module,"U");
            }
            if(processFor==null||processFor.isEmpty()){
                return userService.getJsonError("-99","Request Error!","Parameter \"processFor\" not found","Parameter \"processFor\" not found.","99",channel,action,requestdata,userName,module,"U");
            }
        }catch (JSONException e){
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }catch(Exception e){
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }

        data = userService.getGstNumberById(refId);
        if(data==null){
            return userService.getJsonError("-99","Request Error!","GST Number Not Found.","GST Number Not Found.","99",channel,action,requestdata,userName,module,"U");
        }else if(data.contains("error")){
            return userService.getJsonError("-99","Error!",g_error_msg,data,"99",channel,action,requestdata,userName,module,"E");
        }else{
            gstNumber = data;
        }
        if (gstNumber==null){
            return userService.getJsonError("-99","Request Error!","GST Number Not Found.","GST Number Not Found.","99",channel,action,requestdata,userName,module,"U");
        }else{
            encryptgstNumber = Utility.perfiosDataEncryptPub(gstNumber);
        }


        result = funcCheckPerfiosInitiateRequest(refId,serialNo,entityType,processFor,"GST_UPLOAD");
        if(!result.equals("success")){ return  result; }

        /*get API Details*/
        URLConfigDto urlConfigDto = userService.findURLDtlByID(18);
        if(urlConfigDto.getKey()==null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL key Not Found.","99",channel,action,requestdata,userName,module,"U");
        }
        completeURL = userService.func_get_base_url("los/webhooks")+urlConfigDto.getSmtp_server();
        System.out.println("Complete URL:"+completeURL);

        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid(); //ratnaaFin
        Utility.print("sendURL:"+sendURL);
        String date = simpleDateFormatDate.format(new Date());
        String time = simpleDateFormatTime.format(new Date());
        UUID uuid = UUID.randomUUID();
        String userID = time+uuid.toString()+date;

        //set GST details
        PerfiosReqResDtl perfiosReqResDtl = new PerfiosReqResDtl();
        perfiosReqResDtl.setRef_tran_cd(refId);
        perfiosReqResDtl.setRef_sr_cd(serialNo);
        perfiosReqResDtl.setUserid(userID);
        perfiosReqResDtl.setStatus("I");
        perfiosReqResDtl.setRequest_type("GST_UPLOAD");
        perfiosReqResDtl.setInitiated_req(requestdata);
        perfiosReqResDtl.setEntity_type(entityType);
        perfiosReqResDtl.setEntered_by(loginUserId);
        perfiosReqResDtl.setLast_entered_by(loginUserId);
        Utility.print("loginUserId:"+loginUserId);
        Utility.print("machine:"+perfiosReqResDtl.getMachine_nm());
        Utility.print("machine:"+perfiosReqResDtl.getLast_modified_date());


        //set request parameter
        periodFrom      = periodFrom == null ? "" : periodFrom;
        periodTo        = periodTo == null ? "" : periodTo;
//        productType     = productType.equalsIgnoreCase("null") ? "" : productType; /*max len:10*/

        String create_Json;
        Utility.print(productType);
        Utility.print(dataFetchMode);
        if(productType.equalsIgnoreCase("null")){
            productType = "";
        }
        create_Json = "{\"clientTransactionId\":\""+urlConfigDto.getKey()+"\",\"gstNumber\":\""+encryptgstNumber+"\",\"username\":\""+encryptusername+"\",\"periodFrom\":\""+periodFrom+"\",\"periodTo\":\""+periodTo+"\",\"redirectUrl\": \""+redirectURL+"\",\"transactionCompleteUrl\":\""+completeURL+"\",\"dataFetchMode\":\""+dataFetchMode+"\",\"productType\":\""+productType+"\"}";//,\"additionalParam\":\""+additionalParam+"\"}";

        System.out.println("Request JSON:\n"+create_Json);
        try{
            //call api
            try {
                URL obj = new URL(sendURL);
                Boolean lbstatus = false;
                String transactionId = null, userRedirectURL;
                HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
                conn.setRequestMethod("POST");
                conn.addRequestProperty("content-Type", "application/json");
                System.out.println("create-json:"+create_Json);
                jsonObject1 = new JSONObject(create_Json);
                conn.addRequestProperty("signature", getSignature(create_Json));
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(create_Json.getBytes());
                os.flush();
                os.close();
                //get response body of api request
                result = Utility.getURLResponse(conn);
                perfiosReqResDtl.setUrl_res(result);
                jsonObject1 = new JSONObject(result);
                if(jsonObject1.has("success")){
                    lbstatus = jsonObject1.getBoolean("success");
                }
                if (conn.getResponseCode() == 200 && lbstatus) {
                    utility.print("success request");
                    transactionId 	= jsonObject1.getString("transactionId");
                    userRedirectURL	= jsonObject1.getString("url");
                    perfiosReqResDtl.setTransaction_id(transactionId);
                    perfiosReqResDtl.setReq_status("true");
                    userService.savePerfiosReqResDtl(perfiosReqResDtl);

                    jsonObject2.put("status", "0");
                    jsonObject2.put("response_data", jsonObject1);
                    userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                    return jsonObject2.toString();
                }else{
                    perfiosReqResDtl = null;
                    utility.print("Request failed..!\n"+result);
                    String message, code;
                    /*{code: InvalidGSTIN,message: Invalid GSTIN. A valid GSTIN looks like this 29ABCDE1234F1ZW,success: false}*/
                    if(jsonObject1.has("message")){
                        message = jsonObject1.getString("message");
                    }else{
                        message = "";
                    }
                    if(jsonObject1.has("code")){
                        code = jsonObject1.getString("code");
                    }else{
                        code = "";
                    }
                    return userService.getJsonError("-99", "GST Upload Request Failed.", message, message, "99", channel, action, requestdata, userName, module, "U");
                }
            }catch(IOException e){
                e.printStackTrace();
                return userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            }catch (JSONException e){
                return userService.getJsonError("-99","Error- JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            }
            catch(Exception e){
                return userService.getJsonError("-99","Error- Exception",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            }
        }catch(Exception e){
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
    }

    // gst upload start
    public String funcGstStatementUpload(String application, String module, String action, String event,
                                         String requestdata, String userName) {
        // declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject jsonObject, jsonObject1, jsonObject2;
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        final long docId = 14; // for gst pdf documnent
        String userID, docUUID,loginUserId = userService.getLoginUserID(userName);
        long refId = 0, serialNo = 0;
        int cnt = 0;
        String channel, result, perfiosTransactionId,entityType=null;
        String patterndate = "yyyyMMdd";
        String patterntime = "HHmmssSSS";
        Blob blobfile;
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patterndate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patterntime);
        channel = null;
        result = null;
        perfiosTransactionId = null;
        /* end declaration */

        // read requestdata
        if (requestdata.isEmpty()) {
            return "Request Body Empty.";
        }
        try {
            jsonObject = new JSONObject(requestdata);
            channel = jsonObject.getString("channel");
            if (jsonObject.getJSONObject("request_data").has("perfiosTransactionId")) {
                perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Action Not Found!", "99",
                        channel, "NOT_FOUND", requestdata, userName, module, "U");
            }
            if (channel == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Channel Not Found!", "99",
                        channel, action, requestdata, userName, module, "U");
            }
            if (perfiosTransactionId == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "perfiosTransactionId Not Found!",
                        "99", channel, action, requestdata, userName, module, "U");
            }
            userService.saveJsonLog(channel, "req", action, requestdata, userName, null);
        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        } catch (Exception e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        }
        // get transaction detail
        PerfiosReqResDto perfiosReqResDtlDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDtlDto.getUserid() == null || perfiosReqResDtlDto.getUserid().isEmpty()) {
            return userService.getJsonError("-99", "Initial GST Upload Transaction Not Found",
                    "Initial Transaction Not found.", "Initiate Transaction Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }

        // get url dtl
        URLConfigDto urlConfigDto = userService.findURLDtlByID(21);
        if (urlConfigDto.getUrl() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }
        if (urlConfigDto.getUserid() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL userID Not Found.", "99", channel,
                    action, requestdata, userName, module, "U");
        }
        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid() + "/" + perfiosTransactionId;
        Utility.print("Request Sending URL generated:" + sendURL);
        // get blob file data
        refId = perfiosReqResDtlDto.getRef_tran_cd();
        serialNo = perfiosReqResDtlDto.getRef_sr_cd();
        entityType = perfiosReqResDtlDto.getEntity_type();
        List<DocUploadDtl> docList = userService.getDocListByDocId(refId, serialNo,entityType, docId);
        if (docList.isEmpty()) {
            return userService.getJsonError("-99", "Not Found Any Document", g_error_msg, "Document Not Found.", "99",
                    channel, action, requestdata, userName, module, "U");
        }
        Utility.print("Document upload process starting...");
        try {
            String docType = null, resultOut = null, remarks = null;
            String fileName = "", errorCode = "", errorMessage = "", req_status = "",
                    doc_status = ""/* (U(upload)|F(failed)|R(reject)|P(process)|S(uploded and processed)) */;
            Boolean successBool = false;
            Blob blobdata = null;
            Boolean allFailed = true;
            JSONObject jsonDocStatus = new JSONObject();
            for (DocUploadDtl docUploadDtl : docList) {

                cnt += 1;
                docUUID = docUploadDtl.getDoc_uuid();

                DocUploadBlobDtl docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                if (docUploadBlobDtl == null) {
                    return userService.getJsonError("-99", "Uploading Document Not found.", g_error_msg,
                            "Document Not found for UUID:" + docUUID, "99", channel, action, requestdata, userName,
                            module, "U");
                }
                if (docUploadBlobDtl.getDocContentType() == null) {
                    return userService.getJsonError("-99", "Uploading Document Type not found.", g_error_msg,
                            "Document Type Not found for UUID:" + docUUID, "99", channel, action, requestdata, userName,
                            module, "U");
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
                    // attaching file to sendURL
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
                            /* { success: true,transactionId: PCPTSTTO09IM3PR3VUTDP} */
                            allFailed = false;
                            Utility.print("SUCCESS RESPONSE");
                            req_status = "true";
                            doc_status = "U"; // uploaded
                            jsonObject2 = new JSONObject();
                            jsonObject2.put("status", "0");
                            jsonObject2.put("response_data", jsonObject1);
                            userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                            result = jsonObject2.toString();
                        } else {
                            /*
                             * { errorCode: InsecureFileType, errorMessage: Client tried to upload an
                             * insecure file type (executable, DLL, JAR, etc.), success: false }
                             */
                            Utility.print("ERROR RESPONSE");
                            req_status = "false";
                            doc_status = "F"; // upload failed
                            errorMessage = jsonObject1.getString("message");
                            errorCode = jsonObject1.getString("code");
                            remarks = errorCode + ":" + errorMessage;
//                            result = userService.getJsonError("-99","GST Statements Upload Failed.",errorMessage,errorMessage,"99",channel,action,requestdata,userName,null,"U");
                        }
                        jsonDocStatus.put(fileName, jsonObject1);
                        Utility.print(result);
                    } catch (IOException e) {
                        return userService.getJsonError("-99", "Error-IOException", g_error_msg, e.getMessage(), "99",
                                channel, action, requestdata, userName, module, "E");
                    } catch (JSONException e) {
                        return userService.getJsonError("-99", "JSON Error", g_error_msg, e.getMessage(), "99", channel,
                                action, requestdata, userName, module, "E");
                    }
                    // save each file status
                    connection = jdbcTemplate.getDataSource().getConnection();
                    connection.setAutoCommit(false);
                    cs = connection.prepareCall(
                            "{ call PACK_DOCUMENT.proc_insert_gstupload_document(?,?,?,?,?,?,?,?,?,?,?,?) }");
                    cs.setString(1, perfiosReqResDtlDto.getUserid());
                    cs.setString(2, perfiosTransactionId);
                    cs.setString(3, String.valueOf(docId));
                    cs.setString(4, docUUID);
                    cs.setString(5, req_status);
                    cs.setString(6, docType);
                    cs.setString(7, fileName);
                    cs.setString(8, doc_status);
                    cs.setString(9, resultOut);
                    cs.setString(10, remarks);
                    cs.setString(11, loginUserId);


                    cs.registerOutParameter(12, 2005);
                    cs.execute();
                    final String returnData = cs.getString(12);
                    connection.close();
                    cs.close();
                    Utility.print(returnData);
                } else {
                    return userService.getJsonError("-99", "GST Data Upload Error", g_error_msg,
                            "Fetching Data found null or not failed to get inputstream", "99", channel, action,
                            userName, userName, module, "U");
                }
            }
            if (allFailed) {
                /* NOTE: Update transaction status F because all file failed to upload */
                userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, "F", null, null, null, null, "F",
                        "0 DOCUMENT(S) UPLOADED");
            }
            try {
                jsonObject2 = new JSONObject();
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonDocStatus);
                userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                result = jsonObject2.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return userService.getJsonError("-99", "Error- JSONException", g_error_msg, e.getMessage(), "99",
                        channel, action, userName, userName, module, "E");
            }
            return result;
        } catch (SQLException e) {
            return userService.getJsonError("-99", "Error-SQLException", g_error_msg, e.getMessage(), "99", channel,
                    action, userName, userName, module, "E");
        } catch (FileNotFoundException e) {
            return userService.getJsonError("-99", "Error-FileNotFoundException", g_error_msg, e.getMessage(), "99",
                    channel, action, userName, userName, module, "E");
        }
    }

    // gst upload status
    public String funcGstStatementUploadStatus(String application,String module,String moduleCategory,String action,String event,String requestdata,String userName) {
        //declaration//
        String message = null, transactionId = null, channel = null;
        JSONObject jsonObject, jsonObject1, jsonObject2;
        //end declaration//

        //read requestdata//
        try {
            jsonObject = new JSONObject(requestdata);
            transactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            channel = jsonObject.getString("channel");
            action = "gstStatementUploadStatus";
        } catch (JSONException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Json Error!", g_error_msg, e.getMessage(), "99", channel, action, requestdata, userName, module, "E");
        }

        module = module+"/"+moduleCategory;
        action = action+"/"+event;
        //validations
        if (transactionId.isEmpty()) {
            return userService.getJsonError("-99", "Error in GST Statement Upload Status.", "transactionId Not Found.", "transactionId Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if (channel.isEmpty()) {
            return userService.getJsonError("-99", "Error in GST Statement Upload Status.", "channel Not Found.", "channel Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        userService.saveJsonLog(channel, "req", action, requestdata, userName, null);

        //process
        PerfiosReqResDto perfiosReqResDto = userService.findByPerfiosTransactionID(transactionId);
        if (perfiosReqResDto.getTransaction_id()==null){
            return userService.getJsonError("-99", "Transaction Not Found at our side", g_error_msg, "Transaction Detail Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if (perfiosReqResDto.getStatus().equals("S")) {
            message = "{\"status\":\"0\",\"response_data\":{\"message\":\"COMPLETED\",\"status\": true}}";
            userService.saveJsonLog(channel, "res", action, message, userName, null);
            return message;
        }else if(perfiosReqResDto.getStatus().equals("I")){
            message = "{\"status\":\"0\",\"response_data\":{\"message\":\"INITIATED\",\"status\": true}}";
            userService.saveJsonLog(channel, "res", action, message, userName, module);
            return message;
        }else if(perfiosReqResDto.getStatus().equals("F")){
            return userService.getJsonError("-99","Transaction Status is Failed.","Transaction Status Found Failed, Unable to process Request.","Transaction Status is Failed.","99",channel,action,requestdata,userName,module,"U");
        }

        URLConfigDto urlConfigDto = userService.findURLDtlByID(19);
        if(urlConfigDto.getUserid()==null){
            return userService.getJsonError("-99", "URL Configuration Not Found.", g_error_msg, "URL detail Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }

        try {
            jsonObject2 = new JSONObject();
            Utility utility = new Utility();
            String ls_url,result,ls_response;
            ls_url = urlConfigDto.getUrl() + urlConfigDto.getUserid() + "/" + transactionId;
            Utility.print("Gst Statement Upload generated URL:" + ls_url);
            URL url = new URL(ls_url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("cache-control", "no-cache");
            result = utility.getURLResponse(conn);
            jsonObject1= new JSONObject(result);
            System.out.println("Response Code:"+conn.getResponseCode());
            if(conn.getResponseCode()==200){
			/*{message: COMPLETED,"status : true,
			statementsStatus: [
						{fileName: XYZ.pdf,status: COMPLETED,message: },
						{fileName: ABC.pdf,status: REJECTED,
						message: The uploaded file is not a valid GST return file.}]
			}*/

                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,null);
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
                if(jsonObject1.getString("message").equals("COMPLETED")){
                    gstInfoRetrieve(transactionId,20,null);
                }
                //update gst document status
                int rowUpdate = updateGstDocumentStatus(transactionId,"GST_ITR_UPLOAD_STATUS",jsonObject1.toString());
                Utility.print("GST Document Update Count(s):"+rowUpdate);

                return jsonObject2.toString();
            }else{
                return userService.getJsonError("-99","GST Statement Upload Resport Get Status Failed",g_error_msg,result,"99",channel,action,requestdata,userName,module,"U");
            }
        }catch (Exception e) {
            e.getMessage();
            return userService.getJsonError("-99", "Error- Exception", g_error_msg, e.getMessage(), "-99", channel, action, requestdata, userName, module, "E");
        }
    }
    // update document status
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

    // gst process document
    public String funcGstStatementProcess(String application, String module, String action, String event,
                                          String requestdata, String userName) {
        // declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject jsonObject, jsonObject1, jsonObject2;
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String channel, result, perfiosTransactionId;
        channel = result = perfiosTransactionId = null;
        /* end declaration */

        // read requestdata
        if (requestdata.isEmpty()) {
            return "Request Body Empty.";
        }
        try {
            jsonObject = new JSONObject(requestdata);
            channel = jsonObject.getString("channel");
            if (jsonObject.getJSONObject("request_data").has("perfiosTransactionId")) {
                perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Action Not Found!", "99",
                        channel, "NOT_FOUND", requestdata, userName, module, "U");
            }
            if (channel == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Channel Not Found!", "99",
                        channel, action, requestdata, userName, module, "U");
            }
            if (perfiosTransactionId == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "perfiosTransactionId Not Found!",
                        "99", channel, action, requestdata, userName, module, "U");
            }
            userService.saveJsonLog(channel, "req", action, requestdata, userName, module);
        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        } catch (Exception e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        }
        // get transaction detail
        PerfiosReqResDto perfiosReqResDtlDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDtlDto.getUserid().isEmpty()) {
            return userService.getJsonError("-99", "Initial GST Upload Transaction Not Found",
                    "Initial Transaction Not found.", "Initial Transaction Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        } else if (perfiosReqResDtlDto.getStatus().equals("F")) {
            return userService.getJsonError("-99", "Request to Process failed transaction.",
                    "Transaction Status Found Failed, Unable to process.", "Not Found Any Document to be process.",
                    "99", channel, action, requestdata, userName, module, "U");
        }

        // get url dtl
        URLConfigDto urlConfigDto = userService.findURLDtlByID(24);
        if (urlConfigDto.getUrl() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }
        if (urlConfigDto.getUserid() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL userID Not Found.", "99", channel,
                    action, requestdata, userName, module, "U");
        }
        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid() + "/" + perfiosTransactionId;
        Utility.print("Request Sending URL generated:" + sendURL);
        try {
            URL url = new URL(sendURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("cache-control", "no-cache");
            result = utility.getURLResponse(conn);
            jsonObject1 = new JSONObject(result);
            System.out.println("Response Code:" + conn.getResponseCode());
            Utility.print("==RESPONSE==");
            utility.print(result);
            if (conn.getResponseCode() == 200) {
                /*
                 * {success: true,transactionId: PCPTSTTO09IM3PR3VUTDP}
                 */
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                return jsonObject2.toString();
            } else {
                /*
                 * { errorCode: TransactionIdNotFound, errorMessage: We could not find the
                 * Perfios Transaction Id referred to by the client, success: false}
                 */
                String message = null, code = null;
                if (jsonObject1.has("code")) {
                    code = jsonObject1.getString("code");
                }
                if (jsonObject1.has("message")) {
                    message = jsonObject1.getString("message");
                }
                return userService.getJsonError("-99", "Error in GST Statement Process.", message,
                        code + ": " + message, "99", channel, action, requestdata, userName, module, "U");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    //moved: fetchDocumentStatus() to scheduler
    //on date: 25/06/2021

    /*****************************************
     * IT UPLOAD
     ***************************************************************/
    // IT initiate upload document
    public String funcInitiateItUpload(String application,String module,String moduleCategory,String action,String event,String requestdata,String userName) {
        /* declaration */
        Utility utility = new Utility();
        JSONObject  jsonObject,jsonObject1,jsonObject2;
        jsonObject  = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        final String productType = "itstatement";
        String      channel,completeURL,result,finYear,entityType=null,reInitiate="N",
                loginUserID = userService.getLoginUserID(userName);
        long        refId=0, serialNo=0;
        channel = null; result = null; finYear = null;
        String      patterndate = "yyyyMMdd";
        String      patterntime = "HHmmssSSS";
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patterndate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patterntime);
        /*end declaration*/

        module = module+"/"+moduleCategory;
        action = action+"/"+event;

        /*read requestdata*/
        if(requestdata.isEmpty()){return "Request Body Empty.";}

        //check api allow//
        int para_cd = 9;
        SysParaMst sysParaMst = userService.getParaVal("9999","9999",para_cd);
        if(sysParaMst.getPara_value().equals("N")){
            return userService.getJsonError("99","Service Closed","Service has been disabled temporary,Please try after sometime.","Service Closed with code:"+para_cd,"99",channel,action,requestdata,userName,module,"U");
        }
        try {
            jsonObject  = new JSONObject(requestdata);
            if(jsonObject.has("channel")){
                channel     = jsonObject.getString("channel");
            }
            jsonObject  =jsonObject.getJSONObject("request_data");

            if(jsonObject.has("refID")){
                refId       = jsonObject.getLong("refID");
            }
            if(jsonObject.has("serialNo")){
                serialNo    = jsonObject.getLong("serialNo");
            }
            if(jsonObject.has("entityType")){
                entityType  = jsonObject.getString("entityType");
            }
            if (jsonObject.has("reInitiate")){
                reInitiate  = jsonObject.getString("reInitiate");
            }
            if (action==null){
                return userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
            }
            if (channel==null){
                return userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
            }
            userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
            if (refId<=0){
                return userService.getJsonError("-99","Request Error!","refID Not Found.","refID Not Found.","99",channel,action,requestdata,userName,module,"U");
            }
            if (serialNo<=0){
                return userService.getJsonError("-99","Request Error!","serialNo Not Found.","serialNo Not Found.","99",channel,action,requestdata,userName,module,"U");
            }
            if(entityType==null){
                return userService.getJsonError("-99","Request Error!","Invalid entityType.","Invalid entityType.","99",channel,action,requestdata,userName,module,"U");
            }else{
                serialNo = entityType=="L"? 1 : serialNo;
            }
            if(entityType.indexOf("L")<0 && entityType.indexOf("I")<0){
                return userService.getJsonError("-99","Request Error!","Invalid entityType.","Invalid entityType.","99",channel,action,requestdata,userName,module,"U");
            }
        }catch (JSONException e){
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }catch(Exception e){
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }

//        //##############CHECKING SCANNED_ITR DOC and finYear FIELD VALIDATION
//        //NOTE: Check Have user any one SCANNED_ITR(19) [from para_cd:4] or SCANNED_ITRV(20) [from para_cd:5] document
//        //if have then finYear is required as per perfios doc
//        SysParaMst sysParaMstRow1   = new SysParaMst();
//        sysParaMstRow1              = userService.getParaVal("9999","9999",4);
//        final long SCANNED_ITR_CD   = Long.parseLong(sysParaMstRow1.getPara_value());
//
//        sysParaMstRow1              = userService.getParaVal("9999","9999",5);
//        final long  SCANNED_ITRV    = Long.parseLong(sysParaMstRow1.getPara_value());
//        int i=1; long docId;
//        while (i<=2){
//            if(i==1){
//                docId = SCANNED_ITR_CD;
//            }else{
//                docId = SCANNED_ITRV;
//            }
//            i+=i;
//            List<DocUploadDtl> docList = userService.getDocListByDocId(refId,docId);
//            if(docList.size()>0){
//                Utility.print("FIN YEAR:"+finYear);
//                if(finYear==null || finYear.isEmpty()){
//                    return userService.getJsonError("-99","Financial Year Not Found","Financial Year Requierd for SCANNED_ITR","Customer SCANNED_ITR Document found,so finYear parameter is required.","99",channel,action,requestdata,userName,module,"U");
//                }
//            }
//        }
        //##############END finYear FIELD VALIDATION

        //check initiate request
        //if(!reInitiate.equals("Y")){
        result = funcCheckPerfiosInitiateRequest(refId,serialNo,entityType,null,"ITR_UPLOAD");
        if(!result.equals("success")){
            return  result;
        }
        //}
        /*get API Details*/
        URLConfigDto urlConfigDto = userService.findURLDtlByID(26);
        if(urlConfigDto==null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Detail NotFound.","99",channel,action,requestdata,userName,module,"U");
        }
        if(urlConfigDto.getKey()==null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL key Not Found.","99",channel,action,requestdata,userName,module,"U");
        }
        completeURL = userService.func_get_base_url("los/webhooks")+urlConfigDto.getSmtp_server();
        System.out.println("Complete URL:"+completeURL);

        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid(); //ratnaaFin
        String date = simpleDateFormatDate.format(new Date());
        String time = simpleDateFormatTime.format(new Date());
        UUID uuid = UUID.randomUUID();
        String userID = time+uuid.toString()+date;

        //set GST details
        PerfiosReqResDtl perfiosReqResDtl = new PerfiosReqResDtl();
        perfiosReqResDtl.setRef_tran_cd(refId);
        perfiosReqResDtl.setRef_sr_cd(serialNo);
        perfiosReqResDtl.setUserid(userID);
        perfiosReqResDtl.setStatus("I");
        perfiosReqResDtl.setRequest_type("ITR_UPLOAD");
        perfiosReqResDtl.setInitiated_req(requestdata);
        perfiosReqResDtl.setEntity_type(entityType);
        perfiosReqResDtl.setEntered_by(loginUserID);
        perfiosReqResDtl.setLast_entered_by(loginUserID);

        String create_Json = "{\"clientTransactionId\":\""+urlConfigDto.getKey()+"\",\"transactionCompleteUrl\":\""+completeURL+"\",\"type\":\""+productType+"\"}";
        System.out.println("Request JSON:\n"+create_Json);
        try{
            try {
                URL obj = new URL(sendURL);
                Boolean lbstatus = null;
                String transactionId = null, userRedirectURL;
                HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
                conn.setRequestMethod("POST");
                conn.addRequestProperty("content-Type", "application/json");
                System.out.println("request-json:"+create_Json);
                jsonObject1 = new JSONObject(create_Json);
                conn.addRequestProperty("signature", getSignature(create_Json));
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(create_Json.getBytes());
                os.flush();
                os.close();
                //get response body of api request
                result = Utility.getURLResponse(conn);
                perfiosReqResDtl.setUrl_res(result);
                jsonObject1 = new JSONObject(result);
                lbstatus = jsonObject1.getBoolean("success");
                if (conn.getResponseCode() == 200 && lbstatus) {
                    utility.print("success request");
                    transactionId 	= jsonObject1.getString("transactionId");
                    perfiosReqResDtl.setTransaction_id(transactionId);
                    perfiosReqResDtl.setReq_status("true");
                    userService.savePerfiosReqResDtl(perfiosReqResDtl);

                    jsonObject2.put("status", "0");
                    jsonObject2.put("response_data", jsonObject1);
                    userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                    return jsonObject2.toString();
                }else{
                    utility.print("Request failed..!");
                    String message, code;
                    /*{code: InvalidGSTIN,message: Invalid GSTIN. A valid GSTIN looks like this 29ABCDE1234F1ZW,success: false}*/
                    message = jsonObject1.getString("message");
                    code = jsonObject1.getString("code");
                    return userService.getJsonError("-99", "ITR initiate Request Failed.", message, code+":"+message, "99", channel, action, requestdata, userName, module, "U");
                }
            }catch(IOException e){
                Utility.print(e.getMessage());
                return userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            }catch (JSONException e){
                Utility.print(e.getMessage());
                return userService.getJsonError("-99","Error- JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            }
            catch(Exception e){
                Utility.print(e.getMessage());
                return userService.getJsonError("-99","Error- Exception",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            }
        }catch(Exception e){
            Utility.print(e.getMessage());
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
    }

    // IT upload document process
    public String funcItStatementUpload(String application, String module, String action, String event,
                                        String requestdata, String userName) {
        // declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject jsonObject, jsonObject1, jsonObject2;
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        final String loginUserId = userService.getLoginUserID(userName);
        String channel = null, result = null, perfiosTransactionId = null,entityType=null;

        long refId = 0, serialNo = 0;
        /* end declaration */

        // read requestdata
        if (requestdata.isEmpty()) {
            return "Request Body Empty.";
        }
        try {
            jsonObject = new JSONObject(requestdata);
            channel = jsonObject.getString("channel");
            if (jsonObject.getJSONObject("request_data").has("perfiosTransactionId")) {
                perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Action Not Found!", "99",
                        channel, "NOT_FOUND", requestdata, userName, module, "U");
            }
            if (channel == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Channel Not Found!", "99",
                        channel, action, requestdata, userName, module, "U");
            }
            if (perfiosTransactionId == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "perfiosTransactionId Not Found!",
                        "99", channel, action, requestdata, userName, module, "U");
            }
            userService.saveJsonLog(channel, "req", action, requestdata, userName, module);
        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        } catch (Exception e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        }
        // get transaction detail
        PerfiosReqResDto perfiosReqResDtlDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDtlDto.getTransaction_id() == null) {
            return userService.getJsonError("-99", "Initial ITR Transaction Not Found",
                    "Initial ITR Transaction Not found.", "Initial ITR Transaction Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        } else if (perfiosReqResDtlDto.getStatus().equals("F")) {
            return userService.getJsonError("-99", "Request to Process failed transaction.",
                    "Transaction Status Found Failed, Unable to process.", "Transaction Status is in failed status.",
                    "99", channel, action, requestdata, userName, module, "U");
        }

        // get url dtl
        URLConfigDto urlConfigDto = userService.findURLDtlByID(27);
        if (urlConfigDto.getUrl() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }
        if (urlConfigDto.getUserid() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL userID Not Found.", "99", channel,
                    action, requestdata, userName, module, "U");
        }
        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid() + "/" + perfiosTransactionId;
        Utility.print("Request Sending URL generated:" + sendURL);
        // get blob file data
        refId = perfiosReqResDtlDto.getRef_tran_cd();
        serialNo = perfiosReqResDtlDto.getRef_sr_cd();
        entityType = perfiosReqResDtlDto.getEntity_type();
        List<CrmDocumentMst> docMst = userService.getDocMstListByDocType("ITR");
        long docId = 0;
        int docMstSize = docMst.size();
        Utility.print("document mst row count for ITR:" + docMstSize); // expect:6 from CRM_DOCUMENT_MST

        int docMstCount = 0;
        if (docMstSize > 0) {
            // loop for Document Mst traverse for doc type = ITR
            String finYear = "", docType, resultOut, fileName, errorCode, errorMessage, req_status, doc_status, docUUID,
                    docPwd, docTitle, remarks = null;
            Boolean successBool = false;
            Blob blobdata = null;
            Boolean allFailed = true;
            CRMAppDto crmObjPII = userService.findAppByID(3);
            // phase:1
            JSONObject jsonDocStatus = new JSONObject();
            for (CrmDocumentMst crmDocumentMst : docMst) {
                docId = crmDocumentMst.getTran_cd();
                docTitle = crmDocumentMst.getDoc_title();
                docMstCount = docMstCount + 1;
                List<DocUploadDtl> docList = userService.getDocListByDocId(refId, serialNo,entityType, docId); // 1044-19
                if (docList.isEmpty() || docList.size() <= 0) {
                    Utility.print("No row in document_Dtl for:" + refId + "-" + docId);
                    continue;
                }
                Utility.print("Row found in document_Dtl for:" + refId + "-" + docId);
                Utility.print("DOC_TTL:" + crmDocumentMst.getDoc_title() + "\nDOC_DESC:" + docTitle);

                try {
                    int cnt = 0;
                    for (DocUploadDtl docUploadDtl : docList) {
                        cnt += 1;
                        docUUID = docUploadDtl.getDoc_uuid();
                        DocUploadBlobDtl docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                        if (docUploadBlobDtl == null) {
                            userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, "F", null, null, null,
                                    null, "F", "Not Found Data For UUID:" + docUUID);
                            return userService.getJsonError("-99", "Uploading Document Data Not found.", g_error_msg,
                                    "Document Data Not found for UUID:" + docUUID, "99", channel, action, requestdata,
                                    userName, module, "U");
                        }
                        if (docUploadBlobDtl.getDocContentType() == null) {
                            return userService.getJsonError("-99", "Uploading Document Content Type not found.",
                                    g_error_msg, "Document Content Type Not found for UUID:" + docUUID, "99", channel,
                                    action, requestdata, userName, module, "U");
                        }

                        // Getting Document meta data
                        blobdata = new SerialBlob(docUploadBlobDtl.getData());
                        docType = docUploadBlobDtl.getDocContentType();
                        docPwd = docUploadBlobDtl.getPassword();
                        finYear = docUploadBlobDtl.getParam1();
                        if (docPwd == null) {
                            docPwd = "";
                        } else {
                            docPwd = userService.func_get_data_val(crmObjPII.getA(), crmObjPII.getB(), docPwd);
                            Utility.print("document password:" + docPwd);
                            docPwd = Utility.perfiosDataEncryptPub(docPwd);
                        }

                        String docTypeExt = docType.substring(docType.indexOf('/') + 1);
                        if (blobdata.getBinaryStream() != null) {
                            Utility.print("Document upload process started..!");
                            Utility.print("Document Name:" + docUploadBlobDtl.getDoc_name());

                            fileName = "file" + docId + "" + cnt; // expect: file19-1<randomStamps>
                            File sendFile = utility.blobToFileConverter(blobdata, fileName, "." + docTypeExt);
                            fileName = sendFile.getName();
                            // attaching file to sendURL
                            try {
                                Utility.print("File Generated to be Upload:" + fileName);
                                Utility.print("FinYear:" + finYear);

                                Utility.print("password:" + docPwd);
                                Utility.print("uploadFileType:" + docTitle);

                                utility.initiateMultipart(sendURL, "UTF-8");
                                utility.addFormField("password", docPwd);
                                utility.addFormField("uploadFileType", docTitle);
                                utility.addFormField("financialYear", finYear);
                                utility.addFilePart("file", sendFile);
                                Utility.print("uploadFileType:" + docTitle);

                                resultOut = utility.finish();
                                utility.deleteFile();
                                Utility.print(resultOut);
                                jsonObject1 = new JSONObject(resultOut);
                                successBool = jsonObject1.getBoolean("success");
                                if (utility.getHttpConn().getResponseCode() == 200 && successBool) {
                                    /* { success: true,transactionId: PCPTSTTO09IM3PR3VUTDP} */
                                    allFailed = false;
                                    Utility.print("SUCCESS RESPONSE");
                                    req_status = "true";
                                    doc_status = "U"; // uploaded 14-9(4), 14-11(1)
                                    /*
                                     * jsonObject2 = new JSONObject(); jsonObject2.put("status", "0");
                                     * jsonObject2.put("response_data", jsonObject1);
                                     * userService.saveJsonLog(channel, "res", action, jsonObject2.toString(),
                                     * userName, null); result = jsonObject2.toString();
                                     */
                                } else {
                                    /*
                                     * { errorCode: InsecureFileType, errorMessage: Client tried to upload an
                                     * insecure file type (executable, DLL, JAR, etc.), success: false }
                                     */
                                    Utility.print("ERROR RESPONSE");
                                    req_status = "false";
                                    doc_status = "F"; // upload failed
                                    errorMessage = jsonObject1.getString("message");
                                    errorCode = jsonObject1.getString("code");
                                    remarks = errorCode + ":" + errorMessage;
                                    /*
                                     * result =
                                     * userService.getJsonError("-99","GST Statements Upload Failed.",errorMessage,
                                     * errorMessage,"99",channel,action,requestdata,userName,null,"U");
                                     */
                                }
                                jsonDocStatus.put(fileName, jsonObject1);
                            } catch (IOException e) {
                                return userService.getJsonError("-99", "Error-IOException", g_error_msg, e.getMessage(),
                                        "99", channel, action, requestdata, userName, module, "E");
                            } catch (JSONException e) {
                                return userService.getJsonError("-99", "JSON Error", g_error_msg, e.getMessage(), "99",
                                        channel, action, requestdata, userName, module, "E");
                            }
                            // save each file status
                            connection = jdbcTemplate.getDataSource().getConnection();
                            connection.setAutoCommit(false);
                            cs = connection.prepareCall(
                                    "{ call PACK_DOCUMENT.proc_insert_gstupload_document(?,?,?,?,?,?,?,?,?,?,?,?) }");
                            cs.setString(1, perfiosReqResDtlDto.getUserid());
                            cs.setString(2, perfiosTransactionId);
                            cs.setString(3, String.valueOf(docId));
                            cs.setString(4, docUUID);
                            cs.setString(5, req_status);
                            cs.setString(6, docType);
                            cs.setString(7, fileName);
                            cs.setString(8, doc_status);
                            cs.setString(9, resultOut);
                            cs.setString(10, remarks);
                            cs.setString(11, loginUserId);


                            cs.registerOutParameter(12, 2005);
                            cs.execute();
                            // either 0 or 1: fail or success
                            final String returnData = cs.getString(12);
                            if (returnData.equals("1")) {
                                Utility.print("File Response saved successfully.");
                            } else {
                                Utility.print("File Response failed to save.");
                            }
                            connection.close();
                            cs.close();
                        } else {
                            return userService.getJsonError("-99", "Data Upload Error", g_error_msg,
                                    "Fetching Data found null or not failed to get inputstream", "99", channel, action,
                                    userName, userName, module, "U");
                        }
                    }
                } catch (SQLException e) {
                    return userService.getJsonError("-99", "Error-SQLException", g_error_msg, e.getMessage(), "99",
                            channel, action, userName, userName, module, "E");
                } catch (FileNotFoundException e) {
                    return userService.getJsonError("-99", "Error-FileNotFoundException", g_error_msg, e.getMessage(),
                            "99", channel, action, userName, userName, module, "E");
                }
            } // end loop (CrmDocumentMst)
            if (allFailed) {
                /* NOTE: Update transaction status F because all file failed to upload */
                userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, "F", null, null, null, null, "F",
                        "0 DOCUMENT(S) UPLOADED");
            }
            try {
                jsonObject2 = new JSONObject();
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonDocStatus);
                userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                result = jsonObject2.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return userService.getJsonError("-99", "Error- JSONException", g_error_msg, e.getMessage(), "99",
                        channel, action, userName, userName, module, "E");
            }
            return result;
        } else {
            return userService.getJsonError("-99", "Master Document Type Not Found.(ITR)", g_error_msg,
                    "ITR Document Type not in Master Table.", "99", channel, action, requestdata, userName, module,
                    "U");
        }

    }

    // IT upload document status
    public String funcItStatementUploadStatus(String application,String module,String moduleCategory,String action,String event,String requestdata,String userName) {
        //declaration//
        String message = null, perfiosTransactionId = null, channel = null;
        JSONObject jsonObject, jsonObject1, jsonObject2;
        //end declaration//

        module = module+"/"+moduleCategory;
        action = action+"/"+event;

        //read requestdata//
        try {
            jsonObject = new JSONObject(requestdata);
            perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            channel = jsonObject.getString("channel");
        } catch (JSONException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Json Error!", g_error_msg, e.getMessage(), "99", channel, action, requestdata, userName, module, "E");
        }

        //validations
        if (perfiosTransactionId.isEmpty()) {
            return userService.getJsonError("-99", "Error in ITR Statement Upload Status.", "perfiosTransactionId Not Found.", "perfiosTransactionId Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if (channel.isEmpty()) {
            return userService.getJsonError("-99", "Error in ITR Statement Upload Status.", "channel Not Found.", "channel Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        userService.saveJsonLog(channel, "req", action, requestdata, userName, module);

        //process
        PerfiosReqResDto perfiosReqResDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDto.getTransaction_id()==null){
            return userService.getJsonError("-99", "Transaction Not Found at our side", g_error_msg, "Transaction Detail Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if (perfiosReqResDto.getStatus().equals("S")) {
            message = "{\"status\":\"0\",\"response_data\":{\"message\":\"COMPLETED\",\"status\": true}}";
            userService.saveJsonLog(channel, "res", action, message, userName, module);
            return message;
        }else if(perfiosReqResDto.getStatus().equals("I")){
            message = "{\"status\":\"0\",\"response_data\":{\"message\":\"INITIATED\",\"status\": true}}";
            userService.saveJsonLog(channel, "res", action, message, userName, module);
            return message;
        }else if(perfiosReqResDto.getStatus().equals("F")){
            return userService.getJsonError("-99","Transaction Status is Failed.","Transaction Status Found Failed, Unable to process Request.","Transaction Status is Failed. Due to:"+perfiosReqResDto.getRemarks(),"99",channel,action,requestdata,userName,module,"U");
        }

        URLConfigDto urlConfigDto = userService.findURLDtlByID(28);
        if(urlConfigDto.getUserid()==null){
            return userService.getJsonError("-99", "URL Configuration Not Found.", g_error_msg, "URL detail Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }

        try {
            jsonObject2 = new JSONObject();
            Utility utility = new Utility();
            String ls_url,result,ls_response;
            ls_url = urlConfigDto.getUrl() + urlConfigDto.getUserid() + "/" + perfiosTransactionId;
            Utility.print("ITR Statement Upload generated URL:" + ls_url);
            URL url = new URL(ls_url);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("cache-control", "no-cache");
            result = utility.getURLResponse(conn);
            jsonObject1= new JSONObject(result);
            System.out.println("Response Code:"+conn.getResponseCode());
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
            }
			}*/
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
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
                String status    = jsonObject1.getString("transactionStatus");
                if(status.equals("COMPLETED")){
                    gstInfoRetrieve(perfiosTransactionId,30,null);
                }else{
                    String errorCode = jsonObject1.getString("errorCode");
                    message          = jsonObject1.getString("message");
                    userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",status+":"+errorCode+":"+message);
                }
                //update gst document status
                if(!jsonObject1.isNull("statementsStatus")){
                    int rowUpdate = updateGstDocumentStatus(perfiosTransactionId,"GST_ITR_UPLOAD_STATUS",jsonObject1.toString());
                    Utility.print("ITR Document Update Count(s):"+rowUpdate);
                }else{
                    Utility.print("statementsStatus is null");
                }
                return jsonObject2.toString();
            }else{
                return userService.getJsonError("-99","ITR Statement Upload Get Status Failed",g_error_msg,result,"99",channel,action,requestdata,userName,module,"U");
            }
        }catch (Exception e) {
            e.getMessage();
            return userService.getJsonError("-99", "Error- Exception", g_error_msg, e.getMessage(), "-99", channel, action, requestdata, userName, module, "E");
        }
    }

    // itr upload document process
    public String funcItStatementProcess(String application, String module, String action, String event,
                                         String requestdata, String userName) {
        // declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject jsonObject, jsonObject1, jsonObject2;
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String channel, result, perfiosTransactionId;
        channel = null;
        result = null;
        perfiosTransactionId = null;
        /* end declaration */

        // read requestdata
        if (requestdata.isEmpty()) {
            return "Request Body Empty.";
        }
        try {
            jsonObject = new JSONObject(requestdata);
            channel = jsonObject.getString("channel");
            if (jsonObject.getJSONObject("request_data").has("perfiosTransactionId")) {
                perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Action Not Found!", "99",
                        channel, "NOT_FOUND", requestdata, userName, module, "U");
            }
            if (channel == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Channel Not Found!", "99",
                        channel, action, requestdata, userName, module, "U");
            }
            if (perfiosTransactionId == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "perfiosTransactionId Not Found!",
                        "99", channel, action, requestdata, userName, module, "U");
            }
            userService.saveJsonLog(channel, "req", action, requestdata, userName, module);
        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        } catch (Exception e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        }
        // get transaction detail
        PerfiosReqResDto perfiosReqResDtlDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDtlDto.getUserid().isEmpty()) {
            return userService.getJsonError("-99", "Initial ITR Upload Transaction Not Found",
                    "Initial Transaction Not found.", "Initial Transaction Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        } else if (perfiosReqResDtlDto.getStatus().equals("F")) {
            return userService.getJsonError("-99", "Request to Process failed transaction.",
                    "Transaction Status Found Failed, Unable to process.", "Not Found Any Document to be process.",
                    "99", channel, action, requestdata, userName, module, "U");
        }

        // get url dtl
        URLConfigDto urlConfigDto = userService.findURLDtlByID(29);
        if (urlConfigDto.getUrl() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }
        if (urlConfigDto.getUserid() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL userID Not Found.", "99", channel,
                    action, requestdata, userName, module, "U");
        }
        String sendURL = urlConfigDto.getUrl() + "" + urlConfigDto.getUserid() + "/" + perfiosTransactionId;
        Utility.print("Request Sending URL generated:" + sendURL);
        try {
            URL url = new URL(sendURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("cache-control", "no-cache");
            result = utility.getURLResponse(conn);
            jsonObject1 = new JSONObject(result);
            System.out.println("Response Code:" + conn.getResponseCode());
            Utility.print("==RESPONSE==");
            utility.print(result);
            if (conn.getResponseCode() == 200) {
                /*
                 * {success: true,transactionId: PCPTSTTO09IM3PR3VUTDP}
                 */
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                return jsonObject2.toString();
            } else {
                /*
                 * { code: TransactionIdNotFound, message: We could not find the Perfios
                 * Transaction Id referred to by the client, success: false}
                 */
                String message = null, code = null;
                if (jsonObject1.has("code")) {
                    code = jsonObject1.getString("code");
                }
                if (jsonObject1.has("message")) {
                    message = jsonObject1.getString("message");
                }
                return userService.getJsonError("-99", "Error in GST Statement Process.", message,
                        code + ": " + message, "99", channel, action, requestdata, userName, module, "U");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Error- IOException", g_error_msg, e.getMessage(), "99", channel,
                    action, requestdata, userName, module, "E");
        } catch (Exception e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Error- IOException", g_error_msg, e.getMessage(), "99", channel,
                    action, requestdata, userName, module, "E");
        }
    }

    /*****************************************
     * BANK STATEMENT
     ***************************************************************/
    // initiate bank statement
    public String funcInitiateStatementUpload(String application,String module,String moduleCategory,String action,String event,String requestdata,String userName) {
        /* declaration */
        final String loginUserID = userService.getLoginUserID(userName);
        Utility utility = new Utility();
        JSONObject  jsonObject  = new JSONObject();
        JSONObject  jsonObject1 = new JSONObject();
        JSONObject  jsonObject2 = new JSONObject();
        JSONArray   jsonArrayVariable = new JSONArray();
        String       channel,completeURL,result,yearMonthFrom,yearMonthTo,acceptancePolicy=null,facility="",entityType=null,reInitiate="N";
        String       loanAmount=null,loanType=null,loanDuration=null,sanctionLimitFixed=null,sanctionLimitFixedAmount=null;
        String       dpXMLData=null, slXMLData=null,variableArrayKey=null,additionalParam=null;
        List<String> variableAmounts = new ArrayList<String>();
        long        refId=0,serialNo =0,bankLineID=0,bankID=0;
        long    allowedRange=0,monthRange  = 0, rangeFrom=0,rangeTo=0;

        channel = null; result = null; yearMonthFrom = null; yearMonthTo = null;
        String      patterndate = "yyyyMMdd";
        String      patterntime = "HHmmssSSS";
        String yearMonthFromFormatted=null,yearMonthToFormatted=null;
        String      sendDatePattern = "yyyy-MM";
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patterndate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patterntime);
        SimpleDateFormat sendDateFormat = new SimpleDateFormat(sendDatePattern);

        /*end declaration*/
        Utility.print("stage:1");

        module = module+"/"+moduleCategory;
        action = action+"/"+event;
        Utility.print("stage:2");
        /*read requestdata*/
        /*SAMPE REQUEST: {"request_data": {"refID": 1044,"yearMonthFrom": "YYYY-MM","yearMonthTo": "YYYY-MM",}}*/
        if(requestdata.isEmpty()){return "Request Body Empty.";}
        int para_cd = 10;
        SysParaMst sysParaMst = userService.getParaVal("9999","9999",para_cd);
        if(sysParaMst.getPara_value().equals("N")){
            return userService.getJsonError("99","Service Closed","Service has been disabled temporary,Please try after sometime.","Service Closed with code:"+para_cd,"99",channel,action,requestdata,userName,module,"U");
        }
        Utility.print("stage:3");
        try {
            jsonObject  = new JSONObject(requestdata);
            channel     = jsonObject.getString("channel");
            //get request_data jsonObject
            jsonObject = jsonObject.getJSONObject("request_data");

            if(jsonObject.has("refID")){
                refId       = jsonObject.getLong("refID");
            }
            if(jsonObject.has("serialNo")){
                serialNo    = jsonObject.getLong("serialNo");
            }
            if(jsonObject.has("entityType")){
                entityType    = jsonObject.getString("entityType");
            }
            if(jsonObject.has("reInitiate")){
                reInitiate    = jsonObject.getString("reInitiate");
            }
            if(jsonObject.has("fromDate")){
                yearMonthFrom = jsonObject.getString("fromDate");
            }
            if(jsonObject.has("toDate")){
                yearMonthTo = jsonObject.getString("toDate");
            }
            if(jsonObject.has("bankLineID")){
                bankLineID = jsonObject.getLong("bankLineID");
            }

            if(jsonObject.has("acceptancePolicy")){
                acceptancePolicy = jsonObject.getString("acceptancePolicy");
            }
            if(jsonObject.has("bankFacility")){
                facility = jsonObject.getString("bankFacility");
            }
            //newly added//
            if(jsonObject.has("loanAmount")){
                loanAmount = jsonObject.getString("loanAmount");

            }
            if(jsonObject.has("loanType")){
                loanType = jsonObject.getString("loanType");
            }
            if(jsonObject.has("loanDuration")){
                loanDuration = String.valueOf(jsonObject.getLong("loanDuration"));
            }
            if(jsonObject.has("sanctionLimitType")){
                sanctionLimitFixed = jsonObject.getString("sanctionLimitType");
            }
            if(jsonObject.has("sanctionLimitFixedAmount")){
                sanctionLimitFixedAmount = jsonObject.getString("sanctionLimitFixedAmount");
            }
            Utility.print("stage:4");
            //-----------------------------------------
            /**construct drawingPowerVariableAmounts**/
            //-----------------------------------------
            if(jsonObject.has("drawingPowerVariableAmount")){
                jsonArrayVariable = jsonObject.getJSONArray("drawingPowerVariableAmount");
                if(jsonArrayVariable.length()>0){
                    /*{"drawingPowerVariableAmounts": {
                            "variableAmount": [{"amount": 500000}, {"amount": 500000}, {"amount": 500000} ]
                            }
                      }* */
                    /**extract variable amounts**/
                    for(int j=0; j<jsonArrayVariable.length();j++){
                        jsonObject1 = jsonArrayVariable.getJSONObject(j);
                        variableAmounts.add(jsonObject1.getString("drawingPowerAmount"));
                    }
                    jsonArrayVariable = new JSONArray();
                    for(String amount: variableAmounts){
                        jsonObject1 = new JSONObject();
                        jsonObject1.put("amount",amount);
                        jsonArrayVariable.put(jsonObject1);
                    }
                    jsonObject1 = new JSONObject();
                    jsonObject1.put("variableAmount",jsonArrayVariable);
                    jsonObject2.put("drawingPowerVariableAmounts",jsonObject1);
                    dpXMLData = XML.toString(jsonObject2);
                }
            }
            //-----------------------------------------
            /**construct sanctionLimitVariableAmounts**/
            //-----------------------------------------
            Utility.print("stage:5");
            if(jsonObject.has("sanctionLimitVariableAmount")) {
                Utility.print("stage:6");
                variableAmounts = new ArrayList<String>();
                jsonArrayVariable = new JSONArray();
                jsonObject1 = new JSONObject();
                jsonObject2 = new JSONObject();
                jsonArrayVariable = jsonObject.getJSONArray("sanctionLimitVariableAmount");
                Utility.print("Length of sanction variable:"+jsonArrayVariable.length());
                if(jsonArrayVariable.length()>0){
                    /*{"drawingPowerVariableAmounts": {
                            "variableAmount": [{"amount": 500000}, {"amount": 500000}, {"amount": 500000} ]
                            }}*/
                    /**extract variable amounts**/
                    for(int j=0; j<jsonArrayVariable.length();j++){
                        jsonObject1 = jsonArrayVariable.getJSONObject(j);
                        variableAmounts.add(jsonObject1.getString("sanctionAmount"));
                    }
                    jsonArrayVariable = new JSONArray();
                    for(String amount: variableAmounts){
                        jsonObject1 = new JSONObject();
                        jsonObject1.put("amount",amount);
                        jsonArrayVariable.put(jsonObject1);
                    }
                    jsonObject1 = new JSONObject();
                    jsonObject1.put("variableAmount",jsonArrayVariable);
                    jsonObject2.put("sanctionLimitVariableAmounts",jsonObject1);
                    slXMLData = XML.toString(jsonObject2);
                }
            }
            Utility.print("stage:6");
            jsonObject1 = new JSONObject();
            jsonObject2 = new JSONObject();
            if(action==null){
                return userService.getJsonError("-99","Request Error!",g_error_msg,"Action Not Found!","99",channel,"NOT_FOUND",requestdata,userName,module,"U");
            }
            if(channel==null){
                return userService.getJsonError("-99","Request Error!",g_error_msg,"Channel Not Found!","99",channel,action,requestdata,userName,module,"U");
            }
            userService.saveJsonLog(channel,"req",action,requestdata,userName,module);
            //validation for parameters
            if (refId<=0){
                return userService.getJsonError("-99","Request Error!","refID Not Found.","refID Not Found.","99",channel,action,requestdata,userName,module,"U");
            }
            if (serialNo<=0){
                return userService.getJsonError("-99","Request Error!","serialNo Not Found.","serialNo Not Found.","99",channel,action,requestdata,userName,module,"U");
            }
            if (bankLineID<=0){
                return userService.getJsonError("-99","Request Error!","bankLineID Not Found.","bankLineID Not Found.","99",channel,action,requestdata,userName,module,"U");
            }
            if(entityType==null || entityType.isEmpty()){
                return userService.getJsonError("-99","Request Error!","Invalid entityType.","Invalid entityType.","99",channel,action,requestdata,userName,module,"U");
            }
            if(entityType.indexOf("L")<0 && entityType.indexOf("I")<0){
                return userService.getJsonError("-99","Request Error!","Invalid entityType.","Invalid entityType.","99",channel,action,requestdata,userName,module,"U");
            }
            if(loanAmount==null || loanAmount.isEmpty() || Long.parseLong(loanAmount)<=0){
                return userService.getJsonError("-99","Request Error!","Invalid loanAmount.","Invalid loanAmount.","99",channel,action,requestdata,userName,module,"U");
            }
            if(loanType==null || loanType.isEmpty()){
                return userService.getJsonError("-99","Request Error!","Invalid loanType.","Invalid loanType.","99",channel,action,requestdata,userName,module,"U");
            }
            if(loanDuration==null || loanDuration.isEmpty()){
                return userService.getJsonError("-99","Request Error!","Invalid loanDuration.","Invalid loanDuration.","99",channel,action,requestdata,userName,module,"U");
            }
            if(facility==null || facility.isEmpty()){
                return userService.getJsonError("-99","Request Error!","facility Not Found.","facility Not Found.","99",channel,action,requestdata,userName,module,"U");
            }else {
                facility = facility.equalsIgnoreCase("cash credit") ? "CC" : (facility.equalsIgnoreCase("overdraft")?"OD":"None");
            }
            if(!facility.equalsIgnoreCase("none")) {
                if(sanctionLimitFixed==null || sanctionLimitFixed.isEmpty()){
                    return userService.getJsonError("-99","Request Error!","sanctionLimitType Not Found.","sanctionLimitType Not Found.","99",channel,action,requestdata,userName,module,"U");
                }
            }
            Utility.print("stage:7");

            /**Get parameter flag for date validation process allow or not**/
            sysParaMst = userService.getParaVal("9999","9999",6);
            String dateValidate = sysParaMst.getPara_value();
            long uptoYear = 0;
            Utility.print("stage:8");
            if(sysParaMst!=null){
                sysParaMst = userService.getParaVal("9999","9999",13);
                Utility.print("stage:9");
                if(dateValidate.equals("Y")){
                    Utility.print("stage:10");
                    if(yearMonthFrom==null || yearMonthTo==null){
                        return userService.getJsonError("-99","Request Error!","Required Parameter Not Found: YearMonthFrom or YearMonthTo.","Required Parameter Not Found: YearMonthFrom or YearMonthTo.","99",channel,action,requestdata,userName,module,"U");
                    }else{
                        /**old**/
                        //yearMonthFromFormatted = yearMonthFrom.substring(0,7);
                        //yearMonthToFormatted   = yearMonthTo.substring(0,7);
                        /**old**/
                        /*dateFormat changed from front*/
                        yearMonthFromFormatted = userService.getDateFormattedString(yearMonthFrom,sendDatePattern);
                        yearMonthToFormatted   = userService.getDateFormattedString(yearMonthTo,sendDatePattern);
                        Utility.print("yearMonthFrom:"+yearMonthFrom);
                        Utility.print("yearMonthTo:"+yearMonthTo);
                        Utility.print("yearMonthFromFormatted:"+yearMonthFromFormatted);
                        Utility.print("yearMonthToFormatted:"+yearMonthToFormatted);
                    }
                    Utility.print("stage:11");
                    if(!yearMonthFromFormatted.matches("([0-9]{4})-[0-9]{2}") || !yearMonthToFormatted.matches("([0-9]{4})-[0-9]{2}")){
                        return userService.getJsonError("-99","Request Error!","Required Parameter(YearMonthFrom or YearMonthTo) found invalid Date Format, Date format should be 'YYYY-MM'.","YearMonthFrom or YearMonthTo Date Format Should be 'YYYY-MM'.","99",channel,action,requestdata,userName,module,"U");
                    }
                    Utility.print("stage:12");
                    if(sysParaMst!=null){
                        allowedRange = Long.parseLong(sysParaMst.getPara_value());
                        if(allowedRange>0){
                            uptoYear = allowedRange/12;
                            rangeFrom  = Long.parseLong(yearMonthFromFormatted.substring(0,4));
                            rangeTo    = Long.parseLong(yearMonthToFormatted.substring(0,4));
                            monthRange = rangeTo - rangeFrom;
                            if(monthRange>uptoYear){
                                return userService.getJsonError("-99","Request Error!","Out of date Range, valid range is "+allowedRange+" month(s).","Expected month range <="+allowedRange+", actual got "+monthRange,"99",channel,action,requestdata,userName,module,"U");
                            }else{
                                rangeFrom = Long.parseLong(yearMonthFromFormatted.substring(5));
                                rangeTo   = Long.parseLong(yearMonthToFormatted.substring(5));
                                Utility.print("monthRange:"+monthRange);
                                Utility.print("uptoYear:"+uptoYear);
                                if(monthRange == uptoYear){
                                    Utility.print("monthRange:"+monthRange);
                                    Utility.print("monthRange:"+monthRange);
                                }else{
                                    monthRange = rangeTo - rangeFrom + 1;
                                    //"Out of date Range, valid range is "+allowedRange+" month(s).","Expected month range <="+allowedRange+", actual got "+monthRange
                                }
                                Utility.print("monthRange:"+monthRange);
                                Utility.print("uptoYear:"+uptoYear);
                                if(monthRange>allowedRange){
                                    return userService.getJsonError("-99","Request Error!","Out of date Range, valid range is "+allowedRange+" month(s).","Expected month range <="+allowedRange+", actual got "+monthRange,"99",channel,action,requestdata,userName,module,"U");
                                }
                            }
                        }
                    }
                    Utility.print("stage:13");
                }

            }
            if (acceptancePolicy==null || acceptancePolicy.isEmpty()){
                return userService.getJsonError("-99","Request Error!","acceptancePolicy Not Found.","acceptancePolicy Not Found.","99",channel,action,requestdata,userName,module,"U");
            }
            Utility.print("stage:14");
        }catch (JSONException e){
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }catch(Exception e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
        Utility.print("stage:15");
        //check initiate request
//        if(!reInitiate.equals("Y")){
        result = funcCheckPerfiosInitiateRequest(refId,serialNo,entityType,String.valueOf(bankLineID),"STMT_UPLOAD");
        Utility.print("stage:16");
        if(!result.equals("success")){
            return  result;
        }
        Utility.print("stage:17");
//        }
        Utility.print("result:"+result);
        /*get API Details from db*/
        URLConfigDto urlConfigDto = userService.findURLDtlByID(31);
        if(urlConfigDto==null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Detail NotFound.","99",channel,action,requestdata,userName,module,"U");
        }
        if(urlConfigDto.getKey()==null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL key Not Found.","99",channel,action,requestdata,userName,module,"U");
        }
        completeURL = userService.func_get_base_url("los/webhooks")+urlConfigDto.getSmtp_server();
        System.out.println("Complete URL:"+completeURL);

        String sendURL = urlConfigDto.getUrl(); //+ "" + urlConfigDto.getUserid();
        String date = simpleDateFormatDate.format(new Date());
        String time = simpleDateFormatTime.format(new Date());
        UUID uuid = UUID.randomUUID();
        String userID = time+uuid.toString()+date;

        //set GST details
        PerfiosReqResDtl perfiosReqResDtl = new PerfiosReqResDtl();
        perfiosReqResDtl.setRef_tran_cd(refId);
        perfiosReqResDtl.setUserid(userID);
        perfiosReqResDtl.setStatus("I");
        perfiosReqResDtl.setRequest_type("STMT_UPLOAD");
        perfiosReqResDtl.setInitiated_req(requestdata);
        perfiosReqResDtl.setEntity_type(entityType);
        perfiosReqResDtl.setRef_sr_cd(serialNo);
        perfiosReqResDtl.setBank_line_id(bankLineID);
        perfiosReqResDtl.setEntered_by(loginUserID);
        perfiosReqResDtl.setLast_entered_by(loginUserID);

        /**SAMPLE PAYLOAD**/
        /*<payload>
            <apiVersion>2.1</apiVersion>
            <vendorId>ratnaaFin</vendorId>
            <txnId>ratnaaFin_uniqueTxnId</txnId>
            <institutionId>2</institutionId>
            <loanAmount>100000</loanAmount>
            <loanDuration>24</loanDuration>
            <loanType>Home</loanType>
            <transactionCompleteCallbackUrl>https://example.com/callback</transactionCompleteCallbackUrl>
            <yearMonthFrom>2019-02</yearMonthFrom>
            <yearMonthTo>2019-08</yearMonthTo>
            <acceptancePolicy>atLeastOneTransactionPerMonthInRange</acceptancePolicy>
            <uploadingScannedStatements>true</uploadingScannedStatements>
        </payload>*/

        /** 1)   first get data from db by refID**/
        //> institutionId, loanAmount ,loanDuration ,loanType,uploadingScannedStatements
        HashMap  outParam= new HashMap(), inParam = new HashMap();
        String error_msg;

        inParam.put("refID",refId);
        inParam.put("serialNo",serialNo);
        inParam.put("entityType",entityType);
        inParam.put("bankLineID",bankLineID);

        outParam    =   userService.callingDBObject("procedure","pack_document.proc_perfios_bank_initial_param",inParam);
        if(outParam.containsKey("error")){
            error_msg = (String) outParam.get("error");
            return userService.getJsonError("-99","Error while Executing callingDBObject",g_error_msg,error_msg,"99",channel,action,requestdata,userName,module,"E");
        }

        /** 2) get outParam values **/
        String scannedStmtFlag;
//        loanAmount      = (String)outParam.get("loanAmount");
//        loanDuration    = (String)outParam.get("loanDuration");
//        loanType        = (String)outParam.get("loanType");
        scannedStmtFlag = (String)outParam.get("scannedStmtFlag");
        bankID          = Long.parseLong((String)outParam.get("bankID"));
        if(bankID<=0){
            return userService.getJsonError("-99","Request Error!","bankID Not Found.","bankID Not Found.","99",channel,action,requestdata,userName,module,"U");
        }
        if(scannedStmtFlag.equalsIgnoreCase("Y")){
            scannedStmtFlag = "true";
        }else{
            scannedStmtFlag = "false";
        }
        String monthParameter,facilityParameter;
        sanctionLimitFixed = sanctionLimitFixed==null?"":(sanctionLimitFixed.equalsIgnoreCase("fixed")?"true":"false");
        sanctionLimitFixedAmount=sanctionLimitFixedAmount==null?"":sanctionLimitFixedAmount;
        dpXMLData = dpXMLData==null?"":dpXMLData;
        slXMLData = slXMLData==null?"":slXMLData;
        facility  = facility==null?"":facility;
        if(!facility.equalsIgnoreCase("none")){
            facilityParameter = "<facility>"+facility+"</facility>\n<sanctionLimitFixed>"+sanctionLimitFixed+"</sanctionLimitFixed>\n";
            if(!sanctionLimitFixedAmount.isEmpty()){
                facilityParameter = facilityParameter+"<sanctionLimitFixedAmount>"+sanctionLimitFixedAmount+"</sanctionLimitFixedAmount>\n";
            }
            additionalParam = facilityParameter+dpXMLData+slXMLData;
        }else{
            additionalParam   = "";
        }
        /** 3) Request payload **/
        if(yearMonthFrom.isEmpty() || yearMonthTo.isEmpty()){
            monthParameter = "";
        }else{
            monthParameter = "<yearMonthFrom>"+yearMonthFromFormatted+"</yearMonthFrom>\n" +
                    "<yearMonthTo>"+yearMonthToFormatted+"</yearMonthTo>\n";
        }

        urlConfigDto.getKey(); //
        String payload = "<payload>\n"+
                "<apiVersion>2.1</apiVersion>\n" +
                "<vendorId>"+urlConfigDto.getUserid()+"</vendorId>\n" +
                "<txnId>"+perfiosReqResDtl.getUserid()+"</txnId>\n" +
                "<institutionId>"+bankID+"</institutionId>\n" +
                "<loanAmount>"+loanAmount+"</loanAmount>\n" +
                "<loanDuration>"+loanDuration+"</loanDuration>\n" +
                "<loanType>"+loanType+"</loanType>\n" +
                "<transactionCompleteCallbackUrl>"+completeURL+"</transactionCompleteCallbackUrl>\n" +monthParameter+
                "<acceptancePolicy>"+acceptancePolicy+"</acceptancePolicy>\n" +
                "<uploadingScannedStatements>"+scannedStmtFlag+"</uploadingScannedStatements>\n" +additionalParam+
                "</payload>";
        System.out.println("==Generated Payload==\n"+payload);

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
            Utility.print("2");
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
            Utility.print("3");
            Utility.print("Response code:"+conn.getResponseCode());
            result = Utility.getURLResponse(conn);
            jsonObject =  new JSONObject(Utility.xmlToJson(result));
            //            result = eResponse;
            perfiosReqResDtl.setUrl_res(result);

            String sqlstmt,xmlResponseCode=null,xmlResponseMessage=null,xmlPerfiosTranId=null;
            if(jsonObject.has("Success")){
                xmlPerfiosTranId    = jsonObject.getJSONObject("Success").getString("perfiosTransactionId");
                Utility.print("test- prefiosID:"+xmlPerfiosTranId);
            }else{
                xmlResponseCode     = jsonObject.getJSONObject("Error").getString("code");
                xmlResponseMessage  = jsonObject.getJSONObject("Error").getString("message");
            }

            Utility.print("TranId :"+xmlPerfiosTranId);
            Utility.print("ErrCode:"+xmlResponseCode);
            Utility.print("ErrMsg :"+xmlResponseMessage);
            /**End read xml response**/


            Boolean status = (xmlPerfiosTranId == null) ? false : true;
            Utility.print("9");
            if (status) {
                Utility.print("10");
                utility.print("success request");
                perfiosReqResDtl.setTransaction_id(xmlPerfiosTranId);
                perfiosReqResDtl.setReq_status("true");
                userService.savePerfiosReqResDtl(perfiosReqResDtl);
                jsonObject1.put("success","true");
                jsonObject1.put("transactionId",xmlPerfiosTranId);
                jsonObject2.put("" +
                        "status", "0");
                jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                outParam = null;
//                inParam.put("refID",refId);
//                inParam.put("serialNo",serialNo);
//                inParam.put("entityType",entityType);
//                inParam.put("bankID",bankID);
                outParam =  userService.callingDBObject("procedure","pack_document.proc_active_api_flag_bankid_wise",inParam);
                Utility.print("checking result");
                if(outParam.containsKey("result")){
                    Utility.print((String)outParam.get("result"));
                }
                return jsonObject2.toString();
            }else{
                Utility.print("10");
                utility.print("Request failed..!");
                /*<Error>
                    <code>BadParameter</code>
                    <message>yearMonthFrom: Cannot be older than date2019-02-01</message>
                </Error>*/
                return userService.getJsonError("-99", "Request Failed.",xmlResponseMessage, xmlResponseCode, "99", channel, action, requestdata, userName, module, "U");
            }
        }catch (JSONException e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error- JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
        catch(IOException e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error- IOException",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }catch(Exception e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error- Exception",g_error_msg,e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
        }
    }	// start statement upload
    public String funcStartStatementUpload(String application, String module, String action, String event,
                                           String requestdata, String userName) {
        // declaration
        Connection connection;
        CallableStatement cs;
        Utility utility = new Utility();
        JSONObject jsonObject, jsonObject1, jsonObject2;
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        final String loginUserId = userService.getLoginUserID(userName);
        String channel, result, perfiosTransactionId,entityType=null;
        channel = null;
        result = null;
        perfiosTransactionId = null;
        long refId = 0, serialNo = 0;
        /* end declaration */

        // read requestdata
        if (requestdata.isEmpty()) {
            return "Request Body Empty.";
        }
        try {
            jsonObject = new JSONObject(requestdata);
            channel = jsonObject.getString("channel");
            if (jsonObject.getJSONObject("request_data").has("perfiosTransactionId")) {
                perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Action Not Found!", "99",
                        channel, "NOT_FOUND", requestdata, userName, module, "U");
            }
            if (channel == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Channel Not Found!", "99",
                        channel, action, requestdata, userName, module, "U");
            }
            if (perfiosTransactionId == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "perfiosTransactionId Not Found!",
                        "99", channel, action, requestdata, userName, module, "U");
            }
            userService.saveJsonLog(channel, "req", action, requestdata, userName, module);
        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        } catch (Exception e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        }
        // get transaction detail
        URLConfigDto urlConfigDto = userService.findURLDtlByID(32);
        Utility.print("vendorID");
        Utility.print(urlConfigDto.getUserid());
        PerfiosReqResDto perfiosReqResDtlDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDtlDto == null) {
            return userService.getJsonError("-99", "Initial Statement Transaction Not Found",
                    "Initial Statement Transaction Not found.", "Initial Statement Transaction Not Found.", "99",
                    channel, action, requestdata, userName, module, "U");
        } else if (perfiosReqResDtlDto.getStatus().equals("F")) {
            return userService.getJsonError("-99", "Request to Process failed transaction.",
                    "Transaction Status Found Failed, Unable to process.", "Transaction Status is in failed status.",
                    "99", channel, action, requestdata, userName, module, "U");
        }
        // get url dtl

        if (urlConfigDto.getUrl() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }
        if (urlConfigDto.getUserid() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL userID Not Found.", "99", channel,
                    action, requestdata, userName, module, "U");
        }
        String sendURL = urlConfigDto.getUrl(); // + "" + urlConfigDto.getUserid()+"/"+perfiosTransactionId;
        Utility.print("Request Sending URL generated:" + sendURL);
        // get blob file data
        refId = perfiosReqResDtlDto.getRef_tran_cd();
        serialNo = perfiosReqResDtlDto.getRef_tran_cd();
        entityType = perfiosReqResDtlDto.getEntity_type();
        List<CrmDocumentMst> docMst = userService.getDocMstListByDocType("STMT");
        long docId = 0;
        int docMstSize = docMst.size();
        Utility.print("document mst row count for Statement:" + docMstSize); // expect:5 from CRM_DOCCUMENT_MST

        int docMstCount = 0;
        if (docMstSize > 0) {
            // loop for Document Mst traverse for doc type = STMT
            String docType, resultOut, fileName, errorCode, errorMessage, req_status, doc_status, docUUID, docPwd,
                    docTitle;
            Boolean successBool = false;
            Blob blobdata = null;
            Boolean allFailed = true;
            CRMAppDto crmObjPII = userService.findAppByID(3);
            // phase:1
            JSONObject jsonDocStatus = new JSONObject();
            for (CrmDocumentMst crmDocumentMst : docMst) {
                docId = crmDocumentMst.getTran_cd();
                docTitle = crmDocumentMst.getDoc_title();
                docMstCount = docMstCount + 1;
                List<DocUploadDtl> docList = userService.getDocListByDocId(refId, serialNo,entityType, docId); // 1044-19
                if (docList.isEmpty() || docList.size() <= 0) {
                    Utility.print("No row in document_Dtl for:" + refId + "-" + docId);
                    continue;
                }
                Utility.print("Row found in document_Dtl for:" + refId + "-" + docId);
                Utility.print("DOC_TTL:" + crmDocumentMst.getDoc_title() + "\nDOC_DESC:" + docTitle);

                try {
                    int cnt = 0;
                    for (DocUploadDtl docUploadDtl : docList) {
                        cnt += 1;
                        docUUID = docUploadDtl.getDoc_uuid();
                        DocUploadBlobDtl docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                        if (docUploadBlobDtl == null) {
                            userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, "F", null, null, null,
                                    null, "F", "Not Found Data For UUID:" + docUUID);
                            return userService.getJsonError("-99", "Uploading Document Data Not found.", g_error_msg,
                                    "Document Data Not found for UUID:" + docUUID, "99", channel, action, requestdata,
                                    userName, module, "U");
                        }
                        if (docUploadBlobDtl.getDocContentType() == null) {
                            return userService.getJsonError("-99", "Uploading Document Content Type not found.",
                                    g_error_msg, "Document Content Type Not found for UUID:" + docUUID, "99", channel,
                                    action, requestdata, userName, module, "U");
                        }

                        // Getting Document meta data
                        blobdata = new SerialBlob(docUploadBlobDtl.getData());
                        docType = docUploadBlobDtl.getDocContentType();
                        docPwd = docUploadBlobDtl.getPassword();
                        if (docPwd == null) {
                            docPwd = "";
                        } else {
                            docPwd = userService.func_get_data_val(crmObjPII.getA(), crmObjPII.getB(), docPwd);
                            docPwd = Utility.perfiosDataEncryptPub(docPwd);
                        }

                        String docTypeExt = docType.substring(docType.indexOf('/') + 1);
                        if (blobdata.getBinaryStream() != null) {
                            Utility.print("Document upload process started..!");
                            Utility.print("Document Name:" + docUploadBlobDtl.getDoc_name());

                            fileName = "file" + docId + "" + cnt; // expect: file<docId><cnt><randomStamps>
                            File sendFile = utility.blobToFileConverter(blobdata, fileName, "." + docTypeExt);
                            String remarks = null;
                            fileName = sendFile.getName();
                            // attaching file to sendURL
                            Utility.print(perfiosReqResDtlDto.getUserid());
                            Utility.print(perfiosTransactionId);
                            try {
                                Utility.print("File Generated to be Upload:" + fileName);
                                utility.initiateMultipart(sendURL, "UTF-8");
                                utility.addFormField("perfiosTransactionId", perfiosTransactionId);
                                utility.addFormField("vendorId", urlConfigDto.getUserid());
                                utility.addFormField("password", docPwd);
                                utility.addFilePart("file", sendFile);
                                resultOut = utility.finish();
                                utility.deleteFile();
                                Utility.print(resultOut);

                                // resultOut = "<?xml version=1.0
                                // encoding=UTF-8?>\n<Success>\n\t<statementId>1234</statementId>\n\t<accounts>\n
                                // <account>\n <accountPattern>11111111</accountPattern>\n
                                // <transactionStartDate>2016-06-27</transactionStartDate>\n
                                // <transactionEndDate>2016-09-22</transactionEndDate>\n
                                // </account>\n\t\t<account>\n\t\t\t<accountPattern>22222222</accountPattern>\n\t\t\t<transactionStartDate>2016-08-10</transactionStartDate>\n\t\t\t<transactionEndDate>2016-08-11</transactionEndDate>\n\t\t</account>\n\t</accounts>\n</Success>";
                                // resultOut = "<?xml version=1.0
                                // encoding=UTF-8?>\n<Accepted>\n\t<statementId>1234</statementId>\n</Accepted>";
                                /*
                                 * resultOut = "<Error>\n" + "<code>CannotProcessFile</code>\n" +
                                 * "<message>We could not process the statement file uploaded by the Client</message>\n"
                                 * + "<statementErrorCode>E_DATE_RANGE</statementErrorCode>\n" + "</Error>";
                                 */
                                // read xml response data

                                HashMap outParam = new HashMap(), inParam = new HashMap();
                                String error_msg;

                                inParam.put("perfiosTransactionId", perfiosTransactionId);
                                inParam.put("xmlResponse", resultOut);

                                outParam = userService.callingDBObject("procedure",
                                        "pack_document.proc_readxml_stmt_upload_response", inParam);
                                if (outParam.containsKey("error")) {
                                    error_msg = (String) outParam.get("error");
                                    return userService.getJsonError("-99", "Error while Executing callingDBObject",
                                            g_error_msg, error_msg, "99", channel, action, requestdata, userName,
                                            module, "E");
                                }
                                /** 2) get outParam values **/
                                String responseStatus, outJson;
                                responseStatus = (String) outParam.get("responseStatus");
                                outJson = (String) outParam.get("outJson");
                                jsonObject1 = new JSONObject(outJson);
                                if (responseStatus.equals("accepted") || responseStatus.equals("success")) {
                                    allFailed = false;
                                    Utility.print("SUCCESS RESPONSE");
                                    req_status = "true";
                                    doc_status = "U"; // uploaded 14-9(4), 14-11(1)
                                } else {
                                    Utility.print("ERROR RESPONSE");
                                    req_status = "false";
                                    doc_status = "F"; // upload failed
                                    if (jsonObject1.has("code")) {
                                        remarks = jsonObject1.getString("code");
                                    }
                                    if (jsonObject1.has("statementErrorCode")) {
                                        remarks = remarks + ":" + jsonObject1.getString("statementErrorCode");
                                    }
                                    if (jsonObject1.has("message")) {
                                        remarks = remarks + ":" + jsonObject1.getString("message");
                                    }
                                }
                                jsonDocStatus.put(fileName, jsonObject1);
                            } /*
                             * catch (IOException e){ return
                             * userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage()
                             * ,"99",channel,action,requestdata,userName,null,"E"); }
                             */catch (JSONException | IOException e) {
                                return userService.getJsonError("-99", "JSON Error", g_error_msg, e.getMessage(), "99",
                                        channel, action, requestdata, userName, module, "E");
                            }
                            // save each file status
                            connection = jdbcTemplate.getDataSource().getConnection();
                            connection.setAutoCommit(false);
                            cs = connection.prepareCall(
                                    "{ call PACK_DOCUMENT.proc_insert_gstupload_document(?,?,?,?,?,?,?,?,?,?,?,?) }");
                            cs.setString(1, perfiosReqResDtlDto.getUserid());
                            cs.setString(2, perfiosTransactionId);
                            cs.setString(3, String.valueOf(docId));
                            cs.setString(4, docUUID);
                            cs.setString(5, req_status);
                            cs.setString(6, docType);
                            cs.setString(7, fileName);
                            cs.setString(8, doc_status);
                            cs.setString(9, resultOut);
                            cs.setString(10, remarks);
                            cs.setString(11, loginUserId);


                            cs.registerOutParameter(12, 2005);
                            cs.execute();
                            // either 0 or 1: fail or success
                            final String returnData = cs.getString(12);
                            if (returnData.equals("1")) {
                                Utility.print("File Response saved successfully.");
                            } else {
                                Utility.print("File Response failed to save.");
                            }
                            connection.close();
                            cs.close();
                        } else {
                            return userService.getJsonError("-99", "Data Upload Error", g_error_msg,
                                    "Fetching Data found null or not failed to get inputstream", "99", channel, action,
                                    userName, userName, module, "U");
                        }
                    }
                } catch (SQLException e) {
                    return userService.getJsonError("-99", "Error-SQLException", g_error_msg, e.getMessage(), "99",
                            channel, action, userName, userName, module, "E");
                } catch (FileNotFoundException e) {
                    return userService.getJsonError("-99", "Error-FileNotFoundException", g_error_msg, e.getMessage(),
                            "99", channel, action, userName, userName, module, "E");
                }
            } // end loop (CrmDocumentMst)
            if (allFailed) {
                /* NOTE: Update transaction status F because all file failed to upload */
                userService.updatePerfiosWebhookStatus(perfiosTransactionId, null, "F", null, null, null, null, "F",
                        "0 DOCUMENT(S) UPLOADED");
            }
            try {
                jsonObject2 = new JSONObject();
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonDocStatus);
                userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                result = jsonObject2.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return userService.getJsonError("-99", "Error- JSONException", g_error_msg, e.getMessage(), "99",
                        channel, action, userName, userName, module, "E");
            }
            return result;
        } else {
            return userService.getJsonError("-99", "Master Document Type Not Found.(Statement)", g_error_msg,
                    "Statement Document Type not in Master Table.", "99", channel, action, requestdata, userName,
                    module, "U");
        }

    }

    // process document
    public String funcStatementProcess(String application, String module, String action, String event,
                                       String requestdata, String userName) {
        // declaration
        Utility utility = new Utility();
        JSONObject jsonObject, jsonObject1, jsonObject2;
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String channel, result, perfiosTransactionId;
        channel = null;
        result = null;
        perfiosTransactionId = null;
        /* end declaration */

        // read requestdata
        if (requestdata.isEmpty()) {
            return "Request Body Empty.";
        }
        try {
            jsonObject = new JSONObject(requestdata);
            channel = jsonObject.getString("channel");
            if (jsonObject.getJSONObject("request_data").has("perfiosTransactionId")) {
                perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Action Not Found!", "99",
                        channel, "NOT_FOUND", requestdata, userName, module, "U");
            }
            if (channel == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Channel Not Found!", "99",
                        channel, action, requestdata, userName, module, "U");
            }
            if (perfiosTransactionId == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "perfiosTransactionId Not Found!",
                        "99", channel, action, requestdata, userName, module, "U");
            }
            userService.saveJsonLog(channel, "req", action, requestdata, userName, module);
        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        } catch (Exception e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        }
        // get transaction detail
        PerfiosReqResDto perfiosReqResDtlDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDtlDto.getTransaction_id() == null) {
            return userService.getJsonError("-99", "Initial Bank Statement Upload Transaction Not Found",
                    "Initial Transaction Not found.", "Initial Transaction Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }
        if (perfiosReqResDtlDto.getUserid() == null) {
            return userService.getJsonError("-99", "Initial Bank Statement Upload Transaction Not Found",
                    "Initial Transaction Not found.", "Initial Transaction Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        } else if (perfiosReqResDtlDto.getStatus().equals("F")) {
            return userService.getJsonError("-99", "Request to Process failed transaction.",
                    "Transaction Status Found Failed, Unable to process.",
                    "Transaction Status is Failed Due to: " + perfiosReqResDtlDto.getRemarks(), "99", channel, action,
                    requestdata, userName, null, "U");
        }

        // get url dtl
        URLConfigDto urlConfigDto = userService.findURLDtlByID(33);
        if (urlConfigDto.getUrl() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }
        if (urlConfigDto.getUserid() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL userID Not Found.", "99", channel,
                    action, requestdata, userName, module, "U");
        }
        String sendURL = urlConfigDto.getUrl(); // + "" + urlConfigDto.getUserid()+"/"+perfiosTransactionId;
        Utility.print("Request Sending URL generated:" + sendURL);
        /* sample payload */
        /*
         * <?xml version="1.0" encoding="UTF-8"?> <payload> <apiVersion>2.1</apiVersion>
         * <perfiosTransactionId>ZZZZ1234567890123</perfiosTransactionId>
         * <vendorId>vendorId</vendorId> </payload>
         */
        String payload = "<payload><apiVersion>2.1</apiVersion>" + "<perfiosTransactionId>" + perfiosTransactionId
                + "</perfiosTransactionId>" + "<vendorId>" + urlConfigDto.getUserid() + "</vendorId>" + "</payload>";
        try {

            payload = payload.replaceAll("\n", "");
            Utility.print("==condensed payload==");
            Utility.print(payload);
            String dig, signature, mappingData;

            dig = Utility.makeDigest(payload);
            signature = Utility.perfiosDataEncryptPvt(dig);

            HashMap<String, String> requestMapping = new HashMap<String, String>();
            requestMapping.put("payload", payload);
            requestMapping.put("signature", signature);
            byte[] postData = Utility.keyValueMappingString(requestMapping).getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            URL url = new URL(sendURL);
            Utility.print("send URL: " + sendURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            Utility.print("Response code:" + conn.getResponseCode());
            result = Utility.getURLResponse(conn);
            Utility.print(result);

            /* ##############possible response############ */
            /*
             * [e-pdf success] <?xml version=1.0 encoding=UTF-8?> <Success>
             * <message>Transaction completed successfully</message> </Success>
             */

			/*--[scanned accepted]
				<?xml version=1.0 encoding=UTF-8?>
				<Accepted>
				<message>Transaction submitted for processing</message>
				</Accepted>
			*/

			/*--[error]
			<?xml version=1.0 encoding=UTF-8?>
			<Error>
			<code>MethodNotAllowed</code>
			<message>This API supports only POST</message>
			</Error>
			*/
            /* ########################################## */
            /** Read XML response **/
            HashMap inParam = new HashMap();
            HashMap outParam = new HashMap();
            String sqlstmt, xmlErrorCode = null, xmlResponseMessage, xmlResponseStatus, xmlPerfiosTranId;
            sqlstmt = "select  existsnode(value(hdr_data),'/Accepted') as is_accepted, "
                    + "existsnode(value(hdr_data),'/Success') as is_success, "
                    + "existsnode(value(hdr_data),'/Error') as is_error, "
                    + "extractvalue(value(hdr_data),'/Error/code/text()') as err_cd, "
                    + "extractvalue(value(hdr_data),'/Error/code/text()') as err_msg, "
                    + "extractvalue(value(hdr_data),'/Accepted/message/text()') as accepted_msg, "
                    + "extractvalue(value(hdr_data),'/Success/message/text()') as success_msg "
                    + "from table(xmlsequence(extract(xmltype('" + result + "'),'*')))hdr_data";

            // set parameter
            inParam.put("sql", sqlstmt.replaceAll("\n", ""));
            // execute statement
            outParam = userService.callingDBObject("sql", "XMLResponseProcessUpload", inParam);

            if (outParam.containsKey("error")) {
                String error_msg = (String) outParam.get("error");
                return userService.getJsonError("-99", "Error while Executing callingDBObject", g_error_msg, error_msg,
                        "99", channel, action, requestdata, userName, module, "E");
            }
            xmlPerfiosTranId = perfiosTransactionId;
            xmlResponseStatus = (String) outParam.get("xmlResponseStatus"); /* success|error|accepted */
            xmlResponseMessage = (String) outParam.get("xmlResponseMessage");
            if (xmlResponseStatus.equals("error")) {
                xmlErrorCode = (String) outParam.get("xmlErrorCode");
            }
            Utility.print("TranId :" + xmlPerfiosTranId);
            Utility.print("Status :" + xmlResponseStatus);
            Utility.print("ErrCode:" + xmlErrorCode);
            Utility.print("Msg    :" + xmlResponseMessage);
            /** End read xml response **/
            if (xmlResponseStatus.equals("success") || xmlResponseStatus.equals("accepted")) {
                /*
                 * <Accepted><message>Transaction submitted for processing</message></Accepted>
                 * OR <Success><message>Transaction completed successfully</message></Success>
                 */
                jsonObject2.put("status", "0");
                jsonObject1.put("status", xmlResponseStatus);
                jsonObject1.put("message", xmlResponseMessage);
                jsonObject2.put("response_data", jsonObject1);
                userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                return jsonObject2.toString();
            } else {
                /*
                 * <?xml version=1.0 encoding=UTF-8?> <Error> <code>MethodNotAllowed</code>
                 * <message>This API supports only POST</message> </Error>
                 */
                return userService.getJsonError("-99", "Error in Bank Statement Process.", xmlResponseMessage,
                        xmlErrorCode + ": " + xmlResponseMessage, "99", channel, action, requestdata, userName, module,
                        "U");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Error- IOException", g_error_msg, e.getMessage(), "99", channel,
                    action, requestdata, userName, module, "E");
        } catch (Exception e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Error- IOException", g_error_msg, e.getMessage(), "99", channel,
                    action, requestdata, userName, module, "E");
        }
    }

    // cancel transaction
    public String funcCancelTransaction(String application, String module, String moduleCategory,String action, String event,
                                        String requestdata, String userName) {
        // declaration//
        String message = null, perfiosTransactionId = null, channel = null;
        JSONObject jsonObject, jsonObject1, jsonObject2;
        // end declaration//

        module = module+"/"+moduleCategory;
        action = action+"/"+event;
        // read requestdata//
        try {
            jsonObject = new JSONObject(requestdata);
            perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            channel = jsonObject.getString("channel");
        } catch (JSONException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Json Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        }

        // validations
        if (perfiosTransactionId.isEmpty()) {
            return userService.getJsonError("-99", "Error in ITR Statement Upload Status.", "transactionId Not Found.",
                    "transactionId Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if (channel.isEmpty()) {
            return userService.getJsonError("-99", "Error in ITR Statement Upload Status.", "channel Not Found.",
                    "channel Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        userService.saveJsonLog(channel, "req", action, requestdata, userName, module);

        // process
        PerfiosReqResDto perfiosReqResDtlDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDtlDto.getTransaction_id() == null) {
            return userService.getJsonError("-99", "Transaction Not Found at our side", g_error_msg,
                    "Transaction Detail Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if (perfiosReqResDtlDto.getStatus().equals("F")) {
            return userService.getJsonError("-99", "Request to Process failed transaction.",
                    "Transaction Status Found Failed, Unable to process.",
                    "Transaction Status is Failed Due to: " + perfiosReqResDtlDto.getRemarks(), "99", channel, action,
                    requestdata, userName, module, "U");
        }

        URLConfigDto urlConfigDto = userService.findURLDtlByID(35);
        if (urlConfigDto.getUrl() == null) {
            return userService.getJsonError("-99", "Data Error!", g_error_msg, "URL Not Found.", "99", channel, action,
                    requestdata, userName, module, "U");
        }
        if (urlConfigDto.getUserid() == null) {
            return userService.getJsonError("-99", "URL Configuration Not Found.", g_error_msg, "URL detail Not Found.",
                    "99", channel, action, requestdata, userName, module, "U");
        }

        // urlConfigDto.getKey()
        String payload = "<payload><apiVersion>2.1</apiVersion>" + "<perfiosTransactionId>" + perfiosTransactionId
                + "</perfiosTransactionId>" + "<vendorId>" + urlConfigDto.getUserid() + "</vendorId>" + "</payload>";
        try {
            jsonObject2 = new JSONObject();
            Utility utility = new Utility();
            String sendURL, result;
            sendURL = urlConfigDto.getUrl(); // + urlConfigDto.getUserid() + "/" + transactionId;
            Utility.print("generated URL:" + sendURL);

            payload = payload.replaceAll("\n", "");
            Utility.print("==condensed payload==");
            Utility.print(payload);
            String dig, signature, mappingData;

            dig = Utility.makeDigest(payload);
            signature = Utility.perfiosDataEncryptPvt(dig);

            HashMap<String, String> requestMapping = new HashMap<String, String>();
            requestMapping.put("payload", payload);
            requestMapping.put("signature", signature);
            byte[] postData = Utility.keyValueMappingString(requestMapping).getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            URL url = new URL(sendURL);
            Utility.print("send URL: " + sendURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            Utility.print("Response code:" + conn.getResponseCode());
            result = Utility.getURLResponse(conn);
            Utility.print(result);
            /*
             * <Success> <message>Transaction cancelled successfully</message> </Success>
             */

            /*
             * <Error> <code>MethodNotAllowed</code> <message>This API supports only
             * POST</message> </Error>
             */
            try {
                jsonObject = new JSONObject();
                jsonObject1 = new JSONObject(Utility.xmlToJson(result));
                /*
                 * { "Success":{ "message":"sad" } }
                 */
                if (jsonObject1.has("Success")) {
                    int rowUpdate = updateGstDocumentStatus(perfiosTransactionId, "STMT_CANCEL_TRANSACTION",
                            jsonObject1.toString());
                    Utility.print(rowUpdate + " Row(s) updated");

                    jsonObject.put("status", "0");
                    jsonObject.put("response_data", jsonObject1);
                    userService.saveJsonLog(channel, "res", action, jsonObject.toString(), userName, module);
                    return jsonObject.toString();
                } else {
                    return userService.getJsonError("-99", "Error in Cancel Transaction", g_error_msg,
                            jsonObject1.toString(), "99", channel, action, requestdata, userName, module, "U");
                }
            } catch (JSONException e) {
                return userService.getJsonError("-99", "Error in xmlToJson converion.", g_error_msg, e.getMessage(),
                        "99", channel, action, requestdata, userName, module, "E");
            }
        } catch (Exception e) {
            e.getMessage();
            return userService.getJsonError("-99", "Error- Exception", g_error_msg, e.getMessage(), "-99", channel,
                    action, requestdata, userName, module, "E");
        }
    }

    // upload status
    public String funcStatementUploadStatus(String application,String module, String moduleCategory,String action,String event,String requestdata,String userName) {
        //declaration//
        String message = null, perfiosTransactionId = null, channel = null;
        JSONObject jsonObject, jsonObject1, jsonObject2;
        //end declaration//

        module = module+"/"+moduleCategory;
        action = action+"/"+event;
        //read requestdata//
        try {
            jsonObject = new JSONObject(requestdata);
            perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            channel = jsonObject.getString("channel");
        }catch (JSONException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Json Error!", g_error_msg, e.getMessage(), "99", channel, action, requestdata, userName, module, "E");
        }

        //validations
        if(perfiosTransactionId.isEmpty()){
            return userService.getJsonError("-99", "Error in Bank Statement Upload Status.", "transactionId Not Found.", "transactionId Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if (channel.isEmpty()) {
            return userService.getJsonError("-99", "Error in Bank Statement Upload Status.", "channel Not Found.", "channel Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        userService.saveJsonLog(channel, "req", action, requestdata, userName, module);


        Utility.print("entered transactionid:"+perfiosTransactionId);
        //process
        PerfiosReqResDto perfiosReqResDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
        if (perfiosReqResDto.getTransaction_id()==null){
            return userService.getJsonError("-99", "Transaction Not Found at our side", g_error_msg, "Transaction Detail Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if (perfiosReqResDto.getStatus().equals("S")) {
            message = "{\"status\":\"0\",\"response_data\":{\"message\":\"COMPLETED\",\"status\": true}}";
            userService.saveJsonLog(channel, "res", action, message, userName, module);
            return message;
        }else if(perfiosReqResDto.getStatus().equals("I")){
            message = "{\"status\":\"0\",\"response_data\":{\"message\":\"INITIATED\",\"status\": true}}";
            userService.saveJsonLog(channel, "res", action, message, userName, module);
            return message;
        }else if(perfiosReqResDto.getStatus().equals("F")){
            return userService.getJsonError("-99","Request to Process failed transaction.","Transaction Status Found Failed, Unable to process.","Transaction Status is Failed Due to: "+perfiosReqResDto.getRemarks(),"99",channel,action,requestdata,userName,module,"U");
        }

        URLConfigDto urlConfigDto = userService.findURLDtlByID(34);
        if(urlConfigDto.getUrl() == null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestdata,userName,module,"U");
        }
        if(urlConfigDto.getKey()== null){
            return userService.getJsonError("-99", "URL Key Not Found.", g_error_msg, "URL Key Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }
        if(urlConfigDto.getUserid()==null){
            return userService.getJsonError("-99", "URL Configuration Not Found.", g_error_msg, "URL detail Not Found.", "99", channel, action, requestdata, userName, module, "U");
        }

        //urlConfigDto.getKey()
        String payload = "<payload><apiVersion>2.1</apiVersion>"+
                "<txnId>"+perfiosReqResDto.getUserid()+"</txnId>" +
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

            Utility.print("1");
            Utility.print("Response code:"+conn.getResponseCode());
            result = Utility.getURLResponse(conn);
            Utility.print("2");
            Utility.print(result);
            try{
                Utility.print("3");

                jsonObject = new JSONObject();
                jsonObject1 = new JSONObject(Utility.xmlToJson(result));
                Utility.print("bank status:"+jsonObject1.toString());
	                /*{"Status": {
	                        "Part": {
	                            "reason": "Cancelled by client.",
	                            "errorCode": "E_API_CLIENT_CANCELLED",
	                            "perfiosTransactionId": "FDGX1613116663303",
	                            "status": "failure"
	                        },
	                        "parts": 1,
	                        "files": "notavailable",
	                        "processing": "completed",
	                        "txnId": "132743655966b12d2-7861-4f60-bb7a-093e1b979cc820210212"
	                    }}*/
                if(jsonObject1.has("Status")){
                    Utility.print("4");
                    String status = null,files = null;
                    status      = jsonObject1.getJSONObject("Status").getJSONObject("Part").getString("status");
                    files       = jsonObject1.getJSONObject("Status").getString("files");
                    int rowUpdate = updateGstDocumentStatus(perfiosTransactionId, "STMT_UPLOAD_STATUS", jsonObject1.toString());
                    Utility.print(rowUpdate + " Row(s) updated");
                    jsonObject.put("status","0");
                    jsonObject.put("response_data",jsonObject1);
                    if(status.equals("success") && files.equals("available")){
                        Utility.print("transaction is completed");
                        retrieveStatementReport(perfiosTransactionId,perfiosReqResDto.getUserid(),null);
                    }
                    userService.saveJsonLog(channel,"res",action,jsonObject.toString(),userName,module);
                    return jsonObject.toString();
                }else {
                    Utility.print("5");
                    return userService.getJsonError("-99","Error in Statement Upload Status",g_error_msg,jsonObject1.toString(),"99",channel,action,requestdata,userName,module,"U");
                }

            }catch(JSONException e) {
                return userService.getJsonError("-99", "Error in xmlToJson converion.", g_error_msg, e.getMessage(),"99",channel,action,requestdata,userName,module,"E");
            }
        }catch (Exception e) {
            e.getMessage();
            return userService.getJsonError("-99", "Error- Exception", g_error_msg, e.getMessage(), "-99", channel, action, requestdata, userName, module, "E");
        }
    }

    // get statement report
    public String funcGetStatementReport(String application, String module, String action, String event,
                                         String requestdata, String userName) {
        // declaration
        Utility utility = new Utility();
        JSONObject jsonObject, jsonObject1, jsonObject2;
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        String channel = null, result = null, perfiosTransactionId = null;
        /* end declaration */

        // read requestdata
        if (requestdata.isEmpty()) {
            return "Request Body Empty.";
        }
        try {
            jsonObject = new JSONObject(requestdata);
            channel = jsonObject.getString("channel");
            if (jsonObject.getJSONObject("request_data").has("perfiosTransactionId")) {
                perfiosTransactionId = jsonObject.getJSONObject("request_data").getString("perfiosTransactionId");
            }

            if (action == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Action Not Found!", "99",
                        channel, "NOT_FOUND", requestdata, userName, module, "U");
            }
            if (channel == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "Channel Not Found!", "99",
                        channel, action, requestdata, userName, module, "U");
            }
            if (perfiosTransactionId == null) {
                return userService.getJsonError("-99", "Request Error!", g_error_msg, "perfiosTransactionId Not Found!",
                        "99", channel, action, requestdata, userName, module, "U");
            }
            userService.saveJsonLog(channel, "req", action, requestdata, userName, module);

            // get transaction detail
            PerfiosReqResDto perfiosReqResDtlDto = userService.findByPerfiosTransactionID(perfiosTransactionId);
            Utility.print("perfios:" + perfiosReqResDtlDto.getUserid());
            if (perfiosReqResDtlDto.getTransaction_id() == null) {
                return userService.getJsonError("-99", "Initial Bank Statement Upload Transaction Not Found",
                        "Initial Transaction Not found.", "Initial Transaction Not Found.", "99", channel, action,
                        requestdata, userName, module, "U");
            } else if (perfiosReqResDtlDto.getStatus().equals("F")) {
                return userService.getJsonError("-99", "Request to Process failed transaction.",
                        "Transaction Status Found Failed, Unable to process.",
                        "Transaction Status is Failed Due to: " + perfiosReqResDtlDto.getRemarks(), "99", channel,
                        action, requestdata, userName, module, "U");
            }

            String ls_webhook = null;
            String retrieveStatus;
            retrieveStatus = retrieveStatementReport(perfiosTransactionId, perfiosReqResDtlDto.getUserid(), ls_webhook);
            return retrieveStatus;
        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        } catch (Exception e) {
            Utility.print(e.getMessage());
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action,
                    requestdata, userName, module, "E");
        }
    }

    // retrieve statement method
    public String retrieveStatementReport(String perfiosTransactionId, String txnId, String ls_webhookres) {
        Utility utility = new Utility();
        String processResponse=utility.getWebhookProcessStructure();
        //processResponse structure
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
                        userService.updatePerfiosWebhookStatus(perfiosTransactionId, httpStatus, "S", ls_webhookres,
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

    public String funcDocUploadProcess(String module, String moduleCategory, String action, String event,
                                       String subEvent, String subEvent1, String userName, Long refID, Long serialNo, List<MultipartFile> files,
                                       String metaData, String uploadCategory,String entityType,String categoryCD) {
        String requestData = "MULTIPART_FORM_DATA", response = null, channel = "W";
        CRMAppDto crmAppDto3 = userService.findAppByID(3);
        JSONObject jsonObject, jsonObject1, jsonObject2, jsonMetaData;
        JSONArray jsonArray = new JSONArray();
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
        jsonMetaData = new JSONObject();
        // common parameter
        String fileName = null, filePwd = null, fileExt = null, remarks = null, password = null, blobID = null,
                docID = null;
        // additional parameter
        long bankLineID = 0;
        String finYear = null;

        // lead/management/document/bank/data/post
        if (event != null && subEvent != null && subEvent1 != null) {
            action = action + "/" + event + "/" + subEvent + "/" + subEvent1;
        } else if (event != null && subEvent != null) {
            action = action + "/" + event + "/" + subEvent;
        } else {
            action = action + "/" + event;
        }

        module = module + "/" + moduleCategory;
        Utility.print("file count:" + files.size());
        if (files.isEmpty() || files == null || files.size() <= 0) {
            return userService.getJsonError("-99", "Not found any file.", "Please upload at least one file.",
                    "Not found any file to be upload.", "99", channel, action, requestData, userName, module, "U");
        }
        try {
            jsonObject = new JSONObject(metaData);
            Utility.print("Main MetaData:" + jsonObject.toString());
            // save request data
            userService.saveJsonLog(channel, "req", action, requestData, userName, module);

            String uuid = null,documentCategory=null;
            Boolean deleteStatus = true;
            documentCategory = uploadCategory.toUpperCase()+"_DOC_TYPE";
            /* userService.deleteDocument(Long.parseLong(refID),Long.parseLong(docID)) */;
            DocUploadDtl docUploadDtl = new DocUploadDtl();
            DocUploadBlobDtl docUploadBlobDtl = new DocUploadBlobDtl();
            CrmLeadMst crmLeadMst = new CrmLeadMst();
            crmLeadMst = userService.findLeadByID(refID);

            if(crmLeadMst==null){
                return userService.getJsonError("99", "Inquiry Code Not Found.", g_error_msg,
                        "Inquiry Code not found for lead:" + refID, "99", "W", action, requestData, userName, module,
                        "U");

            }
            if (crmLeadMst.getInquiry_tran_cd() == null) {
                return userService.getJsonError("99", "Inquiry Code Not Found.", g_error_msg,
                        "Inquiry Code not found for lead:" + refID, "99", "W", action, requestData, userName, module,
                        "U");
            }

            int loopCount = 0, saveCount = 0;
            //if (deleteStatus) {
            for (MultipartFile mimeMultipart : files) {
                loopCount += 1;
                Utility.print("files found!");
                blobID = mimeMultipart.getOriginalFilename();
                try {
                    jsonMetaData = jsonObject.getJSONObject(blobID);
                    Utility.print("metaData:" + jsonMetaData.toString());

                    // common paramter
                    fileName = jsonMetaData.getString("name");
                    filePwd = jsonMetaData.getString("password");
                    fileExt = jsonMetaData.getString("ext");
                    remarks = jsonMetaData.getString("remarks");
                    docID = jsonMetaData.getString("docTypeID");


                    if (uploadCategory.equals("bank")) {
                        bankLineID = jsonMetaData.getLong("bankLineID");
                    } else if (uploadCategory.equals("itr")) {
                        finYear = jsonMetaData.optString("finYear");
//                        if (finYear == null || finYear.isEmpty()) {
//                            return userService.getJsonError("-99", "Financial Year is Required.",
//                                    "Financial Year is Required.", "finYear value not found.", "99", channel,
//                                    action, requestData, userName, module, "U");
//                        }
                    } else if (uploadCategory.equals("gst")) {
                        // no additional parameter at now
                    } else if (uploadCategory.equals("kyc")) {
                        // no additional parameter at now
                    } else if (uploadCategory.equals("other")) {
                        // no additional parameter at now
                    }

                    //checking not any api active for this type of upload for this user
                    HashMap inParam = new HashMap(), outParam;
                    String dbError; long result=0;
                    inParam.put("refID",refID);
                    inParam.put("serialNo",serialNo);
                    inParam.put("entityType",entityType);
                    inParam.put("documentCategory",categoryCD);//documentCategory
                    inParam.put("bankLineID",bankLineID);
                    inParam.put("docTypeID",docID);

                    outParam =  userService.callingDBObject("procedure","pack_document.proc_active_document_count",inParam);
                    if(outParam.containsKey("error")){
                        dbError = (String) outParam.get("error");
                        Utility.print(dbError);
                        return userService.getJsonError("-99", "Error",g_error_msg, dbError, "99", channel, action, requestData, userName, module, "U");
                    }else{
                        result = Long.parseLong((String)outParam.get("result"));
                    }

                    if(result>0){
                        return userService.getJsonError("-99", "Upload process declined.",
                                "Documents are in process, Please try after some time",
                                "Number of Processing Documents:"+result,
                                "99", channel, action, requestData, userName, module, "U");
                    }
                    //end checking

                    Utility.print(fileName);
                    Utility.print(filePwd);
                    Utility.print(fileExt);
                    Utility.print(remarks);
                    Utility.print(String.valueOf(bankLineID));
                    Utility.print(docID);

                    docUploadDtl = new DocUploadDtl();
                    docUploadBlobDtl = new DocUploadBlobDtl();

                    // uuid =
                    // org.apache.commons.codec.binary.Base64.encodeBase64String(uuid.getBytes());
                    // set document dtl column values
                    uuid = userService.getuniqueId();
                    docUploadDtl.setDoc_uuid(uuid);
                    docUploadDtl.setDoc_id(Long.parseLong(docID));
                    docUploadDtl.setDoc_upoload_dt(new Date());
                    docUploadDtl.setLead_id(refID);
                    docUploadDtl.setSr_cd(serialNo);
                    docUploadDtl.setInquiry_id(crmLeadMst.getInquiry_tran_cd());
                    docUploadDtl.setStatus("P");
                    docUploadDtl.setEntity_type(entityType);
                    docUploadDtl.setEntred_by("SYS");
                    docUploadDtl.setLast_entred_by("SYS");
                    docUploadDtl.setLast_machine_nm(InetAddress.getLocalHost().getHostName());
                    docUploadDtl.setEntered_dt(new Date());
                    docUploadDtl.setRemarks(remarks);
                    docUploadDtl.setDoc_category_nm(categoryCD);

                    // set document blob dtl column values
                    docUploadBlobDtl.setUuid(uuid);
                    docUploadBlobDtl.setData(mimeMultipart.getBytes());
                    docUploadBlobDtl.setDoc_name(fileName);
                    docUploadBlobDtl.setDoc_size(mimeMultipart.getSize() / 1024);
                    docUploadBlobDtl.setDocContentType(mimeMultipart.getContentType());
                    docUploadBlobDtl.setBank_line_id(bankLineID);
                    docUploadBlobDtl.setPassword(filePwd);
                    if (finYear != null) {
                        docUploadBlobDtl.setParam1(finYear);
                    }

                    Utility.print("docUUID:" + docUploadDtl.getDoc_uuid());
                    Utility.print("blobUUID:" + docUploadBlobDtl.getUuid());
                    Utility.print("Inquiry:" + docUploadDtl.get_inquiry_id());

                    userService.saveDocument(docUploadDtl);
                    saveCount += 1;
                    userService.saveDocumentLob(docUploadBlobDtl);
                    Utility.print("save count:" + saveCount);
                    jsonObject1.put("fileName", fileName);
                    jsonObject1.put("status", "success");
                    jsonArray.put(jsonObject1);
                    jsonObject1 = new JSONObject();
                } catch (JSONException e) {
                    return userService.getJsonError("-99", "Error-JSONException", g_error_msg,
                            "Error while reading metaData:" + e.getMessage(), "99", channel, action, requestData,
                            userName, module, "E");
                } catch (Exception e) {
                    String flag = "" + saveCount;
                    if (saveCount > 0) {
                        flag = userService.deleteUploadedDocumentByLeadID(refID,
                                Long.parseLong(docID));
                        Utility.print(
                                "Document delete status for refID(" + refID + ")&docID(" + docID + "): " + flag);
                    }
                    return userService.getJsonError("-99", "Failed to upload document", g_error_msg,
                            e.getMessage() + "Deleted:" + flag, "99", channel, action, requestData, userName,
                            module, "E");
                }
            }
            // after loop
            try {
                jsonObject2.put("status", "0");
                jsonObject2.put("response_data", jsonArray);
                response = jsonObject2.toString();
                userService.saveJsonLog(channel, "res", action, response, userName, module);
                return response;
            } catch (JSONException e) {
                return userService.getJsonError("-99", "JSONException..!", g_error_msg, e.getMessage(), "99",
                        channel, action, requestData, userName, module, "E");
            }
            //}
        } catch (JSONException e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel,
                    action, requestData, userName, module, "E");
        } catch (Exception e) {
            e.printStackTrace();
            return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel,
                    action, requestData, userName, module, "E");
        }
        //return "success";
    }

    public HttpServletResponse documentDownload(String module, String moduleCategory, String userName, String action,
                                                String event, HttpServletResponse response, @RequestBody String requestBody, String subEvent,
                                                String subEvent1) throws IOException {
        JSONObject jsonRequest = null;
        org.json.JSONArray jsonArray = null;
        String channel = "W";
        String zipName = null;
        String patternDate = "yyyy-MM-dd";
        String patternTime = "-HHmmssSS-";
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patternDate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patternTime);
        String date = simpleDateFormatDate.format(new Date());
        String time = simpleDateFormatTime.format(new Date());
        String docUUID = null, fileName = null, extension = null;
        Blob fileBlob = null;
        Utility.print("getDownload:1");

        if (event != null && subEvent != null && subEvent1 != null) {
            zipName = date + time + event + "Documents.zip";
            action = action + "/" + event + "/" + subEvent + "/" + subEvent1;
        } else if (event != null && subEvent != null) {
            zipName = date + time + action + "Documents.zip";
            action = action + "/" + event + "/" + subEvent;
        } else {
            zipName = date + time + action + "Documents.zip";
            action = action + "/" + event;
        }
        module = module + "/" + moduleCategory;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        String zipFilePath = Utility.LOCAL_PATH + Utility.SEPERATOR + zipName;
        try {
            int totalDocUUID = 0;
            jsonRequest = new JSONObject(requestBody);
            Utility.print("Actual Request:" + jsonRequest.toString());
            jsonArray = jsonRequest.getJSONObject("request_data").getJSONArray("docUUID");
            totalDocUUID = jsonArray.length();
            InputStream inputStream = null, zipInputStream = null;
            userService.saveJsonLog(channel, "req", action, requestBody, userName, module);
            DocUploadBlobDtl docUploadBlobDtl;
            if (totalDocUUID == 1) {
                /** for single docUUID send actual document **/
                // docUUID = jsonRequest.getJSONObject("request_data").getString("docUUID");
                docUUID = jsonArray.getString(0);
                docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                fileBlob = new SerialBlob(docUploadBlobDtl.getData());
                fileName = docUploadBlobDtl.getDoc_name();
                extension = docUploadBlobDtl.getDocContentType();
                if (fileName == null) {
                    fileName = "file".concat(date + time);
                    Utility.print("fileName:" + fileName);
                    if (extension != null) {
                        extension = extension.substring(extension.indexOf('/') + 1);
                        Utility.print("slice ext:" + extension);
                        fileName = !extension.isEmpty() ? fileName.concat("." + extension) : fileName;
                    }
                }
                inputStream = fileBlob.getBinaryStream();
                response = userService.startFileDownload(response, inputStream, fileName);
            } else if (totalDocUUID > 1) {
                /** for multiple docUUID send files in archive format **/
                try {
                    File zipFile = new File(zipFilePath);
                    fos = new FileOutputStream(zipFile);
                    zos = new ZipOutputStream(fos);
                    for (int i = 0; i < totalDocUUID; i++) {
                        docUUID = jsonArray.getString(i);
                        Utility.print("loop docUUID:" + docUUID);
                        docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                        fileBlob = new SerialBlob(docUploadBlobDtl.getData());
                        fileName = docUploadBlobDtl.getDoc_name();
                        extension = docUploadBlobDtl.getDocContentType();
                        if (fileName == null || fileName.isEmpty()) {
                            fileName = "file".concat(date + time);
                            Utility.print("fileName:" + fileName);
                            if (extension != null) {
                                extension = extension.substring(extension.indexOf('/') + 1);
                                Utility.print("slice ext:" + extension);
                                fileName = !extension.isEmpty() ? fileName.concat("." + extension) : fileName;
                            }
                        }
                        inputStream = fileBlob.getBinaryStream();
                        /** write to zip file **/
                        Utility.writeToZipFile(inputStream, (i + 1) + "" + fileName, zos);
                    }
                    fileName = zipName;
                    zos.close();
                    fos.close();
                    zipInputStream = new FileInputStream(zipFile);
                    response = userService.startFileDownload(response, zipInputStream, fileName);
                    zipInputStream.close();
                    if (zipFile.delete()) {
                        Utility.print(fileName + " file deleted from server");
                    } else {
                        zipFile.deleteOnExit();
                        Utility.print(fileName + " will be delete on exit");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utility.print(fileName + "file is ready for download.");
            userService.saveJsonLog(channel, "res", action, "http status:" + response.getStatus(), userName, module);
        } catch (JSONException e) {
            response.setStatus(500);
            Utility.print(e.getMessage());
            userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action,
                    requestBody, userName, module, "E");
        } catch (SerialException e) {
            response.setStatus(500);
            Utility.print(e.getMessage());
            userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action,
                    requestBody, userName, module, "E");
        } catch (SQLException e) {
            response.setStatus(500);
            Utility.print(e.getMessage());
            userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action,
                    requestBody, userName, module, "E");
        }
        return response;
    }

    public HttpServletResponse perfiosdocumentDownload( String module,String moduleCategory,String userName,String action,String event,
                                                        HttpServletResponse response,@RequestBody String requestBody, String subEvent,String subEvent1) throws IOException
    {
        JSONObject jsonRequest =null;
        JSONArray jsonArray =null;
        String channel ="W";
        String zipName = null;
        String patternDate = "yyyyMMdd";
        String patternTime = "HHmmssSS";
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patternDate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patternTime);
        String date = simpleDateFormatDate.format(new Date());
        String time = simpleDateFormatTime.format(new Date());
        String docUUID =null, fileName=null,extension=null;
        Blob fileBlob = null;

        if(event!=null && subEvent!=null && subEvent1!=null){
            zipName = event+"perfios_report.zip";
            action  = action+"/"+event+"/"+subEvent+"/"+subEvent1;
        }else if(event!=null && subEvent!=null){
            zipName = action+"perfios_report.zip";
            action = action+"/"+event+"/"+subEvent;
        }else{
            zipName = action+"perfios_report.zip";
            action = action+"/"+event;
        }
        module = module+"/"+moduleCategory;
        FileOutputStream        fos = null;
        ZipOutputStream         zos  = null;
        String zipFilePath = Utility.LOCAL_PATH+Utility.SEPERATOR+zipName;
        try {
            int totalDocUUID = 0;
            jsonRequest  = new JSONObject(requestBody);
            Utility.print("Actual Request:"+jsonRequest.toString());
            jsonArray    = jsonRequest.getJSONObject("request_data").getJSONArray("docUUID");
            totalDocUUID = jsonArray.length();
            InputStream  inputStream =null, zipInputStream=null;
            userService.saveJsonLog(channel,"req",action,requestBody,userName,module);
            DocUploadBlobDtl docUploadBlobDtl;
            PerfiosReqResDto perfiosReqResDto;
            if(totalDocUUID==1){
                /**for single docUUID send actual document**/
                //docUUID   = jsonRequest.getJSONObject("request_data").getString("docUUID");
                docUUID   = jsonArray.getString(0);
                //docUploadBlobDtl = userService.findDocBlobByUUID(docUUID);
                perfiosReqResDto = userService.findByPerfiosTransactionID(docUUID);
                if(perfiosReqResDto.getRequest_type().equalsIgnoreCase("stmt_upload")){
                    fileBlob = new SerialBlob(perfiosReqResDto.getXlsfile());
                    fileName = "perfiosReport_"+docUUID+".xlsx";
                }else{
                    fileBlob = new SerialBlob(perfiosReqResDto.getZipfile());
                    fileName = "perfiosReport_"+docUUID+".zip";
                }
                extension = "application/zip";
                if(fileName==null){
                    fileName="file".concat(date+time);
                    if(extension!=null){
                        extension = extension.substring(extension.indexOf('/')+1);
                        fileName = !extension.isEmpty() ? fileName.concat("."+extension): fileName;
                    }
                }
                inputStream = fileBlob.getBinaryStream();
                response = userService.startFileDownload(response,inputStream,fileName);
            }
            Utility.print(fileName+"file is ready for download.");
            userService.saveJsonLog(channel,"res",action,"http status:"+response.getStatus(),userName,module);
        }catch (JSONException e){
            userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestBody,userName,module,"E");
            response.sendError(500);
            e.printStackTrace();
        }
        catch (SerialException e) {
            userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestBody,userName,module,"E");
            response.sendError(500);
            e.printStackTrace();
        } catch (SQLException e) {
            userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestBody,userName,module,"E");
            response.sendError(500);
            e.printStackTrace();
        }
        return  response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lead/cam/download")
    public void funcCAMDownload(@RequestParam(name = "refID", required = true) String leadID,
                                @RequestParam(name = "serialNo", required = true) String serialNo,
                                @RequestParam(name = "download", required = true) String download,HttpServletResponse response) {
        CRMCAMDtlDto crmcamDtlDto = userService.getCAMData(Long.parseLong(leadID), Long.parseLong(serialNo));
        if (crmcamDtlDto.getCam_data() != null) {
            Blob fileBlob = null;
            InputStream inputStream = null;
            try {
                fileBlob = new SerialBlob(crmcamDtlDto.getCam_data());
                inputStream = fileBlob.getBinaryStream();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            response.setContentType("application/pdf");
            String FileName = leadID+"_LEAD_CAM.pdf";
            if (download.equals("Yes")){
                response.setHeader("Content-Disposition", String.format("attachment; filename="+FileName+""));
            }else {
                response.setHeader("Content-Disposition", String.format("inline; filename="+FileName+""));
            }
            // Here we have mentioned it to show as attachment
            // response.setHeader("Content-Disposition", String.format("attachment;
            // filename=\"" + file.getName() + "\""));

            try {
                // response.setContentLength((int) IOUtils.toByteArray(inputStream).length);
                // IOUtils.copy(inputStream,response.getOutputStream());
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String funcCheckPerfiosInitiateRequest(Long refID, Long srID,String entityType,String param1,String requestType){
        HashMap inParam = new HashMap(),outParam;
        String dbError =null;
        JSONObject jsonObject=new JSONObject(), jsonObject1 = new JSONObject();
        inParam.put("refID",refID);
        inParam.put("srID",srID);
        inParam.put("entityType",entityType);
        inParam.put("requestType",requestType);
        inParam.put("param1",param1);

        outParam = userService.callingDBObject("procedure","pack_document.proc_perfios_init_req_validate",inParam);
        Utility.print("check initiate");
        if(outParam.containsKey("error")){
            dbError = (String)outParam.get("error");
            Utility.print("Error:"+dbError);
            try {
                jsonObject1.put("error_cd","-99");
                jsonObject1.put("error_title","Error");
                jsonObject1.put("error_msg",g_err_msg);
                jsonObject1.put("error_detail",dbError);

                jsonObject.put("status","99");
                jsonObject.put("error_data",jsonObject1);
                return  jsonObject.toString();
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        Utility.print("Error:"+(String)outParam.get("result"));
        return  (String)outParam.get("result");
    }

    /**#############Corpository API#############**/
    /**1. @Login **/
    public String funcInitiateCorpositoryLogin(String g_application, String module, String moduleCategory, String action, String event, String requestData, String userName){
        final String requestType = "login", channel="W";
        JSONObject jsonObject  = new JSONObject();
        JSONObject jsonURLResponse = new JSONObject();
        LosCorpositoryAPI losCorpositoryAPI = new LosCorpositoryAPI();
        String sendURL = null,loginID = null,loginPassword = null,resStatus=null,data=null;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(38);

        module = module+"/"+moduleCategory;
        requestData = "CORPOSITORY-LOGIN-API";

        if(urlConfigDto==null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Details Not Found.","99",channel,action,requestData,userName,module,"U");
        }
        sendURL = urlConfigDto.getUrl();
        Utility.print("send url:"+sendURL);
        if((sendURL==null || sendURL.isEmpty())){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestData,userName,module,"U");
        }
        loginID = urlConfigDto.getUserid();
        if(loginID==null || loginID.isEmpty()){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestData,userName,module,"U");
        }
        loginPassword = urlConfigDto.getKey();
        if(loginPassword==null || loginPassword.isEmpty()){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL key Not Found.","99",channel,action,requestData,userName,module,"U");
        }

        data = getCorpositoryTokenDetail();
        Utility.print("Token Search Result From DB:"+data);
        if(data.equalsIgnoreCase("not_found")){
            try {
                String url,result,response,createJson;
                createJson = "{\"request\": \"login\",\"para\": {\"user-id\": \""+loginID+"\",\"password\": \""+loginPassword+"\"}}";
                userService.saveJsonLog(channel,"req",action,requestData,userName,module);
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
                Utility.print("Response Code  :"+conn.getResponseCode());
                Utility.print("Response Result:"+result);

                try{
                    jsonURLResponse = new JSONObject(result);
                }catch (JSONException e){
                    //avoind error
                }
                if(conn.getResponseCode()==conn.HTTP_OK){
                    resStatus = "S";
                    jsonObject.put("status","0");
                    jsonObject.put("response_data",jsonURLResponse);
                    userService.saveJsonLog(channel,"res",action,result,userName,module);

                    losCorpositoryAPI.setRequest_type(requestType);
                    losCorpositoryAPI.setInitiated_req1(requestData);
                    losCorpositoryAPI.setInitiated_req2(createJson);
                    losCorpositoryAPI.setStatus(resStatus);
                    losCorpositoryAPI.setApi_res(result);
                    losCorpositoryAPI.setRef_tran_cd(null);
                    losCorpositoryAPI.setRef_Sr_cd(null);
                    losCorpositoryAPI.setEntity_type(null);
                    losCorpositoryAPI.setRemarks(requestType+" success");
                    userService.saveCorpositoryAPI(losCorpositoryAPI);
                    return jsonObject.toString();
                }else {
                    resStatus = "F";
                    losCorpositoryAPI.setRequest_type(requestType);
                    losCorpositoryAPI.setInitiated_req1(requestData);
                    losCorpositoryAPI.setInitiated_req2(createJson);
                    losCorpositoryAPI.setStatus(resStatus);
                    losCorpositoryAPI.setApi_res(result);
                    losCorpositoryAPI.setRef_tran_cd(null);
                    losCorpositoryAPI.setRef_Sr_cd(null);
                    losCorpositoryAPI.setEntity_type(null);
                    losCorpositoryAPI.setRemarks(requestType+" failed");
                    userService.saveCorpositoryAPI(losCorpositoryAPI);
                    return userService.getJsonError("-99","Response Status:"+conn.getResponseCode(),g_error_msg,result,"99",channel,action,requestData,userName,module,"U");
                }
            }catch(JSONException e){
                e.printStackTrace();
                return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            }
            catch (Exception e) {
                e.printStackTrace();
                return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            }
        }else{
            return  data;
        }

    }

    public String getCorpositoryTokenDetail_v2(String userName){
        final String requestType = "login", channel="W",
                module      = "lead/external", action="corpository/login",
                loginUserID = userService.getLoginUserID(userName);
        JSONObject jsonObject  = new JSONObject();
        JSONObject jsonURLResponse = new JSONObject();
        LosCorpositoryAPI losCorpositoryAPI = new LosCorpositoryAPI();
        String sendURL = null,loginID = null,loginPassword = null,resStatus=null,data=null;
        String requestData = "fetching token";
        URLConfigDto urlConfigDto = userService.findURLDtlByID(38);

        if(urlConfigDto==null){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Details Not Found.","99",channel,action,requestData,userName,module,"U");
        }
        sendURL = urlConfigDto.getUrl();
        Utility.print("send url:"+sendURL);
        if((sendURL==null || sendURL.isEmpty())){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestData,userName,module,"U");
        }
        loginID = urlConfigDto.getUserid();
        if(loginID==null || loginID.isEmpty()){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL userID Not Found.","99",channel,action,requestData,userName,module,"U");
        }
        loginPassword = urlConfigDto.getKey();
        if(loginPassword==null || loginPassword.isEmpty()){
            userService.getJsonError("-99","Data Error!",g_error_msg,"URL key Not Found.","99",channel,action,requestData,userName,module,"U");
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
                        jsonObject =  new JSONObject(
                                userService.getJsonError("-99","Error response in login(corpository)",g_error_msg,result,"99",channel,action,requestData,userName,module,"U")
                        );
                    }
                    losCorpositoryAPI.setRequest_type(requestType);
//                    losCorpositoryAPI.setInitiated_req1(requestData);
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
                    return jsonObject.toString();
                }else {
                    resStatus = "F";
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
                    return userService.getJsonError("-99","Response Status:"+conn.getResponseCode(),g_error_msg,result,"99",channel,action,requestData,userName,module,"U");
                }
            }catch(JSONException e){
                e.printStackTrace();
                return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
            }
            catch (Exception e) {
                e.printStackTrace();
                return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
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

    /**2 @CompanySearch **/
    public String funcInitiateCompanySearch(String g_application, String module, String moduleCategory, String action, String event, String requestData, String userName){
        JSONObject jsonObject =null,jsonObject1=null,jsonTokenData=null;
        JSONObject jsonURLResponse = new JSONObject();
        final String requestType = "searchCompanies",loginUserId = userService.getLoginUserID(userName);
        String filter=null, sendURL,result,channel="W",tokenResult=null,
                authTokenID=null,v_tag=null,data=null,
                resStatus=null,entityName=null;
        long authUserID = 0;
        long refID=0;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(38);
        LosCorpositoryAPI losCorpositoryAPI = new LosCorpositoryAPI();

        module = module+"/"+moduleCategory;
        action = action+"/"+event;


        userService.saveJsonLog(channel,"req",action,requestData,userName,module);
        /*read requestdata*/
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
            if(jsonObject.has("entityName")){
                v_tag = "entityName";
                entityName = jsonObject.getString(v_tag);
            }
            if(refID<=0){
                return userService.getJsonError("-99","Data Error","refID not found","refID not found","99",channel,action,requestData,userName,module,"U");
            }
        }catch (JSONException e){
            return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage()+" tag:"+v_tag,"99",channel,action,requestData,userName,module,"E");
        }catch(Exception e){
            return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }

        //other data requirements
        //>>fetching entityName
        if(entityName==null || entityName.isEmpty()){
            data = userService.getEntityNameById(refID);
            if(data.indexOf("error")>=0){
                return userService.getJsonError("-99","Error while calling DB",g_error_msg,data,"99",channel,action,requestData,userName,module,"E");
            }else{
                entityName = data;
            }
        }
        if(entityName==null || entityName.trim().isEmpty()){
            return userService.getJsonError("-99","Data Error","EntityName not found","EntityName not found","99",channel,action,requestData,userName,module,"U");
        }

        if(urlConfigDto==null){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Details Not Found.","99",channel,action,requestData,userName,module,"U");
        }

        sendURL = urlConfigDto.getUrl();
        Utility.print("send url:"+sendURL);
        if((sendURL==null || sendURL.isEmpty())){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"URL Not Found.","99",channel,action,requestData,userName,module,"U");
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
                return  tokenResult;
            }
        }catch (JSONException e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error while fetching Corpository token details",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }

        String createJson = "{\n" +
                " \"request\": \"searchCompanies\",\n" +
                " \"para\": {\n" +
                " \"api_auth_token\": \""+authTokenID+"\",\n" +
                " \"user_id\":\""+authUserID+"\",\n" +
                " \"source_system\":\"clientapi\",\n" +
                " \"search-criteria\": {\n" +
                "  \"company-ids\": [],\n" +
                " \"company-names\": [],\n" +
                " \"company-name-partials\": [\""+entityName+"\"],\n" +
                " \"cins\": [],\n" +
                " \"cin-partials\": [],\n" +
                " \"partials-search-type\": \"\",\n" +
                " \"city\": [],\n" +
                " \"state\": [],\n" +
                " \"status\": [],\n" +
                " \"type\": [],\n" +
                " \"liability\": [],\n" +
                " \"offset-start\": 0,\n" +
                " \"offset-end\": 50\n" +
                " }\n" +
                " }\n" +
                "}";
        try{
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
                //avoid error
            }
            if(conn.getResponseCode()==conn.HTTP_OK){
                if(jsonURLResponse.getLong("status")==1){
                    resStatus = "S";
                    jsonObject1.put("status","0");
                    //add ref id in response
                    jsonURLResponse.put("refID",refID);
                    jsonObject1.put("response_data",jsonURLResponse);
                }else{
                    resStatus = "F";
                    jsonObject1 =  new JSONObject(
                            userService.getJsonError("-99","Error in companySearch",g_error_msg,result,"99",channel,action,requestData,userName,module,"U")
                    );
                }
            }else {
                resStatus = "F";
                jsonObject1 =  new JSONObject(
                        userService.getJsonError("-99","Error in companySearch",g_error_msg,result,"99",channel,action,requestData,userName,module,"U")
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
            losCorpositoryAPI.setEntered_by(loginUserId);
            losCorpositoryAPI.setLast_entered_by(loginUserId);
            userService.saveCorpositoryAPI(losCorpositoryAPI);
            return jsonObject1.toString();
        }catch(JSONException e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
        catch (Exception e) {
            e.printStackTrace();
            return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
    }

    /**3 @PlaceOrder or creditOrder**/
    public String funcInitiateCreditOrder(String g_application, String module, String moduleCategory, String action, String event, String requestData, String userName){
        JSONObject jsonObject =null, jsonObject1=null,jsonTokenData=null;
        JSONObject jsonURLResponse = new JSONObject();
        final String requestType = "creditOrder",loginUserId = userService.getLoginUserID(userName);
        String filter=null, sendURL,result,channel="W",tokenResult=null,
                authTokenID=null,v_tag=null,enitityName=null,data=null,
                resStatus=null;
        long authUserID = 0;
        long refID=0,companyID=0;
        String companyName = null,webhookURL=null;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(38);

        LosCorpositoryAPI losCorpositoryAPI = new LosCorpositoryAPI();

        module = module+"/"+moduleCategory;
        action = action+"/"+event;
        userService.saveJsonLog(channel,"req",action,requestData,userName,module);
        /*read requestdata*/
	/*
		{
		"request_data":{
			"company_id":4616
			"company_name":"xyz",
			"refID":98
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
            if(jsonObject.has("companyName")){
                v_tag = "companyName";
                companyName = jsonObject.getString(v_tag);
            }
            if(refID<=0){
                return userService.getJsonError("-99","Data Error","refID not found","refID not found","99",channel,action,requestData,userName,module,"U");
            }
            if(companyID<=0){
                return userService.getJsonError("-99","Data Error","companyID not found","companyID not found","99",channel,action,requestData,userName,module,"U");
            }
            if(companyName==null||companyName.trim().isEmpty()){
                return userService.getJsonError("-99","Data Error","companyName not found","companyName not found","99",channel,action,requestData,userName,module,"U");
            }
        }catch (JSONException e){
            return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage()+" tag:"+v_tag,"99",channel,action,requestData,userName,module,"E");
        }catch(Exception e){
            return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }


        //check for: already initiated or not
        result = funcCheckCorpositoryInitiateRequest(refID,Long.valueOf(1),"L",requestType,null);
        if(!result.equalsIgnoreCase("success")){
            return  result;
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
        webhookURL = userService.func_get_base_url("los/webhooks")+urlConfigDto.getSmtp_server();
        if(webhookURL==null || webhookURL.trim().isEmpty()){
            return userService.getJsonError("-99","Data Error!",g_error_msg,"Webhoook url Not Found.","99",channel,action,requestData,userName,module,"U");
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
            e.printStackTrace();
            return userService.getJsonError("-99","Error while fetching Corpository token details",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
        String createJson = "{\"request\": \"creditOrder\",\n" +
                "\"para\": {\n"+
                "\"api_auth_token\": \""+authTokenID+"\",\n"+
                "\"user_id\": \""+authUserID+"\",\n"+
                "\"webhook_urls\": [{\"web-hook-url\": \""+webhookURL+"\"}],\n" +
                "\"company_data\":[{\n" +
                "\"company-id\": \""+companyID+"\",\n" +
                "\"company-name\": \""+companyName+"\"\n" +
                "}],\n"+
                "\"source_system\": \"clientapi\"\n" +
                "}}";
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
                            userService.getJsonError("-99","Error in creditOrder",g_error_msg,result,"99",channel,action,requestData,userName,module,"U")
                    );
                }
            }else {
                resStatus = "F";
                jsonObject1 =  new JSONObject(
                        userService.getJsonError("-99","Error in creditOrder",g_error_msg,result,"99",channel,action,requestData,userName,module,"U")
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
            losCorpositoryAPI.setEntered_by(loginUserId);
            losCorpositoryAPI.setLast_entered_by(loginUserId);
            userService.saveCorpositoryAPI(losCorpositoryAPI);
            return jsonObject1.toString();
        }catch(JSONException e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
        catch (Exception e) {
            e.printStackTrace();
            return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
    }

    /**4 @request for financial_detail**/
    public String funcGetFinancialDetail(String g_application, String module, String moduleCategory, String action, String event, String requestData, String userName){
        JSONObject jsonObject =null,jsonObject1=null,jsonTokenData=null;
        JSONObject jsonURLResponse = new JSONObject();
        final String requestType = "financial_detail",loginUserId = userService.getLoginUserID(userName);
        String filter=null, sendURL,result,channel="W",tokenResult=null,
                authTokenID=null,v_tag=null,enitityName=null,data=null,
                resStatus=null;
        long authUserID = 0;
        long refID=0,companyID=0,transactionID=0;
        String companyName = null,webhookURL=null;
        URLConfigDto urlConfigDto = userService.findURLDtlByID(39);

        LosCorpositoryAPI losCorpositoryAPI = new LosCorpositoryAPI();

        module = module+"/"+moduleCategory;
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
            losCorpositoryAPI.setEntered_by(loginUserId);
            losCorpositoryAPI.setLast_entered_by(loginUserId);
            userService.saveCorpositoryAPI(losCorpositoryAPI);
            return jsonObject1.toString();
        }catch(JSONException e){
            e.printStackTrace();
            return userService.getJsonError("-99","Error-JSONException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
        catch (Exception e) {
            e.printStackTrace();
            return userService.getJsonError("-99","Error-Exception",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
        }
    }

    /**initiate request validation**/
    public String funcCheckCorpositoryInitiateRequest(Long refID, Long srID,String entityType,String requestType,String param1){
        HashMap inParam = new HashMap(),outParam;
        String dbError =null;
        JSONObject jsonObject=new JSONObject(), jsonObject1 = new JSONObject();
        inParam.put("refID",refID);
        inParam.put("srID",srID);
        inParam.put("entityType",entityType);
        inParam.put("requestType",requestType);
        inParam.put("param1",param1);

        outParam = userService.callingDBObject("procedure","pack_corpository.proc_init_req_validate",inParam);
        if(outParam.containsKey("error")){
            dbError = (String)outParam.get("error");
            try {
                jsonObject1.put("error_cd","-99");
                jsonObject1.put("error_title","Error");
                jsonObject1.put("error_msg",g_err_msg);
                jsonObject1.put("error_detail",dbError);

                jsonObject.put("status","99");
                jsonObject.put("error_data",jsonObject1);
                return  jsonObject.toString();
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return  (String)outParam.get("result");
    }

    /**Initiate addhar request**/
    private String funcInitiateAadhar(String module, String moduleCategory, String action, String event,String requestData, String userName) {
        String channel = null,entityType=null,mobileNumber = null,sms = "1";
        long refID=0,serialNo=0;
        module = module+"/"+moduleCategory;
        action = module + "/" + action;
        if (requestData.isEmpty()) {
            return "";
        }
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(requestData);
            channel = jsonObject.getString("channel");
            jsonObject1 = jsonObject.getJSONObject("request_data");
            if(jsonObject1.has("refID")){
                refID 		= jsonObject1.getLong("refID");
            }
            if(jsonObject1.has("serialNo")){
                serialNo 	= jsonObject1.getLong("serialNo");
            }
            if(jsonObject1.has("entityType")){
                entityType	= jsonObject1.getString("entityType");
            }
//            if(jsonObject1.has("sms")){
//                sms = String.valueOf(jsonObject1.getLong("sms"));
//            }
        } catch (JSONException e) {
            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
        }
        if (channel.isEmpty()) {
            return userService.getJsonError("-99", "Error!", g_error_msg, "Channel Code not Found.", "99", channel, action, requestData, userName, module, "U");
        }
        userService.saveJsonLog(channel, "req", action, requestData, userName, module);

        if (refID <=0) {
            return userService.getJsonError("-99", "Error!", g_error_msg, "refID not found", "99", channel, action, requestData, userName, module, "U");
        }
        if (serialNo <=0) {
            return userService.getJsonError("-99", "Error!", g_error_msg, "serialNo not found", "99", channel, action, requestData, userName, module, "U");
        }
        if (entityType ==null){
            return userService.getJsonError("-99", "Error!", g_error_msg, "entityType not found", "99", channel, action, requestData, userName, module, "U");
        }
        Utility.print("11-2");

        String mobile =null;
        Long inquiryCode = null;
        HashMap inParam = new HashMap(), outParam=null;
        String dbResult = null;


        inParam.put("refID",refID);
        inParam.put("serialNo",serialNo);
        inParam.put("entityType",entityType);

        outParam = userService.callingDBObject("procedure", "pack_healthcheck_common.proc_aadhar_init_req_validate", inParam);

        if (outParam.containsKey("error")) {
            String dbError = (String) outParam.get("error");
            return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
        }
        dbResult = (String)outParam.get("result");
        Utility.print("dbResult:\n"+dbResult);
        if(!dbResult.equals("success")){
            userService.saveJsonLog(channel,"res",action,dbResult,userName,module);
            return dbResult;
        }
        outParam = userService.callingDBObject("procedure", "pack_healthcheck_common.proc_get_lead_info", inParam);
        if (outParam.containsKey("error")) {
            String dbError = (String) outParam.get("error");
            return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
        }
        mobile 		= (String)outParam.get("mobile");
        Utility.print("mobile: "+mobile);
        Utility.print("inquiryCD: "+(String)outParam.get("inquiryCode"));

        inquiryCode = Long.parseLong((String)outParam.get("inquiryCode"));
        Utility.print("fetched mobile:"+mobile);
        Utility.print("fetched inquiryCode:"+inquiryCode);

        //generate UUID for unique ID
        UUID uuid = UUID.randomUUID();
        //set unique id details
        UniqueID_dtl uniqueID_dtl = new UniqueID_dtl();
        String userID = userService.getuniqueId();
        uniqueID_dtl.setUserid(userID);
        uniqueID_dtl.setRef_inquiry_id(inquiryCode);
        uniqueID_dtl.setLead_id(refID);
        uniqueID_dtl.setSr_cd(serialNo);
        uniqueID_dtl.setEntity_type(entityType);
        uniqueID_dtl.setStatus("P");
        uniqueID_dtl.setInitiated_req(requestData);

        //get API Details
        URLConfigDto urlConfigDto = userService.findURLDtlByID(6);
        ResponseEntity < String > result = null;
        try {
            Utility.print("userid:"+userID);
            Utility.print("mobile:"+mobile);
            Utility.print("sms:"+sms);

            if (!urlConfigDto.getKey().isEmpty()) {
                try {
                    String apiRes = null;
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("authkey", urlConfigDto.getKey());
                    MultiValueMap < String,String > map = new LinkedMultiValueMap < String,String > ();
                    map.add("userId", userID);
                    map.add("mobile", mobile);
                    map.add("sms", sms);
                    HttpEntity < MultiValueMap < String,
                            String >> request = new HttpEntity < MultiValueMap < String,
                            String >> (map, headers);
                    result = restTemplate.postForEntity(urlConfigDto.getUrl(), request, String.class);
                    //set result as response
                    apiRes = result.getBody();
                    Utility.print("apiResponse:\n"+apiRes);
                    uniqueID_dtl.setUrl_res(result.getBody());
                    if (result.getStatusCode().equals(HttpStatus.OK)) {
                        String lstatus = null;
                        try {
                            JSONObject jsonObject = new JSONObject(apiRes);
                            lstatus = jsonObject.getString("status");
                            //set status
                            uniqueID_dtl.setReq_status(lstatus);
                            if (lstatus.equals("200")) {
                                try {
                                    jsonObject2.put("status", "0");
                                    jsonObject1.put("transactionId", jsonObject.getString("transactionId"));
                                    jsonObject1.put("url", jsonObject.getString("url"));
                                    jsonObject2.put("response_data", jsonObject1);
                                    //set transaction Details
                                    uniqueID_dtl.setTransaction_id(jsonObject.getString("transactionId"));
                                    userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
                                    //save the details
                                    userService.saveUniqueIDDtl(uniqueID_dtl);
                                    return jsonObject2.toString();
                                } catch (JSONException e) {
                                    return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
                                }
                            } else {
                                return userService.getJsonError("-99", "Error!", g_error_msg,"API call status:"+lstatus, "99", channel, action, requestData, userName, module, "U");
                            }
                        } catch (JSONException e) {
                            return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
                        }
                    } else {
                        return userService.getJsonError("-99", "Error!", g_error_msg, "Success response code not found.", "99", channel, action, requestData, userName, module, "U");
                    }
                } catch (Exception e) {
                    return userService.getJsonError("-99", "Error!", g_error_msg, "Success response code not found.", "99", channel, action, requestData, userName, module, "E");
                }
            }else {
                return userService.getJsonError("-99","URL Detail not found",g_error_msg,"API key not found","99",channel,action,requestData,userName,module,"U");
            }
        } catch (Exception e) {
            return userService.getJsonError("-99","Exeption Error",e.getMessage(),e.getMessage(),"99",channel,action,requestData,userName,module,"E");

        }
    }

    /**adadhar img/xml download**/
    public HttpServletResponse aadharDataDownload( String module,String moduleCategory,String userName,String action,String event,
                                                   HttpServletResponse response,String fileType,String download,String requestBody, String subEvent,String subEvent1) throws IOException{
        JSONObject jsonRequest = null;
        JSONArray jsonArray = null;
        String channel = "W";
        String patternDate = "yyyyMMdd";
        String patternTime = "HHmmssSS";
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patternDate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patternTime);
        String date = simpleDateFormatDate.format(new Date());
        String time = simpleDateFormatTime.format(new Date());
        String transactionID = null, fileName = null, extension = null;
        Blob fileBlob = null;

        module = module + "/" + moduleCategory;
        action = action + "/" + event + "/" + subEvent;
        try {
            int totalTranID = 0;
            jsonRequest = new JSONObject(requestBody);
            Utility.print("Actual Request:" + jsonRequest.toString());
            jsonArray = jsonRequest.getJSONObject("request_data").getJSONArray("transactionID");
            totalTranID = jsonArray.length();
            InputStream inputStream = null, zipInputStream = null;
            userService.saveJsonLog(channel, "req", action, requestBody, userName, module);
            UniqueIDDtlDto uniqueIDDtlDto;
            if (totalTranID == 1) {
                /**for single tranID send actual document**/
                transactionID = jsonArray.getString(0);
                uniqueIDDtlDto = userService.findByTransactionID(transactionID);
                Blob bdata = null;
                if (fileType.equals("image")) {
                    response.setContentType("images/jpeg");
                    extension = ".jpeg";
                    bdata = uniqueIDDtlDto.getImg();
                    if (bdata==null) {
                        userService.getJsonError("-99", "Aadhar detail fetch error", "Data not found", "Data not found", "99", channel, action, requestBody, userName, module, "U");
                    } else {
                        fileBlob = new SerialBlob(bdata);
                    }
                }else {
                    response.setContentType("text/xml");
                    extension = ".xml";
                    bdata = uniqueIDDtlDto.getXmlfile();
                    if (bdata == null) {
                        userService.getJsonError("-99", "Aadhar detail fetch error", "Data not found", "Data not found", "99", channel, action, requestBody, userName, module, "U");
                    } else {
                        fileBlob = new SerialBlob(bdata);
                    }
                }
                fileName = "aadhar_"+fileType+"_"+date+time+extension;
                Utility.print("download flag:"+download);
                if (download.equalsIgnoreCase("yes")){
                    Utility.print("download flag:"+download);
                    response.setHeader("Content-Disposition", String.format("attachment; filename="+fileName+""));
                }else {
                    response.setHeader("Content-Disposition", String.format("inline; filename="+fileName+""));
                }
                inputStream = fileBlob.getBinaryStream();
                try {
                    FileCopyUtils.copy(inputStream, response.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (totalTranID > 1) {
                String zipFilePath = Utility.LOCAL_PATH + Utility.SEPERATOR + "aadharDocuments.zip";
                FileOutputStream fos = null;
                ZipOutputStream zos = null;
                /**for multiple docUUID send files in archive format**/
                try {
                    File zipFile = new File(zipFilePath);
                    fos = new FileOutputStream(zipFile);
                    zos = new ZipOutputStream(fos);
                    for (int i = 0; i < totalTranID; i++) {
                        transactionID = jsonArray.getString(i);
                        Utility.print("loop transactionID:" + transactionID);
                        uniqueIDDtlDto = userService.findByTransactionID(transactionID);
                        Blob bdata = null;
                        if (fileType.equals("img")) {
                            extension = ".jpeg";
                            bdata = uniqueIDDtlDto.getImg();
                            if (bdata == null) {
                                userService.getJsonError("-99", "Aadhar detail fetch error", "Data not found", "Data not found", "99", channel, action, requestBody, userName, module, "U");
                            } else {
                                fileBlob = new SerialBlob(bdata);
                            }
                        } else {
                            extension = ".xml";
                            bdata = uniqueIDDtlDto.getXmlfile();
                            if (bdata == null) {
                                userService.getJsonError("-99", "Aadhar detail fetch error", "Data not found", "Data not found", "99", channel, action, requestBody, userName, module, "U");
                            } else {
                                fileBlob = new SerialBlob(bdata);
                            }
                        }
                        fileName = "file"+(i+1)+"_"+date+time+extension;
                        inputStream = fileBlob.getBinaryStream();
                        /**write to zip file**/
                        Utility.writeToZipFile(inputStream, fileName, zos);
                    }
                    fileName = "aadhar_"+fileType+"_collection";
                    zos.close();
                    fos.close();
                    zipInputStream = new FileInputStream(zipFile);
                    response = userService.startFileDownload(response, zipInputStream, fileName);
                    zipInputStream.close();
                    if (zipFile.delete()) {
                        Utility.print(fileName + " file deleted from server");
                    } else {
                        zipFile.deleteOnExit();
                        Utility.print(fileName + " will be delete on exit");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Utility.print(fileName + "file is ready for download.");
            userService.saveJsonLog(channel, "res", action, "http status:" + response.getStatus(), userName, module);
        }catch (JSONException e) {
            userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestBody, userName, module, "E");
            response.sendError(500);
            e.printStackTrace();
        } catch (SerialException e) {
            userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestBody, userName, module, "E");
            response.sendError(500);
            e.printStackTrace();
        } catch (SQLException e) {
            userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestBody, userName, module, "E");
            response.sendError(500);
            e.printStackTrace();
        }
        return response;
    }

    public String funcCommonFileUpload(String module, String moduleCategory, String action, String event, String userName, Long id,  List<MultipartFile> files, String metaData) {
        String requestData = "MULTIPART_FORM_DATA", response = null, channel = "W", exceptionMessage =null;
        String userID = userService.getLoginUserID(userName);
        int fileCount = 0;

        JSONObject jsonObject, jsonObject1, jsonObject2;
        //JSONArray jsonArray = new JSONArray();
        jsonObject = new JSONObject();
        jsonObject1 = new JSONObject();
        jsonObject2 = new JSONObject();
//        jsonMetaData = new JSONObject();
        // common parameter
        String fileName = null, filePwd = null, fileExt = null, remarks = null, password = null, blobID = null,
                docID = null;

        //lead/document/sanction/upload
        //{module}/{moduleCategory}/action/event
        module = module + "/" + moduleCategory;
        action = action + "/" + event;

        if (files == null || files.isEmpty() || files.get(0).getSize() <= 0) {
            return userService.getJsonError("-99", "Not found any file.", "File Not Found.",
                    "Not found any file to be upload.", "99", channel, action, requestData, userName, module, "U");
        }

        fileCount = files.toArray().length;
        if (fileCount>1){
            return userService.getJsonError("-99", "Only one file can be accept.", "Only one file can be accept.",
                    "Only one file can be accept.", "99", channel, action, requestData, userName, module, "U");
        }

        try {
            jsonObject = new JSONObject(metaData);
            Utility.print("Additional info(MetaData):" + jsonObject.toString());
            // save request data
            userService.saveJsonLog(channel, "req", action, requestData, userName, module);
            MultipartFile file = (MultipartFile)files.get(0) ;
            switch(action){
                case "termsheet/upload":
                    blobID = file.getOriginalFilename();
                    Utility.print("file going to be update for TermSheet:"+blobID);
                    //update
                    userService.updateTermsheetFile(id,file.getBytes(),userID);
                    jsonObject1.put("status","success");
                    jsonObject1.put("message","TermSheet updated");

                    Utility.print("TermSheet updated");
                    break;
                case "sanction/upload":
                    blobID = file.getOriginalFilename();
                    Utility.print("file going to be update for Sanction:"+blobID);
                    //update
                    userService.updateSanctionFile(id,file.getBytes(),userID);
                    jsonObject1.put("status","success");
                    jsonObject1.put("message","Sanction updated");

                    Utility.print("Sanction updated");
                    break;
                default:
                    return "INVALID_CASE:"+action;
            }
            jsonObject2.put("status", "0");
            jsonObject2.put("response_data", jsonObject1);
            response = jsonObject2.toString();
            userService.saveJsonLog(channel, "res", action, response, userName, module);
            return response;
        }catch(JSONException e){
            e.printStackTrace();
            return userService.getJsonError("-99", "JSONException..!", g_error_msg, e.getMessage(), "99",
                    channel, action, requestData, userName, module, "E");
        }
        catch(Exception e){
            e.printStackTrace();
            exceptionMessage = e.getMessage();
            exceptionMessage = exceptionMessage==null?"0":exceptionMessage;
            return userService.getJsonError("-99", "Failed to upload", g_error_msg,
                    exceptionMessage,"99", channel, action, requestData, userName,
                    module, "E");
        }

    }
}
