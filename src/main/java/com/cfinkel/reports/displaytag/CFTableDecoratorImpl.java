package com.cfinkel.reports.displaytag;

import com.cfinkel.reports.Format;
import com.cfinkel.reports.ObjectsByColumn;
import com.cfinkel.reports.generatedbeans.DrillDownElement;
import com.cfinkel.reports.generatedbeans.InputParamElement;
import com.cfinkel.reports.web.AppData;
import com.cfinkel.reports.web.ParameterNames;
import com.cfinkel.reports.web.WebContext;
import com.cfinkel.reports.wrappers.Output;
import org.apache.log4j.Logger;
import org.displaytag.decorator.XQTableDecorator;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: Apr 10, 2006
 * Time: 3:07:15 AM
 *
 */
public class CFTableDecoratorImpl extends XQTableDecorator {
    private static final Logger log = Logger.getLogger(CFTableDecoratorImpl.class);
    Output output;
    ObjectsByColumn<DrillDownElement> drillDowns;
    String reportPrefix;
    private ObjectsByColumn<Format> formats;

    {
        HttpServletRequest request = WebContext.get().getRequest();
        output = (Output)request.getAttribute("output");
        drillDowns = output.getDrillDowns();
        formats = output.getFormats();
        reportPrefix = request.getContextPath() + AppData.getReportsURL();
    }

    /**
     *  Retrieves value
     * proper formatting for drill-downs
     * @param columnName
     * @return value
     */
    public String retrieveValue(String columnName) {

        Map currentRowObject = (Map)this.getCurrentRowObject();

        Object valueObject = currentRowObject.get(columnName);
        // should not happen:
        if (valueObject == null) return "";

        Format format = formats.get(columnName);
        String value;
        if (format != null) {
            value = format.getDecimalFormat().format(valueObject);
        } else {
            value = valueObject.toString();
        }

        DrillDownElement drillDownElement = drillDowns.get(columnName);
        if (drillDownElement != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<a href=\"");
            sb.append(getDrillDownHyperLink(drillDownElement,currentRowObject));
            sb.append("\">");
            sb.append(value);
            sb.append("</a>");
            return sb.toString();
        } else {
            return value;
        }

    }

    private String getDrillDownHyperLink(DrillDownElement drillDownElement, Map row) {
        StringBuilder link = new StringBuilder();
        link.append(reportPrefix).append("/")
                .append(drillDownElement.getReport()).append("?");

        // add inputs now:
        try {
            for (InputParamElement inputParamElement : drillDownElement.getInputParam()) {
                Object value;
                value = row.get(inputParamElement.getColumn());

                String name = inputParamElement.getName();
                String urlValue = URLEncoder.encode(value.toString(),"UTF-8");

                link.append(name);
                link.append("=").append(urlValue).append("&");
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e); //this shouldn't happen
        }

        link.append(ParameterNames.run);

        return link.toString();
    }

}
