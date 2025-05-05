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

import com.google.gson.Gson;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javax.management.remote.JMXConnectorFactory.connect;

/**
 * The DBManager class is the main controller to interact with the database.
 * It connects to the database through the singleton class DbController.
 * It has all the CRUD methods for the database.
 */
public class DBManager {
    DbController db;

    /**
     * The constructor of the class calls the database's initialization method initializeDB()
     * and connects to the instance of DbController singleton class.
     */
    public DBManager() {
        db = DbController.getInstance();
        initialiseDB();
    }

    /**
     * Initializes the tables in the database.
     */
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
                    FOREIGN KEY(clientId) REFERENCES clients(id) ON DELETE CASCADE,
                    FOREIGN KEY(roomNum) REFERENCES rooms(roomNum) ON DELETE SET NULL
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
     * Finds all the rooms that have the same type as the input.
     * @param roomType the type of the rooms to look for.
     * @return a list of rooms that match the roomType
     */

    //return the list of rooms from table
    public List<Room> findRoomByType(String roomType)
    {

        List<Room> rooms = new ArrayList<>();

        //lambda expression for each room we add to the list based on the room type parameter of method
        //using stream to perform an action on each element in the stream for each room in list
        //select from json type for rooms selectJsonRooms method shows in json format the room column names
        selectJsonRooms().stream().forEach(room -> {
            if (room.getRoomType().equalsIgnoreCase(roomType))
                rooms.add(room);
        });
        return rooms;
    }

