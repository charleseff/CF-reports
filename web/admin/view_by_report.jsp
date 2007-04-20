<%@ page import="com.cfinkel.reports.web.AppData"%>
<%@ page import="com.cfinkel.reports.wrappers.Report"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page errorPage="/error.jsp" %>
<%--
  $Author$
  $Revision$
  $Date$
  
  Creation:
  User: charles
  Date: Apr 11, 2006
  Time: 7:54:56 PM
--%>
<html>
<head>
    <%@ include file="/includes/include.jsp"%>
    <title>Report Admin</title>
</head>
<body>
<jsp:include flush="true" page="/includes/logout.jsp" ></jsp:include>
<%
    List<String> reportPaths = AppData.getReportPaths();
    Map<String, Report> reports = AppData.getReports();

%>
<h3>Report Actions:</h3>
<ul>
    <li>
        <a href="<%=request.getContextPath()%>/admin/loadAllReports.do">Load All Reports</a>
        <span style="color:red;">** This may take a while and will block users from using reports during the process</span>
    </li>
    <li> <a href="<%=request.getContextPath()%>/admin/unloadReportsClearSessionData.do">Unload All Reports - Force refresh on all sessions</a>
        This will unload all reports and clear all session data regarding reports from all current sessions
    </li>
    <li> <a href="<%=request.getContextPath()%>/admin/unloadDeadReports.do">Unload reports that are no longer active</a></li>
</ul>

<h3>Report Paths: (in system directory <i><%=AppData.getReportsDirectory().toURL()%></i>)</h3>
<table style="border:medium">
    <%

        for (String reportPath : reportPaths) {
            Report report = reports.get(reportPath);
            boolean isLoaded = false;
            if (report != null) isLoaded = true;

    %>
    <tr>
        <td>
            <a href="<%=request.getContextPath()%>/report<%=reportPath%>"><%=reportPath%></a>:
        </td>
        <td>
            <%
                if (isLoaded) {
            %>
            <span style="color:greenyellow;">Loaded</span>&nbsp;|
            <a href="<%=request.getContextPath()%>/admin/unloadReport.do?reportPath=<%=reportPath%>">Unload Report</a>&nbsp;|
            <a href="<%=request.getContextPath()%>/admin/reloadReport.do?reportPath=<%=reportPath%>">Reload Report</a>&nbsp;|
            <a href="<%=request.getContextPath()%>/admin/viewDetails.jsp?reportPath=<%=reportPath%>">View Details</a>
            <%
            } else {
            %>
            Not Loaded&nbsp;
            <a href="<%=request.getContextPath()%>/admin/loadReport.do?reportPath=<%=reportPath%>">Load Report</a>
            <%
                }
            %>
        </td>
    </tr>
    <%
        }
    %>
</table>

<h5>
    <br/>
    <a href="<%=request.getContextPath()%>/admin/index.jsp">Back</a>
</h5>
</body>
</html>