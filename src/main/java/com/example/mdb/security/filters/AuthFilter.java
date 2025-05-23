package com.example.mdb.security.filters;

import com.example.mdb.enums.auth.TokenType;
import com.example.mdb.security.jwt.AuthenticatedTokenDetails;
import com.example.mdb.security.jwt.ExtractedToken;
import com.example.mdb.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenType tokenType;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isValid(header)) {
            log.debug("Authenticating request...");
            String token = header.contains("Bearer ") ? header.substring(7) : header;

            var extractedToken = jwtService.parseToken(token);
            Claims claims = Optional.ofNullable(extractedToken)
                    .map(ExtractedToken::claims)
                    .orElse(null);

            JwsHeader headers = Optional.ofNullable(extractedToken)
                    .map(ExtractedToken::headers)
                    .orElse(null);

            boolean correctType = Optional.ofNullable(headers)
                    .map(h -> (String) h.get("type"))
                    .map(String::toUpperCase)
                    .map(TokenType::valueOf)
                    .map(t -> {
                        var result = t.equals(tokenType);
                        log.debug("The token is of type: {}, validation result: {}", t.name(), result);
                        return result;
                    })
                    .orElse(false);

            if (!correctType || claims == null) filterChain.doFilter(request, response);

            String role = claims.get("role", String.class);
            String email = claims.getSubject();
            log.debug("Token found, extracted claims, email/username; {}, role: {}", email, role);

            if (isValid(role) && isValid(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
                var authorities = List.of(new SimpleGrantedAuthority(role));
                var authToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                authToken.setDetails(request);

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("User '{}' - authenticated.", email);

                AuthenticatedTokenDetails tokenDetails = new AuthenticatedTokenDetails(
                        email,
                        role,
                        claims.getExpiration().toInstant(),
                        token);

                request.setAttribute("tokenDetails", tokenDetails);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValid(String s) {
        return s != null && !s.isBlank();
    }
}
