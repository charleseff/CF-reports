package com.cfinkel.reports.charts;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.entity.StandardEntityCollection;
import org.apache.log4j.Logger;
import com.cfinkel.reports.generatedbeans.OutputElement;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 22, 2006
 * Time: 2:06:41 PM
 */
public class ChartGenerator {
    private static final Logger log = Logger.getLogger(ChartGenerator.class);

    protected static String writeImageMap(JFreeChart chart, OutputElement outputElement, HttpSession session, PrintWriter pw) {
        String filename;
        try {
            //  Write the chart image to the temporary directory
            ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
            filename = ServletUtilities.saveChartAsPNG(chart,
                    outputElement.getChart().getWidth().intValue(),
                    outputElement.getChart().getHeight().intValue(),
                    info, session);

            //  Write the image map to the PrintWriter
            ChartUtilities.writeImageMap(pw, filename, info, false);
            pw.flush();
        } catch (IOException e) {
            log.error(e);
            filename = "public_error_500x300.png";
        }
        return filename;
    }

}
