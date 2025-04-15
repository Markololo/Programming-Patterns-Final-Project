package org.example;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class Client {
private int clientID;
    private String name;
    private String contact;
    private int numOfMembers;
}
