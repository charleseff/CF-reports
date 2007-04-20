package com.cfinkel.reports.web;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 **/

public class GlobalController implements Filter {
    protected WebContext webContext;

    private static final Logger log = Logger.getLogger(GlobalController.class);
    private ServletContext servletContext;

    public void init (FilterConfig filterConfig) {
        servletContext = filterConfig.getServletContext();
        webContext = new WebContext();
    }

    public void doFilter (ServletRequest servletRequest,
                          ServletResponse servletResponse,
                          FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // set threadlocal:
        webContext.set(request,response,servletContext);
        chain.doFilter(request,response);

    }

    public void destroy () {
    }

    public static void setHtmlResponseContent(  HttpServletRequest request ,
                                                HttpServletResponse response ) {
        String ctxPath = request.getContextPath();
        request.setAttribute("ctxPath",ctxPath);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setBufferSize(8192*4);
        response.setContentType("text/html");
    }
}
