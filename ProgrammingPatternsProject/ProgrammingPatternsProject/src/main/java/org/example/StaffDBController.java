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

//book client with a specified room type and bookingID
public void book(Client client, int  bookingID, String desiredRoomType) {
    if (client == null) {
        System.out.println("Client is null.");
        return;
    }

    for (Room room : rooms) {
        if (room.isAvailable() && room.getRoomType().equalsIgnoreCase(desiredRoomType)) {
            // found a matching available room
            room.setAvailable(false);

            Booking booking = new Booking(
                    bookingID,
                    new Date(),
                    new Date(System.currentTimeMillis() + 86400000), // +1 day
                    client.getId()
            );

            //add to list of hotel bookings
            bookings.add(booking);

            //adds to the clients list of personal booking
            client.getBookings().add(booking);

          //  Marks that the client is currently in the hotel.
            client.setInHotel(true);

            System.out.println("Booking successful: Client = " + client.getName() + ", Room = " + room.getRoomNum());
            return;
        }
    }
    System.out.println("No available room of type: " + desiredRoomType);

}


//cancels booking
public void cancelBooking(int bookingID)
{
    for (Booking booking : bookings)
    {
        if (booking.getBookingNum() == bookingID)
        {
            bookings.remove(booking);
            for (Room room : rooms)
            {
                if (!room.isAvailable())

                    room.setAvailable(true);
                    break;
                }
            }
            System.out.println("Booking canceled.");
            return;
        }
    System.out.println("Booking ID not found.");

}
}




