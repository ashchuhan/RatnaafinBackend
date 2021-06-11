package com.ratnaafin.crm.common.service;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.net.*;
import java.security.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//new class added
public class Utility extends  Bucket {
    private static final String ENCRYPTION_ALGO = getEncAlgorithm();
    public static final String DIGEST_ALGO = getDigestAlgorithm();
    public static final String LOCAL_PATH = getCurrentDir();
    public static final String SEPERATOR = getSeperator();
    private static final String BANK_PRIVATE_KEY = getBankUploadPrivatekey();
    private static final String GST_ITR_PRIVATE_KEY = getGstItrUploadPrivateKey();

    private  File newfileDir;
    //for multipart form data//
    private static final String boundary = "**";
    private static final String LINE_FEED = "\r\n";
    private HttpsURLConnection httpConn;
    private String charset;
    private DataOutputStream outputStream;
    private PrintWriter writer;
    private static final int maxBufferSize = 1 * 1024 * 1024;
    private String twoHyphens = "--";
    private String lineEnd = "\r\n";
    private String outputString = "";
    int bytesRead;
    int bytesAvailable;
    int bufferSize;

    //to get Zip inputstream from remote url
    /**dependent to saveZipFile**/
    public ZipArchiveInputStream getZipInputStream() {
        try {
            InputStream inputStream = new FileInputStream(newfileDir);
            ZipArchiveInputStream zin = new ZipArchiveInputStream(inputStream);

            return zin;
        } catch (IOException e) {
            System.out.println("Error: IOException");
            e.printStackTrace();
        }
        return null;
    }

    //to get Clob data inside from zip file
    /**dependent to saveZipFile**/
    public String getZipClobData(String fileExt) {
        String lsdata = null;
        try {
            //ZipFile from apache lib and Zip entry
            ZipFile zipfile = new ZipFile(newfileDir);

            ZipArchiveEntry entry;
            Enumeration<? extends ZipArchiveEntry> entries = zipfile.getEntries();
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                System.out.println(entry.getName());
                if (entry.getName().endsWith(fileExt)) {
                    lsdata = getClobData(zipfile.getInputStream(entry));
                }
            }
            zipfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lsdata;
    }

    /**dependent to saveZipFile**/
    public String getClobData(InputStream stream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String clobdata = "";
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                clobdata += line + "\n";
                line = bufferedReader.readLine();
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clobdata;
    }

    //blob data read :18-01-2021
    /**dependent to saveZipFile**/
    public Blob getZipBlobData(String fileExt) {
        Blob fileBlob = null;
        try {
            //ZipFile from apache lib and Zip entry
            ZipFile zipfile = new ZipFile(newfileDir);
            ZipArchiveEntry entry;
            Enumeration<? extends ZipArchiveEntry> entries = zipfile.getEntries();
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                System.out.println(entry.getName());
                if (entry.getName().endsWith(fileExt)) {
                    fileBlob = getBlobData(zipfile.getInputStream(entry));
                }
            }
            zipfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBlob;
    }

