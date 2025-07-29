package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.AuthRequestDTO;
import com.scheduler.schedulerapp.dto.AuthResponseDTO;
import com.scheduler.schedulerapp.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class AuthResolver {

    @Autowired
    private AuthService authService;

    @MutationMapping
    public AuthResponseDTO login(@Argument String email, @Argument String password) {
        // Login should work without authentication
        AuthRequestDTO request = new AuthRequestDTO(email, password);
        return authService.authenticate(request);
    }

    @QueryMapping
    public String getCurrentUser() {
        // This requires authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return auth.getName();
        }
        throw new SecurityException("Authentication required");
    }

    @QueryMapping
    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        }
        throw new SecurityException("Authentication required");
    }

    @MutationMapping
    public Boolean logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return true;
        }
        throw new SecurityException("Authentication required");
    }
}