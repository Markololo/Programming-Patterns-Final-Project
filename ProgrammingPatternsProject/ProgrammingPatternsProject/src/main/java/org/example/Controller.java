package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Controller {
    public static Connection connect() {
        String Base_Path = "jdbc:sqlite:src/main/resources/database";//java db connectivity(jdbc)
        //jdbc:sqlite is a jdbc connection URL to connect to a SQLite database in java
        //after the '.' is where you want your db to be(.src/...). But the '.' didn't work, so we removed it.
        //We made the database folder before
        String DB_Path = Base_Path + "Hotel_DB.db";//Making our database file

        Connection connection;
        try {
            //try to connect to the db:
            connection = DriverManager.getConnection(DB_Path);
            //DriverManager's a class that knows all the registered db drivers like SQLite, mysql, ...
            //and chooses the current one when you try to connect.
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

}
