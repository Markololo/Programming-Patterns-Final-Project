package org.example;

/**
 * This is a superclass to make more specific classes
 * such as the Client class and the Staff class.
 * It contains the id field to identify each object and the name field.
 * Lombok is used to make classical methods like constructors, getters, setters, toString, etc.
 */
public abstract class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

//    for accessing in booking class the id
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}