package com.example.mdb.mapper;

import com.example.mdb.dto.MovieResponse;
import com.example.mdb.entity.Movie;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class MovieMapper {

    public MovieResponse movieResponseMapper(Movie movie, double avgRatings) {
        if (movie == null)
            return null;

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedRatings = df.format(avgRatings);

        return MovieResponse .builder()
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
}
