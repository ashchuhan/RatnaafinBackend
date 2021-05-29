package com.middleware.app;

import com.middleware.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@RestController
@Controller
public class MiddlewareAppApplication {
	private  String MID_KEY = "r6ENdp/FRIDHaJ1rE7doilf/SJ1sCQE0VcVa+nuN+QI="; //"Ratnaafin@Middleware";
	//private  String liveURL = "https://ratnaafinapi.aiplservices.com/middleware";
	private  String liveURL = "https://digix.aiplsolution.in/ratnaafin/middleware";
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(MiddlewareAppApplication.class, args);
	}

	@RequestMapping(method = RequestMethod.GET,value = "/result",produces = {"text/plain","text/plain"})
	public String result_val(@RequestHeader(name = "key") String a, @RequestHeader(name = "salt",required = false) String b,@RequestHeader(name = "data") String data){
		if((!data.isEmpty()) && (!a.isEmpty())){
			return userService.func_get_result_val(a,b,data);
		}
		return "";
	}

	@RequestMapping(method = RequestMethod.GET,value = "/data",produces = {"text/plain","text/plain"})
	public String data_val(@RequestHeader(name = "key") String a, @RequestHeader(name = "salt",required = false) String b,@RequestHeader(name = "data") String data){
		if((!data.isEmpty()) && (!a.isEmpty())){
			return userService.func_get_data_val(a,b,data);
		}
		return "";
	}

	@RequestMapping(method = RequestMethod.GET,value = "/genpassword",produces = {"text/plain","text/plain"})
	public String gen_pwd(@RequestHeader(name = "password") String password){
		if (!password.isEmpty()){
			return userService.genPassword(password);
		}
		return "";
	}

	@RequestMapping(method = RequestMethod.GET,value = "/verifypassword",produces = {"text/plain","text/plain"})
	public String verifyPassword(@RequestHeader(name = "password") String password,@RequestHeader(name = "encryptPassword") String encryptPassword){
		if ((!password.isEmpty()) && (!encryptPassword.isEmpty())){
			return userService.verifyPassword(password,encryptPassword);
		}
		return "0";
	}

	@RequestMapping(method = RequestMethod.GET,value = "/middlewareApp/CAMGenerate")
	public String nextProcess(@RequestHeader(name="signature") String signature,
							  @RequestHeader(name="refID") String refID,
							  @RequestHeader(name="serialNo") String serialNo){
		String url=null,createJson=null,result=null;
		url = liveURL+"/lead/cam/generate";
		createJson =  "{\"request_data\": {\"refID\":"+refID+",\"serialNo\": "+serialNo+"},\"channel\": \"W\"}";
		try{
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("signature",signature);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(createJson.getBytes());
			os.flush();
			os.close();
			result = userService.getURLResponse(conn);
		}catch (MalformedURLException e){
			result = e.getMessage();
			e.printStackTrace();
		}catch (IOException e){
			result = e.getMessage();
			e.printStackTrace();
		}
		return  result;
	}

	@RequestMapping(method = RequestMethod.GET,value = "/middlewareApp/Corpository/{action}")
	public String corpositoryApi(@PathVariable(name = "action") String action,
									   @RequestHeader(name="signature") String signature,
									   @RequestHeader(name="requestData") String requestData, HttpServletResponse httpServletResponse) {

		String createJson = null;
		String result = null;
		String url 		= null;
		System.out.println("MIDDLEWARE INITIATED...");

		switch (action){
			case "financial_detail":
				url = liveURL+"/lead/corpository/financial";
				break;
			default:
				System.out.println("Request Not Matched:/middlewareApp/Corpository/"+action);
				result = "Not Found:/middlewareApp/Corpository/"+action;
				httpServletResponse.setStatus(404);
				return  result;
		}
		try{
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("signature",signature);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(requestData.getBytes());
			os.flush();
			os.close();
			result = userService.getURLResponse(conn);
		}catch (MalformedURLException e){
			result = e.getMessage();
			e.printStackTrace();
		}catch (IOException e){
			result = e.getMessage();
			e.printStackTrace();
		}
		return  result;
	}

	@RequestMapping(method = RequestMethod.GET,value = "/{module}/{action}")
	public String perfiosUploadProcess(@PathVariable(name = "module") String module,
									   @PathVariable(name = "action") String action,
									   @RequestHeader(name="signature") String signature,
									   @RequestHeader(name="transactionId") String transactionId, HttpServletResponse httpServletResponse){
		String createJson = null;
		String result = null;
		String url 		= null;
		System.out.println("MIDDLEWARE INITIATED...");
		createJson = "{\"request_data\":{\"perfiosTransactionId\":"+transactionId+"},\"channel\":\"W\"}";
		switch (module+"/"+action){
			case "middlewareApp/gst_upload":
				url = liveURL+"/lead/gstupload/startupload";
				break;
			case "middlewareApp/itr_upload":
				url = liveURL+"/lead/itrupload/startupload";
				break;
			case "middlewareApp/stmt_upload":
				url = liveURL+"/lead/statementupload/startupload";
				break;
			default:
				System.out.println("Request Not Matched:/"+module+"/"+action);
				result = "Not Found:/"+module+"/"+action;
				httpServletResponse.setStatus(404);
				return  result;
		}
		try{
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("content-Type", "application/json");
			conn.addRequestProperty("signature",signature);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.write(createJson.getBytes());
			os.flush();
			os.close();
			result = userService.getURLResponse(conn);
		}catch (MalformedURLException e){
			result = e.getMessage();
			e.printStackTrace();
		}catch (IOException e){
			result = e.getMessage();
			e.printStackTrace();
		}
		return  result;
	}
}
