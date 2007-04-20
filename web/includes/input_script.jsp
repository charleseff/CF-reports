<%@ page import="com.cfinkel.reports.generatedbeans.Control"%>
<%@ page import="com.cfinkel.reports.util.Util"%>
<%@ page import="com.cfinkel.reports.wrappers.Input"%>
<%@ page import="com.cfinkel.reports.wrappers.Report"%>
<%@ page import="javax.servlet.jsp.JspException"%>
<%@ page isELIgnored="false" %>
<jsp:useBean id="input" scope="request"
             type="com.cfinkel.reports.wrappers.Input"/>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<%
    Report report = input.getReport();

    if (input.isHasDependents()) {
%>
<script type="text/javascript">
    function <%=input.getName()%>_changed() {
    <%
            for (String dependentName : input.getDependents().keySet()) {
            Input dependent = input.getDependents().get(dependentName);
    %>
        InputChangeHandler.inputChanged(<%=dependent.get().getName()%>_callback,
                '<%=report.getReportPath()%>',
                '<%=input.getName()%>',
                <%-- todo: will this work in all cases?
                DWRUtil.getValue('<%=input.getName()%>'),
                --%>
                <%=input.getInputValueJavascript()%>,
                '<%=dependent.getName()%>'
                );
    <%
    }
    %>

    }

    <%

            for (String dependentName : input.getDependents().keySet()) {
            Input dependent = input.getDependents().get(dependentName);
    %>
    function <%=dependent.get().getName()%>_callback(inputMarkup) {
        removeAllChildNodes(document.getElementById("<%=dependent.getName()%>_description"));
        DWRUtil.setValue("<%=dependent.getName()%>_description",inputMarkup.description);

        removeAllChildNodes(document.getElementById("<%=dependent.getName()%>_control"));
        DWRUtil.setValue("<%=dependent.getName()%>_control",inputMarkup.control);

    <%

        if (Util.equalsAny(dependent.getControl(),Control.DATE,Control.DATETIME,Control.TIME) )
        {

            String dateTimeFormat = "";
            String showsTime;
            switch (dependent.getControl()) {
                case DATE:
                    showsTime = "false";
                    dateTimeFormat = "%m/%d/%Y";
                    break;
                case DATETIME:
                    showsTime = "true";
                    dateTimeFormat = "%m/%d/%Y %I:%M %p";
                    break;
                case TIME:
                    showsTime = "true";
                    dateTimeFormat = "%I:%M %p";
                    break;
                default:
                    throw new JspException("type should be date or time here.");
            }
    %>

        Calendar.setup(
        {
            inputField : "<%=dependent.get().getName()%>",
            ifFormat : "<%=dateTimeFormat%>",
            button : "<%=dependent.get().getName() + "_trigger"%>",
            showsTime : "<%=showsTime%>",
            timeFormat : "12"
        } );

    <%
        }
    %>

    }
    <%
        }
    %>
</script>

<%
    }

    // validation javascript:
    if (input.isHasValidation()) {
        String replyFunctionName = "check_" + input.getName() + "_reply";
%>
<script type="text/javascript">
    function check_<%=input.getName()%>() {
        InputValidator.isValid(<%=replyFunctionName%>,
                '<%=report.getReportPath()%>','<%=input.getName()%>',
                DWRUtil.getValue("<%=input.getName()%>")
                );
    }

    function <%=replyFunctionName%>(validationReply) {
        if (validationReply.valid == false) {
            DWRUtil.setValue("<%=input.getName()%>_error", validationReply.message);
            document.getElementById("<%=input.getName()%>").style.color = "red";
        }
    }

</script>
<%
    }


%>

<%
    // calendar script:
    if (Util.equalsAny(input.getControl(), Control.DATE,Control.DATETIME, Control.TIME)) {

        String dateTimeFormat = "";
        String showsTime;
        switch (input.getControl()) {
            case DATE:
                showsTime = "false";
                dateTimeFormat = "%m/%d/%Y";
                break;
            case DATETIME:
                showsTime = "true";
                dateTimeFormat = "%m/%d/%Y %I:%M %p";
                break;
            case TIME:
                showsTime = "true";
                dateTimeFormat = "%I:%M %p";
                break;
            default:
                throw new JspException("type should be date or time here.");
        }
%>

<script type="text/javascript">
    Calendar.setup(
    {
        inputField : "<%=input.get().getName()%>",
        ifFormat : "<%=dateTimeFormat%>",
        button : "<%=input.get().getName() + "_trigger"%>",
        showsTime : "<%=showsTime%>",
        timeFormat : "12"
    } );

</script>
<%
    }
%>
