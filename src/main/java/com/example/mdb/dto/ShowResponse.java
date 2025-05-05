package com.example.mdb.dto;

import java.time.Instant;

public record ShowResponse(

        String showId,
        Instant startsAt,
        Instant endsAt
) {}
