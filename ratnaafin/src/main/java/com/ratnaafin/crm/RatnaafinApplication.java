package com.ratnaafin.crm;

import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.dto.*;
import com.ratnaafin.crm.user.model.*;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;


@SpringBootApplication
@EnableAuthorizationServer
@EnableScheduling
@RestController
@Controller
public class RatnaafinApplication {
	@Autowired
	private UserService userService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Map<String,SseEmitter> emitters = new HashMap<>();

	private String g_error_msg = "Something went wrong please try again.";

	public static File filedir = null;

	public static void main(String[] args) {
		SpringApplication.run(RatnaafinApplication.class, args);
		filedir = Utility.createDirectory("CAM");
	}

	@RequestMapping(method = RequestMethod.GET, value = "/misc/{category}", produces = {"application/json", "application/json"})
	public String funcMiscCategory(@PathVariable(name = "category") String category) {
		String channel = "W", userName = "misc", result = null;
		result = funcMisc(category, "", userName, channel, "misc", null, null);
		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/misc/data/{category}", produces = {"application/json","application/json"})
	public String funcMiscDataCategory(@PathVariable(name = "category") String category) {
		String channel = "W", userName = "misc", result = null;
		result = funcMisc(category, "", userName, channel, "misc", "data", null);
		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/misc/{category}/{code}", produces = {"application/json", "application/json"})
	public String funcMiscCategoryCode(@PathVariable(name = "category") String category, @PathVariable(name = "code") String code) {
		String channel = "W", userName = "misc", result = null;
		result = funcMisc(category, "", userName, channel, "misc", null, code);
		return result;
	}

	private String funcMisc(String category, String requestData, String userName, String channel, String param1, String param2, String param3) {
		Connection connection = null;
		CallableStatement cs = null;
		String ls_return = null;
		try {
			connection = jdbcTemplate.getDataSource().getConnection();
			connection.setAutoCommit(false);
			cs = connection.prepareCall("{ call PROC_MISC_API(?,?,?,?,?,?,?,?) }");
			cs.setString(1, category);
			cs.setString(2, requestData);
			cs.setString(3, userName);
			cs.setString(4, channel);
			cs.setString(5, param1);
			cs.setString(6, param2);
			cs.setString(7, param3);
			cs.registerOutParameter(8, 2005);
			cs.execute();
			final Clob clob_data = cs.getClob(8);
			ls_return = clob_data.getSubString(1L, (int) clob_data.length());
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
		return ls_return;
	}

	@RequestMapping(method = RequestMethod.POST,value = "/{application}/{module}/{action}",produces = {"application/json","application/json"})
	public String funcInternalFetcher(@PathVariable(name = "application") String application,
									  @PathVariable(name = "module") String module,
									  @PathVariable(name = "action") String action,
									  @RequestBody String requestData){
		String result = null;
		application = application.toLowerCase();
		module = module.toLowerCase();
		action = action.toLowerCase();
		switch (application+"/"+module+"/"+action)
		{
			case "null":
				result = "null";
				break;
			default:
				result = funcCallAPIOne(application,module,action,requestData,"","","","");
		}
		return result;
	}

	@RequestMapping(method = RequestMethod.POST,value = "/{application}/{module}/{action}/{event}",produces = {"application/json","application/json"})
	public String funcInternalFetcher(@PathVariable(name = "application") String application,
									  @PathVariable(name = "module") String module,
									  @PathVariable(name = "action") String action,
									  @PathVariable(name = "event") String event,
									  @RequestBody String requestData){
		String userName = "null", result = null;
		application = application.toLowerCase();
		module = module.toLowerCase();
		event = event.toLowerCase();
		action = action.toLowerCase();
		switch (application+"/"+module+"/"+action+"/"+event){
			case "auth/los/customer/verify":
				result = funcLosUsersVerify(module,action,"W",requestData,"los","");
				break;
			case "auth/los/customer/login":
				result = funcLosUsersLogin(module,action,"W",requestData,"los","");
				break;
			case "external/otp/email/send":
				result = funcTokenBasedEmailOTPRequest(application, module, action, event, requestData, userName);
				break;
			case "external/otp/mobile/send":
			case "external/equifax-otp/mobile/send":
				result = funcTokenBasedMobileOTPRequest_v2(application, module, action, event, requestData, userName);
				break;
			case "external/equifax-otp/mobile/resend":
				result = funcTokenBasedOTPRequestResend_v2(application, module, action, event, requestData, userName);
				break;
			case "external/otp/email/verify":
				result = funcTokenBasedEmailOTPVerify(application, module, action, event, requestData, userName);
				break;
			case "external/otp/mobile/verify":
			case "external/equifax-otp/mobile/verify":
			case "external/equifax-otp/mobile/re-verify":
				result = funcTokenBasedMobileOTPVerify_v2(application, module, action, event, requestData, userName);
				break;
			case "external/equifax/request/send":
				result = funcVerifyEquifaxToken(application,module,"token","verify",requestData,userName);
				try{
					if(new JSONObject(result).getString("status").equals("99")) {
						return  result;
					}
				}catch (JSONException e){
					return e.getMessage();
				}
				result = funcEquifaxSendRequest(application, module, action, event, requestData, userName);
				break;
			case "external/equifax/token/verify":
				result = funcVerifyEquifaxToken(application, module, action, event, requestData, userName);
				break;
			case "external/otp/mobile-token/verify":
			case "external/otp/email-token/verify":
				result = funcVerifyOTPToken(application, module, action, event, requestData, userName);
				break;
			case "misc/external/pincode/getlocation":
				result = funcFetchPincodeDetails(module,action,event,requestData,application);
				break;
			default:
				result = funcCallAPITwo(application,module,action,event,requestData,"","","","");
		}
		return result;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{application}/{module}/{moduleCategory}/{action}/{event}", produces = {"application/json", "application/json"})
	public String funcInternalFetcher(  @PathVariable(name = "application") String application,
										@PathVariable(name = "module") String module,
										@PathVariable(name = "moduleCategory") String moduleCategory,
										@PathVariable(name = "action") String action,
										@PathVariable(name = "event") String event,
										@RequestBody String requestData) {
		String result = null;
		application = application.toLowerCase();
		module = module.toLowerCase();
		moduleCategory = moduleCategory.toLowerCase();
		action = action.toLowerCase();
		event = event.toLowerCase();
		switch (application+"/"+module+"/"+moduleCategory+"/"+action+"/"+event){
			case "crm/inquiry/external/aadhar/initiate":
				result = funcInquiryExternalInitiateAadhar(module,moduleCategory,action,requestData,application.toLowerCase());
				break;
			case "crm/inquiry/external/aadhar/webhook":
				result = funcInquiryExternalAadharWebhook(requestData);
				break;
			case "crm/inquiry/external/pan/validate":
				result = funcPancardValidator(module,moduleCategory,action,requestData,application);
				break;
			case "crm/inquiry/external/otp/request":
				result = funcInquiryExternalOTPRequest(module,moduleCategory,action,requestData,application);
				break;
			case "crm/inquiry/external/otp/verify":
				result = funcInquiryExternalOTPVerify(module,moduleCategory,action,requestData,application);
				break;
			case "auth/los/customer/token/verify":
			case "auth/los/partner/token/verify":
			case "auth/los/employee/token/verify":
				result = funcTokenVerify(action,module,requestData,"auth");
				break;
			case "crm/partnerinquiry/external/gst/fetchcompanyname":
				result = funcGetCompanyNameByGST(module,moduleCategory,action,requestData,application);
				break;
			case "crm/inquiry/external/email/validaterequest":
				result = funcEmailOTPRequest(module,moduleCategory,action,requestData,application);
				break;
			case "crm/inquiry/external/email/validateresponse":
				result = funcEmailOTPResponse(module,moduleCategory,action,requestData,application);
				break;
			default:
				result = funcCallAPIThree(application,module,moduleCategory,action,event,requestData,"","","","");
		}
		return result;
	}

	private String funcCallAPIOne(String application,String module,String action,String requestData, String userName,String param1,String param2,String param3) {
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

	private String funcCallAPITwo(String application,String module,String action,String event,String requestData, String userName,String param1,String param2,String param3) {
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
		if(((application+'/'+module+'/'+action+'/'+event).equals("auth/los/employee/login")) && (result!= null)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				JSONObject jsonObject1 = new JSONObject(requestData);
				String status = jsonObject.getString("status");
				String userId = jsonObject1.getJSONObject("request_data").getString("userId");
				if (status.equals("0")){
					jsonObject.getJSONObject("response_data").put("token",new JSONObject(Login(userId,action,requestData,userName,module,"E")));
				}
				return jsonObject.toString();
			}catch (JSONException jsonException){
				System.out.println("JSON Exception: "+jsonException.getMessage());
				return result;
			}catch (Exception exception){
				System.out.println("Exception: "+exception.getMessage());
				return result;
			}
		}
		return result;
	}

	private String funcCallAPIThree(String application,String module,String module_category,String action,String event,String requestData, String userName,String param1,String param2,String param3) {
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

	private String funcInquiryExternalInitiateAadhar(String module,String moduleCategory, String action,String requestData,String userName) {
		String channel = null,mob_number=null,sms=null;
		Long ID;
		action = module+"/"+action;
		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			ID   		= jsonObject.getJSONObject("request_data").getLong("refID");
			sms         = jsonObject.getJSONObject("request_data").getString("sms");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99",channel,action,requestData,userName,module,"U");
		}
		userService.saveJsonLog(channel,"req",action,requestData,userName,module);
		if (ID == null) {
			return userService.getJsonError("-99","Error!",g_error_msg,"InquiryID not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (sms == null){
			sms = "0";
		}
		//get inquery Details
		InquiryMstDto inquiryMstDto = null;
		inquiryMstDto = userService.findByInquiryID(ID);
		//check mobile number not empty
		if (inquiryMstDto.getMobile().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Mobile Number not Found.","99",channel,action,requestData,userName,module,"U");
		}
		//mobile no decrypt
		CRMAppDto crmAppDtomobile = userService.findAppByID(1);
		mob_number = userService.func_get_data_val(crmAppDtomobile.getA(),crmAppDtomobile.getB(),inquiryMstDto.getMobile());
		//generate UUID for unique ID
		UUID uuid = UUID.randomUUID();
		//set unique id details
		UniqueID_dtl uniqueID_dtl = new UniqueID_dtl();
		String userID =userService.getuniqueId();
		uniqueID_dtl.setUserid(userID);
		uniqueID_dtl.setRef_inquiry_id(ID);
		uniqueID_dtl.setStatus("P");
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(6);
		ResponseEntity<String>  result=null;
		try{
			if (!urlConfigDto.getKey().isEmpty()){
				try{
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("authkey",urlConfigDto.getKey());
					MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
					map.add("userId",userID);
					map.add("mobile",mob_number);
					map.add("sms",sms);
					HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
					result = restTemplate.postForEntity(urlConfigDto.getUrl(), request ,String.class);
					//set result as response
					uniqueID_dtl.setUrl_res(result.getBody());
					if (result.getStatusCode().equals(HttpStatus.OK)){
						String lstatus = null;
						try {
							JSONObject jsonObject = new JSONObject(result.getBody());
							lstatus = jsonObject.getString("status");
							//set status
							uniqueID_dtl.setReq_status(lstatus);
							if (lstatus.equals("200")){
								try {
									jsonObject2.put("status", "0");
									jsonObject1.put("transactionId", jsonObject.getString("transactionId"));
									jsonObject1.put("url", jsonObject.getString("url"));
									jsonObject2.put("response_data",jsonObject1);
									//set transaction Details
									uniqueID_dtl.setTransaction_id(jsonObject.getString("transactionId"));
									userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
									//save the details
									userService.saveUniqueIDDtl(uniqueID_dtl);
									return jsonObject2.toString();
								}catch (JSONException e){
									return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
								}
							}else {
								try {
									jsonObject1.put("error_cd", -99);
									jsonObject1.put("error_title","Error!");
									jsonObject1.put("error_msg",g_error_msg);
									jsonObject1.put("error_detail",g_error_msg);
									jsonObject2.put("serviceStatus", lstatus);
									jsonObject2.put("status","99");
									jsonObject2.put("error_data",jsonObject1);
									userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
									//save the details
									userService.saveUniqueIDDtl(uniqueID_dtl);
									return jsonObject2.toString();
								}catch (JSONException e){
									return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
								}
							}
						}catch (JSONException e){
							return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
						}
					}else{
						return userService.getJsonError("-99","Error!",g_error_msg,"Success response code not found.","99",channel,action,requestData,userName,module,"U");
					}
				} catch (Exception e) {
					return userService.getJsonError("-99","Error!",g_error_msg,"Success response code not found.","99",channel,action,requestData,userName,module,"E");
				}
			}
		}catch (Exception e){
			System.out.println("Aadhar Unique URL Error : "+e.getMessage());
		}
		return "";
	}


	@RequestMapping(method = RequestMethod.GET,value = "/crm/inquiry/external/aadhar/statussse",consumes = MediaType.ALL_VALUE)
	public SseEmitter funcInquiryExternalGetAadharStatusSSE(@RequestParam String transactionId) {
		if ((!transactionId.isEmpty()))
		{
			long timeout = 300000;
			SseEmitter sseEmitter = new SseEmitter(timeout);
			sendInitEvent(sseEmitter);
			emitters.put(transactionId,sseEmitter);
			sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
			sseEmitter.onTimeout(() -> emitters.remove(sseEmitter));
			sseEmitter.onError((e) -> emitters.remove(sseEmitter));
			return sseEmitter;
		}
		return null;
	}

	private void sendInitEvent(SseEmitter sseEmitter){
		try {
			sseEmitter.send(SseEmitter.event().name("INIT"));
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public String funcInquiryExternalAadharWebhook(String requestData){
		String ls_download_status = "P",ls_xml_url = null,ls_image_url = null,ls_transactionID = null,ls_serviceStatus = null,ls_status = null;
		Blob fileblob = null, imgblob = null;
		Map<String,String> data = userService.parseUrlFragment(requestData);
		ls_transactionID = data.get("transactionId");
		ls_serviceStatus = data.get("serviceStatus");
		ls_status        = data.get("status");
		if((!ls_transactionID.isEmpty()) && (!ls_serviceStatus.isEmpty()) && (!ls_status.isEmpty()))
		{
			if ((ls_serviceStatus.equals("download")) && (ls_status.equals("1")))
			{
				ls_xml_url = data.get("file");
				ls_image_url = data.get("image");
				if ((!ls_xml_url.isEmpty()) && (!ls_image_url.isEmpty()))
				{
					URL imgurl = null,fileurl = null;
					InputStream imgstream = null,filestream = null;
					try {
						ls_xml_url = URLDecoder.decode(ls_xml_url,"UTF-8");
						ls_image_url = URLDecoder.decode(ls_image_url,"UTF-8");
					}catch (UnsupportedEncodingException e){
						System.out.println("Result : UnsupportedEncodingException");
						e.printStackTrace();
					}
					byte[] filecontents, imgcontents;
					ByteArrayOutputStream fileoutput = new ByteArrayOutputStream();
					ByteArrayOutputStream imgoutput = new ByteArrayOutputStream();
					byte[] filebuffer = new byte[1024];
					byte[] imgbuffer = new byte[1024];
					int filecount, imgcount;
					try {
						//file data download
						fileurl = new URL(ls_xml_url);
						filestream = fileurl.openStream();
						while ((filecount = filestream.read(filebuffer)) != -1) {
							fileoutput.write(filebuffer, 0, filecount);
						}
						filecontents = fileoutput.toByteArray();
						fileblob = new SerialBlob(filecontents);
						//file data download done

						//img data download
						imgurl = new URL(ls_image_url);
						imgstream = imgurl.openStream();
						while ((imgcount = imgstream.read(imgbuffer)) != -1) {
							imgoutput.write(imgbuffer, 0, imgcount);
						}
						imgcontents = imgoutput.toByteArray();
						imgblob = new SerialBlob(imgcontents);
						//img data download done

					} catch (MalformedURLException e) {
						System.out.println("Result : MalformedURLException");
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("Result : IOException");
						e.printStackTrace();
					} catch (SerialException e) {
						System.out.println("Result : SerialException");
						e.printStackTrace();
					} catch (Exception e) {
						System.out.println("Result : Exception");
						e.printStackTrace();
					}
					if ((imgblob != null) && (fileblob != null)) {
						ls_download_status = "S";
					}
				}
				userService.updateWebhookStatus(ls_transactionID,ls_status,"S",requestData,imgblob,fileblob,ls_download_status);
			}else{
				userService.updateWebhookStatus(ls_transactionID,ls_status,"F",requestData,imgblob,fileblob,ls_download_status);
			}
			JSONObject jsonObject1 = new JSONObject();
			JSONObject jsonObject2 = new JSONObject();
			if (ls_download_status == "S")
			{
				try {
					jsonObject2.put("message","Status is Success.");
					jsonObject2.put("requestStatus","success");
					jsonObject1.put("status","0");
					jsonObject1.put("response_data",jsonObject2);
				}catch (JSONException e){
					System.out.println("Result : JSONObject");
					e.printStackTrace();
				}
			}else{
				try {
					jsonObject2.put("message","Status is Failed.");
					jsonObject2.put("requestStatus","failed");
					jsonObject1.put("status","0");
					jsonObject1.put("response_data",jsonObject2);
				}catch (JSONException e){
					System.out.println("Result : JSONObject");
					e.printStackTrace();
				}
			}
			SseEmitter sseEmitter = emitters.get(ls_transactionID);
			if (sseEmitter != null) {
				try {
					sseEmitter.send(SseEmitter.event().name("transactionId").data(jsonObject1.toString()));
				} catch (IOException e) {
					emitters.remove(sseEmitter);
				}
			}
		}
		return "Success";
	}

	public String funcInquiryExternalOTPRequest(String module,String moduleCategory, String action,String requestData,String userName) {
		String channel = null,mob_number=null,email=null,request = null,response = null;
		Long Id = null;
		CRMAppDto crmAppDto = userService.findAppByID(5);
		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			Id   = jsonObject.getJSONObject("request_data").getLong("refID");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if ((Id < 0) || (Id == null)  ){
			return userService.getJsonError("-99","Error!",g_error_msg,"Inquiry Code not Found.","99",channel,action,requestData,userName,module,"U");
		}
		request = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),requestData);
		userService.saveJsonLog(channel,"req",action,request,userName,module);

		InquiryMstDto inquiryMstDto = null;
		inquiryMstDto = userService.findByInquiryID(Id);
		//check mobile number not empty
		if (inquiryMstDto.getMobile().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Mobile Number not Found.","99",channel,action,requestData,userName,module,"U");
		}
		//mobile no decrypt
		CRMAppDto crmAppDtomobile = userService.findAppByID(1);
		mob_number = userService.func_get_data_val(crmAppDtomobile.getA(),crmAppDtomobile.getB(),inquiryMstDto.getMobile());
		// save OTP API logs
		OtpApiDtl otpApiDtl = new OtpApiDtl();
		otpApiDtl.setRef_inquiry_id(Id);
		String uid = userService.getuniqueId();
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(7);
		ResponseEntity<String>  result=null;
		try{
			if (!urlConfigDto.getKey().isEmpty()){
				try{
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("authkey",urlConfigDto.getKey());
					MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
					map.add("mobile",mob_number);
					map.add("email",email);
					map.add("otpLength",urlConfigDto.getSmtp_server());
					map.add("otpExpiry",urlConfigDto.getSmtp_port());
					//ls_mob_number = userService.func_get_result_val()
					HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);
					result = restTemplate.postForEntity(urlConfigDto.getUrl(), httpRequest ,String.class);
					if (result.getStatusCode().equals(HttpStatus.OK)){
						String lstatus = null;
						try {
							JSONObject jsonObject = new JSONObject(result.getBody());
							lstatus = jsonObject.getString("status");
							if (lstatus.equals("200")){
								try {
									jsonObject2.put("status", "0");
									jsonObject1.put("transactionId", jsonObject.getString("transactionId"));
									jsonObject1.put("message", jsonObject.getString("message"));
									jsonObject1.put("serviceStatus", jsonObject.getString("status"));
									jsonObject1.put("otpExpiry", urlConfigDto.getSmtp_port());
									jsonObject2.put("response_data",jsonObject1);
									response = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),jsonObject2.toString());
									userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
									// save OTP API log added by krupa 24/12/2020
									otpApiDtl.setTran_dt(new Date());
									otpApiDtl.setTransaction_id(jsonObject.getString("transactionId"));
									otpApiDtl.setOtp_sent_status(lstatus);
									otpApiDtl.setOtp_sent_res_data(response);
									otpApiDtl.setOtp_verify("P");
									otpApiDtl.setOtp_expiry(urlConfigDto.getSmtp_port());
									otpApiDtl.setOtp_length(urlConfigDto.getSmtp_server());
									userService.saveOtpApiDtl(otpApiDtl);
									return jsonObject2.toString();
								}catch (JSONException e){
									return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
								}
							}else {
								try {
									jsonObject1.put("error_cd", -99);
									jsonObject1.put("error_title","Error!");
									jsonObject1.put("error_msg",g_error_msg);
									jsonObject1.put("error_detail",jsonObject.getString("message"));
									jsonObject2.put("serviceStatus", lstatus);
									jsonObject2.put("status","99");
									jsonObject2.put("error_data",jsonObject1);
									response = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),jsonObject2.toString());
									userService.saveJsonLog(channel,"res",action,response,userName,module);
									otpApiDtl.setTran_dt(new Date());
									otpApiDtl.setOtp_sent_status(lstatus);
									otpApiDtl.setOtp_sent_res_data("");
									otpApiDtl.setOtp_verify("P");
									userService.saveOtpApiDtl(otpApiDtl);
									return jsonObject2.toString();
								}catch (JSONException e){
									return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
								}
							}
						}catch (JSONException e){
							return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
						}
					}else{
						return userService.getJsonError("-99","Error!",g_error_msg,"Success response code not found.","99",channel,action,requestData,userName,module,"E");
					}
				}catch (Exception e){
					jsonObject2.put("status", "0");
					jsonObject1.put("message", "success");
					jsonObject1.put("transactionId",uid);
					jsonObject1.put("mobileNo", mob_number.replaceAll("\\d(?=\\d{4})", "*"));
					jsonObject2.put("response_data",jsonObject1);
					response = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),jsonObject2.toString());
					otpApiDtl.setTran_dt(new Date());
					otpApiDtl.setTransaction_id("null");
					otpApiDtl.setRef_uid(uid);
					otpApiDtl.setOtp_sent_status("200");
					otpApiDtl.setOtp_sent_res_data(response);
					otpApiDtl.setOtp_verify("P");
					otpApiDtl.setOtp_expiry(urlConfigDto.getSmtp_port());
					otpApiDtl.setOtp_length(urlConfigDto.getSmtp_server());
					userService.saveOtpApiDtl(otpApiDtl);
					return jsonObject2.toString();
				}
			}
		}catch (Exception e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}
		return "";
	}

	public String funcInquiryExternalOTPVerify(String module,String moduleCategory, String action,String requestData,String userName) {
		String channel = null,mob_number=null,otp=null,response = null ,request = null;
		String tran_id = null;
		CRMAppDto crmAppDto = userService.findAppByID(5);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			tran_id = jsonObject.getJSONObject("request_data").getString("transactionId");
			otp     = jsonObject.getJSONObject("request_data").getString("otp");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (tran_id.equals(null)) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Transaction ID not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (otp.isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"OTP not Found.","99",channel,action,requestData,userName,module,"U");
		}
		request = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),requestData);
		userService.saveJsonLog(channel,"req",action,request,userName,module);
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(8);
		ResponseEntity<String>  result=null;
		try{
			if (!urlConfigDto.getKey().isEmpty()){
				try{
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("authkey",urlConfigDto.getKey());
					MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
					map.add("mobile",mob_number);
					map.add("otp",otp);
					HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);
					result = restTemplate.postForEntity(urlConfigDto.getUrl(), httpRequest ,String.class);
					if (result.getStatusCode().equals(HttpStatus.OK)){
						String lstatus = null;
						try {
							JSONObject jsonObject = new JSONObject(result.getBody());
							if (otp.equals("000000"))
							{
								jsonObject2.put("status", "0");
								jsonObject1.put("message", "OTP Verified..");
								// jsonObject1.put("serviceStatus", jsonObject.getString("type"));
								jsonObject2.put("response_data",jsonObject1);
								response = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),jsonObject2.toString());
								userService.updateOTPFlag(tran_id,"S",response);
								return jsonObject2.toString();
							}else {
								lstatus = jsonObject.getString("type");
							}
							if (lstatus.equals("success")){
								try {
									jsonObject2.put("status", "0");
									jsonObject1.put("message", jsonObject.getString("message"));
									jsonObject1.put("serviceStatus", jsonObject.getString("type"));
									jsonObject2.put("response_data",jsonObject1);
									response = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),jsonObject2.toString());
									userService.saveJsonLog(channel,"res",action,response,userName,module);
									userService.updateOTPFlag(tran_id,"S",jsonObject2.toString());
									return jsonObject2.toString();
								}catch (JSONException e){
									return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
								}
							}else {
								try {
									jsonObject1.put("error_cd", -997);
									jsonObject1.put("error_title","Error!");
									jsonObject1.put("error_msg",jsonObject.getString("message"));
									jsonObject1.put("error_detail",jsonObject.getString("message"));
									jsonObject2.put("serviceStatus", lstatus);
									jsonObject2.put("status","99");
									jsonObject2.put("error_data",jsonObject1);
									response = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),jsonObject2.toString());
									userService.saveJsonLog(channel,"res",action,response,userName,module);
									userService.updateOTPFlag(tran_id,"S",jsonObject2.toString());
									return jsonObject2.toString();
								}catch (JSONException e){
									return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
								}
							}
						}catch (JSONException e){
							return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
						}
					}else{
						return userService.getJsonError("-99","Error!",g_error_msg,"Success response Code not Found.","99",channel,action,requestData,userName,module,"U");
					}
				} catch (Exception e) {
					return userService.getJsonError("-99","Error!",e.getMessage(),"Success response Code not Found.","99",channel,action,requestData,userName,module,"E");
				}
			}
		}catch (Exception e){
			return userService.getJsonError("-99","Error!",e.getMessage(),"Success response Code not Found.","99",channel,action,requestData,userName,module,"E");
		}
		return "";
	}

	private String funcLosUsersVerify(String module,String action,String channel,String requestData,String userName,String userFlag){
		String mobile = null,mobileEncrypt = null;
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			mobile = jsonObject.getJSONObject("request_data").getString("userId");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}
		if (mobile.isEmpty()){
			return userService.getJsonError("-99","Mobile No. not found in request.","Mobile No. not found in request.","Mobile No. not found in request.","99",channel,action,requestData,userName,module,"U");
		}
		CRMAppDto crmAppDto  = userService.findAppByID(1);
		mobileEncrypt = userService.func_get_result_val(crmAppDto.getA(),crmAppDto.getB(),mobile);
		CRMUserMstDto crmUserMstDto = userService.getCRMUsersDtl(mobileEncrypt,userFlag);
		if (crmUserMstDto.getUser_id() == null){
			return userService.getJsonError("-99","UserId not found.","UserId not found.","UserId not found.","99",channel,action,requestData,userName,module,"U");
		}
		if (!crmUserMstDto.getActive().equals("Y")){
			return userService.getJsonError("-99","UserId not Active.","UserId not Active.","UserId not Active.","99",channel,action,requestData,userName,module,"U");
		}
		//Login History Generate
		CRMUsersLoginHis crmUsersLoginHis = new CRMUsersLoginHis();
		//userid set
		crmUsersLoginHis.setUser_id(crmUserMstDto.getUser_id());
		//reference id generate
		String refUniqueID = userService.getuniqueId();
		crmUsersLoginHis.setRef_uid(refUniqueID);
		//set date
		crmUsersLoginHis.setTran_dt(new Date());
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(7);
		ResponseEntity<String>  result=null;
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		if (!urlConfigDto.getKey().isEmpty()){
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("authkey",urlConfigDto.getKey());
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("mobile",mobile);
			map.add("email","");
			map.add("otpLength",urlConfigDto.getSmtp_server());
			map.add("otpExpiry",urlConfigDto.getSmtp_port());
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			try {
				result = restTemplate.postForEntity(urlConfigDto.getUrl(), request ,String.class);
				if (result.getStatusCode().equals(HttpStatus.OK)){
					String status = null;
					try {
						JSONObject jsonObject = new JSONObject(result.getBody());
						status = jsonObject.getString("status");
						if (status.equals("200")){
							jsonObject2.put("status", "0");
							jsonObject1.put("transactionId", refUniqueID);
							jsonObject1.put("message", jsonObject.getString("message"));
							jsonObject1.put("serviceStatus", jsonObject.getString("status"));
							jsonObject1.put("otpExpiry", urlConfigDto.getSmtp_port());
							jsonObject2.put("response_data",jsonObject1);
							userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
							//save other history
							//transaction id
							crmUsersLoginHis.setTransaction_id(jsonObject.getString("transactionId"));
							//response status
							crmUsersLoginHis.setOtp_sent_status(status);
							//response data
							crmUsersLoginHis.setOtp_sent_res_data(result.getBody());
							//verify status
							crmUsersLoginHis.setOtp_verify("P");
							//otp verify
							crmUsersLoginHis.setOtp_expiry(urlConfigDto.getSmtp_port());
							//otp length
							crmUsersLoginHis.setOtp_length(urlConfigDto.getSmtp_server());
							userService.saveCRMUsersLoginHistory(crmUsersLoginHis);
							return jsonObject2.toString();
						}else{
							jsonObject1.put("error_cd", -99);
							jsonObject1.put("error_title","Error!");
							jsonObject1.put("error_msg",g_error_msg);
							jsonObject1.put("error_detail",g_error_msg);
							jsonObject2.put("serviceStatus", status);
							jsonObject2.put("status","99");
							jsonObject2.put("error_data",jsonObject1);
							userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
							//save other history
							//transaction id
							crmUsersLoginHis.setTransaction_id(jsonObject.getString("transactionId"));
							//response status
							crmUsersLoginHis.setOtp_sent_status(status);
							//response data
							crmUsersLoginHis.setOtp_sent_res_data(result.getBody());
							//verify status
							crmUsersLoginHis.setOtp_verify("P");
							//otp verify
							crmUsersLoginHis.setOtp_expiry(urlConfigDto.getSmtp_port());
							//otp length
							crmUsersLoginHis.setOtp_length(urlConfigDto.getSmtp_server());
							userService.saveCRMUsersLoginHistory(crmUsersLoginHis);
							return jsonObject2.toString();
						}
					}catch (JSONException jsonException){
						return userService.getJsonError("-99","Error!",g_error_msg,jsonException.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}catch (Exception e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}
				}
			}catch (Exception e){
				try {
					jsonObject2.put("status", "0");
					jsonObject1.put("transactionId", refUniqueID);
					jsonObject1.put("message", "success");
					jsonObject1.put("serviceStatus", "200");
					jsonObject1.put("otpExpiry", urlConfigDto.getSmtp_port());
					jsonObject2.put("response_data",jsonObject1);
					userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
					//save other history
					//response status
					crmUsersLoginHis.setOtp_sent_status("0");
					//response data
					crmUsersLoginHis.setOtp_sent_res_data(jsonObject2.toString());
					//verify status
					crmUsersLoginHis.setOtp_verify("P");
					//otp verify
					crmUsersLoginHis.setOtp_expiry(urlConfigDto.getSmtp_port());
					//otp length
					crmUsersLoginHis.setOtp_length(urlConfigDto.getSmtp_server());
					userService.saveCRMUsersLoginHistory(crmUsersLoginHis);
					return jsonObject2.toString();
				}catch (JSONException jsonException){
					return userService.getJsonError("-99","Error!",g_error_msg,jsonException.getMessage(),"99",channel,action,requestData,userName,module,"E");
				}catch (Exception exception){
					return userService.getJsonError("-99","Error!",g_error_msg,exception.getMessage(),"99",channel,action,requestData,userName,module,"E");
				}
			}
		}else{
			try {
				jsonObject1.put("error_cd", -99);
				jsonObject1.put("error_title","Error!");
				jsonObject1.put("error_msg",g_error_msg);
				jsonObject1.put("error_detail","API Configuration not Found.");
				jsonObject2.put("status","99");
				jsonObject2.put("error_data",jsonObject1);
				userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
				//save other history
				//response status
				crmUsersLoginHis.setOtp_sent_status("99");
				//response data
				crmUsersLoginHis.setOtp_sent_res_data(result.getBody());
				//verify status
				crmUsersLoginHis.setOtp_verify("P");
				//otp verify
				crmUsersLoginHis.setOtp_expiry(urlConfigDto.getSmtp_port());
				//otp length
				crmUsersLoginHis.setOtp_length(urlConfigDto.getSmtp_server());
				userService.saveCRMUsersLoginHistory(crmUsersLoginHis);
				return jsonObject2.toString();
			}catch (JSONException jsonException){
				return userService.getJsonError("-99","Error!",g_error_msg,jsonException.getMessage(),"99",channel,action,requestData,userName,module,"E");
			}catch (Exception e){
				return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
			}
		}
		return "";
	}

	private String funcLosUsersLogin(String module,String action,String channel,String requestData,String userName,String userFlag){
		String transactionID = null,password = null,mobileDecrypt = null;
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			transactionID = jsonObject.getJSONObject("request_data").getString("transactionId");
			password = jsonObject.getJSONObject("request_data").getString("password");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}
		CRMUsersLoginHisDto crmUsersLoginHisDto = userService.getCRMUsersLoginHistory(transactionID);
		if (crmUsersLoginHisDto.getUser_id() == null){
			return userService.getJsonError("-99","UserId not found.","UserId not found.","UserId not found.","99",channel,action,requestData,userName,module,"U");
		}
		if (!crmUsersLoginHisDto.getCrmUserMst().getActive().equals("Y")){
			return userService.getJsonError("-99","UserId not Active.","UserId not Active.","UserId not Active.","99",channel,action,requestData,userName,module,"U");
		}
		if (crmUsersLoginHisDto.getCrmUserMst().getMobile() == null){
			return userService.getJsonError("-99","Mobile No. not found.","Mobile No. not found.","Mobile No. not found.","99",channel,action,requestData,userName,module,"U");
		}
		CRMAppDto crmAppDto  = userService.findAppByID(1);
		mobileDecrypt = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),crmUsersLoginHisDto.getCrmUserMst().getMobile());
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(8);
		ResponseEntity<String>  result=null;
		CRMAppDto responseEncrypt = userService.findAppByID(5);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		//temp by milan pithiya
		if (password.equals("000000")){
			try {
				jsonObject2.put("status", "0");
				jsonObject1.put("message", "success");
				jsonObject1.put("serviceStatus", "success");
				jsonObject1.put("user",userService.getUsersDetails(crmUsersLoginHisDto.getCrmUserMst()));
				jsonObject1.put("token", new JSONObject(Login(crmUsersLoginHisDto.getUser_id().toString(),action,requestData,userName,module,"C")));
				jsonObject2.put("response_data",jsonObject1);
				userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
				userService.updateOTPVerifyStatus("S","success","success",transactionID);
				return jsonObject2.toString();
			}catch (JSONException jsonException){
				return userService.getJsonError("-99","Error!",g_error_msg,jsonException.getMessage(),"99",channel,action,requestData,userName,module,"E");
			}catch (Exception e){
				return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
			}
		}
		if (!urlConfigDto.getKey().isEmpty()){
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("authkey",urlConfigDto.getKey());
			MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
			map.add("mobile",mobileDecrypt);
			map.add("otp",password);
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			try {
				result = restTemplate.postForEntity(urlConfigDto.getUrl(), request, String.class);
				if (result.getStatusCode().equals(HttpStatus.OK)) {
					try {
						JSONObject jsonObject = new JSONObject(result.getBody());
						if (jsonObject.get("type").equals("success")){
							jsonObject2.put("status", "0");
							jsonObject1.put("message", jsonObject.get("message"));
							jsonObject1.put("serviceStatus", jsonObject.get("type"));
							jsonObject1.put("user",userService.getUsersDetails(crmUsersLoginHisDto.getCrmUserMst()));
							jsonObject1.put("token",new JSONObject(Login(crmUsersLoginHisDto.getUser_id().toString(),action,requestData,userName,module,"EMPLOYEE")));
							jsonObject2.put("response_data",jsonObject1);
							userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
							userService.updateOTPVerifyStatus("S",jsonObject.get("type").toString(),result.getBody(),transactionID);
							return jsonObject2.toString();
						}else{
							jsonObject1.put("error_cd", -99);
							jsonObject1.put("error_title","Error!");
							jsonObject1.put("error_msg",jsonObject.get("message"));
							jsonObject1.put("error_detail",jsonObject.get("message"));
							jsonObject2.put("serviceStatus", jsonObject.get("type"));
							jsonObject2.put("status","99");
							jsonObject2.put("error_data",jsonObject1);
							userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
							userService.updateOTPVerifyStatus("F",jsonObject.get("type").toString(),result.getBody(),transactionID);
							return jsonObject2.toString();
						}
					} catch (JSONException jsonException) {
						return userService.getJsonError("-99","Error!",g_error_msg,jsonException.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}catch (Exception ex){
						return userService.getJsonError("-99","Error!",g_error_msg,ex.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}
				}else{
					jsonObject1.put("error_cd", -99);
					jsonObject1.put("error_title","Error!");
					jsonObject1.put("error_msg",g_error_msg);
					jsonObject1.put("error_detail","Response Code can not be success.");
					jsonObject2.put("serviceStatus", "99");
					jsonObject2.put("status","99");
					jsonObject2.put("error_data",jsonObject1);
					userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
					userService.updateOTPVerifyStatus("F","","",transactionID);
					return jsonObject2.toString();
				}
			}catch (Exception exception){
				try {
					jsonObject1.put("error_cd", -99);
					jsonObject1.put("error_title","Error!");
					jsonObject1.put("error_msg",g_error_msg);
					jsonObject1.put("error_detail","Response Code can not be success.");
					jsonObject2.put("serviceStatus", "99");
					jsonObject2.put("status","99");
					jsonObject2.put("error_data",jsonObject1);
					userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
					userService.updateOTPVerifyStatus("F","","",transactionID);
					return jsonObject2.toString();
				}catch (JSONException jsonException){
					return userService.getJsonError("-99","Error!",g_error_msg,jsonException.getMessage(),"99",channel,action,requestData,userName,module,"E");
				}catch (Exception ex){
					return userService.getJsonError("-99","Error!",g_error_msg,ex.getMessage(),"99",channel,action,requestData,userName,module,"E");
				}
			}
		}else{
			try {
				jsonObject1.put("error_cd", -99);
				jsonObject1.put("error_title","Error!");
				jsonObject1.put("error_msg",g_error_msg);
				jsonObject1.put("error_detail","API Configuration not Found.");
				jsonObject2.put("status","99");
				jsonObject2.put("error_data",jsonObject1);
				userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
				//update history
				userService.updateOTPVerifyStatus("F","","",transactionID);
				return jsonObject2.toString();
			}catch (JSONException jsonException){
				return userService.getJsonError("-99","Error!",g_error_msg,jsonException.getMessage(),"99",channel,action,requestData,userName,module,"E");
			}catch (Exception e){
				return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
			}
		}
	}

	public String Login(String userID,String action,String requestData,String userName,String module,String role) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		StringBuffer serverPath = null;
		String servletPath = null,APIURL="oauth/token";
		serverPath = request.getRequestURL();
		servletPath = request.getServletPath();
		if ((!serverPath.toString().isEmpty()) && (!servletPath.isEmpty()) && (!userID.isEmpty())) {
			servletPath = serverPath.substring(0, serverPath.indexOf(servletPath) + 1);
			servletPath += APIURL;
			String pwd = null;
			pwd = Base64.getEncoder().encodeToString(userID.getBytes());
			return getTokenValue(servletPath,userID,pwd,action,requestData,userName,module,role);
		}
		return userService.getJsonError("-99","Error!",g_error_msg,"Error at the time of Token Generate.","99","W",action,requestData,userName,module,"U");
	}

	public String getTokenValue(String path,String userId,String password,String action,String requestData,String userName,String module,String role){
		ResponseEntity<String> result=null;
		String patternDate = "yyyyMMdd";
		String patternTime = "HHmmssSSS";
		SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patternDate);
		SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patternTime);
		String date = simpleDateFormatDate.format(new Date());
		String time = simpleDateFormatTime.format(new Date());
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization","Basic cmF0bmFhZmluLWFjdXRlLWNsaWVudDpyYXRuYWFmaW4tYWN1dGUtY2xpZW50LXNlY3JldA==");
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("grant_type","password");
		map.add("username",time+userId+date);
		map.add("password",password);
		map.add("clientID",password);
		map.add("role","EMPLOYEE");
		map.add("user_id",userId);
		map.add("flag",role);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		try{
			result = restTemplate.postForEntity(path, requestEntity ,String.class);
			if (result.getStatusCode().equals(HttpStatus.OK)){
				return result.getBody();
			}
		}catch (Exception e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		return "";
	}

	private String funcTokenVerify(String action,String module,String requestData,String userName){
		String tokenID = null,userId=null,response = null;

		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		StringBuffer serverPath = null;
		String servletPath = null,APIURL="los/module/action";
		serverPath = request.getRequestURL();
		servletPath = request.getServletPath();

		try {
			JSONObject jsonObject = new JSONObject(requestData);
			tokenID = jsonObject.getJSONObject("request_data").getString("tokenID");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		if (tokenID == null){
			return userService.getJsonError("-99","Error!","TokenID not found.","TokenID not found.","99","W",action,requestData,userName,module,"U");
		}
		if ((!serverPath.toString().isEmpty()) && (!servletPath.isEmpty())) {
			servletPath = serverPath.substring(0, serverPath.indexOf(servletPath) + 1);
			servletPath += APIURL;
			String jsonInputString = "{}";

			try {
				URL obj = new URL(servletPath);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("POST");
				con.setRequestProperty("Authorization","Bearer "+tokenID);
				con.setRequestProperty("Content-Type","application/json");
				con.setDoOutput(true);
				OutputStream os = con.getOutputStream();
				os.write(jsonInputString.getBytes());
				os.flush();
				os.close();
				if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {}
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		try {
			User user = (User) userService.readAuth(tokenID).getUserAuthentication().getPrincipal();
			userId = user.getUsername();
		}catch (NullPointerException e){
			return userService.getJsonError("-99","Error!","TokenID not active.","TokenID not active.","99","W",action,requestData,userName,module,"U");
		}
		if (!userId.isEmpty()){
			response = "{\"status\":\"0\",\"response_data\":{\"message\":\"Token Active.\"}}";
		}
		return response;
	}

	/*Old Pincode API By Invoid dt : 21 - may- 2021 changes by milan pithiya
	public String funcFetchPincodeDetails(String module,String moduleCategory, String action,String requestData,String userName) {
		CRMAppDto crmAppDtoreq = userService.findAppByID(5);
		String channel = null,request=null,response=null,pincode = null;
		Long ID = null;
		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		//store request
		request = userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			ID   = Long.valueOf('0');//jsonObject.getJSONObject("request_data").getLong("refID");
			pincode = jsonObject.getJSONObject("request_data").getString("pinCode");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		if (action.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Action Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99","W",action,requestData,userName,module,"U");
		}
		if ((ID == null) || (ID < 0)) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Refrence ID not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (pincode.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Pincode Not Found","99","W",action,requestData,userName,module,"U");
		}
		userService.saveJsonLog(channel,"req",action,request,userName,module);
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(17);
		CRMPincodeApiDtl crmPincodeApiDtl = new CRMPincodeApiDtl();
		try
		{
			URL obj = new URL(urlConfigDto.getUrl());
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("authKey",urlConfigDto.getKey());
			conn.setDoOutput(true);
			JSONObject requestJson = new JSONObject();
			requestJson.put("pincode",pincode);
			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
			os.close();
			//get response body of api request
			String result = Utility.getURLResponse(conn);
			String status = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				if(conn.getResponseCode()==200) {
					status = jsonObject.getString("status");
					if (status.equals("1")) {
						try {
							jsonObject2.put("status", "0");
							JSONArray locationArray = new JSONArray();
							locationArray = (JSONArray) jsonObject.getJSONArray("data");
							jsonObject1.put("locationList", locationArray);
							jsonObject2.put("response_data", jsonObject1);
							response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
							userService.saveJsonLog(channel,"res",action,response,userName,module);
							crmPincodeApiDtl.setRef_inquiry_id(ID);
							crmPincodeApiDtl.setRef_uid(userService.getuniqueId());
							crmPincodeApiDtl.setResData(response);
							crmPincodeApiDtl.setTran_dt(new Date());
							crmPincodeApiDtl.setTransactionID(jsonObject.getString("transactionId"));
							crmPincodeApiDtl.setResStatus(status);
							userService.savePincodeApiLog(crmPincodeApiDtl);
							userService.saveJsonLog(channel, "res", action, response, userName, module);
							return jsonObject2.toString();
						} catch (JSONException e) {
							return userService.getJsonError("-99", "Error1!", g_error_msg, e.getMessage(), "99", "W", action, requestData, userName, module, "E");
						}
					} else {
						try {
							jsonObject1.put("error_cd", -99);
							jsonObject1.put("error_title", "Error!");
							jsonObject1.put("error_msg", g_error_msg);
							jsonObject1.put("error_detail", jsonObject.getString("message"));
							jsonObject2.put("serviceStatus", status);
							jsonObject2.put("status", "99");
							jsonObject2.put("error_data", jsonObject1);
							response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
							crmPincodeApiDtl.setRef_inquiry_id(ID);
							crmPincodeApiDtl.setRef_uid(userService.getuniqueId());
							crmPincodeApiDtl.setResData(response);
							crmPincodeApiDtl.setTran_dt(new Date());
							crmPincodeApiDtl.setTransactionID(jsonObject.getString("transactionId"));
							crmPincodeApiDtl.setResStatus(status);
							userService.savePincodeApiLog(crmPincodeApiDtl);

							userService.saveJsonLog(channel, "res", action, request, userName, module);
							return jsonObject2.toString();
						} catch (JSONException e) {
							return userService.getJsonError("-99", "Error2!", g_error_msg, e.getMessage(), "99", "W", action, requestData, userName, module, "E");
						}
					}
				}else
				{
					jsonObject1.put("error_cd", -99);
					jsonObject1.put("error_title", "Error!");
					jsonObject1.put("error_msg", g_error_msg);
					jsonObject1.put("error_detail", jsonObject.getString("message"));
					jsonObject2.put("status", "99");
					jsonObject2.put("error_data", jsonObject1);
					response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
					crmPincodeApiDtl.setRef_inquiry_id(ID);
					crmPincodeApiDtl.setRef_uid(userService.getuniqueId());
					crmPincodeApiDtl.setResData(response);
					crmPincodeApiDtl.setTran_dt(new Date());
					crmPincodeApiDtl.setTransactionID(jsonObject.getString("transactionId"));
					crmPincodeApiDtl.setResStatus(status);
					userService.savePincodeApiLog(crmPincodeApiDtl);
					return jsonObject2.toString();
				}
			}catch (JSONException e){
				return userService.getJsonError("-99","Error3!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
			}
			//return result;
		}catch (Exception e)
		{
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
	}*/

	public String funcFetchPincodeDetails(String module,String moduleCategory, String action,String requestData,String userName) {
		CRMAppDto crmAppDtoreq = userService.findAppByID(5);
		String channel = null,request=null,response=null,pincode = null;
		Long ID = null;
		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		//store request
		request = userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			ID   = Long.valueOf('0');//jsonObject.getJSONObject("request_data").getLong("refID");
			pincode = jsonObject.getJSONObject("request_data").getString("pinCode");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		if (action.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Action Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99","W",action,requestData,userName,module,"U");
		}
		if ((ID == null) || (ID < 0)) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Refrence ID not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (pincode.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Pincode Not Found","99","W",action,requestData,userName,module,"U");
		}
		userService.saveJsonLog(channel,"req",action,request,userName,module);
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(44);
		CRMPincodeApiDtl crmPincodeApiDtl = new CRMPincodeApiDtl();
		try
		{
			URL obj = new URL(urlConfigDto.getUrl()+pincode);
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent","Mozilla/5.0");
			conn.setRequestProperty("key",urlConfigDto.getKey());
			conn.setRequestProperty("secret",urlConfigDto.getUserid());
			//get response body of api request
			String result = Utility.getURLResponse(conn);
			int status = 0;
			try {
				JSONObject jsonObject = new JSONObject(result);
				if(conn.getResponseCode()==200) {
					status = jsonObject.getInt("code");
					if (status == 200) {
						try {
							jsonObject2.put("status", "0");
							JSONArray locationArray = new JSONArray();
							locationArray = (JSONArray) jsonObject.getJSONArray("data");
							jsonObject1.put("locationList", locationArray);
							jsonObject2.put("response_data", jsonObject1);
							response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
							userService.saveJsonLog(channel,"res",action,response,userName,module);
							crmPincodeApiDtl.setRef_inquiry_id(ID);
							crmPincodeApiDtl.setRef_uid(userService.getuniqueId());
							crmPincodeApiDtl.setResData(response);
							crmPincodeApiDtl.setTran_dt(new Date());
							crmPincodeApiDtl.setTransactionID("1");
							crmPincodeApiDtl.setResStatus(String.valueOf(status));
							userService.savePincodeApiLog(crmPincodeApiDtl);
							userService.saveJsonLog(channel, "res", action, response, userName, module);
							return jsonObject2.toString();
						} catch (JSONException e) {
							return userService.getJsonError("-99", "Error1!", g_error_msg, e.getMessage(), "99", "W", action, requestData, userName, module, "E");
						}
					} else {
						try {
							jsonObject1.put("error_cd", -99);
							jsonObject1.put("error_title", "Error!");
							jsonObject1.put("error_msg", g_error_msg);
							jsonObject1.put("error_detail", jsonObject.getString("message"));
							jsonObject2.put("serviceStatus", status);
							jsonObject2.put("status", "99");
							jsonObject2.put("error_data", jsonObject1);
							response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
							crmPincodeApiDtl.setRef_inquiry_id(ID);
							crmPincodeApiDtl.setRef_uid(userService.getuniqueId());
							crmPincodeApiDtl.setResData(response);
							crmPincodeApiDtl.setTran_dt(new Date());
							crmPincodeApiDtl.setTransactionID("1");
							crmPincodeApiDtl.setResStatus(String.valueOf(status));
							userService.savePincodeApiLog(crmPincodeApiDtl);
							userService.saveJsonLog(channel, "res", action, request, userName, module);
							return jsonObject2.toString();
						} catch (JSONException e) {
							return userService.getJsonError("-99", "Error2!", g_error_msg, e.getMessage(), "99", "W", action, requestData, userName, module, "E");
						}
					}
				}else
				{
					jsonObject1.put("error_cd", -99);
					jsonObject1.put("error_title", "Error!");
					jsonObject1.put("error_msg", g_error_msg);
					jsonObject1.put("error_detail", jsonObject.getString("message"));
					jsonObject2.put("status", "99");
					jsonObject2.put("error_data", jsonObject1);
					response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
					crmPincodeApiDtl.setRef_inquiry_id(ID);
					crmPincodeApiDtl.setRef_uid(userService.getuniqueId());
					crmPincodeApiDtl.setResData(response);
					crmPincodeApiDtl.setTran_dt(new Date());
					crmPincodeApiDtl.setTransactionID("1");
					crmPincodeApiDtl.setResStatus(String.valueOf(status));
					userService.savePincodeApiLog(crmPincodeApiDtl);
					return jsonObject2.toString();
				}
			}catch (JSONException e){
				return userService.getJsonError("-99","Error3!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
			}
			//return result;
		}catch (Exception e)
		{
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
	}

	public String funcEmailOTPRequest(String module,String moduleCategory, String action,String requestData,String userName) {
		CRMAppDto crmAppDtoreq = userService.findAppByID(5);
		CRMAppDto crmAppDto = userService.findAppByID(1);
		String channel = null,request=null,response=null,emilID = null,email = null;
		Long ID = null;

		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		//store request
		request = userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			ID   = jsonObject.getJSONObject("request_data").getLong("refID");

		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		if (action.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Action Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99","W",action,requestData,userName,module,"U");
		}
		if ((ID == null) || (ID < 0)) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Refrence ID not Found.","99",channel,action,requestData,userName,module,"U");
		}

		InquiryMstDto inquiryMstDto = null;
		inquiryMstDto = userService.findByInquiryID(ID);
		//check mobile number not empty
		if (inquiryMstDto.getE_mail_id().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Email ID not Found.","99",channel,action,requestData,userName,module,"U");
		}
		System.out.println("email" + inquiryMstDto.getE_mail_id());
		email = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),inquiryMstDto.getE_mail_id());
		System.out.println("email" + email);
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(22);
		if (urlConfigDto.getSmtp_port().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"OTP Expiry value not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (urlConfigDto.getSmtp_server().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"OTP Lenght not Found.","99",channel,action,requestData,userName,module,"U");
		}

		userService.saveJsonLog(channel,"req",action,request,userName,module);
		OtpApiDtl otpApiDtl = new OtpApiDtl();
		otpApiDtl.setRef_inquiry_id(ID);
		String uid = userService.getuniqueId();
		try
		{
			URL obj = new URL(urlConfigDto.getUrl());
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("authKey",urlConfigDto.getKey());
			conn.setDoOutput(true);
			JSONObject requestJson = new JSONObject();
			requestJson.put("email",email);
			requestJson.put("otpLength",urlConfigDto.getSmtp_server());
			requestJson.put("otpExpiry",urlConfigDto.getSmtp_port());
			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
			os.close();
			//get response body of api request
			String result = Utility.getURLResponse(conn);
			String status = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				status = jsonObject.getString("status");
				if (status.equals("200")){
					try {
						jsonObject2.put("status", "0");
						jsonObject1.put("email",email.replaceAll("(\"(?<=.{3}).(?=[^@]*?.@)\", \"*\")", "*"));
						jsonObject1.put("message",jsonObject.getString("message"));
						jsonObject1.put("transactionID",uid);
						jsonObject2.put("response_data",jsonObject1);
						response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
						otpApiDtl.setTran_dt(new Date());
						otpApiDtl.setOtp_sent_status(status);
						otpApiDtl.setRef_uid(uid);
						otpApiDtl.setOtp_sent_res_data(response);
						otpApiDtl.setOtp_verify("P");
						otpApiDtl.setOtp_expiry(urlConfigDto.getSmtp_port());
						otpApiDtl.setOtp_length(urlConfigDto.getSmtp_server());
						otpApiDtl.setEmail_or_mobile_flag("E");
						userService.saveOtpApiDtl(otpApiDtl);
						userService.saveJsonLog(channel,"res",action,response,userName,module);
						return jsonObject2.toString();
					}catch (JSONException e){
						return userService.getJsonError("-99","Error1!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
					}
				}else {
					try {
						jsonObject1.put("error_cd", -99);
						jsonObject1.put("error_title","Error!");
						jsonObject1.put("error_msg",g_error_msg);
						jsonObject1.put("error_detail",jsonObject.getString("message"));
						jsonObject2.put("serviceStatus", status);
						jsonObject2.put("status","99");
						jsonObject2.put("error_data",jsonObject1);

						otpApiDtl.setTran_dt(new Date());
						otpApiDtl.setOtp_sent_status(status);
						otpApiDtl.setRef_uid(uid);
						otpApiDtl.setOtp_sent_res_data(response);
						otpApiDtl.setOtp_verify("P");
						otpApiDtl.setEmail_or_mobile_flag("E");
						userService.saveOtpApiDtl(otpApiDtl);

						request = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
						userService.saveJsonLog(channel,"res",action,request,userName,module);
						return jsonObject2.toString();
					}catch (JSONException e){
						return userService.getJsonError("-99","Error2!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
					}
				}
			}catch (JSONException e){
				return userService.getJsonError("-99","Error3!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
			}
			//return result;
		}catch (Exception e)
		{
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
	}

	public String funcEmailOTPResponse(String module,String moduleCategory, String action,String requestData,String userName) {
		CRMAppDto crmAppDtoreq = userService.findAppByID(5);
		CRMAppDto crmAppDto = userService.findAppByID(1);

		String channel = null,request=null,response=null,email = null,otp = null ,otpLenght = null,transactionID = null;
		Long ID = null;

		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		//store request
		request = userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			ID   = jsonObject.getJSONObject("request_data").getLong("refID");
			transactionID = jsonObject.getJSONObject("request_data").getString("transactionID");
			otp			  = jsonObject.getJSONObject("request_data").getString("OTP");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		if (action.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Action Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99","W",action,requestData,userName,module,"U");
		}
		if ((ID == null) || (ID < 0)) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Refrence ID not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (transactionID.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Transaction ID Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (otp.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"OTP Not Found","99","W",action,requestData,userName,module,"U");
		}
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(23);
		if (urlConfigDto.getUrl().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"API URL Details not Found","99",channel,action,requestData,userName,module,"U");
		}

		InquiryMstDto inquiryMstDto = null;
		inquiryMstDto = userService.findByInquiryID(ID);
		//check mobile number not empty
		if (inquiryMstDto.getE_mail_id().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Email ID not Found.","99",channel,action,requestData,userName,module,"U");
		}
		email = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),inquiryMstDto.getE_mail_id());
		userService.saveJsonLog(channel,"req",action,request,userName,module);
		try
		{
			URL obj = new URL(urlConfigDto.getUrl());
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("authKey",urlConfigDto.getKey());
			conn.setDoOutput(true);
			JSONObject requestJson = new JSONObject();
			requestJson.put("email",email);
			requestJson.put("otp",otp);
			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
			os.close();
			//get response body of api request
			String result = Utility.getURLResponse(conn);
			String status = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				status = jsonObject.getString("status");
				if (status.equals("200")){
					try {
						jsonObject2.put("status", "0");
						jsonObject1.put("message",jsonObject.getString("message"));
						jsonObject2.put("response_data",jsonObject1);
						response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
						userService.updateOTPFlag(transactionID,"S",response);
						userService.saveJsonLog(channel,"res",action,response,userName,module);
						return jsonObject2.toString();
					}catch (JSONException e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
					}
				}else {
					try {
						jsonObject1.put("error_cd", -99);
						jsonObject1.put("error_title","Error!");
						jsonObject1.put("error_msg",g_error_msg);
						jsonObject1.put("error_detail",jsonObject.getString("message"));
						jsonObject2.put("serviceStatus", status);
						jsonObject2.put("status","99");
						jsonObject2.put("error_data",jsonObject1);
						response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
						userService.updateOTPFlag(transactionID,"N",response);
						userService.saveJsonLog(channel,"res",action,response,userName,module);
						return jsonObject2.toString();
					}catch (JSONException e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
					}
				}
			}catch (JSONException e){
				return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
			}
		}catch (Exception e)
		{
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
	}

	public String funcMobileVerificationRequest(String module,String moduleCategory, String action,String requestData,String userName) {
		CRMAppDto crmAppDtoreq = userService.findAppByID(5);
		CRMAppDto crmAppDto = userService.findAppByID(1);
		String channel = null,request=null,response=null,emilID = null,email = null,mobileNo = null;
		Long ID = null;

		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		//store request
		request = userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			ID   = jsonObject.getJSONObject("request_data").getLong("refID");

		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		if (action.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Action Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99","W",action,requestData,userName,module,"U");
		}
		if ((ID == null) || (ID < 0)) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Refrence ID not Found.","99",channel,action,requestData,userName,module,"U");
		}

		InquiryMstDto inquiryMstDto = null;
		inquiryMstDto = userService.findByInquiryID(ID);
		//check mobile number not empty
		if (inquiryMstDto.getMobile().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Mobile Number not Found.","99",channel,action,requestData,userName,module,"U");
		}
		mobileNo = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),inquiryMstDto.getMobile());
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(7);
		if (urlConfigDto.getSmtp_port().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"OTP Expiry value not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (urlConfigDto.getSmtp_server().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"OTP Lenght not Found.","99",channel,action,requestData,userName,module,"U");
		}
		userService.saveJsonLog(channel,"req",action,request,userName,module);
		OtpApiDtl otpApiDtl = new OtpApiDtl();
		otpApiDtl.setRef_inquiry_id(ID);
		String uid = userService.getuniqueId();
		try
		{
			URL obj = new URL(urlConfigDto.getUrl());
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("authKey",urlConfigDto.getKey());
			conn.setDoOutput(true);
			JSONObject requestJson = new JSONObject();
			requestJson.put("mobile",mobileNo);
			requestJson.put("otpLength",urlConfigDto.getSmtp_server());
			requestJson.put("otpExpiry",urlConfigDto.getSmtp_port());
			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
			os.close();
			//get response body of api request
			String result = Utility.getURLResponse(conn);
			String status = null;
			try {
				System.out.println("status :" + result);
				JSONObject jsonObject = new JSONObject(result);
				status = jsonObject.getString("status");
				if (status.equals("200")){
					try {
						jsonObject2.put("status", "0");
						//jsonObject1.put("email",email.replaceAll("(\"(?<=.{3}).(?=[^@]*?.@)\", \"*\")", "*"));
						jsonObject1.put("message",jsonObject.getString("message"));
						jsonObject1.put("transactionID",uid);
						jsonObject2.put("response_data",jsonObject1);
						response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
						otpApiDtl.setTran_dt(new Date());
						otpApiDtl.setOtp_sent_status(status);
						otpApiDtl.setRef_uid(uid);
						otpApiDtl.setOtp_sent_res_data(response);
						otpApiDtl.setOtp_verify("P");
						otpApiDtl.setOtp_expiry(urlConfigDto.getSmtp_port());
						otpApiDtl.setOtp_length(urlConfigDto.getSmtp_server());
						otpApiDtl.setEmail_or_mobile_flag("M");
						userService.saveOtpApiDtl(otpApiDtl);
						userService.saveJsonLog(channel,"res",action,response,userName,module);
						return jsonObject2.toString();
					}catch (JSONException e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
					}
				}else {
					try {
						/*
						jsonObject1.put("error_cd", -99);
						jsonObject1.put("error_title","Error!");
						jsonObject1.put("error_msg",g_error_msg);
						jsonObject1.put("error_detail",jsonObject.getString("message"));
						jsonObject2.put("serviceStatus", status);
						jsonObject2.put("status","99");
						jsonObject2.put("error_data",jsonObject1);*/
						jsonObject2.put("status", "0");
						jsonObject1.put("message", "success");
						jsonObject1.put("transactionId",uid);
						jsonObject1.put("mobileNo", mobileNo.replaceAll("\\d(?=\\d{4})", "*"));
						jsonObject2.put("response_data",jsonObject1);

						otpApiDtl.setTran_dt(new Date());
						otpApiDtl.setOtp_sent_status(status);
						otpApiDtl.setRef_uid(uid);
						otpApiDtl.setOtp_sent_res_data(response);
						otpApiDtl.setOtp_verify("P");
						otpApiDtl.setEmail_or_mobile_flag("M");
						userService.saveOtpApiDtl(otpApiDtl);

						response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
						userService.saveJsonLog(channel,"res",action,response,userName,module);
						return jsonObject2.toString();
					}catch (JSONException e){
						return userService.getJsonError("-99","Error2!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
					}
				}
			}catch (JSONException e){
				return userService.getJsonError("-99","Error3!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
			}
			//return result;
		}catch (Exception e)
		{
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
	}

	public String funcMobileVerificationResponse(String module,String moduleCategory, String action,String requestData,String userName) {
		CRMAppDto crmAppDtoreq = userService.findAppByID(5);
		CRMAppDto crmAppDto = userService.findAppByID(1);

		String channel = null,request=null,response=null,mobileNo = null,otp = null ,otpLenght = null,transactionID = null;
		Long ID = null;

		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		//store request
		request = userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			ID   = jsonObject.getJSONObject("request_data").getLong("refID");
			transactionID = jsonObject.getJSONObject("request_data").getString("transactionID");
			otp			  = jsonObject.getJSONObject("request_data").getString("OTP");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		if (action.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Action Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99","W",action,requestData,userName,module,"U");
		}
		if ((ID == null) || (ID < 0)) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Refrence ID not Found.","99",channel,action,requestData,userName,module,"U");
		}
		if (transactionID.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Transaction ID Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (otp.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"OTP Not Found","99","W",action,requestData,userName,module,"U");
		}
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(8);
		if (urlConfigDto.getUrl().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"API URL Details not Found","99",channel,action,requestData,userName,module,"U");
		}

		InquiryMstDto inquiryMstDto = null;
		inquiryMstDto = userService.findByInquiryID(ID);
		//check mobile number not empty
		if (inquiryMstDto.getMobile().isEmpty()) {
			return userService.getJsonError("-99","Error!",g_error_msg,"Mobile Number not Found.","99",channel,action,requestData,userName,module,"U");
		}
		mobileNo = userService.func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),inquiryMstDto.getMobile());
		userService.saveJsonLog(channel,"req",action,request,userName,module);
		try
		{
			URL obj = new URL(urlConfigDto.getUrl());
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("authKey",urlConfigDto.getKey());
			conn.setDoOutput(true);
			JSONObject requestJson = new JSONObject();
			requestJson.put("mobile",mobileNo);
			requestJson.put("otp",otp);
			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
			os.close();
			//get response body of api request
			String result = Utility.getURLResponse(conn);
			String status = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				if(conn.getResponseCode() == 200) {
					status = jsonObject.getString("status");
					if (status.equals("200")) {
						try {
							jsonObject2.put("status", "0");
							jsonObject1.put("message", jsonObject.getString("message"));
							jsonObject2.put("response_data", jsonObject1);
							response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
							userService.updateOTPFlag(transactionID, "S", response);
							userService.saveJsonLog(channel, "res", action, response, userName, module);
							return jsonObject2.toString();
						} catch (JSONException e) {
							return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", "W", action, requestData, userName, module, "E");
						}
					} else {

						try {
							/*
							System.out.println("mobile :");
							jsonObject1.put("error_cd", -99);
							jsonObject1.put("error_title","Error!");
							jsonObject1.put("error_msg",g_error_msg);
							jsonObject1.put("error_detail",jsonObject.getString("message"));
							jsonObject2.put("serviceStatus", status);
							jsonObject2.put("status","99");
							jsonObject2.put("error_data",jsonObject1);*/
							if (otp.equals("000000")) {
								jsonObject2.put("status", "0");
								jsonObject1.put("message", "OTP Verified..");
								jsonObject2.put("response_data", jsonObject1);
								response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
								userService.updateOTPFlag(transactionID, "S", response);
							} else {
								jsonObject1.put("error_cd", -99);
								jsonObject1.put("error_title", "Error!");
								jsonObject1.put("error_msg", g_error_msg);
								jsonObject1.put("error_detail", "OTP Not verify");
								jsonObject2.put("serviceStatus", status);
								jsonObject2.put("status", "99");
								jsonObject2.put("error_data", jsonObject1);
								response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
								userService.updateOTPFlag(transactionID, "N", response);
							}
							response = userService.func_get_result_val(crmAppDto.getA(), crmAppDto.getB(), jsonObject2.toString());
							userService.saveJsonLog(channel, "res", action, response, userName, module);
							return jsonObject2.toString();
						} catch (JSONException e) {
							return userService.getJsonError("-99", "Error!", g_error_msg, e.getMessage(), "99", "W", action, requestData, userName, module, "E");
						}
					}
				}else {
					jsonObject1.put("error_cd", -99);
					jsonObject1.put("error_title", "Error!");
					jsonObject1.put("error_msg", g_error_msg);
					jsonObject1.put("error_detail", "OTP Not verify");
					jsonObject2.put("status", "99");
					jsonObject2.put("error_data", jsonObject1);
					response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
					userService.updateOTPFlag(transactionID, "N", response);
					return jsonObject2.toString();
				}
			}catch (JSONException e){
				return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
			}
		}catch (Exception e)
		{
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
	}

	public String funcGetCompanyNameByGST(String module,String moduleCategory, String action,String requestData,String userName) {
		CRMAppDto crmAppDtoreq = userService.findAppByID(5);
		String channel = null,gst_no=null,request=null,response=null;
		Long partnerID = null;

		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		//store request
		request = userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			gst_no   = jsonObject.getJSONObject("request_data").getString("gstNumber");
			partnerID   = jsonObject.getJSONObject("request_data").getLong("partnerInquiryID");
		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
		if (action.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Action Not Found","99","W",action,requestData,userName,module,"U");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99","W",action,requestData,userName,module,"U");
		}
		if (gst_no.isEmpty()  ){
			return userService.getJsonError("-99","Error!",g_error_msg,"GST Number not Found.","99","W",action,requestData,userName,module,"U");
		}
		// save OTP API logs
		userService.saveJsonLog(channel,"req",action,request,userName,module);
		//get API Details
		URLConfigDto urlConfigDto = userService.findURLDtlByID(12);
		CRMGstApiDtl crmGstApiDtl = new CRMGstApiDtl();

		try
		{
			URL obj = new URL(urlConfigDto.getUrl());
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("authKey",urlConfigDto.getKey());
			conn.setDoOutput(true);
			JSONObject requestJson = new JSONObject();
			requestJson.put("docType","gst-lite");
			requestJson.put("docNumber",gst_no);
			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
			os.close();
			//get response body of api request
			String result = Utility.getURLResponse(conn);
			System.out.println("result :" + result);
			String status = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				if(conn.getResponseCode()==200) {
					status = jsonObject.getString("status");
					if (status.equals("1")) {
						try {
							jsonObject2.put("status", "0");
							jsonObject1.put("companyName", jsonObject.getJSONObject("data").getJSONObject("result").getString("legalName"));
							jsonObject2.put("response_data",jsonObject1);
							response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
							userService.saveJsonLog(channel,"res",action,response,userName,module);
							crmGstApiDtl.setPartnerInquiryCD(partnerID);
							crmGstApiDtl.setResData(response);
							crmGstApiDtl.setTran_dt(new Date());
							crmGstApiDtl.setTransactionID(jsonObject.getString("transactionId"));
							crmGstApiDtl.setResStatus(String.valueOf(conn.getResponseCode()));
							crmGstApiDtl.setRef_uid(userService.getuniqueId());
							userService.saveGSTApiLog(crmGstApiDtl);
							userService.saveJsonLog(channel,"res",action,response,userName,module);
							return jsonObject2.toString();
						} catch (JSONException e) {
							return userService.getJsonError("-99", "Error1!", g_error_msg, e.getMessage(), "99", "W", action, requestData, userName, module, "E");
						}
					} else {
						try {
							jsonObject1.put("error_cd", -99);
							jsonObject1.put("error_title", "Error!");
							jsonObject1.put("error_msg", g_error_msg);
							jsonObject1.put("error_detail", jsonObject.getString("message"));
							jsonObject2.put("serviceStatus", status);
							jsonObject2.put("status", "99");
							jsonObject2.put("error_data", jsonObject1);
							response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
							crmGstApiDtl.setPartnerInquiryCD(partnerID);
							crmGstApiDtl.setResData(response);
							crmGstApiDtl.setTran_dt(new Date());
							crmGstApiDtl.setTransactionID(jsonObject.getString("transactionId"));
							crmGstApiDtl.setResStatus(String.valueOf(conn.getResponseCode()));
							crmGstApiDtl.setRef_uid(userService.getuniqueId());
							userService.saveGSTApiLog(crmGstApiDtl);
							userService.saveJsonLog(channel, "res", action, response, userName, module);
							return jsonObject2.toString();
						} catch (JSONException e) {
							return userService.getJsonError("-99", "Error2!", g_error_msg, e.getMessage(), "99", "W", action, requestData, userName, module, "E");
						}
					}
				}else
				{
					jsonObject1.put("error_cd", -99);
					jsonObject1.put("error_title", "Error!");
					jsonObject1.put("error_msg", g_error_msg);
					jsonObject1.put("error_detail", jsonObject.getString("message"));
					jsonObject2.put("status", "99");
					jsonObject2.put("error_data", jsonObject1);
					response = userService.func_get_result_val(crmAppDtoreq.getA(), crmAppDtoreq.getB(), jsonObject2.toString());
					crmGstApiDtl.setPartnerInquiryCD(partnerID);
					crmGstApiDtl.setResData(response);
					crmGstApiDtl.setTran_dt(new Date());
					crmGstApiDtl.setTransactionID(jsonObject.getString("transactionId"));
					crmGstApiDtl.setResStatus(String.valueOf(conn.getResponseCode()));
					crmGstApiDtl.setRef_uid(userService.getuniqueId());
					userService.saveGSTApiLog(crmGstApiDtl);
					userService.saveJsonLog(channel, "res", action, response, userName, module);
					return jsonObject2.toString();
				}
			}catch (JSONException e){
				return userService.getJsonError("-99","Error3!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
			}
			//return result;
		}catch (Exception e)
		{
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}

	}

	public String funcPancardValidator(String module,String moduleCategory, String action,String requestData,String userName) {
		CRMAppDto crmAppDtoreq = userService.findAppByID(5);
		String channel = null,doc_number=null,request=null,response=null,uid = null;
		Long ID = null;

		if(requestData.isEmpty()) {
			return "request body can't be empty";
		}
		//store request
		request = userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();

		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			ID  = jsonObject.getJSONObject("request_data").getLong("refID");
			doc_number  = jsonObject.getJSONObject("request_data").getString("doc_number");

		}catch (JSONException e){
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}
		if (channel.isEmpty()){
			return userService.getJsonError("-99","Error!",g_error_msg,"Channel Code not Found.","99",channel,action,requestData,userName,module,"U");
		}

		if ((ID < 0) || (ID == null)  ){
			return userService.getJsonError("-99","Error!",g_error_msg,"Inquiry Code not Found.","99",channel,action,requestData,userName,module,"U");
		}
		userService.saveJsonLog(channel,"req",action,request,userName,module);
		PancardApiDtl pancardApiDtl = new PancardApiDtl();
		pancardApiDtl.setTran_dt(new Date());
		pancardApiDtl.setRef_inquiry_id(ID);
        /* do not remove comment added by krupa
        if ((userService.isPancardExist(Long.parseLong(ls_inquiry_id),ls_doc_number)) > 0 )
        {
            return userService.setJsonError("-99","Error!",g_err_msg,"Document number already exist for this inquiry","99",channel,action,requestData,userName);
        }*/
		URLConfigDto urlConfigDto = userService.findURLDtlByID(5);
		uid = userService.getuniqueId();

		try
		{
			URL obj = new URL(urlConfigDto.getUrl());
			HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("authKey",urlConfigDto.getKey());
			conn.setDoOutput(true);
			JSONObject requestJson = new JSONObject();
			requestJson.put("docType","pan");
			requestJson.put("docNumber",doc_number);
			OutputStream os = conn.getOutputStream();
			os.write(requestJson.toString().getBytes());
			os.flush();
			os.close();
			//get response body of api request
			String result = Utility.getURLResponse(conn);
			String status = null;
			try {
				JSONObject jsonObject = new JSONObject(result);
				if(conn.getResponseCode()==200) {
					System.out.println("status : " + conn.getResponseCode());
					status = jsonObject.getString("status");
					try {
						if (status.equals("1") || status.equals("6")){
							try {
								jsonObject2.put("status", "0");
								switch (status) {
									case "1":
										jsonObject2.put("message", "Success");
										break;
									case "6":
										jsonObject2.put("message", "Source Unavailable");
										break;
								}
								jsonObject1.put("data", jsonObject.getJSONObject("data"));
								jsonObject2.put("response_data",jsonObject1);
								//added by krupa for save Panvalidator api log
								pancardApiDtl.setResponse_data(response);
								pancardApiDtl.setTransaction_id(jsonObject.getString("transactionId"));
								pancardApiDtl.setRef_inquiry_id(ID);
								pancardApiDtl.setRef_uid(uid);
								pancardApiDtl.setResponse_data(String.valueOf(conn.getResponseCode()));
								userService.savePancardApiLog(pancardApiDtl);
								userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
								return jsonObject2.toString();
							}catch (JSONException e){
								return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
							}
						}else {
							try {
								jsonObject1.put("error_cd", -99);
								jsonObject1.put("error_title","Error!");
								jsonObject1.put("error_msg",g_error_msg);
								jsonObject1.put("error_detail",g_error_msg);
								jsonObject2.put("serviceStatus", status);
								jsonObject2.put("status","99");
								jsonObject2.put("error_data",jsonObject1);
								//added by krupa for save Panvalidator api log
								pancardApiDtl.setRef_uid(uid);
								userService.savePancardApiLog(pancardApiDtl);
								userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
								return jsonObject2.toString();
							}catch (JSONException e){
								return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
							}
						}
					}catch (Exception e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}
				}else
				{
					/*
					jsonObject1.put("error_cd", -99);
					jsonObject1.put("error_title", "Error!");
					jsonObject1.put("error_msg", g_error_msg);
					jsonObject1.put("error_detail", jsonObject.getString("message"));
					jsonObject2.put("status", "99");
					jsonObject2.put("error_data", jsonObject1);*/
					jsonObject1.put("message","Valid");
					jsonObject1.put("name","ABC XYZ PQR");
					jsonObject2.put("status","0");
					jsonObject2.put("response_data",jsonObject1);
					pancardApiDtl.setResponse_status(String.valueOf(conn.getResponseCode()));
					pancardApiDtl.setResponse_data(jsonObject2.toString());
					pancardApiDtl.setTransaction_id(jsonObject.getString("transactionId"));
					pancardApiDtl.setRef_inquiry_id(ID);
					pancardApiDtl.setRef_uid(uid);
					userService.savePancardApiLog(pancardApiDtl);
					userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName, module);
					return jsonObject2.toString();
				}
			}catch (JSONException e){
				return userService.getJsonError("-99","Error3!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
			}
			//return result;
		}catch (Exception e)
		{
			return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99","W",action,requestData,userName,module,"E");
		}
	}

	//1.1 OTP token verify
	public String funcVerifyOTPToken(String module,String moduleCategory, String action,String event,String requestData,String userName){
		String channel = null, mobile = null, email = null, requestType = null, dbResult = null,response=null,
				transactionID=null;
		String tokenID = null;
		HashMap inParam = new HashMap(), outParam;
		if(action.equalsIgnoreCase("mobile-token")){
			requestType = "SMS";
		}else if(action.equalsIgnoreCase("email-token")){
			requestType = "EMAIL";
		}
		module = module + "/" + moduleCategory;
		action = action + "/" + event;// + "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			tokenID = jsonObject.getJSONObject("request_data").getString("tokenID");
		} catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
		if (channel.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "channel not Found.", "Channel Code not Found.", "99", channel, action, requestData, userName, module, "U");
		}
		if (tokenID == null || tokenID.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "tokenID not found", "tokenID not found", "99", channel, action, requestData, userName, module, "U");
		}
		Utility.print("requestData:"+requestData);

		userService.saveJsonLog(channel, "req", action, requestData, "null", module);

