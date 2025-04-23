package com.example.mdb.service;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserRequest;
import com.example.mdb.entity.UserDetails;
import jakarta.validation.Valid;

public interface UserService {

    // UserDetails addUser(UserDetails userDetails);
    UserDetails registerUser(UserRegistrationRequest registrationRequest);

    UserDetails updateUser(String email, @Valid UserRequest userRequest);

    void softDeleteUserByEmail(String email);
}
