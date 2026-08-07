package com.example.mdb.service.impl;

import com.example.mdb.config.AppEnv;
import com.example.mdb.dto.LoginRequest;
import com.example.mdb.dto.OtpSendRequest;
import com.example.mdb.dto.OtpVerifyRequest;
import com.example.mdb.dto.auth.AuthResponse;
import com.example.mdb.entity.OtpVerification;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.enums.auth.TokenType;
import com.example.mdb.exception.InvalidOtpException;
import com.example.mdb.exception.UserNotFoundException;
import com.example.mdb.mapper.auth.AuthMapper;
import com.example.mdb.repository.OtpVerificationRepository;
import com.example.mdb.repository.UserRepository;
import com.example.mdb.security.SecurityConfig;
import com.example.mdb.security.jwt.AuthenticatedTokenDetails;
import com.example.mdb.security.jwt.JwtService;
import com.example.mdb.security.jwt.TokenPayLoad;
import com.example.mdb.service.AuthService;
import com.example.mdb.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final SecurityConfig securityConfig;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final UserRepository userRepository;

    private final OtpVerificationRepository otpVerificationRepository;
    private final NotificationService notificationService;
    private final AppEnv env;

    public AuthResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(token);

        if (!authentication.isAuthenticated())
            throw new UsernameNotFoundException("Invalid Login Details");

        UserDetails userDetails = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + authentication.getName()));

        TokenPayLoad access = tokenGenerator(userDetails, env.getToken().getAccessDuration(), TokenType.ACCESS);
        TokenPayLoad refresh = tokenGenerator(userDetails, env.getToken().getRefreshDuration(), TokenType.REFRESH);

        String accessToken = jwtService.createJwtToken(access);
        String refreshToken = jwtService.createJwtToken(refresh);

        return authMapper.authResponseMapper(userDetails, access, refresh, accessToken, refreshToken);
    }

    @Override
    public AuthResponse refresh(AuthenticatedTokenDetails tokenDetails) {
        UserDetails user = userRepository.findByEmail(tokenDetails.email())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + tokenDetails.email()));

        TokenPayLoad access = tokenGenerator(user, env.getToken().getAccessDuration(), TokenType.ACCESS);
        String accessToken = jwtService.createJwtToken(access);

        return new AuthResponse(
                user.getUserId(),
                user.getFullName(),
                user.getUsername(),
                tokenDetails.email(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                tokenDetails.role(),
                access.expiration().toEpochMilli(),
                tokenDetails.tokenExpiration().toEpochMilli(),
                accessToken,
                tokenDetails.currentToken()
        );
    }

    @Override
    @Transactional
    public String sendOtp(OtpSendRequest request) {
        String email = request.email();

        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No account found with this email: " + email));

        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        String otpCode = String.valueOf(otp);

        otpVerificationRepository.findByEmail(email).ifPresent(otpVerificationRepository::delete);

        OtpVerification otpVerification = OtpVerification.builder()
                .email(email)
                .otpCode(otpCode)
                .expiryTime(Instant.now().plus(5, ChronoUnit.MINUTES))
                .build();

        otpVerificationRepository.save(otpVerification);

        notificationService.sendOtpEmail(email, otpCode);

        log.info("OTP generated and saved for email: {}", email);
        return "OTP sent successfully to your registered email address.";
    }

    @Override
    @Transactional
    public AuthResponse verifyOtpAndLogin(OtpVerifyRequest request) {
        String email = request.email();

        OtpVerification otpVerification = otpVerificationRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidOtpException("No active OTP request found for this email."));

        if (!otpVerification.getOtpCode().equals(request.otp())) {
            throw new InvalidOtpException("Incorrect OTP. Please try again.");
        }

        if (Instant.now().isAfter(otpVerification.getExpiryTime())) {
            otpVerificationRepository.delete(otpVerification);
            throw new InvalidOtpException("OTP has expired. Please request a new one.");
        }

        otpVerificationRepository.delete(otpVerification);

        UserDetails userDetails = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        TokenPayLoad access = tokenGenerator(userDetails, env.getToken().getAccessDuration(), TokenType.ACCESS);
        TokenPayLoad refresh = tokenGenerator(userDetails, env.getToken().getRefreshDuration(), TokenType.REFRESH);

        String accessToken = jwtService.createJwtToken(access);
        String refreshToken = jwtService.createJwtToken(refresh);

        log.info("User {} successfully logged in via OTP", email);
        return authMapper.authResponseMapper(userDetails, access, refresh, accessToken, refreshToken);
    }

    private TokenPayLoad tokenGenerator(UserDetails userDetails, long expirationValue, TokenType tokenType){
        Map<String, Object> claims = new HashMap<>();

        String role = userDetails.getUserRole().name();
        claims.put("role", role);

        long addMillis = expirationValue;
        if (expirationValue < 100000L) {
            addMillis = expirationValue * 60L * 1000L;
        }

        return new TokenPayLoad(
                claims,
                userDetails.getEmail(),
                Instant.now(),
                Instant.now().plusMillis(addMillis),
                tokenType
        );
    }
}