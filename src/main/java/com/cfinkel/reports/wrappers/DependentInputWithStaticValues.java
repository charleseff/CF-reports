package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * $Author: charles $
 * $Revision: 8904 $
 * $Date: 2006-05-01 18:02:06 -0400 (Mon, 01 May 2006) $
 *
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 25, 2006
 * Time: 8:03:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class DependentInputWithStaticValues extends DependentInput {
    private Map<String,When> whens = new LinkedHashMap<String,When>();

    /**
     * constructor
     * @param dependentInputElement
     * @param parentInput
     * @param report
     * @throws BadReportSyntaxException
     */
    public DependentInputWithStaticValues(DependentInputElement dependentInputElement, Report report, Input parentInput, int depth) throws BadReportSyntaxException {
        super(dependentInputElement,report,depth,parentInput);

        this.whens = new LinkedHashMap<String,When>();
        List<WhenElement> whenElements = dependentInputElement.getWhen();

        for (WhenElement whenElement : whenElements) {

            String parentValue  = whenElement.getParentValue();
            if (parentValue.equals("*")) {
                throw new BadReportSyntaxException("if dependent-input has 'when' value of *, cannot have other 'when elements");
            } else {
                // has static values: make sure parent value isn't already in there:
                if (whens.get(parentValue) != null)
                    throw new BadReportSyntaxException("dependent inputs already has parent value '" + parentValue + "'");
                When when = new When(whenElement,parentInput,report);
                whens.put(parentValue,when);
            }
        }
    }

    // abstract methods:
    public Map<String, String> getValues() throws Exception {
        When when = getCurrentWhen();
        // todo: should I just return null when when is null?
        return (when == null) ? new HashMap<String,String>() :
                getCurrentWhen().getValues();
    }

    public String getDefaultVal() {
        When when = getCurrentWhen();
        if (when == null) return "";
        WhenElement whenElement = when.getWhenElement();
        String defaultVal = whenElement.getDefault();
        if (defaultVal == null) return "";
        return Input.getDefaultValue(defaultVal,whenElement.getDatatype());
    }

    public String getDescription() {
        When when = getCurrentWhen();
        return (when == null) ? "" :
                when.getWhenElement().getDescription();
    }

    public Control getControl() {
        When when = getCurrentWhen();
        return (when == null) ? Control.HIDDEN :
                when.getControl();
    }

    public ControlElement getControlElement() {
        When when = getCurrentWhen();
        return (when == null) ? null :
                when.getControlElement();
    }

    public Datatype getDatatype() {
        When when = getCurrentWhen();
        return (when == null) ? Datatype.STRING :
                when.getWhenElement().getDatatype();
    }


    /**
     *
     * @return currentWhen, or null if there is none
     */
    private When getCurrentWhen() {

        String parentValue = getParentValueFromRequest();

        if ((parentValue == null) || parentValue.equals("")) {
            parentValue = parentInput.getDefaultVal();
        }
        return whens.get(parentValue);

    }

}
