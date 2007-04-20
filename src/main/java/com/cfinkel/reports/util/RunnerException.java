package com.cfinkel.reports.util;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 3, 2006
 * Time: 2:13:42 PM
 */
public class RunnerException extends Exception {
    public RunnerException(String s, Throwable e) {
        super(s,e);
    }


    public RunnerException(String s) {
        super(s);
    }
}
