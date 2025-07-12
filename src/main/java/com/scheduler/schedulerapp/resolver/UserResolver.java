package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.model.User;
import com.scheduler.schedulerapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class UserResolver {

    @Autowired
    private UserRepository userRepository;

    @QueryMapping
    public List<User> users() {
        return userRepository.findAll();
    }

    @QueryMapping
    public Optional<User> user(@Argument String id) {
        return userRepository.findById(id);
    }

    @QueryMapping
    public List<User> usersByRole(@Argument String role) {
        return userRepository.findByRole(role);
    }
}