package com.example.mdb.mapper;

import com.example.mdb.dto.UserResponse;
import com.example.mdb.entity.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse userResponseMapper(UserDetails userDetails){
        if(userDetails == null)
            return null;

        return UserResponse.builder()
                .userId(userDetails.getUserId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .phoneNumber(userDetails.getPhoneNumber())
                .userRole(userDetails.getUserRole().name())
                .build();
    }
}
