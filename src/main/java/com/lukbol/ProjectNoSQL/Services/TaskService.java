package com.lukbol.ProjectNoSQL.Services;

import com.lukbol.ProjectNoSQL.Models.Task;
import com.lukbol.ProjectNoSQL.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
    public List<Task> getTasksByProjectId(String projectId) {
        return taskRepository.findByProject_Id(projectId);
    }
    public Optional<Task> updateTask(String id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setPriority(updatedTask.getPriority());
            task.setDueDate(updatedTask.getDueDate());
            task.setAssignedTo(updatedTask.getAssignedTo());
            task.setProject(updatedTask.getProject());
            task.setAttachments(updatedTask.getAttachments());
            task.setTags(updatedTask.getTags());
            task.setUpdatedAt(LocalDateTime.now());
            return taskRepository.save(task);
        });
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}
