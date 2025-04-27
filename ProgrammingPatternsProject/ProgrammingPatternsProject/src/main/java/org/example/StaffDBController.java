package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is for the staff actions, since staff and clients have
 * different permissions to do things.
 * Hotel staff can add new room details, check room availability,
 * books rooms, check guests in and out, and view booking records.
 */
public class StaffDBController
{
    private List<Room> rooms;
    private List<Booking> bookings;


    /* adds room */
    public void addRoom(int roomNum, String roomType, double price, boolean available, Date addedDate)
    {
        Room newRoom = new Room(roomNum, roomType, price, available, addedDate);
        rooms.add(newRoom);
    }


    //returns list of available rooms
    public ArrayList<Room> checkRoomAvailability() {
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    //book client with a specified room type
    public void book(Client client) {
        if (client != null) {

        }
    }

    //cancels booking
    public void cancelBooking(int bookingID) {

    }
}

