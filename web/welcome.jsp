<%@ page import="com.cfinkel.reports.VersionNumber"%>
<%--
  $Author$
  $Revision$
  $Date$
  
  Creation:
  User: charles
  Date: Jan 26, 2006
  Time: 7:32:01 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <%@ include file="/includes/include.jsp"%>
    <title>Reports</title>
</head>
<body>
<p>Welcome to CF Reports version <%=VersionNumber.get()%>.</p>
<p>
    <jsp:include flush="true" page="includes/userStatus.jsp" ></jsp:include>
</p>
</body>
</html>