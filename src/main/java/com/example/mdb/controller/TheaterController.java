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
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("theaters/{theaterId}")
    public ResponseEntity<ResponseStructure<TheaterResponse>> findTheater(@PathVariable String theaterId){
        TheaterResponse theaterResponse = theaterService.findTheater(theaterId);
        return responseBuilder.success(HttpStatus.OK, "Theater details successfully fetched", theaterResponse);
    }

    // To fetch all theatres.
//    @GetMapping("theaters")
//    public ResponseEntity<ResponseStructure<List<TheaterResponse>>> findAllTheaters(){
//        List<TheaterResponse> theaterList = theaterService.findAllTheaters();
//        return responseBuilder.success(HttpStatus.OK, "All theaters fetched successfully", theaterList);
//    }

    @PutMapping("/theaters/{theaterId}")
    public ResponseEntity<ResponseStructure<TheaterResponse>> updateTheater(@PathVariable String theaterId, @Valid @RequestBody TheaterRegistrationRequest registrationRequest){
        TheaterResponse theaterResponse = theaterService.updateTheater(theaterId, registrationRequest);
        return responseBuilder.success(HttpStatus.OK, "Theater details have been updated successfully.", theaterResponse);
    }
}
