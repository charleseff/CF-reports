package com.cfinkel.reports.wrappers;

import com.cfinkel.reports.exceptions.BadReportSyntaxException;
import com.cfinkel.reports.generatedbeans.OutputElement;
import com.cfinkel.reports.generatedbeans.QueryElement;
import com.cfinkel.reports.web.AppData;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * $Author:charles $
 * $Revision:9037 $
 * $Date:2006-05-08 13:14:49 -0400 (Mon, 08 May 2006) $
 *
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: Mar 25, 2006
 * Time: 6:09:54 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Query {
    private static final Logger log = Logger.getLogger(Query.class);

    protected JdbcTemplate jdbcTemplate;

    public DataSource getDataSource() {
        return dataSource;
    }

    private DataSource dataSource;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    // sets jdbctemplate and maxrows
    public Query(QueryElement queryElement, OutputElement outputElement) throws BadReportSyntaxException {

        String datasourceName = queryElement.getDatasource();
        dataSource = AppData.getDataSources().get(datasourceName);
        if (dataSource == null) {
            try {
                dataSource = AppData.addAndReturnDataSource(datasourceName);
            } catch (NamingException e) {
                String errorMessage = "Couldn't find JNDI value for datasource: " + datasourceName + ".  Didn't add datasource.";
                log.info(errorMessage,e);
                throw new BadReportSyntaxException(errorMessage);
            } catch (SQLException e) {
                String errorMessage = "SQL Exception - error";
                log.error(errorMessage,e);
                throw new RuntimeException(errorMessage,e);
            }
        }
        jdbcTemplate = new JdbcTemplate(dataSource);

        if (outputElement != null && outputElement.getMaxRowsForDisplayTag() > 0) {
            jdbcTemplate.setMaxRows(outputElement.getMaxRowsForDisplayTag());
        }
    }


    public abstract QueryElement getQueryElement();

    /**
     * @param parameters
     * @return
     * @throws DataAccessException
     */
    abstract List getData(Map<Input, Object> parameters) throws DataAccessException, ParseException;

    public abstract String getQueryString(Map<Input, Object> parameters) throws ParseException;
}
