package com.example.mdb.dto;

import java.util.List;

public record GroupedShowsByTheaterResponse(

        TheaterResponse theater,
        List<ShowResponse> shows
) {}
