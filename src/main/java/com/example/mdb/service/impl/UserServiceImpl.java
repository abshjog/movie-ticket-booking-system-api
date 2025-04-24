package com.example.mdb.service.impl;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.User;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.enums.UserRole;
import com.example.mdb.exception.EmailAlreadyExistsException;
import com.example.mdb.exception.UserNotFoundByEmailException;
import com.example.mdb.mapper.UserMapper;
import com.example.mdb.repository.UserRepository;
import com.example.mdb.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails registerUser(UserRegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new EmailAlreadyExistsException("Email " + registrationRequest.email() + " already exists");
        }

        // Directly using the enum value from the registrationRequest
        UserRole userRole = registrationRequest.userRole();

        UserDetails newUser = createUserEntity(registrationRequest, userRole);
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
        UserDetails existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // Update permitted fields from UserRequest.
        existingUser.setUsername(userRequest.username());
        existingUser.setPhoneNumber(userRequest.phoneNumber());
        existingUser.setEmail(userRequest.email()); // Replace the email with the new one

        // Persist the changes and return the updated entity.
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public UserResponse softDeleteUser(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundByEmailException("Email not found in the Database");
        }

        // Since we know the user exists, fetch the user
        UserDetails user = userRepository.findByEmail(email);

        // Encapsulate the soft-delete behavior within the entity
        user.setDeleted(true);
        user.setDeletedAt(Instant.now());

        // Save the updated user using repository save
        UserDetails updatedUser = userRepository.save(user);

        // Build the response directly without relying on an external mapper
        return new UserResponse(
                updatedUser.getUserId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getUserRole().toString(),
                updatedUser.getDateOfBirth(),
                updatedUser.getPhoneNumber()
        );
    }
}