package com.example.mdb.mapper;

import com.example.mdb.dto.UserResponse;
import com.example.mdb.entity.UserDetails;

public class UserMapper {

    public static UserResponse toUserResponse(UserDetails user) {
        return new UserResponse(
                user.getUserId(),                     // The unique identifier for the user
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().name(),          // Converting enum to String
                user.getDateOfBirth(),
                user.getPhoneNumber()
        );
    }
}
