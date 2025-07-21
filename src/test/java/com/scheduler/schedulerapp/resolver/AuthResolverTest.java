package com.scheduler.schedulerapp.resolver;

import com.scheduler.schedulerapp.dto.AuthRequestDTO;
import com.scheduler.schedulerapp.dto.AuthResponseDTO;
import com.scheduler.schedulerapp.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthResolverTest {

    @Mock
    private AuthService authService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthResolver authResolver;

    @BeforeEach
    void init() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        String username = "john.doe";
        String password = "secret";
        AuthResponseDTO expected = new AuthResponseDTO("jwt-token", username, "ADMIN", "Login successful");

        when(authService.authenticate(any(AuthRequestDTO.class))).thenReturn(expected);

        AuthResponseDTO actual = authResolver.login(username, password);

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(authService).authenticate(
                argThat(req -> req.getUsername().equals(username) &&
                        req.getPassword().equals(password)));
    }

    @Test
    void login_ShouldPropagateException_WhenAuthServiceFails() {
        when(authService.authenticate(any(AuthRequestDTO.class)))
                .thenThrow(new RuntimeException("authentication failed"));

        assertThrows(RuntimeException.class,
                () -> authResolver.login("user", "pwd"));
    }

    @Test
    void getCurrentUser_ShouldReturnUsername_WhenAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john.doe");

        assertEquals("john.doe", authResolver.getCurrentUser());
    }

    @Test
    void getCurrentUser_ShouldReturnNull_WhenAnonymous() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        assertNull(authResolver.getCurrentUser());
    }

    @Test
    void getCurrentUser_ShouldReturnNull_WhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertNull(authResolver.getCurrentUser());
    }

    @Test
    void getCurrentUser_ShouldReturnNull_WhenAuthenticationIsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertNull(authResolver.getCurrentUser());
    }

    @Test
    void getCurrentUserRole_ShouldReturnRole_WhenAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john.doe");
        when(authentication.getAuthorities())
                .thenReturn((Collection) Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        assertEquals("ADMIN", authResolver.getCurrentUserRole());
    }

    @Test
    void getCurrentUserRole_ShouldReturnNull_ForAnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        assertNull(authResolver.getCurrentUserRole());
    }

    @Test
    void getCurrentUserRole_ShouldReturnNull_WhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertNull(authResolver.getCurrentUserRole());
    }

    @Test
    void getCurrentUserRole_ShouldReturnNull_WhenAuthenticationIsNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertNull(authResolver.getCurrentUserRole());
    }

    @Test
    void logout_ShouldAlwaysReturnTrue() {
        assertTrue(authResolver.logout());
    }
}
