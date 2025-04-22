package com.lukbol.ProjectNoSQL.Models;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Getter
@Setter
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String username;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String password;
    private boolean activated;

    @DBRef
    private List<Role> roles;

    public User() {}

    public User(String name, String surname, String email, String phoneNumber, String password, String username, Boolean activated) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.username = username;
        this.activated = activated;
    }
}
