package com.cfinkel.reports.ajax.beans;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: Apr 26, 2006
 * Time: 6:23:59 PM
 */
public class InputMarkup {

    private final String description;
    private final String control;

    public String getDescription() {
        return description;
    }

    public String getControl() {
        return control;
    }

    public InputMarkup(String description, String control) {
        this.control = control;
        this.description = description;
    }
}
