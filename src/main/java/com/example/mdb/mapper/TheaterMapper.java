package com.example.mdb.mapper;

import com.example.mdb.dto.TheaterResponse;
import com.example.mdb.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {

    public TheaterResponse theaterResponseMapper(Theater theater) {
        if (theater == null)
            return null;

        return TheaterResponse.builder()
                .theaterId(theater.getTheaterId())
                .name(theater.getName())
                .address(theater.getAddress())
                .city(theater.getCity())
                .landmark(theater.getLandmark())
                .build();
    }
}
