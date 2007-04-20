package com.cfinkel.reports.util;

import java.io.File;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 1, 2006
 * Time: 5:33:07 PM
 */
public interface RunnerForFile<T> {

    void run(File file) throws RunnerException;


}
