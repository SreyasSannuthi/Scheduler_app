package com.scheduler.schedulerapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

@Document(collection = "doctorBranchMappings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(def = "{'doctorId': 1, 'branchId': 1}", unique = true)
public class DoctorBranchMapping {

    @Id
    private String id;
    private String doctorId;
    private String branchId;
    private String doctorName;
    private String branchCode;
}