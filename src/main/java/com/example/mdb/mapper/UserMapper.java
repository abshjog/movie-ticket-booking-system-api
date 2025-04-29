package com.example.mdb.mapper;

import com.example.mdb.dto.UserResponse;
import com.example.mdb.entity.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserResponse toUserResponse(UserDetails user) {

        if(user == null)
            return null;
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().name(),
                user.getPhoneNumber()
        );
    }
}
