package com.cfinkel.reports.web;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: Apr 17, 2006
 * Time: 11:37:18 AM
 *
 * For adding and removing sessions from the list
 */
public class SessionListener implements HttpSessionListener {
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        AppData.sessions.add(httpSessionEvent.getSession());
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        AppData.sessions.remove(httpSessionEvent.getSession());
    }
}
