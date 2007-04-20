<%--
  $Author$
  $Revision$
  $Date$
  
  Creation:
  User: charles
  Date: Jan 26, 2006
  Time: 7:32:01 PM
--%>
<%@ page errorPage="/error.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = (String)request.getAttribute("error");
%>
<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>Login</title>
</head>
<body>
<%
    if (error != null) { %>
<div style="color:#FF0000;"><%=error%><br></div>
<%
    }
%>
<div>Please log in to continue.</div>
<form action="j_security_check" method="post">

    <table>
        <tr>
            <td>Name:</td>
            <td><input type="text" name="j_username"/></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type="password" name="j_password"/></td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" value="Log In"/></td>
        </tr>
    </table>
</form>
</body>
</html>