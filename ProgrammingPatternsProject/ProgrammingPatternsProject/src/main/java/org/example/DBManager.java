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
                    roomNum INTEGER NOT NULL,
                    startDate DATE NOT NULL,
                    endDate DATE,
                    FOREIGN KEY(clientId) REFERENCES clients(id),
                    FOREIGN KEY(roomNum) REFERENCES rooms(roomNo)
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

    public boolean insertBookingRecord(int bookingNum, int clientId, int roomNum, Date startDate) {
        try {
            String sql = "INSERT INTO bookings(bookingNum, clientId, roomNum, startDate) VALUES(?,?,?,?)";

            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, bookingNum);
            preparedStatement.setInt(2, clientId);
            preparedStatement.setInt(3, roomNum);
            preparedStatement.setDate(4, new java.sql.Date(startDate.getTime()));//autocorrected date to match sql
//            preparedStatement.setDate(5, new java.sql.Date(endDate.getTime()));
            //preparedStatement.setDate(5, null);//End date will be updated at checkout
//            preparedStatement.setBoolean(6, true);//Booking is Active when added
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

    /**
     * Handles checking out a client from the hotel.
     * The client table will be updated to indicate the client is not in the hotel anymore
     * The bookings table will be updated: the end date will be set
     * The room of the client will be available again.
     * @param clientID the id of the client to check out.
     * @return true if the operation was run successfully
     */
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

                //Checkout client: no longer in hotel
                String updateClientStatusSql = "UPDATE clients SET isInHotel = ? WHERE id = ?";
                PreparedStatement updateStatement = con.prepareStatement(updateClientStatusSql);
                updateStatement.setBoolean(1, false);
                updateStatement.setInt(2, clientID);
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Client checked out successfully.");

                    // modify bookings if the client was able to check out
                    String updateBookingSql = "UPDATE bookings SET endDate = ? WHERE clientId = ? AND endDate IS NULL";
                    PreparedStatement updateBookingStatement = con.prepareStatement(updateBookingSql);

                    java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                    updateBookingStatement.setDate(1, today);  // Set the endDate to today's date
                    updateBookingStatement.setInt(2, clientID);

                    int rowsUpdatedBooking = updateBookingStatement.executeUpdate();

                    if (rowsUpdatedBooking <= 0) {
                        return false;
                    }

                    //Mark the room as available
                    String updateRoomAvailabilitySql = "UPDATE rooms SET isAvailable = ? WHERE roomNo IN (SELECT roomNum FROM bookings WHERE clientId = ? AND endDate IS NULL)";
                    PreparedStatement updateRoomStatement = con.prepareStatement(updateRoomAvailabilitySql);
                    updateRoomStatement.setBoolean(1, true);  // Mark the room as available.
                    updateRoomStatement.setInt(2, clientID);
                    updateRoomStatement.executeUpdate();

                    return true;//Checkout Successful.
                } else {
                    return false;//Something went wrong.
                }
            } else {
                return false;//Client not found since rs.next() is false
            }
        } catch (SQLException e) {
            return false;//Error occurred
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

    private <T> List<T> selectJSONRows(Class<T> selectedClass, String sql) {
        List<T> rows = new ArrayList<T>();
        Gson gson = new Gson();
        try {
            Connection con = db.connect();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch
            while (rs.next()) {
                String jsonResult = rs.getString("json_result");
                T row = gson.fromJson(jsonResult, selectedClass);
                rows.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rows;
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
        return selectJSONRows(Room.class, sql);
    }

    public List<Booking> selectJsonBookings() {
        String sql = """
                SELECT json_object(
                'bookingNum', bookingNum,
                'clientId', clientId,
                'roomNum', roomNum,
                'startDate', startDate
                'endDate', endDate
                ) AS json_result
                FROM Rooms;
                """;
        return selectJSONRows(Booking.class, sql);
    }

    public List<Client> selectJsonClients() {
        String sql = """
                SELECT json_object(
                'id', id,
                'name', name,
                'contact', contact,
                'numOfMembers', numOfMembers
                'isInHotel', isInHotel
                ) AS json_result
                FROM Rooms;
                """;
        return selectJSONRows(Client.class, sql);
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
}
