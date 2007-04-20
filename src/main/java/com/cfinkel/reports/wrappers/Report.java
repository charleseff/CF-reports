package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.exceptions.InvalidInputException;
import com.cfinkel.reports.generatedbeans.*;
import com.cfinkel.reports.web.ParameterNames;
import com.cfinkel.reports.web.WebContext;
import com.cfinkel.reports.util.MapToInput;
import com.cfinkel.reports.ReportSessionInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

/**
 * 
 * $Author: charles $
 * $Revision: 8817 $
 * $Date: 2006-04-26 18:57:49 -0400 (Wed, 26 Apr 2006) $
 *
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 24, 2006
 * Time: 8:49:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Report implements Serializable {

    public Map<String, Query> getQueries() {
        return queries;
    }

    private Map<String, Query> queries;

    private static final Logger log = Logger.getLogger(Report.class);

    private ReportElement reportElement;

    public ReportElement getReportElement() {
        return reportElement;
    }

    public Map<String, Input> getAllInputs() {
        return allInputs;
    }

    private Map<String,Input> allInputs;

    public Map<String, Input> getBaseInputs() {
        return baseInputs;
    }

    private Map<String,Input> baseInputs;

    public Map<String, Output> getOutputs() {
        return outputs;
    }

    private Map<String,Output> outputs;

    private String reportPath = "";

    public String getReportURI() {
        WebContext webContext = WebContext.get();
        if (webContext != null)
            return webContext.getRequest().getContextPath() + "/report" + reportPath;
        // todo: is there a better thing than to report half the URI?
        return reportPath;
    }

    public String getReportPath() {
        return reportPath;
    }

    private List<String> roles;

    public Report(ReportElement reportElement, String reportPath) throws BadReportSyntaxException {
        this.reportPath = reportPath;
        this.reportElement = reportElement;
        allInputs = new HashMap<String,Input>();
        baseInputs = new LinkedHashMap<String,Input>();
        outputs = new LinkedHashMap<String,Output>();
        queries = new HashMap<String,Query>();

        for (BaseInputElement baseInputElement : reportElement.getInput()) {
            Input input = new BaseInput(baseInputElement,this);
            baseInputs.put(baseInputElement.getName(),input);
        }

        //get queries before outputs
        for (QueryElement queryElement : reportElement.getQuery()) {
            if (queries.get(queryElement.getName()) != null)
                throw new BadReportSyntaxException("Query named '" + queryElement.getName() + "' already exists.");
            Query query;
            if (queryElement instanceof PreparedQueryElement) {
                query = new PreparedQuery((PreparedQueryElement)queryElement,this);
            } else {
                query = new GeneratedQuery((GeneratedQueryElement)queryElement);
            }
            queries.put(queryElement.getName(),query);

        }

// get outputs
        for (OutputElement outputElement : reportElement.getOutput()) {
            if (outputs.get(outputElement.getName()) != null) {
                throw new BadReportSyntaxException("There are more than one output named '" + outputElement.getName());
            }
            Output output = new Output(outputElement,this);
            outputs.put(outputElement.getName(),output);
        }

        // get roles:
        this.roles = new ArrayList<String>();
        for (String role : reportElement.getAccess().getRole()) {
            roles.add(StringUtils.trim(role));
        }

    }

    /**
     * Runs the report.  Returns a map of output names to lists of data outputed
     * @return
     * @throws InvalidInputException
     * @throws ParseException
     */
    public Map<String,List> runNewSearch(ReportSessionInfo reportSessionInfo) throws InvalidInputException, ParseException {

        Map<Input, Object> inputMap = MapToInput.convertParameterMapToInputs(reportSessionInfo.getCachedParameterMapOfRunReport(),this);
        Map<String, Output> theseOutputs = new HashMap<String, Output>();

        // only get non-value list outputs:
        for (String outputName : this.getOutputs().keySet()) {
            Output output = this.getOutputs().get(outputName);
            if( ! output.getOutputElement().isValueList()) {
                theseOutputs.put(outputName,output);
            }
        }

        return getOutputData(inputMap, theseOutputs);
    }

    /**
     * runs only for non-cached data
     * todo: this should not run for outputs that have adapters:
     * @return all data
     * @throws InvalidInputException
     * @throws ParseException
     */
    public Map<String, List> runOnlyForNonCachedData(ReportSessionInfo reportSessionInfo) throws InvalidInputException, ParseException {

        Map<Input, Object> inputMap = MapToInput.convertParameterMapToInputs(
                reportSessionInfo.getCachedParameterMapOfRunReport(),this);
        Map<String, Output> outputs = getListOfNonCachedNonValueListOutputs(
                reportSessionInfo);

        Map<String, List> nonCachedData = getOutputData(inputMap,outputs);

        Map<String,List> allData = new LinkedHashMap<String,List>();
        allData.putAll(reportSessionInfo.getCachedData());
        allData.putAll(nonCachedData);
        return allData;
    }

    private Map<String, List> getOutputData(  Map<Input, Object> inputMap, Map<String, Output> outputs) throws ParseException {
        Map<String, Query> queries =   getQueriesForOutputs(outputs);
        // get the data:
        Map<String,List> queryDatas = new HashMap<String,List>();
        for (String queryName : queries.keySet()) {
            Query query = queries.get(queryName);
            List data = query.getData(inputMap);
            queryDatas.put(queryName,data);
        }

        Map<String, List> outputData = new LinkedHashMap<String,List>();

        // this is really for the post processing:
        for (String outputName : outputs.keySet()) {
            Output output = outputs.get(outputName);
            String queryName = output.getQuery().getQueryElement().getName();
            List data = queryDatas.get(queryName);
            outputData.put(outputName,output.getData(data));
        }
        return outputData;

    }

    private Map<String, Output> getListOfNonCachedNonValueListOutputs(ReportSessionInfo reportSessionInfo) throws InvalidInputException, ParseException {
        Map<String, Output>  outputs = new HashMap<String, Output> ();
        for (String output : this.getOutputs().keySet()) {
            if (reportSessionInfo.getCachedData().get(output) == null &&
                    reportSessionInfo.getValueListAdapters().get(output) == null) {
                outputs.put(output,this.getOutputs().get(output));
            }
        }
        return outputs;
    }

    private Map<String, Query> getQueriesForOutputs ( Map<String, Output>  outputs) {
        Map<String, Query> queries = new HashMap<String, Query> ();

        for (String outputName : outputs.keySet()) {
            Output output = outputs.get(outputName);
            queries.put(output.getQuery().getQueryElement().getName(),output.getQuery());
        }
        return queries;
    }

    /**
     * returns true if this report should show a submit button
     * @return true or false
     */
    public boolean isShouldShowSubmitButton() {
        if (this.getBaseInputs().size() == 0) return false;

        for (String string : this.getAllInputs().keySet()) {
            Input input = this.getAllInputs().get(string);
            if (!input.getControl().equals(Control.HIDDEN))
                return true;
        }
        return false;

    }

    /**
     * Updates custom classes, for generated queries and post query processors
     */
    public void updateCustomClasses() throws BadReportSyntaxException {

        for (String  queryName :        getQueries().keySet()) {
            Query query = getQueries().get(queryName);
            if (query instanceof GeneratedQuery) {
                ((GeneratedQuery)query).updateQueryGeneratorClass();
            }
        }

        for (String outputName : getOutputs().keySet()) {
            Output output = getOutputs().get(outputName);
            output.updatePostProcessClass();
        }
    }

    public String getWhiteSpaceSeparatedInputs() {
        StringBuilder string = new StringBuilder();
        for (String inputName : this.getAllInputs().keySet()) {
            string.append(inputName).append(" ");
        }
        string.append(ParameterNames.run);
        return string.toString();
    }


    /**
     * returns true if user has access
     * @param request
     * @return true if user has access
     */
    public boolean hasAccess(HttpServletRequest request) {
        //  if (request.isUserInRole("CN=developers,OU=aliases/lists,DC=cfinkel,DC=com")) return true;

        if (roles.contains("anon") || roles.contains("anonymous")) return true;

        for (String role : this.roles) {
            if (request.isUserInRole(role)) {
                return true;
            }
        }

        return false;
    }

}
