package com.example.mdb.service.impl;

import com.example.mdb.dto.ChangePasswordRequest;
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

        UserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new AccessDeniedException("Security Alert: You can only update your own profile!");
        }

        if (!user.getEmail().equals(userRequest.email()) && userRepository.existsByEmail(userRequest.email())) {
            throw new EmailAlreadyExistsException("The new email is already taken by another user.");
        }

        log.info("Mapping updated data for user: {}", user.getEmail());
        user = copy(user, userRequest);

        return userMapper.userResponseMapper(user);
    }

    @Override
    public UserResponse softDeleteUser(String userId, String authenticatedEmail) {
        UserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

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
    public void changePassword(String userId, ChangePasswordRequest request, String authenticatedEmail) {
        log.info("Initiating password change for user ID: {}", userId);

        UserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new AccessDeniedException("Security Alert: You can only change your own password!");
        }

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password!");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the old password!");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Password successfully changed for user: {}", user.getEmail());
    }

    @Override
    public Page<UserResponse> findAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDetails> usersPage = userRepository.findAll(pageable);

        return usersPage.map(userMapper::userResponseMapper);
    }

    private UserDetails copy(UserDetails userEntity, UserRegistrationRequest request) {
        userEntity.setFullName(request.fullName()); // 👇 Mapping FullName
        userEntity.setUserRole(request.userRole());
        userEntity.setPassword(passwordEncoder.encode(request.password()));
        userEntity.setEmail(request.email());
        userEntity.setDateOfBirth(request.dateOfBirth());
        userEntity.setPhoneNumber(request.phoneNumber());
        userEntity.setUsername(request.username());
        userEntity.setDeleted(false);
        return userRepository.save(userEntity);
    }

    private UserDetails copy(UserDetails userEntity, UserUpdationRequest request) {
        userEntity.setFullName(request.fullName()); // 👇 Mapping FullName
        userEntity.setDateOfBirth(request.dateOfBirth());
        userEntity.setPhoneNumber(request.phoneNumber());
        userEntity.setEmail(request.email());
        userEntity.setUsername(request.username());
        return userRepository.save(userEntity);
    }
}


//1. User sends registration data → captured in `UserRegistrationRequest` (DTO)
//2. Service layer uses `copy()` to map DTO → Entity (`UserDetails`)
//3. Entity saved in DB.
//4. When sending response back → Entity converted to `UserResponse` via `UserMapper`
