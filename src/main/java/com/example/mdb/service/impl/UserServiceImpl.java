package com.example.mdb.service.impl;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.dto.UserResponse;
import com.example.mdb.dto.UserUpdationRequest;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.User;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.exception.EmailAlreadyExistsException;
import com.example.mdb.exception.UserNotFoundByEmailException;
import com.example.mdb.mapper.UserMapper;
import com.example.mdb.repository.UserRepository;
import com.example.mdb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public UserResponse updateUser(UserUpdationRequest userRequest, String email) {
        log.info("editing user...");
        if (userRepository.existsByEmail(email)) {
            UserDetails user = userRepository.findByEmail(email);
            log.info("user is unique");
            if (! user.getEmail().equals(userRequest.email()) && userRepository.existsByEmail(userRequest.email())){
                throw new EmailAlreadyExistsException("User with the entered email already exists");
            }

            log.info("mapping data...");
            user = copy(user, userRequest);

            return userMapper.userResponseMapper(user);
        }
        throw new UserNotFoundByEmailException("Email not found in the Database");
    }

    @Override
    public UserResponse softDeleteUser(String email) {
        if (userRepository.existsByEmail(email)) {
            UserDetails user = userRepository.findByEmail(email);
            user.setDeleted(true);
            user.setDeletedAt(Instant.now());
            userRepository.save(user);
            return userMapper.userResponseMapper(user);
        }
        throw new UserNotFoundByEmailException("Email not found in the Database");
    }

    @Override
    public Page<UserResponse> findAllUsers(int page, int size) {
        // 1. Pageable object banao (Konsa page, kitna bada page)
        Pageable pageable = PageRequest.of(page, size);

        // 2. Repo se Page of Entities uthao
        Page<UserDetails> usersPage = userRepository.findAll(pageable);

        // 3. Entity ko Response DTO mein map karo (Java 8 streams magic)
        return usersPage.map(user -> UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userRole(user.getUserRole().name())
                .phoneNumber(user.getPhoneNumber())
                .build());
    }

    private UserDetails copy(UserDetails userRole, UserRegistrationRequest user) {
        userRole.setUserRole(user.userRole());
        userRole.setPassword(passwordEncoder.encode(user.password()));
        userRole.setEmail(user.email());
        userRole.setDateOfBirth(user.dateOfBirth());
        userRole.setPhoneNumber(user.phoneNumber());
        userRole.setUsername(user.username());
        userRole.setDeleted(false);
        userRepository.save(userRole);
        return userRole;
    }

    private UserDetails copy(UserDetails userRole, UserUpdationRequest user) {
        userRole.setDateOfBirth(user.dateOfBirth());
        userRole.setPhoneNumber(user.phoneNumber());
        userRole.setEmail(user.email());
        userRole.setUsername(user.username());
        userRole.setDeleted(false);
        userRepository.save(userRole);
        return userRole;
    }
}


//1. User sends registration data → captured in `UserRegistrationRequest` (DTO)
//2. Service layer uses `copy()` to map DTO → Entity (`UserDetails`)
//3. Entity saved in DB.
//4. When sending response back → Entity converted to `UserResponse` via `UserMapper`
