package com.cfinkel.reports.util;

import javax.servlet.http.HttpServletRequest;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * User: charles
 * Date: Jan 25, 2006
 * Time: 5:40:13 PM
 */
public class Util {

    /**
     * @param strings
     * @return true if any are null or blank
     */
    public static boolean anyAreNullOrBlank(String... strings) {
        for (String string : strings) {
            if ((string == null) || (string.equals(""))) return true;
        }
        return false;
    }

    public static boolean anyAreNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) return true;
        }
        return false;
    }

    public static boolean equalsAnyIgnoreCase(String s, String... strings) {
        for (String string : strings) {
            if (s.equalsIgnoreCase(string)) return true;
        }
        return false;
    }

    public static boolean equalsAny(Object s, Object... objects) {
        for (Object object : objects) {
            if (s.equals(object)) return true;
        }
        return false;
    }

    public static boolean hasValue(String[] stringArray, String key) {
        for (String string : stringArray) {
            if (string.equals(key)) return true;
        }
        return false;
    }

    /**
     * joins v1 by v2, number times
     *
     * @param v1
     * @param v2
     * @param number
     * @return string
     */
    public static String join(String v1, String v2, int number) {
        StringBuilder string = new StringBuilder();
        string.append(v1);
        for (int i = 1; i < number; i++) {
            string.append(v2);
            string.append(v1);
        }
        return string.toString();
    }

    /**
     * @param request httprequest
     * @return user name from the request, or anonymous user, if there is no user
     */
    public static String getUserName(HttpServletRequest request) {
        if (request.getUserPrincipal() == null)
            return "Anonymous User";
        return request.getUserPrincipal().getName();

    }

}