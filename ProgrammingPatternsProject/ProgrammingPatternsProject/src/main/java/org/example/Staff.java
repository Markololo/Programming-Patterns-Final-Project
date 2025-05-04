package org.example;

import lombok.*;

@Getter
@Setter
@ToString
/**
 * This is a subclass of the User class. It represents staff of the hotel.
 * In addition to the User class's fields, it stores the positon of the staff (e.g.: receptionist, manager,etc.).
 * Lombok is used to make classical methods like constructors, getters, setters, toString, etc.
 */
public abstract class Staff extends User{
    private String position;

    public Staff(int id, String name, String position) {
        super(id, name);
        this.position = position;
    }
}
