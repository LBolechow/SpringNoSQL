package com.lukbol.ProjectNoSQL.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document(collection = "projects")
public class Project {
    @Id
    private String id;

    private String name;
    private String description;

    @DBRef
    private User owner;

    @DBRef
    private List<User> members;

    @DBRef
    private List<Task> tasks;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

