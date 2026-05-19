package com.example.mdb.service;

import com.example.mdb.dto.TheaterRequest;
import com.example.mdb.dto.TheaterResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface TheaterService {


    TheaterResponse addTheater(String email, @Valid TheaterRequest theaterRequest);

    TheaterResponse findTheater(String theaterId);

    TheaterResponse updateTheater(String theaterId, @Valid TheaterRequest theaterRequest);

    List<TheaterResponse> getMyTheaters(String name);
}