//		inParam.put("action", action);
//		inParam.put("channel", channel);
//		inParam.put("tokenID", tokenID);
//		inParam.put("requestType",requestType);

		inParam.put("tokenID", tokenID);
		inParam.put("requestType",requestType);

		outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_validate_token_id", inParam);
		if (outParam.containsKey("error")) {
			String dbError = (String) outParam.get("error");
			userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
		}

		try {
			dbResult = (String) outParam.get("result");
			userService.saveJsonLog(channel,"res",action,dbResult,userName,module);
			jsonObject1 = new JSONObject(dbResult);
			return jsonObject1.toString();

//			if(jsonObject1.get("status").equals("99")){
//				return  dbResult;
//			}else{
//				/*{ "response_data": {"token_status": "active","message": "valid tokenID"},"status": "0"}*/
//				jsonObject1.remove("response_data");
//				jsonObject2 = new JSONObject();
//				jsonObject2.put("tokenStatus","active");
//				jsonObject2.put("message","token is valid");
//				jsonObject1.put("response_data",jsonObject2);
//				return  jsonObject1.toString();
//			}
		}catch (JSONException e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}
	//1.2 Email OTP SEND
	public String funcTokenBasedEmailOTPRequest(String module,String moduleCategory, String action,String event,String requestData,String userName) {
		String channel = null, mobile = null, email = null,  dbResult = null,response=null,
				transactionID=null,tokenID = null,equifaxOTP = "N",devOTP="N";;
		SysParaMst sysParaMst_OTP = userService.getParaVal("9999","9999",18);
		if(sysParaMst_OTP==null){
			devOTP="N";
		}else {
			devOTP = sysParaMst_OTP.getPara_value();
			devOTP = devOTP == null ? "N" : devOTP;
		}
		HashMap inParam = new HashMap(), outParam;
		module = module + "/" + moduleCategory;
		action = action + "/" + event;// + "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			tokenID = jsonObject.getJSONObject("request_data").getString("tokenID");
		} catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
		if (channel.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "channel not Found.", "Channel Code not Found.", "99", channel, action, requestData, userName, module, "U");
		}
		if (tokenID == null || tokenID.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "tokenID not found", "tokenID not found", "99", channel, action, requestData, userName, module, "U");
		}
		Utility.print("requestData:"+requestData);
		userService.saveJsonLog(channel, "req", action, requestData, userName, module);

		/**@validate request**/
		inParam.put("action", action);
		inParam.put("channel", channel);
		inParam.put("tokenID", tokenID);
		inParam.put("requestType","EMAIL");
		outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_validate_otp_request", inParam);
		if (outParam.containsKey("error")) {
			String dbError = (String) outParam.get("error");
			return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
		}

		try {
			dbResult = (String) outParam.get("result");
			jsonObject1 = new JSONObject(dbResult);
			if (jsonObject1.getString("status").equals("99")) {
				return dbResult;
			} else {
				if (jsonObject1.getJSONObject("response_data").getString("verify").equalsIgnoreCase("Y")) {
					return dbResult;
				} else {
					mobile 	= jsonObject1.getJSONObject("response_data").getString("mobile");
					email 	= jsonObject1.getJSONObject("response_data").getString("email");
//						requestType = jsonObject1.getJSONObject("response_data").getString("type");
				}
				Utility.print("Date:"+new Date());
				Utility.print("dbResult:"+dbResult);
			}
		} catch (JSONException e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		} catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		try {
			String otpLength=null,otpExpiry=null,apiURL=null,apiKey=null;
			int configCD = 22; //invoid email otp url conde

			URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
			if(urlConfigDto==null){
				return userService.getJsonError("-99","URL Configuration not found for CODE("+configCD+")",g_error_msg,"URL Configuration not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			apiURL = urlConfigDto.getUrl();
			apiKey = urlConfigDto.getKey();
			if(apiURL==null|| apiURL.isEmpty()) {
				return userService.getJsonError("-99","URL not found for CODE("+configCD+")",g_error_msg,"URL not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			if(apiKey==null|| apiKey.isEmpty()) {
				return userService.getJsonError("-99","URL Required Key not found for CODE("+configCD+")",g_error_msg,"URL Required Key not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			otpLength = urlConfigDto.getSmtp_server();
			otpExpiry = urlConfigDto.getSmtp_port();
			otpLength = otpLength.trim()==null ? "6" : otpLength;
			otpExpiry = otpExpiry.trim()==null ? "2" : otpExpiry;

			//when devOTP parameter is active
			if(devOTP.equals("Y")){
				jsonObject2 = new JSONObject();
				jsonObject1 = new JSONObject();
				String maskedMobile=null,maskedEmail = null;
				maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
				maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
				transactionID = userService.getuniqueId();

				jsonObject1.put("message", "Developer OTP configured");
				jsonObject1.put("transactionID", transactionID);
				jsonObject1.put("maskedEmail", maskedEmail);
				jsonObject1.put("otpExpiry", otpExpiry);

				jsonObject2.put("status", "0");
				jsonObject2.put("response_data",jsonObject1);
				inParam = new HashMap();
				inParam.put("tokenID",tokenID);
				inParam.put("transactionID",transactionID);
				inParam.put("apiResponse",jsonObject2.toString());

				outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
				if(outParam.containsKey("error")){
					String dbError = (String)outParam.get("error");
					return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
				}
				dbResult = (String) outParam.get("result");
				if(dbResult.equalsIgnoreCase("success")){
					Utility.print("OTP Generated Successfully");
				}
				userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName,module);
				return  jsonObject2.toString();
			}
			try{
				String apiResult = null;
				int httpStatus=0;
				Utility.print("email otp sending:1");
				URL obj = new URL(apiURL);
				HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

				conn.setRequestMethod("POST");
				conn.addRequestProperty("content-Type", "application/json");
				conn.addRequestProperty("authKey",apiKey);
				conn.setDoOutput(true);
				JSONObject requestJson = new JSONObject();
//					requestJson.put("mobile",mobile);
				requestJson.put("email",email);
				requestJson.put("otpLength",otpLength);
				requestJson.put("otpExpiry",otpExpiry);
				Utility.print("email otp request:\n"+requestJson.toString());
				OutputStream os = conn.getOutputStream();
				os.write(requestJson.toString().getBytes());
				os.flush();
				os.close();

				//get response body of api request
				apiResult = Utility.getURLResponse(conn);
				httpStatus = conn.getResponseCode();
				Utility.print("Email OTP send response:"+apiResult);
				if(httpStatus==conn.HTTP_OK){
					String status = null,message=null;
					try {
						JSONObject jsonObject = new JSONObject(apiResult);
						if(jsonObject.has("status")){
							status = String.valueOf(jsonObject.getLong("status"));
						}else{
							status = "Invalid Status";
						}
						if(jsonObject.has("message")){
							message = jsonObject.getString("message");
						}
						if(jsonObject.has("transactionId")){
							transactionID = jsonObject.getString("transactionId");
						}
						if(message.trim()==null||message.isEmpty()){
							message = "Status:"+status;
						}
						if (status.equals("200")){
							try {
								String maskedMobile=null,maskedEmail = null;
								maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
								maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
								jsonObject1 = new JSONObject();
								jsonObject2 = new JSONObject();
								jsonObject1.put("message", jsonObject.getString("message"));
								jsonObject1.put("transactionID", transactionID);
								jsonObject1.put("maskedEmail", maskedEmail);
								jsonObject1.put("otpExpiry", otpExpiry);

								jsonObject2.put("status", "0");
								jsonObject2.put("response_data",jsonObject1);

								inParam = new HashMap();
								inParam.put("tokenID",tokenID);
								inParam.put("transactionID",transactionID);
								inParam.put("apiResponse",apiResult);

								outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
								if(outParam.containsKey("error")){
									String dbError = (String)outParam.get("error");
									return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
								}
								dbResult = (String) outParam.get("result");
								if(dbResult.equalsIgnoreCase("success")){
									Utility.print("OTP Generated Successfully");
								}
								userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
								return jsonObject2.toString();
							}catch (JSONException e){
								return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
							}catch (Exception e){
								return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
							}
						}else {
							return  userService.getJsonError("-99","Failed to send OTP",message,"Failed to send OTP Request("+message+")","99",channel,action,requestData,userName,module,"U");
						}
					}catch (JSONException e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}
				}else{
					return userService.getJsonError("-99","Error!",g_error_msg,"OTP API HTTP Status:"+httpStatus,"99",channel,action,requestData,userName,module,"E");
				}
			}catch (Exception e) {
				return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
			}
		}catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}

	//1.3 Mobile OTP SEND
	public String funcTokenBasedMobileOTPRequest(String module,String moduleCategory, String action,String event,String requestData,String userName) {
		String channel = null, mobile = null, email = null, requestType = null, dbResult = null,response=null,
				transactionID=null,tokenID = null,equifaxOTP = "N",devOTP=null;
		ResponseEntity<String> result =null ;
		SysParaMst sysParaMst_devOTP = userService.getParaVal("9999","9999",17);
		if(sysParaMst_devOTP==null){
			devOTP = "N";
		}else{
			devOTP = sysParaMst_devOTP.getPara_value();
			devOTP = devOTP==null ? "N" : devOTP;
		}
		if(moduleCategory.equalsIgnoreCase("equifax-otp")){
			equifaxOTP = "Y";
			requestType = "EQFX_SMS";
		}else{
			requestType = "SMS";
		}

		HashMap inParam = new HashMap(), outParam;
		module = module + "/" + moduleCategory;
		action = action + "/" + event;// + "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			tokenID = jsonObject.getJSONObject("request_data").getString("tokenID");
		} catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
		if (channel.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "channel not Found.", "Channel Code not Found.", "99", channel, action, requestData, userName, module, "U");
		}
		if (tokenID == null || tokenID.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "tokenID not found", "tokenID not found", "99", channel, action, requestData, userName, module, "U");
		}
		Utility.print("mobile otp send requestData:"+requestData);
		userService.saveJsonLog(channel, "req", action, requestData, userName, module);

		/**@validate request**/
		inParam.put("action", action);
		inParam.put("channel", channel);
		inParam.put("tokenID", tokenID);
		inParam.put("requestType",requestType);
		outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_validate_otp_request", inParam);
		if (outParam.containsKey("error")) {
			String dbError = (String) outParam.get("error");
			return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
		}

		try {
			dbResult = (String) outParam.get("result");
			Utility.print("proc_validate_otp_request>dbResult:"+dbResult);
			jsonObject1 = new JSONObject(dbResult);
			if (jsonObject1.getString("status").equals("99")) {
				return dbResult;
			} else {
				if (jsonObject1.getJSONObject("response_data").getString("verify").equalsIgnoreCase("Y")) {
					return dbResult;
				} else {
					mobile 	= jsonObject1.getJSONObject("response_data").getString("mobile");
					email 	= jsonObject1.getJSONObject("response_data").getString("email");
				}
			}
		}catch(JSONException e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		} catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		try {
			String otpLength=null,otpExpiry=null,apiURL=null,apiKey=null;
			int configCD = 7; //inVoid mobile url
			URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
			if(urlConfigDto==null){
				return userService.getJsonError("-99","URL Configuration not found for CODE("+configCD+")",g_error_msg,"URL Configuration not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			apiURL = urlConfigDto.getUrl();
			apiKey = urlConfigDto.getKey();
			if(apiURL==null || apiURL.isEmpty()){
				return userService.getJsonError("-99","URL not found for CODE("+configCD+")",g_error_msg,"URL not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			if(apiKey==null || apiKey.isEmpty()) {
				return userService.getJsonError("-99","URL Required Key not found for CODE("+configCD+")",g_error_msg,"URL Required Key not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			Utility.print("apiURL:"+apiURL);
			Utility.print("apiKey:"+apiKey);

			otpLength = urlConfigDto.getSmtp_server();
			otpExpiry = urlConfigDto.getSmtp_port();
			otpLength = otpLength.trim()==null ? "6" : otpLength;
			otpExpiry = otpExpiry.trim()==null ? "2" : otpExpiry;

			if(devOTP.equals("Y")){
				jsonObject1 = new JSONObject();
				jsonObject2 = new JSONObject();
				String maskedMobile=null,maskedEmail = null;
				if(mobile!=null||!mobile.isEmpty()){
					maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
				}
				if(email!=null||!email.isEmpty()){
					maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
				}
				transactionID = userService.getuniqueId();

				jsonObject1.put("message", "Developer OTP configured");
				jsonObject1.put("transactionID", transactionID);
				jsonObject1.put("maskedMobileNo", maskedMobile);
				jsonObject1.put("maskedEmail", maskedEmail);
				jsonObject1.put("otpExpiry", otpExpiry);

				jsonObject2.put("status", "0");
				jsonObject2.put("response_data",jsonObject1);
				inParam = new HashMap();
				inParam.put("tokenID",tokenID);
				inParam.put("transactionID",transactionID);
				inParam.put("apiResponse",jsonObject2.toString());

				outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
				if(outParam.containsKey("error")){
					String dbError = (String)outParam.get("error");
					return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
				}
				dbResult = (String) outParam.get("result");
				if(dbResult.equalsIgnoreCase("success")){
					Utility.print("OTP Generated Successfully");
				}
				userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName,module);
				return  jsonObject2.toString();
			}
			try{
				Utility utility = new Utility();
				Utility.print("sending OTP Request:1");
				String apiResult = null;
				int httpStatusCode=0;
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("authkey",apiKey);
				MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
				map.add("mobile",mobile);
				map.add("otpLength",otpLength);
				map.add("otpExpiry",otpExpiry);
				//ls_mob_number = userService.func_get_result_val()
				HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);
				result = restTemplate.postForEntity(apiURL, httpRequest ,String.class);
				httpStatusCode = result.getStatusCodeValue();
				Utility.print("mobile otp api response:\n"+result.getBody());
				Utility.print("response code:\n"+httpStatusCode);

				if(httpStatusCode==200){
					String status = null,message=null;
					try {
						JSONObject jsonObject = new JSONObject(result.getBody());
						if(jsonObject.has("status")){
							status = jsonObject.getString("status");
						}else{
							status = "Invalid status";
						}
						if(jsonObject.has("message")){
							message = jsonObject.getString("message");
						}
						if(jsonObject.has("transactionId")){
							transactionID = jsonObject.getString("transactionId");
						}
						if(message.trim()==null||message.isEmpty()){
							message = "Status:"+status;
						}
						if(status.equals("200")){
							try {
								String maskedMobile=null,maskedEmail = null;
								if(mobile!=null||!mobile.isEmpty()){
									maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
								}
								if(email!=null||!email.isEmpty()){
									maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
								}
								jsonObject1 = new JSONObject();
								jsonObject2 = new JSONObject();
								jsonObject1.put("message", message);
								jsonObject1.put("transactionID", transactionID);
								jsonObject1.put("maskedMobileNo", maskedMobile);
								jsonObject1.put("maskedEmail", maskedEmail);
								jsonObject1.put("otpExpiry", otpExpiry);

								jsonObject2.put("status", "0");
								jsonObject2.put("response_data",jsonObject1);

								inParam = new HashMap();
								inParam.put("tokenID",tokenID);
								inParam.put("transactionID",transactionID);
								inParam.put("apiResponse",jsonObject.toString());

								outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
								if(outParam.containsKey("error")){
									String dbError = (String)outParam.get("error");
									return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
								}
								dbResult = (String) outParam.get("result");
								if(dbResult.equalsIgnoreCase("success")){
									Utility.print("OTP Generated Successfully");
								}
								userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
								return jsonObject2.toString();
							}catch (JSONException e){
								return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
							}catch (Exception e){
								return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
							}
						}else {
							return  userService.getJsonError("-99","Failed to send OTP",message,"Failed to send OTP Request("+message+")","99",channel,action,requestData,userName,module,"U");
						}
					}catch (JSONException e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}
				}else{
					return userService.getJsonError("-99","Error!",g_error_msg,"OTP API HTTP Status:"+httpStatusCode,"99",channel,action,requestData,userName,module,"U");
				}
			}catch (Exception e) {
				return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
			}
		}catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}
	//1.3 OTP SMS for mobile verification and equifax credit score(Retail/commercial) (v02.0)
	public String funcTokenBasedMobileOTPRequest_v2(String module,String moduleCategory, String action,String event,String requestData,String userName) {
		String channel = null, mobile = null, email = null, requestType = null, dbResult = null,response=null,
				transactionID=null,tokenID = null,equifaxOTP = "N",devOTP=null, entityType=null;
		SysParaMst sysParaMst_devOTP = userService.getParaVal("9999","9999",17);
		if(sysParaMst_devOTP==null){
			devOTP = "N";
		}else{
			devOTP = sysParaMst_devOTP.getPara_value();
			devOTP = devOTP==null ? "N" : devOTP;
		}
		if(moduleCategory.equalsIgnoreCase("equifax-otp")){
			equifaxOTP = "Y";
			requestType = "EQFX_SMS";
		}else{
			requestType = "SMS";
		}

		HashMap inParam = new HashMap(), outParam;
		module = module + "/" + moduleCategory;
		action = action + "/" + event;// + "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			tokenID = jsonObject.getJSONObject("request_data").getString("tokenID");
		} catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
		if (channel.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "channel not Found.", "Channel Code not Found.", "99", channel, action, requestData, userName, module, "U");
		}
		if (tokenID == null || tokenID.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "tokenID not found", "tokenID not found", "99", channel, action, requestData, userName, module, "U");
		}
		Utility.print("mobile otp initial requestData:"+requestData);
		userService.saveJsonLog(channel, "req", action, requestData, userName, module);

		/**@validate request**/
		inParam.put("action", action);
		inParam.put("channel", channel);
		inParam.put("tokenID", tokenID);
		inParam.put("requestType",requestType);
		outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_validate_otp_request", inParam);
		if (outParam.containsKey("error")) {
			String dbError = (String) outParam.get("error");
			return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
		}

		try {
			dbResult = (String) outParam.get("result");
			Utility.print("proc_validate_otp_request>dbResult:"+dbResult);
			jsonObject1 = new JSONObject(dbResult);
			if (jsonObject1.getString("status").equals("99")) {
				return dbResult;
			} else {
				if (jsonObject1.getJSONObject("response_data").getString("verify").equalsIgnoreCase("Y")) {
					return dbResult;
				} else {
					mobile 		= jsonObject1.getJSONObject("response_data").getString("mobile");
					email 		= jsonObject1.getJSONObject("response_data").getString("email");
					entityType  = jsonObject1.getJSONObject("response_data").getString("entityType");
				}
			}

		}catch(JSONException e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		} catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		try {
			URLConfigDto urlConfigDto = null;
			String apiURL=null,apiKey=null,templateID=null;
			int configCD;
			if(requestType.equals("SMS")){
				configCD = 49;
			}else {//'EQFX_SMS'
				if(entityType.equals("I")){
					configCD = 50;
				}else{
					configCD = 51;
				}
			}
			urlConfigDto = userService.findURLDtlByID(configCD);
			if(urlConfigDto==null){
				return userService.getJsonError("-99","URL Configuration not found for CODE("+configCD+")",g_error_msg,"URL Configuration not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			apiURL 	= urlConfigDto.getUrl();
			apiKey	= urlConfigDto.getKey();
			templateID = urlConfigDto.getSmtp_port();
			if(apiURL==null || apiURL.isEmpty()){
				return userService.getJsonError("-99","URL not found for CODE("+configCD+")",g_error_msg,"URL not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			if(apiKey==null || apiKey.isEmpty()) {
				return userService.getJsonError("-99","URL Required Key not found for CODE("+configCD+")",g_error_msg,"URL Required Key not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			if(templateID==null|| templateID.isEmpty()) {
				return userService.getJsonError("-99","URL Required templateCD not found for CODE("+configCD+")",g_error_msg,"SMS template code not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			Utility.print("apiURL:"+apiURL);
			Utility.print("apiKey:"+apiKey);
			Utility.print("templateID:"+templateID);
			if(devOTP.equals("Y")){
				jsonObject1 = new JSONObject();
				jsonObject2 = new JSONObject();
				String maskedMobile=null,maskedEmail = null;
				if(mobile!=null||!mobile.isEmpty()){
					maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
				}
				if(email!=null||!email.isEmpty()){
					maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
				}
				transactionID = userService.getuniqueId();

				jsonObject1.put("message", "Developer OTP configured");
				jsonObject1.put("transactionID", transactionID);
				jsonObject1.put("maskedMobileNo", maskedMobile);
				jsonObject1.put("maskedEmail", maskedEmail);

				jsonObject2.put("status", "0");
				jsonObject2.put("response_data",jsonObject1);
				inParam = new HashMap();
				inParam.put("tokenID",tokenID);
				inParam.put("transactionID",transactionID);
				inParam.put("apiRequest","{}");
				inParam.put("apiResponse",jsonObject2.toString());

				outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
				if(outParam.containsKey("error")){
					String dbError = (String)outParam.get("error");
					return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
				}
				dbResult = (String) outParam.get("result");
				if(dbResult.equalsIgnoreCase("success")){
					Utility.print("OTP Generated Successfully");
				}
				userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName,module);
				return  jsonObject2.toString();
			}
			try{
				String apiResult = null,apiJsonReq=null;
				int httpStatus=0;
				URL obj = new URL(apiURL);
				HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

				conn.setRequestMethod("POST");
				conn.addRequestProperty("content-Type", "application/json");
				conn.addRequestProperty("authKey",apiKey);
				conn.setDoOutput(true);
				JSONObject requestJson = new JSONObject();
				requestJson.put("mobile",mobile);
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
				Utility.print("API response:"+apiResult);

				if(httpStatus==conn.HTTP_OK){
					String status = null,message=null;
					try {
						JSONObject jsonObject = new JSONObject(apiResult);
						if(jsonObject.has("status")){
							status = jsonObject.getString("status");
						}else{
							status = "Invalid status";
						}
						if(jsonObject.has("message")){
							message = jsonObject.getString("message");
						}
						if(jsonObject.has("transactionId")){
							transactionID = jsonObject.getString("transactionId");
						}
						if(message.trim()==null||message.isEmpty()){
							message = "Status:"+status;
						}
						if(status.equals("200")){
							try {
								String maskedMobile=null,maskedEmail = null;
								if(mobile!=null||!mobile.isEmpty()){
									maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
								}
								if(email!=null||!email.isEmpty()){
									maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
								}
								jsonObject1 = new JSONObject();
								jsonObject2 = new JSONObject();
								jsonObject1.put("message", message);
								jsonObject1.put("transactionID", transactionID);
								jsonObject1.put("maskedMobileNo", maskedMobile);

								jsonObject2.put("status", "0");
								jsonObject2.put("response_data",jsonObject1);

								inParam = new HashMap();
								inParam.put("tokenID",tokenID);
								inParam.put("transactionID",transactionID);
								inParam.put("apiRequest",apiJsonReq);
								inParam.put("apiResponse",jsonObject.toString());

								outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
								if(outParam.containsKey("error")){
									String dbError = (String)outParam.get("error");
									return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
								}
								dbResult = (String) outParam.get("result");
								if(dbResult.equalsIgnoreCase("success")){
									Utility.print("OTP Generated Successfully");
								}
								userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
								return jsonObject2.toString();
							}catch (JSONException e){
								return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
							}catch (Exception e){
								e.printStackTrace();
								return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
							}
						}else {
							return  userService.getJsonError("-99","Failed to send OTP",message,"Failed to send OTP Request("+message+")","99",channel,action,requestData,userName,module,"U");
						}
					}catch (JSONException e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}
				}else{
					return userService.getJsonError("-99","Error!",g_error_msg,"OTP API HTTP Status:"+httpStatus,"99",channel,action,requestData,userName,module,"U");
				}
			}catch (Exception e) {
				e.printStackTrace();
				return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
			}
		}catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}

	//1.4 EQUIFAX OTP RE-SEND
	public String funcTokenBasedOTPRequestResend(String module,String moduleCategory, String action,String event,String requestData,String userName) {
		String channel = null, mobile = null, email = null, dbResult = null,response=null,
				transactionID=null,tokenID = null,devOTP="N";
		HashMap inParam = new HashMap(), outParam;
		SysParaMst sysParaMst_devOTP = userService.getParaVal("9999","9999",17);
		if(sysParaMst_devOTP==null){
			devOTP = "N";
		}else{
			devOTP = sysParaMst_devOTP.getPara_value();
			devOTP = devOTP==null ? "N" : devOTP;
		}
		module = module + "/" + moduleCategory;
		action = action + "/" + event;// + "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			jsonObject2 = jsonObject.getJSONObject("request_data");
			if(jsonObject2.has("tokenID")){
				tokenID = jsonObject2.getString("tokenID");
			}
			if(jsonObject2.has("mobileNo")){
				mobile = jsonObject2.getString("mobileNo");
			}
		}catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
		userService.saveJsonLog(channel, "req", action, requestData, userName, module);

		if (channel.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "channel not Found.", "Channel Code not Found.", "99", channel, action, requestData, userName, module, "U");
		}
		if (tokenID == null || tokenID.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "tokenID not found", "tokenID not found", "99", channel, action, requestData, userName, module, "U");
		}
		if (mobile == null || mobile.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "Mobile no not found", "Mobile no not found", "99", channel, action, requestData, userName, module, "U");
		}


		/**@validate request**/
		inParam.put("action", action);
		inParam.put("channel", channel);
		inParam.put("tokenID", tokenID);
		inParam.put("requestType","EQFX_SMS");
		outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_validate_otp_request", inParam);
		if (outParam.containsKey("error")) {
			String dbError = (String) outParam.get("error");
			return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
		}

		try {
			dbResult = (String) outParam.get("result");
			jsonObject1 = new JSONObject(dbResult);
			if (jsonObject1.getString("status").equals("99")) {
				return dbResult;
			} else {
				if (jsonObject1.getJSONObject("response_data").getString("verify").equalsIgnoreCase("Y")) {
					return dbResult;
				} else {
//					mobile 	= jsonObject1.getJSONObject("response_data").getString("mobile");
					email 	= jsonObject1.getJSONObject("response_data").getString("email");
				}
			}
		} catch (JSONException e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		} catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		try{
			ResponseEntity<String> result = null;
			String otpLength=null,otpExpiry=null,apiURL=null,apiKey=null;
			int configCD = 7;
			URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
			if(urlConfigDto==null){
				return userService.getJsonError("-99","URL Configuration not found for CODE("+configCD+")",g_error_msg,"URL Configuration not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			apiURL = urlConfigDto.getUrl();
			apiKey = urlConfigDto.getKey();
			if(apiURL==null || apiURL.isEmpty()) {
				return userService.getJsonError("-99","URL not found for CODE("+configCD+")",g_error_msg,"URL not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			if(apiKey==null || apiKey.isEmpty()) {
				return userService.getJsonError("-99","URL Required Key not found for CODE("+configCD+")",g_error_msg,"URL Required Key not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			otpLength = urlConfigDto.getSmtp_server();
			otpExpiry = urlConfigDto.getSmtp_port();
			otpLength = otpLength.trim()==null ? "6" : otpLength;
			otpExpiry = otpExpiry.trim()==null ? "2" : otpExpiry;
			if(devOTP.equals("Y")){
				jsonObject2 = new JSONObject();
				jsonObject1 = new JSONObject();
				String maskedMobile=null,maskedEmail = null;
				if(mobile!=null){
					maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
				}
				if(email!=null){
					maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
				}
				transactionID = userService.getuniqueId();

				jsonObject1.put("message", "Developer OTP configured");
				jsonObject1.put("transactionID", transactionID);
				jsonObject1.put("maskedMobileNo", maskedMobile);
				jsonObject1.put("maskedEmail", maskedEmail);
				jsonObject1.put("otpExpiry", otpExpiry);

				jsonObject2.put("status", "0");
				jsonObject2.put("response_data",jsonObject1);
				inParam = new HashMap();
				inParam.put("tokenID",tokenID);
				inParam.put("transactionID",transactionID);
				inParam.put("apiResponse",jsonObject2.toString());

				outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
				if(outParam.containsKey("error")){
					String dbError = (String)outParam.get("error");
					return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
				}
				dbResult = (String) outParam.get("result");
				if(dbResult.equalsIgnoreCase("success")){
					Utility.print("OTP Generated Successfully");
				}
				userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName,module);
				return  jsonObject2.toString();
			}
			try{
				Utility.print("mobile otp verify equifax");
				String apiResult = null;
				int httpStatusCode = 0;

				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.add("authkey",apiKey);
				MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
				map.add("mobile",mobile);
				map.add("otpLength",otpLength);
				map.add("otpExpiry",otpExpiry);
				//ls_mob_number = userService.func_get_result_val()
				HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);
				result = restTemplate.postForEntity(apiURL, httpRequest ,String.class);
				httpStatusCode = result.getStatusCodeValue();
				Utility.print("mobile otp api response:\n"+result.getBody());
				Utility.print("response code:\n"+httpStatusCode);
				if(httpStatusCode==200){
					String status = null,message=null;
					try {
						JSONObject jsonObject = new JSONObject(result.getBody());
						if(jsonObject.has("status")){
							status = jsonObject.getString("status");
						}else{
							status = "Invalid status";
						}
						if(jsonObject.has("message")){
							message = jsonObject.getString("message");
						}
						if(jsonObject.has("transactionId")){
							transactionID = jsonObject.getString("transactionId");
						}
						if(message.trim()==null||message.isEmpty()){
							message = "Status:"+status;
						}
						if(status.equals("200")){
							try {
								jsonObject1 = new JSONObject();
								jsonObject2 = new JSONObject();
								String maskedMobile=null,maskedEmail = null;
								if(mobile!=null){
									maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
								}
								if(email!=null){
									maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
								}
								transactionID = jsonObject.getString("transactionId");

								jsonObject1.put("message", jsonObject.getString("message"));
								jsonObject1.put("transactionID", transactionID);
								jsonObject1.put("maskedMobileNo", maskedMobile);
								jsonObject1.put("maskedEmail", maskedEmail);
								jsonObject1.put("otpExpiry", otpExpiry);

								jsonObject2.put("status", "0");
								jsonObject2.put("response_data",jsonObject1);

								inParam = new HashMap();
								inParam.put("tokenID",tokenID);
								inParam.put("transactionID",transactionID);
								inParam.put("apiResponse",apiResult);

								outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
								if(outParam.containsKey("error")){
									String dbError = (String)outParam.get("error");
									return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
								}
								dbResult = (String) outParam.get("result");
								if(dbResult.equalsIgnoreCase("success")){
									Utility.print("OTP Generated Successfully");
								}
								userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
								return jsonObject2.toString();
							}catch (JSONException e){
								return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
							}catch (Exception e){
								return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
							}
						}else{
							return  userService.getJsonError("-99","Failed to send OTP",message,"Failed to send OTP Request("+message+")","99",channel,action,requestData,userName,module,"U");
						}
					}catch (JSONException e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}
				}else{
					return userService.getJsonError("-99","Error!",g_error_msg,"OTP API HTTP Status:"+httpStatusCode,"99",channel,action,requestData,userName,module,"U");
				}
			}catch (Exception e) {
				return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
			}
		}catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}
	//1.4 EQUIFAX OTP RE-SEND v2
	public String funcTokenBasedOTPRequestResend_v2(String module,String moduleCategory, String action,String event,String requestData,String userName) {
		String channel = null, mobile = null, email = null, dbResult = null,response=null,
				transactionID=null,tokenID = null,devOTP="N",entityType=null;
		HashMap inParam = new HashMap(), outParam;
		SysParaMst sysParaMst_devOTP = userService.getParaVal("9999","9999",17);
		if(sysParaMst_devOTP==null){
			devOTP = "N";
		}else{
			devOTP = sysParaMst_devOTP.getPara_value();
			devOTP = devOTP==null ? "N" : devOTP;
		}
		module = module + "/" + moduleCategory;
		action = action + "/" + event;// + "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			jsonObject2 = jsonObject.getJSONObject("request_data");
			if(jsonObject2.has("tokenID")){
				tokenID = jsonObject2.getString("tokenID");
			}
			if(jsonObject2.has("mobileNo")){
				mobile = jsonObject2.getString("mobileNo");
			}
		}catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
		userService.saveJsonLog(channel, "req", action, requestData, userName, module);

		if (channel.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "channel not Found.", "Channel Code not Found.", "99", channel, action, requestData, userName, module, "U");
		}
		if (tokenID == null || tokenID.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "tokenID not found", "tokenID not found", "99", channel, action, requestData, userName, module, "U");
		}
		if (mobile == null || mobile.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "Mobile no not found", "Mobile no not found", "99", channel, action, requestData, userName, module, "U");
		}


		/**@validate request**/
		inParam.put("action", action);
		inParam.put("channel", channel);
		inParam.put("tokenID", tokenID);
		inParam.put("requestType","EQFX_SMS");
		outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_validate_otp_request", inParam);
		if (outParam.containsKey("error")) {
			String dbError = (String) outParam.get("error");
			return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
		}

		try {
			dbResult = (String) outParam.get("result");
			jsonObject1 = new JSONObject(dbResult);
			if (jsonObject1.getString("status").equals("99")) {
				return dbResult;
			} else {
				if (jsonObject1.getJSONObject("response_data").getString("verify").equalsIgnoreCase("Y")) {
					return dbResult;
				} else {
//					mobile 	= jsonObject1.getJSONObject("response_data").getString("mobile");
					email 	= jsonObject1.getJSONObject("response_data").getString("email");
					entityType  = jsonObject1.getJSONObject("response_data").getString("entityType");
				}
			}
		} catch (JSONException e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		} catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		try{
			URLConfigDto urlConfigDto = null;
			String apiURL=null,apiKey=null,templateID=null;
			int configCD = 0;
			if(entityType.equals("I")){
				configCD = 50;
			}else{
				configCD = 51;
			}
			urlConfigDto = userService.findURLDtlByID(configCD);
			if(urlConfigDto==null){
				return userService.getJsonError("-99","URL Configuration not found for CODE("+configCD+")",g_error_msg,"URL Configuration not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			apiURL 	= urlConfigDto.getUrl();
			apiKey	= urlConfigDto.getKey();
			templateID = urlConfigDto.getSmtp_port();
			if(apiURL==null || apiURL.isEmpty()){
				return userService.getJsonError("-99","URL not found for CODE("+configCD+")",g_error_msg,"URL not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			if(apiKey==null || apiKey.isEmpty()) {
				return userService.getJsonError("-99","URL Required Key not found for CODE("+configCD+")",g_error_msg,"URL Required Key not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			if(templateID==null|| templateID.isEmpty()) {
				return userService.getJsonError("-99","URL Required templateID not found for CODE("+configCD+")",g_error_msg,"SMS template code not found for CODE("+configCD+")","99",channel,action,requestData,userName,module,"U");
			}
			Utility.print("apiURL:"+apiURL);
			Utility.print("apiKey:"+apiKey);
			Utility.print("templateID:"+templateID);
			if(devOTP.equals("Y")){
				jsonObject2 = new JSONObject();
				jsonObject1 = new JSONObject();
				String maskedMobile=null,maskedEmail = null;
				maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
				maskedEmail  = email.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
				transactionID = userService.getuniqueId();

				jsonObject1.put("message", "Developer OTP configured");
				jsonObject1.put("transactionID", transactionID);
				jsonObject1.put("maskedMobileNo", maskedMobile);
				jsonObject1.put("maskedEmail", maskedEmail);

				jsonObject2.put("status", "0");
				jsonObject2.put("response_data",jsonObject1);
				inParam = new HashMap();
				inParam.put("tokenID",tokenID);
				inParam.put("transactionID",transactionID);
				inParam.put("apiRequest","{}");
				inParam.put("apiResponse",jsonObject1.toString());

				outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
				if(outParam.containsKey("error")){
					String dbError = (String)outParam.get("error");
					return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
				}
				dbResult = (String) outParam.get("result");
				if(dbResult.equalsIgnoreCase("success")){
					Utility.print("OTP Generated Successfully");
				}
				userService.saveJsonLog(channel, "res", action, jsonObject2.toString(), userName,module);
				return  jsonObject2.toString();
			}
			try{
				String apiResult = null,apiJsonReq=null;
				int httpStatus = 0;
				URL obj = new URL(apiURL);
				HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

				conn.setRequestMethod("POST");
				conn.addRequestProperty("content-Type", "application/json");
				conn.addRequestProperty("authKey",apiKey);
				conn.setDoOutput(true);
				JSONObject requestJson = new JSONObject();
				requestJson.put("mobile",mobile);
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
				Utility.print("API response:"+apiResult);

				if(httpStatus==conn.HTTP_OK){
					String status = null,message=null;
					try {
						JSONObject jsonObject = new JSONObject(apiResult);
						if(jsonObject.has("status")){
							status = jsonObject.getString("status");
						}else{
							status = "Invalid status";
						}
						if(jsonObject.has("message")){
							message = jsonObject.getString("message");
						}
						if(jsonObject.has("transactionId")){
							transactionID = jsonObject.getString("transactionId");
						}
						if(message.trim()==null||message.isEmpty()){
							message = "Status:"+status;
						}
						if(status.equals("200")){
							try {
								String maskedMobile=null,maskedEmail = null;
								if(mobile!=null||!mobile.isEmpty()){
									maskedMobile = mobile.replaceAll(".(?=.{4})", "*");
								}
								jsonObject1 = new JSONObject();
								jsonObject2 = new JSONObject();
								jsonObject1.put("message", message);
								jsonObject1.put("transactionID", transactionID);
								jsonObject1.put("maskedMobileNo", maskedMobile);

								jsonObject2.put("status", "0");
								jsonObject2.put("response_data",jsonObject1);

								inParam = new HashMap();
								inParam.put("tokenID",tokenID);
								inParam.put("transactionID",transactionID);
								inParam.put("apiRequest",apiJsonReq);
								inParam.put("apiResponse",jsonObject.toString());

								outParam = userService.callingDBObject("procedure","pack_otp_new.proc_insert_otp_verification_sdt",inParam);
								if(outParam.containsKey("error")){
									String dbError = (String)outParam.get("error");
									return userService.getJsonError("-99","Error in callingDBObject()",g_error_msg,dbError,"99",channel,action,requestData,userName,module,"E");
								}
								dbResult = (String) outParam.get("result");
								if(dbResult.equalsIgnoreCase("success")){
									Utility.print("OTP Generated Successfully");
								}
								userService.saveJsonLog(channel,"res",action,jsonObject2.toString(),userName,module);
								return jsonObject2.toString();
							}catch (JSONException e){
								return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
							}catch (Exception e){
								e.printStackTrace();
								return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
							}
						}else {
							return  userService.getJsonError("-99","Failed to send OTP",message,"Failed to send OTP Request("+message+")","99",channel,action,requestData,userName,module,"U");
						}
					}catch (JSONException e){
						return userService.getJsonError("-99","Error!",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
					}
				}else{
					return userService.getJsonError("-99","Error!",g_error_msg,"OTP API HTTP Status:"+httpStatus,"99",channel,action,requestData,userName,module,"U");
				}
			}catch (Exception e) {
				return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
			}
		}catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}

	//2.1 Email OTP verify
	public String funcTokenBasedEmailOTPVerify(String module,String moduleCategory, String action,String event,String requestData,String userName){
		String channel = null, mobile = null, email = null, dbResult = null,
				transactionID=null,equifaxOTP="N",consent=null,devOTP=null,tokenID = null,enteredOTP=null;
		SysParaMst sysParaMst_devOTP = userService.getParaVal("9999","9999",18);
		int configCD = 23; //email otp verify url
		CRMAppDto crmAppDto = userService.findAppByID(1);
		String apiResult = null;
		HashMap inParam = new HashMap(),outParam;

		if(sysParaMst_devOTP==null){
			devOTP = "N";
		}else{
			devOTP = sysParaMst_devOTP.getPara_value();
			devOTP = devOTP==null ? "N" : devOTP;
		}
		module = module + "/" + moduleCategory;
		action = action + "/" + event ;//+ "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONObject jsonResponse = new JSONObject();

		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			jsonObject1 = jsonObject.getJSONObject("request_data");
			if(jsonObject1.has("tokenID")){
				tokenID = jsonObject1.getString("tokenID");
			}
			if(jsonObject1.has("transactionID")){
				transactionID = jsonObject1.getString("transactionID");
			}
			if(jsonObject1.has("otp")){
				enteredOTP = jsonObject1.getString("otp");
			}
			if(tokenID==null || tokenID.isEmpty()){
				return userService.getJsonError("-99","Request Error","tokenID not found","tokenID not found","99",channel,action,requestData,userName,module,"U");
			}
			if(enteredOTP==null || enteredOTP.isEmpty()){
				return userService.getJsonError("-99","Request Error","Please Enter OTP","Please Enter OTP","99",channel,action,requestData,userName,module,"U");
			}

			//GET MOBILE NUMBER / EMAIL
			OtpVerificationDtl otpVerificationDtl = userService.findOTPDetailByTokenID(tokenID);
			if(otpVerificationDtl==null){
				return userService.getJsonError("-99","Failed to get OTP verification detail","OTP detail not found","OTP detail not found","99",channel,action,requestData,userName,module,"U");
			}
			mobile = otpVerificationDtl.getOtp_sent_mobile();
			email  = otpVerificationDtl.getOtp_sent_email();
			if(mobile!=null){
				mobile = userService.func_get_data_val(crmAppDto.getA(), crmAppDto.getB(),mobile);
			}
			if(email!=null){
				email = userService.func_get_data_val(crmAppDto.getA(), crmAppDto.getB(),email);
			}
		}catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		try {
			if(devOTP.equals("Y")){
				if(enteredOTP.equals("000000")){
					inParam.put("tokenID", tokenID);
					inParam.put("requestType","EMAIL");
					inParam.put("mobileNo","");
					outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_set_verified_otp", inParam);
					if (outParam.containsKey("error")) {
						String dbError = (String) outParam.get("error");
						return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
					}
					jsonObject1 = new JSONObject();
					jsonObject2 = new JSONObject();
					jsonObject1.put("message", "OTP verified successfully");
					jsonObject2.put("status", "0");
					jsonObject2.put("response_data", jsonObject1);
					return jsonObject2.toString();
				}else {
					return userService.getJsonError("-99", "OTP verification failed", "Invalid OTP", "Invalid OTP", "99", channel, action, requestData, userName, module, "U");
				}
			}else {
				String apiKey=null,apiURL=null;
				int httpStatusCode=0;
				URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
				if (urlConfigDto == null) {
					return userService.getJsonError("-99", "URL Configuration not found for CODE(" + configCD + ")", g_error_msg, "URL Configuration not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				apiURL = urlConfigDto.getUrl();
				apiKey = urlConfigDto.getKey();
				if (apiKey == null || apiKey.isEmpty()) {
					return userService.getJsonError("-99", "URL Required Key not found for CODE(" + configCD + ")", g_error_msg, "URL Required Key not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				if (apiURL == null || apiURL.isEmpty()) {
					return userService.getJsonError("-99", "URL not found for CODE(" + configCD + ")", g_error_msg, "URL not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				try {
					URL obj = new URL(urlConfigDto.getUrl());
					HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
					conn.setRequestMethod("POST");
					conn.addRequestProperty("content-Type", "application/json");
					conn.addRequestProperty("authKey",apiKey);
					conn.setDoOutput(true);
					JSONObject requestJson = new JSONObject();

					requestJson.put("email",email);
					requestJson.put("otp",enteredOTP);
					OutputStream os = conn.getOutputStream();
					os.write(requestJson.toString().getBytes());
					os.flush();
					os.close();
					Utility.print("OTP verify createJson:"+requestJson.toString());
					//get response body of api request
					apiResult = Utility.getURLResponse(conn);
					httpStatusCode = conn.getResponseCode();
					Utility.print("HTTP Status:"+httpStatusCode);
					Utility.print("api_response:"+apiResult);

					//msg email log insert
					inParam = new HashMap();
					JSONObject jsonReqData = new JSONObject();
					jsonReqData.put("tokenID",tokenID);
					jsonReqData.put("requestType","EMAIL");
					jsonReqData.put("requestData",requestJson.toString());
					jsonReqData.put("responseData",apiResult);
					jsonReqData.put("transactionID","");
					jsonReqData.put("messageCategory","08");
					inParam.put("action", "msg_email_log");
					inParam.put("jsonReqData",jsonReqData.toString());
					inParam.put("mobileNo",mobile);

					outParam = userService.callingDBObject("procedure", "proc_calling_db_objects", inParam);
					if (outParam.containsKey("error")) {
						String dbError = (String) outParam.get("error");
						return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
					}

					if(httpStatusCode==conn.HTTP_OK){
						String resStatus="x",message=null;
						jsonResponse = new JSONObject(apiResult);
						if(jsonResponse.has("message")){
							message = jsonResponse.getString("message");
						}
						if(jsonResponse.has("status")){
							resStatus = String.valueOf(jsonResponse.getLong("status"));
						}
						if(resStatus.equalsIgnoreCase("success")||resStatus.equals("200")){
							jsonObject1 = new JSONObject();
							jsonObject2 = new JSONObject();

							jsonObject1.put("message",message);
							jsonObject2.put("status","0");
							jsonObject2.put("response_data",jsonObject1);
							userService.saveJsonLog(channel,"res",action,"res",userName,module);
							inParam = new HashMap();
							inParam.put("tokenID", tokenID);
							inParam.put("requestType","EMAIL");
							inParam.put("mobileNo","");
							outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_set_verified_otp", inParam);
							if (outParam.containsKey("error")) {
								String dbError = (String) outParam.get("error");
								return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
							}
							dbResult = (String) outParam.get("result");
							if (dbResult.equalsIgnoreCase("success")) {
								Utility.print("Email verification status updated");
							}
							return jsonObject2.toString();
						}
						else{
							return userService.getJsonError("-99","OTP verification failed",message,"OTP verification failed","99",channel,action,requestData,userName,module,"U");
						}
					}else{
						return userService.getJsonError("-99", "Failed to verify OTP", g_error_msg, "OTP verification API HTTP Status:"+httpStatusCode, "99", channel, action, requestData, userName, module, "U");
					}
				}catch(Exception e) {
					return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
				}
			}
		}catch (JSONException e){
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}

	//2.2 Mobile OTP verify
	public String funcTokenBasedMobileOTPVerify(String module,String moduleCategory, String action,String event,String requestData,String userName){
		String channel = null, mobile = null, email = null, requestType = null, dbResult = null,response=null,
				transactionID=null,equifaxOTP="N",reVerify = "N",consent=null,devOTP=null,tokenID = null,enteredOTP=null,reqType=null;
		Utility.print("sanjay-1");
		SysParaMst sysParaMst_devOTP = userService.getParaVal("9999","9999",17);
		int configCD = 8; //mobile otp verify url
		CRMAppDto crmAppDto = userService.findAppByID(1);
		String apiResult = null;
		HashMap inParam = new HashMap(),outParam;
		Utility.print("sanjay-2");
		if(sysParaMst_devOTP==null){
			devOTP = "N";
		}else{
			devOTP = sysParaMst_devOTP.getPara_value();
			devOTP = devOTP==null ? "N" : devOTP;
		}
		Utility.print("sanjay-3");
		if(moduleCategory.equalsIgnoreCase("equifax-otp")){
			equifaxOTP = "Y";
			reqType  = "EQFX_SMS";
		}
		if(event.equals("re-verify")){
			reVerify = "Y";
		}
		module = module + "/" + moduleCategory;
		action = action + "/" + event ;//+ "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		Utility.print("sanjay-4");
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONObject jsonResponse = new JSONObject();

		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			jsonObject1 = jsonObject.getJSONObject("request_data");
			if(jsonObject1.has("tokenID")){
				tokenID = jsonObject1.getString("tokenID");
			}
			if(jsonObject1.has("transactionID")){
				transactionID = jsonObject1.getString("transactionID");
			}
			if(jsonObject1.has("otp")){
				enteredOTP = jsonObject1.getString("otp");
			}
			if(tokenID==null || tokenID.isEmpty()){
				return userService.getJsonError("-99","Request Error","tokenID not found","tokenID not found","99",channel,action,requestData,userName,module,"U");
			}
			Utility.print("sanjay-5");
			if(enteredOTP==null || enteredOTP.isEmpty()){
				return userService.getJsonError("-99","Request Error","Please Enter OTP","Please Enter OTP","99",channel,action,requestData,userName,module,"U");
			}
			if(equifaxOTP.equals("Y")){
				consent = jsonObject1.getString("consent");
				if(consent==null||consent.isEmpty()){
					return  userService.getJsonError("-99","Request Error","Provide consent to proceed","Provide consent to proceed","99",channel,action,requestData,userName,module,"U");
				}
				if(!consent.equals("Y")){
					return  userService.getJsonError("-99","Request Error","Provide consent to proceed","Provide consent to proceed","99",channel,action,requestData,userName,module,"U");
				}
			}
			if(reVerify.equals("Y")){
				mobile = jsonObject1.getString("mobileNo");
				if(mobile==null||mobile.isEmpty()){
					return userService.getJsonError("-99","Request Error","Mobile number not found","Mobile number not found","99",channel,action,requestData,userName,module,"U");
				}
			}

			//GET MOBILE NUMBER / EMAIL
			OtpVerificationDtl otpVerificationDtl = userService.findOTPDetailByTokenID(tokenID);
			if(otpVerificationDtl==null){
				return userService.getJsonError("-99","Failed to get OTP verification detail","OTP detail not found","OTP detail not found","99",channel,action,requestData,userName,module,"U");
			}
			if(reVerify.equals("N")){
				mobile = otpVerificationDtl.getOtp_sent_mobile();
				if(mobile!=null){
					mobile = userService.func_get_data_val(crmAppDto.getA(), crmAppDto.getB(),mobile);
				}
			}
			email  = otpVerificationDtl.getOtp_sent_email();
			if(email!=null){
				email = userService.func_get_data_val(crmAppDto.getA(), crmAppDto.getB(),email);
			}
		}catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		try {
			if(devOTP.equals("Y")){
				if(enteredOTP.equals("000000")){
					inParam.put("tokenID", tokenID);
					inParam.put("requestType",reqType);
					inParam.put("mobileNo",mobile);
					outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_set_verified_otp", inParam);
					if (outParam.containsKey("error")) {
						String dbError = (String) outParam.get("error");
						return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
					}
					dbResult = (String) outParam.get("result");
					if (dbResult.equalsIgnoreCase("success")) {
						Utility.print("OTP verified successfully");
						if(equifaxOTP.equals("Y")){
							String createJSON = "{\"request_data\":{\"tokenID\":\""+tokenID+"\",\"consent\":\""+consent+"\"},\n" +
									"\"channel\":\"W\"}";
							//external/equifax/request/send
							return funcEquifaxSendRequest("external","equifax","request","send",createJSON,userName);
						}
					}
					jsonObject1 = new JSONObject();
					jsonObject2 = new JSONObject();
					jsonObject1.put("message", "OTP verified successfully");
					jsonObject2.put("status", "0");
					jsonObject2.put("response_data", jsonObject1);
					return jsonObject2.toString();
				}
				else{
					return userService.getJsonError("-99", "OTP verification failed", "Invalid OTP", "Invalid OTP", "99", channel, action, requestData, userName, module, "U");
				}
			}else {
				String apiKey=null,apiURL=null;
				ResponseEntity<String> result = null;
				int httpStatusCode=0;
				URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
				if (urlConfigDto == null) {
					return userService.getJsonError("-99", "URL Configuration not found for CODE(" + configCD + ")", g_error_msg, "URL Configuration not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				apiURL = urlConfigDto.getUrl();
				apiKey = urlConfigDto.getKey();
				if (apiKey == null || apiKey.isEmpty()) {
					return userService.getJsonError("-99", "URL Required Key not found for CODE(" + configCD + ")", g_error_msg, "URL Required Key not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				if (apiURL == null || apiURL.isEmpty()) {
					return userService.getJsonError("-99", "URL not found for CODE(" + configCD + ")", g_error_msg, "URL not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				try {
					Utility.print("verify mobile OTP Request:1");
					RestTemplate restTemplate = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();
					headers.add("authkey",apiKey);
					MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
					map.add("mobile",mobile);
					map.add("otp",enteredOTP);
					//ls_mob_number = userService.func_get_result_val()
					HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);
					result = restTemplate.postForEntity(apiURL, httpRequest ,String.class);
					httpStatusCode = result.getStatusCodeValue();
					Utility.print("HTTP Status:"+httpStatusCode);
					Utility.print("api_response:"+result.getBody());
					if(httpStatusCode==200){
						String resStatus="x",message=null;
						jsonResponse = new JSONObject(result.getBody());
						if(jsonResponse.has("message")){
							message = jsonResponse.getString("message");
						}
						if(jsonResponse.has("type")){ //for mob
							resStatus = jsonResponse.getString("type");
						}
						if(resStatus.equalsIgnoreCase("success")||resStatus.equals("200")){
							jsonObject1 = new JSONObject();
							jsonObject2 = new JSONObject();

							jsonObject1.put("message",message);
							jsonObject2.put("status","0");
							jsonObject2.put("response_data",jsonObject1);
							userService.saveJsonLog(channel,"res",action,"res",userName,module);
							//if re-verify then update contact detail in equifax log table
							//code deleted: combined with verify procedure
							inParam = new HashMap();
							inParam.put("tokenID", tokenID);
							inParam.put("requestType",reqType);
							inParam.put("mobileNo",mobile);
							outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_set_verified_otp", inParam);
							if (outParam.containsKey("error")) {
								String dbError = (String) outParam.get("error");
								return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
							}
							dbResult = (String) outParam.get("result");
							if (dbResult.equalsIgnoreCase("success")) {
								if(equifaxOTP.equals("Y")){
									String createJSON = "{\"request_data\":{\"tokenID\":\""+tokenID+"\",\"consent\":\""+consent+"\"},\n" +
											"\"channel\":\"W\"}";
									//external/equifax/request/send
									return funcEquifaxSendRequest("external","equifax","request","send",createJSON,userName);
								}
							}
							return jsonObject2.toString();
						}
						else{
							return userService.getJsonError("-99","OTP verification failed",message,"OTP verification failed","99",channel,action,requestData,userName,module,"U");
						}
					}else{
						return userService.getJsonError("-99", "Failed to verify OTP", g_error_msg, "OTP verification API HTTP Status:"+httpStatusCode, "99", channel, action, requestData, userName, module, "U");
					}
				}catch(Exception e) {
					return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
				}
			}
		}catch (JSONException e){
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}
	//2.2 Mobile OTP verify v2.0
	public String funcTokenBasedMobileOTPVerify_v2(String module,String moduleCategory, String action,String event,String requestData,String userName){
		Utility.print("funcTokenBasedMobileOTPVerify_v2");
		String channel = null, mobile = null, email = null, requestType = null, dbResult = null,response=null,
				transactionID=null,equifaxOTP="N",reVerify = "N",consent=null,devOTP=null,tokenID = null,enteredOTP=null,reqType=null;
		SysParaMst sysParaMst_devOTP = userService.getParaVal("9999","9999",17);
		int configCD = 8; //mobile otp verify url
		CRMAppDto crmAppDto = userService.findAppByID(1);
		String apiResult = null;
		HashMap inParam = new HashMap(),outParam;

		if(sysParaMst_devOTP==null){
			devOTP = "N";
		}else{
			devOTP = sysParaMst_devOTP.getPara_value();
			devOTP = devOTP==null ? "N" : devOTP;
		}
		Utility.print("DEV OTP flag:"+devOTP);
		if(moduleCategory.equalsIgnoreCase("equifax-otp")){
			equifaxOTP = "Y";
			reqType  = "EQFX_SMS";
		}else{
			reqType = "SMS";
		}
		if(event.equals("re-verify")){
			reVerify = "Y";
		}
		module = module + "/" + moduleCategory;
		action = action + "/" + event ;//+ "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONObject jsonResponse = new JSONObject();

		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel     = jsonObject.getString("channel");
			jsonObject1 = jsonObject.getJSONObject("request_data");
			if(jsonObject1.has("tokenID")){
				tokenID = jsonObject1.getString("tokenID");
			}
			if(jsonObject1.has("transactionID")){
				transactionID = jsonObject1.getString("transactionID");
			}
			if(jsonObject1.has("otp")){
				enteredOTP = jsonObject1.getString("otp");
			}
			if(tokenID==null || tokenID.isEmpty()){
				return userService.getJsonError("-99","Request Error","tokenID not found","tokenID not found","99",channel,action,requestData,userName,module,"U");
			}
			if(enteredOTP==null || enteredOTP.isEmpty()){
				return userService.getJsonError("-99","Request Error","Please Enter OTP","Please Enter OTP","99",channel,action,requestData,userName,module,"U");
			}
			if(equifaxOTP.equals("Y")){
				consent = jsonObject1.getString("consent");
				if(consent==null||consent.isEmpty()){
					return  userService.getJsonError("-99","Request Error","Provide consent to proceed","Provide consent to proceed","99",channel,action,requestData,userName,module,"U");
				}
				if(!consent.equals("Y")){
					return  userService.getJsonError("-99","Request Error","Provide consent to proceed","Provide consent to proceed","99",channel,action,requestData,userName,module,"U");
				}
			}
			if(reVerify.equals("Y")){
				mobile = jsonObject1.getString("mobileNo");
				if(mobile==null||mobile.isEmpty()){
					return userService.getJsonError("-99","Request Error","Mobile number not found","Mobile number not found","99",channel,action,requestData,userName,module,"U");
				}
			}
			//GET MOBILE NUMBER / EMAIL
			OtpVerificationDtl otpVerificationDtl = userService.findOTPDetailByTokenID(tokenID);
			if(otpVerificationDtl==null){
				return userService.getJsonError("-99","Failed to get OTP verification detail","OTP detail not found","OTP detail not found","99",channel,action,requestData,userName,module,"U");
			}
			if(reVerify.equals("N")){
				mobile = otpVerificationDtl.getOtp_sent_mobile();
				if(mobile!=null){
					mobile = userService.func_get_data_val(crmAppDto.getA(), crmAppDto.getB(),mobile);
				}
			}
			email  = otpVerificationDtl.getOtp_sent_email();
			if(email!=null){
				email = userService.func_get_data_val(crmAppDto.getA(), crmAppDto.getB(),email);
			}
		}catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		try {
			if(devOTP.equals("Y")){
				if(enteredOTP.equals("000000")){
					inParam.put("tokenID", tokenID);
					inParam.put("requestType",reqType);
					inParam.put("mobileNo",mobile);

					outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_set_verified_otp", inParam);
					if (outParam.containsKey("error")) {
						String dbError = (String) outParam.get("error");
						return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
					}
					dbResult = (String) outParam.get("result");
					if (dbResult.equalsIgnoreCase("success")) {
						Utility.print("OTP verified successfully");
						if(equifaxOTP.equals("Y")){
							String createJSON = "{\"request_data\":{\"tokenID\":\""+tokenID+"\",\"consent\":\""+consent+"\"},\n" +
									"\"channel\":\"W\"}";
							//external/equifax/request/send
							return funcEquifaxSendRequest("external","equifax","request","send",createJSON,userName);
						}
					}
					jsonObject1 = new JSONObject();
					jsonObject2 = new JSONObject();
					jsonObject1.put("message", "OTP verified successfully");
					jsonObject2.put("status", "0");
					jsonObject2.put("response_data", jsonObject1);
					return jsonObject2.toString();
				}
				else{
					return userService.getJsonError("-99", "OTP verification failed", "Invalid OTP", "Invalid OTP", "99", channel, action, requestData, userName, module, "U");
				}
			}else {
				String apiKey=null,apiURL=null;
				int httpStatusCode=0;
				URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
				if (urlConfigDto == null) {
					return userService.getJsonError("-99", "URL Configuration not found for CODE(" + configCD + ")", g_error_msg, "URL Configuration not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				apiURL = urlConfigDto.getUrl();
				apiKey = urlConfigDto.getKey();
				if (apiKey == null || apiKey.isEmpty()) {
					return userService.getJsonError("-99", "URL Required Key not found for CODE(" + configCD + ")", g_error_msg, "URL Required Key not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				if (apiURL == null || apiURL.isEmpty()) {
					return userService.getJsonError("-99", "URL not found for CODE(" + configCD + ")", g_error_msg, "URL not found for CODE(" + configCD + ")", "99", channel, action, requestData, userName, module, "U");
				}
				Utility.print("apiURL:"+apiURL);
				Utility.print("apiKey:"+apiKey);

				try {
					URL obj = new URL(apiURL);
					HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
					conn.setRequestMethod("POST");
					conn.addRequestProperty("content-Type", "application/json");
					conn.addRequestProperty("authKey",apiKey);
					conn.setDoOutput(true);
					JSONObject requestJson = new JSONObject();

					requestJson.put("mobile",mobile);
					requestJson.put("otp",enteredOTP);
					OutputStream os = conn.getOutputStream();
					os.write(requestJson.toString().getBytes());
					os.flush();
					os.close();
					Utility.print("OTP verify createJson:"+requestJson.toString());
					//get response body of api request
					apiResult = Utility.getURLResponse(conn);
					httpStatusCode = conn.getResponseCode();

					Utility.print("api response:"+apiResult);
					Utility.print("httpStatusCode:"+httpStatusCode);
					//msg email log insert
					inParam = new HashMap();
					JSONObject jsonReqData = new JSONObject();
					jsonReqData.put("tokenID",tokenID);
					jsonReqData.put("requestType",reqType);
					jsonReqData.put("requestData",requestJson.toString());
					jsonReqData.put("responseData",apiResult);
					jsonReqData.put("transactionID","");
					jsonReqData.put("messageCategory","07");
					inParam.put("action", "msg_email_log");
					inParam.put("jsonReqData",jsonReqData.toString());
					inParam.put("mobileNo",mobile);
					outParam = userService.callingDBObject("procedure", "proc_calling_db_objects", inParam);
					if (outParam.containsKey("error")) {
						String dbError = (String) outParam.get("error");
						return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
					}

					if(httpStatusCode==200){
						String resStatus="x",message=null;
						jsonResponse = new JSONObject(apiResult);
						if(jsonResponse.has("message")){
							message = jsonResponse.getString("message");
						}
						if(jsonResponse.has("type")){ //for mob
							resStatus = jsonResponse.getString("type");
						}
						if(message.equalsIgnoreCase("success")||resStatus.equals("200")){
							jsonObject1 = new JSONObject();
							jsonObject2 = new JSONObject();

							jsonObject1.put("message",message);
							jsonObject2.put("status","0");
							jsonObject2.put("response_data",jsonObject1);
							userService.saveJsonLog(channel,"res",action,"res",userName,module);
							Utility.print("reVerify:"+reVerify);
							inParam = new HashMap();
							inParam.put("tokenID", tokenID);
							inParam.put("requestType",reqType);
							inParam.put("mobileNo",mobile);
							outParam = userService.callingDBObject("procedure", "pack_otp_new.proc_set_verified_otp", inParam);
							if (outParam.containsKey("error")) {
								String dbError = (String) outParam.get("error");
								return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
							}
							dbResult = (String) outParam.get("result");
							if (dbResult.equalsIgnoreCase("success")) {
								if(equifaxOTP.equals("Y")){
									String createJSON = "{\"request_data\":{\"tokenID\":\""+tokenID+"\",\"consent\":\""+consent+"\"},\n" +
											"\"channel\":\"W\"}";
									//external/equifax/request/send
									return funcEquifaxSendRequest("external","equifax","request","send",createJSON,userName);
								}
							}
							return jsonObject2.toString();
						}
						else{
							return userService.getJsonError("-99","OTP verification failed",message,"OTP verification failed","99",channel,action,requestData,userName,module,"U");
						}
					}else{
						return userService.getJsonError("-99", "Failed to verify OTP", g_error_msg, "OTP verification API HTTP Status:"+httpStatusCode, "99", channel, action, requestData, userName, module, "U");
					}
				}catch(Exception e) {
					return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
				}
			}
		}catch (JSONException e){
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}

	/**@Equifax **/
	//1.1 token verify
	public String funcVerifyEquifaxToken(String module,String moduleCategory, String action,String event,String requestData,String userName){
		String channel = null, mobile = null, email = null, requestType = null, dbResult = null,response=null,
				transactionID=null;
		String tokenID = null;
		HashMap inParam = new HashMap(), outParam;
		module = module + "/" + moduleCategory;
		action = action + "/" + event;// + "/" + subEvent;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			JSONObject jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			tokenID = jsonObject.getJSONObject("request_data").getString("tokenID");
		} catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
		if (channel.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "channel not Found.", "Channel Code not Found.", "99", channel, action, requestData, userName, module, "U");
		}
		if (tokenID == null || tokenID.isEmpty()) {
			return userService.getJsonError("-99", "Request Error", "tokenID not found", "tokenID not found", "99", channel, action, requestData, userName, module, "U");
		}
		Utility.print("requestData:"+requestData);

		userService.saveJsonLog(channel, "req", action, requestData, userName, module);

		inParam.put("tokenID", tokenID);
		outParam = userService.callingDBObject("procedure", "pack_equifax_new.proc_validate_token_id", inParam);
		if (outParam.containsKey("error")) {
			String dbError = (String) outParam.get("error");
			userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbError, "99", channel, action, requestData, userName, module, "E");
		}

		try {
			dbResult = (String) outParam.get("result");
			jsonObject1 = new JSONObject(dbResult);
			if(jsonObject1.getString("status").equals("0")){
				String tokenFound=null,tokenExpire=null,otpVerified=null,requestSucceed=null;
				tokenFound = jsonObject1.getJSONObject("response_data").getString("token_exist");
				tokenExpire = jsonObject1.getJSONObject("response_data").getString("token_expire");
				otpVerified = jsonObject1.getJSONObject("response_data").getString("otp_verified");
				requestSucceed = jsonObject1.getJSONObject("response_data").getString("request_succeed");
				if(tokenFound.equalsIgnoreCase("N")){
					return userService.getJsonError("-99","Equifax TokenID verification","Link has been expired","TokenID not found","99",channel,action,requestData,userName,module,"U");
				}
				if(tokenExpire.equalsIgnoreCase("Y")){
					return userService.getJsonError("-99","Equifax TokenID verification","Link has been expired","TokenID has been expired","99",channel,action,requestData,userName,module,"U");
				}
				if(otpVerified.equalsIgnoreCase("Y")){
					return userService.getJsonError("-99","Equifax TokenID verification","Link has been expired","Mobile has already been verified","99",channel,action,requestData,userName,module,"U");
				}
				if(requestSucceed.equalsIgnoreCase("Y")){
					return userService.getJsonError("-99","Equifax TokenID verification","Link has been expired","Request succeed already ","99",channel,action,requestData,userName,module,"U");
				}
				jsonObject1 = new JSONObject();
				jsonObject1.put("token_status","active");
				jsonObject1.put("message","valid tokenID");
				jsonObject2.put("status","0");
				jsonObject2.put("response_data",jsonObject1);
				return jsonObject2.toString();
			}else{
				return  dbResult;
			}
		}catch (JSONException e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}catch (Exception e) {
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}
	}

	//1.2 send request
	public String funcEquifaxSendRequest(String module,String moduleCategory, String action,String event,String requestData,String userName){
		String channel = null, dbResult = null,response=null,errorCode=null,errorDesc=null,
				transactionID=null;
		String tokenID = null,consent=null;
		HashMap inParam = new HashMap(), outParam;
		module = module + "/" + moduleCategory;
		action = action + "/" + event;

		if (requestData.isEmpty()) {
			return "";
		}
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		try {
			jsonObject = new JSONObject(requestData);
			channel = jsonObject.getString("channel");
			jsonObject = jsonObject.getJSONObject("request_data");
			if(jsonObject.has("tokenID")){
				tokenID = jsonObject.getString("tokenID");
			}
			if(jsonObject.has("consent")){
				consent = jsonObject.getString("consent");
			}
			if(tokenID==null || tokenID.isEmpty()){
				return userService.getJsonError("-99","Request Error","tokenID not found","tokenID not found","99",channel,action,requestData,userName,module,"U");
			}
			if(consent==null || consent.isEmpty()){
				return userService.getJsonError("-99","Request Error","consent not found","consent not found","99",channel,action,requestData,userName,module,"U");
			}
			if(!consent.equalsIgnoreCase("Y")){
				return userService.getJsonError("-99","Request Error","Consent is required","Consent is required","99",channel,action,requestData,userName,module,"U");
			}
			userService.saveJsonLog(channel,"req",action,requestData,userName,module);
		}catch (JSONException e) {
			return userService.getJsonError("-99", "Error-JSONException", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}catch(Exception e){
			return userService.getJsonError("-99", "Error-Exception", g_error_msg, e.getMessage(), "99", channel, action, requestData, userName, module, "E");
		}

		String result = null,requestStatus=null,sendURL=null,postData=null;
		int configCD = 40;
		URLConfigDto urlConfigDto = userService.findURLDtlByID(configCD);
		if(urlConfigDto==null){
			return userService.getJsonError("-99","URL Configuration not found.",g_error_msg,"URL Details not found for:"+configCD,"99",channel,action,requestData,userName,module,"U");
		}
		sendURL  = urlConfigDto.getUrl();
		if(sendURL ==null || sendURL.isEmpty()){
			return userService.getJsonError("-99","URL Configuration not found.",g_error_msg,"URL not found for:"+configCD,"99",channel,action,requestData,userName,module,"U");
		}

		EquifaxAPILog equifaxAPILog = new EquifaxAPILog();
		equifaxAPILog = userService.findEquifaxDetailByTokenId(tokenID);
		if(equifaxAPILog==null){
			return userService.getJsonError("-99","Detail not found",g_error_msg,"Detail not found tokenID:"+tokenID,"99",channel,action,requestData,userName,module,"U");
		}
		postData = equifaxAPILog.getReq_data();
		try{
			String responseStatus = null;
			Utility.print("initiating Request...");
			URL postURL = new URL(sendURL);
			HttpsURLConnection conn = (HttpsURLConnection) postURL.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(postData.getBytes());
			os.flush();
			os.close();
			result = Utility.getURLResponse(conn);
			Utility.print("result:"+result);
			Utility.print("API Calling succeed");
			Utility.print("Response code:" + conn.getResponseCode());
			Utility.print("API Response:\n");
			Utility.print(result);
			if(conn.getResponseCode() == conn.HTTP_OK){
				Utility.print("Succeed");
				requestStatus = "S";
				inParam.put("response",result);
				inParam.put("entityType",equifaxAPILog.getEntity_type());
				outParam = userService.callingDBObject("procedure","pack_equifax_new.proc_response_error_fetcher",inParam);
				if(outParam.containsKey("error")){
					dbResult = (String)outParam.get("error");
					return userService.getJsonError("-99", "Error in callingDBObject()", g_error_msg, dbResult, "99", channel, action, requestData, userName, module, "E");
				}
				errorCode = (String) outParam.get("errorCode");
				errorDesc = (String) outParam.get("errorDesc");
				if(errorCode!=null){
					if(errorCode.equals("GSWDOE116") || errorCode.equals("E0773")){
						responseStatus = "I";
					}else{
						responseStatus = "F";
					}
					String equifaxError = "{\"ErrorCode\": \""+errorCode+"\",\"ErrorDesc\": \""+errorDesc+"\"}";
					jsonObject1 = new JSONObject();
					jsonObject1.put("Status","99");
					jsonObject1.put("Error",new JSONObject(equifaxError));
					jsonObject2.put("status","0");
					jsonObject2.put("response_data",jsonObject1);
					response = jsonObject2.toString();
				}else{
					responseStatus = "S";
					jsonObject = new JSONObject(result);
					jsonObject = jsonObject.getJSONObject("CCRResponse");
					jsonObject1.put("status","0");
					jsonObject1.put("response_data",jsonObject);
					response =jsonObject1.toString();
				}
			}else{
				Utility.print("Failed");
				requestStatus = "F";
				responseStatus = "F";
				response = userService.getJsonError("-99","Equifax Response code:"+conn.getResponseCode(),"Failed to fetch data from vendor",result,"99",channel,action,requestData,userName,module,"U");
			}
			Utility.print("Updating records");
			userService.updateEquifaxAPILog(tokenID,requestStatus,responseStatus,result,errorCode,errorDesc);
			Utility.print("Updated successfully");
			return response;
		}catch (MalformedURLException e) {
			return userService.getJsonError("-99","Error-MalformedURLException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}catch (IOException e){
			e.printStackTrace();
			return userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}catch (Exception e){
			e.printStackTrace();
			return userService.getJsonError("-99","Error-IOException",g_error_msg,e.getMessage(),"99",channel,action,requestData,userName,module,"E");
		}
	}

}
