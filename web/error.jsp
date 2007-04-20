<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<%--
  $Author$
  $Revision$
  $Date$
  
  Creation:
  User: charles
  Date: Jan 26, 2006
  Time: 7:32:01 PM
--%>

<!DOCTYPE html
PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<%@ include file="/includes/include.jsp" %>
<head><title>Error</title></head>
<body>
<div>
    Error
    <pre>
        <%
            if (exception != null) {
                if (exception instanceof OutOfMemoryError) {
        %>
        <span style="color:red;">Your query returned too much data to be displayed.  Please trying reducing your search criteria and try again.<br/></span>
        <%
                } else {

                    if (exception.getMessage() != null)
                        out.print(exception.getMessage() + "<br/>");
                    else if (exception.toString() != null)
                        out.print(exception.toString() + "<br/>");
                    StackTraceElement[] elements = exception.getStackTrace();
                    for (StackTraceElement element : elements) {
                        out.print(element.toString() + "<br/>");
                    }
                }
            }
        %>
    </pre>
</div>
</body>
</html>