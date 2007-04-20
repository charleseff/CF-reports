<%@ page import="com.cfinkel.reports.util.Util" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
<%
    if (session.getAttribute("user") == null) {
%>
<i>You are not currently logged in.  <a href="<%=request.getContextPath()%>/report">Click here</a> to log in.</i>
<%
} else {
%>
<jsp:include flush="true" page="logout.jsp" ></jsp:include>
<%    }
%>
--%>
<table>
    <tr>
        <td><i>You are now logged in as <b><%=Util.getUserName(request)%>
        </b>.</i>&nbsp;&nbsp;</td>
    </tr>
</table>
