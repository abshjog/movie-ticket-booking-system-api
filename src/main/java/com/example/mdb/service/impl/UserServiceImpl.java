package com.example.mdb.service.impl;

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
    public UserDetails addUser(UserDetails user) {
        // Checking if the email is already registered.
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + user.getEmail() + " already exists");
        }

        // Create a new instance based on the user role.
        UserDetails newUser = createUserByRole(user);

        // Copying common user properties from the input 'user' to the new user instance.
        userProperties(user, newUser);

        // Saving and returning the new user account.
        return userRepository.save(newUser);
    }

    private UserDetails createUserByRole(UserDetails user) {
        if (user.getUserRole() == UserRole.USER) {
            return new User();
        } else if (user.getUserRole() == UserRole.THEATER_OWNER) {
            return new TheaterOwner();
        } else {
            throw new IllegalArgumentException("Unknown user role: " + user.getUserRole());
        }
    }

    private void userProperties(UserDetails source, UserDetails target) {
        target.setUserRole(source.getUserRole());
        target.setEmail(source.getEmail());
        target.setUsername(source.getUsername());
        target.setPassword(source.getPassword());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setDateOfBirth(source.getDateOfBirth());
        target.setPhoneNumber(source.getPhoneNumber());
    }
}
