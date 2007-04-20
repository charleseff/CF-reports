<jsp:useBean id="input" scope="request"
             type="com.cfinkel.reports.wrappers.Input"/>
<%--
  $Author$
  $Revision$
  $Date$

  Creation:
  User: charles
  Date: Jan 31, 2006
  Time: 5:43:39 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<tr id="${input.name}_tr">
    <td id="${input.name}_description" align="right">
        <jsp:include flush="true" page="input_description.jsp"/>
    </td>
    <td id="${input.name}_control">
        <jsp:include flush="true" page="input_control.jsp"/>
    </td>
</tr>

<c:if test="${input.report.reportElement.verticalInputs}">
    <c:forEach items="${input.dependents}" var="dependentInput">
        <c:set var="input" value="${dependentInput.value}" scope="request"/>
        <jsp:include flush="true" page="input.jsp"/>
    </c:forEach>
</c:if>
