package com.ratnaafin.crm.user.service.impl;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ratnaafin.crm.user.dao.*;
import com.ratnaafin.crm.user.dto.*;
import com.ratnaafin.crm.user.mapper.*;
import com.ratnaafin.crm.user.model.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.utils.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.*;
import java.text.SimpleDateFormat;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ratnaafin.crm.admin.constant.TrueFalse;
import com.ratnaafin.crm.common.exception.UserAlreadyExistException;
import com.ratnaafin.crm.common.exception.UserNotFoundException;
import com.ratnaafin.crm.common.service.Utility;
import com.ratnaafin.crm.user.service.UserService;

@Service(value = "userService")
@Transactional
public class UserServiceImpl implements UserService{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserKeyDao userkeydao;

    @Autowired
    private LoginDetailsDao loginDetailDao;

    @Autowired
    private CRMAppDao crmAppDao;

    @Autowired
    private CRMLoginDao crmLoginDao;

    @Autowired
    private AllJsonDao allJsonDao;

    @Autowired
    private APIErrorLogDao apierrorlogDao;

    @Autowired
    private URLConfigDao urlConfigDao;

    @Autowired
    private UniqueIDDtlDao uniqueIDDtlDao;

    @Autowired
    private InquiryMstDao inquiryMstDao;

    @Autowired
    private CRMUserMstDao crmUserMstDao;

    @Autowired
    private CRMUsersLoginHisDao crmUsersLoginHisDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OtpApiDtlDao otpApiDtlDao;

    @Autowired
    private PancardApiDtlDao pancardApiDtlDao;

    @Resource(name="tokenStore")
    private TokenStore tokenStore;

    @Resource(name="tokenServices")
    private ConsumerTokenServices tokenServices;

    @Autowired
    private SecurityOtpSmsDao securityOtpSmsDao;

    @Autowired
    private MobileSMSTrigDao mobileSMSTrigDao;

    @Autowired
    private SecurityOTPDao securityOTPDao;

    @Autowired
    private InquiryQueDtlDao inquiryQueDtlDao;

    @Autowired
    private testBlobDao testBlobDao;

    @Autowired
    DocUploadDtlDao docUploadDtlDao;

    @Autowired
    DocUploadBlobDao docUploadBlobDao;

    @Autowired
    private PerfiosReqResDao   perfiosReqResDao;

    @Autowired
    private BranchMasterDao branchMasterDao;

    @Autowired
    private CRMGstApiDtlDao crmGstApiDtlDao;

    @Autowired
    private CRMPincodeApiDtlDao crmPincodeApiDtlDao;

    @Autowired
    private SysParaMstDao sysParaMstDao;

    @Autowired
    private CrmDocumentMstDao crmDocumentMstDao;

    @Autowired
    private CrmLeadMstDao crmLeadMstDao;

    @Autowired
    private CRMCAMDtlDao crmcamDtlDao;

    @Autowired
    private CrmMiscMstDao crmMiscMstDao;

    @Autowired
    private  LosCorpositoryAPIDao losCorpositoryAPIDao;

    @Autowired
    private EquifaxAPILogDao equifaxAPILogDao;

    @Autowired
    private OtpVerificationDao otpVerificationDao;


    public List<UserDto> findAll() {
        List<User_master> list = new ArrayList<>();
        userDao.findAll().iterator().forEachRemaining(list::add);
        return UserMapper.convertUserListToDtoList(list);
    }

    @Override
    public void delete(String userName) {
        UserDto existingUser = findByUserName(userName);
        if(existingUser.getId() == 0) {
            throw new UserNotFoundException(userName);
        }
        userDao.delete(existingUser.getId());
    }

    @Override
    public UserDto findByUserName(String userName) {
        return UserMapper.convertUserToDto(userDao.findByUserName(userName, TrueFalse.TRUE.getBvalue()));
    }

    @Override
    public User_key_detailDto findByUserID(long UserID) {
        return UserKeyMapper.convertUserKeyToDto(userkeydao.findKeyByUserId(UserID));
    }

