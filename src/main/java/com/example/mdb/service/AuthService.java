package com.example.mdb.service;

import com.example.mdb.dto.LoginRequest;
import com.example.mdb.dto.auth.AuthResponse;
import com.example.mdb.security.jwt.AuthenticatedTokenDetails;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse refresh(AuthenticatedTokenDetails tokenDetails);
}
