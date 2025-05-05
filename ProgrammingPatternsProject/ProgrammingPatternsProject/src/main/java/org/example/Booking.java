package org.example;

import lombok.*;

import java.util.Date;

/**
 * A model class to represent the bookings.
 * It includes the booking number, the ID of the client that is booking,
 * the number of the booked room, and the dates the beginning and end of the booking.
 * If the booking ended before starting, it means it was canceled and the client should not be billed.
 * Lombok is used to make classical methods like constructors, getters, setters, toString, etc.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Booking {
    private int bookingNum;
    private int clientId;
    private int roomNum;
    private String startDate;
    private String endDate;
}
