package com.cfinkel.reports.web;

import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 26, 2006
 * Time: 9:52:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebContext {
    private static ThreadLocal<WebContext> user = new ThreadLocal<WebContext>();

    public static ThreadLocal getUser() {
        return user;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ServletContext getContext() {
        return context;
    }
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext context;
    /*
    private ServletConfig config;
    private Container container;
    */

    public void set(HttpServletRequest request, HttpServletResponse response, ServletContext context)
    {
        this.request = request;
        this.response = response;
        this.context = context;
        user.set(this);
    }

    @Nullable
    public static WebContext get() {
        return user.get();
    }
}
