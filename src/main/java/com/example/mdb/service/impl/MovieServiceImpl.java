package com.example.mdb.service.impl;

import com.example.mdb.dto.MovieResponse;
import com.example.mdb.entity.Feedback;
import com.example.mdb.entity.Movie;
import com.example.mdb.exception.MovieNotFoundByIdException;
import com.example.mdb.mapper.MovieMapper;
import com.example.mdb.repository.MovieRepository;
import com.example.mdb.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    public MovieResponse fetchMovie(String movieId) {
        if (movieRepository.existsById(movieId)){
            Movie movie = movieRepository.findById(movieId).get();
            List<Feedback> feedbacks = movie.getFeedbacks();

            double avgRatings = 0;

            for(Feedback feedback : feedbacks){
                avgRatings+=feedback.getRating();
            }
            avgRatings/= feedbacks.size();
            return movieMapper.movieResponseMapper(movie, avgRatings);
        }
        throw new MovieNotFoundByIdException("Movie with the provided ID is not found. Please verify the ID and try again.");
    }

    @Override
    public Set<MovieResponse> searchMovies(String search) {
        if(search==null || search.isBlank()){
            return null;
        }
        List<Movie> fetchedMovies = movieRepository.findByTitleContainingIgnoreCase(search);

        return movieMapper.movieResponseMapper(fetchedMovies);
    }
}
