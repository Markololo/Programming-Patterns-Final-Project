package org.example;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
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
//    private boolean isActive;


}//If time allows, add buttons to update records
