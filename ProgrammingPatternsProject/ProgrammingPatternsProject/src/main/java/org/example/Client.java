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
    private List<Booking> bookings;
}
