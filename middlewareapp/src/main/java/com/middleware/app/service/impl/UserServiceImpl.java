package com.middleware.app.service.impl;

import com.middleware.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public String func_get_result_val(String a, String b, String c) {
        byte[] encValue = null;
        try {
            Key key  = new SecretKeySpec(a.getBytes("UTF8"), "AES");
            Cipher ci = Cipher.getInstance("AES");
            ci.init(Cipher.ENCRYPT_MODE, key);
            encValue = ci.doFinal(c.getBytes());
        }catch (UnsupportedEncodingException e){
            System.out.println("result : UnsupportedEncoding");
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e) {
            System.out.println("result : NoSuchAlgorithmException");
            e.printStackTrace();
        }catch (NoSuchPaddingException e){
            System.out.println("result : NoSuchPaddingException");
            e.printStackTrace();
        }catch (InvalidKeyException e){
            System.out.println("result : InvalidKeyException");
            e.printStackTrace();
        }catch (IllegalBlockSizeException e){
            System.out.println("result : IllegalBlockSizeException");
            e.printStackTrace();
        }catch (BadPaddingException e){
            System.out.println("result : BadPaddingException");
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("result : Exception");
            e.printStackTrace();
        }
        String encryptedValue = java.util.Base64.getEncoder().encodeToString(encValue);
        return encryptedValue;
    }

    @Override
    public String func_get_data_val(String a, String b, String c) {
        byte[] decValue = null;
        try {
            Key key = new SecretKeySpec(a.getBytes("UTF8"), "AES");
            Cipher ci = Cipher.getInstance("AES");
            ci.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = java.util.Base64.getDecoder().decode(c);
            decValue = ci.doFinal(decordedValue);
        }catch (UnsupportedEncodingException e){
            System.out.println("result : UnsupportedEncoding");
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e) {
            System.out.println("result : NoSuchAlgorithmException");
            e.printStackTrace();
        }catch (NoSuchPaddingException e){
            System.out.println("result : NoSuchPaddingException");
            e.printStackTrace();
        }catch (InvalidKeyException e){
            System.out.println("result : InvalidKeyException");
            e.printStackTrace();
        }catch (IllegalBlockSizeException e){
            System.out.println("result : IllegalBlockSizeException");
            e.printStackTrace();
        }catch (BadPaddingException e){
            System.out.println("result : BadPaddingException");
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("result : Exception");
            e.printStackTrace();
        }
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    @Override
    public String genPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public String verifyPassword(String password, String encPassword) {
        boolean bool = false;
        bool = passwordEncoder.matches(password,encPassword);
        if (bool == true){
            return "1";
        }
        return "0";
    }

    @Override
    public  String getURLResponse(HttpURLConnection conn) throws IOException {
        BufferedReader br;
        String output, result;
        StringBuilder sb= new StringBuilder();
        if (100 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        while((output=br.readLine())!=null){
            sb.append(output);
        }
        result = sb.toString(); sb=null;
        return  result;
    }
}
