package com.ratnaafin.crm.admin.controller;

import com.ratnaafin.crm.user.controller.UserController;
import com.ratnaafin.crm.user.model.ApiWebhookActivity;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/los/webhooks") //: /los/webhooks/gstInfoWebhook //
@Controller
public class AdminController {
	private final String className = this.getClass().getSimpleName();
	public  String user_error = "Error(u):";
	public  String sys_error = "Error(e):";

	@Autowired
	private UserService userService;
	@Autowired
	public EntityManager entityManager;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	UserController userController = new UserController();

	private String userError(String msg){ return user_error+msg; }
	private String sysError(String msg){ return sys_error+msg; }

	@RequestMapping(method = RequestMethod.POST,value = "/{action}",produces = {"application/json","application/json"})
	public String funcInternalFetcher(	@PathVariable(name = "action") String action,
										  @RequestBody String requestData)
	{
		JSONObject jsonObject;
		String result = null,trxId=null,vendor=null;
		Map<String,String> data;
		action = action.toLowerCase();
		System.out.println(action);
		ApiWebhookActivity apiWebhookActivity = new ApiWebhookActivity();
		try {
			//webhook log insert
			insertWebhookLog(action,requestData,"AdminController");
			switch (action) {
				case "gstinfowebhook":
				case "itruploadwebhook":
				case "gstuploadwebhook":
					data = userService.parseUrlFragment(requestData);
					vendor = "perfios";
					trxId = data.get("perfiosTransactionId");
					break;
				case "statementwebhook": //statementwebhook
					jsonObject = new JSONObject(requestData);
					vendor = "perfios";
					trxId = jsonObject.getString("perfiosTransactionId");
					break;
				case "placeorderwebhook":
					jsonObject = new JSONObject(requestData);
					vendor = "corpository";
					trxId  = jsonObject.getString("reference-id");
					break;
			}
			apiWebhookActivity.setVendor(vendor);
			apiWebhookActivity.setWebhook_nm(action);
			apiWebhookActivity.setRef_trx_id(trxId);
			apiWebhookActivity.setWebhook_data(requestData);
			apiWebhookActivity.setWebhook_flag("P");
			apiWebhookActivity.setProcess_status("P");
			apiWebhookActivity.setProcess_response("");
			userService.saveApiWebhook(apiWebhookActivity);
			return "{\"message\":\"success\"}";
		}catch (Exception e){
			HashMap inParam = new HashMap(),outParam;
			inParam.put("error_msg",e.getMessage());
			inParam.put("remarks","Error in AdminController for webhook:"+action);
			inParam.put("obj_name","AdminController.java");
			inParam.put("error_flag","E");
			outParam    =   userService.callingDBObject("procedure","proc_insert_error_log",inParam);
			e.printStackTrace();
			return e.getMessage();
		}
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
			e.printStackTrace();
			return;
		}catch (Exception e){
			e.printStackTrace();
			return;
		}
	}
}
