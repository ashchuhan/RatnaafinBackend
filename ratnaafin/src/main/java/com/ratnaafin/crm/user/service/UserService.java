package com.ratnaafin.crm.user.service;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Blob;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.ratnaafin.crm.user.model.*;
import org.json.JSONObject;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.ratnaafin.crm.user.dao.CrmLeadMstDao;
import com.ratnaafin.crm.user.dto.*;
import com.ratnaafin.crm.user.model.DocUploadBlobDtl;
import com.ratnaafin.crm.user.model.DocUploadDtl;

public interface UserService {
    UserDto save(UserDto user);
    List<UserDto> findAll();
    void delete(String userName);
    void updateLoginAttempt(String username, boolean flag);
    void saveLoginDetails(LoginDetails login);
    UserDto findByUserName(String userName);
    List<String> getTokens(String clientId, String userName);
    String revokeToken(String tokenId);
    String revokeRefreshToken(String tokenId);
    String updatePassword(UserDto userDto);
    UserDto createProfile(UserDto userDto);
    KeyPairGeneratorDto KeyPairGenerator();
    long createProfile(UserDto userDto,String Role);
    User_key_detailDto UserKeyProfileSave(String ServerUsername,String Username,String mac_id,String client_id,String host_name,String os_name);
    User_key_detailDto findByUserID(long UserID);
    String Decryptdata(String ClientID,String RequestData);
    String Encryptdata(String UserName,String ResponseData);
    PublicKey loadPublicKeyFile(String data);
    PrivateKey loadPrivateKeyFile(String data);
    CRMAppDto findAppByID(long ID);
    CRMLoginDto findUserByMobile(String mobile);
    CRMLoginDto findUserByEmail(String email);
    String func_get_result_val(String a,String b,String c);
    String func_get_data_val(String a,String b,String c);
    boolean func_check_pass(String rawpassword,String enpassword);
    void saveJsonLog(AllJsonLog allJsonLog);
    void saveAPIErrorLog(APIErrorLog errorLog);
    URLConfigDto findURLDtlByID(long id);
    String sentOTP(long id);
    boolean sentEmail(long id);
    UniqueIDDtlDto findByTransactionID(String transactionID);
    InquiryMstDto findByInquiryID(long ID);
    void saveUniqueIDDtl(UniqueID_dtl uniqueIDDtl);
    Map<String,String> parseUrlFragment(String url);
    void updateWebhookStatus(String transactionID, String webhookStatus, String status, String webhookRes, Blob img, Blob xmlfile,String downloadStatus);
    OAuth2Authentication readAuth(String tokenId);
    void crmSetLoginPassword(String mobile, String user_password);
    String func_get_pass(String rawpassword);
    void saveJsonLog(String ls_channel, String ls_flag, String ls_action, String ls_req_res , String ls_user, String module);
    void saveErrorLog(String ls_channel, String ls_action, String ls_req_res, String ls_user ,String ls_error_msg,String module);
    String getJsonError(String error_cd,String error_title,String error_msg, String error_detail,String status ,String channel,String action,String request,String user,String module,String flag);
    void savePancardApiLog(PancardApiDtl pancardApiDtl);
    int isPancardExist(long ref_inquiry_id,String pancardno);
    void saveOtpApiDtl(OtpApiDtl otpApiDtl);
    String getInquiryQueKeyValue(Long ref_inquiry_id,String lable);
    int updateInquiryStatus(long inquiryId,String status);
    int assignTeamLead(long inquiryId,long teamLeadID);
    int assignTeamMember(long inquiryId,long teamMemberID,String status);
    int updateInquiryPriority(long inquiryId,String priority,String leadGenerate);
    void saveDoc(testBlob testBlob);
    testBlob findbyDocumentID(String id);
    void saveDocument(DocUploadDtl docUploadDtl);
    void saveDocumentLob(DocUploadBlobDtl docUploadBlobDtl);
    CRMUserMstDto getCRMUsersDtl(String mobile,String user_flag);
    void saveCRMUsersLoginHistory(CRMUsersLoginHis crmUsersLoginHis);
    String getuniqueId();
    CRMUsersLoginHisDto getCRMUsersLoginHistory(String refId);
    void updateOTPVerifyStatus(String otpFlag,String resStatus,String resData,String refId);
    JSONObject getUsersDetails(CRMUserMst crmUserMst);
    String func_get_base_url(String pattern);
    void savePerfiosReqResDtl(PerfiosReqResDtl perfiosReqResdtl);
    PerfiosReqResDto findByPerfiosTransactionID(String transactionID);
    void updatePerfiosWebhookStatus(String transactionID, String webhookStatus, String status, String webhookRes, Blob zip, Blob xlsfile,String jsonfile, String downloadStatus, String remarks);
    String encryptReqRes(UserService userService,String requestData);
    String deleteDocument(Long refID,Long docID);
    DocUploadBlobDtl findDocByUUID(String uuid);
    int updateDocStatus(Long inquiryId,Long docID,String status,String remarks);
    int getDocumentCnt(Long refID,Long docID);
    void updateOTPFlag(String transaction_id,String otpFlag,String verifyResponseData);
    void saveGSTApiLog(CRMGstApiDtl crmGstApiDtl);
    void savePincodeApiLog(CRMPincodeApiDtl crmPincodeApiDtl);
    //    List<DocUploadDtl> getDocListByDocId(long inquiryId, long docId);
    List<DocUploadDtl> getDocListByDocId(long leadId, long srId, String entityType,long docId);
    DocUploadBlobDtl findDocBlobByUUID(String uuid);
    List<CrmDocumentMst> getDocMstListByDocType(String docType);
    SysParaMst getParaVal(String comp_cd , String branch_cd , long para_cd);
    HashMap<String, String> callingDBObject(String objectType,String objectName, HashMap args);
    CrmLeadMst findLeadByID(long inquiry_tran_cd);
    String deleteUploadedDocumentByLeadID(Long lead_id,Long doc_id);
    HttpServletResponse startFileDownload(HttpServletResponse response, InputStream is,String fileName);
    boolean isMiddlewareRequest(String signature);
    void updateCAMStatus(long serialNo, long leadID, Blob camData, Date modifiedDate, String status,String enteredBy);
    CRMCAMDtlDto getCAMData(long leadID,long serialNo);
    List<CrmMiscMst> findByCategory(String categoryCode);
    String getGstNumberById(Long refId);
    DocUploadBlobDtl findDocByUUIDBankID(String uuid,Long bankLineID);
    void saveCorpositoryAPI(LosCorpositoryAPI losCorpositoryAPI);
    String getEntityNameById(Long refId);
    //added on 30/04/2021 SANJAY
    OtpVerificationDtl findOTPDetailByTokenID(String tokenID);
    List<OtpVerificationDtl> findPendingOTPLinkDetail();
    void updateOTPLinkSentStatus(String token_id, String status,String remarks,String shortedURL);
    //added on 30/04/2021 END

