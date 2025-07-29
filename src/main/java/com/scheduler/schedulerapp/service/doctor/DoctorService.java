package com.scheduler.schedulerapp.service.doctor;

import com.scheduler.schedulerapp.model.HospitalStaff;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<HospitalStaff> getAllDoctors();
    Optional<HospitalStaff> getDoctorById(String id);
    List<HospitalStaff> getDoctorsByRole(String role);
    HospitalStaff createDoctor(HospitalStaff doctor);
    HospitalStaff updateDoctor(String id, HospitalStaff doctor);
    void deleteDoctor(String id);
}
