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
