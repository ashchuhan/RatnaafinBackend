package com.ratnaafin.crm;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class Data1 {
    public String func_get_result_val(String a, String b, String c) {

        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        byte[] result = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(a.toCharArray(), b.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey,ivspec);
            result = aesCipher.doFinal(c.getBytes());
        }catch (NoSuchAlgorithmException e){
            System.out.println("result : NoSuchAlgorithmException");
            e.printStackTrace();
        }catch (InvalidKeySpecException e){
            System.out.println("result : InvalidKeySpecException");
            e.printStackTrace();
        }catch (NoSuchPaddingException e){
            System.out.println("result : NoSuchPaddingException");
            e.printStackTrace();
        }catch (InvalidAlgorithmParameterException e){
            System.out.println("result : InvalidAlgorithmParameterException");
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
            System.out.println("result : Exception "+e.getMessage());
            e.printStackTrace();
        }
        finally {
            System.out.println("result : Finally");
        }
        return bytesToHex(result);
    }
    public String func_get_data_val(String a, String b, String c) {
        if ((a.trim().isEmpty())  || (b.trim().isEmpty()) || (c.trim().isEmpty())){
            return null;
        }
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        byte[] data = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(a.toCharArray(), b.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            byte[] byteCipherText = hexStringToByteArray(c);
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            data = aesCipher.doFinal(byteCipherText);
        }
        catch (NoSuchAlgorithmException e){
            System.out.println("result : NoSuchAlgorithmException");
            e.printStackTrace();
        }catch (InvalidKeySpecException e){
            System.out.println("result : InvalidKeySpecException");
            e.printStackTrace();
        }catch (NoSuchPaddingException e){
            System.out.println("result : NoSuchPaddingException");
            e.printStackTrace();
        }catch (InvalidAlgorithmParameterException e){
            System.out.println("result : InvalidAlgorithmParameterException");
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
            System.out.println("result : Exception "+e.getMessage());
            e.printStackTrace();
        }
        finally {
            System.out.println("result : Finally");
        }
        return new String(data);
    }
    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
