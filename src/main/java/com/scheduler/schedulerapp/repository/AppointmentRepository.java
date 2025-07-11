package com.scheduler.schedulerapp.repository;

import com.scheduler.schedulerapp.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    // Find appointments by user
    List<Appointment> findByUserId(String userId);

    // Find appointments by date range
    List<Appointment> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find appointments by user and date range
    List<Appointment> findByUserIdAndStartTimeBetween(String userId, LocalDateTime start, LocalDateTime end);

    // Find appointments by category
    List<Appointment> findByCategory(String category);

    // Find appointments by status
    List<Appointment> findByStatus(String status);

    // Find overlapping appointments for collision detection
    @Query("{ 'userId': ?0, $and: [ " +
            "{ 'startTime': { $lt: ?2 } }, " +
            "{ 'endTime': { $gt: ?1 } }, " +
            "{ 'status': 'scheduled' } ] }")
    List<Appointment> findCollision(String userId, LocalDateTime startTime, LocalDateTime endTime);

    // Count appointments by user
    long countByUserId(String userId);
}