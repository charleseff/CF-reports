<%--
  $Author$
  $Revision$
  $Date$

  Creation:
  User: charles
  Date: Apr 11, 2006
  Time: 8:55:41 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="/includes/include.jsp"%>
    <title>Main Admin page</title>
</head>

<body>
<jsp:include flush="true" page="/includes/logout.jsp" ></jsp:include>
<h3>Pages:</h3>
<ul>
    <li> <a href="<%=request.getContextPath()%>/admin/memory_monitor.jsp">Memory Monitor</a></li>
    <li> <a href="<%=request.getContextPath()%>/admin/view_by_report.jsp">Reports</a></li>
    <li> <a href="<%=request.getContextPath()%>/admin/datasources.jsp">Datasources</a></li>
</ul>
<h3>Actions:</h3>
<ul>
    <%--
    <li> <a href="<%=request.getContextPath()%>/admin/unloadReportsAndReloadClasses.do">Reload Custom Classes</a>
        <span style="color:red"> **This will first unload reports, as reports may be holding references to classes</span>
    </li>
    --%>
    <li> <a href="<%=request.getContextPath()%>/admin/reloadClassesAndUpdateReports.do">Reload Classes, updating existing reports</a></li>
        <li> <a href="<%=request.getContextPath()%>/admin/clearUserData.do">Clear User Data</a>
            Clears query results data from any logged in users</li>
</ul>
</body>
</html>