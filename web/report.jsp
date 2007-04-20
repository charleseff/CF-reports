<%@ page import="com.cfinkel.reports.web.ParameterNames"%>
<%--
  Creation date: Jan 25, 2006
  Time: 11:50:51 AM
  $Author:charles $
  $Revision:10410 $
  $Date:2006-07-25 17:45:30 -0400 (Tue, 25 Jul 2006) $
  $Id:report.jsp 10410 2006-07-25 17:45:30 -0400 (Tue, 25 Jul 2006) charles $
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ page errorPage="error.jsp" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<jsp:useBean id="reportSessionInfo" scope="request"
             type="com.cfinkel.reports.ReportSessionInfo"/>
<%    request.setAttribute("report",reportSessionInfo.getReport()); %>
<jsp:useBean id="report" scope="request"
             type="com.cfinkel.reports.wrappers.Report" />

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <%@ include file="/includes/include.jsp"%>
    <title>${report.reportElement.title}</title>
    <script type="text/javascript" src="${ctxPath}/js/sortabletable/sortabletable.js"></script>
    <style type="text/css">@import url(${ctxPath}/js/jscalendar-1.0/calendar-win2k-1.css);</style>
    <script type="text/javascript" src="${ctxPath}/js/jscalendar-1.0/calendar.js"></script>
    <script type="text/javascript" src="${ctxPath}/js/jscalendar-1.0/lang/calendar-en.js"></script>
    <script type="text/javascript" src="${ctxPath}/js/jscalendar-1.0/calendar-setup.js"></script>
    <%-- My AJAX objects: --%>
    <script type='text/javascript' src='${ctxPath}/dwr/interface/InputChangeHandler.js'></script>
    <script type='text/javascript' src='${ctxPath}/dwr/interface/InputValidator.js'></script>

    <script type='text/javascript' src='${ctxPath}/dwr/engine.js'></script>
    <script type='text/javascript' src='${ctxPath}/dwr/util.js'></script>
    <script type='text/javascript' src='${ctxPath}/js/cfReportUtil.js'></script>
    <link rel='stylesheet' type='text/css' href='${ctxPath}/css/sortabletable.css' />
    <script type="text/javascript" >
        function init() {
            DWRUtil.useLoadingMessage();
            // todo: add error handler
            // DWREngine.setErrorHandler(errorHandler);
        }
    </script>
</head>

<body onload="init();">

<c:if test="${not report.reportElement.hideLogin}">
    <jsp:include flush="true" page="includes/logout.jsp" ></jsp:include>
    <div><br/></div>
</c:if>

<%-- inputs form: --%>
<c:if test="${not report.reportElement.hideInputs or not reportSessionInfo.reportWasRunAndDataWasNotCleared}" >
    <form action="${report.reportURI}" method="post" id="form">
        <table id="inputs">
            <c:forEach items="${report.baseInputs}" var="baseInput">
                <c:set var="input" value="${baseInput.value}" scope="request"/>
                <jsp:include flush="true" page="includes/input.jsp" ></jsp:include>
            </c:forEach>
        </table>

        <table>
            <tr>
                <c:if test="${report.shouldShowSubmitButton}">
                    <td><input type="submit" name="<%=ParameterNames.run%>"
                               id="<%=ParameterNames.run%>" value="Search"/></td>
                </c:if>
            </tr>
        </table>
    </form>
</c:if>

<%-- results--%>
<c:if test="${reportSessionInfo.reportWasRunAndDataWasNotCleared}">
    <jsp:include flush="true" page="/includes/outputs.jsp"/>

    <c:if test="${not report.reportElement.hideLinkToReport}">
        <p>
            <a href="${report.reportURI}${reportSessionInfo.encodedParameters}">Link to this report.</a>
        </p>
    </c:if>

</c:if>

</body>
</html>