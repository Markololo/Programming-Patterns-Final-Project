package org.example;

import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Hotel {
    private String hotelName;
    private Date openingDate;
    private String location;
    private String owner;
}
