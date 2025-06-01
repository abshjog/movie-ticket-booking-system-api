package com.example.mdb.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ShowResponse(

        String showId,
        Instant startsAt,
        Instant endsAt,
        String screenId, com.example.mdb.enums.ScreenType screenType
) {}
