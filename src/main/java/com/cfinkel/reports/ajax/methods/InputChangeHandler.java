package com.cfinkel.reports.ajax.methods;

import com.cfinkel.reports.ReportSessionInfo;
import com.cfinkel.reports.ajax.beans.InputMarkup;
import com.cfinkel.reports.ajax.exceptions.InvalidSessionException;
import com.cfinkel.reports.web.AttributeNames;
import com.cfinkel.reports.web.WebContext;
import com.cfinkel.reports.wrappers.Input;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import uk.ltd.getahead.dwr.WebContextFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: Feb 3, 2006
 * Time: 4:07:12 PM
 *
 * handles ajax changed inputs
 */
public class InputChangeHandler {
    private static Logger log = Logger.getLogger(InputChangeHandler.class);

    public static InputMarkup inputChanged(String reportPath, String parentInputName,
                                           String parentValue, String dependentName) throws Exception {

        HttpServletRequest request = setLocalThreadInfo();

        HttpSession session = WebContextFactory.get().getSession();
        ReportSessionInfo reportSessionInfo = getReportSessionInfo(reportPath,session);

        Input input = reportSessionInfo.getReport().getAllInputs().get(parentInputName);
        if (input == null)
            throw new Exception("No input found with name " + parentInputName );

        Input dependentInput = input.getDependents().get(dependentName);
        if (dependentInput == null)
            throw new Exception("Dependent input is null");

        setRequestAttributes(parentInputName, parentValue, request, reportSessionInfo, dependentInput);

        String description = StringUtils.trim( WebContextFactory.get().forwardToString("/includes/input_description.jsp") );
        String control = StringUtils.trim( WebContextFactory.get().forwardToString("/includes/input_control.jsp") );
        return new InputMarkup(description,control);

    }

    private static HttpServletRequest setLocalThreadInfo() {
        HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
        WebContext webContext = new WebContext();
        webContext.set(request,WebContextFactory.get().getHttpServletResponse(),WebContextFactory.get().getServletContext());
        return request;
    }

    private static void setRequestAttributes(String parentInputName, String parentValue,
                                             HttpServletRequest request, ReportSessionInfo reportSessionInfo, Input dependentInput) {

        // add reportSessionInfo to request:
        request.setAttribute(AttributeNames.reportSessionInfo, reportSessionInfo);
        request.setAttribute(AttributeNames.input, dependentInput);
        request.setAttribute(AttributeNames.thisIsAjax, true);

        setParameterMap(parentInputName, parentValue, request);
    }

    /**
     * There should be a better way for dependent inputs to get the parent value...
     * @param parentInputName
     * @param parentValue
     * @param request
     */
    public static void setParameterMap(String parentInputName, String parentValue, HttpServletRequest request) {
        Map<String,String> parentValueMap = new HashMap<String,String>();
        parentValueMap.put(parentInputName,parentValue);
        request.setAttribute(AttributeNames.parentValueMap,parentValueMap);
    }

    private static ReportSessionInfo getReportSessionInfo(String reportPath,HttpSession session) throws Exception {
        Map<String,ReportSessionInfo> reportSessionInfos = (Map<String,ReportSessionInfo>)session.getAttribute(AttributeNames.reportSessionInfos);
        if (reportSessionInfos == null) {
            String error = "No reportSessionInfos in session when trying to get reportSessionInfo with Ajax";
            Exception e = new InvalidSessionException(error);
            log.error(error,e);
            throw e;
        }
        ReportSessionInfo reportSessionInfo = reportSessionInfos.get(reportPath);
        if (reportSessionInfo == null) {
            String error = "No reportSessionInfo in session when trying to get with Ajax";
            Exception e = new InvalidSessionException(error);
            log.error(error,e);
            throw e;
        }
        return reportSessionInfo;
    }

}
