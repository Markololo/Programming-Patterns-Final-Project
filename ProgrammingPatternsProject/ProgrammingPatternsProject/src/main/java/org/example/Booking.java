package org.example;

import lombok.*;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Booking {
    private int bookingNum;
    private int clientId;
}
