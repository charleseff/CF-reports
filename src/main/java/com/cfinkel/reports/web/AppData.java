package com.cfinkel.reports.web;

import com.cfinkel.reports.ReportSessionInfo;
import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.ReportElement;
import com.cfinkel.reports.util.DirectoryIterator;
import com.cfinkel.reports.util.RunnerException;
import com.cfinkel.reports.util.RunnerForFile;
import com.cfinkel.reports.wrappers.Report;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

/**
 * $Author: charles $
 * $Revision: 8904 $
 * $Date: 2006-05-01 18:02:06 -0400 (Mon, 01 May 2006) $
 * <p/>
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Apr 14, 2006
 * Time: 1:41:10 AM
 * <p/>
 * Stores all app-scoped data:
 * Lots of static data
 */
public class AppData {
    private static final Logger log = Logger.getLogger(AppData.class);

    /**
     * holds custom classes for QueryGenerator and PostQueryProcessor interfaces
     */
    private static URLClassLoader customClassLoader;
    static File classDirectory;
    static Unmarshaller reportUnmarshaller;
    static Context initContext;
    private static ServletContext context;

    public static ReentrantReadWriteLock getReportsLock() {
        return reportsLock;
    }

    static List<HttpSession> sessions = Collections.synchronizedList(new ArrayList<HttpSession>());

    private static final ReentrantReadWriteLock reportsLock = new ReentrantReadWriteLock();

    /**
     * all the datasources:
     */
    private static Map<String, DataSource> dataSources;
    //  must start with a slash:
    private static String reportsURL = "/report";
    private static File reportsDirectory;

    static {
        try {
            initContext = new InitialContext();
        } catch (NamingException e) {
            log.error("Error getting initial context", e);
            throw new RuntimeException(e);
        }
    }

    static JAXBContext getJAXBContext() {
        return jaxBContext;
    }

    static JAXBContext jaxBContext;


    /**
     * Data structure that holds all reports:
     */
    private static Map<String, Report> reports = Collections.synchronizedMap(new HashMap<String, Report>());


    static void setReportsDirectory(File reportsDirectory) {
        AppData.reportsDirectory = reportsDirectory;
    }

    static void setDataSources(Map<String, DataSource> dataSources) {
        AppData.dataSources = dataSources;
    }


    static void setCustomClassLoader(URLClassLoader customClassLoader) {
        AppData.customClassLoader = customClassLoader;

    }

    public static URLClassLoader getCustomClassLoader() {
        return customClassLoader;
    }


    public static Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    public static DataSource addAndReturnDataSource(String datasource) throws NamingException {

        String jndiPrefix = "java:comp/env/jdbc/" + datasource;

        DataSource ds = (DataSource) initContext.lookup(jndiPrefix);

        // runReport dummy query:
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("select null from dual");

        dataSources.put(datasource, ds);
        log.info("Added Datasource " + datasource);
        return ds;
    }

    public static String getReportsURL() {
        return reportsURL;
    }

    public static File getReportsDirectory() {
        return reportsDirectory;
    }

    public static Map<String, Report> getReports() {
        return reports;
    }

    static void loadAllClassFiles(File dir, final String baseDir) throws RunnerException {

        RunnerForFile runnerForFile = new RunnerForFile() {
            public void run(File file) throws RunnerException {
                try {
                    // convert to string:
                    String classString = file.toURI().toURL().toString();
                    // remove .class:
                    classString = classString.substring(0, classString.length() - 6);
                    // remove c:/etc:
                    classString = classString.substring(baseDir.length(), classString.length());
                    // convert / to .:
                    classString = classString.replaceAll("/", ".");

                    getCustomClassLoader().loadClass(classString);
                } catch (MalformedURLException e) {
                    throw new RunnerException("Malformed URL Exception", e);
                } catch (ClassNotFoundException e) {
                    throw new RunnerException("Class not found exception", e);
                }
            }
        };

        DirectoryIterator directoryIterator = new DirectoryIterator(Pattern.compile(".*\\.class"), runnerForFile);
        directoryIterator.iterateForDirectory(dir);

    }

    /**
     * updates custom classes
     */
    static void updateCustomClasses() {

        for (String reportPath : getReports().keySet()) {
            Report report = getReports().get(reportPath);
            try {
                report.updateCustomClasses();
            } catch (BadReportSyntaxException e) {
                log.error("Error updating class for report at path " + reportPath + ", unloading report from the app.");
                getReports().remove(reportPath);
            }
        }
    }

    static void reloadClasses() throws IOException, RunnerException {

        // call classloader to clear all references to old classes:
        URL[] classURLs = new URL[1];
        classURLs[0] = classDirectory.toURI().toURL();
        setCustomClassLoader(new URLClassLoader(classURLs, Thread.currentThread().getContextClassLoader()));

        loadAllClassFiles(classDirectory, classDirectory.toURI().toURL().toString());
    }


