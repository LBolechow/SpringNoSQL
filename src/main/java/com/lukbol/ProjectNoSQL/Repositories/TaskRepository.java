package com.lukbol.ProjectNoSQL.Repositories;

import com.lukbol.ProjectNoSQL.Models.Task;
import com.lukbol.ProjectNoSQL.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByProject_Id(String projectId);
}
