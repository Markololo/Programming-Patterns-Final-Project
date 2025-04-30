package org.example;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Room {
    private int roomNum;
    private String roomType;
    private double price;//Price per night
    private String isAvailable;
    private String addedDate;

    /**
     * Gets the size of the room base on the type.
     * "Single", "Double", "Twin", "Queen", "Suite" have respectively maximum capacities 1, 2, 2, 2, 4.
     * @return the maximum number of members allowed in the room. If the room type is invalid, 0 is returned.
     */
    public int getSize() {
        switch (roomType){
            case "Single" -> {
                return 1;
            }
            case "Double", "Queen", "Twin" -> {
                return 2;
            }
            case "Suite" -> {
                return 4;
            }
            case "Big Family" -> {
                return 10;//The hotel will give the family a big suite or many smaller rooms.
                // Any number bigger than this requires at least 2 clients responsible.
            }
            default -> {
                return 0;
            }
        }
    }
}
