package com.cfinkel.reports;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Map;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 **/
public interface QueryGenerator {

    /**
     * Object can be either String or String[].
     * @param inputs
     * @param userName
     * @return generated query
     */
    public String generateQuery(Map<String, Object>inputs, String userName) throws ParseException;

}
