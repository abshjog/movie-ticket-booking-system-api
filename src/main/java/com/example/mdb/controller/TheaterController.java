package com.example.mdb.controller;

import com.example.mdb.dto.TheaterRegistrationRequest;
import com.example.mdb.dto.TheaterResponse;
import com.example.mdb.service.TheaterService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class TheaterController {

    private final TheaterService theaterService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping("/theaters")
    public ResponseEntity<ResponseStructure<TheaterResponse>> addTheater(String email, @Valid @RequestBody TheaterRegistrationRequest theaterRegistrationRequest){
        TheaterResponse theaterResponse = theaterService.addTheater(email, theaterRegistrationRequest);
        return responseBuilder.success(HttpStatus.OK, "Theater has been created successfully.", theaterResponse);
    }
}
