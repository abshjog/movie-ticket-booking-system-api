package com.example.mdb.mapper;

import com.example.mdb.dto.TheaterRequest;
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

    public Theater mapToEntity(TheaterRequest request) {
        if (request == null) return null;

        Theater theater = new Theater();
        theater.setName(request.name());
        theater.setAddress(request.address());
        theater.setCity(request.city());
        theater.setLandmark(request.landmark());

        return theater;
    }
}
