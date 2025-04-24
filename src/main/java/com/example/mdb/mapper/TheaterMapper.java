package com.example.mdb.mapper;

import com.example.mdb.dto.TheaterResponse;
import com.example.mdb.entity.Theater;
import org.springframework.stereotype.Component;

@Component
public class TheaterMapper {

    public TheaterResponse theaterResponseMapper(Theater theater) {
        if (theater == null)
            return null;
        return new TheaterResponse(
                theater.getTheaterId(),
                theater.getName(),
                theater.getAddress(),
                theater.getCity(),
                theater.getLandmark()
        );
    }
}


// 1. UUID.randomUUID().toString():
//This generates a universally unique identifier (UUID). A UUID is a 128-bit value used to uniquely identify information.
//The .toString() method converts this UUID object into its string representation.

// 2.setTheaterId:
// This seems to be a method of an object named theater.
// The purpose of this method is to set the unique identifier (ID) for the theater.

// 3. theater.setTheaterId(UUID.randomUUID().toString());:
// This line assigns a unique identifier as a string to the theater object by calling its setTheaterId method.
// Each time this line is executed, a new unique ID will be generated and assigned.