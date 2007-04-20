<%--
  $Author$
  $Revision$
  $Date$
  
  Creation:
  User: charles
  Date: Apr 11, 2006
  Time: 7:54:56 PM
--%>
<%@ page import="java.lang.management.ManagementFactory" %>
<%@ page import="java.lang.management.MemoryPoolMXBean" %>
<%@ page import="java.util.Iterator"%>
<html>
<head>
    <title>JVM Memory Monitor</title>
</head>
<body>
<jsp:include flush="true" page="/includes/logout.jsp" ></jsp:include>


<table border="0" width="100%">
    <tr><td colspan="2" align="center"><h3>Memory MXBean</h3></td></tr>
    <tr><td width="200">Heap Memory Usage</td><td><%= ManagementFactory.getMemoryMXBean().getHeapMemoryUsage() %></td></tr>
    <tr><td>Non-Heap Memory Usage</td><td><%= ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage() %></td></tr>
</table>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td colspan="2" align="center"><h3>Memory Pool MXBeans</h3></td></tr>
<%
    Iterator<MemoryPoolMXBean> iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
    while (iter.hasNext()) {
        MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();
%>
<tr><td colspan="2">
    <table border="0" width="100%" style="border: 1px #98AAB1 solid;">
        <tr><td colspan="2" align="center"><b><%= item.getName() %></b></td></tr>
        <tr><td width="200">Type</td><td><%= item.getType() %></td></tr>
        <tr><td>Usage</td><td><%= item.getUsage() %></td></tr>
        <tr><td>Peak Usage</td><td><%= item.getPeakUsage() %></td></tr>
        <tr><td>Collection Usage</td><td><%= item.getCollectionUsage() %></td></tr>
    </table>
</td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<%
    }
%>
</body>
</html>