    /**dependent to saveZipFile**/
    public Blob getBlobData(InputStream stream) {
        byte[] filecontents;
        Blob fileblob = null;
        ByteArrayOutputStream fileoutput = new ByteArrayOutputStream();
        byte[] filebuffer = new byte[1024];
        int filecount;
        try {
            //file data download
            while ((filecount = stream.read(filebuffer)) != -1) {
                fileoutput.write(filebuffer, 0, filecount);
            }
            filecontents = fileoutput.toByteArray();
            fileblob = new SerialBlob(filecontents);
            stream.close();
            //file data download done;
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
        return fileblob;
    }
    //end blob read

    /*18/01/2021*/
    public boolean saveZipFile(URL url, String filename) {
        String tempFilename = filename;
        if (tempFilename == null) {
            System.out.println("File Found Null");
            return false;
        }
        try {
            if (!checkZipURL(url)) {
                return false;
            }
            //1. create URL connection associate with url
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //2. set method for url request
            connection.setRequestMethod("GET");

            //3. fetch inputstream from url connection
            InputStream inputStream = connection.getInputStream();

            //4. String Path
            File filedir = createDirectory("TempUserdata");

            //5. Set file name with path
            newfileDir = new File(filedir.getAbsolutePath() + SEPERATOR + tempFilename);

            //6. get file outputstream from directory to be write on there
            FileOutputStream fos = new FileOutputStream(newfileDir);

            //7. call function that copy actual stream data at specified path
            copy(inputStream, fos, 1024);
            //zin = getZipInputStream();
            inputStream.close();
            fos.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error: IOException");
        }
        return false;
    }

    /**check whether URL return ZIP and having inside files**/
    public boolean checkZipURL(URL zipurl) {
        Boolean returnValue;
        try {
            InputStream in = zipurl.openStream();
            ZipArchiveInputStream zin = new ZipArchiveInputStream(in);
            ZipEntry entry;
            entry = zin.getNextZipEntry();
            if (entry == null) {
                System.out.println("NO DATA FOUND IN PROVIDED ZIP URL...!");
                in.close();
                zin.close();
                return false;
            }
            while (entry != null) {
                System.out.println(entry.getName());
                entry = zin.getNextZipEntry();
            }
            in.close();
            zin.close();
            returnValue = true;
        } catch (IOException e) {
            System.out.println("Error: IOException");
            e.printStackTrace();
            returnValue = false;
        }
        return returnValue;
    }

    /**just make copy from source inputStream to destination outputStream **/
    public static void copy(InputStream input, OutputStream output, int bufferSize) {
        try {
            byte[] buf = new byte[bufferSize];
            int n = input.read(buf);
            while (n >= 0) {
                output.write(buf, 0, n);
                n = input.read(buf);
            }
            output.flush();
            input.close();
            output.close();
        } catch (IOException e) {
            System.out.println("Error in copy function: IOException");
            e.printStackTrace();
        }
    }

    /**dependent to saveZipFile**/
    public void deleteFile() {
        print("Directory file going to be delete:" + newfileDir.toString());
        System.gc();
        File dirfile = new File(newfileDir.getParent());
        if (newfileDir.delete()) {
            print("file has been removed");
            System.gc();
            if(dirfile.delete()){
                print("Directory "+dirfile.getName()+" removed");
            }else{
                print(dirfile.getName()+" directory has been scheduled for delete.");
            }
        }else{
            print("file has been scheduled for delete.");
            newfileDir.deleteOnExit();
        }
    }

    /**Perfios BANK PASSWORD Data Encrypt**/
    public static String perfiosBankPwdEncrypt(String raw) {
        Key k = buildPublicKey(BANK_PRIVATE_KEY);
        String strEncrypted = "";
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = cipher.doFinal(raw.getBytes("UTF-8"));
            byte[] encoded = Hex.encode(encrypted);
            strEncrypted = new String(encoded);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strEncrypted;
    }
    /**data encrypt by using buildPublicKey method**/
    public static String perfiosDataEncryptPub(String raw) {
        Key k = buildPublicKey(GST_ITR_PRIVATE_KEY);
        String strEncrypted = "";
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = cipher.doFinal(raw.getBytes("UTF-8"));
            byte[] encoded = Hex.encode(encrypted);
            strEncrypted = new String(encoded);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strEncrypted;
    }

    /*Perfios Data Encrypt*/
    /**data encrypt by using buildPrivateKey method**/
    public static String perfiosDataEncryptPvt(String raw) {
        Key k = buildPrivateKey(BANK_PRIVATE_KEY);
        String strEncrypted = "";
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = cipher.doFinal(raw.getBytes("UTF-8"));
            byte[] encoded = Hex.encode(encrypted);
            strEncrypted = new String(encoded);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strEncrypted;
    }

    private static PublicKey buildPublicKey(String privateKeySerialized) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        StringReader reader = new StringReader(privateKeySerialized);
        PublicKey pKey = null;
        try {
            PEMReader pemReader = new PEMReader(reader);
            KeyPair keyPair = (KeyPair) pemReader.readObject();
            pKey = keyPair.getPublic();
            pemReader.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        return pKey;
    }

    private static PrivateKey buildPrivateKey(String privateKeySerialized)
    {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        StringReader reader = new StringReader(privateKeySerialized);
        PrivateKey pKey = null;
        try
        {
            PEMReader pemReader = new PEMReader(reader);
            KeyPair keyPair = (KeyPair) pemReader.readObject();
            pKey = keyPair.getPrivate();
            pemReader.close();
        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
        return pKey;
    }

    public static void print(String str) {
        System.out.println(str);
    }

    public static String getURLResponse(HttpsURLConnection conn) throws IOException {
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

    //digest msg
    public static String makeDigest(String payload)
    {
        String strDigest = "";
        try
        {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGO);
            md.update(payload.getBytes("UTF-8"));
            byte[] digest = md.digest();
            byte[] encoded = Hex.encode(digest);
            strDigest = new String(encoded);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return strDigest;
    }

    //function for create tempfile
    public File blobToFileConverter(Blob blobdata,String prefix, String suffix) throws SQLException, FileNotFoundException {
        InputStream in ;
        OutputStream out;
        try {
            // create a temp file
            File  filedir;
            filedir = createDirectory("gstUpload");
            newfileDir = File.createTempFile(prefix, suffix,filedir);
            // check if the file is created
            if (newfileDir.exists()) {
                System.out.println("Temp File created: "+ newfileDir.getAbsolutePath());
                in  = blobdata.getBinaryStream();
                out = new FileOutputStream(newfileDir);
                copy(in,out,1024);
                out.flush();
                in.close();
                out.close();
            }else{
                System.out.println("Temp File cannot be created: "
                        + newfileDir.getAbsolutePath());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return newfileDir;
    }

    public static File createDirectory(String customDirectory){
        String path = LOCAL_PATH + SEPERATOR + customDirectory;
        File file   = new File(path);
        if (!file.exists()) {
            try{
                file.mkdir();
                System.out.println(" new directory created:" + file.getName());

            }catch(SecurityException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return file;

    }
    //start multipart

    public void initiateMultipart(String requestURL, String charset) throws IOException
    {
        print("initiateMultipart Utility");
        int TIMEOUTCONNECTION = 120000;
        URL url = new URL(requestURL);
        URLConnection connection = url.openConnection();
        httpConn = (HttpsURLConnection) connection;
        //add reuqest header
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        //httpConn.setRequestProperty("Accept-Type", "application/json");
        //httpConn.setRequestProperty("Content-Type", "text/plain");
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestMethod("POST");
        httpConn.setUseCaches(false);
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setConnectTimeout(TIMEOUTCONNECTION);
        httpConn.setReadTimeout(TIMEOUTCONNECTION);

        outputStream = new DataOutputStream(httpConn.getOutputStream());
    }

    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) throws IOException {

        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\""+name+"\"" + lineEnd);
        outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
        outputStream.writeBytes(lineEnd);
        outputStream.writeBytes(value);
        outputStream.writeBytes(lineEnd);

    }

    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {

        String fileName = uploadFile.getName();
        FileInputStream fileInputStream = new FileInputStream(uploadFile);
        System.out.println("file part utility.");
        //Document File
        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + lineEnd);
        outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd);
        outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

        outputStream.writeBytes(lineEnd);
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            outputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        outputStream.writeBytes(lineEnd);
        outputStream.writeBytes(lineEnd);

        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    }

    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        String response = null;
        print("finish Utility");
        try{
            response = getURLResponse(httpConn);
            outputStream.flush();
            outputStream.close();
        }finally {httpConn.disconnect();}
        return  response;
    }

    public HttpsURLConnection getHttpConn(){
        return  httpConn;
    }

    //by rest api upload file
    public void uploadSingleFile(String url,File file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file",file);


        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String serverUrl = url;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
        System.out.println(response);
        System.out.println("Response code: " + response.getStatusCode());
    }


    public static  String keyValueMappingString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public static String xmlToJson(String xmldata){
        JSONObject xmlJSONObj = new JSONObject();
        try {
            xmlJSONObj = XML.toJSONObject(xmldata);
            String jsonPrettyPrintString = xmlJSONObj.toString();
            System.out.println(jsonPrettyPrintString);
        }catch (JSONException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return  xmlJSONObj.toString();
    }

    public static void writeToZipFile( InputStream ins,String fileName, ZipOutputStream zipStream) throws FileNotFoundException, IOException
    {
        BufferedInputStream bis = new BufferedInputStream(ins);
        System.out.println("zipEntry-name:"+fileName);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipStream.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = bis.read(bytes)) >= 0)
        {
            zipStream.write(bytes, 0, length);
        }
        zipStream.closeEntry();
        bis.close();
    }

    public void generateLog(String level,String message,String logfileName){
        Logger logger = Logger.getLogger(logfileName);
        FileHandler fh;
        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate= dateFormat.format(date);
            String filepath=createDirectory("logs"+SEPERATOR+strDate).getCanonicalPath();


            filepath = filepath+"/"+logfileName+".log";
            Utility.print("path:"+filepath);
            fh = new FileHandler(filepath);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            // the following statement is used to log any messages
            switch (level.toLowerCase()){
                case "finest":
                    logger.finest(message);
                    break;
                case "finer":
                    logger.finer(message);
                    break;
                case "fine":
                    logger.fine(message);
                    break;
                case "config":
                    logger.fine(message);
                    break;
                case "info":
                    logger.info(message);
                    break;
                case "warning":
                    logger.warning(message);
                    break;
                case "severe":
                    logger.severe(message);
                    break;
                default:
                    logger.info(message);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
