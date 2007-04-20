package com.cfinkel.reports.valuelist;

import net.mlw.vlh.adapter.jdbc.dynabean.fix.ResultSetDynaClass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

import org.apache.commons.beanutils.DynaProperty;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 16, 2006
 * Time: 7:43:28 PM
 *
 * Created to fix the lowercase bug
 */
public class CFResultSetDynaClass extends ResultSetDynaClass {

    public CFResultSetDynaClass(ResultSet resultSet, boolean lowerCase, boolean useName) throws SQLException {
        super(resultSet, lowerCase, useName);
    }

    protected DynaProperty createDynaProperty(
            ResultSetMetaData metadata,
            int i)
            throws SQLException {

        String name = ( useName ) ? metadata.getColumnName(i) : metadata.getColumnLabel(i);

        String className = null;
        try {
            className = metadata.getColumnClassName(i);
        } catch (SQLException e) {
            // this is a patch for HsqlDb to ignore exceptions
            // thrown by its metadata implementation
        }

        // Default to Object type if no class name could be retrieved
        // from the metadata
        Class clazz = Object.class;
        if (className != null) {
            clazz = loadClass(className);
        }
        return new DynaProperty(name, clazz);

    }
}
