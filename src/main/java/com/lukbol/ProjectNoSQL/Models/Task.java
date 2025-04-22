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
@Document(collection = "tasks")
public class Task {

    public enum TaskStatus {
        TODO, IN_PROGRESS, DONE
    }

    @Id
    private String id;

    private String title;
    private String description;
    private TaskStatus status;
    private int priority;

    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @DBRef
    private List<User> assignedTo;

    @DBRef
    private Project project;

    @DBRef
    private List<Attachment> attachments;

    private List<String> tags;
}