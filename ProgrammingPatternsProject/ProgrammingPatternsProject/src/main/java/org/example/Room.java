package org.example;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Room {
    private int roomNum;;
    private String roomType;
    private double price;//Price per night
    private boolean available;
    private Date addedDate;
}
