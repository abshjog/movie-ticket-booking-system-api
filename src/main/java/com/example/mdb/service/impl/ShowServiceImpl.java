package com.example.mdb.service.impl;

import com.example.mdb.dto.*;
import com.example.mdb.entity.*;
import com.example.mdb.exception.*;
import com.example.mdb.mapper.ShowMapper;
import com.example.mdb.mapper.TheaterMapper;
import com.example.mdb.repository.*;
import com.example.mdb.service.ShowService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ShowServiceImpl implements ShowService {

    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final ShowMapper showMapper;
    private final TheaterMapper theaterMapper;
    private final ShowSeatRepository showSeatRepository;

    @Override
    @Transactional
    public ShowResponse addShow(String theaterId, String screenId, ShowRequest showRequest) {

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new TheaterNotFoundByIdException("Theater Id not found"));

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ScreenNotFoundByIdException("Screen Id not found"));

        Movie movie = movieRepository.findById(showRequest.movieId())
                .orElseThrow(() -> new MovieNotFoundByIdException("Movie Id not found"));

        Instant instantStartTime = Instant.ofEpochMilli(showRequest.startTime());

        if (instantStartTime.isBefore(Instant.now())) {
            throw new InvalidShowTimeException("Show creation failed: Cannot schedule a show for a past date and time.");
        }

        Instant movieCompletionTime = instantStartTime.plus(movie.getRuntime());

        boolean hasOverlap = screen.getShows().stream().anyMatch(s ->
                !(movieCompletionTime.isBefore(s.getStartsAt()) || instantStartTime.isAfter(s.getEndsAt()))
        );

        if (hasOverlap) {
            throw new ScreeningOverlapException("Another show is already booked in this time slot");
        }

        Show show = new Show();
        show.setScreen(screen);
        show.setMovie(movie);
        show.setStartsAt(instantStartTime);
        show.setEndsAt(movieCompletionTime);
        show.setTheater(theater);

        show.setTicketPrice(showRequest.ticketPrice());

        Show savedShow = showRepository.save(show);

        List<Seat> physicalSeats = screen.getSeats();
        List<ShowSeat> showSeats = physicalSeats.stream().map(seat -> {
            ShowSeat showSeat = new ShowSeat();
            showSeat.setShow(savedShow);
            showSeat.setSeat(seat);
            showSeat.setBooked(false);
            return showSeat;
        }).toList();

        showSeatRepository.saveAll(showSeats);

        return showMapper.showResponseMapper(savedShow);
    }

    @Override
    public Page<TheaterShowProjection> fetchShows(String movieId, MovieShowsRequest showsRequest, String city) {
        // ... (Tera purana logic ekdum sahi hai yahan, koi change nahi chahiye) ...
        ZoneId zoneId = (showsRequest.zoneId() == null || showsRequest.zoneId().isBlank())
                ? ZoneId.of("UTC")
                : ZoneId.of(ZoneId.SHORT_IDS.getOrDefault(showsRequest.zoneId().toUpperCase(), "UTC"));

        if (city == null || city.isBlank()) {
            throw new CityNotFoundException("No city found by name");
        }

        Instant start = showsRequest.date().atStartOfDay(zoneId).toInstant();
        Instant end = showsRequest.date().plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant();

        Pageable pageable = PageRequest.of(showsRequest.page() - 1, showsRequest.size());

        Page<String> theaterIdsPage = showRepository.findTheaterIdsWithMatchingShowsAndCity(
                movieId, start, end, showsRequest.screenType(), city, pageable
        );

        List<String> theaterIds = theaterIdsPage.getContent();
        if (theaterIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Show> shows = showRepository.findShowsForTheaters(
                movieId, start, end, showsRequest.screenType(), theaterIds
        );

        Map<String, List<Show>> grouped = shows.stream()
                .collect(Collectors.groupingBy(show -> show.getTheater().getTheaterId()));

        List<TheaterShowProjection> results = theaterIds.stream()
                .map(id -> {
                    List<Show> theaterShows = grouped.get(id);
                    if (theaterShows == null || theaterShows.isEmpty()) return null;

                    Theater theater = theaterShows.get(0).getTheater();
                    List<ShowResponse> showProjections = theaterShows.stream()
                            .map(showMapper::showResponseMapper)
                            .toList();

                    return new TheaterShowProjection(
                            theater.getTheaterId(),
                            theater.getName(),
                            theater.getAddress(),
                            showProjections
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(results, pageable, theaterIdsPage.getTotalElements());
    }

    @Override
    public List<SeatStatusResponse> getSeatAvailability(String showId) {
        List<ShowSeat> showSeats = showSeatRepository.findByShowShowIdOrderBySeatNameAsc(showId);

        if (showSeats.isEmpty()) {
            throw new RuntimeException("No seats mapped for this show!");
        }

        return showSeats.stream()
                .map(ss -> SeatStatusResponse.builder()
                        .seatId(ss.getSeat().getSeatId())
                        .seatName(ss.getSeat().getName())
                        .isBooked(ss.isBooked()) //
                        .build())
                .toList();
    }
}
