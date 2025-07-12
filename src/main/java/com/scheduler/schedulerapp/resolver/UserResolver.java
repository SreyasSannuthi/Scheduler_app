package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.UserResponseDTO;
import com.scheduler.schedulerapp.mapper.DTOMapper;
import com.scheduler.schedulerapp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserResolver {

    @Autowired
    private UserService userService;

    @Autowired
    private DTOMapper dtoMapper;

    @QueryMapping
    public List<UserResponseDTO> users() {
        return userService.getAllUsers().stream()
                .map(dtoMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public UserResponseDTO user(@Argument String id) {
        return userService.getUserById(id)
                .map(dtoMapper::toUserResponseDTO)
                .orElse(null);
    }

    @QueryMapping
    public List<UserResponseDTO> usersByRole(@Argument String role) {
        return userService.getUsersByRole(role).stream()
                .map(dtoMapper::toUserResponseDTO)
                .collect(Collectors.toList());
    }
}
