package com.example.mdb.mapper;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.User;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {

    public UserDetails toEntity(UserRegistrationRequest registrationRequest) {
        // Directly obtain the userRole from the registrationRequest.
        UserRole userRole = registrationRequest.userRole();

        if (userRole == UserRole.THEATER_OWNER) {
            TheaterOwner owner = new TheaterOwner();
            owner.setUsername(registrationRequest.username());
            owner.setEmail(registrationRequest.email());
            owner.setPassword(registrationRequest.password());
            owner.setDateOfBirth(registrationRequest.dateOfBirth());
            owner.setPhoneNumber(registrationRequest.phoneNumber());
            owner.setUserRole(userRole);
            return owner;
        } else {
            User user = new User();
            user.setUsername(registrationRequest.username());
            user.setEmail(registrationRequest.email());
            user.setPassword(registrationRequest.password());
            user.setDateOfBirth(registrationRequest.dateOfBirth());
            user.setPhoneNumber(registrationRequest.phoneNumber());
            user.setUserRole(userRole);
            return user;
        }
    }
}
