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
public class StaffController {
    private List<Room> rooms;
    private List<Booking> bookings;

    public void addRoom(int roomNum, String roomType, double price, boolean available, Date addedDate) {
        Room newRoom = new Room(roomNum, roomType, price, available, addedDate);
        rooms.add(newRoom);
    }

    public ArrayList<Room> checkRoomAvailability() {
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }
}
