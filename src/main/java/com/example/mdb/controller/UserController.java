package com.example.mdb.controller;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.service.UserService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RestResponseBuilder restResponseBuilder;


    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<UserDetails>> registerUser(
            @Valid @RequestBody UserRegistrationRequest registrationRequest) {
        UserDetails savedUser = userService.registerUser(registrationRequest);
        return restResponseBuilder.success(HttpStatus.CREATED, "User created successfully", savedUser);
    }
}
