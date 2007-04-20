package com.cfinkel.reports.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * $Author$
 * $Revision$
 * $Date$
 * <p/>
 * created:
 * User: charles
 * Date: Apr 18, 2006
 * Time: 1:35:45 PM
 *
 * converts context from ssl to non-ssl
 */
public class ContextSwitcher {

    static String nonAuthProtocol = "http:";
    static String authProtocol = "https:";
    static String nonAuthPort = "8080";
    static String authPort = "8443";

    public static void setNonAuthProtocol(String nonAuthProtocol) {
        ContextSwitcher.nonAuthProtocol = nonAuthProtocol;
    }

    public static void setAuthProtocol(String authProtocol) {
        ContextSwitcher.authProtocol = authProtocol;
    }

    public static void setNonAuthPort(String nonAuthPort) {
        ContextSwitcher.nonAuthPort = nonAuthPort;
    }

    public static void setAuthPort(String authPort) {
        ContextSwitcher.authPort = authPort;
    }


    public static String getSSLContextPath(HttpServletRequest request) {
        String path = getPath(request);
        path = StringUtils.replace(path,nonAuthProtocol, authProtocol,1);
        path = StringUtils.replace(path, nonAuthPort, authPort,1);
        return path;
    }

    public static String getNonSSLContextPath(HttpServletRequest request) {
        String path = getPath(request);
        path = StringUtils.replace(path,authProtocol,nonAuthProtocol,1);
        path = StringUtils.replace(path,authPort,nonAuthPort,1);
        return path;
    }

    private static String getPath(HttpServletRequest request) {
        return request.getRequestURL().substring(0,request.getRequestURL().length() - request.getServletPath().length());
    }

}
