package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.*;
import com.cfinkel.reports.util.QueryFactory;
import com.cfinkel.reports.util.Util;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 *
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 25, 2006
 * Time: 7:54:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseInput extends Input implements Serializable {
    private static final Logger log = Logger.getLogger(BaseInput.class);

    String description;
    Control control;
    Datatype dataType;
    String defaultVal = "";
    private Map<String,String> values;

    // abstract methods:
    public Map<String, String> getValues() throws Exception {
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
        return dataType;
    }

    ControlElement controlElement = null;

    public ControlElement getControlElement() {
        return controlElement;
    }


    public BaseInput(BaseInputElement baseInputElement,Report report) throws BadReportSyntaxException {
        super(baseInputElement,report,1);
        description = baseInputElement.getDescription();
        dataType = baseInputElement.getDatatype();

        // separate queries and control:
        List<QueryElement> queryElements = new ArrayList<QueryElement>();
        for (Object object : baseInputElement.getControlAndQueryOrGeneratedQuery()) {
            if (object instanceof QueryElement) {
                queryElements.add((QueryElement)object);
            } else {
                controlElement = (ControlElement)object;
            }
        }

        // set control attribute:
        if (controlElement == null) {
        control = baseInputElement.getControl();
        } else {
            control = getControlAttributeFromControlElement(controlElement);
        }

        // should be runReport in this order:
        if (Util.equalsAny(control,Control.DROPDOWN,Control.LISTBOX,Control.RADIO)) {
            populateValues(baseInputElement, queryElements);
        }
        defaultVal = getDefaultValueFirstTime(baseInputElement);
    }

    /**
     * todo: check exception handling for this method
     * @param baseInputElement
     * @return val
     */
    private String getDefaultValueFirstTime(BaseInputElement baseInputElement ) {
        if (!Util.anyAreNullOrBlank(baseInputElement.getDefault())) {
            return   Input.getDefaultValue(baseInputElement.getDefault(),dataType);
        } else {
            Map<String,String> values;

            // get first key in parent's values list
            try {
                values = this.getValues();
            } catch (Exception e) {
                return "";
            }

            if (values != null && values.size() > 0) {
                return values.keySet().iterator().next();
            }
            else {
                return "";
                // todo: is this cause for error?
            }
        }
    }

    private void populateValues( BaseInputElement baseInputElement, List<QueryElement> queryElements) throws BadReportSyntaxException {
        values = new LinkedHashMap<String,String>();

        values.putAll( getValuesFromAttribute(baseInputElement.getValues())  );


        // process query elements:
        for (QueryElement queryElement : queryElements) {
            Query query = QueryFactory.getQuery(queryElement,this.getReport());

            List data = null;
            try {
                try {
                    data = query.getData(null);
                } catch (ParseException e) {
                    log.error("This should never happen",e);
                }
            } catch (DataAccessException e) {
                log.error("Data access exception running query for input values",e);
                throw new BadReportSyntaxException(e.toString());
            }
            for (Object obj : data) {
                Map map = (Map)obj;
                Iterator it = map.values().iterator();
                String key = it.next().toString();
                String value = it.hasNext() ?  it.next().toString() : key;
                values.put(key,value);
            }
        }

        if (values.size() == 0) throw new  BadReportSyntaxException("No values for input " + baseInputElement.getName() +".");
    }

}
