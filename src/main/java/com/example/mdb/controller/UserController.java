package com.example.mdb.controller;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserRequest;
import com.example.mdb.dto.UserResponse;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RestResponseBuilder restResponseBuilder;

    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<String>> registerUser(
            @Valid @RequestBody UserRegistrationRequest registrationRequest) {
        // UserService will process the registration (and typically validate if the email exists, etc.)
        userService.registerUser(registrationRequest);
        return restResponseBuilder.success(
                HttpStatus.CREATED,
                "User registered successfully",
                "User with email " + registrationRequest.email() + " registered successfully"
        );
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseStructure<UserResponse>> updateUserProfile(
            @RequestParam("email") String email,   // current email to locate the user
            @Valid @RequestBody UserRequest userRequest) {
        UserDetails updatedUser = userService.updateUser(email, userRequest);
        UserResponse userResponse = UserMapper.toUserResponse(updatedUser);
        return restResponseBuilder.success(HttpStatus.OK, "User profile updated successfully", userResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseStructure<String>> deleteUser(@RequestParam("email") String email) {
        userService.softDeleteUserByEmail(email);
        return restResponseBuilder.success(
                HttpStatus.OK,
                "User account soft deleted successfully",
                "User with email " + email + " has now been deleted"
        );
    }
}
