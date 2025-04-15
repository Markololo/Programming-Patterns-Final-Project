package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * This is a controller to restrict the actions that the clients can make (different from the staff controller)
 * Guests can search for available rooms based on type (single, double, suite, etc.) or price,
 * request a room, and view their booking history
 */
public class ClientController extends Controller
{
    //****Why do we need HashMap for rooms?
    List<Room> rooms;

    public boolean requestRoom(int roomID)
    {
        //First, check if it exists and if it's available
        Room requestedRoom = findRoom(roomID);
        if (requestedRoom != null && requestedRoom.isAvailable()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Hotel staff, do you accept booking request? ");
            // TODO: Notification for the staff that there is a booking request
        }
        return true;
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

    private void viewBookingHistory(Client client)
    {}

    private void sendBookingCancelRequest(Client client)
    {}
}

