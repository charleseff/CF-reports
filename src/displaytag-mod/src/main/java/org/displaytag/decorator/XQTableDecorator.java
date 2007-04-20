package org.displaytag.decorator;

import org.displaytag.model.TableModel;
import org.displaytag.render.TableWriterTemplate;
import org.displaytag.util.TagConstants;
import org.displaytag.util.LookupUtil;
import org.displaytag.exception.ObjectLookupException;

import javax.servlet.jsp.PageContext;

/**
 * @author charles
 * @author Charles Finkel
 * @version $Revision: 971 $ ($Author: fgiust $)
 */
public abstract class XQTableDecorator extends TotalTableDecorator
{
    public abstract String retrieveValue(String columnName);

}
