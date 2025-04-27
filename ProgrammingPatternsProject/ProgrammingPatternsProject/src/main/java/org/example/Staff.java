package org.example;

import lombok.*;

@Getter
@Setter
@ToString
public class Staff extends User{
    private String position;

    public Staff(int id, String name, String position) {
        super(id, name);
        this.position = position;
    }
}
