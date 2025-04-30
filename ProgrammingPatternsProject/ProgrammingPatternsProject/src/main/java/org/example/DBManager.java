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
                contact TEXT NOT NULL,
                numOfMembers INTEGER NOT NULL,
                isInHotel TEXT NOT NULL
                );
                """;
        String roomsTable = """
                CREATE TABLE IF NOT EXISTS rooms (
                    roomNum INTEGER PRIMARY KEY,
                    roomType TEXT NOT NULL,
                    price REAL NOT NULL,
                    isAvailable TEXT NOT NULL,
                    addedDate TEXT DEFAULT (DATE('now'))
                );
                """;
        String bookingsTable = """
                CREATE TABLE IF NOT EXISTS bookings (
                    bookingNum INTEGER PRIMARY KEY,
                    clientId INTEGER NOT NULL,
                    roomNum INTEGER NOT NULL,
                    startDate TEXT NOT NULL,
                    endDate TEXT,
                    FOREIGN KEY(clientId) REFERENCES clients(id),
                    FOREIGN KEY(roomNum) REFERENCES rooms(roomNum)
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
    public void deleteRow(String table, String pkColumn, int pkValue) {
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
    public boolean insertClientRecord(String name, String contact, int numOfMembers, String isInHotel) {
        try {
            String sql = "INSERT INTO clients(name,contact,numOfMembers, isInHotel) VALUES(?,?,?,?)";

           // int isInHotelInt = (isInHotel)? 0:1;

            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, contact);
            preparedStatement.setInt(3, numOfMembers);
            preparedStatement.setString(4, isInHotel);

            preparedStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean insertRoomRecord(int roomNum, String roomType, double price, String isAvailable) {
        try {
            String sql = "INSERT INTO rooms(roomNum, roomType, price, isAvailable) VALUES(?,?,?,?)";

            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, roomNum);
            preparedStatement.setString(2, roomType);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, isAvailable);
            preparedStatement.executeUpdate();
            System.out.println("Room Row Added.");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean insertBookingRecord(int bookingNum, int clientId, int roomNum, Date startDate) {
        try {
//            Date today = new Date();
//            if (startDate.compareTo(today) < 0) {
//                return false; //Cannot book today since yesterday
//            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
//            String formattedToday = simpleDateFormat.format(today);
            String formattedStartDate = simpleDateFormat.format(startDate);

            String sql = "INSERT INTO bookings(bookingNum, clientId, roomNum, startDate) VALUES(?,?,?,?)";

            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, bookingNum);
            preparedStatement.setInt(2, clientId);
            preparedStatement.setInt(3, roomNum);
            preparedStatement.setString(4, formattedStartDate);
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
                String isInHotel = rs.getString("isInHotel");
                if (isInHotel.equalsIgnoreCase("false")) {
                    System.out.println("Client is already checked out.");
                    return false;
                }

                //Checkout client: no longer in hotel
                String updateClientStatusSql = "UPDATE clients SET isInHotel = ? WHERE id = ?";
                PreparedStatement updateStatement = con.prepareStatement(updateClientStatusSql);
                updateStatement.setString(1, "false");
                updateStatement.setInt(2, clientID);
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Client checked out successfully.");

                    // modify bookings if the client was able to check out
                    String updateBookingSql = "UPDATE bookings SET endDate = ? WHERE clientId = ? AND endDate IS NULL";
                    PreparedStatement updateBookingStatement = con.prepareStatement(updateBookingSql);

                    java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                    updateBookingStatement.setString(1, today+"");  // Set the endDate to today's date
                    updateBookingStatement.setInt(2, clientID);

                    int rowsUpdatedBooking = updateBookingStatement.executeUpdate();

                    if (rowsUpdatedBooking <= 0) {
                        return false;
                    }

                    //Mark the room as available
                    String updateRoomAvailabilitySql = "UPDATE rooms SET isAvailable = ? WHERE roomNo IN (SELECT roomNum FROM bookings WHERE clientId = ? AND endDate IS NULL)";
                    PreparedStatement updateRoomStatement = con.prepareStatement(updateRoomAvailabilitySql);
                    updateRoomStatement.setString(1, "True");  // Mark the room as available.
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

    public List<Room> selectAvailableRooms() {
        List<Room> allRooms = selectJsonRooms();
        return allRooms.stream().filter(room -> room.getIsAvailable().equalsIgnoreCase("true")).toList();
    }

    public List<Client> selectCurrentClients() {
        List<Client> allClients = selectJsonClients();
        return allClients.stream().filter(client -> client.getIsInHotel().equalsIgnoreCase("true")).toList();
    }

    public Client findClient(int id) {
        List<Client> clients = selectJsonClients();
        for (Client client : clients){
            if (client.getId() == id)
                return client;
        }
        return null;
    }

    public List<Room> selectJsonRooms() {
        String sql = """
                SELECT json_object(
                'roomNum', roomNum,
                'roomType', roomType,
                'price', price,
                'isAvailable', isAvailable,
                'addedDate', addedDate
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

    public List<Booking> selectJsonBookings() {
        String sql = """
                SELECT json_object(
                'bookingNum', bookingNum,
                'clientId', clientId,
                'roomNum', roomNum,
                'startDate', startDate,
                'endDate', endDate
                ) AS json_result
                FROM bookings;
                """;
        List<Booking> bookings = new ArrayList<>();
        Gson gson = new Gson();
        try {
            Connection con = db.connect();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch
            while (rs.next()) {
                String jsonResult = rs.getString("json_result");
                Booking booking = gson.fromJson(jsonResult, Booking.class);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookings;
    }

    public List<Client> selectJsonClients() {

        String sql = """
                SELECT json_object(
                'id', id,
                'name', name,
                'contact', contact,
                'numOfMembers', numOfMembers,
                'isInHotel', isInHotel
                ) AS json_result
                FROM clients;
                """;
        List<Client> clients = new ArrayList<>();
        Gson gson = new Gson();
        try {
            Connection con = db.connect();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch
            while (rs.next()) {
                String jsonResult = rs.getString("json_result");
                Client client = gson.fromJson(jsonResult, Client.class);
                clients.add(client);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return clients;
    }


}
