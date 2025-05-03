package org.example;

import com.google.gson.Gson;
import lombok.*;



import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javax.management.remote.JMXConnectorFactory.connect;
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
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
    public Room findRoomByType(String roomType) {
        List<Room> rooms = selectJsonRooms();
        for (Room room : rooms){
            if (room.getRoomType().equals(roomType))
                return room;
        }
        return null;
    }




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
        for (Booking booking : allBookings) {
            if (booking.getRoomNum() == roomNum) {
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
            System.out.println("Booking Row Added.");

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

    public Room findRoom(int roomNum) {
        List<Room> rooms = selectJsonRooms();
        for (Room room : rooms){
            if (room.getRoomNum() == roomNum)
                return room;
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

    public List<Room> findRoomLowToHighPrice() {
        List<Room> rooms = selectJsonRooms();

        List<Room> availableSortedRooms = rooms.stream()
                .filter(r -> "true".equalsIgnoreCase(r.getIsAvailable())) // or "yes"
                .sorted(Comparator.comparing(Room::getPrice))
                .collect(Collectors.toList());

        System.out.println(availableSortedRooms);
        return availableSortedRooms;
    }

}
