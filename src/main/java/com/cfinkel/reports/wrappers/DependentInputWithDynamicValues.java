package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.*;
import com.cfinkel.reports.util.QueryFactory;
import com.cfinkel.reports.util.Util;
import org.springframework.dao.DataAccessException;

import java.util.*;

/**
 * $Author: charles $
 * $Revision: 8904 $
 * $Date: 2006-05-01 18:02:06 -0400 (Mon, 01 May 2006) $
 *
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 25, 2006
 * Time: 8:03:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class DependentInputWithDynamicValues extends DependentInput {
    private String description;
    private Control control;
    private Datatype datatype;
    private String defaultVal = "";
    private Query query;
    private HashMap <String,String> attributeValues;

    ControlElement controlElement = null;

    public ControlElement getControlElement() {
        return controlElement;
    }

    // abstract methods:
    public Map<String, String> getValues() throws Exception {

        Map<Input,Object> inputMap = new HashMap<Input,Object>();

        // get parent current value from the request:
        String parentValue = getParentValueFromRequest();

        if (Util.anyAreNullOrBlank(parentValue)) {
            parentValue = parentInput.getDefaultVal();
        }
        if (parentValue.equals("")) {
            // get first key in parent's values list
            Map<String,String> parentValues = parentInput.getValues();
            if (parentValues.size() > 0) {
                parentValue = parentValues.keySet().iterator().next();
            }
            else {
                // todo: is this cause for error?
            }
        }

        inputMap.put(parentInput,parentValue);

        List data = null;
        try {
            data = query.getData(inputMap);
        } catch (DataAccessException e) {
            // todo: error with SQL: notify user (developer)
        }
        Map<String,String> values = new LinkedHashMap<String,String>();
        values.putAll(this.attributeValues);

        for (Object obj : data) {
            Map map = (Map)obj;
            Iterator it = map.values().iterator();
            String key = it.next().toString();
            String value = it.hasNext() ?  it.next().toString() : key;
            values.put(key,value);
        }
        return values;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public String getDescription() {
        return description;
    }

    public Control getControl() {
        return control;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public DependentInputWithDynamicValues(DependentInputElement dependentInputElement, Report report, Input parentInput, int depth) throws BadReportSyntaxException {
        super(dependentInputElement,report, depth,parentInput);

        if (dependentInputElement.getWhen().size() > 1)  throw new BadReportSyntaxException("there must be only one 'when' element if it's value is '*'");
        // just get first when element:
        WhenElement whenElement = dependentInputElement.getWhen().get(0);
        this.description = whenElement.getDescription();
        this.datatype = whenElement.getDatatype();
        this.attributeValues = Input.getValuesFromAttribute(whenElement.getValues()  );

        defaultVal = getDefaultValue(whenElement.getDefault(),this.datatype);

        // grab query elements and control element from this crappy data structure:
        List<QueryElement> queryElements = new ArrayList<QueryElement>();
        for (Object object : whenElement.getControlAndQueryOrGeneratedQuery()) {
            if (object instanceof QueryElement) {
                queryElements.add((QueryElement)object);
            } else {
                controlElement = (ControlElement)object;
            }
        }

        if (queryElements.size() == 0) {
            throw new BadReportSyntaxException("there must be one query inside of a dependent input with dynamic values");
        }
        // only grab one query:
        query = QueryFactory.getQuery(queryElements.get(0),report);

        // set control attribute:
        if (controlElement == null) {
            control = whenElement.getControl();
        } else {
            control = getControlAttributeFromControlElement(controlElement);
        }
        this.control = whenElement.getControl();

    }

}
