package com.example.mdb.service;

import com.example.mdb.dto.MovieResponse;

public interface MovieService {

    MovieResponse fetchMovie(String movieId);
}
