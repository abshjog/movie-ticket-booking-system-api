package com.example.mdb.service.impl;

import com.example.mdb.dto.MovieResponse;
import com.example.mdb.entity.Feedback;
import com.example.mdb.entity.Movie;
import com.example.mdb.exception.MovieNotFoundException;
import com.example.mdb.mapper.MovieMapper;
import com.example.mdb.repository.MovieRepository;
import com.example.mdb.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    public MovieResponse fetchMovie(String movieId) {
        // Step 1: Fetch with Optional handling
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with ID: " + movieId));

        List<Feedback> feedbacks = movie.getFeedbacks();
        double avgRatings = (feedbacks != null && !feedbacks.isEmpty())
                ? feedbacks.stream().mapToDouble(Feedback::getRating).average().orElse(0.0)
                : 0.0;

        return movieMapper.movieResponseMapper(movie, avgRatings);
    }

    @Override
    public List<MovieResponse> searchMovies(String search, int page, int size) {
        if (search == null || search.isBlank()) return Collections.emptyList();

        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepository.findByTitleContainingIgnoreCase(search, pageable);

        return new ArrayList<>(movieMapper.movieResponseMapper(moviePage.getContent()));
    }
}
