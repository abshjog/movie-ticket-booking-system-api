package com.example.mdb.service.impl;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.User;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.enums.UserRole;
import com.example.mdb.exception.EmailAlreadyExistsException;
import com.example.mdb.repository.UserRepository;
import com.example.mdb.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public UserDetails registerUser(UserRegistrationRequest registrationRequest) {
        // 1. Check if the email is already registered.
        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new EmailAlreadyExistsException("Email " + registrationRequest.email() + " already exists");
        }

        // 2. Convert the user role string to an enum safely.
        UserRole role;
        try {
            role = UserRole.valueOf(registrationRequest.userRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user role: " + registrationRequest.userRole());
        }

        // 3. Create the correct entity type based on role.
        UserDetails newUser = role == UserRole.THEATER_OWNER ? new TheaterOwner() : new User();

        // 4. Map the DTO properties into the new entity.
        newUser.setUserRole(role);
        newUser.setEmail(registrationRequest.email());
        newUser.setUsername(registrationRequest.username());
        newUser.setPassword(registrationRequest.password()); // Consider hashing the password for production.
        newUser.setPhoneNumber(registrationRequest.phoneNumber());
        newUser.setDateOfBirth(registrationRequest.dateOfBirth());

        // 5. Save and return the newly registered user.
        return userRepository.save(newUser);
    }
}
