<%@ page import="com.cfinkel.reports.ajax.methods.InputChangeHandler"%>
<%@ page import="com.cfinkel.reports.generatedbeans.Control"%>
<%@ page import="com.cfinkel.reports.generatedbeans.ControlElement"%>
<%@ page import="com.cfinkel.reports.util.Util"%>
<%@ page import="com.cfinkel.reports.web.AttributeNames"%>
<%@ page import="com.cfinkel.reports.web.ParameterNames"%>
<%@ page import="com.cfinkel.reports.wrappers.Input"%>
<%@ page import="java.util.Map" %>
<%@ page errorPage="/error.jsp" %>
<jsp:useBean id="input" scope="request"
             type="com.cfinkel.reports.wrappers.Input"/>
<jsp:useBean id="reportSessionInfo" scope="request"
             type="com.cfinkel.reports.ReportSessionInfo"/>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%--
  $Author$
  $Revision$
  $Date$

  Creation:
  User: charles
  Date: Jan 31, 2006
  Time: 5:43:39 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"  isELIgnored="false" %>

<%!
    private String[] getSelectedValues(Input input, Map<String,String> values) {
        String[]        selectedValues = new String[1];
        selectedValues[0] = input.getDefaultVal();
        if (Util.anyAreNullOrBlank(selectedValues[0]) && values != null && values.size() > 0) {
            selectedValues[0] = values.keySet().iterator().next();
        }
        return selectedValues;
    }
%>
<%
    Map<String,String> values = null;

    // in this order, get values first, then set current val of this input:
    Control control = input.getControl();

    if (control.equals(Control.DROPDOWN) || control.equals(Control.LISTBOX) || control.equals(Control.RADIO)) {
        values = input.getValues();
    }

    String[] selectedValues;
    if (request.getAttribute(AttributeNames.thisIsAjax) != null) {
        selectedValues = getSelectedValues(input,values);
    } else {
        selectedValues = reportSessionInfo.getCachedParameterMapOfRunReport().get(input.getName());
        if (selectedValues == null) {
            selectedValues = getSelectedValues(input,values);
        }
    }

    // todo: clean this up: this shouldn't go here:
    InputChangeHandler.setParameterMap(input.getName(),selectedValues[0],request);

%>
<table>
<tr>
<td>

<%
    switch(control) {

        case TEXT:
%>
<input type="text" name="${input.name}" id="${input.name}"
       value="<%=selectedValues[0]%>" ${input.onChangeHTML} />
<%
        break;

    case CHECKBOX:
%>
<input type="checkbox" name="${input.name}" id="${input.name}"
       checked="<%=Util.equalsAnyIgnoreCase(selectedValues[0],"checked","true","check","1","on","yes") ? "checked" : "" %>" >
<%
        break;

    case DROPDOWN:
%>
<select name="${input.name}" id="${input.name}" ${input.onChangeHTML} >
    <%
        for (String key : values.keySet()) {
            String displayValue = values.get(key);
    %>
    <option value="<%=key%>" <%=Util.hasValue(selectedValues,key) ? "selected=\"selected\"" : ""%> ><%=displayValue%></option>
    <%
        }
    %>
</select>
<%
        break;




    case LISTBOX:

        boolean multiple = false;
        int size = 8; // default value of 8
        ControlElement controlElement = input.getControlElement();
        if (input.getControlElement() != null) {
            multiple = controlElement.getListbox().isMulti();
            size = controlElement.getListbox().getRows() == null ? size : controlElement.getListbox().getRows();
        }
%>
<select name="${input.name}" id="${input.name}"
        <%=multiple ?  "multiple=\"multiple\"" : "" %> size="<%=size%>" ${input.onChangeHTML} >
    <%
        for (String key : values.keySet()) {
            String displayValue = values.get(key);
    %>
    <option value="<%=key%>" <%=Util.hasValue(selectedValues,key) ? "selected=\"selected\"" : ""%> ><%=displayValue%></option>
    <%
        }
    %></select><%
        break;

    case RADIO:
%>
<table>
    <%
        boolean isFirstAndDefault = false;
        boolean isHorizontal = false;
        if (selectedValues[0].equals(""))
            isFirstAndDefault = true;
        if (input.getControlElement() != null) {
            isHorizontal = input.getControlElement().getRadio().isHorizontal();
        }

        if (isHorizontal) {   %><tr><%    }
    for (String key : values.keySet()) {
        String displayValue = values.get(key);
        if (!isHorizontal) {     %><tr><%     }

%>
    <td><input type="radio" name="${input.name}" value="<%=key%>"
            <%=(selectedValues[0].equals(key) || isFirstAndDefault) ? "checked" : ""%>
    ${input.onChangeHTML}  />
        <%=displayValue%>&nbsp;&nbsp;
    </td>
    <%
        isFirstAndDefault = false;
        if (!isHorizontal) {     %></tr><%     }
}
    if (isHorizontal) {     %></tr><%  }

%>
</table>
<%      break;

    case DATE:
    case TIME:
    case DATETIME:
%>
<input type="text" name="${input.name}" id="${input.name}" value="<%=selectedValues[0]%>" />
<%-- comment out temporarily until fix for calendar is made --%>

<img src="<%=request.getContextPath()%>/images/sortabletable/calendar.gif" id="<%=input.getName() + "_trigger"%>"
     style="cursor: pointer; border: 1px solid red;" title="Date selector"
     onmouseover="this.style.background='red';" onmouseout="this.style.background=''" alt="calendar"/>
<%
        break;

    case TEXTAREA:
%>
<textarea cols="20" rows="5" id="${input.name}" name="${input.name}" value="<%=selectedValues[0]%>" ></textarea>
<%
        break;







    case HIDDEN:
%>
<input type="hidden" name="${input.name}" id="${input.name}" value="<%=selectedValues[0]%>"/>
<%
            break;
    }

%>

<jsp:include flush="true" page="input_script.jsp"/>
</td>

<c:if test="${input.hasValidation}">
    <td><span id="${input.name}_error" class="error"></span></td>
</c:if>

<c:if test="${input.hasDependents and not input.report.reportElement.verticalInputs}">
    <td>
        <table id="${input.name}_dependents_table">
            <c:forEach items="${input.dependents}" var="dependentInput">
                <c:set var="input" value="${dependentInput.value}" scope="request"/>
                <jsp:include flush="true" page="input.jsp"/>
            </c:forEach>
        </table>
    </td>
</c:if>

</tr>

</table>