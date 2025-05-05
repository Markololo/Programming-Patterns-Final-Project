
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
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is a subclass of the User class. It inherits the user class's name and id fields, and adds
 * its own fields  for the contact information, the party size of the user and isInHotel to check
 * if the user is in the hotel currently.
 * Lombok is used to make classical methods like constructors, getters, setters, toString, etc.
 */
@ToString
@Getter
@Setter
public class Client extends User{//make abstract class user as parent
    private String contact;
    private int numOfMembers;
    private String isInHotel;//Whether the client is still in the hotel or if he checked out.
//    private List<Booking> bookings;//All client bookings

    public Client(int id, String name, String contact, int numOfMembers, String isInHotel) {
        super(id, name);
        this.contact = contact;
        this.numOfMembers = numOfMembers;
        this.isInHotel = isInHotel;
//        bookings = new ArrayList<Booking>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return numOfMembers == client.numOfMembers && isInHotel.equalsIgnoreCase(client.isInHotel) && Objects.equals(contact, client.contact) /*&& Objects.equals(bookings, client.bookings)*/;
    }
}
