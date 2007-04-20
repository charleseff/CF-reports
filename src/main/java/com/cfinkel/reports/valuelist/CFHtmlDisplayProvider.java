package com.cfinkel.reports.valuelist;

import net.mlw.vlh.web.tag.support.HtmlDisplayProvider;
import net.mlw.vlh.web.tag.support.ColumnInfo;
import net.mlw.vlh.web.tag.TableInfo;
import net.mlw.vlh.web.ValueListConfigBean;
import net.mlw.vlh.ValueListInfo;
import com.cfinkel.reports.web.WebContext;
import com.cfinkel.reports.web.ParameterNames;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import java.util.Map;
import java.util.HashMap;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 17, 2006
 * Time: 3:39:43 PM
 */
public class CFHtmlDisplayProvider extends HtmlDisplayProvider {

    // add sortable:
    public String getHeaderCellPreProcess(ColumnInfo columnInfo,
                                          ValueListInfo info) {
        HttpServletRequest request = WebContext.get().getRequest();
        String sortColumn = request.getParameter(ParameterNames.sortColumn);
        String sortDirection = request.getParameter(ParameterNames.sortDirection);

        StringBuilder sb = new StringBuilder();

        sb.append("    <th class=\"sortable");
        if (sortColumn != null && sortColumn.equals(columnInfo.getTitle())) {
            // we are sorting by this column:
            sb.append(" sorted ");
            sb.append(sortDirection == null ? "" : sortDirection.equals("1") ? " order1 " : " order2 "  );
        }
        sb.append("\"");
        sb.append((columnInfo == null || columnInfo.getAttributes() == null) ? ""
                : columnInfo.getAttributes());
        sb.append(">");

        return sb.toString();
    }

    /**
     * Get the HTML that comes before the column text.
     *
     * @return The HTML that comes before the column text.
     */
    public String getHeaderRowPreProcess() {
        return "<thead><tr>\n";
    }

    /**
     * Get the HTML that comes before the column text.
     *
     * @return The HTML that comes before the column text.
     */

    public String getHeaderRowPostProcess() {
        return "</tr></thead>\n";
    }


    /**
     * Changed to not include sort image
     *
     * @param columnInfo The ColumnInfo.
     * @param tableInfo The TableInfo.
     * @param info The ValueListInfo.
     * @return The formated HTML.
     */

    public String getHeaderLabel(ColumnInfo columnInfo, TableInfo tableInfo,
                                 ValueListInfo info, Map includeParameters)
    {
        StringBuffer sb = new StringBuffer();

        ValueListConfigBean config = tableInfo.getConfig();
        Map parameters = new HashMap(includeParameters);

        if (columnInfo.getDefaultSort() != null)
        {
            // Get the current sort column and direction.
            String column = info.getSortingColumn();
            Integer direction = info.getSortingDirection();

            sb.append("<a href=\"").append(tableInfo.getUrl());

            parameters.put(ValueListInfo.PAGING_PAGE + tableInfo.getId(), "1");
            parameters.put(ValueListInfo.SORT_COLUMN + tableInfo.getId(),
                    columnInfo.getAdapterPropertyName());
            parameters
                    .put(
                            ValueListInfo.SORT_DIRECTION + tableInfo.getId(),
                            ((columnInfo.getAdapterPropertyName()
                                    .equals(column)) ? (ValueListInfo.ASCENDING
                                    .equals(direction) ? ValueListInfo.DESCENDING
                                    : ValueListInfo.ASCENDING)
                                    : columnInfo.getDefaultSort()));

            if (info.isFocusEnabled() == true)
            {
                parameters.put(ValueListInfo.DO_FOCUS + tableInfo.getId(), info
                        .isDoFocusAgain() ? "true" : "false");
                if (info.getFocusProperty() != null)
                {
                    parameters.put(ValueListInfo.FOCUS_PROPERTY
                            + tableInfo.getId(), info.getFocusProperty());
                }
                if (info.getFocusValue() != null)
                {
                    parameters.put(ValueListInfo.FOCUS_VALUE
                            + tableInfo.getId(), info.getFocusValue());
                }
            }

            sb.append(config.getLinkEncoder().encode(
                    tableInfo.getPageContext(), parameters));

            sb.append("\">").append(columnInfo.getTitle()).append("</a>");
        }
        else
        {
            sb.append(columnInfo.getTitle());
        }

        return sb.toString();
    }

}
