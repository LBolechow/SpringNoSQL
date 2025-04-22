package com.lukbol.ProjectNoSQL.Models;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Getter
@Setter
@Document(collection = "roles")
public class Role {
    @Id
    private String id;

    private String name;

    @DBRef
    private List<Privilege> privileges;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }
}
