package com.ratnaafin.crm.common.rabbitmq.cam;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;

@RabbitListener(queues = "CAMRequestQueue")
public class CAMConsumer {
    @Autowired
    private UserService userService;

    public static final String CAM_FILE_PATH = Utility.LOCAL_PATH+Utility.SEPERATOR+"CAM"+Utility.SEPERATOR;

    public final Browser browser;
    public CAMConsumer(){
        Playwright playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @RabbitHandler
    public void camRequestQueueMessage(String message) throws Exception {
        try{
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.getString("status").equals("0")){
                long leadID,serialNo,amountIn;
                String enteredBy = null;
                leadID = jsonObject.getJSONObject("response_data").getLong("refID");
                serialNo = jsonObject.getJSONObject("response_data").getLong("serialNo");
                enteredBy = jsonObject.getJSONObject("response_data").getString("enteredBy");
                amountIn = jsonObject.getJSONObject("response_data").getLong("amountIn");
                if (leadID > 0 && serialNo > 0){
                    CAMGenerate(leadID,serialNo,enteredBy,amountIn);
                }
            }
        }catch (JSONException e){
            throw new JSONException("CAMJsonException "+e.getMessage());
        }catch (Exception e){
            throw new Exception("CAM Exception:"+e.getMessage());
        }
    }

    public void CAMGenerate(long leadID,long serialNo,String enteredBy,long amountIn) throws Exception{
        try {
            Page page = browser.newPage();
            try {
                page.navigate("https://ratnaafin.aiplservices.com/middleware/lead/"+leadID+"?amountIn="+amountIn);
                page.waitForTimeout(29000);
                Page.PdfOptions options = new Page.PdfOptions();
                options.format="A4";
                options.displayHeaderFooter = true;
                options.printBackground = true;
                String fileName = CAM_FILE_PATH+userService.getuniqueId()+"_Lead_"+leadID+"_CAM.pdf";
                File filepath = new File(fileName);
                FileInputStream fileInputStream = null;
                options.path = Paths.get(filepath.getAbsolutePath());
                page.pdf(options);
                fileInputStream = new FileInputStream(filepath);
                Blob blob = null;
                Utility utility = new Utility();
                blob = utility.getBlobData(fileInputStream);
                if (blob != null) {
                    userService.updateCAMStatus(serialNo,leadID,blob,new Date(),"S",enteredBy,"Success");
                }else {
                    userService.updateCAMStatus(serialNo,leadID,blob,new Date(),"F",enteredBy,"Failed");
                }
                if(filepath.delete()) {
                    System.out.println("File Deleted");
                }else{
                    filepath.deleteOnExit();
                    System.out.println("File not Deleted");
                }
                page.close();
            }catch (Exception e){
                page.close();
                throw new Exception("CAM Generate Error1"+e.getMessage());
            }
        }catch (Exception e){
            throw new Exception("CAM Generate Error2"+e.getMessage());
        }
    }
}