    //EquifaxAPILogDao-modified and added on: 11/06/2021
    List<EquifaxAPILog> findEquifaxPendingLinkRecord();
    void updateEqfxOTPLinkStatus(String token_id, String status,String remarks,String shortedURL);
    EquifaxAPILog findEquifaxDetailByTokenId(String tokenID);
    void updateEquifaxAPILog(String token_id,String req_status, String res_status,String res_data,String errorCode,String errorDesc);
    //url shortner method
    String getShortURL(String transactionID,String url);
    //get formatted string from date string
    String getDateFormattedString(String dateStr/*date string*/,String pattern/*String return pattern*/);
    String getLoginUserID(String userName);
    //on date:24/06/2021
    void saveApiWebhook(ApiWebhookActivity apiWebhookActivity);
    List<ApiWebhookActivity> findWebhookProcess();
    void updateWebhookProcess(Long tranCd,String processStatus,String processResponse,String webhookFlag,String remarks);
    List<PerfiosReqResDtl> findPendingDocumentProcess();
    //added on dt:13/07/2021
    void updateTermsheetFile(Long tranCD,byte[] file,String userName);
    void updateSanctionFile(Long tranCD, byte[] file, String userName);
    //added on dt:15/07/2021
    LeadSanctionDtl findSanctionDtlById(Long id);
    LeadTermSheetDtl findTermSheetDtlById(Long id);
}
