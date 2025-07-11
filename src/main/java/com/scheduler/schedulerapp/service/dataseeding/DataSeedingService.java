package com.scheduler.schedulerapp.service.dataseeding;

import com.scheduler.schedulerapp.model.User;
import com.scheduler.schedulerapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DataSeedingService {

    @Autowired
    private UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {
        seedUsers();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            List<User> users = Arrays.asList(
                    new User(null, "Sreyas Sannuthi", "admin@scheduler.in", "admin", true),

                    new User(null, "Vibhor Gupta", "vibhor@scheduler.in", "user", true),
                    new User(null, "Arnav Saharan", "arnav@scheduler.in", "user", true),
                    new User(null, "Mayank Pal", "mayank@scheduler.in", "user", true),
                    new User(null, "Shubradip Saha", "shubradip@scheduler.in", "user", true),
                    new User(null, "Prateek Jain", "prateek@scheduler.in", "user", true),
                    new User(null, "Yash Goyal", "yash@scheduler.in", "user", true),
                    new User(null, "Ravi Kumar", "ravi@scheduler.in", "user", true),
                    new User(null, "Ankit Sharma", "ankit@scheduler.in", "user", true),
                    new User(null, "Yuvaraj Singh", "neha@scheduler.in", "user", true),
                    new User(null, "Rohit Verma", "rohit@scheduler.in", "user", true)
            );

            userRepository.saveAll(users);
            System.out.println( users.size() + " users added successfully");

            users.forEach(user ->
                    System.out.println(user.getName() + " (" + user.getRole() + ") - " + user.getEmail())
            );
        } else {
            System.out.println("Users already exist");
        }
    }
}
