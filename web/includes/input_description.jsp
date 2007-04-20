<%@ page import="com.cfinkel.reports.generatedbeans.Control"%>
<jsp:useBean id="input" scope="request"
             type="com.cfinkel.reports.wrappers.Input"/>
<%--
  $Author$
  $Revision$
  $Date$
--%>
<%
    if (!input.getControl().equals(Control.HIDDEN)) {
%><%=input.getDescription()%>: <%
    }
%>