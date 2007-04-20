package com.cfinkel.reports.web;

import com.cfinkel.reports.util.RunnerException;
import com.cfinkel.reports.util.Util;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 */

public class AdminController implements Filter {
    private static final Logger log = Logger.getLogger(AdminController.class);

    public void init(FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        GlobalController.setHtmlResponseContent(request, response);

        if (isAdmin(request)) {
            if (Util.equalsAny(request.getServletPath(), "/admin", "/admin/")) {
                response.sendRedirect(request.getContextPath() + "/admin/index.jsp");
                return;
            }

            // actions:
            if (request.getServletPath().startsWith("/admin/") && request.getServletPath().endsWith(".do")) {
                String action = request.getServletPath().substring(7, request.getServletPath().length() - 3);
                AppData.getReportsLock().writeLock().lock();
                try {
                    processAction(action, request, response);
                } finally {
                    AppData.getReportsLock().writeLock().unlock();
                }
            } else {
                // just forward to JSP page:
                chain.doFilter(servletRequest, servletResponse);
            }
            return;

        } // if user is admin

        else {
            // forward to error page:
            Exception e = new Exception("You do not have access");
            request.setAttribute("error", e);
            RequestDispatcher rd = request.getRequestDispatcher("/error.jsp");
            rd.include(request, response);
            return;
        }

    }

    private boolean isAdmin(HttpServletRequest request) {
        // if all are true, grant access:
        String allAreAdmin = AppData.getContext().getInitParameter("allAreAdmin");
        if (allAreAdmin != null && Util.equalsAnyIgnoreCase(allAreAdmin, "true", "yes"))
            return true;

        // if adminUser set, check that for access:
        String adminUser = AppData.getContext().getInitParameter("adminUser");
        if (adminUser != null)
            return request.isUserInRole(adminUser);

        // check if user is in this role for access:
        return request.isUserInRole("CN=developers,OU=aliases/lists,DC=cfinkel,DC=com");
    }

    private void processAction(String action, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (action.equals("loadAllReports")) {
            AppData.loadAllReports(response.getOutputStream());
        } else if (action.equals("unloadReportsClearSessionData")) {
            AppData.unloadReports();
            AppData.clearSessionData(response.getOutputStream());
            //    AppData.reloadClasses(response);
            response.getOutputStream().println("Successfully unloaded reports and cleared session data");
        } else if (action.equals("clearUserData")) {

            // clear users' data
            response.getOutputStream().println("not yet implemented");
        } else if (action.equals("reloadClassesAndUpdateReports")) {
            try {
                AppData.reloadClasses();
                AppData.updateCustomClasses();
            } catch (RunnerException e) {
                log.error("Run error trying to reload classes", e);
                response.getOutputStream().println("Run error trying to reload classes: " + e.getMessage());
            }
            response.getOutputStream().println("Successfully reloaded classes and updated reports");
        } else if (action.equals("loadReport")) {
            AppData.loadReport(request.getParameter("reportPath"), response.getOutputStream());
        } else if (action.equals("unloadReport")) {
            AppData.unloadReport(request.getParameter("reportPath"), response.getOutputStream());
        } else if (action.equals("reloadReport")) {
            AppData.reloadReport(request.getParameter("reportPath"), response.getOutputStream());
            /*
        } else if (action.equals("testSerialize")) {
            testSerialize();
            */
        } else {
            response.getOutputStream().println("not yet implemented");
        }

    }

    public void destroy() {
    }
/*
    public void testSerialize() {
        for (String reportName : AppData.getReports().keySet()) {
            Report report = AppData.getReports().get(reportName);

            FileOutputStream fos = null;
            ObjectOutputStream out = null;
            try
            {
                fos = new FileOutputStream("test");
                out = new ObjectOutputStream(fos);
                out.writeObject(report);
                out.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    */

}
