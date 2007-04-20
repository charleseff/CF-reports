package com.cfinkel.reports.charts;


import com.cfinkel.reports.exceptions.BadDataForChartException;
import com.cfinkel.reports.wrappers.Output;
import com.cfinkel.reports.generatedbeans.OutputElement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 18, 2006
 * Time: 5:33:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class BarChartGenerator extends ChartGenerator{
    private static final Logger log = Logger.getLogger(BarChartGenerator.class);


    /**
     *
     * @param data
     * @param session
     * @param printWriter
     * @return filename
     */
    public static String generateBarChart(List data, Output output, HttpSession session, PrintWriter printWriter) throws BadDataForChartException {
        OutputElement outputElement = output.getOutputElement();
        // convert data:
        DefaultCategoryDataset  dataset = createDefaultCategoryDataSet(data);

        boolean showLegend = shouldIShowLegend(data);

        JFreeChart chart = ChartFactory.createBarChart(
                outputElement.getName(),         // chart title
                "",               // domain axis label
                "",                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                showLegend,                     // include legend
                true,                     // tooltips?
                false                     // URLs?
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0);

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(
                0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64)
        );
        GradientPaint gp1 = new GradientPaint(
                0.0f, 0.0f, Color.green,
                0.0f, 0.0f, new Color(0, 64, 0)
        );
        GradientPaint gp2 = new GradientPaint(
                0.0f, 0.0f, Color.red,
                0.0f, 0.0f, new Color(64, 0, 0)
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        // OPTIONAL CUSTOMISATION COMPLETED.

        return writeImageMap(chart, outputElement, session, printWriter);

    }

    public static boolean shouldIShowLegend(List data) {
        if (data == null || data.size() == 0) return false;
        Map map = (Map)data.get(0);
        return map.values().size() > 2;
    }

    private static DefaultCategoryDataset createDefaultCategoryDataSet(List data) throws BadDataForChartException {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object obj : data) {
            Map result = (Map)obj;
            if (result.values().size() < 2)
                throw new BadDataForChartException("For chart, must have at least two columns of data," +
                        " with the first column having the categories and the other columns having the data ");

            boolean weAreAtTheFirstColumn = true;
            String dataName = "";
            for (Object entryObj : result.entrySet()) {
                Map.Entry entry = (Map.Entry)entryObj;
                Object datum = entry.getValue();
                String columnName = (String)entry.getKey();

                if (weAreAtTheFirstColumn) {
                    if (!(datum instanceof String))
                        throw new BadDataForChartException("First column must be a string, isstead it's " + datum.getClass().toString());
                    dataName = (String)datum;
                    weAreAtTheFirstColumn = false;
                } else {
                    if (!   ((datum instanceof BigDecimal) || (datum instanceof BigInteger)) )
                        throw new BadDataForChartException("Data must be decimal or integer.  Can't plot otherwise.");
                    // add the value to the dataset:
                    dataset.addValue((Number)datum,columnName,dataName);
                }

            }
        }

        return dataset;
    }

}