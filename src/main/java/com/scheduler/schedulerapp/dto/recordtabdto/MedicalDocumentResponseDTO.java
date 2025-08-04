package com.scheduler.schedulerapp.dto.recordtabdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalDocumentResponseDTO {
    private String documentId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String uploadDate;
    private String uploadedBy;
    private String description;
}
