package com.example.mdb.dto;

import com.example.mdb.enums.Certificate;
import com.example.mdb.enums.Genre;
import lombok.Builder;

import java.time.Duration;
import java.util.Set;

@Builder
public record MovieResponse(

        String movieId,
        String title,
        String description,
        String ratings,
        Duration runtime,
        Certificate certificate,
        Genre genre,
        Set<String> castList
) {}
