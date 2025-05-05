/*
// -------------------------------------------------------
Final Project
Written by: Mariam Salim #6229528 and Meerab Khan #2388387

Mariam's Part:
-Helped with Deliverable 1 and made a GitHub repository
-Made db file with Meerab
-Made the Singleton for the database (db)
-Made the Factory for language switching
-Made the model classes' content
-Made most of the DBManager's methods (tables and methods related to the database)
-Made the Staff Window(layout, controls, methods,
 translation and handling error messages with a popup depending on the methods' return from DBManager)
-Made the "Resource Bundle 'messages'" with translations
-Made the window switching from staff window to client window.
-Made the final report


Meerab's part:
-Most of Deliverable 1
-Made db file with Mariam
-Created model classes' structure (initiate java classes)
-Made the sign in window and its controls and connected it to the client window
-Added controls to the client window and added some GUI methods
-Made Junit testing
-Made login table with client ID and password (TODO)
-Handled login feature (TODO: to complete)
-Made the final report

// For "Programming Patterns" Section 2- Winter 2025
// --------------------------------------------------------
- General explanation:
The Hotel Management System is a small application that allows hotel staff (receptionists and managers)
and guests to interact with the system. Receptionists can add new room details, check room availability,
books rooms, check guests in and out, and view booking records. Guests can search for available rooms based
on type (single, double, suite, etc.) or price, book a room, and view their booking history.
 */

package org.example;

import lombok.*;
import java.util.Date;

/**
 * This class represents the rooms. It's a model class.
 * It is identified by the room number that is unique.
 * In addition, stores the room type, the price per night,
 * the availability(True/False), and the date when the room was added.
 * Lombok is used to make classical methods like constructors, getters, setters, toString, etc.
 */
@AllArgsConstructor
@NoArgsConstructor
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
