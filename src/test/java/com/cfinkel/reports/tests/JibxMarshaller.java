package com.cfinkel.reports.tests;

import org.apache.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.jibx.runtime.*;

import java.lang.reflect.Method;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;

import com.cfinkel.reports.generatedbeans.*;

/**
 * Created by user charles on Oct 21, 2006 at 2:20:34 AM
 */
public class JibxMarshaller {
    private static final Logger log = Logger.getLogger(JibxMarshaller.class);


    public static Object marshallToFileAndUnmarshallBackToObject(Class aClass, Object object, File outputFile) throws JiBXException, FileNotFoundException {
        marshallToFile(aClass, object, outputFile);
        return unmarshallToObject(aClass, outputFile);
    }

    public static Object unmarshallToObject(Class clazz, File file) throws JiBXException, FileNotFoundException {
        IBindingFactory bfact =
                BindingDirectory.getFactory(clazz);
        IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
        return  uctx.unmarshalDocument
                (new FileInputStream(file), null);
    }

    public static void marshallToFile(Class aClass, Object object, File file) throws JiBXException, FileNotFoundException {
        IBindingFactory bfact =
                BindingDirectory.getFactory(PreparedQueryElement.class);

        IMarshallingContext mctx = bfact.createMarshallingContext();
        mctx.setIndent(4);
        mctx.marshalDocument(object, "UTF-8", null,
                new FileOutputStream(file));
    }
}
