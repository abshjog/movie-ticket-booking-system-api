package com.example.mdb.controller;

import com.example.mdb.dto.TheaterRequest;
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
    public ResponseEntity<ResponseStructure<TheaterResponse>> addTheater(String email, @Valid @RequestBody TheaterRequest theaterRequest){
        TheaterResponse theaterResponse = theaterService.addTheater(email, theaterRequest);
        return responseBuilder.success(HttpStatus.OK, "Theater created successfully", theaterResponse);
    }

    @GetMapping("theaters/{theaterId}")
    public ResponseEntity<ResponseStructure<TheaterResponse>> findTheater(@PathVariable String theaterId){
        TheaterResponse theaterResponse = theaterService.findTheater(theaterId);
        return responseBuilder.success(HttpStatus.OK, "Theater fetched successfully", theaterResponse);
    }

    @PutMapping("/theaters/{theaterId}")
    public ResponseEntity<ResponseStructure<TheaterResponse>> updateTheater(@PathVariable String theaterId, @Valid @RequestBody TheaterRequest theaterRequest){
        TheaterResponse theaterResponse = theaterService.updateTheater(theaterId, theaterRequest);
        return responseBuilder.success(HttpStatus.OK, "Theater updated successfully", theaterResponse);
    }
}
