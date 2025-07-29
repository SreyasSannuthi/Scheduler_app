package com.scheduler.schedulerapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "hospitalBranches")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalBranch {

    @Id
    private String id;
    private String branchCode;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String email;
    private String phoneNumber;
    private LocalDateTime startedAt;
    private String closedAt;
    private Boolean isActive = true;
}