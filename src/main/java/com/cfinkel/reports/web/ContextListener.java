package com.cfinkel.reports.web;

import com.cfinkel.reports.scheduler.ReportRefresher;
import com.cfinkel.reports.util.ContextSwitcher;
import com.cfinkel.reports.util.RunnerException;
import org.apache.log4j.Logger;
import org.quartz.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;

/**
 * todo: do I have to do something else if there is an error here?
 * <p/>
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * User: charles
 * Date: Jan 25, 2006
 * Time: 3:05:42 PM
 */

public class ContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(ContextListener.class);
    private Scheduler scheduler;

    /*
    Better way?
        static {
            try {
                Class.forName("com.xxx.security.jaas.UserPrincipal");
                Class.forName("com.xxx.security.jaas.GroupPrincipal");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    */
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        String hostname = setAndReturnHostName();

        try {
            AppData.jaxBContext = JAXBContext.newInstance("com.cfinkel.reports.generatedbeans");
            AppData.reportUnmarshaller = AppData.jaxBContext.createUnmarshaller();

        } catch (JAXBException e) {
            log.error("Error initializing JAXB Context", e);
            throw new RuntimeException(e);
        }

        AppData.setDataSources(new HashMap<String, DataSource>());


        ServletContext context = servletContextEvent.getServletContext();
        AppData.setServletContext(context);

        /*
        if (Util.equalsAnyIgnoreCase(hostname ,"charles-desktop","charles-laptop")) {
            AppData.setReportsDirectory(new File(context.getRealPath("") + "/../reports/xml") );
            AppData.classDirectory = new File(context.getRealPath("") + "/../reports/java");
        }
        else {
        */
        String reportLocation = context.getInitParameter("reportLocation");
        File reportsDirectory;
        if (reportLocation == null) {
            reportsDirectory = new File(System.getProperty("catalina.home") + "/../reports/xml");
            AppData.classDirectory = new File(System.getProperty("catalina.home") + "/../reports/java");
        }
        else {
            reportsDirectory = new File(reportLocation);
            AppData.classDirectory = new File(reportLocation);
        }

        AppData.setReportsDirectory(reportsDirectory);
//        }

        try {
            URL[] classURLs = new URL[1];
            classURLs[0] = AppData.classDirectory.toURI().toURL();
            AppData.setCustomClassLoader(new URLClassLoader(classURLs, Thread.currentThread().getContextClassLoader()));
            AppData.loadAllClassFiles(AppData.classDirectory, AppData.classDirectory.toURI().toURL().toString());

        } catch (MalformedURLException e) {
            log.error("bad URL - for java dir", e);
            throw new RuntimeException(e);
        } catch (RunnerException e) {
            log.error("Runner exception", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

        // set auth protocols:
        if (context.getInitParameter("authProtocol") != null) {
            ContextSwitcher.setAuthProtocol(context.getInitParameter("authProtocol"));
        }
        if (context.getInitParameter("nonAuthProtocol") != null) {
            ContextSwitcher.setNonAuthProtocol(context.getInitParameter("nonAuthProtocol"));
        }
        if (context.getInitParameter("authPort") != null) {
            ContextSwitcher.setAuthPort(context.getInitParameter("authPort"));
        }
        if (context.getInitParameter("nonAuthPort") != null) {
            ContextSwitcher.setNonAuthPort(context.getInitParameter("nonAuthPort"));
        }

        try {
            startReportRefreshScheduler(context);
        } catch (SchedulerException e) {
            log.error("Scheduler Exception", e);
            throw new RuntimeException(e);
        }

    }

    private void startReportRefreshScheduler(ServletContext context) throws SchedulerException {
        int hours = 24; // default
        if (context.getInitParameter("reportRefreshDuration") != null) {
            hours = Integer.parseInt(context.getInitParameter("reportRefreshDuration"));
        }

        // start quartz job:
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

        scheduler = schedFact.getScheduler();

        scheduler.start();

        JobDetail reportRefreshJobDetail = new JobDetail("reportRefreshJob",
                null, ReportRefresher.class);

        // todo: change later to correct values:
        Trigger trigger = TriggerUtils.makeHourlyTrigger(hours);
        trigger.setStartTime(TriggerUtils.getEvenHourDateBefore(new Date()));  // start on the previous even hour
        trigger.setName("myTrigger");

        scheduler.scheduleJob(reportRefreshJobDetail, trigger);

    }

    private String setAndReturnHostName() {
        String hostname;
        try {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("unknown host exception", e);
            throw new RuntimeException(e);
        }

        System.setProperty("cf.hostname", hostname);
        return hostname;
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //kill datasources
        AppData.setDataSources(null);

        // stop scheduler
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            log.error("error shutting down scheduler",e);
        }

        // kill sessions?
    }

}