    /**
     * Updates a room record based on the roomNumber (pk).
     * @param roomNum the primary key of the room.
     * @param price the price per night.
     * @param isAvailable represents a boolean "True" or "False" for the availability of the room.
     * @return an empty string if the update was successful, or a string of the error that occurred if the operation fails.
     */
    public String updateRoom(int roomNum, double price, String isAvailable) {
        List<Room> allRooms = selectJsonRooms();
        List<Booking> allBookings = selectJsonBookings();
        Room roomToUpdate = null;

        //Check if room exists
        for (Room room : allRooms) {
            if (room.getRoomNum() == roomNum)
                roomToUpdate = room;
        }
        if (roomToUpdate == null)
            return "searchRoomError";

        //Check if the room is booked: it cannot be updated if a client is in it.
//        if (roomToUpdate.getIsAvailable().equalsIgnoreCase("false") || roomToUpdate.getIsAvailable().equalsIgnoreCase("faux"))//I'm not sure if we need the OR
//            return "roomAlreadyBooked";//booked or just not available
        for (Booking booking : allBookings) {
            if (booking.getRoomNum() == roomNum && (booking.getEndDate() == null || booking.getEndDate().isEmpty())) {
                return "roomAlreadyBooked";
            }
        }

        try {
            Connection con = db.connect();
            String updateRoom = "UPDATE rooms SET price=?, isAvailable=? WHERE roomNum = ?";
            PreparedStatement updateRoomStmt = con.prepareStatement(updateRoom);
            updateRoomStmt.setDouble(1, price);
            updateRoomStmt.setString(2, isAvailable);
            updateRoomStmt.setInt(3, roomNum);
            updateRoomStmt.executeUpdate();
            return "";//empty string means success
        } catch (SQLException e) {
            return "sqlError";
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

            if (rowsUpdated <= 0)
                throw new Exception();
//                System.out.println("No row with the provided ID exists in table "+table+".");
        } catch (Exception e) {
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

    /**
     * Inserts a room row in the rooms table.
     * @param roomNum the primary key of the record
     * @param roomType the type of room
     * @param price the price per night
     * @param isAvailable represents a boolean "True" or "False" for the availability of the room.
     * @return true if the insert was successful.
     */
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
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Adds a booking record to the database.
     * @param clientId The id of the client that is booking the room.
     * @param roomNum The room number for the booking.
     * @param startDate The starting date of the booking.
     * @return an empty string if the insert was successful, or a string of the error that occurred if the operation fails.
     */
    public String insertBookingRecord(int clientId, int roomNum, LocalDate startDate) {
        try {
            Client client = findClient(clientId);
            Room room = findRoom(roomNum);

            //1.Check inputs
            if (client == null)//client does not exist
            {
                return "searchClientError"; //The problem is with the client id
            }
            if (room == null || room.getIsAvailable().equalsIgnoreCase("False")) {
                return "noRoomAvailable"; //the problem is the room no.
            }

            if (client.getNumOfMembers() > room.getSize()) {
                //Cannot book this room based on the number of members:
                return "roomSizeError";
            }

            //2.Insert booking
            String sql = "INSERT INTO bookings(clientId, roomNum, startDate) VALUES(?,?,?)";
            Connection con = db.connect();
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, clientId);
            preparedStatement.setInt(2, roomNum);
            preparedStatement.setString(3, startDate.toString());
            preparedStatement.executeUpdate();

            //3.Update client's isInHotel
            String updateClientSql = "UPDATE clients SET isInHotel='True' WHERE id=?";
            PreparedStatement preparedStatement2 = con.prepareStatement(updateClientSql);
            preparedStatement2.setInt(1, clientId);
            int rowsUpdated = preparedStatement2.executeUpdate(); //Returns the number of rows affected
            if (rowsUpdated <= 0) {
                return "sqlError";
            }

            //4.Update room availability
            String updateRoomSql = "UPDATE rooms SET isAvailable='False' WHERE roomNum=?";
            PreparedStatement updateRoom = con.prepareStatement(updateRoomSql);
            updateRoom.setInt(1, roomNum);
            updateRoom.executeUpdate();

            return "";//empty string means no problems occurred
        } catch (SQLException e) {
            return "sqlError";
        }
    }

    /**
     * Completes a booking to check out a client. The booking record is updated to today's date that marks the end
     * of the booking. If the booking ends before it starts, that means the client is canceling the booking.
     * @param bookingNum the primary key of the booking record
     * @return an empty string if the operation was successful, or a string of the error that occurred if the operation fails.
     */
    public String completeBooking(int bookingNum) {//to check out
        try {
            Connection con = db.connect();

            List<Booking> bookings = selectJsonBookings();
            Booking bookingToEnd = null;

            //Find booking:
            for (Booking booking : bookings) {
                if (booking.getBookingNum() == bookingNum) {
                    bookingToEnd = booking;
                }
            }

            if (bookingToEnd == null)
                return "searchBookingError";

//            if (LocalDate.now().isBefore(LocalDate.parse(bookingToEnd.getEndDate()))) {
//                return "cannotCheckoutEarlier";
//            }

            //Update endDate in the bookings table
            String updateBooking = "UPDATE bookings SET endDate = ? WHERE bookingNum = ?";
            PreparedStatement updateBookingStmt = con.prepareStatement(updateBooking);
            updateBookingStmt.setString(1, LocalDate.now().toString());
            updateBookingStmt.setInt(2, bookingNum);
            updateBookingStmt.executeUpdate();

            //Mark room as available
            String updateRoom = "UPDATE rooms SET isAvailable = 'True' WHERE roomNum = ?";
            PreparedStatement updateRoomStmt = con.prepareStatement(updateRoom);
            updateRoomStmt.setInt(1, bookingToEnd.getRoomNum());
            updateRoomStmt.executeUpdate();

            //Check for other active bookings
            String checkOtherBookings = "SELECT COUNT(*) FROM bookings WHERE clientId = ? AND endDate IS NULL";
            PreparedStatement checkStmt = con.prepareStatement(checkOtherBookings);
            checkStmt.setInt(1, bookingToEnd.getClientId());
            ResultSet countRs = checkStmt.executeQuery();

            if (countRs.next() && countRs.getInt(1) == 0) {//the count is zero
                // No other active bookings: update client's status
                String updateClient = "UPDATE clients SET isInHotel = 'False' WHERE id = ?";
                PreparedStatement updateClientStmt = con.prepareStatement(updateClient);
                updateClientStmt.setInt(1, bookingToEnd.getClientId());
                updateClientStmt.executeUpdate();
            }
            return "";//empty string means success

        } catch (SQLException e) {
            return "sqlError";
        }
    }

    /**
     * Drops a table from the database.
     * @param tableName Name of the table to remove.
     */
    public void dropTable(String tableName) {
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try {
            Connection con = db.connect();
            Statement statement =  con.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Selects all the rooms that are marked as available in the database.
     * @return the list of available rooms.
     */
    public List<Room> selectAvailableRooms() {
        List<Room> allRooms = selectJsonRooms();
        return allRooms.stream().filter(room -> room.getIsAvailable().equalsIgnoreCase("true")).toList();
    }

    /**
     * Selects all clients that are currently in the hotel
     * @return the list of all clients in the hotel.
     */
    public List<Client> selectCurrentClients() {
        List<Client> allClients = selectJsonClients();
        return allClients.stream().filter(client -> client.getIsInHotel().equalsIgnoreCase("true")).toList();
    }

    /**
     * Finds a client
     * @param id the ID of the client to look for.
     * @return the found client. If it's not found, then null is returned.
     */
    public Client findClient(int id) {
        List<Client> clientList = selectJsonClients().stream().filter(client -> client.getId() == id).toList();
        if (clientList.isEmpty())
            return null;
        return clientList.get(0);
    }

    /**
     * Finds a room in the database that matches the input room number.
     * @param roomNum the number of the room to find.
     * @return the room if it is found, or null if it is not found.
     */
    public Room findRoom(int roomNum) {
        List<Room> rooms = selectJsonRooms().stream().filter(room -> room.getRoomNum() == roomNum).toList();;
        if (rooms.isEmpty())
            return null;
        return rooms.get(0);
    }

    /**
     * Selects all the rooms in the database using json object.
     * @return the list of room records in the database.
     */
    public List<Room> selectJsonRooms() {
//        String sql = """
//                SELECT json_object(
//                'roomNum', roomNum,
//                'roomType', roomType,
//                'price', price,
//                'isAvailable', isAvailable,
//                'addedDate', addedDate
//                ) AS json_result
//                FROM Rooms;
//                """;
//        List<Room> rooms = new ArrayList<>();
//        Gson gson = new Gson();
//        try {
//            Connection con = db.connect();
//            Statement stmt = con.createStatement();
//            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch
//            while (rs.next()) {
//                String jsonResult = rs.getString("json_result");
//                Room room = gson.fromJson(jsonResult, Room.class);
//                rooms.add(room);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return rooms;
        String sql = "SELECT roomNum, roomType, price, isAvailable, addedDate FROM rooms;";
        List<Room> rooms = new ArrayList<>();
        try (Connection con = db.connect();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomNum(rs.getInt("roomNum"));
                room.setRoomType(rs.getString("roomType"));
                room.setPrice(rs.getDouble("price"));
                room.setIsAvailable(rs.getString("isAvailable"));
                room.setAddedDate(rs.getString("addedDate")); // Assuming it's a String
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rooms;
    }

    /**
     * Selects all the bookings in the database using json object.
     * @return the list of booking records in the database.
     */
    public List<Booking> selectJsonBookings() {
//        String sql = """
//                SELECT json_object(
//                'bookingNum', bookingNum,
//                'clientId', clientId,
//                'roomNum', roomNum,
//                'startDate', startDate,
//                'endDate', endDate
//                ) AS json_result
//                FROM bookings;
//                """;
//        List<Booking> bookings = new ArrayList<>();
//        Gson gson = new Gson();
//        try {
//            Connection con = db.connect();
//            Statement stmt = con.createStatement();
//            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch
//            while (rs.next()) {
//                String jsonResult = rs.getString("json_result");
//                Booking booking = gson.fromJson(jsonResult, Booking.class);
//                bookings.add(booking);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return bookings;
        String sql = "SELECT bookingNum, clientId, roomNum, startDate, endDate FROM bookings;";
        List<Booking> bookings = new ArrayList<>();
        try (Connection con = db.connect();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingNum(rs.getInt("bookingNum"));
                booking.setClientId(rs.getInt("clientId"));
                booking.setRoomNum(rs.getInt("roomNum"));
                booking.setStartDate(rs.getString("startDate")); // or use rs.getDate() if your model accepts it
                booking.setEndDate(rs.getString("endDate"));     // can be null
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookings;
    }

    /**
     * Selects all the clients in the database using json object.
     * @return the list of client records in the database.
     */
    public List<Client> selectJsonClients() {
//        String sql = """
//                SELECT json_object(
//                'id', id,
//                'name', name,
//                'contact', contact,
//                'numOfMembers', numOfMembers,
//                'isInHotel', isInHotel
//                ) AS json_result
//                FROM clients;
//                """;
//        List<Client> clients = new ArrayList<>();
//        Gson gson = new Gson();
//        try {
//            Connection con = db.connect();
//            Statement stmt = con.createStatement();
//            ResultSet rs = stmt.executeQuery(sql);//To store the result of the fetch
//            while (rs.next()) {
//                String jsonResult = rs.getString("json_result");
//                Client client = gson.fromJson(jsonResult, Client.class);
//                clients.add(client);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return clients;                   JSON does not update with the database
        String sql = "SELECT id, name, contact, numOfMembers, isInHotel FROM clients;";
        List<Client> clients = new ArrayList<>();
        try (Connection con = db.connect();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String contact = rs.getString("contact");
                int numOfMembers = rs.getInt("numOfMembers");
                String isInHotel = rs.getString("isInHotel"); // Assuming this is a String like "True"/"False"
                Client client = new Client(id, name, contact, numOfMembers, isInHotel);
                clients.add(client);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return clients;
    }

    /**
     * Selects all the rooms in the database based on the price, from low to high.
     * @return the list of sorted room records in the database.
     */
    public List<Room> findRoomLowToHighPrice() {
        List<Room> rooms = selectJsonRooms();
//to return the stored value for the list of rooms we filter in order for each room if the available is set to true then
        //then it sorts using the comparator and method comparing
        List<Room> availableSortedRooms = rooms.stream()
                .filter(r -> "true".equalsIgnoreCase(r.getIsAvailable())) // or "yes"
                //using method reference to compare the price from the room class
                .sorted(Comparator.comparing(Room::getPrice))
                //to convert the stream in to a list
                .collect(Collectors.toList());
        return availableSortedRooms;
    }

}