    /**
     * get all paths to reports
     *
     * @return list of paths to reports
     */
    public static List<String> getReportPaths() throws RunnerException {
        return getReportPaths(reportsDirectory);
    }

    private static List<String> getReportPaths(File dir) throws RunnerException {

        final List<String> reportPaths = new ArrayList<String>();

        RunnerForFile runnerForFile = new RunnerForFile() {
            public void run(File file) throws RunnerException {

                try {
                    // convert to string:
                    String xmlString = file.toURI().toURL().toString();
                    // remove .xml:
                    xmlString = xmlString.substring(0, xmlString.length() - 4);

                    // remove c:/etc:
                    xmlString = xmlString.substring(
                            reportsDirectory.toURI().toURL().toString().length() - 1,
                            xmlString.length());

                    reportPaths.add(xmlString);
                } catch (MalformedURLException e) {
                    throw new RunnerException("malformedURLException", e);
                }
            }
        };

        DirectoryIterator directoryIterator = new DirectoryIterator(Pattern.compile(".*\\.xml"), runnerForFile);
        directoryIterator.iterateForDirectory(dir);


        return reportPaths;
    }

    static void unloadReports() {
        getReports().clear();
    }

    public static void loadAllReports(OutputStream outputStream) throws IOException {

        List<String> reportPaths;
        try {
            reportPaths = getReportPaths();
            for (String reportPath : reportPaths) {
                loadReport(reportPath, outputStream);
            }
        } catch (RunnerException e) {
            log.error(e);
            outputStream.write(("Exception getting all report paths: " + e.getMessage() + "\n").getBytes());
        }
    }


    public static void loadReport(String reportPath, OutputStream outputStream) throws IOException {
        // first check ti see that this report isn't already here:
        if (reports.get(reportPath) != null) {
            String message = "Report at path " + reportPath + " is already loaded.\n";
            outputStream.write(message.getBytes());
            return;
        }

        try {
            ReportElement reportElement = (ReportElement) reportUnmarshaller.unmarshal
                    (new File(AppData.getReportsDirectory() + reportPath + ".xml"));
            Report report = new Report(reportElement, reportPath);
            reports.put(reportPath, report);
            String message = "Successfully loaded report at path " + reportPath + "\n\r";
            outputStream.write(message.getBytes());

        } catch (BadReportSyntaxException e) {
            String message = "Failed to load report at path '" + reportPath + "' with exception: " + e + "\n";
            log.info(message, e);
            outputStream.write(message.getBytes());
        } catch (JAXBException e) {
            String message = "Failed to load report at path '" + reportPath + "' with JAXB exception: " + e + "\n";
            log.info(message);
            outputStream.write(message.getBytes());
        }

    }

    public static void unloadReport(String reportPath, OutputStream outputStream) throws IOException {
        Report report = getReports().get(reportPath);
        if (report == null) {
            outputStream.write(("No report at '" + reportPath + "' is loaded.\n").getBytes());
        }

        // remove session data that attaches to this report:
        for (HttpSession session : sessions) {
            if (session.getAttribute(AttributeNames.reportSessionInfos) != null) {
                Map<String, ReportSessionInfo> reportSessionInfos =
                        (Map<String, ReportSessionInfo>) session.getAttribute(AttributeNames.reportSessionInfos);
                if (reportSessionInfos.get(reportPath) != null) {
                    reportSessionInfos.remove(reportPath);
                    outputStream.write(("Removed session data for this report for session id #"
                            + session.getId() + "\n").getBytes());
                }

            }
        }

        // remove report from app scope:
        getReports().remove(reportPath);

        outputStream.write(("Successfully unloaded report at " + reportPath + "\n").getBytes());

    }

    public static void clearSessionData(OutputStream outputStream) throws IOException {
        for (HttpSession session : sessions) {
            if (session.getAttribute(AttributeNames.reportSessionInfos) != null) {
                session.removeAttribute(AttributeNames.reportSessionInfos);
                outputStream.write(("Removed session info for session id #" + session.getId() + "\n").getBytes());
            }
        }
    }

    public static void reloadReport(String reportPath, OutputStream outputStream) throws IOException {
        unloadReport(reportPath, outputStream);
        loadReport(reportPath, outputStream);
    }

    public static void reloadLoadedReports(ByteArrayOutputStream outputStream) throws IOException {

        // clone report names:
        String[] reportPaths = reports.keySet().toArray(new String[0]).clone();

        for (String reportPath : reportPaths) {
            reloadReport(reportPath, outputStream);
        }

    }

    public static void setServletContext(ServletContext c) {
        context = c;
    }

    public static ServletContext getContext() {
        return context;
    }
}