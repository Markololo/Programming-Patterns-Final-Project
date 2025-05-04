package org.example;

import lombok.*;

/**
 * This is a subclass of the User class. It represents staff of the hotel.
 * In addition to the User class's fields, it stores the positon of the staff (e.g.: receptionist, manager,etc.).
 * Lombok is used to make classical methods like constructors, getters, setters, toString, etc.
 */
@Getter
@Setter
@ToString
public abstract class Staff extends User{
    private String position;

    public Staff(int id, String name, String position) {
        super(id, name);
        this.position = position;
    }
}
