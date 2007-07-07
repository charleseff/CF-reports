package com.cfinkel.reports.hsqldbsample;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: May 30, 2007
 * Time: 10:46:13 PM
 * To change this template use File | Settings | File Templates.
 */

import java.sql.*;
import java.io.File;

/**
 * Title:        Testdb
 * Description:  simple hello world db example of a
 *               standalone persistent db application
 *
 *               every time it runs it adds four more rows to sample_table
 *               it does a query and prints the results to standard out
 *
 * Author: Karl Meissner karl@meissnersd.com
 */
public class DbCreator {

    Connection conn;                                                //our connnection to the db - presist for life of program

    // we dont want this garbage collected until we are done
    public DbCreator(String db_file_name_prefix) throws Exception {    // note more general exception

        // Load the HSQL Database Engine JDBC driver
        // hsqldb.jar should be in the class path or made part of the current jar
        Class.forName("org.hsqldb.jdbcDriver");

        // connect to the database.   This will load the db files and start the
        // database if it is not alread running.
        // db_file_name_prefix is used to open or create files that hold the state
        // of the db.
        // It can contain directory names relative to the
        // current working directory
        conn = DriverManager.getConnection("jdbc:hsqldb:"
                + db_file_name_prefix,    // filenames
                "sa",                     // username
                "");                      // password
    }

    public void shutdown() throws SQLException {

        Statement st = conn.createStatement();

        // db writes out to files and performs clean shuts down
        // otherwise there will be an unclean shutdown
        // when program ends
        st.execute("SHUTDOWN");
        conn.close();    // if there are no other open connection
    }

    //use for SQL command SELECT
    public synchronized void query(String expression) throws SQLException {

        Statement st = null;
        ResultSet rs = null;

        st = conn.createStatement();         // statement objects can be reused with

        // repeated calls to execute but we
        // choose to make a new one each time
        rs = st.executeQuery(expression);    // run the query

        // do something with the result set.
        dump(rs);
        st.close();    // NOTE!! if you close a statement the associated ResultSet is

        // closed too
        // so you should copy the contents to some other object.
        // the result set is invalidated also  if you recycle an Statement
        // and try to execute some other query before the result set has been
        // completely examined.
    }

    //use for SQL commands CREATE, DROP, INSERT and UPDATE
    public synchronized void update(String expression) throws SQLException {
        Statement st = null;
        st = conn.createStatement();    // statements
        int i = st.executeUpdate(expression);    // run the query
        if (i == -1) {
            System.out.println("db error : " + expression);
        }
        st.close();
    }

    public static void dump(ResultSet rs) throws SQLException {

        // the order of the rows in a cursor
        // are implementation dependent unless you use the SQL ORDER statement
        ResultSetMetaData meta   = rs.getMetaData();
        int               colmax = meta.getColumnCount();
        int               i;
        Object            o = null;

        // the result set is a cursor into the data.  You can only
        // point to one row at a time
        // assume we are pointing to BEFORE the first row
        // rs.next() points to next row and returns true
        // or false if there is no next row, which breaks the loop
        for (; rs.next(); ) {
            for (i = 0; i < colmax; ++i) {
                o = rs.getObject(i + 1);    // Is SQL the first column is indexed

                // with 1 not 0
                System.out.print(o.toString() + " ");
            }

            System.out.println(" ");
        }
    }                                       //void dump( ResultSet rs )

    public static void main(String[] args) {

        DbCreator db = null;

        deleteFile("sample_hsqldb_creator/sample_hsqldb/db_file.script");
        deleteFile("sample_hsqldb_creator/sample_hsqldb/db_file.log");
        deleteFile("sample_hsqldb_creator/sample_hsqldb/db_file.properties");

        try {
            db = new DbCreator("sample_hsqldb_creator/sample_hsqldb/db_file");
        } catch (Exception ex1) {
            ex1.printStackTrace();    // could not start db

            return;                   // bye bye
        }

        try {

            //make an empty table
            //
            // by declaring the id column IDENTITY, the db will automatically
            // generate unique values for new rows- useful for row keys
            db.update("CREATE TABLE sample_table ( id INTEGER IDENTITY, str_col VARCHAR(256), num_col INTEGER)");
            db.update("CREATE TABLE sales_offices ( id INTEGER IDENTITY, name VARCHAR(256))");
            db.update("CREATE TABLE sales_people ( id INTEGER IDENTITY, last_name VARCHAR(256), first_name VARCHAR(256), sales_office_id INTEGER)");
            db.update("CREATE TABLE printers ( id INTEGER IDENTITY, name VARCHAR(256), color BOOLEAN )");
        } catch (SQLException ex2) {
            System.out.println(ex2);
        }

        try {

            // add some rows - will create duplicates if run more then once
            // the id column is automatically generated
            db.update(
                    "INSERT INTO sample_table(str_col,num_col) VALUES('Ford', 100)");
            db.update(
                    "INSERT INTO sample_table(str_col,num_col) VALUES('Toyota', 200)");
            db.update(
                    "INSERT INTO sample_table(str_col,num_col) VALUES('Honda', 300)");
            db.update(
                    "INSERT INTO sample_table(str_col,num_col) VALUES('GM', 400)");

            db.update("insert into sales_offices(name) values ('Memphis')");
            db.update("insert into sales_offices(name) values ('Oklahoma')");
            db.update("insert into sales_offices(name) values ('New York')");
            db.update("insert into sales_people(first_name,last_name,sales_office_id) values ('Bob','Smith',2)");
            db.update("insert into sales_people(first_name,last_name,sales_office_id) values ('Kate','Wesley',0)");
            db.update("insert into sales_people(first_name,last_name,sales_office_id) values ('Mike','Flor',0)");
            db.update("insert into sales_people(first_name,last_name,sales_office_id) values ('Sam','Ran',1)");
            db.update("insert into printers(name,color) values ('Big Printer',true)");
            db.update("insert into printers(name,color) values ('Little Printer',false)");

            // do a query
            db.query("SELECT * FROM sample_table WHERE num_col < 250");



            // at end of program
            db.shutdown();
        } catch (SQLException ex3) {
            ex3.printStackTrace();
        }
    }    // main()

    private static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (!file.isFile()) return;
        boolean wasDeleted =   file.delete();
        if (!wasDeleted) throw new RuntimeException("File '" + fileName + "' wasn't actually deleted");
    }
}    // class Testdb