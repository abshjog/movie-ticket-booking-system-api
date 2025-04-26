package com.example.mdb.dto;

import com.example.mdb.entity.Seat;
import com.example.mdb.enums.ScreenType;
import lombok.Builder;

import java.util.List;

@Builder
public record ScreenResponse(

        String screenId,
        ScreenType screenType,
        Integer capacity,
        Integer noOfRows,
        List<Seat> seats
) {}