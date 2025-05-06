package com.example.mdb.controller;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.dto.UserUpdationRequest;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.mapper.UserMapper;
import com.example.mdb.service.UserService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final RestResponseBuilder restResponseBuilder;

    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<UserResponse>> addUser(@RequestBody @Valid UserRegistrationRequest user){
        UserResponse userDetails = userService.addUser(user);
        return restResponseBuilder.success(HttpStatus.OK,"New User account has been successfully created", userDetails);
    }

    @PutMapping("/users/{email}")
    public ResponseEntity<ResponseStructure<UserResponse>> updateUser(@PathVariable String email, @RequestBody @Valid UserUpdationRequest user){
        UserResponse userDetails = userService.updateUser(user, email);
        return restResponseBuilder.success(HttpStatus.OK,"User Details have been updated successfully", userDetails);
    }

    @DeleteMapping("/users/{email}")
    public ResponseEntity<ResponseStructure<UserResponse>> softDeleteUser(@PathVariable String email){
        UserResponse userDetails = userService.softDeleteUser(email);
        return restResponseBuilder.success(HttpStatus.OK,"User account has been successfully deleted", userDetails);
    }
}
