package com.example.mdb.service.impl;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.dto.UserUpdationRequest;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.User;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.exception.EmailAlreadyExistsException;
import com.example.mdb.exception.UserNotFoundException;
import com.example.mdb.mapper.UserMapper;
import com.example.mdb.repository.UserRepository;
import com.example.mdb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@AllArgsConstructor
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse addUser(UserRegistrationRequest user) {
        if (userRepository.existsByEmail(user.email()))
            throw new EmailAlreadyExistsException("User with the entered Email already exists");

        UserDetails userDetails = switch (user.userRole()) {
            case USER -> copy(new User(), user);
            case THEATER_OWNER -> copy(new TheaterOwner(), user);
        };
        return userMapper.userResponseMapper(userDetails);
    }

    @Override
    public UserResponse updateUser(String userId, UserUpdationRequest userRequest, String authenticatedEmail) {
        log.info("Editing user with ID: {}", userId);

        // 1. Pehle user ko ID se dhoondo
        UserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // 2. Security Check: Kya ye user apni hi profile edit kar raha hai?
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new AccessDeniedException("Security Alert: You can only update your own profile!");
        }

        // 3. Email Conflict Check: Agar email badal raha hai toh check karo naya email unique hai ya nahi
        if (!user.getEmail().equals(userRequest.email()) && userRepository.existsByEmail(userRequest.email())) {
            throw new EmailAlreadyExistsException("The new email is already taken by another user.");
        }

        log.info("Mapping updated data for user: {}", user.getEmail());
        user = copy(user, userRequest);

        return userMapper.userResponseMapper(user);
    }

    @Override
    public UserResponse softDeleteUser(String userId, String authenticatedEmail) {
        // 1. Fetch User
        UserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // 2. Security Check
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new AccessDeniedException("Security Alert: You cannot delete someone else's account!");
        }

        user.setDeleted(true);
        user.setDeletedAt(Instant.now());
        userRepository.save(user);

        log.info("User account soft-deleted: {}", userId);
        return userMapper.userResponseMapper(user);
    }

    @Override
    public Page<UserResponse> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDetails> usersPage = userRepository.findAll(pageable);

        // Mapper use karke consistency banaye rakhte hain
        return usersPage.map(userMapper::userResponseMapper);
    }

    // Helper method for Registration
    private UserDetails copy(UserDetails userEntity, UserRegistrationRequest request) {
        userEntity.setUserRole(request.userRole());
        userEntity.setPassword(passwordEncoder.encode(request.password()));
        userEntity.setEmail(request.email());
        userEntity.setDateOfBirth(request.dateOfBirth());
        userEntity.setPhoneNumber(request.phoneNumber());
        userEntity.setUsername(request.username());
        userEntity.setDeleted(false);
        return userRepository.save(userEntity);
    }

    // Helper method for Updates
    private UserDetails copy(UserDetails userEntity, UserUpdationRequest request) {
        userEntity.setDateOfBirth(request.dateOfBirth());
        userEntity.setPhoneNumber(request.phoneNumber());
        userEntity.setEmail(request.email());
        userEntity.setUsername(request.username());
        // Note: Deleted status ko nahi chhedna chahiye update mein
        return userRepository.save(userEntity);
    }
}


//1. User sends registration data → captured in `UserRegistrationRequest` (DTO)
//2. Service layer uses `copy()` to map DTO → Entity (`UserDetails`)
//3. Entity saved in DB.
//4. When sending response back → Entity converted to `UserResponse` via `UserMapper`
