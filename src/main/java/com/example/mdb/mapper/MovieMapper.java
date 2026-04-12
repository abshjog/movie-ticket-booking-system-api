package com.example.mdb.mapper;

import com.example.mdb.dto.MovieResponse;
import com.example.mdb.entity.Feedback;
import com.example.mdb.entity.Movie;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    private final DecimalFormat df = new DecimalFormat("#.#");

    public MovieResponse movieResponseMapper(Movie movie, double avgRatings) {
        if (movie == null)
            return null;

        String formattedRatings = (Double.isNaN(avgRatings) || avgRatings <= 0)
                ? "0.0"
                : df.format(avgRatings);

        return MovieResponse.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .ratings(formattedRatings)
                .runtime(movie.getRuntime())
                .certificate(movie.getCertificate())
                .genre(movie.getGenre())
                .castList(movie.getCastList())
                .build();
    }

    public Set<MovieResponse> movieResponseMapper(Collection<Movie> movies) {
        if (movies == null)
            return null;

        return movies.stream()
                .map(movie -> {
                    double avg = calculateAvgRating(movie.getFeedbacks());
                    return movieResponseMapper(movie, avg);
                })
                .collect(Collectors.toSet());
    }

    private double calculateAvgRating(List<Feedback> feedbacks) {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        double total = feedbacks.stream()
                .mapToDouble(Feedback::getRating)
                .sum();
        return total / feedbacks.size();
    }
}
