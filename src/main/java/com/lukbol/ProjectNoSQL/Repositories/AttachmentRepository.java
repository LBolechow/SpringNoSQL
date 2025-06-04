package com.lukbol.ProjectNoSQL.Repositories;

import com.lukbol.ProjectNoSQL.Models.Attachment;
import com.lukbol.ProjectNoSQL.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AttachmentRepository extends MongoRepository<Attachment, String> {

    List<Attachment> findByTask_Id(String taskId);
}
