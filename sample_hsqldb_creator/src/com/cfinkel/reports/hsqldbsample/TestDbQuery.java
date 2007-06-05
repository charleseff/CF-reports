package com.cfinkel.reports.hsqldbsample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: May 30, 2007
 * Time: 11:13:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestDbQuery {
    public static void main(String[] args) throws SQLException {
        try {
            Class.forName("org.hsqldb.jdbcDriver" );
        } catch (Exception e) {
            System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return;
        }
        Connection c = DriverManager.getConnection("jdbc:hsqldb:file:db_file", "sa", "");

        ResultSet rs =    c.createStatement().executeQuery("SELECT * FROM sample_table WHERE num_col < 250");
        while (rs.next()) {
    int f =4;

        }

    }
}
