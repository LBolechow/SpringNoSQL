package com.lukbol.ProjectNoSQL.Services;

import com.lukbol.ProjectNoSQL.Models.Attachment;
import com.lukbol.ProjectNoSQL.Models.Task;
import com.lukbol.ProjectNoSQL.Repositories.AttachmentRepository;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private AttachmentRepository attachmentRepository;

    public Attachment uploadFile(MultipartFile file, Task task, String comment) throws IOException {
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setGridFsFileId(fileId.toString());
        attachment.setTask(task);
        attachment.setComment(comment);
        attachment.setUploadedAt(LocalDateTime.now());

        return attachmentRepository.save(attachment);
    }

    public Optional<Attachment> getAttachmentById(String id) {
        return attachmentRepository.findById(id);
    }

    public GridFSFile getFileByGridFsId(String gridFsFileId) {
        return gridFsOperations.findOne(
                org.springframework.data.mongodb.core.query.Query.query(
                        org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new ObjectId(gridFsFileId))
                )
        );
    }
    public List<Attachment> getAttachmentsByTaskId(String taskId) {
        return attachmentRepository.findByTask_Id(taskId);
    }

    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll();
    }

    public void deleteAttachment(String id) {
        attachmentRepository.findById(id).ifPresent(attachment -> {
            gridFsTemplate.delete(
                    org.springframework.data.mongodb.core.query.Query.query(
                            org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new ObjectId(attachment.getGridFsFileId()))
                    )
            );
            attachmentRepository.deleteById(id);
        });
    }

    public InputStream downloadFileStream(String gridFsFileId) throws IOException {
        GridFSFile file = getFileByGridFsId(gridFsFileId);
        return gridFsOperations.getResource(file).getInputStream();
    }
}
