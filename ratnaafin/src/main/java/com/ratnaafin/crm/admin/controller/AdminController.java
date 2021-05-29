package com.ratnaafin.crm.admin.controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.persistence.EntityManager;
import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.controller.UserController;
import com.ratnaafin.crm.user.service.UserService;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/los/webhooks") //: /los/webhooks/gstInfoWebhook
@Controller
public class AdminController {
	private String g_error_msg = "Something went wrong please try again.";
	private String g_status = null;
	private String g_err_cd = null;
	private String g_err_title = null;
	private String g_err_dtl = null;
	public  String user_error = "Error(u):";
	public  String sys_error = "Error(e):";


	@Autowired
	private UserController userController;
	@Autowired
	private UserService userService;
	@Autowired
	private JdbcTemplate jdbcTemplate;

//	UserController userController = new UserController();

	private String userError(String msg){ return user_error+msg; }
	private String sysError(String msg){ return sys_error+msg; }

	@RequestMapping(method = RequestMethod.POST,value = "/{action}",produces = {"application/json","application/json"})
	public String funcInternalFetcher(	@PathVariable(name = "action") String action,
										  @RequestBody String requestData)
	{
		String result = null;
		action = action.toLowerCase();
		System.out.println(action);
		//webhook log insert
		insertWebhookLog(action,requestData,"AdminController");

		switch (action) {
			case "gstinfowebhook":
				result = funcGstInfoWebhook(requestData);
				break;
			case "gstuploadwebhook":
				result = funcGstUploadWebhook(requestData);
				break;
			case "itruploadwebhook":
				result = funcItrUploadWebhook(requestData);
				break;
			case "statementwebhook": //statementwebhook
				result = funcStatementWebhook(requestData);
				break;
			case "placeorderwebhook":
				result = funcPlaceOrderWebhook(requestData);
				break;
		}
		return result;
	}

	//GST Info Webhook
	public String funcGstInfoWebhook(String requestdata){
		String perfiosTransactionId = null,status = null,errorCode=null,message=null;

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
				return userController.gstInfoRetrieve(perfiosTransactionId,11,requestdata);
			}
		}else {
			userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",errorCode+":"+message);
		}
		return "success";
	}

	//gst upload webHook
	public String funcGstUploadWebhook(String requestdata){
		String perfiosTransactionId = null,status = null,errorCode=null,message=null;
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
			fetchStatus = userController.fetchDocumentStatus(perfiosTransactionId,19,"GST_ITR_UPLOAD_STATUS");
			Utility.print(fetchStatus);
			if ((status.equals("COMPLETED")))
			{
				String jsonrequest = "{\"request_data\":{\"perfiosTransactionId\":\""+perfiosTransactionId+"\"},\"channel\":\"W\"}";
				//this method will update document status by calling perfios status api.
				userController.gstInfoRetrieve(perfiosTransactionId,20,requestdata);
			}else {
				userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",status+":"+errorCode+":"+message);
			}
		}
		return "success";
	}

	//itr upload webHook
	public String funcItrUploadWebhook(String requestdata){
		String perfiosTransactionId = null,status = null, errorCode=null,message=null;;

    /*requestdata
    	txnId:      Perfios Transaction Id
    	status:     Status of the transaction. See below for possible values (COMPLETED|ERROR|REPORT_GENERATION_FAILED)
    	errorCode:  Perfios Transaction error code
    	message:    Perfios Transaction status message
    */

		/**WEBHOOK FORMAT**/
		/*clientTransactionId=ratnaaFin_It_upload
		 * &errorCode=ERROR_TIMED_OUT
		 * &message=Transaction+Expired+due+to+inactivity
		 * &perfiosTransactionId=PIETLFJNBGT3ADUWJRH48
		 * &status=EXPIRED*/
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
			fetchStatus = userController.fetchDocumentStatus(perfiosTransactionId,28,"GST_ITR_UPLOAD_STATUS");
			if ((status.equals("COMPLETED")))
			{
				String jsonrequest = "{\"request_data\":{\"perfiosTransactionId\":\""+perfiosTransactionId+"\"},\"channel\":\"W\"}";
				return userController.gstInfoRetrieve(perfiosTransactionId,30,requestdata);

			}else {
				//if(status.equals("ERROR") || status.equals("REPORT_GENERATION_FAILED")||status.equals("CANCELLED")||status.equals("REJECTED")){
				userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",status+":"+errorCode+":"+message);
			}
		}
		return "success";
	}

	//statement webhook
	public String funcStatementWebhook(String requestdata) {
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
			e.printStackTrace();
			return e.getMessage();
		}
//		Map<String, String> data = userService.parseUrlFragment(requestdata);
//		perfiosTransactionId = data.get("perfiosTransactionId");
//		clientTransactionId = data.get("clientTransactionId");
//		//for idle case
//		status = data.get("status");
//		if (data.containsKey("userStatus")) {
//			userStatus = data.get("userStatus");
//		}
//		if(data.containsKey("errorCode")){
//			errorCode = data.get("errorCode");
//		}
//		if(data.containsKey("message")){
//			message = data.get("message");
//		}
		if(perfiosTransactionId==null || perfiosTransactionId.isEmpty()){
			return "perfiosTransactionId not found.";
		}
		if (status!=null && status.equalsIgnoreCase("COMPLETED")) {
			return userController.retrieveStatementReport(perfiosTransactionId, clientTransactionId, requestdata);
		}else{
			userService.updatePerfiosWebhookStatus(perfiosTransactionId,"200 OK","F",requestdata,null,null,null,"F",status+":"+errorCode+":"+message);
		}
		return "success";
	}

	//place order webhook
	public String funcPlaceOrderWebhook(String requestData){
		Connection connection;
		CallableStatement cs;
		try{
			connection = jdbcTemplate.getDataSource().getConnection();
			connection.setAutoCommit(false);
			cs = connection.prepareCall("{ call PACK_CORPOSITORY.PROC_UPDATE_CORPOSITORY_WEBHOOK(?) }");

			cs.setString(1, requestData);
			cs.execute();
			connection.close();
			cs.close();
		}catch (SQLException e){
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return  "success";
	}

	public void insertWebhookLog(String requestType,String requestData,String remarks){
		Connection connection;
		CallableStatement cs;
		try{
			connection = jdbcTemplate.getDataSource().getConnection();
			connection.setAutoCommit(false);
			cs = connection.prepareCall("{ call PROC_INSERT_WEBHOOK_LOG(?,?,?) }");

			cs.setString(1, requestType);
			cs.setString(2, requestData);
			cs.setString(3, remarks);
			cs.execute();
			connection.close();
			cs.close();
		}catch (SQLException e){
			return;
//			e.printStackTrace();
		}catch (Exception e){
			return;
//			e.printStackTrace();
		}
	}


}
