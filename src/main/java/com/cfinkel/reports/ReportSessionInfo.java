package com.cfinkel.reports;

import com.cfinkel.reports.exceptions.InvalidInputException;
import com.cfinkel.reports.wrappers.Report;
import com.cfinkel.reports.wrappers.Output;
import com.cfinkel.reports.generatedbeans.OutputElement;
import com.cfinkel.reports.util.MapToInput;
import com.cfinkel.reports.valuelist.CFDynaBeanAdapter;
import com.cfinkel.reports.web.ParameterNames;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mlw.vlh.ValueListAdapter;

/**
 * $Author: charles $
 * $Revision: 8904 $
 * $Date: 2006-05-01 18:02:06 -0400 (Mon, 01 May 2006) $
 * 
 * This object is for session info per report
 * User: charles
 * Date: Apr 14, 2006
 * Time: 1:26:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportSessionInfo implements Serializable {
    private static final Logger log = Logger.getLogger(ReportSessionInfo.class);

    private final Report report;

    public Map<String, List> getCachedData() {
        return cachedData;
    }

    /**
     * session data:
     */
    private Map<String, List> cachedData = null;

    public Map<String, ValueListAdapter> getValueListAdapters() {
        return valueListAdapters;
    }

    private Map<String, ValueListAdapter> valueListAdapters = new HashMap<String,ValueListAdapter>();
    private Map<String,String[]> cachedParameterMapOfRunReport = new HashMap<String,String[]>();
    private boolean reportWasRunAndDataWasNotCleared = false;

    public Report getReport() {
        return report;
    }


    public ReportSessionInfo(Report report) {
        this.report = report;
    }

    public void clearCachedData() {
        if (cachedData != null) {
            cachedData.clear();
            cachedData = null;
        }
        reportWasRunAndDataWasNotCleared = false;
    }

    public Map<String, String[]> getCachedParameterMapOfRunReport() {
        return cachedParameterMapOfRunReport;
    }

    public void setCachedParameterMap(Map<String, String[]> parameterMap) {
        cachedParameterMapOfRunReport = new HashMap<String,String[]>();
        for (String key : parameterMap.keySet()) {
            cachedParameterMapOfRunReport.put(key,parameterMap.get(key));
        }
    }

    /**
     * gets encoded cached parameters
     * @return encoded parameters
     */
    public String getEncodedParameters() {

        StringBuilder s = new StringBuilder("?");

        try {
            for (String inputName : cachedParameterMapOfRunReport.keySet()) {
                String[] inputs = cachedParameterMapOfRunReport.get(inputName);
                for (String value : inputs) {
                    String urlValue = URLEncoder.encode(value,"UTF-8");
                    s.append(inputName).append("=").append(urlValue).append("&amp;");
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        }

        return s.toString();
    }

    public Map<String,List> runReport(Map<String,String[]> parameterMap ) throws InvalidInputException, ParseException {

        this.setCachedParameterMap(parameterMap);
        Map<String, List> reportData = this.report.runNewSearch(this);
        this.cachedData = new HashMap<String,List>();

        for (String outputName : this.report.getOutputs().keySet()) {
            Output output = this.report.getOutputs().get(outputName);
            OutputElement outputElement = output.getOutputElement();

            if (!outputElement.isValueList()) {
                // remove any adapter that may have been present from before:
                this.valueListAdapters.remove(outputName);

                List outputData = reportData.get(outputName);
                if ((outputElement.getMaxRowsForCache() <= 0) ||
                        outputElement.getMaxRowsForCache() > outputData.size()) {
                    // cache all data:
                    this.cachedData.put(outputName,outputData);
                } else if (outputData.size() == outputElement.getMaxRowsForDisplayTag()) {
                    // use valuelist:
                    createAndAddAdapter(output,parameterMap);
                } else {
                    // no cached data
                    // remove cachedData:
                    this.cachedData.remove(outputName);
                }
            } else { // value list:
                createAndAddAdapter(output,parameterMap);
            }
        }

        reportWasRunAndDataWasNotCleared = true;
        return reportData;
    }

    /**
     * todo: fine tune this method:
     * @param output
     */
    private void createAndAddAdapter(Output output,Map<String,String[]> parameterMap) throws InvalidInputException, ParseException {
        CFDynaBeanAdapter adapter = new CFDynaBeanAdapter();
        adapter.setDataSource(output.getQuery().getDataSource());
        adapter.setUseName(false);
        adapter.setDefaultNumberPerPage(output.getOutputElement().getRowsPerPage());
        adapter.setDefaultSortDirection("asc");
        adapter.setShowSql(false);
        //    get query string, then add sort Column
        adapter.setSql("select * from (\n" +
                output.getQuery().getQueryString(MapToInput.convertParameterMapToInputs(parameterMap,this.report))
                + ")\n"
                + "                /~" + ParameterNames.sortColumn + ": ORDER BY \"[" +  ParameterNames.sortColumn
                + "]\" [" + ParameterNames.sortDirection + "]~/"
        );
        this.valueListAdapters.put(output.getOutputElement().getName(),adapter);
    }

    public boolean isReportWasRunAndDataWasNotCleared() {
        return reportWasRunAndDataWasNotCleared;
    }

    public Map<String, List> runOnlyForNonCachedData() throws InvalidInputException, ParseException {
        return this.report.runOnlyForNonCachedData(this);
    }

    public boolean isHasAnAdapterForOutput(Output output) {
        return this.valueListAdapters.get(output.getOutputElement().getName()) != null;
    }

}
