package org.example;


import java.util.List;
import java.util.Scanner;

public class ClientController
{
    //****Why do we need HashMap for rooms?
    /*List<Room> rooms;

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
*/
    //Helper method to find a room based on the ID
    /*private Room findRoom(int roomID)
    {
        for (Room room : rooms)
        {
            if (room.getRoomNum() == roomID)
                return room;
        }
        return null;//null if no room is found
    }*/

    private void viewBookingHistory(Client client)
    {}

    private void sendBookingCancelRequest(Client client)
    {}
}

