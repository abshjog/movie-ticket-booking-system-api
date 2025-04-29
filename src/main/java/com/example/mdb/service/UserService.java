package com.example.mdb.service;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.dto.UserUpdationRequest;
import com.example.mdb.entity.UserDetails;
import jakarta.validation.Valid;

public interface UserService {

    UserResponse addUser(@Valid UserRegistrationRequest user);

    UserResponse updateUser(@Valid UserUpdationRequest user, String email);

    UserResponse softDeleteUser(String email);
}
