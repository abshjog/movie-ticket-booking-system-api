package com.example.mdb.service.impl;

import com.example.mdb.dto.ShowResponse;
import com.example.mdb.entity.Movie;
import com.example.mdb.entity.Screen;
import com.example.mdb.entity.Show;
import com.example.mdb.exception.MovieNotFoundByIdException;
import com.example.mdb.exception.ScreenNotFoundByIdException;
import com.example.mdb.exception.ScreeningOverlapException;
import com.example.mdb.exception.TheaterNotFoundByIdException;
import com.example.mdb.mapper.ShowMapper;
import com.example.mdb.repository.MovieRepository;
import com.example.mdb.repository.ScreenRepository;
import com.example.mdb.repository.ShowRepository;
import com.example.mdb.repository.TheaterRepository;
import com.example.mdb.service.ShowService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@AllArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final ShowMapper showMapper;

    @Override
    public ShowResponse addShow(String theaterId, String screenId, String movieId, Long startTime) {
        if (theaterRepository.existsById(theaterId)) {

            if (screenRepository.existsById(screenId)) {

                if (movieRepository.existsById(movieId)) {

                    Screen screen = screenRepository.findById(screenId).get();
                    Set<Show> shows = screen.getShows();

                    Movie movie = movieRepository.findById(movieId).get();

                    Instant instantStartTime = Instant.ofEpochMilli(startTime);

                    for (Show s : shows) {
                        Instant showStartTime = s.getStartsAt();
                        Instant showEndTime = s.getEndsAt();
                        Instant movieCompletionTime = instantStartTime.plus(movie.getRuntime());

                        if (! ( movieCompletionTime.isBefore(showStartTime) || instantStartTime.isAfter(showEndTime) )) {
                            throw new ScreeningOverlapException("Screening time conflict!! The screening scheduled from " + s.getStartsAt() + " to " + s.getEndsAt() + " overlaps with an existing screening. Please choose a different time slot.");
                        }
                    }

                    Show show = copy(new Show(), startTime, screen, movie);

                    return showMapper.showResponseMapper(show);
                }
                throw new MovieNotFoundByIdException("Movie with the provided ID is not found. Please verify the ID and try again.");
            }
            throw new ScreenNotFoundByIdException("Screen not found by the provided ID");
        }
        throw new TheaterNotFoundByIdException("Theater not found by the provided ID");
    }

    private Show copy(Show show, Long startTime, Screen screen, Movie movie) {
        show.setScreen(screen);
        show.setMovie(movie);
        Instant instantStartTime = Instant.ofEpochMilli(startTime);
        show.setStartsAt(instantStartTime);
        Instant endTime = instantStartTime.plus(movie.getRuntime());
        System.out.println(endTime);
        show.setEndsAt(endTime);
        showRepository.save(show);
        return show;
    }
}
