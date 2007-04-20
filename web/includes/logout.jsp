<%@ page import="com.cfinkel.reports.util.Util" %>
<%--
  $Author$
  $Revision$
  $Date$
  
  Creation:
  User: charles
  Date: Mar 8, 2006
  Time: 3:43:55 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // kinda hacky:
    String thisPath;
    if (request.getServletPath().equals("/welcome.jsp")) {
        thisPath = request.getContextPath() + "/report";
    } else {
        thisPath = request.getContextPath() + request.getServletPath();
    }
%>
<table>
    <tr>
        <td><i>You are now logged in as <b><%=Util.getUserName(request)%>
        </b>.</i>&nbsp;&nbsp;</td>
    </tr>
</table>