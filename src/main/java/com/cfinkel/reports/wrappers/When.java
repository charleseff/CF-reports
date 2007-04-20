package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.Control;
import com.cfinkel.reports.generatedbeans.ControlElement;
import com.cfinkel.reports.generatedbeans.QueryElement;
import com.cfinkel.reports.generatedbeans.WhenElement;
import com.cfinkel.reports.util.QueryFactory;
import com.cfinkel.reports.util.Util;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import java.text.ParseException;
import java.util.*;

/**
 * $Author: charles $
 * $Revision: 8904 $
 * $Date: 2006-05-01 18:02:06 -0400 (Mon, 01 May 2006) $
 * 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 26, 2006
 * Time: 9:36:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class When {

    private static final Logger log = Logger.getLogger(When.class);
    WhenElement whenElement;
    private ControlElement controlElement = null;
    private Control control;

    public Map<String, String> getValues() {
        return values;
    }

    Map<String,String> values;
    private Report report;
    private Input parentInput;

    public WhenElement getWhenElement() {
        return whenElement;
    }

    public When (WhenElement whenElement, Input parentInput, Report report) throws BadReportSyntaxException {
        this.whenElement = whenElement;
        this.report = report;
        this.parentInput = parentInput;

        List<Query> queries = new ArrayList<Query>();

        // grab query elements and control element from this crappy data structure:
        List<QueryElement> queryElements = new ArrayList<QueryElement>();
        for (Object object : whenElement.getControlAndQueryOrGeneratedQuery()) {
            if (object instanceof QueryElement) {
                queryElements.add((QueryElement)object);
            } else {
                controlElement = (ControlElement)object;
            }
        }

        if (queryElements.size() != 0) {
            for (QueryElement queryElement : queryElements) {
                Query query  = QueryFactory.getQuery(queryElement,report);
                queries.add(query);
            }
        }

        // set control attribute:
        if (controlElement == null) {
            control = whenElement.getControl();
        } else {
            control = Input.getControlAttributeFromControlElement(controlElement);
        }


        if (Util.equalsAny(control, Control.DROPDOWN, Control.LISTBOX, Control.RADIO)) {
            populateValues(queries);
        }

    }

    /**
     * populate values
     * @throws BadReportSyntaxException
     */
    private void populateValues(List<Query> queries) throws BadReportSyntaxException {
        values = new LinkedHashMap<String,String>();

        values.putAll( Input.getValuesFromAttribute(whenElement.getValues())  );
        


        HashMap<Input,Object> inputs = new HashMap<Input,Object>();
        inputs.put(parentInput,this.getWhenElement().getParentValue());
        for (Query query : queries) {
            List data;
            try {
                try {
                    data = query.getData(inputs);
                } catch (ParseException e) {
                    throw new BadReportSyntaxException("Parse Exception getting data for dependent input " +
                            "when parent value has value '" + this.getWhenElement().getParentValue() + "'");
                }
            } catch (DataAccessException e) {
                log.error("data access exception running query",e);
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

        if (values.size() == 0) throw new  BadReportSyntaxException("No values for input " + this.parentInput.getName());
    }

    public ControlElement getControlElement() {
            return controlElement;
    }

    public Control getControl() {
        return control;
    }
}
