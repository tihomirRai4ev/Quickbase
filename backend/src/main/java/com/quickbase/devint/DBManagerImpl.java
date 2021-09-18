package com.quickbase.devint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This DBManager implementation provides a connection to the database containing population data.
 * <p>
 * Created by ckeswani on 9/16/15.
 */
public class DBManagerImpl implements DBManager {

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:backend/resources/data/citystatecountry.db");
            System.out.println("Opened database successfully");
        } catch (Exception e) {
            System.out.println(e);
            try {
                connection.close();
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }

        return connection;
    }
}
