package com.cfinkel.reports.web;

import com.cfinkel.reports.ReportSessionInfo;
import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.exceptions.InvalidInputException;
import com.cfinkel.reports.generatedbeans.ReportElement;
import com.cfinkel.reports.util.Util;
import com.cfinkel.reports.wrappers.Report;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 **/

public class ReportController implements Filter {

    private static final Logger log = Logger.getLogger(ReportController.class);

    public void init (FilterConfig filterConfig) {
    }

    public void doFilter (ServletRequest servletRequest,
                          ServletResponse servletResponse,
                          FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();


        // get report path:
        String reportPath = request.getServletPath().substring(
                AppData.getReportsURL().length(),
                request.getServletPath().length()
        );

        // allow js, images, and css to go through:
        if (reportPath.startsWith("/js") || reportPath.startsWith("/css") || reportPath.startsWith("/images")
                || (reportPath.startsWith("/img")) ) {
            chain.doFilter(request,response);
            return;
        }

        //set response content:
       GlobalController.setHtmlResponseContent(request,response);

        if (reportPath.length() == 0 ||
                (Util.equalsAnyIgnoreCase(reportPath,"/","") ) ) {
            // forward to welcome page:
            RequestDispatcher rd = request.getRequestDispatcher("/welcome.jsp");
            rd.include(request,response);
            return;
        }

        // lock for reads now:
        AppData.getReportsLock().readLock().lock();
        try {
            Map<String, Report> reports = AppData.getReports();
            Report report = reports.get(reportPath);

            if (report == null) {
                try {
                    report = createReport(reportPath);
                    // add report to app scope:
                    reports.put(reportPath,report);
                } catch (JAXBException e) {
                    log.info("Error unmarshalling XML report", e);
                    if ((e.getLinkedException() != null) &&
                            (e.getLinkedException() instanceof FileNotFoundException)) {
                        RequestDispatcher rd = request.getRequestDispatcher("/report_not_found.jsp");
                        rd.include(request, response);
                    } else {
                        RequestDispatcher rd = request.getRequestDispatcher("/bad_syntax.jsp");
                        request.setAttribute("error", e);
                        rd.include(request, response);
                    }
                    return;
                } catch (BadReportSyntaxException e) {
                    log.info("Bad Report Syntax", e);
                    RequestDispatcher rd = request.getRequestDispatcher("/invalid_xml.jsp");
                    request.setAttribute("error", e);
                    rd.include(request, response);
                    return;
                }
            }

            ReportSessionInfo reportSessionInfo = getReportSessionInfo(report,session,reportPath);
            request.setAttribute(AttributeNames.reportSessionInfo,reportSessionInfo);

            // check if user has access to this report:
            if (report.hasAccess(request)) {

                try {
                    if (request.getParameter(ParameterNames.clearData) != null) {
                        reportSessionInfo.clearCachedData();
                        response.sendRedirect(request.getContextPath() + request.getServletPath());
                        return;
                    }

                    if ((request.getParameter(ParameterNames.run) != null) || report.getAllInputs().size() == 0) {
                        Map<String, List> reportData = reportSessionInfo.runReport(request.getParameterMap());
                        request.setAttribute(AttributeNames.reportData,reportData);
                    } else if (reportSessionInfo.isReportWasRunAndDataWasNotCleared()) {
                        Map<String, List> reportData = reportSessionInfo.runOnlyForNonCachedData();
                        request.setAttribute(AttributeNames.reportData,reportData);
                    }
                } catch (InvalidInputException e) {
                    log.info("invalid input exception",e); // should not happen
                    throw new ServletException(e);
                } catch (ParseException e) {
                    log.info("parse exception",e); // should not happen
                    throw new ServletException(e);
                } catch (DataAccessException e) {
                    log.info("DataAccessException", e);
                    RequestDispatcher rd = request.getRequestDispatcher("/exception.jsp");
                    request.setAttribute("exception", e);
                    rd.include(request, response);
                    return;
                }

                // include jsp:
                RequestDispatcher rd = request.getRequestDispatcher("/report.jsp");
                rd.include(request, response);

            } else {
                // no access to report:
                RequestDispatcher rd = request.getRequestDispatcher("/no_access.jsp");
                rd.include(request, response);
            }
        }
        catch (Exception e) {
            log.error("Exception running report", e);
        }
        finally {
            AppData.getReportsLock().readLock().unlock();
        }

    }

    private Report createReport(String reportPath) throws JAXBException, BadReportSyntaxException {
        JAXBContext jc = AppData.getJAXBContext();
        Unmarshaller u = jc.createUnmarshaller();

        ReportElement reportElement = (ReportElement) u.unmarshal
                (new File(AppData.getReportsDirectory() + reportPath + ".xml"));
        return new Report(reportElement, reportPath);
    }

    private ReportSessionInfo getReportSessionInfo(Report report,  HttpSession session ,String reportPath) {
        Map<String, ReportSessionInfo> reportSessionInfos;
        if (session.getAttribute(AttributeNames.reportSessionInfos) == null) {
            reportSessionInfos = new HashMap<String,ReportSessionInfo>();
            session.setAttribute(AttributeNames.reportSessionInfos,reportSessionInfos);
        } else {
            reportSessionInfos = (Map<String, ReportSessionInfo>)session.getAttribute(AttributeNames.reportSessionInfos);
        }

        ReportSessionInfo reportSessionInfo;

        if (reportSessionInfos.get(reportPath) == null) {
            reportSessionInfo = new ReportSessionInfo(report);
            reportSessionInfos.put(reportPath, reportSessionInfo);
        } else {
            reportSessionInfo = reportSessionInfos.get(reportPath);
        }

        return reportSessionInfo;
    }

    public void destroy () {
    }

}
