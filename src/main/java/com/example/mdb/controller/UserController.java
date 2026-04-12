package com.example.mdb.controller;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.dto.UserUpdationRequest;
import com.example.mdb.service.UserService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final RestResponseBuilder restResponseBuilder;

    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<UserResponse>> addUser(@RequestBody @Valid UserRegistrationRequest user){
        UserResponse userDetails = userService.addUser(user);
        return restResponseBuilder.success(HttpStatus.CREATED,"New User account has been successfully created", userDetails);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseStructure<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserResponse> users = userService.findAllUsers(page, size);
        return restResponseBuilder.success(HttpStatus.OK, "Users fetched successfully", users);
    }

    @PutMapping("/users/me")
    @PreAuthorize("hasAnyAuthority('USER', 'THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<UserResponse>> updateUser(Authentication auth, @RequestBody @Valid UserUpdationRequest user){
        String email = auth.getName();
        UserResponse userDetails = userService.updateUser(user, email);
        return restResponseBuilder.success(HttpStatus.OK,"User Details have been updated successfully", userDetails);
    }

    @DeleteMapping("/users/me")
    @PreAuthorize("hasAnyAuthority('USER', 'THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<UserResponse>> softDeleteUser(Authentication auth){
        String email = auth.getName();
        UserResponse userDetails = userService.softDeleteUser(email);
        return restResponseBuilder.success(HttpStatus.OK,"User account has been successfully deleted", userDetails);
    }
}
