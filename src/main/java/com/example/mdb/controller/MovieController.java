package com.example.mdb.controller;

import com.example.mdb.dto.MovieResponse;
import com.example.mdb.service.MovieService;
import com.example.mdb.utility.ResponseStructure;
import com.example.mdb.utility.RestResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;
    private final RestResponseBuilder responseBuilder;

    @GetMapping("/{movieId}")
    public ResponseEntity<ResponseStructure<MovieResponse>> fetchMovie(@PathVariable String movieId){
        MovieResponse movieResponse = movieService.fetchMovie(movieId);
        return responseBuilder.success(HttpStatus.OK, "Movie has been successfully fetched", movieResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseStructure<List<MovieResponse>>> searchMovies(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<MovieResponse> movieResponses = movieService.searchMovies(search, page, size);
        return responseBuilder.success(HttpStatus.OK, "Movies fetched Successfully", movieResponses);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<MovieResponse>>> fetchAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<MovieResponse> movieResponses = movieService.fetchAllMovies(page, size);
        return responseBuilder.success(HttpStatus.OK, "All movies fetched successfully", movieResponses);
    }
}
