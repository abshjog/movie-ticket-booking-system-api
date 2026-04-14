package com.example.mdb.service;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.dto.UserUpdationRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface UserService {

    UserResponse addUser(@Valid UserRegistrationRequest user);

    Page<UserResponse> findAllUsers(int page, int size);

    UserResponse updateUser(String userId, @Valid UserUpdationRequest user, String authenticatedEmail);

    UserResponse softDeleteUser(String userId, String authenticatedEmail);
}
