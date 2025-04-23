package com.example.mdb.service.impl;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserRequest;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.User;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.enums.UserRole;
import com.example.mdb.exception.EmailAlreadyExistsException;
import com.example.mdb.repository.UserRepository;
import com.example.mdb.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails registerUser(UserRegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new EmailAlreadyExistsException("Email " + registrationRequest.email() + " already exists");
        }

        UserRole role;
        try {
            role = UserRole.valueOf(registrationRequest.userRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user role: " + registrationRequest.userRole());
        }

        UserDetails newUser = createUserEntity(registrationRequest, role);
        return userRepository.save(newUser);
    }

    private UserDetails createUserEntity(UserRegistrationRequest request, UserRole role) {
        UserDetails newUser = role == UserRole.THEATER_OWNER ? new TheaterOwner() : new User();
        newUser.setUserRole(role);
        newUser.setEmail(request.email());
        newUser.setUsername(request.username());
        newUser.setPassword(request.password()); // Remember: hash passwords before persisting
        newUser.setPhoneNumber(request.phoneNumber());
        newUser.setDateOfBirth(request.dateOfBirth());
        return newUser;
    }

    @Override
    public UserDetails updateUser(String email, UserRequest userRequest) {
        // Retrieve the existing user using the provided email.
        Optional<UserDetails> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }
        UserDetails existingUser = optionalUser.get();

        // Map the allowed (non-sensitive) update fields from UserRequest.
        existingUser.setUsername(userRequest.username());
        existingUser.setPhoneNumber(userRequest.phoneNumber());
        existingUser.setEmail(userRequest.email()); // Replace the email with the new one

        // Persist the changes and return the updated entity.
        return userRepository.save(existingUser);
    }

    @Override
    public void softDeleteUser(String userId) {
        UserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setDeleted(true);  // Use setDeleted() instead of setIsDeleted()
        user.setDeletedAt(Instant.now());
        userRepository.save(user);
    }
}
