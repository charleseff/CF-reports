package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.QueryGenerator;
import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.GeneratedQueryElement;
import com.cfinkel.reports.generatedbeans.OutputElement;
import com.cfinkel.reports.generatedbeans.QueryElement;
import com.cfinkel.reports.util.Util;
import com.cfinkel.reports.web.AppData;
import com.cfinkel.reports.web.WebContext;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class GeneratedQuery extends Query {
    private static final Logger log = Logger.getLogger(GeneratedQuery.class);
    private GeneratedQueryElement generatedQueryElement;

    private QueryGenerator queryGenerator;

    public QueryElement getQueryElement() {
        return generatedQueryElement;
    }

    public GeneratedQuery(GeneratedQueryElement generatedQueryElement) throws BadReportSyntaxException {
        super(generatedQueryElement, null);
        populateQueryGeneratorClass(generatedQueryElement);
    }

    public GeneratedQuery(GeneratedQueryElement generatedQueryElement, OutputElement outputElement) throws BadReportSyntaxException {
        super(generatedQueryElement, outputElement);
        populateQueryGeneratorClass(generatedQueryElement);
    }

    private void populateQueryGeneratorClass(GeneratedQueryElement generatedQueryElement) throws BadReportSyntaxException {
        this.generatedQueryElement = generatedQueryElement;

        if (generatedQueryElement.getClazz() != null) {
            updateQueryGeneratorClass();
        } else {
            throw new BadReportSyntaxException("generated-query element must 'class' attribute");
        }
    }

    /**
     * inputs is the map from HttpServletRequest.getParameterMap()
     *
     * @param parameters
     * @return
     * @throws DataAccessException
     */
    public List getData(Map<Input, Object> parameters) throws DataAccessException, ParseException {
        String query = getQueryString(parameters);
        List data;
        data = jdbcTemplate.queryForList(query);
        return data;
    }

    public String getQueryString(Map<Input, Object> parameters) throws ParseException {
        // get the user from session:
        String userName = "unknown user";
        WebContext user = WebContext.get();
        if (user != null) {
            userName = Util.getUserName(user.getRequest());
        }

        //         * reconstruct original rqeuest param map (a bit of a kludge)
        //   * todo: make less kludgey
        Map<String, Object> stringParameters = new HashMap<String, Object>();
        for (Input input : parameters.keySet()) {
            Object value = parameters.get(input);
            if ((value instanceof String) ||
                    ((value instanceof String[]) && (((String[]) value).length > 1))
                    ) {
                stringParameters.put(input.getName(), parameters.get(input));
            } else {
                String finalVal = ((String[]) value)[0];
                stringParameters.put(input.getName(), finalVal);
            }
        }

        String query = queryGenerator.generateQuery(stringParameters, userName);
        log.info("User " + userName + " generated query: \n" + query);
        return query;
    }


    public void updateQueryGeneratorClass() throws BadReportSyntaxException {

        String className = generatedQueryElement.getClazz();
        try {
            Class clazz = AppData.getCustomClassLoader().loadClass(className);
            queryGenerator = (QueryGenerator) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BadReportSyntaxException("Class " + className + " cannot be abstract or interface.");
        } catch (IllegalAccessException e) {
            throw new BadReportSyntaxException("No access to class " + className);
        } catch (ClassNotFoundException e) {
            throw new BadReportSyntaxException("Can't find class " + className);
        }
    }
}
