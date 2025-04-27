package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.management.remote.JMXConnectorFactory.connect;

public class DBManager {
    DbController db;

    public DBManager() {
        db = DbController.getInstance();
        initialiseDB();
    }

    private void initialiseDB() {

        String clientsTable = """
                CREATE TABLE IF NOT EXISTS clients
                (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                contact INTEGER NOT NULL,
                numOfMembers INTEGER NOT NULL,
                isInHotel BOOLEAN NOT NULL
                );
                """;
        String roomsTable = """
                CREATE TABLE IF NOT EXISTS rooms (
                    roomNum INTEGER PRIMARY KEY,
                    roomType TEXT NOT NULL,
                    price REAL NOT NULL,
                    isAvailable BOOLEAN NOT NULL,
                    addedDate DATE DEFAULT (DATE('now'))
                );
                """;
        String bookingsTable = """
                CREATE TABLE IF NOT EXISTS bookings (
                    bookingNum INTEGER PRIMARY KEY,
                    clientId INTEGER NOT NULL,
                    roomId INTEGER NOT NULL,
                    startDate DATE NOT NULL,
                    endDate DATE NOT NULL,
                    FOREIGN KEY(clientId) REFERENCES clients(id),
                    FOREIGN KEY(roomId) REFERENCES rooms(roomNo)
                );
                """;
        String staffTable = """
                CREATE TABLE IF NOT EXISTS staff (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    position TEXT NOT NULL
                );
                """;
        try {
            Connection con = db.connect();
            Statement statement =  con.createStatement();
            statement.execute(clientsTable);
            statement.execute(roomsTable);
            statement.execute(bookingsTable);
            statement.execute(staffTable);
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
    public void deleteRow(String table, int pkValue, String pkColumn) {
        String sql = "DELETE FROM " + table + " WHERE "+pkColumn+"=?";

        try {
            Connection con = db.connect();
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
     * Retrieves student data amd returns it as a list of student objects.
     */
    public List<Room> selectJsonRooms() {
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
            Connection con = db.connect();
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
     * Inserts a row to the client table
     * @param name name of the client
     * @param contact contact of the client
     * @param numOfMembers number of members with the client
     */
    public void insertClientRecord(String name, String contact, int numOfMembers, boolean isInHotel) {
        try {
            String sql = "INSERT INTO clients(name,contact,numOfMembers, isInHotel) VALUES(?,?,?,?)";

            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, contact);
            preparedStatement.setInt(3, numOfMembers);
            preparedStatement.setBoolean(4, isInHotel);

            preparedStatement.executeUpdate();
            System.out.println("Client Row Added.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e1) {
            throw new RuntimeException("Error while trying to insert row. Cannot find the Client table.");
        }
    }

    public boolean insertRoomRecord(int roomNum, String roomType, double price, boolean isAvailable) {
        try {
            String sql = "INSERT INTO rooms(roomNum, roomType, price, isAvailable) VALUES(?,?,?,?)";

            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, roomNum);
            preparedStatement.setString(2, roomType);
            preparedStatement.setDouble(3, price);
            preparedStatement.setBoolean(4, isAvailable);
            preparedStatement.executeUpdate();
            System.out.println("Room Row Added.");
            return true;
        } catch (SQLException e) {
            System.out.println("Cannot find the Rooms table or Room Already exists:");
            return false;
        } catch (Exception e1) {
            return false;
        }
    }

    public boolean insertBookingRecord(int bookingNum, int clientId, int roomId, Date startDate, Date endDate) {
        try {
            String sql = "INSERT INTO bookings(bookingNum, clientId, roomId, startDate, endDate) VALUES(?,?,?,?,?)";

            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, bookingNum);
            preparedStatement.setInt(2, clientId);
            preparedStatement.setInt(3, roomId);
            preparedStatement.setDate(4, new java.sql.Date(startDate.getTime()));//autocorrected date to match sql
            preparedStatement.setDate(5, new java.sql.Date(endDate.getTime()));
            preparedStatement.executeUpdate();
            System.out.println("Booking Row Added.");
            return true;
        } catch (SQLException e) {
            System.out.println("Cannot find the Bookings table or Booking already exists.");
            return false;
        } catch (Exception e1) {
            return false;
        }
    }

    public boolean checkoutClient(int clientID) {
        try {
            //check if client is in the hotel
            String checkIfInHotel = "SELECT isInHotel FROM clients WHERE id = ?";
            Connection con = db.connect();
            PreparedStatement checkStatement = con.prepareStatement(checkIfInHotel);
            checkStatement.setInt(1, clientID);
            ResultSet rs = checkStatement.executeQuery();
            if (rs.next()) {
                boolean isInHotel = rs.getBoolean("isInHotel");

                if (!isInHotel) {
                    System.out.println("Client is already checked out.");
                    return false;
                }

                //Checkout client
                String updateClientStatusSql = "UPDATE clients SET isInHotel = ? WHERE id = ?";
                PreparedStatement updateStatement = con.prepareStatement(updateClientStatusSql);
                updateStatement.setBoolean(1, false);
                updateStatement.setInt(2, clientID);
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Client checked out successfully.");

                    // Optionally, update the bookings table to mark this booking as checked-out or completed.
                    // You can modify the `bookings` table if necessary.

                    //Mark the room as available again
                    String updateRoomAvailabilitySql = "UPDATE rooms SET isAvailable = ? WHERE roomNum IN (SELECT roomId FROM bookings WHERE clientId = ? AND endDate >= DATE('now'))";
                    PreparedStatement updateRoomStatement = con.prepareStatement(updateRoomAvailabilitySql);
                    updateRoomStatement.setBoolean(1, true);  // Mark the room as available.
                    updateRoomStatement.setInt(2, clientID);
                    updateRoomStatement.executeUpdate();

                    return true;  // Checkout Successful.
                } else {
                    return false;  // Something went wrong.
                }
            } else {
                return false;  // Client not found since rs.next() is false
            }
        } catch (SQLException e) {
            return false;  // Error occurred
        }
    }


    /**
     * Drops a table from the db
     * @param tableName table to remove
     */
    public void dropTable(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try {
            Connection con = db.connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Success: Table " + tableName + " does not exist now.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a column to a table
     * @param columnName name of the column
     * @param columnType data type
     * @param tableName name of the updated table
     * @param constraints constraints of the column, use empty string if there isn't
     */
    public void addColumn(String columnName, String columnType, String tableName, String constraints) {
        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType + " " + constraints + ";";
        try {
            Connection con = db.connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Column " + columnName + " added in table " + tableName);
        } catch (SQLException e) {
            System.out.println("Something went wrong while trying to add a column: " + e.getMessage());;
        }
    }


    /**
     * You can query all data in plain text and display them instead of using execute or execute update.
     * Here executeQuery is used. The result's stored in a ResultSet object,
     * and we can use getters such as getInt, getString, etc. to retrieve each field from it.
     * @param table name of the table
     */
    public String selectPlainText(String table) {
        String sql = "SELECT * FROM " + table;
        StringBuilder builder = new StringBuilder();
        try {
            Connection con = db.connect();
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
     * Creates an empty table.
     * Add columns later to complete table creation
     * @param tableName name of the new table
     */
    private void createTable(String tableName) {
        //Using a placeholder that can be ignored to create an empty table:
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(placeholder INTEGER);";
        try {
            Connection con = db.connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
            System.out.println("Table " + tableName + "Created Successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) {
//        //Test the connection jdbc connect()
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
// * @param name name of the client
// * @param contact contact of the client
// * @param numOfMembers*/
//        //dropTable("Client");
//        createTable("Client");
//        addColumn("clientID", "INTEGER", "Client", "PRIMARY KEY");
//
//        //insertClientRecord(1234, "Marko", "514-331-9023", 3);
//        //System.out.println(selectPlainText("client"));
//        //dropTable("Client");
//
//        //addColumn("email", "TEXT", "students");
//        //dropTable("students");
//        //insertStudentRecord("Alex", 25);//id is 1 by default I think
//        //insertStudentRecord("Alex", 19);//id is 2
//        //updateStudent(2, "Alex", 17);// -> An existing student was updated successfully
//        //updateStudent(2, "Alex", 17);
//        //deleteStudent(2);
//        //insertStudentRecord("Martin", 19);
//        //updateStudent(3, "Mark", 18);
//        //System.out.println(selectPlainText());//Plain text Format
//
//        //JSON-GUI
//        //Convert list of students to a nicely formatted JSON string(PrettyPrinting)
//        //GSON Builder let us customize JSON and is a helper class used to customize how Gson behaves
//        //setPrettyPrinting() tells GSON "when converting objects to JSON, make it easy to read using indentation and line breaks"
//        // .create() finalizes the configuration and returns a JSON object that you can use.
////        Gson gson = new GsonBuilder().setPrettyPrinting().create();
////
////        List<Room> rooms = selectJson();//JSON Format
////        //System.out.println(students);
////        String prettyJson = gson.toJson(rooms);
////
//////        //Create textArea
////        JTextArea textArea = new JTextArea(prettyJson);
////        textArea.setLineWrap(true); //Automatically wraps lines
////        textArea.setWrapStyleWord(true); //To wrap at word boundaries, not in the middle of the word
////        textArea.setEditable(false); //Make it read-only
////        //Add scrolling:
////        JScrollPane scrollPane = new JScrollPane(textArea);
////        //Create the swing window (JFrame):
////        JFrame frame = new JFrame("Students in JSON");
////        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////        frame.getContentPane().add(scrollPane);
////        frame.setSize(600,200);
////        frame.setVisible(true);
//    }
}
