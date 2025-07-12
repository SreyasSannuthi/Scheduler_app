package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findByUserId(String userId);

    List<Appointment> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByUserIdAndStartTimeBetween(String userId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByCategory(String category);

    List<Appointment> findByStatus(String status);

    @Query("{ 'userId': ?0, $and: [ " +
            "{ 'startTime': { $lt: ?2 } }, " +
            "{ 'endTime': { $gt: ?1 } }, " +
            "{ 'status': 'scheduled' } ] }")
    List<Appointment> findCollision(String userId, LocalDateTime startTime, LocalDateTime endTime);

}