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
@Document(collection = "attachments")
public class Attachment {

    @Id
    private String id;

    private String fileName;
    private String fileType;
    private String gridFsFileId;

    private String comment;

    @DBRef
    private Task task;

    private LocalDateTime uploadedAt;
}
