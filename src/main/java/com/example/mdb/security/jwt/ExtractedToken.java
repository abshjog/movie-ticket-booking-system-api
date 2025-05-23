package com.example.mdb.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;

public record ExtractedToken(

        JwsHeader headers,
        Claims claims
) {}
