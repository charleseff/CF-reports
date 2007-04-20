package com.cfinkel.reports;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * $Author: charles $
 * $Revision: 8904 $
 * $Date: 2006-05-01 18:02:06 -0400 (Mon, 01 May 2006) $
 * 
 * objects attached to a column in a row, which has both a String name and an int column number
 */
public class ObjectsByColumn<T> {
    private static final Logger log = Logger.getLogger(ObjectsByColumn.class);

    private final List<T> objectsByColNum;
    private final  Map<String,T> objectsByColName;

    public boolean isEmpty() {
        return objectsByColName.size() == 0 &&
                objectsByColNum.size() == 0;
    }

    public ObjectsByColumn() {
        objectsByColNum = new ArrayList<T>();
        objectsByColName = new HashMap<String,T> ();
    }

    public void add(T obj, Object column) {
        if (column instanceof Integer) {
            addByColumnNumber(obj,(Integer)column);
        } else if (column instanceof String) {
            addByColName(obj,(String)column);
        } else {
            log.error("column should be of type Integer or String, instead it's " + column.getClass());
        }
    }

    /**
     * add by both column name and #
     * @param obj
     */
    public void add(T obj, int columnNumber, String columnName) {
        addByColumnNumber(obj,columnNumber);
        addByColName(obj,(String)columnName);
    }

    public void addByColumnNumber(T obj, int column) {
        // a bit confusing but it works:
        if (column > objectsByColNum.size() ) {
            for (int i = objectsByColNum.size(); i < column - 1; i ++) {
                objectsByColNum.add(null);
            }
            objectsByColNum.add(obj);
        } else {
            objectsByColNum.set(column-1,obj);
        }
    }

    public void addByColName(T obj, String columnName) {
        objectsByColName.put(columnName,obj);
    }

    /**
     * put prio on name over num:
     * assume the first column is column 1, NOT column 0
     * @param c
     * @param s
     * @return format
     */
    public T get(int c, String s) {
        T obj = objectsByColName.get(s);
        if (obj != null) return obj;

        if (objectsByColNum.size() < c) return null;

        obj = objectsByColNum.get(c-1);
        return obj == null ? null : obj;
    }

    public T get(String s) {
        return objectsByColName.get(s);
    }

}
