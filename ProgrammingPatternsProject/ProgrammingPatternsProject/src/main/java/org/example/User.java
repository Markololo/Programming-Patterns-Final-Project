package org.example;

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