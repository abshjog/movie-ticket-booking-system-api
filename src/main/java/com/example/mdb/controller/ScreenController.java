package com.example.mdb.controller;

import com.example.mdb.dto.ScreenRequest;
import com.example.mdb.dto.ScreenResponse;
import com.example.mdb.service.ScreenService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/theaters/{theaterId}/screens")
@CrossOrigin(origins = "*")
public class ScreenController {

    private final ScreenService screenService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping
    @PreAuthorize("hasAuthority('THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<ScreenResponse>> addScreen(
            @Valid @RequestBody ScreenRequest screenRequest,
            @PathVariable String theaterId) {
        ScreenResponse screenResponse = screenService.addScreen(screenRequest, theaterId);
        return responseBuilder.success(HttpStatus.OK, "Screen has been successfully created", screenResponse);
    }

    @GetMapping("/{screenId}")
    public ResponseEntity<ResponseStructure<ScreenResponse>> findScreen(
            @PathVariable String theaterId,
            @PathVariable String screenId) {
        ScreenResponse screenResponse = screenService.findScreen(theaterId, screenId);
        return responseBuilder.success(HttpStatus.OK, "Screen has been successfully fetched", screenResponse);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<ScreenResponse>>> getScreensByTheater(
            @PathVariable String theaterId) {
        List<ScreenResponse> responses = screenService.getScreensByTheater(theaterId);
        return responseBuilder.success(HttpStatus.OK, "Screens retrieved successfully", responses);
    }
}
