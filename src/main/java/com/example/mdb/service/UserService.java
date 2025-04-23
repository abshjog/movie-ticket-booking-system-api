package com.example.mdb.service;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.entity.UserDetails;

public interface UserService {

    // UserDetails addUser(UserDetails userDetails);
    UserDetails registerUser(UserRegistrationRequest registrationRequest);
}
