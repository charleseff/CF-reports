package com.cfinkel.reports;

import java.text.DecimalFormat;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: Apr 1, 2006
 * Time: 1:46:00 AM
 */

/**
 * Number Format for numbers returned from output
 */
public class Format {
        public DecimalFormat getDecimalFormat() {
            return decimalFormat;
        }

        final DecimalFormat decimalFormat;
    final String decimalFormatString;

    public String getDecimalFormatString() {
        return decimalFormatString;
    }

    public Format (String s) {
        this.decimalFormatString = s;
        this.decimalFormat = new DecimalFormat(s);
    }
    }