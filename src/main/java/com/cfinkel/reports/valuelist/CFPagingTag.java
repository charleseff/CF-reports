package com.cfinkel.reports.valuelist;

import net.mlw.vlh.web.tag.ConfigurableTag;
import net.mlw.vlh.web.tag.ValueListSpaceTag;
import net.mlw.vlh.web.tag.DefaultPagingTag;
import net.mlw.vlh.web.util.JspUtils;
import net.mlw.vlh.ValueListInfo;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Locale;
import java.util.HashMap;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import com.cfinkel.reports.web.ParameterNames;

/**
 * Generate buttons to navigate through pages of data using i18n
 * (internationalization). The following keys are required to be define in
 * message sources.
 *
 * <p>
 * If you like to, you can add your properties file in your locale and add this
 * lines of code in your language:
 * </p>
 * Summary info:
 * <ol>
 * <code>
 *     <li>paging.text.totalRow={0} Total  </li>
 *     <li>paging.text.pageFromTotal= <b>{0}</b> of  {1} page(s) </li>
 *  </code>
 * </ol>
 * Paging info:
 * <ol>
 * <li>paging.first(off), paging.first(on)</li>
 * <li>paging.previous(off), paging.previous(on)</li>
 * <li>paging.forward(off), paging.forward(on)</li>
 * <li>paging.last(off), paging.last(on)</li>
 * <li>paging.delim</li>
 * <li>paging.text.totalRow</li>
 * <li>paging.text.pageFromTotal</li>
 * </ol>
 * Focus info:
 * <ol>
 * <li>paging.focus(on), paging.focus(off), paging.focus(disabled),
 * paging.focus(error)</li>
 * </ol>
 *
 *       pictures for paging.focus
 * @author Matthew L. Wilson, Andrej Zachar
 * @version $Revision:10429 $ $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 */
public class CFPagingTag extends ConfigurableTag {
    private int pages = 0;

    private boolean showSummary = false;

    private int page;

    private int total;

    private int qtyOnPage;

    private int numberOfPages;

    private int currentPage = 0;

    private int maxPage = 0;

    private ValueListSpaceTag _parent;

    private Map parameters;

    /**
     * Creates new GridTag
     */
    public CFPagingTag()
    {
        super();
    }


   protected ValueListInfo getValueListInfo()
   {
      return _parent.getValueList().getValueListInfo();
   }

