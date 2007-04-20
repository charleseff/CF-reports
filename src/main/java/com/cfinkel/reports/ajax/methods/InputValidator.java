package com.cfinkel.reports.ajax.methods;

import com.cfinkel.reports.ajax.beans.InputValidationReply;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: Feb 3, 2006
 * Time: 4:07:12 PM
 *
 * handles ajax changed inputs
 */
public class InputValidator {

    public static InputValidationReply isValid(String reportPath, String inputName, String inputValue) throws IOException, ServletException {
        return new InputValidationReply(false,"This is just a test");
    }

}
