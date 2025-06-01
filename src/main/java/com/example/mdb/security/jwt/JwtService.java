package com.example.mdb.security.jwt;

import com.example.mdb.config.AppEnv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class JwtService {

    private final AppEnv env;

    public String createJwtToken(TokenPayLoad tokenPayload) {
        return Jwts.builder()
                .setHeaderParam("type", tokenPayload.TokenType().name().toLowerCase())
                .setClaims(tokenPayload.claims())
                .setSubject(tokenPayload.subject())
                .setIssuedAt(new Date(tokenPayload.issuedAt().toEpochMilli()))
                .setExpiration(new Date(tokenPayload.expiration().toEpochMilli()))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public ExtractedToken parseToken (String token){
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token);

            JwsHeader header = claimsJws.getHeader();

            Claims claimsBody = claimsJws.getBody();

            ExtractedToken extractedToken = new ExtractedToken(header, claimsBody);

            return extractedToken;
        } catch (JwtException e) {
            log.warn("Failed to parse token, returning null..", e);
            return null;
        }
    }

    private Key getSignatureKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(env.getToken().getSecret()));
    }
}
