package com.example.mdb.controller;

import com.example.mdb.entity.UserDetails;
import com.example.mdb.service.UserService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseStructure<UserDetails>> createUser(@RequestBody UserDetails userDetails) {
        UserDetails savedUser = userService.addUser(userDetails);
        return RestResponseBuilder.success(HttpStatus.CREATED, "User created successfully", savedUser);
    }
}