    @Override
    public UserDto save(UserDto userDto) {
        UserDto existingUser = findByUserName(userDto.getUser_name());
        if(existingUser.getId() != 0) {
            throw new UserAlreadyExistException("99","-99","User Exist","User already exist. UserName : "+userDto.getUser_name(),"User already exist. UserName : "+userDto.getUser_name());
        }
        User_master user = UserMapper.convertDtoToUser(userDto);
        setUserRoles(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return UserMapper.convertUserToDto(userDao.save(user));
    }

    private void setUserRoles(User_master user) {
        List<Role> dbRoleList = new ArrayList<>();
        for (Role role : user.getRole()) {
            Role dbRole = findRoleByName(role.getName());
            if(dbRole != null) {
                dbRoleList.add(dbRole);
            }
        }
        user.setRole(dbRoleList);
    }

    private Role findRoleByName(String name) {
        return roleDao.findByName(name);
    }

    @Override
    public String updatePassword(UserDto userDto) {
        UserDto existingUser = findByUserName(userDto.getUser_name());
        if(existingUser.getId() == 0) {
            throw new UserNotFoundException(userDto.getUser_name());
        }

        String bPassword  = passwordEncoder.encode(userDto.getPassword());
        userDao.updatePassword(existingUser.getId(), bPassword);
        return "Password change";
    }

    @Override
    public void updateLoginAttempt(String username, boolean flag) {
        userDao.updateLoginAttempt(username, flag);
    }

    @Override
    public void saveLoginDetails(LoginDetails login) {
        loginDetailDao.save(login);
    }

    /**
     * This will return all valid tokens.
     * @return
     */
    @Override
    public List<String> getTokens(String clientId, String userName) {
        List<String> tokenValues = new ArrayList<String>();
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(clientId, userName);
        if (tokens!=null){
            for (OAuth2AccessToken token:tokens){
                tokenValues.add(token.getValue());
            }
        }
        return tokenValues;
    }

    @Override
    public String revokeToken(String tokenId) {
        tokenServices.revokeToken(tokenId);
        return tokenId;
    }

    @Override
    public String revokeRefreshToken(String tokenId) {
        if (tokenStore instanceof JdbcTokenStore){
            ((JdbcTokenStore) tokenStore).removeRefreshToken(tokenId);
        }
        return tokenId;
    }

    @Override
    public UserDto createProfile(UserDto userDto) {
        //UserDto existingUser = findByUserName(userDto.getUser_name());
        //if(existingUser.getId() != 0) {
        //    throw new UserAlreadyExistException("99","-99","User Exist","User already exist. UserName : "+userDto.getUser_name(),"User already exist. UserName : "+userDto.getUser_name());
        //}
        User_master user = UserMapper.convertDtoToUser(userDto);
		/*
		List<Role> roleList = null;
        if (userDto.getRole() != null) {
            roleList = new ArrayList<>();
            for (int i = 0; i < userDto.getRole().size(); i++) {
            	System.out.println("I :"+i);
                roleList.add(RoleMapper.convertDtoToRole(userDto.getRole().get(i)));
            }
        }*/
        user.setRole(RoleMapper.convertDtoListToRoleList(userDto.getRole()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return UserMapper.convertUserToDto(userDao.save(user));
    }

    @Override
    public long createProfile(UserDto userDto, String Role) {
        long userid = 0;
        UserDto existingUser = findByUserName(userDto.getUser_name());
        userid = existingUser.getId();
        if(userid == 0) {
            User_master user = UserMapper.convertDtoToUser(userDto);
            user.setRole(useDefaultUserRole(Role.toUpperCase()));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDao.save(user);
        }
        return userid;
    }

    @Override
    public User_key_detailDto UserKeyProfileSave(String ServerUsername,String Username,String mac_id,String client_id,String host_name,String os_name) {
        UserDto userServerDto = new UserDto();
        userServerDto = findByUserName(ServerUsername);
        User_key_detailDto uDetailDto = new User_key_detailDto();
        if (userServerDto.getId() != 0) {
            User_key_detailDto uServerDetailDto = new User_key_detailDto();
            uServerDetailDto = findByUserID(userServerDto.getId());
            if (uServerDetailDto.getId() != 0) {
                UserDto userDto = new UserDto();
                userDto = findByUserName(Username);
                if (userDto.getId() != 0) {
                    uDetailDto = findByUserID(userDto.getId());
                    if (uDetailDto.getId() == 0) {
                        uDetailDto.setUser_id(userDto.getId());
                        uDetailDto.setMac_add(mac_id);
                        uDetailDto.setIp_add(client_id);
                        uDetailDto.setHost_name(host_name);
                        uDetailDto.setOs_name(os_name);
                        KeyPairGeneratorDto kDto = new KeyPairGeneratorDto();
                        kDto = KeyPairGenerator();
                        uDetailDto.setPublic_key(kDto.getStr_public_key());
                        uDetailDto.setPrivate_key(kDto.getStr_private_key());
                        uDetailDto.setServerPublic_key(uServerDetailDto.getPublic_key());
                        User_key_detail uKey_detail = UserKeyMapper.convertDtoToUserKey(uDetailDto);
                        userkeydao.save(uKey_detail);
                    }else {
                        uDetailDto.setServerPublic_key(uServerDetailDto.getPublic_key());
                    }
                }
            }
        }
        return uDetailDto;
    }

    @Override
    public String Encryptdata(String UserName, String ResponseData) {
        System.out.println("Encrypt Client ID "+UserName);
        String PrivateKey = null, PublicKey = null;

        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        KeyAgreement aKeyAgree;

        UserDto userDto = new UserDto();
        User_key_detailDto uDetailDto = null;
        userDto = UserMapper.convertUserToDto(userDao.findByUserName(UserName, TrueFalse.TRUE.getBvalue()));
        if (userDto.getId() != 0) {
            uDetailDto = new User_key_detailDto();
            uDetailDto = findByUserID(userDto.getId());
            if (uDetailDto.getId() != 0) {
                userkeydao.updateusagedate(uDetailDto.getId(), new Date());
                PrivateKey = uDetailDto.getPrivate_key();
                PublicKey = uDetailDto.getPublic_key();
                System.out.println("Encrpt PrivateKey Key : "+PrivateKey);
                System.out.println("Encrpt PublicKey Key : "+PublicKey);
                if((!PrivateKey.isEmpty()) && (!PublicKey.isEmpty())) {
                    publicKey = loadPublicKeyFile(PublicKey);
                    privateKey = loadPrivateKeyFile(PrivateKey);
                    byte[] encText = null;
                    try {
                        Security.addProvider(new BouncyCastleProvider());
                        aKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
                        aKeyAgree.init(privateKey);
                        aKeyAgree.doPhase(publicKey, true);

                        byte[] aBys = aKeyAgree.generateSecret();
                        KeySpec aKeySpec = new DESKeySpec(aBys);
                        SecretKeyFactory aFactory = SecretKeyFactory.getInstance("DES");
                        Key aSecretKey = aFactory.generateSecret(aKeySpec);
                        Cipher aCipher = Cipher.getInstance(aSecretKey.getAlgorithm());
                        aCipher.init(Cipher.ENCRYPT_MODE, aSecretKey);
                        encText = aCipher.doFinal(ResponseData.getBytes());
                    } catch (NoSuchAlgorithmException e) {
                        System.out.println("ECCrypto encryptText NoSuchAlgorithmException: " + e.getMessage());
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        System.out.println("ECCrypto encryptText NoSuchProviderException: " + e.getMessage());
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        System.out.println("ECCrypto encryptText InvalidKeyException: " + e.getMessage());
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        System.out.println("ECCrypto encryptText IllegalBlockSizeException: " + e.getMessage());
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        System.out.println("ECCrypto encryptText BadPaddingException: " + e.getMessage());
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        System.out.println("ECCrypto encryptText NoSuchPaddingException: " + e.getMessage());
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        System.out.println("ECCrypto encryptText InvalidKeySpecException: " + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.out.println("ECCrypto encryptText Exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return Base64.encodeBase64String(encText);
                }
            }
        }
        return null;
    }

    @Override
    public String Decryptdata(String ClientID, String RequestData) {
        System.out.println("Decript Client ID "+ClientID);
        String ServerPrivateKey = null, ServerPublicKey = null, Decryptdata = null;
        UserDto userServerDto = new UserDto();

        KeyAgreement aKeyAgree;

        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        Security.addProvider(new BouncyCastleProvider());

        userServerDto = UserMapper.convertUserToDto(userDao.findByUserName(ClientID, TrueFalse.TRUE.getBvalue()));
        if (userServerDto.getId() != 0) {
            User_key_detailDto uServerDetailDto = new User_key_detailDto();
            uServerDetailDto = findByUserID(userServerDto.getId());
            if (uServerDetailDto.getId() != 0) {
                userkeydao.updateusagedate(uServerDetailDto.getId(), new Date());
                ServerPrivateKey = uServerDetailDto.getPrivate_key();
                ServerPublicKey = uServerDetailDto.getPublic_key();
                System.out.println("Decript PrivateKey Key : "+ServerPrivateKey);
                System.out.println("Decript PublicKey Key : "+ServerPublicKey);
                if((!ServerPrivateKey.isEmpty()) && (!ServerPublicKey.isEmpty())) {
                    privateKey = loadPrivateKeyFile(ServerPrivateKey);
                    publicKey = loadPublicKeyFile(ServerPublicKey);
                    try {
                        aKeyAgree = KeyAgreement.getInstance("ECDH", "BC");
                        aKeyAgree.init(privateKey);
                        aKeyAgree.doPhase(publicKey, true);

                        byte[] aBys = aKeyAgree.generateSecret();
                        KeySpec aKeySpec = new DESKeySpec(aBys);
                        SecretKeyFactory aFactory = SecretKeyFactory.getInstance("DES");
                        Key aSecretKey = aFactory.generateSecret(aKeySpec);

                        Cipher aCipher = Cipher.getInstance(aSecretKey.getAlgorithm());
                        aCipher.init(Cipher.DECRYPT_MODE, aSecretKey);
                        byte[] decText = aCipher.doFinal(Base64.decodeBase64(RequestData.getBytes()));
                        Decryptdata = new String(decText);
                    } catch (NoSuchAlgorithmException e) {
                        System.out.println("ECCrypto decryptText NoSuchAlgorithmException: " + e.getMessage() );
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        System.out.println("ECCrypto decryptText NoSuchProviderException: " + e.getMessage() );
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        System.out.println("ECCrypto decryptText InvalidKeyException: " + e.getMessage() );
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        System.out.println("ECCrypto decryptText IllegalBlockSizeException: " + e.getMessage() );
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        System.out.println("ECCrypto decryptText BadPaddingException: " + e.getMessage() );
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        System.out.println("ECCrypto decryptText NoSuchPaddingException: " + e.getMessage() );
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        System.out.println("ECCrypto decryptText InvalidKeySpecException: " + e.getMessage() );
                        e.printStackTrace();
                    }
                    return Decryptdata;
                }
            }

        }
        return null;
    }

    @Override
    public PrivateKey loadPrivateKeyFile(String data) {
        PrivateKey privateKey = null;
        try {
            byte[] privKeyByteArray = java.util.Base64.getDecoder().decode(data.getBytes("UTF-8"));
            System.out.println("privKeyByteArray "+privKeyByteArray.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKeyByteArray);
            System.out.println("PKCS8EncodedKeySpec "+keySpec.toString());
            KeyFactory keyFactory = KeyFactory.getInstance("ECDH");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            System.out.println("ECCrypto loadPrivateKeyFile InvalidKeySpecException: " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ECCrypto loadPrivateKeyFile NoSuchAlgorithmException: " + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("ECCrypto loadPrivateKeyFile UnsupportedEncodingException: " + e.getMessage());
            e.printStackTrace();
        }
        return privateKey;
    }

    @Override
    public CRMAppDto findAppByID(long ID) {
        CRMAppDto crmAppDto = null;
        if (ID > 0){
            crmAppDto = CRMAppMapper.convertAppToDto(crmAppDao.findAppByID(ID));
            //crmAppDto.setA("-~RANKEY~-"+crmAppDto.getA()+"-~RANKEY~-");
            //crmAppDto.setB("-~SALT~-"+crmAppDto.getB()+"-~SALT~-");
        }
        return crmAppDto;
    }

    @Override
    public CRMLoginDto findUserByMobile(String mobile) {
        CRMLoginDto crmLoginDto = null;
        if (!mobile.trim().isEmpty()){
            crmLoginDto = CRMLoginMapper.convertCRMLoginToDto(crmLoginDao.findBymobileno(mobile,"C"));
        }
        return crmLoginDto;
    }

    @Override
    public CRMLoginDto findUserByEmail(String email) {
        CRMLoginDto crmLoginDto = null;
        if (!email.trim().isEmpty()){
            crmLoginDto = CRMLoginMapper.convertCRMLoginToDto(crmLoginDao.findByemailid(email,"C"));
        }
        return crmLoginDto;
    }

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

    public String func_get_result_val1(String a, String b, String c) {
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        byte[] result = null;
        String ls_error = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(a.toCharArray(), b.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.ENCRYPT_MODE, secretKey,ivspec);
            result = aesCipher.doFinal(c.getBytes());
            // return ": a : "+ a +" : b : "+ b +" : c : "+ c;
        }catch (NoSuchAlgorithmException e){
            ls_error = "NoSuchAlgorithmException";
            System.out.println("result : NoSuchAlgorithmException");
        }catch (InvalidKeySpecException e){
            ls_error = "InvalidKeySpecException";
            System.out.println("result : InvalidKeySpecException");
        }catch (NoSuchPaddingException e){
            ls_error = "NoSuchPaddingException";
            System.out.println("result : NoSuchPaddingException");
        }catch (InvalidAlgorithmParameterException e){
            ls_error = "InvalidAlgorithmParameterException";
            System.out.println("result : InvalidAlgorithmParameterException");
        }catch (InvalidKeyException e){
            ls_error = "InvalidKeyException";
            System.out.println("result : InvalidKeyException");
        }catch (IllegalBlockSizeException e){
            ls_error = "IllegalBlockSizeException";
            System.out.println("result : IllegalBlockSizeException");
        }catch (BadPaddingException e){
            ls_error = "BadPaddingException";
            System.out.println("result : BadPaddingException");
        }catch (Exception e) {
            ls_error = "Exception : "+e.getMessage();
        }catch(ExceptionInInitializerError e){
            ls_error = "ExceptionInInitializerError : "+e.getMessage();
            System.out.println("1result : Exception "+e.getMessage());
        }
        finally {
            if (ls_error != null) {
                APIErrorLog apiError = new APIErrorLog();
                apiError.setAction("en");
                apiError.setChannel("W");
                apiError.setError_msg(ls_error);
                apiError.setRequest_data("test");
                apiError.setRequest_unique_id("test");
                saveAPIErrorLog(apiError);
            }
            System.out.println("result : Finally");
        }
        return bytesToHex(result);
        //  return " krupa";
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

    public String func_get_data_val1(String a, String b, String c) {
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        byte[] data = null;
        String ls_error = null;
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
            ls_error = "NoSuchAlgorithmException";
            System.out.println("result : NoSuchAlgorithmException");
        }catch (InvalidKeySpecException e){
            ls_error = "InvalidKeySpecException";
            System.out.println("result : InvalidKeySpecException");
        }catch (NoSuchPaddingException e){
            ls_error = "NoSuchPaddingException";
            System.out.println("result : NoSuchPaddingException");
        }catch (InvalidAlgorithmParameterException e){
            ls_error = "InvalidAlgorithmParameterException";
            System.out.println("result : InvalidAlgorithmParameterException");
        }catch (InvalidKeyException e){
            ls_error = "InvalidKeyException";
            System.out.println("result : InvalidKeyException");
        }catch (IllegalBlockSizeException e){
            ls_error = "IllegalBlockSizeException";
            System.out.println("result : IllegalBlockSizeException");
        }catch (BadPaddingException e){
            ls_error = "BadPaddingException";
            System.out.println("result : BadPaddingException");
        }catch (Exception e) {
            ls_error = "Exception : "+e.getMessage();
        }catch(ExceptionInInitializerError e){
            ls_error = "ExceptionInInitializerError : "+e.getMessage();
            System.out.println("1result : Exception "+e.getMessage());
        }
        finally {
            if (ls_error != null) {
                APIErrorLog apiError = new APIErrorLog();
                apiError.setAction("get data val");
                apiError.setChannel("W");
                apiError.setError_msg(ls_error);
                apiError.setRequest_data("test");
                apiError.setRequest_unique_id("test");
                saveAPIErrorLog(apiError);
            }
            System.out.println("result : Finally");
        }
        return new String(data);
    }

    @Override
    public boolean func_check_pass(String rawpassword, String enpassword) {
        //String enpwd = null;
        //enpwd = passwordEncoder.encode(rawpassword);
        return passwordEncoder.matches(rawpassword,enpassword);
    }

    @Override
    public void saveJsonLog(AllJsonLog allJsonLog) {
        allJsonDao.save(allJsonLog);
    }

    @Override
    public void saveAPIErrorLog(APIErrorLog errorLog) { apierrorlogDao.save(errorLog);}


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

    @Override
    public PublicKey loadPublicKeyFile(String data) {
        PublicKey publicKey = null;
        try {
            byte[] publickKeyByteArray = java.util.Base64.getDecoder().decode(data.getBytes("UTF-8"));
            System.out.println("publickKeyByteArray "+publickKeyByteArray.toString());
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("ECDH");
            System.out.println("X509EncodedKeySpec "+new X509EncodedKeySpec(publickKeyByteArray).toString());
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publickKeyByteArray));
        } catch (InvalidKeySpecException e) {
            System.out.println("ECCrypto loadPublicKeyFile InvalidKeySpecException: " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ECCrypto loadPublicKeyFile NoSuchAlgorithmException: " + e.getMessage() );
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("ECCrypto loadPublicKeyFile UnsupportedEncodingException: " + e.getMessage() );
            e.printStackTrace();
        }
        return publicKey;
    }

    private List<Role> useDefaultUserRole(String Role) {
        Role userRole = roleDao.findByName(Role);
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        return roles;
    }

    @Override
    public KeyPairGeneratorDto KeyPairGenerator() {
        KeyPairGeneratorDto kDto = new KeyPairGeneratorDto();
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(521); //571
            KeyPair generatedKeyPair = keyGen.genKeyPair();
            kDto.setStr_public_key(Publickey(generatedKeyPair));
            kDto.setStr_private_key(Privatekey(generatedKeyPair));
        }catch (Exception e) {
            e.printStackTrace();
            return kDto;
        }
        return kDto;
    }
    private String Publickey(KeyPair keyPair)
    {
        PublicKey pub = keyPair.getPublic();
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pub.getEncoded());
        return java.util.Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded());
    }
    private String Privatekey(KeyPair keyPair) {
        PrivateKey priv = keyPair.getPrivate();
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(priv.getEncoded());
        return java.util.Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
    }
    private String getHexString(byte[] b)
    {
        String result = "";
        for (int i = 0; i < b.length; i++)
        {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
    @Override
    public URLConfigDto findURLDtlByID(long id) {
        URLConfigDto urlConfig = URLConfigMapper.convertToDto(urlConfigDao.findURLById(id,"Y"));
        if (urlConfig != null){
            CRMAppDto crmAppDto  = findAppByID(6);
            if (!crmAppDto.getA().trim().isEmpty() && !crmAppDto.getB().trim().isEmpty()){
                try{
                    urlConfig.setId(urlConfig.getId());
                }catch (NullPointerException e){
                    //e.printStackTrace();remove By Milan for not showcase NullPointerException Details
                }
                try{
                    urlConfig.setActive(urlConfig.getActive());
                }catch (NullPointerException e){
                    //e.printStackTrace();remove By Milan for not showcase NullPointerException Details
                }
                try{
                    urlConfig.setCountry_cd(urlConfig.getCountry_cd());
                }catch (NullPointerException e){
                    //e.printStackTrace();remove By Milan for not showcase NullPointerException Details
                }
                try{
                    urlConfig.setLanguage(urlConfig.getLanguage());
                }catch (NullPointerException e){
                    //e.printStackTrace();remove By Milan for not showcase NullPointerException Details
                }
                try{
                    urlConfig.setExpiry_dt(urlConfig.getExpiry_dt());
                }catch (NullPointerException e){
                    //e.printStackTrace();remove By Milan for not showcase NullPointerException Details
                }
                try{
                    if (!urlConfig.getUrl().trim().isEmpty()) {
                        urlConfig.setUrl(func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),urlConfig.getUrl()));
                    }
                }catch (NullPointerException e){
                    //e.printStackTrace();remove By Milan for not showcase NullPointerException Details
                }
                try{
                    if (!urlConfig.getUserid().trim().isEmpty()) {
                        urlConfig.setUserid(func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),urlConfig.getUserid()));
                    }
                }catch (NullPointerException e){
                    //e.printStackTrace();remove By Milan for not showcase NullPointerException Details
                }
                try{
                    if (!urlConfig.getKey().trim().isEmpty()) {
                        urlConfig.setKey(func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),urlConfig.getKey()));
                    }
                }catch (NullPointerException e){
                    //e.printStackTrace();remove By Milan for not showcase NullPointerException Details
                }
            }
        }
        return urlConfig;
    }
    @Override
    public String sentOTP(long id) {

        String ls_status = null;
        if (id > 0)
        {
            String ls_url = null;
            String sms_url = null;
            String ls_mobile_no = null;
            String ls_dc_url = null;
            String ls_customer_car = null;
            String ls_otp = null;
            long   ll_exp_time = 0;
            long   ll_trig_id = 0;

            URLConfigDto urlConfig = findURLDtlByID(3);
            ls_url = urlConfig.getUrl();

            SecurityOTPHdr securityOTPHdr = securityOTPDao.findById(id);
            ls_otp          = securityOTPHdr.getSent_otp();
            ll_exp_time     = securityOTPHdr.getExpiry_sec();
            ls_mobile_no    = securityOTPHdr.getContact2();
            ll_trig_id      = securityOTPHdr.getSms_trig_id();

            MobileSMSTrig mobileSMSTrig = mobileSMSTrigDao.findById(ll_trig_id);
            sms_url   = mobileSMSTrig.getUser_msg_txt();

            CRMAppDto crmAppDto  = findAppByID(4);
            CRMAppDto crmAppDto1  = findAppByID(1);

            sms_url = sms_url.replaceAll("<EXP_TIME>", String.valueOf(ll_exp_time/60) );
            sms_url = sms_url.replaceAll("<OTP_NO>", func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),ls_otp) );

            try {
                //   ls_dc_url = ls_url.replaceAll("<MOBILE_NO>", URLEncoder.encode("7405132703","UTF-8"));
                ls_dc_url = ls_url.replaceAll("<MOBILE_NO>", URLEncoder.encode(func_get_data_val(crmAppDto1.getA(),crmAppDto1.getB(),ls_mobile_no),"UTF-8"));
                ls_dc_url = ls_dc_url.replaceAll("<MESSAGE_TXT>", URLEncoder.encode(sms_url));
                ls_dc_url = ls_dc_url.replaceAll("<KEY>", URLEncoder.encode(urlConfig.getKey(),"UTF-8"));
                ls_dc_url = ls_dc_url.replaceAll("<USER>", URLEncoder.encode(urlConfig.getUserid(),"UTF-8"));
                // msg sent code
                URL otp_url = new URL(ls_dc_url);
                HttpURLConnection uc = (HttpURLConnection) otp_url.openConnection();
                uc.disconnect();

                if(uc.getResponseCode() == 200)
                {
                    // securityOtpSmsDao.updateOtpStatus(id,"S");
                    securityOTPDao.updateOtpStatus(id,"S");
                    ls_status = "200";
                }else {
                    //securityOtpSmsDao.updateOtpStatus(id,"F");
                    securityOTPDao.updateOtpStatus(id,"F");
                    ls_status = "99";
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return  ls_status ;// temp return 200 for success code
    }
    @Override
    public boolean sentEmail(long id) {
        boolean lb_email_status = false;
        if (id > 0) {
            String ls_email_body = null;
            String ls_email_url = null;
            String ls_to_email = null;
            String ls_password = null;
            String ls_smtp_port = null;
            String ls_smtp_server = null;
            String ls_subject = null;
            String ls_otp = null;
            String ls_customer_car = null;
            long   ll_exp_time = 0;
            long   ll_trig_id = 0;

            URLConfigDto urlConfig = findURLDtlByID(4);
            ls_email_url    = urlConfig.getUrl();
            ls_password     = urlConfig.getKey();
            ls_smtp_port    = urlConfig.getSmtp_port();
            ls_smtp_server  = urlConfig.getSmtp_server();

            SecurityOTPHdr securityOTPHdr = securityOTPDao.findById(id);
            ls_otp      = securityOTPHdr.getSent_otp();
            ll_exp_time = securityOTPHdr.getExpiry_sec();
            ls_to_email = securityOTPHdr.getEmail_id();
            ll_trig_id  = securityOTPHdr.getSms_trig_id();

            MobileSMSTrig mobileSMSTrig = mobileSMSTrigDao.findById(ll_trig_id);
            ls_email_body = mobileSMSTrig.getUser_email_txt();
            ls_subject = mobileSMSTrig.getEmail_subject();
            ls_customer_car = mobileSMSTrig.getCustomer_car();

            String ls_from_email = ls_email_url;
            String ls_pass = ls_password;

            CRMAppDto crmAppDto4 = findAppByID(4);
            CRMAppDto crmAppDto2 = findAppByID(2);

            Properties props = new Properties();
            props.put("mail.smtp.host", ls_smtp_server.trim());
            props.put("mail.smtp.port", ls_smtp_port.trim());
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            if (ls_email_body != null && ls_subject != null && ls_to_email !=null)
            {
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(ls_from_email, ls_pass);
                    }
                });
                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(ls_email_url));
                    message.addRecipient(Message.RecipientType.TO,new InternetAddress(URLEncoder.encode(func_get_data_val(crmAppDto2.getA(),crmAppDto2.getB(),ls_to_email),"UTF-8")));  //message.addRecipient(Message.RecipientType.CC,new InternetAddress(to));
                    //message.addRecipient(Message.RecipientType.TO, new InternetAddress("krupa.mistry@acuteinformatics.in"));
                    message.setSubject(ls_subject);

                    ls_email_body = ls_email_body.replaceAll("<OTP_NO>", func_get_data_val(crmAppDto4.getA(), crmAppDto4.getB(), ls_otp));
                    ls_email_body = ls_email_body.replaceAll("<CUSTOMER_CARE>", ls_customer_car);
                    ls_email_body = ls_email_body.replaceAll("<EXP_TIME>", String.valueOf(ll_exp_time/60));

                    message.setContent(ls_email_body, "text/html");
                    // Send message
                    Transport.send(message);
                    securityOTPDao.updateEmailStatus(id, "S");
                    lb_email_status = true;
                } catch (SendFailedException e) {
                    securityOTPDao.updateEmailStatus(id, "F");
                    lb_email_status = false;
                    e.printStackTrace();
                } catch (Exception e) {
                    securityOTPDao.updateEmailStatus(id, "F");
                    e.printStackTrace();
                }

            }else
            {

                lb_email_status = true;
                System.out.println("lb_email_status" + lb_email_status);
            }
        }
        return  lb_email_status;
    }

    @Override
    public UniqueIDDtlDto findByTransactionID(String transactionID) {
        UniqueIDDtlDto uniqueIDDtlDto = null;
        if (!transactionID.isEmpty()){
            uniqueIDDtlDto = UniqueIDDtlMapper.convertToDto(uniqueIDDtlDao.findByTransactionID(transactionID));
        }
        return uniqueIDDtlDto;
    }

    @Override
    public InquiryMstDto findByInquiryID(long ID) {
        InquiryMstDto inquiryMstDto = null;
        if(ID != 0){
            inquiryMstDto = InquiryMstMapper.convertToDto(inquiryMstDao.findByInquiryID(ID));
        }
        return inquiryMstDto;
    }

    @Override
    public void saveUniqueIDDtl(UniqueID_dtl uniqueIDDtl) {
        uniqueIDDtlDao.save(uniqueIDDtl);
    }

    @Override
    public Map<String, String> parseUrlFragment(String url) {
        Map<String, String> output = new LinkedHashMap<> ();
        String[] keys = url.split ("&");
        for (String key : keys) {
            String[] values = key.split ("=");
            output.put (values[0], (values.length > 1 ? values[1] : ""));
        }
        return output;
    }

    @Override
    public void updateWebhookStatus(String transactionID, String webhookStatus, String status, String webhookRes, Blob img, Blob xmlfile,String downloadStatus) {
        uniqueIDDtlDao.updateWebhookStatus(transactionID,webhookStatus,status,webhookRes,img,xmlfile,downloadStatus);
    }

    @Override
    public OAuth2Authentication readAuth(String tokenId) {
        return tokenStore.readAuthentication(tokenId);
    }

    @Override
    public void crmSetLoginPassword(String mobile, String user_password) {
        crmLoginDao.crmSetLoginPassword(mobile,user_password);

    }

    @Override
    public String func_get_pass(String rawpassword) {
        String enpwd = null;
        enpwd = passwordEncoder.encode(rawpassword);
        return enpwd;
    }

    @Override
    public void savePancardApiLog(PancardApiDtl pancardApiDtl) {
        pancardApiDtlDao.save(pancardApiDtl);
    }

    @Override
    public int isPancardExist(long ref_inquiry_id,String pancardno) {
        int pancardCount = pancardApiDtlDao.getpancardCount(ref_inquiry_id,pancardno);
        return pancardCount;
    }

    @Override
    public void saveOtpApiDtl(OtpApiDtl otpApiDtl) {
        otpApiDtlDao.save(otpApiDtl);
    }



    @Override
    public String getInquiryQueKeyValue(Long ref_inquiry_id, String lable) {
        return inquiryQueDtlDao.getInquiryQueKeyValue(ref_inquiry_id,lable);
    }


    @Override
    public int updateInquiryStatus(long inquiryId, String status) {
        return inquiryMstDao.updateInquiryStatus(inquiryId,status);
    }

    @Override
    public int assignTeamLead(long inquiryId, long teamLeadID) {
        return inquiryMstDao.assignTeamLead(inquiryId,teamLeadID);
    }

    @Override
    public int assignTeamMember(long inquiryId, long teamMemberID,String status) {
        return inquiryMstDao.assignTeamMember(inquiryId,teamMemberID,status);
    }

    @Override
    public int updateInquiryPriority(long inquiryId, String priority,String leadGenerate) {
        return inquiryMstDao.updateInquiryPriority(inquiryId,priority,leadGenerate);
    }

    @Override
    public void saveDoc(testBlob testBlob) {
        testBlobDao.save(testBlob);
    }

    @Override
    public void saveDocument(DocUploadDtl docUploadDtl) {
        docUploadDtlDao.save(docUploadDtl);
    }

    @Override
    public void saveDocumentLob(DocUploadBlobDtl docUploadBlobDtl) {
        docUploadBlobDao.save(docUploadBlobDtl);
    }

    @Override
    public testBlob findbyDocumentID(String id) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public CRMUserMstDto getCRMUsersDtl(String mobile,String user_flag) {
        CRMUserMstDto crmUserMstDto = CRMUserMstMapper.convertToDto(crmUserMstDao.findBymobileno(mobile));
        return crmUserMstDto;
    }

    @Override
    public void saveCRMUsersLoginHistory(CRMUsersLoginHis crmUsersLoginHis) {
        crmUsersLoginHisDao.save(crmUsersLoginHis);
    }

    @Override
    public String getuniqueId() {
        //generate UUID for unique ID
        UUID uuid = UUID.randomUUID();
        String patternDate = "yyyyMMdd";
        String patternTime = "HHmmssSSS";
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(patternDate);
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(patternTime);
        String date = simpleDateFormatDate.format(new Date());
        String time = simpleDateFormatTime.format(new Date());
        return (time+uuid.toString()+date);
    }

    @Override
    public CRMUsersLoginHisDto getCRMUsersLoginHistory(String refId) {
        CRMUsersLoginHisDto crmUsersLoginHisDto =CRMUsersLoginHisMapper.convertToDto(crmUsersLoginHisDao.findByRefId(refId));
        return crmUsersLoginHisDto;
    }

    @Override
    public void updateOTPVerifyStatus(String otpFlag,String resStatus,String resData,String refId) {
        crmUsersLoginHisDao.updateResponseStatus(otpFlag,resStatus,resData,refId);
    }

    @Override
    public JSONObject getUsersDetails(CRMUserMst crmUserMst) {
        CRMAppDto crmAppDtoPII = this.findAppByID(3);
        JSONObject jsonObject = new JSONObject();
        String firstName = null, middleName = null, lastName = null, mobile = null, email = null, jsonPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", userName = null,userFlag=null;
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(jsonPattern);
        String lastLoginDate = "NA";
        if (crmUserMst.getLast_login_dt() != null) {
            lastLoginDate = simpleDateFormatDate.format(crmUserMst.getLast_login_dt());
        }
        if (crmUserMst.getFirst_name() != null) {
            firstName = this.func_get_data_val(crmAppDtoPII.getA(), crmAppDtoPII.getB(), crmUserMst.getFirst_name());
        }
        if (crmUserMst.getMiddle_name() != null){
            middleName = this.func_get_data_val(crmAppDtoPII.getA(), crmAppDtoPII.getB(), crmUserMst.getMiddle_name());
        }
        if (crmUserMst.getLast_name() != null) {
            lastName = this.func_get_data_val(crmAppDtoPII.getA(), crmAppDtoPII.getB(), crmUserMst.getLast_name());
        }
        switch (crmUserMst.getFlag()){
            case "C":
                userFlag = "customer";
                break;
            case "P":
                userFlag = "partner";
                break;
        }
        String comp_cd = null,branch_cd = null,branch_nm = null;
        if ((crmUserMst.getComp_cd() != null) && (crmUserMst.getBranch_cd() != null)) {
            BranchMasterDto branchMasterDto = BranchMasterMapper.convertAppToDto(branchMasterDao.func_find_branch_nm_by_branch_cd(crmUserMst.getComp_cd(),crmUserMst.getBranch_cd()));
            if (branchMasterDto != null){
                branch_nm = branchMasterDto.getBranch_nm();
            }
            comp_cd = crmUserMst.getComp_cd().trim();
            branch_cd = crmUserMst.getBranch_cd().trim();
        }
        try {
            jsonObject.put("flag",userFlag);
            jsonObject.put("firstName", firstName);
            jsonObject.put("middleName", middleName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("companyCode",comp_cd);
            jsonObject.put("baseBranchCode", branch_cd);
            jsonObject.put("baseBranchName",branch_nm);
            jsonObject.put("lastLoginDate", lastLoginDate);
        } catch (JSONException jsonException) {
            System.out.println(jsonException.getMessage());
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
        return jsonObject;
    }

    @Override
    public void saveErrorLog(String ls_channel, String ls_action, String ls_req_res, String ls_user ,String ls_error_msg,String module) {
        CRMAppDto crmAppDtoreq = this.findAppByID(5);
        ls_req_res = this.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),ls_req_res);
        APIErrorLog apiError = new APIErrorLog();
        apiError.setAction(ls_action);
        apiError.setChannel(ls_channel);
        apiError.setError_msg(ls_error_msg);
        apiError.setRequest_data(ls_req_res);
        apiError.setRequest_unique_id(ls_user);
        apiError.setModule(module);
        apierrorlogDao.save(apiError);
    }

    @Override
    public void saveJsonLog(String ls_channel, String ls_flag, String ls_action, String ls_req_res, String ls_user,String module) {
        CRMAppDto crmAppDtoreq = this.findAppByID(5);
        ls_req_res = this.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),ls_req_res);
        AllJsonLog allJsonLog = new AllJsonLog();
        allJsonLog.setChannel(ls_channel);
        allJsonLog.setAction(ls_action);
        allJsonLog.setFlag(ls_flag);
        allJsonLog.setRequest_data(ls_req_res);
        allJsonLog.setUnique_id(ls_user);
        allJsonLog.setModule(module);
        allJsonDao.save(allJsonLog);
    }

    @Override
    public String getJsonError(String error_cd, String error_title, String error_msg, String error_detail, String status ,String channel,String action,String request,String user,String module,String flag) {
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        if (flag != "U"){
            saveErrorLog(channel,action,request,user,error_detail,module);
        }
        try
        {
            jsonObject1.put("error_cd", error_cd);
            jsonObject1.put("error_title",error_title);
            jsonObject1.put("error_msg",error_msg);
            jsonObject1.put("error_detail",error_detail);
            jsonObject2.put("status",status);
            jsonObject2.put("error_data",jsonObject1);
        }catch (JSONException e){
            saveErrorLog(channel,action,request,user,e.getMessage(),module);
            try {
                jsonObject1.put("error_cd", error_cd);
                jsonObject1.put("error_title", error_title);
                jsonObject1.put("error_msg", error_msg);
                jsonObject1.put("error_detail", e.getMessage());
                jsonObject2.put("status", status);
                jsonObject2.put("error_data", jsonObject1);
                saveJsonLog(channel,"res",action,jsonObject2.toString(),user,module);
                return jsonObject2.toString();
            } catch (JSONException jsonException) {
                System.out.println("Result : JsonException Error."+jsonException.getMessage());
                jsonException.printStackTrace();
            }
        }
        saveJsonLog(channel,"res",action,jsonObject2.toString(),user,module);
        return jsonObject2.toString();
    }
    @Override
    public String func_get_base_url(String pattern) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        StringBuffer ls_server_path = null;
        String ls_servletPath = null,userId = "";;
        ls_server_path = request.getRequestURL();
        ls_servletPath = request.getServletPath();
        if ((!ls_server_path.toString().isEmpty()) && (!ls_servletPath.isEmpty())) {
            ls_servletPath = ls_server_path.substring(0, ls_server_path.indexOf(ls_servletPath) + 1);
        }
        return ls_servletPath+pattern;
    }
    @Override
    public void savePerfiosReqResDtl(PerfiosReqResDtl perfiosReqResdtl) {
        perfiosReqResDao.save(perfiosReqResdtl);
    }
    @Override
    public PerfiosReqResDto findByPerfiosTransactionID(String transactionID) {
        PerfiosReqResDto perfiosReqResDto = null;
        if (!transactionID.isEmpty()){}
        perfiosReqResDto = PerfiosReqResMapper.convertToDto(perfiosReqResDao.findByPerfiosTransactionID(transactionID));

        return perfiosReqResDto;
    }

    @Override
    public void updatePerfiosWebhookStatus(String transactionID, String webhookStatus, String status, String webhookRes, Blob zip, Blob xlsfile,String jsonfile,String downloadStatus,String remarks) {
        perfiosReqResDao.updatePerfiosWebhookStatus(transactionID,webhookStatus,status,webhookRes,zip,xlsfile,jsonfile,downloadStatus,remarks);

    }

    @Override
    public String encryptReqRes(UserService userService,String requestData) {
        CRMAppDto crmAppDtoreq = userService.findAppByID(5);
        return userService.func_get_result_val(crmAppDtoreq.getA(),crmAppDtoreq.getB(),requestData);
    }

    @Override
    public String deleteDocument(Long refID, Long docID) {
        //int ll_cnt = docUploadDtlDao.getDocumentCnt(refID, docID);
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        /*
        if(ll_cnt > 0){
                try {
                    jsonObject1.put("error_cd", "-99");
                    jsonObject1.put("error_title", "Something went wrong.Please contact to system Admin.!");
                    jsonObject1.put("error_msg", "Document is verified. you can't upload document again");
                    jsonObject1.put("error_detail","Document is verified. you can't upload document again");
                    jsonObject2.put("status", "99");
                    jsonObject2.put("response_data",jsonObject1);
                    return jsonObject2.toString();
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
        } else {*/
        try {
            docUploadDtlDao.deleteDocument(refID, docID);
            jsonObject2.put("status", "0");
            jsonObject1.put("message", "documents are removed successfully");
            jsonObject2.put("response_data",jsonObject1);
            return jsonObject2.toString();
        } catch (Exception jsonException) {
            return "error!";
        }
        // }
    }

    @Override
    public DocUploadBlobDtl findDocByUUID(String uuid) {
        return docUploadBlobDao.findDocByUUID(uuid);
    }


    @Override
    public int updateDocStatus(Long inquiryId,Long docID,String status,String remarks) {
        return docUploadDtlDao.updateDocStatus(inquiryId,docID,status,remarks);
    }

    @Override
    public int getDocumentCnt(Long refID, Long docID) {
        return docUploadDtlDao.getDocumentCnt(refID,docID);
    }

    @Override
    public void updateOTPFlag(String transaction_id,String otpFlag,String verifyResponseData) {
        otpApiDtlDao.updateOTPVerifyFlag(transaction_id,otpFlag,verifyResponseData);
    }

    @Override
    public void saveGSTApiLog(CRMGstApiDtl crmGstApiDtl) {
        crmGstApiDtlDao.save(crmGstApiDtl);
    }

    @Override
    public void savePincodeApiLog(CRMPincodeApiDtl crmPincodeApiDtl) {
        crmPincodeApiDtlDao.save(crmPincodeApiDtl);
    }


    @Override
    public List<DocUploadDtl> getDocListByDocId(long leadId, long srId, String entityType,long docId){
        return docUploadDtlDao.getDocListByDocId(leadId,srId,entityType,docId);
    }

    @Override
    public DocUploadBlobDtl findDocBlobByUUID(String uuid) {
        return docUploadBlobDao.findDocByUUID(uuid);
    }

    @Override
    public SysParaMst getParaVal(String comp_cd , String branch_cd , long para_cd){
        return sysParaMstDao.getParaVal(comp_cd,branch_cd,para_cd);
    }

    @Override
    public List<CrmDocumentMst> getDocMstListByDocType(String docType){
        return crmDocumentMstDao.getDocMstListByDocType(docType);
    }

    public HashMap<String, String> callingDBObject(String objectType, String objectName,HashMap param){
        Connection connection = null;
        CallableStatement cs  = null;
        HashMap<String,String> outParam = new HashMap<String, String>();
        String result,entityType;
        long bankLineID = 0;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            /** executing sql or call any procedure or function by adding cases**/
            if(objectType.equals("sql")){
                String sql = null,responseStatus=null;
                sql  = (String)param.get("sql");
                switch (objectName){
                    case "readXMLResponse":
                        Utility.print("CallingDB:"+objectType+"/"+objectName);
                        try(Statement stmt = connection.createStatement()){
                            ResultSet rs = stmt.executeQuery(sql);
                            while (rs.next()){
                                outParam.put("code",rs.getString("code"));
                                outParam.put("message",rs.getString("message"));
                                outParam.put("perfiosTransactionId",rs.getString("perfiosTransactionId"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            outParam.put("error",e.getMessage());
                        }
                        break;
                    case "XMLResponseProcessUpload":
                        Utility.print("CallingDB:"+objectType+"/"+objectName);
                        String isError      = "0";
                        String isAccepted   = "0";
                        String isSuccess    = "0";
                        try(Statement stmt = connection.createStatement()){
                            ResultSet rs = stmt.executeQuery(sql);
                            while (rs.next()){
                                isError     = rs.getString("is_error");
                                isAccepted  = rs.getString("is_accepted");
                                isSuccess   = rs.getString("is_success");

                                if(isError!=null && isError.equals("1")){
                                    responseStatus = "error";
                                    outParam.put("xmlErrorCode",rs.getString("err_cd"));
                                    outParam.put("xmlResponseMessage",rs.getString("err_msg"));
                                }
                                if(isAccepted!=null && isAccepted.equals("1")){
                                    responseStatus = "accepted";
                                    outParam.put("xmlResponseMessage",rs.getString("accepted_msg"));
                                }
                                if(isSuccess!=null && isSuccess.equals("1")){
                                    responseStatus = "success";
                                    outParam.put("xmlResponseMessage",rs.getString("success_msg"));
                                }
                                outParam.put("xmlResponseStatus",responseStatus);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            outParam.put("error",e.getMessage());
                        }
                        break;
                    case "findGeneralDtlByID":
                        String refId = (String)param.get("refId");
                        sql = sql.replace("?1",refId);

                        String legal_entity_name=null,entity_type=null,gst_no=null;
                        try(Statement stmt = connection.createStatement()){
                            ResultSet rs = stmt.executeQuery(sql);
                            while (rs.next()){
                                legal_entity_name   = rs.getString("legal_entity_name");
                                entity_type         = rs.getString("entity_type");
                                gst_no              = rs.getString("gst_no");

                                outParam.put("legal_entity_name",legal_entity_name);
                                outParam.put("entity_type",entity_type);
                                outParam.put("gst_no",gst_no);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            outParam.put("error",e.getMessage());
                        }
                        break;
                }
            }
            else if(objectType.equals("procedure")){
                long refID,srID,bankID;
                String requestData=null,remarks=null;
                switch (objectName){
                    case "pack_document.proc_perfios_bank_initial_param":
                        Utility.print("CallingDB:"+objectType+"/"+objectName);
                        String loanAmount,loanDuration,loanType,scannedStmtFlag;
                        refID       = (long)param.get("refID");
                        srID        = (long)param.get("serialNo");
                        bankLineID  = (long)param.get("bankLineID");
                        entityType  = (String)param.get("entityType");
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?,?,?,?,?)}");
                        cs.setLong(1,refID);
                        cs.setLong(2,srID);
                        cs.setLong(3,bankLineID);
                        cs.setString(4,entityType);


                        cs.registerOutParameter(5,2005); //loanAmt
                        cs.registerOutParameter(6,2005); //loan Duration
                        cs.registerOutParameter(7,2005); //loanType
                        cs.registerOutParameter(8,2005); //isScanned doc
                        cs.registerOutParameter(9, Types.NUMERIC); //bankid
                        cs.execute();

                        loanAmount      = cs.getString(5);
                        loanDuration    = cs.getString(6);
                        loanType        = cs.getString(7);
                        scannedStmtFlag = cs.getString(8);
                        bankID          = cs.getLong(9);

                        outParam.put("loanAmount",loanAmount);
                        outParam.put("loanDuration",loanDuration);
                        outParam.put("loanType",loanType);
                        outParam.put("scannedStmtFlag",scannedStmtFlag);
                        outParam.put("bankID",String.valueOf(bankID));
                        break;
                    case "pack_document.proc_perfios_init_req_validate":
                    case "pack_corpository.proc_init_req_validate":
                        String requestType,param1;
                        refID       = (Long)param.get("refID");
                        srID        = (Long)param.get("srID");
                        entityType  = (String)param.get("entityType");
                        requestType = (String)param.get("requestType");
                        param1      = (String)param.get("param1");

                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?,?)}");
                        cs.setLong(1,refID);
                        cs.setLong(2,srID);
                        cs.setString(3,entityType);
                        cs.setString(4,requestType);
                        cs.setString(5,param1);

                        cs.registerOutParameter(6,2005);
                        cs.execute();
                        result      = cs.getString(6);

                        outParam.put("result",result);
                        break;
                    case "pack_document.proc_active_document_count":
                        String documentCategory=null,docTypeID=null;
                        long count = 0;
                        refID            = (Long)param.get("refID");
                        srID             = (Long)param.get("serialNo");
                        bankLineID        = (Long)param.get("bankLineID");
                        entityType        = (String)param.get("entityType");
                        documentCategory  = (String)param.get("documentCategory");
                        docTypeID         = (String)param.get("docTypeID");
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?,?,?)}");
                        cs.setLong(1,refID);
                        cs.setLong(2,srID);
                        cs.setString(3,entityType);
                        cs.setString(4,documentCategory);
                        cs.setLong(5,bankLineID);
                        cs.setString(6,docTypeID);

                        cs.registerOutParameter(7,Types.NUMERIC);
                        cs.execute();
                        count = cs.getInt(7);
                        outParam.put("result",""+count);
                        break;
                    case "pack_document.proc_active_api_flag_bankid_wise":
                        Utility.print("executing proc_active_api_flag_bankid_wise");
                        refID       = (Long)param.get("refID");
                        srID        = (Long)param.get("serialNo");
                        entityType  = (String)param.get("entityType");
                        bankLineID  = (Long)param.get("bankLineID");
                        Utility.print("refID:"+refID);
                        Utility.print("srID:"+srID);
                        Utility.print("entityType:"+entityType);
                        Utility.print("bankLineID:"+bankLineID);


                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?)}");
                        cs.setLong(1,refID);
                        cs.setLong(2,srID);
                        cs.setString(3,entityType);
                        cs.setLong(4,bankLineID);
                        cs.registerOutParameter(5,2005);
                        cs.execute();

                        result = cs.getString(5);
                        outParam.put("result",""+result);
                        break;
                    case "pack_document.proc_readxml_stmt_upload_response":
                        Utility.print("in case4:"+objectType+"/"+objectName);
                        String perfiosTranId,xmlResponse,responseStatus,outJson;
                        //get inParam
                        perfiosTranId = (String)param.get("perfiosTransactionId");
                        xmlResponse   = (String)param.get("xmlResponse");

                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?)}");
                        cs.setString(1,perfiosTranId);
                        cs.setString(2,xmlResponse);

                        cs.registerOutParameter(3,2005); //responseStatus
                        cs.registerOutParameter(4,2005); //outJson
                        cs.execute();

                        //set variable from out parameters
                        responseStatus = cs.getString(3);
                        outJson        = cs.getString(4);

                        //set out parameter
                        outParam.put("responseStatus",responseStatus);
                        outParam.put("outJson",outJson);
                        break;
                    case "pack_corpository.proc_get_active_token_detail":
                        Utility.print(objectType+"/"+objectName);
                        String flag=null;
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?,?)}");

                        cs.registerOutParameter(1,2005); //auth_token
                        cs.registerOutParameter(2,2005); //auth_user_id
                        cs.registerOutParameter(3,2005); //user_name
                        cs.registerOutParameter(4,2005); //user_email
                        cs.registerOutParameter(5,2005); //flag
                        cs.registerOutParameter(6,2005); //json output
                        cs.execute();

                        //set variable from out parameters
                        flag    = cs.getString(5);
                        outJson = cs.getString(6);
                        //set out parameter
                        outParam.put("flag",flag);
                        outParam.put("result",outJson);
                        break;
                    case "proc_insert_error_log":
                        Utility.print(objectType+"/"+objectName);
                        String obj_name,error_flag,error_msg;
                        //get inParam
                        obj_name    = (String)param.get("obj_name");
                        error_flag  = (String)param.get("error_flag");
                        error_msg   = (String)param.get("error_msg");
                        remarks     = (String)param.get("remarks");

                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?)}");

                        cs.setString(1,obj_name);
                        cs.setString(2,error_flag);
                        cs.setString(3,error_msg);
                        cs.setString(4,remarks);
                        cs.execute();

                        outParam.put("result","success");
                        connection.commit();
                        break;
                    case "pack_otp_new.proc_validate_otp_request":
                        Utility.print(objectType+"/"+objectName);
                        String action,channel,tokenID;
                        //get inParam
                        action   = (String)param.get("action");
                        channel  = (String)param.get("channel");
                        tokenID  = (String)param.get("tokenID");
                        requestType = (String)param.get("requestType");

                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?)}");

                        cs.setString(1,action);
                        cs.setString(2,channel);
                        cs.setString(3,tokenID);
                        cs.setString(4,requestType);

                        cs.registerOutParameter(5,2005);
                        cs.execute();

                        result = cs.getString(5);
                        outParam.put("result",result);
                        break;
                    case "pack_otp_new.proc_insert_otp_verification_sdt":
                        Utility.print(objectType+"/"+objectName);
                        String transactionID,apiResponse,apiRequest;
                        //get inParam
                        tokenID        = (String)param.get("tokenID");
                        transactionID  = (String)param.get("transactionID");
                        apiRequest     = (String)param.get("apiRequest");
                        apiResponse    = (String)param.get("apiResponse");

                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?)}");

                        cs.setString(1,tokenID);
                        cs.setString(2,transactionID);
                        cs.setString(3,apiRequest);
                        cs.setString(4,apiResponse);
                        cs.registerOutParameter(5,2005);
                        cs.execute();

                        result = cs.getString(5);
                        outParam.put("result",result);
                        break;
                    case "pack_otp_new.proc_set_verified_otp":
                        Utility.print(objectType+"/"+objectName);
                        String mobileNo=null;
                        //get inParam
                        tokenID = (String)param.get("tokenID");
                        requestType = (String)param.get("requestType");
                        mobileNo    = (String)param.get("mobileNo");
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?)}");

                        cs.setString(1,tokenID);
                        cs.setString(2,requestType);
                        cs.setString(3,mobileNo);

                        cs.registerOutParameter(4,2005);
                        cs.execute();

                        result = cs.getString(4);
                        outParam.put("result",result);
                        break;
                    case "pack_equifax_new.proc_validate_token_id":
                        Utility.print(objectType+"/"+objectName);
                        //get inParam
                        tokenID = (String)param.get("tokenID");

                        cs = connection.prepareCall("{ call "+objectName+"(?,?)}");

                        cs.setString(1,tokenID);
                        cs.registerOutParameter(2,2005);
                        cs.execute();

                        result = cs.getString(2);
                        outParam.put("result",result);
                        break;
                    case "pack_equifax_new.proc_response_error_fetcher":
                        Utility.print(objectType+"/"+objectName);
                        String errorCode=null,errorDesc=null,response=null;
                        //get inParam
                        response = (String)param.get("response");
                        entityType =(String)param.get("entityType");
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?)}");

                        cs.setString(1,response);
                        cs.setString(2,entityType);

                        cs.registerOutParameter(3,2005);
                        cs.registerOutParameter(4,2005);
                        cs.execute();

                        errorCode = cs.getString(3);
                        errorDesc = cs.getString(4);

                        outParam.put("errorCode",errorCode);
                        outParam.put("errorDesc",errorDesc);
                        break;
                    case "pack_otp_new.proc_insert_others_otp_dtl":
                        long inquiryCode = 0,serialNo=0;
                        String mobile = null;
                        inquiryCode = (Long)param.get("inquiryCode");
                        refID       = (Long)param.get("refID");
                        serialNo    = (Long)param.get("serialNo");
                        entityType  = (String)param.get("entityType");
                        requestType = (String)param.get("requestType");
                        mobile      = (String)param.get("mobile");
                        tokenID     = (String)param.get("tokenID");
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?,?,?,?)}");

                        cs.setLong(1,inquiryCode);
                        cs.setLong(2,refID);
                        cs.setLong(3,serialNo);
                        cs.setString(4,entityType);
                        cs.setString(5,requestType);
                        cs.setString(6,mobile);
                        cs.setString(7,tokenID);

                        cs.registerOutParameter(8,2005);
                        cs.execute();
                        result = cs.getString(8);
                        outParam.put("result",result);
                        break;
                    case "pack_healthcheck_common.proc_get_lead_info":
                        Utility.print(objectType+"/"+objectName);
                        //get inParam
                        refID       = (Long)param.get("refID");
                        serialNo    = (Long)param.get("serialNo");
                        entityType  = (String)param.get("entityType");
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?)}");

                        cs.setLong(1,refID);
                        cs.setLong(2,serialNo);
                        cs.setString(3,entityType);

                        cs.registerOutParameter(4,Types.NUMERIC);
                        cs.registerOutParameter(5,2005);
                        cs.execute();

                        inquiryCode = cs.getLong(4);
                        mobile = cs.getString(5);

                        outParam.put("inquiryCode",String.valueOf(inquiryCode));
                        outParam.put("mobile",mobile);
                        break;
                    case "pack_healthcheck_common.proc_aadhar_init_req_validate":
                        Utility.print(objectType+"/"+objectName);

                        //get inParam
                        refID       = (Long)param.get("refID");
                        serialNo    = (Long)param.get("serialNo");
                        entityType  = (String)param.get("entityType");
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?)}");

                        cs.setLong(1,refID);
                        cs.setLong(2,serialNo);
                        cs.setString(3,entityType);

                        cs.registerOutParameter(4,2005);
                        cs.execute();

                        result = cs.getString(4);
                        outParam.put("result",result);
                        break;
                    case "pack_otp_new.proc_validate_token_id":
                        Utility.print(objectType+"/"+objectName);
                        //get inParam
                        tokenID     = (String)param.get("tokenID");
                        requestType = (String)param.get("requestType");
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?)}");

                        cs.setString(1,tokenID);
                        cs.setString(2,requestType);

                        cs.registerOutParameter(3,2005);
                        cs.execute();

                        result = cs.getString(3);
                        outParam.put("result",result);
                        break;
                    case "pack_healthcheck_common.proc_insert_msg_mail_api_log":
                        String messageCategory=null;
                        Utility.print(objectType+"/"+objectName);
                        //get inParam
                        tokenID     = (String)param.get("tokenID");
                        requestType = (String)param.get("requestType");
                        requestData = (String)param.get("requestData");
                        response    = (String)param.get("responseData");
                        transactionID = (String)param.get("transactionID");
                        messageCategory = (String)param.get("messageCategory");

                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?,?,?,?,?)}");

                        cs.setString(1,tokenID);
                        cs.setString(2,requestType);
                        cs.setString(3,requestData);
                        cs.setString(4,response);
                        cs.setString(5,transactionID);
                        cs.setString(6,messageCategory);


                        cs.registerOutParameter(7,2005);
                        cs.execute();

                        result = cs.getString(7);
                        Utility.print("proc_output:\n"+result);
                        outParam.put("result",result);
                        break;
                    case "proc_calling_db_objects":
                        Utility.print(objectType+"/"+objectName);
                        //get inParam
                        JSONObject jsonReqData;
                        String caseAction,data;
                        caseAction     = (String)param.get("action");
                        data           = (String)param.get("jsonReqData");
                        jsonReqData    = new JSONObject(data);
                        cs = connection.prepareCall("{ call "+objectName+"(?,?,?)}");

                        cs.setString(1,caseAction);
                        cs.setString(2,jsonReqData.toString());

                        cs.registerOutParameter(3,2005);
                        cs.execute();

                        result = cs.getString(3);
                        outParam.put("result",result);
                        break;
                    default:
                        outParam.put("error","case not found:"+objectType+"-"+objectName);
                }
            }
            connection.close();
            if(cs!=null){
                cs.close();
            }
            return  outParam;
        }catch (SQLException e) {
            e.printStackTrace();
            outParam.put("error",e.getMessage());
            return outParam;
        }catch (Exception e) {
            e.printStackTrace();
            outParam.put("error",e.getMessage()+",Cause:"+e.getCause().getMessage());
            return outParam;
        }
    }

    @Override
    public  CrmLeadMst findLeadByID(long inquiry_tran_cd) {
        return crmLeadMstDao.findLeadByID(inquiry_tran_cd);
    }

    @Override
    public  String deleteUploadedDocumentByLeadID(Long lead_id,Long doc_id) {
        try {
            docUploadDtlDao.deleteDocumentByLeadID(lead_id, doc_id);
            return "success";
        }catch(Exception e){
            System.out.println(e.getMessage());
            return "fail";
        }
    }

    @Override
    public HttpServletResponse startFileDownload(HttpServletResponse response, InputStream is, String fileName){
        try{
            response.addHeader("Content-Disposition", "attachment; filename="+fileName);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        }catch (IOException e){
            e.printStackTrace();
        }
        return  response;
    }

    @Override
    public boolean isMiddlewareRequest(String signature) {
        CRMAppDto crmAppDto = findAppByID(6);
        URLConfigDto urlConfigDto = findURLDtlByID(37);
        boolean flag = false;
        if(signature!=null){
            try{
                signature =  func_get_data_val(crmAppDto.getA(),crmAppDto.getB(),signature);
            }catch (Exception e){
                return  false;
            }
            if(signature.equals(urlConfigDto.getKey())){
                flag = true;
            }else { flag = false;}
        }else{
            flag = false;
        }
        return flag;
    }

    @Override
    public void updateCAMStatus(long serialNo, long leadID, Blob camData, Date modifiedDate, String status,String enteredBy) {
        crmcamDtlDao.updateCAMStatus(serialNo,leadID,camData,modifiedDate,status,enteredBy);
    }

    @Override
    public CRMCAMDtlDto getCAMData(long leadID, long serialNo) {
        CRMCAMDtlDto crmcamDtlDto = null;
        crmcamDtlDto = CRMCAMDtlMapper.convertToDto(crmcamDtlDao.getCRMCAMData(leadID,serialNo));
        return crmcamDtlDto;
    }

    @Override
    public  List<CrmMiscMst> findByCategory(String categoryCode){
        return crmMiscMstDao.findByCategory(categoryCode);
    }

    @Override
    public  String getGstNumberById(Long refId){
        CRMAppDto crmPII = findAppByID(3);
        HashMap  outParam= new HashMap(), inParam = new HashMap();
        String gstNumber,dbError;
        inParam.put("sql","select * from crm_lead_general_dtl where tran_cd  =?1 and sr_cd=1");
        inParam.put("refId",String.valueOf(refId));
        outParam    =   callingDBObject("sql","findGeneralDtlByID",inParam);
        if(outParam.containsKey("error")) {
            dbError = (String) outParam.get("error");
            return "error:"+dbError;
        }
        gstNumber = (String)outParam.get("gst_no");
        if(gstNumber==null||gstNumber.isEmpty()){
            return null;
        }else{
            return  func_get_data_val(crmPII.getA(),crmPII.getB(),gstNumber);
        }
    }

    @Override
    public DocUploadBlobDtl findDocByUUIDBankID(String uuid,Long bankLineID){
        return docUploadBlobDao.findDocByUUIDBankID(uuid,bankLineID);
    }

    @Override
    public void saveCorpositoryAPI(LosCorpositoryAPI losCorpositoryAPI){
        losCorpositoryAPIDao.save(losCorpositoryAPI);
    }

    @Override
    public  String getEntityNameById(Long refId){
        HashMap  outParam= new HashMap(), inParam = new HashMap();
        String entityName,dbError;
        inParam.put("sql","select * from crm_lead_general_dtl where tran_cd  =?1 and sr_cd=1");
        inParam.put("refId",String.valueOf(refId));
        outParam    =   callingDBObject("sql","findGeneralDtlByID",inParam);
        if(outParam.containsKey("error")) {
            dbError = (String) outParam.get("error");
            return "error:"+dbError;
        }
        entityName = (String)outParam.get("legal_entity_name");
        return entityName;
    }

    @Override
    public void updateEquifaxAPILog(String token_id ,String req_status,String res_status, String res_data,String errorCode, String errorDesc){
        equifaxAPILogDao.updateEquifaxAPILog(token_id,req_status,res_status,res_data,new Date(),errorCode,errorDesc);
    }

    @Override
    public List<EquifaxAPILog> findEquifaxPendingLinkRecord(){
        return equifaxAPILogDao.findEquifaxPendingLinkRecord();
    }

    @Override
    public void updateEqfxOTPLinkStatus(String token_id, String status,String remarks){
        equifaxAPILogDao.updateEqfxOTPLinkStatus(token_id, status,remarks,new Date());
    }

    @Override
    public OtpVerificationDtl findOTPDetailByTokenID(String tokenID){
        return otpVerificationDao.findOTPDetailByTokenID(tokenID);
    }

    @Override
    public List<OtpVerificationDtl> findPendingOTPLinkDetail(){
        return  otpVerificationDao.findPendingOTPLinkDetail();
    }

    @Override
    public void updateOTPLinkSentStatus(String token_id,String status,String remarks){
        otpVerificationDao.updateOTPLinkSentStatus(token_id,status, new Date(),remarks);
    }

    @Override
    public EquifaxAPILog findEquifaxDetailByTokenId(String tokenID){
        return equifaxAPILogDao.findEquifaxDetailByTokenId(tokenID);
    }

    @Override
    public String getShortURL(String transactionID,String url){
        String methodName = "getShortURL()|";
        int configCD=52,httpStatus=0;
        HashMap inParam,outParam;
        String apiURL=null,apiKey=null,apiUser=null,apiResult = null,errorFlag=null,errorMessage=null,errorRemarks=null,
                objectName= this.getClass().getSimpleName()+".java";
        try{
            URLConfigDto urlConfigDto = findURLDtlByID(configCD);
            apiURL  = urlConfigDto.getUrl();
            apiKey  = urlConfigDto.getKey();
            apiUser = urlConfigDto.getUserid();
            if(apiURL==null|| apiURL.isEmpty()) {
                errorFlag = "E";
                errorMessage = "URL not found for CODE("+configCD+")";
                errorRemarks = methodName+errorMessage;
                inParam = new HashMap();
                inParam.put("error_msg",errorMessage);
                inParam.put("remarks",errorRemarks);
                inParam.put("obj_name",objectName);
                inParam.put("error_flag",errorFlag);
                outParam    =   callingDBObject("procedure","proc_insert_error_log",inParam);
                return "0";
            }
            if(apiKey==null|| apiKey.isEmpty()) {
                errorFlag = "E";
                errorMessage = "URL key found for CODE("+configCD+")";
                errorRemarks = methodName+errorMessage;
                inParam = new HashMap();
                inParam.put("error_msg",errorMessage);
                inParam.put("remarks",errorRemarks);
                inParam.put("obj_name",objectName);
                inParam.put("error_flag",errorFlag);
                outParam    =  callingDBObject("procedure","proc_insert_error_log",inParam);
                return "0";
            }
            if(apiUser==null|| apiUser.isEmpty()) {
                errorFlag = "E";
                errorMessage = "URL userName not found for CODE("+configCD+")";
                errorRemarks = methodName+errorMessage;
                inParam = new HashMap();
                inParam.put("error_msg",errorMessage);
                inParam.put("remarks",errorRemarks);
                inParam.put("obj_name",objectName);
                inParam.put("error_flag",errorFlag);
                outParam    = callingDBObject("procedure","proc_insert_error_log",inParam);
                return "0";
            }
            URL obj = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("content-Type", "application/json");
            conn.addRequestProperty("key",apiKey);
            conn.addRequestProperty("secret",apiUser);
            conn.setDoOutput(true);

            JSONObject requestJson = new JSONObject();
            String  apiJsonReq =null;

            requestJson.put("url",url);
            apiJsonReq = requestJson.toString();

            Utility.print("URL shortner payload:\n"+apiJsonReq);
            OutputStream os = conn.getOutputStream();
            os.write(requestJson.toString().getBytes());
            os.flush();
            os.close();
            //get response body of api request
            apiResult = Utility.getURLResponse(conn);
            httpStatus = conn.getResponseCode();
            Utility.print("http status:"+httpStatus);
            Utility.print("API response:"+apiResult);
            if(httpStatus==conn.HTTP_OK){
                /**sample response**/
                /*{ "data": "http://v.ratnaafin.com/pJRk3LD",
                    "code": 200,
                    "message": "Link has been shortened"}
                * */
                String responseStatus = null,responseMessage=null,responseData=null;
                try {
                    JSONObject jsonObject = new JSONObject(apiResult);
                    //start: read key values
                    if(jsonObject.has("code")){
                        responseStatus = String.valueOf(jsonObject.getLong("code"));
                    }else{
                        responseStatus = "";
                    }
                    if(jsonObject.has("message")){
                        responseMessage = jsonObject.getString("message");
                    }
                    if(jsonObject.has("data")){
                        responseData = jsonObject.getString("data");
                    }
                    //end: read key values
                    //insert api log
                    inParam = new HashMap();
                    inParam.put("tokenID",transactionID);
                    inParam.put("requestType","SMS");
                    inParam.put("requestData",apiJsonReq);
                    inParam.put("responseData",apiResult);
                    inParam.put("transactionID","");
                    inParam.put("messageCategory","09");
                    outParam = callingDBObject("procedure","pack_healthcheck_common.proc_insert_msg_mail_api_log",inParam);
                    if(responseStatus.equals("200")){
                        return  responseData;
                    }else{
                        return "0";
                    }
                }catch (JSONException e){
                    errorFlag = "E";
                    errorMessage = "JSONException:"+e.getMessage();
                    errorRemarks = methodName+errorMessage;
                    inParam = new HashMap();
                    inParam.put("error_msg",errorMessage);
                    inParam.put("remarks",errorRemarks);
                    inParam.put("obj_name",objectName);
                    inParam.put("error_flag",errorFlag);
                    outParam    = callingDBObject("procedure","proc_insert_error_log",inParam);
                    e.printStackTrace();
                    return  "0";
                }
            }else{
                errorFlag = "U";
                errorMessage = "API HTTP Status:"+httpStatus;
                errorRemarks = methodName+errorMessage;
                inParam = new HashMap();
                inParam.put("error_msg",errorMessage);
                inParam.put("remarks",errorRemarks);
                inParam.put("obj_name",objectName);
                inParam.put("error_flag",errorFlag);
                outParam    = callingDBObject("procedure","proc_insert_error_log",inParam);
                return "0";
            }
        }catch (Exception e) {
            e.printStackTrace();
            Utility.print("API Calling failed:"+e.getMessage());
            errorFlag = "E";
            errorMessage = "Exception:"+e.getMessage();
            errorRemarks = methodName+"Error while calling API";
            inParam = new HashMap();
            inParam.put("error_msg",errorMessage);
            inParam.put("remarks",errorRemarks);
            inParam.put("obj_name",objectName);
            inParam.put("error_flag",errorFlag);
            outParam    = callingDBObject("procedure","proc_insert_error_log",inParam);
            e.printStackTrace();
            return "0";
        }
    }

    public String getDateFormattedString(String dateStr/*date string*/,String pattern/*String return pattern*/) {
        //keep note: dateStr is always in format: Sat Jan 16 2021 15:26:49 GMT+0530
        String extractPattern = "EEE MMM dd yyyy";
        SimpleDateFormat extractFormat = new SimpleDateFormat(extractPattern);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String returnDateStr=null;
        try {
            switch (pattern){
                case "MMyyyy":
                case "yyyy-MM":
                case "yyyy":
                    String splitDate = dateStr.substring(0,15);
                    System.out.println("splitDate:"+splitDate);
                    Date dt = extractFormat.parse(splitDate);
                    System.out.println("return date:"+dt);

                    returnDateStr = simpleDateFormat.format(dt);
                    System.out.println("returnDateStr: "+returnDateStr);
                    break;

            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return  returnDateStr;
    }
}
