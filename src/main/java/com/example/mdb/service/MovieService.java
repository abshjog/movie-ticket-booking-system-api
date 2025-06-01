package com.example.mdb.service;

import com.example.mdb.dto.MovieResponse;

import java.util.Set;

public interface MovieService {

    MovieResponse fetchMovie(String movieId);

    Set<MovieResponse> searchMovies(String search);
}
