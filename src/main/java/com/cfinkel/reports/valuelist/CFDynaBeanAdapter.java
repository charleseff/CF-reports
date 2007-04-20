package com.cfinkel.reports.valuelist;

import net.mlw.vlh.adapter.jdbc.dynabean.fix.ResultSetDynaClass;
import net.mlw.vlh.adapter.jdbc.AbstractDynaJdbcAdapter;
import net.mlw.vlh.ValueListInfo;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.log4j.Logger;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 16, 2006
 * Time: 7:40:21 PM
 * added to use CFResultSetDynaClass isntead of Default.  Meant just to fix the lowercase bug
 */
public class CFDynaBeanAdapter extends AbstractDynaJdbcAdapter {
    private static final Logger log = Logger.getLogger(CFDynaBeanAdapter.class);


    public List processResultSet(String name, ResultSet result, int numberPerPage, ValueListInfo info) throws SQLException {
        List list = new ArrayList();

        ResultSetDynaClass rsdc = new CFResultSetDynaClass(result, false, isUseName());
        BasicDynaClass bdc = new BasicDynaClass(name, BasicDynaBean.class, rsdc.getDynaProperties());

        int rowIndex = 0;
        for (Iterator rows = rsdc.iterator(); rows.hasNext() && rowIndex < numberPerPage; rowIndex++)
        {
            try
            {
                DynaBean oldRow = (DynaBean) rows.next();
                DynaBean newRow = bdc.newInstance();

                DynaProperty[] properties = oldRow.getDynaClass().getDynaProperties();
                for (int i = 0, length = properties.length; i < length; i++)
                {
                    String propertyName = properties[i].getName();
                    Object value = oldRow.get(propertyName);
                    newRow.set(propertyName, value);
                }

                list.add(newRow);
            }
            catch (Exception e)
            {
                log.error(e);
            }
        }

        return list;
    }
}
