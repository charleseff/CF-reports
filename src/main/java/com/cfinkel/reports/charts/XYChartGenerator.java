package com.cfinkel.reports.charts;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.io.PrintWriter;

import com.cfinkel.reports.exceptions.BadDataForChartException;
import com.cfinkel.reports.wrappers.Output;
import com.cfinkel.reports.generatedbeans.OutputElement;

import javax.servlet.http.HttpSession;

/**
 * $Author:charles $
 * $Revision:10429 $
 * $Date:2006-07-26 18:00:43 -0400 (Wed, 26 Jul 2006) $
 * <p/>
 * created:
 * User: charles
 * Date: May 18, 2006
 * Time: 11:51:04 AM
 */
public class XYChartGenerator extends ChartGenerator {
    private static final Logger log = Logger.getLogger(XYChartGenerator.class);

    /**
     * Creates a chart.
     *
     *
     * @return The file name.
     */
    public static String generateChart(List data, Output output, HttpSession session, PrintWriter printWriter) throws BadDataForChartException {
        OutputElement outputElement = output.getOutputElement();
        IntervalXYDataset dataset = createDataset(data);
        JFreeChart chart = ChartFactory.createXYBarChart(
                outputElement.getName(),
                "X",
                false,
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setForegroundAlpha(0.85f);
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        return writeImageMap(chart, outputElement, session, printWriter);
    }


    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private static IntervalXYDataset createDataset(List data) throws BadDataForChartException {

        XYSeries series = new XYSeries("Data");
        for (Object obj : data) {
            Map result = (Map)obj;
            if (result.values().size() < 2)
                throw new BadDataForChartException("For chart, must have at least two columns of data");
            Iterator iterator = result.entrySet().iterator();
            Object column1Value = ((Map.Entry)iterator.next()).getValue();
            Object column2Value = ((Map.Entry)iterator.next()).getValue();
            if (!(column1Value instanceof Number) || !(column2Value instanceof Number))
                throw new BadDataForChartException("Data must be decimal or integer.  Can't plot otherwise.");
            series.add((Number)column1Value,
                    (Number)column2Value);
        }

        return new XYSeriesCollection(series);
    }

}
