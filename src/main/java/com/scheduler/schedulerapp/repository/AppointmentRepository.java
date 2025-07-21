package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByDoctorId(String doctorId);

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByDoctorIdAndStartTimeBetween(String doctorId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatientIdAndStartTimeBetween(String patientId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByStatus(String status);

    @Query("{ 'doctorId': ?0, $and: [ " +
            "{ 'startTime': { $lt: ?2 } }, " +
            "{ 'endTime': { $gt: ?1 } }, " +
            "{ 'status': 'scheduled' } ] }")
    List<Appointment> findDoctorCollision(String doctorId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("{ 'patientId': ?0, $and: [ " +
            "{ 'startTime': { $lt: ?2 } }, " +
            "{ 'endTime': { $gt: ?1 } }, " +
            "{ 'status': 'scheduled' } ] }")
    List<Appointment> findPatientCollision(String patientId, LocalDateTime startTime, LocalDateTime endTime);


}