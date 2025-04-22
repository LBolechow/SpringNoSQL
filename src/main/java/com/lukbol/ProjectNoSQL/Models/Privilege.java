package com.lukbol.ProjectNoSQL.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "privileges")
public class Privilege {
    @Id
    private String id;

    private String name;

    public Privilege() {}

    public Privilege(String name) {
        this.name = name;
    }
}
