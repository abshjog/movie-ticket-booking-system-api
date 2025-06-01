package com.example.mdb.controller;

import com.example.mdb.dto.MovieShowsRequest;
import com.example.mdb.dto.ShowResponse;
import com.example.mdb.dto.TheaterShowProjection;
import com.example.mdb.service.ShowService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Validated
public class ShowController {

    private final ShowService showService;
    private final RestResponseBuilder responseBuilder;

    @PostMapping("theaters/{theaterId}/screens/{screenId}/shows")
    @PreAuthorize("hasAuthority('THEATER_OWNER')")
    public ResponseEntity<ResponseStructure<ShowResponse>> addShow(@PathVariable String theaterId, @PathVariable String screenId, String movieId , @NotNull Long startTime, @NotNull String zoneId ){
        ShowResponse showResponse = showService.addShow(theaterId, screenId, movieId, startTime, zoneId);
        return responseBuilder.success(HttpStatus.OK, "Show successfully created", showResponse);
    }

    @GetMapping("movies/{movieId}/shows")
    public ResponseEntity<ResponseStructure<Page<TheaterShowProjection>>> fetchShows(
            @PathVariable String movieId,
            @RequestBody MovieShowsRequest showsRequest,
            @RequestHeader(value = "X-City", required = false)  String city // Header for city
    ) {
        Page<TheaterShowProjection> shows  = showService.fetchShows(movieId, showsRequest, city);
        return responseBuilder.success(HttpStatus.OK, "Fetched Successfully", shows);
    }
}