    /**
     */
    public int doStartTag() throws JspException
    {
        _parent = (ValueListSpaceTag) JspUtils.getParent(this,
                ValueListSpaceTag.class);

        Locale local = _parent.getConfig().getLocaleResolver().resolveLocale(
                (HttpServletRequest) pageContext.getRequest());
        MessageSource message = _parent.getConfig().getMessageSource();
      ValueListInfo valueListInfo = getValueListInfo();

        // Create a map of parameters that are used to generate the links.
        parameters = new HashMap(_parent.getTableInfo().getParameters());
        // todo: set these correctly
      parameters.put(ParameterNames.sortColumn, valueListInfo.getSortingColumn());
      parameters.put(ParameterNames.sortDirection, valueListInfo.getSortingDirection());

        StringBuffer sb = new StringBuffer();

        page = _parent.getValueList().getValueListInfo().getPagingPage();
        total = _parent.getValueList().getValueListInfo()
                .getTotalNumberOfEntries();
        qtyOnPage = _parent.getValueList().getValueListInfo()
                .getPagingNumberPer();
        numberOfPages = _parent.getValueList().getValueListInfo()
                .getTotalNumberOfPages();

        currentPage = (int) (page - (pages / 2));
        if (currentPage < 1)
        {
            currentPage = 1;
        }

        maxPage = (currentPage - 1) + pages;
        if (maxPage > numberOfPages)
        {
            currentPage -= (maxPage - numberOfPages);
            maxPage = numberOfPages;
        }
        if (maxPage < 2)
        {
            maxPage = 0;
        }
        if (currentPage < 1)
        {
            currentPage = 1;
        }

        sb.append("\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\""
                + (getAttributes()==null ? "":(" "+getAttributes()))
                + ">\n");
        sb.append("  <tr>\n");

        if (showSummary == true)
        {
            sb.append(generateSumary(message, local));
            sb.append("     <td align=\"right\">\n");
            sb
                    .append("\n      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" >\n");
            sb.append("          <tr>\n");

        }

        String value = null;

        String delim = _parent.getConfig().getDisplayHelper().help(pageContext,
                message.getMessage("paging.delim", null, "", local));

        sb
                .append(generateFocusControl(_parent.getValueList()
                        .getValueListInfo(), delim, message, local));

        if (page > 1)
        {
            sb.append("   <td>[<a href=\"").append(
                    _parent.getTableInfo().getUrl());
            parameters.put(ValueListInfo.PAGING_PAGE
                    + _parent.getTableInfo().getId(), "1");
            sb.append(_parent.getConfig().getLinkEncoder().encode(pageContext,
                    parameters));
            sb.append("\">");
            sb.append(
                    value = "First")
                    .append("</a>/</td>\n");
            if (value.length() > 0 && delim.length() > 0)
            {
                sb.append("   <td>").append(delim).append("</td>\n");
            }
            sb.append("   <td><a href=\"");
            sb.append(_parent.getTableInfo().getUrl());
            parameters.put(ValueListInfo.PAGING_PAGE
                    + _parent.getTableInfo().getId(), String.valueOf(page - 1));
            sb.append(_parent.getConfig().getLinkEncoder().encode(pageContext,
                    parameters));
            sb.append("\">");
            sb.append(
                    value = "Previous").append("</a>]</td>\n");
            if (value.length() > 0 && delim.length() > 0)
            {
                sb.append("   <td>").append(delim).append("</td>\n");
            }
        }
        else
        {
            sb.append("   <td>").append("[First/").append("</td>\n");
            sb.append("   <td>").append("Previous]").append("</td>\n");
        }

        JspUtils.write(pageContext, sb.toString());
        pageContext.setAttribute("page" + _parent.getTableInfo().getId(),
                new Integer(currentPage));

        return EVAL_BODY_AGAIN;
    }

    /**
     * @throws JspException
     * @throws NoSuchMessageException
     */
    private StringBuffer generateSumary(MessageSource message, Locale local)
            throws NoSuchMessageException, JspException
    {
        StringBuffer sb = new StringBuffer();

        sb.append(" <td nowrap=\"true\" valign=\"top\" align=\"left\">");
        sb.append(_parent.getConfig().getDisplayHelper().help(pageContext,
                message.getMessage("paging.text.totalRow", new Object[]
                { new Integer(total) }, local)));
        sb.append(_parent.getConfig().getDisplayHelper().help(pageContext,
                message.getMessage("paging.text.pageFromTotal", new Object[]
                { new Integer(page), new Integer(numberOfPages) }, local)));
        sb.append(" </td>\n");
        return sb;
    }

    /**
     * @param local
     * @param message
     * @param delim
     * @throws JspException
     */
    private StringBuffer generateFocusControl(ValueListInfo info, String delim,
                                              MessageSource message, Locale local) throws JspException
    {
        String value;

        StringBuffer sb = new StringBuffer();

        if (info.isFocusEnabled() == true)
        {
            parameters.put(ValueListInfo.FOCUS_PROPERTY
                    + _parent.getTableInfo().getId(), info.getFocusProperty());

            if (info.getFocusValue() != null)
            {
                parameters.put(ValueListInfo.FOCUS_VALUE
                        + _parent.getTableInfo().getId(), info.getFocusValue());
            }
            // AAA focus error behavier
            HashMap focusParameters = new HashMap(parameters);
            if (info.getFocusStatus() != ValueListInfo.FOCUS_TOO_MANY_ITEMS)
            {
                sb.append("   <td><a href=\"").append(
                        _parent.getTableInfo().getUrl());
                focusParameters.put(ValueListInfo.DO_FOCUS
                        + _parent.getTableInfo().getId(),
                        info.isDoFocusAgain() ? "false" : "true");

                sb.append(_parent.getConfig().getLinkEncoder().encode(
                        pageContext, focusParameters));
                sb.append("\">");
                sb
                        .append(
                                value = _parent
                                        .getConfig()
                                        .getDisplayHelper()
                                        .help(
                                                pageContext,
                                                message
                                                        .getMessage(
                                                                info
                                                                        .isDoFocusAgain() ? "paging.focus(off)"
                                                                        : "paging.focus(on)",
                                                                null, local)))
                        .append("</a></td>\n");
                if (value.length() > 0 && delim.length() > 0)
                {
                    sb.append("   <td>").append(delim).append("</td>\n");
                }
            }
            else
            {
                if (info.isFocusEnabled() == true)
                {
                    sb.append("   <td>").append(
                            _parent.getConfig().getDisplayHelper().help(
                                    pageContext,
                                    message.getMessage("paging.focus(error)",
                                            null, local))).append("</td>\n");
                }
            }

        }
        else
        {
            sb.append("   <td>").append("</td>\n");
        }
        return sb;
    }

    /**
     * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
     */
    public int doAfterBody() throws JspException
    {
        if (currentPage <= maxPage)
        {
            String label = getBodyContent().getString().trim();

            StringBuffer sb = getRenderedContent(label);

            JspUtils.writePrevious(pageContext, sb.toString());

            pageContext.setAttribute("page" + _parent.getTableInfo().getId(),
                    new Integer(++currentPage));
            getBodyContent().clearBody();
            return EVAL_BODY_AGAIN;
        }
        else
        {
            return SKIP_BODY;
        }
    }

    /**
     * @param label
     */
    private StringBuffer getRenderedContent(String label) {
        StringBuffer sb = new StringBuffer();
        if (currentPage == page)
        {
            sb.append("<th>").append(label).append("</th>\n");
        }
        else
        {
            sb.append("<td><a href=\"").append(
                    _parent.getTableInfo().getUrl());
            parameters.put(ValueListInfo.PAGING_PAGE
                    + _parent.getTableInfo().getId(), String
                    .valueOf(currentPage));
            sb.append(_parent.getConfig().getLinkEncoder().encode(
                    pageContext, parameters));
            sb.append("\">").append(label).append("</a></td>\n");
        }
        return sb;
    }

    /**
     */
    public int doEndTag() throws JspException
    {
        Locale local = _parent.getConfig().getLocaleResolver().resolveLocale(
                (HttpServletRequest) pageContext.getRequest());
        MessageSource message = _parent.getConfig().getMessageSource();

        StringBuffer sb = new StringBuffer();

        String value = null;
        String delim = _parent.getConfig().getDisplayHelper().help(pageContext,
                message.getMessage("paging.delim", null, local));

        if (!(getBodyContent() != null && getBodyContent().getString() != null && getBodyContent()
                .getString().trim().length() > 0))
        {
            while(currentPage <= maxPage)
            {
                sb.append(getRenderedContent(String.valueOf(currentPage)));
                currentPage++;
            }
        }

        if (page < numberOfPages)
        {
            sb.append("   <td>[<a href=\"").append(
                    _parent.getTableInfo().getUrl());
            parameters.put(ValueListInfo.PAGING_PAGE
                    + _parent.getTableInfo().getId(), String.valueOf(page + 1));
            sb.append(_parent.getConfig().getLinkEncoder().encode(pageContext,
                    parameters));
            sb.append("\">").append(
                    value = "Next").append("</a>/</td>\n");
            if (value.length() > 0 && delim.length() > 0)
            {
                sb.append("   <td>").append(delim).append("</td>\n");
            }
            sb.append("   <td><a href=\"").append(
                    _parent.getTableInfo().getUrl());
            parameters.put(ValueListInfo.PAGING_PAGE
                    + _parent.getTableInfo().getId(), String
                    .valueOf(numberOfPages));
            sb.append(_parent.getConfig().getLinkEncoder().encode(pageContext,
                    parameters));
            sb.append("\">");
            sb.append("Last");
            sb.append("</a>]</td>\n");
        }
        else
        {
            sb.append("   <td>").append("[Next/").append("</td>\n");
            sb.append("   <td>").append("Last]").append("</td>\n");
        }

        sb.append("  </tr>\n");
        sb.append("</table>\n");
        if (showSummary == true)
        {
            sb.append("     </td>\n");
            sb.append("</table>\n");
        }
        JspUtils.write(pageContext, sb.toString());

        pages = 0;
        showSummary = false;
        resetAttributes();

        return SKIP_BODY;
    }

    /**
     * @param pages The pages to set.
     */
    public void setPages(int pages)
    {
        this.pages = pages;
    }

    /**
     * @param showSummary The showSummary to set.
     */
    public void setShowSummary(String showSummary)
    {
        this.showSummary = ("true".equalsIgnoreCase(showSummary));
    }
}
