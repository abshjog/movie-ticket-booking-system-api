package com.example.mdb.controller;

import com.example.mdb.dto.LoginRequest;
import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.dto.auth.AuthResponse;
import com.example.mdb.security.jwt.AuthenticatedTokenDetails;
import com.example.mdb.service.AuthService;
import com.example.mdb.service.UserService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<UserResponse>> register(@RequestBody @Valid UserRegistrationRequest user){
        UserResponse userDetails = userService.addUser(user);
        return responseBuilder.success(HttpStatus.CREATED, "Account created successfully", userDetails);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody LoginRequest loginRequest){
        AuthResponse authResponse = authService.login(loginRequest);
        return responseBuilder.success(HttpStatus.OK, "Login Successful", authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseStructure<AuthResponse>> refresh(HttpServletRequest request){
        AuthenticatedTokenDetails tokenDetails = (AuthenticatedTokenDetails) request.getAttribute("tokenDetails");
        AuthResponse authResponse = authService.refresh(tokenDetails);
        return responseBuilder.success(HttpStatus.OK, "Token Refreshed", authResponse);
    }
}
