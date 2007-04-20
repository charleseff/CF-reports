package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.Format;
import com.cfinkel.reports.ObjectsByColumn;
import com.cfinkel.reports.PostQueryProcessor;
import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.*;
import com.cfinkel.reports.web.AppData;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * $Author: charles $
 * $Revision: 8904 $
 * $Date: 2006-05-01 18:02:06 -0400 (Mon, 01 May 2006) $
 *
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 25, 2006
 * Time: 5:40:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Output {
    private ObjectsByColumn<Integer> groups;

    public ObjectsByColumn<Boolean> getTotals() {
        return totals;
    }

    private ObjectsByColumn<Boolean> totals;

    public ObjectsByColumn<Integer> getGroups() {
        return groups;
    }

    public ObjectsByColumn<DrillDownElement> getDrillDowns() {
        return drillDowns;
    }

    private ObjectsByColumn<Format> formats;
    private ObjectsByColumn<DrillDownElement> drillDowns;

    private static final Logger log = Logger.getLogger(Output.class);
    public OutputElement getOutputElement() {
        return outputElement;
    }

    private OutputElement outputElement;

    public Query getQuery() {
        return query;
    }

    private Query query;
    private PostQueryProcessor postQueryProcessor;

    public Chart getChart() {
        return chart;
    }

    public ObjectsByColumn<Format> getFormats() {
        return formats;
    }

    private Chart chart;

    public Output(OutputElement outputElement, Report report) throws BadReportSyntaxException {
        this.outputElement = outputElement;
        this.formats = new ObjectsByColumn<Format>();

        if (outputElement.getQueryRef() != null) {
            query = report.getQueries().get(outputElement.getQueryRef());
            if (query == null)
                throw new BadReportSyntaxException("No query " + outputElement.getQueryRef() + " found.");

        } else if (outputElement.getQuery() != null) {
            PreparedQueryElement queryElement = outputElement.getQuery();
            if (report.getQueries().get(queryElement.getName()) != null)
                throw new BadReportSyntaxException("Query named " + queryElement.getName() + " already present in report");
            query = new PreparedQuery(queryElement,report,outputElement);
            // add query to report:
            report.getQueries().put(queryElement.getName(),query);

        } else {
            GeneratedQueryElement queryElement = outputElement.getGeneratedQuery();
            if (report.getQueries().get(queryElement.getName()) != null)
                throw new BadReportSyntaxException("Query named " + queryElement.getName() + " already present in report");

            query = new GeneratedQuery(queryElement,outputElement);
            // add query to report:
            report.getQueries().put(queryElement.getName(),query);
        }

        updatePostProcessClass();

        chart =  outputElement.getChart() == null ? Chart.TABLE : outputElement.getChart().getType();

        int i = 1;

        // formats:
        try {
            for (FormatElement formatElement : outputElement.getFormat()) {
                Format format = new Format(formatElement.getValue());
                formats.addByColName(format,formatElement.getColumn());
            }
        } catch (Exception e) {
            log.error("exception adding formats",e);
            throw new BadReportSyntaxException("Exception adding formats");
        }

        //drilldowns:
        drillDowns = new ObjectsByColumn<DrillDownElement>();
        for (DrillDownElement drillDownElement : outputElement.getDrillDown()) {
            drillDowns.addByColName(drillDownElement,drillDownElement.getColumn());

        }

        //groups:
        groups = new ObjectsByColumn<Integer>();
        i = 1;
        for (String group : outputElement.getGroup()) {
            groups.addByColName(i,group);
            i++;
        }

        //totals:
        totals = new ObjectsByColumn<Boolean>();
        for (String total : outputElement.getTotal()) {
            totals.addByColName(true,total);

        }
    }

    public void updatePostProcessClass() throws BadReportSyntaxException {
        if (outputElement.getPostProcessClass() != null) {
            String className = outputElement.getPostProcessClass();
            try {
                Class clazz = AppData.getCustomClassLoader().loadClass(className);
                postQueryProcessor = (PostQueryProcessor)clazz.newInstance();
            } catch (InstantiationException e) {
                throw new BadReportSyntaxException("Class " + className + " cannot be abstract or interface.");
            } catch (IllegalAccessException e) {
                throw new BadReportSyntaxException("No access to class " + className);
            } catch (ClassNotFoundException e) {
                throw new BadReportSyntaxException("Can't find class " + className);
            }
        }

    }

    protected List getData (List data) throws DataAccessException {

        List newData = data;
        if (postQueryProcessor != null) {
            newData = postQueryProcessor.postProcess(data);
        }
        return newData;
    }

    public String getFormatAttributeForColumn(int column, String columnName) {
        //   <%--                format="<%=output.getFormats().get(column,columnName)%>" --%>
        // should look like format="{0,number,0,000.00} $" or something --
        Format format = this.getFormats().get(column,columnName);
        if (format == null) return "";

        StringBuilder sb = new StringBuilder("{0,number,");
        sb.append(format.getDecimalFormatString());
        sb.append("}");

        return sb.toString();
    }
}
