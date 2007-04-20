package com.cfinkel.reports.valuelist;

import net.mlw.vlh.*;
import org.apache.log4j.Logger;

import com.cfinkel.reports.ReportSessionInfo;
import com.cfinkel.reports.web.WebContext;
import com.cfinkel.reports.web.AttributeNames;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 11, 2006
 * Time: 8:16:47 PM
 */
public class CFValueListHandlerImpl extends DefaultValueListHandlerImpl {



    private static final Logger log = Logger.getLogger(CFValueListHandlerImpl.class);

    /**
     *
     * @param name
     * @param info
     * @return valuelist
     */
    public ValueList getValueList(String name, ValueListInfo info)
    {
        return getValueListFromAdapter(name, info);
    }


    /**
     * Returns value list adapter from session info
     * @param name
     *            of adapter
     * @return ValueListAdapter
     */
    private ValueListAdapter getAdapter(String name)
    {
        ReportSessionInfo reportSessionInfo = (ReportSessionInfo)
                WebContext.get().getRequest().getAttribute(AttributeNames.reportSessionInfo);

        ValueListAdapter adapter = reportSessionInfo.getValueListAdapters().get(name);

        if (adapter == null)
        {
            throw new NullPointerException("Adapter could not be located: " + name);
        }
        return adapter;
    }


   /**
    * @param name
    *            of adapter
    * @param info
    *            for wanted ValueList.
    * @return ValueList from adpater specified by the name.
    */
   private ValueList getValueListFromAdapter(String name, ValueListInfo info)
   {
      if (log.isDebugEnabled())
      {
         log.debug("Start to load the ValueList from the adapter '" + name + "' with info='" + info + "'");
      }
      ValueList valueList;

      if (info == null)
      {
         info = new ValueListInfo();
         if (log.isDebugEnabled())
         {
            log.debug("Creating a new ValueListInfo for the adapter '" + name + "'.");
         }
      }
      info.getFilters().put(ValueListInfo.VALUE_LIST_NAME, name);

      valueList = getAdapter(name).getValueList(name, info);

      if (log.isDebugEnabled())
      {
         log.debug("The ValueList was loaded from the adapter '" + name + "'.");
      }

      return valueList;
   }

}
