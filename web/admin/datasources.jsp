<%@ page import="com.cfinkel.reports.web.AppData"%>
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
<h3>Datasources:</h3>
<ul>
    <%
        for (String dataSourceName : AppData.getDataSources().keySet()) {
    %>
    <li><%=dataSourceName%></li>
    <%
        }
    %>
</ul>
</body>
</html>