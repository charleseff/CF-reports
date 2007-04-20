package com.cfinkel.reports.scheduler;

import static com.cfinkel.reports.web.AppData.getReportsLock;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.apache.log4j.Logger;
import com.cfinkel.reports.web.AppData;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: Jul 5, 2006
 * Time: 12:27:55 PM
 * Quart job: report refresher
 */
public class ReportRefresher  implements Job {
    private static final Logger log = Logger.getLogger(ReportRefresher.class);


    public void execute(JobExecutionContext context) throws JobExecutionException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        getReportsLock().writeLock().lock();
        try {
            AppData.reloadLoadedReports(outputStream);
        } catch (IOException e) {
            log.error(e);
        } finally {
            getReportsLock().writeLock().unlock();
            log.info("Report refresher output: \n" + outputStream.toString());
        }
    }
}
