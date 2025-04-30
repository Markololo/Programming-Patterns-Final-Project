package org.example;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
