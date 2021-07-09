package com.ratnaafin.crm.common.config;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Component
public class ApplicationFilter implements Filter {
    private final Log logger = LogFactory.getLog(getClass());
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        logger.info("Logging Request  : "+req.getMethod() +" "+req.getRequestURI()+" "+new Date());
        if ("POST".equalsIgnoreCase(req.getMethod()) && "/".equalsIgnoreCase(req.getRequestURI())) {
            res.sendRedirect("/");
            logger.info("Logging Response  : "+req.getRequestURI()+" "+res.getStatus()+" "+new Date());
            return;
        }
        chain.doFilter(request, response);
        logger.info("Logging Response  : "+req.getRequestURI()+" "+res.getStatus()+" "+new Date());
    }

    @Override
    public void destroy() {

    }
}
