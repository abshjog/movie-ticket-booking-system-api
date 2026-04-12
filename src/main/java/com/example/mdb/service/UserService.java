package com.example.mdb.service;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.dto.UserUpdationRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface UserService {

    UserResponse addUser(@Valid UserRegistrationRequest user);

    UserResponse updateUser(@Valid UserUpdationRequest user, String email);

    UserResponse softDeleteUser(String email);

    Page<UserResponse> findAllUsers(int page, int size);
}
