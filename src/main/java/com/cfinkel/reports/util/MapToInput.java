package com.cfinkel.reports.util;

import com.cfinkel.reports.wrappers.Input;
import com.cfinkel.reports.wrappers.Report;
import com.cfinkel.reports.exceptions.InvalidInputException;

import java.util.Map;
import java.util.HashMap;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 15, 2006
 * Time: 6:38:54 PM
 */
public class MapToInput {

    public static Map<Input, Object> convertParameterMapToInputs(Map<String, String[]> parameterMap, Report report) throws InvalidInputException {
        Map<Input,Object> inputMap = new HashMap<Input,Object>();
        // convert map to inputs:
        for (String  key : report.getAllInputs().keySet()) {
            Input input = report.getAllInputs().get(key);
            Object valueObj = parameterMap.get(input.get().getName());

            if (valueObj == null) {
                throw new InvalidInputException("required request not present");
            }
            inputMap.put(input,valueObj);
        }
        return inputMap;
    }

}
