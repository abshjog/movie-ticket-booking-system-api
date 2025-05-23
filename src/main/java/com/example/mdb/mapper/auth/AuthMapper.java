package com.example.mdb.mapper.auth;

import com.example.mdb.dto.auth.AuthResponse;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.security.jwt.TokenPayLoad;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse authResponseMapper(UserDetails userDetails, TokenPayLoad access, TokenPayLoad refresh, String accessToken, String refreshToken) {

        return AuthResponse.builder()
                .userId(userDetails.getUserId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .role(userDetails.getUserRole().toString())
                .accessExpiration(access.expiration().toEpochMilli())
                .refreshExpiration(refresh.expiration().toEpochMilli())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshToken(refreshToken)
                .build();
    }
}
