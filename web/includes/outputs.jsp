<%@ page import="com.cfinkel.reports.charts.BarChartGenerator"%>
<%@ page import="com.cfinkel.reports.exceptions.BadDataForChartException"%>
<%@ page import="com.cfinkel.reports.web.ParameterNames"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Set"%><%@ page import="com.cfinkel.reports.web.AttributeNames"%>
<%@ page import="com.cfinkel.reports.generatedbeans.Chart"%>
<%@ page import="com.cfinkel.reports.charts.XYChartGenerator"%>
<%@ page errorPage="/error.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://valuelist.sourceforge.net/tags-valuelist" prefix="vlh"%>
<%@ taglib uri="http://www.cfinkel.com/tags-report" prefix="cf"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://bibeault.org/tld/ccc" prefix="ccc" %>
<%--
  $Author$s
  $Revision$
  $Date$

  Creation:
  User: charles
  Date: Feb 7, 2006
  Time: 1:57:43 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<jsp:useBean id="reportSessionInfo" scope="request"
             type="com.cfinkel.reports.ReportSessionInfo"/>
<ccc:constantsMap
        className="com.cfinkel.reports.generatedbeans.Chart"
        var="Chart"/>
<%
    request.setAttribute("report",reportSessionInfo.getReport());
%>
<jsp:useBean id="report" scope="request"
             type="com.cfinkel.reports.wrappers.Report"/>
<c:set var="outputs" value="${reportSessionInfo.report.outputs}"/>
<%
    Map<String,List> datas =  (Map<String,List>)request.getAttribute(AttributeNames.reportData);
%>
<c:forEach var="outputSet" items="${report.outputs}">
<c:set var="outputName" value="${outputSet.key}"/>
<c:set var="output" value="${outputSet.value}"/>
<c:set var="outputElement" value="${output.outputElement}"/>

<jsp:useBean id="output" type="com.cfinkel.reports.wrappers.Output"/>
<jsp:useBean id="outputElement" type="com.cfinkel.reports.generatedbeans.OutputElement"/>
<jsp:useBean id="outputName" type="java.lang.String"/>

<div>
<%
    if (reportSessionInfo.isHasAnAdapterForOutput(output)) {
        if (!outputElement.isValueList()) {
%>
<br/><span style="color:red;">Your search returned more than ${outputElement.maxRowsForDisplayTag} items.
                You can only export to Excel if your search returns fewer than ${outputElement.maxRowsForDisplayTag} items.</span><br/>
<%
    }
%>
<c:if test="${not outputElement.hideName}">
    <h2>${outputName}</h2>
</c:if>
<vlh:root value="list" url="?" includeParameters="" configName="cfReportLook">
    <vlh:retrieve name="${outputElement.name}" />
    <table width="600" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td align="left" nowrap="true">
                <span class="pagebanner">
                    <c:out value="${list.valueListInfo.totalNumberOfEntries}"/> items found, displaying page <c:out value="${list.valueListInfo.pagingPage}"/> of <c:out value="${list.valueListInfo.totalNumberOfPages}"/>
                </span>
                <span class="pagelinks">
                    <cf:paging pages="10" ><c:out value="${page}"/>,&nbsp;</cf:paging>
                </span>
        </tr>
        <tr>
            <td colspan="2">
                <table  class="display" >
                    <cf:row bean="row" >
                        <c:forEach var="property" items="${row.dynaClass.dynaProperties}">
                            <vlh:column title="${property.name}"   property="${property.name}"   sortable="asc" />
                        </c:forEach>
                    </cf:row>
                </table>
            </td>
        </tr>
    </table>
</vlh:root>
<%
} else {
    List data = datas.get(outputName);
    request.setAttribute("data",datas.get(outputName));
%>
<c:choose>
    <c:when  test="${empty data}">
        Process ${outputName} returned no results
    </c:when>

    <c:otherwise>
        <%
            int maxRows = output.getQuery().getJdbcTemplate().getMaxRows();
            if (maxRows > 0 && data.size() >= maxRows) {
        %>
        <br/><span style="color:red;">Your search returned more than <%=maxRows%> items.
                Displaying only the first <%=maxRows%> items.</span><br/>
        <%
            }
        %>

        <c:choose>
            <c:when test="${output.chart eq Chart.TABLE}">
                <c:if test="${not outputElement.hideName}">
                    <h2>${outputName}</h2>
                </c:if>

                <%
                    request.setAttribute(outputName + "_data",data);
                    Map rowZero = (Map)data.get(0);
                    Set<String> columnNames = rowZero.keySet();
                    request.setAttribute("columnNames",columnNames);

                    // this is necessary for the decorator:
                    request.setAttribute("output",output);

                %>

                <display:table
                        name="${outputElement.name}_data"  sort="list"  pagesize="${outputElement.rowsPerPage}"
                        id="${outputElement.name}_outputtable" htmlId="${outputElement.name}_outputtable"
                        export="true" class="display" excludedParams="${report.whiteSpaceSeparatedInputs}"
                        decorator="com.cfinkel.reports.displaytag.CFTableDecoratorImpl"
                        >
                    <%
                        for (String columnName : columnNames) {
                            int group;
                            boolean haveTotal;
                            if (output.getGroups().get(columnName) == null)
                                group = -1;
                            else group = output.getGroups().get(columnName);

                            haveTotal = output.getTotals().get(columnName) != null;

                    %>
                    <display:column
                            property="<%=columnName%>" sortable="true"
                            group="<%=group%>" total="<%=haveTotal%>"
                            />

                    <%
                        }
                    %>
                </display:table>
            </c:when>

            <c:when test="${output.chart eq Chart.BAR or output.chart eq Chart.XY }">
                <%

                    String fileName;
                    try {
                        fileName =  (output.getChart() == Chart.BAR ) ? BarChartGenerator.generateBarChart(data,output,session,new PrintWriter(out))
                                :  XYChartGenerator.generateChart(data,output,session,new PrintWriter(out));
                        String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + fileName;
                %>
                <img src="<%= graphURL %>" alt="graph" width="${outputElement.chart.width}"
                     height="${outputElement.chart.height}" usemap="#<%= fileName%>"/>
                <%
                }
                catch (BadDataForChartException e) {
                %>
                Got bad data for chart: <%=e.toString()%>
                <%  } %>
            </c:when>

            <c:when test="${output.chart eq Chart.PIE}">
                Pie chart not yet implemented
            </c:when>
        </c:choose>

    </c:otherwise>
</c:choose>
<%
    }
%>

</div>

<c:if test="${output.outputElement.text ne null}">
    <p><c:out value="${output.outputElement.text}"/></p>
</c:if>

</c:forEach>

<c:if test="${report.reportElement.hideInputs}">
    <%-- show back button: --%>
    <p>
        <form action="${report.reportURI}" method="post">
            <input type="hidden" name="<%=ParameterNames.clearData%>"/>
            <input type="submit" value="Back"/>
        </form>
    </p>
</c:if>
