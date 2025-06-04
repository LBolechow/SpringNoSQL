package com.lukbol.ProjectNoSQL.Services;

import com.lukbol.ProjectNoSQL.Models.Project;
import com.lukbol.ProjectNoSQL.Repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(String id) {
        return projectRepository.findById(id);
    }

    public Project createProject(Project project) {
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    public Optional<Project> updateProject(String id, Project updatedProject) {
        return projectRepository.findById(id).map(project -> {
            project.setName(updatedProject.getName());
            project.setDescription(updatedProject.getDescription());
            project.setOwner(updatedProject.getOwner());
            project.setMembers(updatedProject.getMembers());
            project.setTasks(updatedProject.getTasks());
            project.setUpdatedAt(LocalDateTime.now());
            return projectRepository.save(project);
        });
    }

    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }
}
