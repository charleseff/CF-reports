package com.cfinkel.reports.ajax.beans;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: Apr 26, 2006
 * Time: 4:04:22 PM
 */
public class InputValidationReply {

    private final boolean isValid;
    private final String message;

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }

    public InputValidationReply(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }
}
