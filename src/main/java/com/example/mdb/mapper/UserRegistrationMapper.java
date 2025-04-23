package com.example.mdb.mapper;

import com.example.mdb.dto.UserRegistrationRequest;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.User;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {

// UserController will accept the DTO and passes it directly to the UserService.
// UserService is responsible for mapping the DTO into the proper entity before persisting it.
    public UserDetails toEntity(UserRegistrationRequest dto) {
        // Convert the `userRole` string from the DTO to the enum type.
        // Assuming that an enum like UserRole.THEATER_OWNER and UserRole.USER exist.
        UserRole role = UserRole.valueOf(dto.userRole().toUpperCase());

        if (role == UserRole.THEATER_OWNER) {
            TheaterOwner owner = new TheaterOwner();
            owner.setUsername(dto.username());
            owner.setEmail(dto.email());
            owner.setPassword(dto.password());
            owner.setDateOfBirth(dto.dateOfBirth());
            owner.setPhoneNumber(dto.phoneNumber());
            owner.setUserRole(role);
            return owner;
        } else {
            User user = new User();
            user.setUsername(dto.username());
            user.setEmail(dto.email());
            user.setPassword(dto.password());
            user.setDateOfBirth(dto.dateOfBirth());
            user.setPhoneNumber(dto.phoneNumber());
            user.setUserRole(role);
            return user;
        }
    }
}
