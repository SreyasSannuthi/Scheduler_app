package com.scheduler.schedulerapp.model.recordtab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalDocument {
    private String documentId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private String uploadedBy;
    private String description;
}