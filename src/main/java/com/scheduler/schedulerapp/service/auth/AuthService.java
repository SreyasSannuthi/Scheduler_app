package com.scheduler.schedulerapp.service.auth;

import com.scheduler.schedulerapp.dto.AuthRequestDTO;
import com.scheduler.schedulerapp.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO authenticate(AuthRequestDTO request);

    String getCurrentUserId();
    String getCurrentUserName();
}