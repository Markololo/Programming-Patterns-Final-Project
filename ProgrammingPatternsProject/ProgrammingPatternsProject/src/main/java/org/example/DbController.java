package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to manage the database connection.
 */
public class DbController {
    private static DbController dbObject;

    //to restrict object creation outside the class.
    private DbController() {}

    public static DbController getInstance() {
        if (dbObject == null) {//if no object has been made
            dbObject = new DbController();
        }
        return dbObject;
    }

    /**
     * Connect to JDBC driver for db connection.
     * @return jdbc connection
     */
    public Connection connect() {
//        String Base_Path = "jdbc:sqlite:src/main/resources/database";
//        String DB_Path = Base_Path + "ProductData.db";
//        String DB_Path = "jdbc:sqlite:src/main/resources/productData.db";
        String DB_Path = "jdbc:sqlite:productData.db";//the jar will locate it in the same folder

        Connection connection;
        try {
            //try to connect to the db:
            connection = DriverManager.getConnection(DB_Path);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}



