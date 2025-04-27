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
    private Date startDate;
    private Date endDate;
    private int clientId;
}//If time allows, add buttons to update records
