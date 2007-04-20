package com.cfinkel.reports.valuelist;

import net.mlw.vlh.web.tag.DefaultRowTag;
import net.mlw.vlh.web.tag.support.DisplayProvider;
import net.mlw.vlh.web.ValueListConfigBean;

import javax.servlet.jsp.JspException;


/**
 *
 * extended to use Displaytag's even & odd classes
 */
public class CFRowTag extends DefaultRowTag {
    private DisplayProvider cfDisplayProvider;


    /**
     * @return style
     */
    public String getRowStyleClass() throws JspException {
        ValueListConfigBean config = getRootTag().getConfig();
        if (currentRowNumber == getRootTag().getValueList().getValueListInfo().getFocusedRowNumberInTable())
        {
           return config.getFocusedRowStyle();

        }
        else {
            return (currentRowNumber % config.getStyleCount() == 0) ? "odd" : "even";
        }
    }

    /**
     * Use my display provder instead:
     * @return CF display provder
     */
    public DisplayProvider getDisplayProvider() {
        if (cfDisplayProvider == null) {
            CFHtmlDisplayProvider cfHtmlDisplayProvider = new CFHtmlDisplayProvider();
            cfHtmlDisplayProvider.setUsePadding(false);
            cfDisplayProvider = cfHtmlDisplayProvider;
        }
        return cfDisplayProvider;
    }

}
