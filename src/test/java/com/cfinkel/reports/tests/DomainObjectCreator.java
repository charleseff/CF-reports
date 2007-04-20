package com.cfinkel.reports.tests;

import org.apache.log4j.Logger;
import org.jibx.runtime.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;

import com.cfinkel.reports.generatedbeans.*;

/**
 * Created by user charles on Oct 21, 2006 at 2:20:34 AM
 */
public class DomainObjectCreator {
    private static final Logger log = Logger.getLogger(DomainObjectCreator.class);

    public static DependentInputElement createNonLeafDependentInput() {
        DependentInputElement dependentInputElement = new DependentInputElement();
        dependentInputElement.setName("just some thang");
        dependentInputElement.setDependentInput(createLeafDependentInputList());
        dependentInputElement.setWhen(createWhenList());
        return dependentInputElement;
    }

    private static List<DependentInputElement> createLeafDependentInputList() {
        DependentInputElement dependentInputElement = new DependentInputElement();
        dependentInputElement.setName("oiej");
        dependentInputElement.setWhen(createWhenList());
        List<DependentInputElement> list = new ArrayList<DependentInputElement>();
        list.add(dependentInputElement);
        return list;
    }

    public static List<WhenElement> createWhenList() {
        WhenElement when = new WhenElement();
        when.setControl(createControl());
        when.setDatatype(createDataType());
        when.setDefault("default_ stf");
        when.setDescription("just some stuff");
        when.setParentValue("my dad");
        when.setValues("4,6,dfg");

        List<WhenElement> whenList = new ArrayList<WhenElement>();
        whenList.add(when);
        return whenList;
    }

    public static Datatype createDataType() {
        return Datatype.FLOAT;
    }

    public static Control createControl() {
        return  Control.CHECKBOX;
    }

    public static GeneratedQueryElement createGeneratedQuery() {
        GeneratedQueryElement generatedQueryElement = new GeneratedQueryElement();
        generatedQueryElement.setClazz("com.not.real.Class");
        generatedQueryElement.setDatasource("someDatasource");
        generatedQueryElement.setName("just_a_random_name");
        return generatedQueryElement;
    }

    public static PreparedQueryElement createPreparedQueryElement() {
        PreparedQueryElement queryElement = new PreparedQueryElement();
        queryElement.setSql("stuff");
        queryElement.setName("stuffName");
        queryElement.setDatasource("tha datasource");
        queryElement.setInputRef(createInputRefs());
        return queryElement;
    }

    public static List<String> createInputRefs() {
        List<String> inputRefs = new ArrayList<String>();
        inputRefs.add("someThang");
        return inputRefs;
    }
}
