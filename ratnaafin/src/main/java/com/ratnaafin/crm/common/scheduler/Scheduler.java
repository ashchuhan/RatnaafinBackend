package com.ratnaafin.crm.common.scheduler;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.service.UserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class Scheduler {
    public static final String CAM_FILE_PATH = Utility.LOCAL_PATH+Utility.SEPERATOR+"CAM"+Utility.SEPERATOR;

    public static String CAM_DATA = "N";

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
                            page.navigate("https://ratnaafin.aiplservices.com/middleware/"+leadId);
                            page.waitForTimeout(29000);
                            Page.PdfOptions options = new Page.PdfOptions();
                            options.format="A4";
                            options.displayHeaderFooter = true;
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
        return ls_return;
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

}
