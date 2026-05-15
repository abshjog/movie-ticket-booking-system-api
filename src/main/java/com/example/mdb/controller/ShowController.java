package com.example.mdb.controller;

import com.example.mdb.dto.*;
import com.example.mdb.service.ShowService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@CrossOrigin(origins = "*")
@RequestMapping("/shows")
public class ShowController {

    private final ShowService showService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping("/theaters/{theaterId}/screens/{screenId}")
    @PreAuthorize("hasAuthority('THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<ShowResponse>> addShow(
            @PathVariable String theaterId,
            @PathVariable String screenId,
            @Validated @RequestBody ShowRequest showRequest) {

        ShowResponse showResponse = showService.addShow(theaterId, screenId, showRequest);

        return responseBuilder.success(HttpStatus.OK, "Show successfully created", showResponse);
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<ResponseStructure<Page<TheaterShowProjection>>> fetchShows(
            @PathVariable String movieId,
            @ModelAttribute MovieShowsRequest showsRequest,
            @RequestHeader(value = "X-City", required = false, defaultValue = "Bhopal") String city) {

        Page<TheaterShowProjection> shows  = showService.fetchShows(movieId, showsRequest, city);
        return responseBuilder.success(HttpStatus.OK, "Fetched Successfully", shows);
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<ResponseStructure<List<SeatStatusResponse>>> getSeatAvailability(@PathVariable String showId) {
        return responseBuilder.success(
                HttpStatus.OK,
                "Seat availability fetched successfully",
                showService.getSeatAvailability(showId)
        );
    }
}
