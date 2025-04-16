package org.example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StaffController {
    private List<Room> rooms;
    private List<Booking> bookings;


    /* adds room */
    public void addRoom(int roomNum, String type, double price, boolean availability, Date addedDate)
    {
        Room newRoom = new Room(roomNum, type, price, availability, addedDate);
        rooms.add(newRoom);
    }


    //returns list of available rooms
    /*public ArrayList<Room> checkRoomAvailability() {
        ArrayList<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }*/

    //book client with a specified room type
    public void book(Client client)
    {
        if(client != null)
        {

        }
    }



    //cancels booking
    public void cancelBooking(int bookingID)
    {

    }




}
