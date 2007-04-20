package com.cfinkel.reports.tests;

import com.cfinkel.reports.generatedbeans.*;
import org.apache.log4j.Logger;
import org.jibx.runtime.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.lang.reflect.Method;

/**
 * Created by user charles on Oct 21, 2006 at 2:20:34 AM
 */
public class JibxTest {
    private static final Logger log = Logger.getLogger(JibxTest.class);

    @DataProvider(name = "dataProvider")
    public Object[][] createFileName(Method method) throws FileNotFoundException {
        return new Object[][] {
                { new File("temp/" + method.getName() + "Output.xml") },
        };
    }

    /**
     * tests that an object can be marshalled from an xml document
     * @throws JiBXException
     * @throws FileNotFoundException
     * @param outputFile
     */
    @Test(dataProvider = "dataProvider")
    public void testPreparedQueryFromObject(File outputFile) throws JiBXException, FileNotFoundException {
        PreparedQueryElement queryElement = DomainObjectCreator.createPreparedQueryElement();
        PreparedQueryElement queryElementBackAgain = (PreparedQueryElement) JibxMarshaller.marshallToFileAndUnmarshallBackToObject(PreparedQueryElement.class, queryElement, outputFile);
    }


    @Test(dataProvider = "dataProvider")
    public void testGeneratedQueryFromObject(File outputFile) throws JiBXException, FileNotFoundException {
        GeneratedQueryElement generatedQueryElement = DomainObjectCreator.createGeneratedQuery();
        GeneratedQueryElement generatedQueryElementBackAgain = (GeneratedQueryElement)
                JibxMarshaller.marshallToFileAndUnmarshallBackToObject(GeneratedQueryElement.class, generatedQueryElement, outputFile);

    }

    @Test(dataProvider = "dataProvider")
    public void testDependentInputFromObject(File outputFile) throws JiBXException, FileNotFoundException {
        DependentInputElement dependentInputElement = DomainObjectCreator.createNonLeafDependentInput();
        DependentInputElement dependentInputElementBackAgain = (DependentInputElement)
                JibxMarshaller.marshallToFileAndUnmarshallBackToObject(DependentInputElement.class, dependentInputElement, outputFile);
        
        assert dependentInputElement.getDependentInput() != null;
        assert dependentInputElement.getDependentInput().size() > 0;
        assert dependentInputElementBackAgain.getDependentInput() != null;
        assert dependentInputElementBackAgain.getDependentInput().size() > 0;
    }


}
