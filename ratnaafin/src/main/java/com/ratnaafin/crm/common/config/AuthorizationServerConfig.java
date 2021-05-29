package com.ratnaafin.crm.common.config;

import com.ratnaafin.crm.admin.constant.TrueFalse;
import com.ratnaafin.crm.user.dao.ClientDao;
import com.ratnaafin.crm.user.dto.UserDto;
import com.ratnaafin.crm.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Collection;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig implements AuthorizationServerConfigurer{
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static String USERNAME = null;
    private static String CLIENT_ID = null;
    private static String ROLE = null;
    private static String USERID = null;
    private static String USER_FLAG = null;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserService userService;

    @Autowired
    ClientDao clientdao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .checkTokenAccess("isAuthenticated()")
                .tokenKeyAccess("permitAll()");
                //.allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .jdbc(dataSource).passwordEncoder(passwordEncoder);
                //.inMemory()
                //.withClient("ratnaafin-acute-client").secret(passwordEncoder.encode("{noop}ratnaafin-acute-client-secret"));
                //.authorizedGrantTypes("password", "authorization_code", "refresh_token")
                //.redirectUris("http://localhost:8081/login");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        endpoints.tokenStore(tokenStore());
        endpoints.authenticationManager(authenticationManager);
        //endpoints.tokenEnhancer(tokenEnhancer(tokenEnhancerChain));
    }

    /*
    @Bean
    public TokenEnhancer tokenEnhancer(TokenEnhancerChain tokenEnhancerChain) {
        return new CustomTokenEnhancer();
    }

    public class CustomTokenEnhancer implements TokenEnhancer{
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            User user = (User) authentication.getPrincipal();
            if (!USERNAME.equals("ratnaafin-acute-client")){

                //User_key_detailDto userKeyDetaildto = new User_key_detailDto();
                //userKeyDetaildto = userService.UserKeyProfileSave("ratnaafin-acute-client",USERNAME,MAC_ID,CLIENT_ID,HOST_NAME,OS_NAME);

                Map<String, Object> additionalInfo = new HashMap<>();
                additionalInfo.put("sessionId", USERNAME);
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
                return accessToken;
            }
            return null;
        }
    }*/

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @EventListener
    public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String grantType = request.getParameter("grant_type");
        if (grantType != null && grantType.equals("password")) {
            String username = (String) event.getAuthentication().getPrincipal();
            System.out.println("Login attempt failed ! User :: " + username);
            userService.updateLoginAttempt(username, TrueFalse.TRUE.getBvalue());
        }
    }

    @EventListener
    public void authenticationSuccess(AuthenticationSuccessEvent event) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String grantType = request.getParameter("grant_type");
        if (grantType != null && grantType.equals(GRANT_TYPE_PASSWORD)) {
            User user = (User) event.getAuthentication().getPrincipal();
            USERNAME = request.getParameter("username");
            CLIENT_ID = request.getParameter("clientID");
            ROLE = request.getParameter("role");
            USERID = request.getParameter("user_id");
            USER_FLAG = request.getParameter("flag");
            if ((!USERNAME.equals("ratnaafin-acute-client")) && (USERNAME != null ) && (CLIENT_ID != null)){
                UserDto checkuser = new UserDto();
                checkuser.setUser_name(USERNAME);
                checkuser.setPassword(CLIENT_ID);
                checkuser.setFlag(USER_FLAG);
                checkuser.setUser_id(USERID);
                userService.createProfile(checkuser, ROLE);
            }
            try {
                Collection<OAuth2AccessToken> tokens = tokenStore().findTokensByClientIdAndUserName(user.getUsername(), USERNAME);
                if (tokens != null) {
                    tokens.stream().map((token) -> {
                        tokenStore().removeRefreshToken(token.getRefreshToken());
                        return token;
                    }).forEach((token) -> {
                        tokenStore().removeAccessToken(token);
                    });
                }
            } catch (Exception e) {
                System.out.println("Exception at login listener :: " + e.getMessage());
                e.printStackTrace();
            }
            /*
            if ((!user.getUsername().equals("ratnaafin-acute-client")) && (!user.getUsername().isEmpty())){
                LoginDetails login = new LoginDetails();
                login.setMac_add(null);
                login.setLogin_time(new Date());
                login.setUser_name(user.getUsername());
                login.setIp_add(null);
                login.setHost_name(null);
                login.setOs_name(null);
                userService.saveLoginDetails(login);// check its every time inserted ?
                //userService.updateLoginAttempt(user.getUsername(), TrueFalse.FALSE.getBvalue());
            }*/
        }
    }
}
