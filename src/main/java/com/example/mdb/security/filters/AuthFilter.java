package com.example.mdb.security.filters;

import com.example.mdb.enums.auth.TokenType;
import com.example.mdb.security.jwt.AuthenticatedTokenDetails;
import com.example.mdb.security.jwt.ExtractedToken;
import com.example.mdb.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
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
            String token = header.contains("Bearer ") ? header.substring(7) : header;

            try {
                var extractedToken = jwtService.parseToken(token);
                if (extractedToken == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Claims claims = extractedToken.claims();
                JwsHeader headers = extractedToken.headers();

                boolean correctType = Optional.ofNullable(headers)
                        .map(h -> (String) h.get("type"))
                        .map(String::toUpperCase)
                        .map(TokenType::valueOf)
                        .map(t -> t.equals(tokenType))
                        .orElse(false);

                if (!correctType || claims == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String role = claims.get("role", String.class);
                String email = claims.getSubject();

                if (isValid(role) && isValid(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var authorities = List.of(new SimpleGrantedAuthority(role));
                    var authToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    authToken.setDetails(request);

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    AuthenticatedTokenDetails tokenDetails = new AuthenticatedTokenDetails(
                            email, role, claims.getExpiration().toInstant(), token);

                    request.setAttribute("tokenDetails", tokenDetails);
                }
            } catch (ExpiredJwtException e) {
                log.error("JWT Expired: {}", e.getMessage());
                sendErrorResponse(response, "Token has expired. Please login again.");
                return;
            } catch (JwtException e) {
                log.error("JWT Invalid: {}", e.getMessage());
                sendErrorResponse(response, "Invalid Token. Access Denied.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private boolean isValid(String s) {
        return s != null && !s.isBlank();
    }
}
