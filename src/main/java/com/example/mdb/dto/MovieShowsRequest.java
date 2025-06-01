package com.example.mdb.dto;

import com.example.mdb.enums.ScreenType;

import java.time.LocalDate;

public record MovieShowsRequest(

        LocalDate date,
        String zoneId,
        ScreenType screenType,
        int size,
        int page
) {}
