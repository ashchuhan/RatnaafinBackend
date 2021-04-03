package com.middleware.app.service;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface UserService {
    String func_get_result_val(String a,String b,String c);
    String func_get_data_val(String a,String b,String c);
    String genPassword(String password);
    String verifyPassword(String password,String encPassword);
    String getURLResponse(HttpURLConnection conn) throws IOException;
}
