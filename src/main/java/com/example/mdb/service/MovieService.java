package com.example.mdb.service;

import com.example.mdb.dto.MovieResponse;

import java.util.List;
import java.util.Set;

public interface MovieService {

    MovieResponse fetchMovie(String movieId);

    List<MovieResponse> searchMovies(String search, int page, int size);
}
