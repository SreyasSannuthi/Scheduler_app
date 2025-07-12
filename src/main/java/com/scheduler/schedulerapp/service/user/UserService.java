package com.scheduler.schedulerapp.service.user;

import com.scheduler.schedulerapp.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(String id);
    List<User> getUsersByRole(String role);
    User createUser(User user);
    User updateUser(String id, User user);
    void deleteUser(String id);
}
