package com.cfinkel.reports.wrappers;


import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.OutputElement;
import com.cfinkel.reports.generatedbeans.PreparedQueryElement;
import com.cfinkel.reports.generatedbeans.QueryElement;
import com.cfinkel.reports.util.Util;
import com.cfinkel.reports.web.WebContext;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import java.text.ParseException;
import java.util.*;

/**
 * $Author: charles $
 * $Revision: 8904 $
 * $Date: 2006-05-01 18:02:06 -0400 (Mon, 01 May 2006) $
 * <p/>
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 25, 2006
 * Time: 6:09:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreparedQuery extends Query {

    private static final Logger log = Logger.getLogger(PreparedQuery.class);

    private PreparedQueryElement queryElement;
    private List<Input> inputs;

    public QueryElement getQueryElement() {
        return queryElement;
    }

    public PreparedQuery(PreparedQueryElement preparedQueryElement, Report report) throws BadReportSyntaxException {
        super(preparedQueryElement, null);
        populateInputs(preparedQueryElement, report);
    }

    public PreparedQuery(PreparedQueryElement preparedQueryElement, Report report, OutputElement outputElement) throws BadReportSyntaxException {
        super(preparedQueryElement, outputElement);
        populateInputs(preparedQueryElement, report);
    }

    private void populateInputs(PreparedQueryElement preparedQueryElement, Report report) throws BadReportSyntaxException {
        this.queryElement = preparedQueryElement;
        inputs = new ArrayList<Input>();
        for (String inputName : preparedQueryElement.getInputRef()) {
            Input input = report.getAllInputs().get(inputName);
            if (input == null) throw new BadReportSyntaxException("Input name " + inputName + " not found.");
            inputs.add(input);
        }

    }

    /**
     * inputs is the map from HttpServletRequest.getParameterMap()
     *
     * @param inputs
     * @return
     * @throws DataAccessException
     */
    public List getData(Map<Input, Object> inputs) throws DataAccessException, ParseException {

        Object[] queryParameters = getQueryParameters(inputs);

        String adjustedQuery = adjustQueryToInputs(inputs);

        String userName = "unknown user";
        WebContext user = WebContext.get();
        if (user != null) {
            userName = Util.getUserName(user.getRequest());
        }

        // log:
        StringBuilder sb = new StringBuilder();
        sb.append("User ").append(userName).append(" generated query: \n").append(adjustedQuery).append("\nwith ");
        if (queryParameters == null)
            sb.append("no parameters");
        else sb.append("parameters ").append(queryParameters.toString());
        log.info(sb);

        // runs the query:
        return jdbcTemplate.queryForList(adjustedQuery, queryParameters);
    }

    /**
     * todo: this shouldn't happen
     *
     * @param parameters
     * @return
     * @throws ParseException
     */
    public String getQueryString(Map<Input, Object> parameters) throws ParseException {
        throw new ParseException("Not yet implemented", 0);
    }

    private String adjustQueryToInputs(Map<Input, Object> inputs) {
        if (inputs == null) return this.queryElement.getSql();

        StringBuilder adjustedQuery = new StringBuilder();
        StringTokenizer st = new StringTokenizer(this.queryElement.getSql(), "?");

        try {
            adjustedQuery.append(st.nextToken());

            // adjust query for multiple-value inputs:

            for (Input input : this.inputs) {
                Object value = inputs.get(input);

                if ((value instanceof String[]) &&
                        ((String[]) value).length > 1) {
                    // add question marks:
                    adjustedQuery.append(Util.join("?", ",", ((String[]) value).length));
                } else {
                    // just keep one question mark:
                    adjustedQuery.append("?");
                }
                adjustedQuery.append(st.nextToken());
            }
        } catch (NoSuchElementException e) {
            // possibly reached last token, just return what we have:
            return adjustedQuery.toString();
        }

        return adjustedQuery.toString();
    }

    private Object[] getQueryParameters(Map<Input, Object> inputsFromRequest) throws ParseException {
        if (this.inputs.size() == 0) return null;

        int numberOfInputs = getNumberOfInputs(inputsFromRequest);
        Object[] queryParameters = new Object[numberOfInputs];

        // create parameter map for query:
        int i = 0;
        try {
            // iterate through input refs of this query:
            for (Input input : this.inputs) {
                Object obj = inputsFromRequest.get(input);
                if (obj instanceof String) {
                    queryParameters[i] = input.format((String) obj);
                    i++;
                } else {
                    // String[] :
                    String[] values = (String[]) obj;
                    for (String value : values) {
                        queryParameters[i] = input.format(value);
                        i++;
                    }
                }
            }
        } catch (ParseException e) {
            throw e;
        }
        return queryParameters;
    }

    private int getNumberOfInputs(Map<Input, Object> inputsFromRequest) {
        int numberOfInputs = 0;
        for (Input input : this.inputs) {
            Object obj = (inputsFromRequest.get(input));
            if (obj != null) {
                if (obj instanceof String) {
                    numberOfInputs++;
                } else {
                    numberOfInputs += ((String[]) obj).length;
                }
            }
        }
        return numberOfInputs;
    }

}
