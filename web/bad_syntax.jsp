<%--
  $Author$
  $Revision$
  $Date$
  
  Creation:
  User: charles
  Date: Jan 26, 2006
  Time: 7:32:41 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="error" scope="request"
             type="javax.xml.bind.JAXBException"/>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<%@ include file="/includes/include.jsp"%>
<head><title>Bad Syntax</title></head>
<body>
<p>
    Bad Syntax for the XML file: <br>
    <%=error.toString()%>
</p>
<jsp:include flush="true" page="includes/logout.jsp" ></jsp:include>
</body>
</html>