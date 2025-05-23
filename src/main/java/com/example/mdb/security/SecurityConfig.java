package com.example.mdb.security;

import com.example.mdb.enums.auth.TokenType;
import com.example.mdb.security.filters.AuthFilter;
import com.example.mdb.security.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Order(2)
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**");

        http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/register", "/login")
                .permitAll()
                .anyRequest()
                .authenticated());

        setDefault(new AuthFilter(jwtService, TokenType.ACCESS), http);

        return http.build();
    }

    @Order(1)
    @Bean
    SecurityFilterChain refreshFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/refresh/**");

        http.authorizeHttpRequests(auth -> auth
                .anyRequest()
                .authenticated());

        setDefault(new AuthFilter(jwtService, TokenType.REFRESH), http);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    private HttpSecurity setDefault(AuthFilter authFilter, HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        return http;
    }
}
