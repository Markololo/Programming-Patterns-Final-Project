package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBManager {

    /**
     * Connect to JDBC driver
     * @return jdbc connection
     */
    public static Connection connect() {
        String Base_Path = "jdbc:sqlite:src/main/resources/database";
        String DB_Path = Base_Path + "Hotel_DB.db";//Making our database file
        Connection connection;
        try {
            //try to connect to the db:
            connection = DriverManager.getConnection(DB_Path);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    /**
     * Adds a column to a table
     * @param columnName name of the column
     * @param columnType data type
     * @param tableName name of the updated table
     * @param constraints constraints of the column, use empty string if there isn't
     */
    public static void addColumn(String columnName, String columnType, String tableName, String constraints) {
        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType + " " + constraints + ";";
        try {
            Connection con = connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Column " + columnName + " added in table " + tableName);
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to add a column: " + e.getMessage());;
        }
    }

    /**
     * Drops a table from the db
     * @param tableName table to remove
     */
    public static void dropTable(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try {
            Connection con = connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Success: Table " + tableName + " does not exist now.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a row from a table using the primary key. Make sure
     * the primary key is the first column of every table, and it's an integer.
     * @param table name of the table of the row to be deleted
     * @param pkValue the value of the primary key or ID of the row
     */
    public static void deleteRow(String table, int pkValue, String pkColumn) {
        String sql = "DELETE FROM " + table + " WHERE "+pkColumn+"=?";

        try {
            Connection con = connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, pkValue);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0)
                System.out.println("Row with PrimaryKey " + pkValue + " removed successfully");
            else
                System.out.println("No row with the provided ID exists in table "+table+".");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * You can query all data in plain text and display them instead of using execute or execute update.
     * Here executeQuery is used. The result's stored in a ResultSet object,
     * and we can use getters such as getInt, getString, etc. to retrieve each field from it.
     * @param table name of the table
     */
    public static String selectPlainText(String table) {
        String sql = "SELECT * FROM " + table;
        StringBuilder builder = new StringBuilder();
        try {
            Connection con = connect();
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch

            switch (table.toLowerCase()) {
                case "client" : {
                    while (rs.next()) {
                        int id = rs.getInt("clientID");
                        String name = rs.getString("name");
                        String contact = rs.getString("contact");
                        int numOfMembers = rs.getInt("numOfMembers");

                        builder.append(String.format("ID: %d, Name: %s, Contact: %s, Members Count: %d%n", id, name, contact, numOfMembers));//For new line, we use '\n' or %n

                    }
                    break;
                }
                case "room" : {
                    while (rs.next()) {
                        int id = rs.getInt("roomNum");
                        String roomType = rs.getString("roomType");
                        double price = rs.getDouble("price");
                        boolean available = rs.getBoolean("available");
                        //Date addedDate = rs.getDate("addedDate");
                        Date addedDate = rs.getDate("addedDate");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        String formattedDate = formatter.format(addedDate);

                        builder.append(String.format("Room Number: %d, Room Type: %s, Price Per Night: %.2f, Available: %b, Added Date: %s%n", id, roomType, price, available, formattedDate));
                    }
                    break;
                }
                case "booking" : {
                    while (rs.next()) {
                        int bookingNum = rs.getInt("bookingNum");
                        int clientId = rs.getInt("clientId");

                        builder.append(String.format("Booking Number: %d, Client ID: %d%n", bookingNum, clientId));
                    }
                    break;
                }
                default: throw new IllegalArgumentException("Invalid table name. No row can be deleted. Table: " + table);
            };
        } catch (SQLException e) {
            System.out.println("Connection Failure. Make sure the table exists.");
        }
        return builder.toString();
    }

    /**
     * Retrieves student sata amd returns it as a list of student objects.
     */
    public static List<Room> selectJsonRooms() {
        String sql = """
                SELECT json_object(
                'Room Num', roomNum,
                'Type', roomType,
                'Price', price,
                'Available', available
                'AddedDate', addedDate
                ) AS json_result
                FROM Rooms;
                """;
        List<Room> rooms = new ArrayList<>();
        Gson gson = new Gson();
        try {
            Connection con = connect();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch
            while (rs.next()) {
                String jsonResult = rs.getString("json_result");
                Room room = gson.fromJson(jsonResult, Room.class);
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rooms;
    }


    /**
     * Creates an empty table.
     * Add columns later to complete table creation
     * @param tableName name of the new table
     */
    public static void createTable(String tableName) {
        //Using a placeholder that can be ignored to create an empty table:
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(placeholder INTEGER);";
        try {
            Connection con = connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Table " + tableName + "Created Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts a row to the client table
     * @param clientID primary key of the table (the id of the client)
     * @param name name of the client
     * @param contact contact of the client
     * @param numOfMembers number of members with the client
     */
    public static void insertClientRecord(int clientID, String name, String contact, int numOfMembers) {
        try {
            String sql = "INSERT INTO Client(clientID,name,contact,numOfMembers) VALUES(?,?,?,?)";

            Connection con = connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, clientID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, contact);
            preparedStatement.setInt(4, numOfMembers);
            preparedStatement.executeUpdate();
            System.out.println("Client Row Added");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e1) {
            throw new RuntimeException("Error while trying to insert row. Cannot find the Client table.");
        }
    }

    public static void main(String[] args) {
        //Test the connection jdbc connect()
//        try {
//            Connection con = connect();
//            if (con != null) {
//                System.out.println("...Successfully connected to SQLite!");
//            }
//        } catch (Exception e ) {
//            System.out.println("Connection Failure... :(");
//            System.out.println(e.getMessage());
//        }
/*     * @param clientID primary key of the table (the id of the client)
 * @param name name of the client
 * @param contact contact of the client
 * @param numOfMembers*/
        //dropTable("Client");
        createTable("Client");
        addColumn("clientID", "INTEGER", "Client", "PRIMARY KEY");

        //insertClientRecord(1234, "Marko", "514-331-9023", 3);
        //System.out.println(selectPlainText("client"));
        //dropTable("Client");

        //addColumn("email", "TEXT", "students");
        //dropTable("students");
        //insertStudentRecord("Alex", 25);//id is 1 by default I think
        //insertStudentRecord("Alex", 19);//id is 2
        //updateStudent(2, "Alex", 17);// -> An existing student was updated successfully
        //updateStudent(2, "Alex", 17);
        //deleteStudent(2);
        //insertStudentRecord("Martin", 19);
        //updateStudent(3, "Mark", 18);
        //System.out.println(selectPlainText());//Plain text Format

        //JSON-GUI
        //Convert list of students to a nicely formatted JSON string(PrettyPrinting)
        //GSON Builder let us customize JSON and is a helper class used to customize how Gson behaves
        //setPrettyPrinting() tells GSON "when converting objects to JSON, make it easy to read using indentation and line breaks"
        // .create() finalizes the configuration and returns a JSON object that you can use.
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//        List<Room> rooms = selectJson();//JSON Format
//        //System.out.println(students);
//        String prettyJson = gson.toJson(rooms);
//
////        //Create textArea
//        JTextArea textArea = new JTextArea(prettyJson);
//        textArea.setLineWrap(true); //Automatically wraps lines
//        textArea.setWrapStyleWord(true); //To wrap at word boundaries, not in the middle of the word
//        textArea.setEditable(false); //Make it read-only
//        //Add scrolling:
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        //Create the swing window (JFrame):
//        JFrame frame = new JFrame("Students in JSON");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(scrollPane);
//        frame.setSize(600,200);
//        frame.setVisible(true);
    }
}
