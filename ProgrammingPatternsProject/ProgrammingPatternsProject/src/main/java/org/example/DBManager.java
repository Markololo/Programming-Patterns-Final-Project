package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager
{
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


    public static void createTable(String tableName, int id, String type) {
        String sql = """
                CREATE TABLE IF NOT EXISTS TableName
                (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                age INTEGER
                );
                """;
        //In java, triple quotes are called text blocks, so that we don't write long strings like "abc" + "..." + ...

        try {
            Connection con = connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Table Created Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}