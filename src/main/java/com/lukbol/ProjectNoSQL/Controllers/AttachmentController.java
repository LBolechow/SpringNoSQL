package com.lukbol.ProjectNoSQL.Controllers;

import com.lukbol.ProjectNoSQL.Models.Attachment;
import com.lukbol.ProjectNoSQL.Models.Task;
import com.lukbol.ProjectNoSQL.Services.AttachmentService;
import com.lukbol.ProjectNoSQL.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final TaskRepository taskRepository;

    @PostMapping("/attachment/upload")
    public ResponseEntity<Attachment> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("taskId") String taskId,
            @RequestParam(value = "comment", required = false) String comment
    ) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Attachment savedAttachment = attachmentService.uploadFile(file, task, comment);
        return ResponseEntity.ok(savedAttachment);
    }
    @GetMapping("/attachment/byTask/{taskId}")
    public ResponseEntity<List<Attachment>> getAttachmentsByTask(@PathVariable String taskId) {
        return ResponseEntity.ok(attachmentService.getAttachmentsByTaskId(taskId));
    }

    @GetMapping("/attachment/getAll")
    public List<Attachment> getAllAttachments() {
        return attachmentService.getAllAttachments();
    }

    @GetMapping("/attachment/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String id) throws IOException {
        Attachment attachment = attachmentService.getAttachmentById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        InputStreamResource resource = new InputStreamResource(
                attachmentService.downloadFileStream(attachment.getGridFsFileId())
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .body(resource);
    }

    @DeleteMapping("/attachment/delete/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable String id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}
