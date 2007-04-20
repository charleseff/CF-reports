package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.InputElement;
import com.cfinkel.reports.web.AttributeNames;
import com.cfinkel.reports.web.WebContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 *
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 25, 2006
 * Time: 5:40:14 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DependentInput extends Input {
    protected final Input parentInput;

    protected String getParentValueFromRequest() {
        WebContext webContext = WebContext.get();
        if (webContext != null) {
            HttpServletRequest request = webContext.getRequest();
            Map<String,String> map = (Map<String,String>)request.getAttribute(AttributeNames.parentValueMap);
            return map == null ? null :  map.get(this.parentInput.getName());
        }
        return null;
    }


    public DependentInput(InputElement inputElement, Report report, int depth, Input parentInput) throws BadReportSyntaxException {
        super(inputElement, report, depth);
        this.parentInput = parentInput;
    }
}
