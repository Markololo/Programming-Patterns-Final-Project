package org.example;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Client {
private int clientID;
    private String name;
    private String contact;
    private int numOfMembers;
    private boolean isInHotel;//Whether the client is still in the hotel or if he checked out.
    private List<Booking> bookings;//All client bookings
}
