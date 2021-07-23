package com.ratnaafin.crm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.header.writers.frameoptions.StaticAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.util.Arrays;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "resource_id";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                //anonymous().disable()
                .cors()
                //.and().headers().frameOptions().sameOrigin()
                .and().headers().frameOptions().disable()
                .and().authorizeRequests()
                .antMatchers("/result").permitAll()
                .antMatchers("/data").permitAll()
                .antMatchers("/Login").permitAll()
                .antMatchers("/users/getInquiryData").permitAll()
                .antMatchers("/users/getaadharstatus").permitAll()
                .antMatchers("/users/aadharWebhookRes").permitAll()
                .antMatchers("/los/webhooks/gstinfowebhook").permitAll()
                .antMatchers("/los/webhooks/gstuploadwebhook").permitAll()
                .antMatchers("/los/webhooks/itruploadwebhook").permitAll()
                .antMatchers("/los/webhooks/statementwebhook").permitAll()
                .antMatchers("/los/webhooks/placeorderwebhook").permitAll()
                //document download
                .antMatchers("/los/lead/document/bank/data/download").permitAll()
                .antMatchers("/los/lead/document/gst/data/download").permitAll()
                .antMatchers("/los/lead/document/itr/data/download").permitAll()
                .antMatchers("/los/lead/document/kyc/data/download").permitAll()
                .antMatchers("/los/lead/document/other/data/download").permitAll()
                .antMatchers("/los/lead/external/perfios/data/download").permitAll()
                .antMatchers("/los/lead/external/corpository/data/download").permitAll()
                .antMatchers("/los/lead/external/equifaxreport/data/download").permitAll()



                .antMatchers("/los/lead/management/document/bank/data/download").permitAll()
                .antMatchers("/los/lead/management/document/gst/data/download").permitAll()
                .antMatchers("/los/lead/management/document/itr/data/download").permitAll()
                .antMatchers("/los/lead/management/document/kyc/data/download").permitAll()
                .antMatchers("/los/lead/management/document/other/data/download").permitAll()
                //end download URLs
                .antMatchers("/los/inquiry/document/download").permitAll()
                //middleware api call
                .antMatchers("/los/middleware/lead/gstupload/startupload").permitAll()
                .antMatchers("/los/middleware/lead/itrupload/startupload").permitAll()
                .antMatchers("/los/middleware/lead/statementupload/startupload").permitAll()

                .antMatchers("/los/lead/cam/download").permitAll()

                .antMatchers("/admin/**").access("hasRole('ADMIN')")
                .antMatchers("/los/**").access("hasAnyRole('EMPLOYEE')")
                .antMatchers("/reports/**").access("hasAnyRole('REPORT')")
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());

    }
}


