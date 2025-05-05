/*
// -------------------------------------------------------
Final Project
Written by: Mariam Salim #6229528 and Meerab Khan #2388387

Mariam's Part:
-Helped with Deliverable 1 and made a GitHub repository
-Made db file with Meerab
-Made the Singleton for the database (db)
-Made the Factory for language switching
-Made the model classes' content
-Made most of the DBManager's methods (tables and methods related to the database)
-Made the Staff Window(layout, controls, methods,
 translation and handling error messages with a popup depending on the methods' return from DBManager)
-Made the "Resource Bundle 'messages'" with translations
-Made the window switching from staff window to client window.
-Made the final report


Meerab's part:
-Most of Deliverable 1
-Made db file with Mariam
-Created model classes' structure (initiate java classes)
-Made the sign in window and its controls and connected it to the client window
-Added controls to the client window and added some GUI methods
-Made Junit testing
-Made login table with client ID and password (TODO)
-Handled login feature (TODO: to complete)
-Made the final report

// For "Programming Patterns" Section 2- Winter 2025
// --------------------------------------------------------
- General explanation:
The Hotel Management System is a small application that allows hotel staff (receptionists and managers)
and guests to interact with the system. Receptionists can add new room details, check room availability,
books rooms, check guests in and out, and view booking records. Guests can search for available rooms based
on type (single, double, suite, etc.) or price, book a room, and view their booking history.
 */

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



