package org.example;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a controller to restrict the actions that the clients can make (different from the staff controller)
 * Guests can search for available rooms based on type (single, double, suite, etc.) or price,
 * request a room, and view their booking history
 */
public class ClientDBController
{
    List<Room> rooms;

    public boolean requestRoom(int roomID)
    {
        //First, check if it exists and if it's available
        Room requestedRoom = findRoom(roomID);
        if (requestedRoom != null && requestedRoom.isAvailable()) {

            return true;
        }
        return false;
    }

    private boolean roomExists(int roomID)
    {
        return true;
    }

    //Helper method to find a room based on the ID
    private Room findRoom(int roomID)
    {
        for (Room room : rooms)
        {
            if (room.getRoomNum() == roomID)
                return room;
        }
        return null;//null if no room is found
    }

    private List<Booking> viewBookingHistory(Client client)
    {
        ArrayList<Booking> bookings = new ArrayList<>();
        return bookings;
    }

    /**
     * Cancels a booking for a client.
     * @param client
     * @return
     */
    private boolean cancelBooking(Client client)
    {

        return true;
    }
}

