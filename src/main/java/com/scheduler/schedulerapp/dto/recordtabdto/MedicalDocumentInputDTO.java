package com.scheduler.schedulerapp.dto.recordtabdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MedicalDocumentInputDTO {
    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "File name too long")
    private String fileName;

    @NotBlank(message = "File content is required")
    private String fileContentBase64;

    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be greater than 0")
    private Integer fileSize;

    @NotBlank(message = "MIME type is required")
    private String mimeType;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}