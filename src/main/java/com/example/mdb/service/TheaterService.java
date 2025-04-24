package com.example.mdb.service;

import com.example.mdb.dto.TheaterRegistrationRequest;
import com.example.mdb.dto.TheaterResponse;
import jakarta.validation.Valid;

public interface TheaterService {

    TheaterResponse addTheater(String email, @Valid TheaterRegistrationRequest theaterRegistrationRequest);
}
