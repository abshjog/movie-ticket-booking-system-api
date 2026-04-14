package com.example.mdb.service.impl;

import com.example.mdb.dto.*;
import com.example.mdb.entity.*;
import com.example.mdb.exception.*;
import com.example.mdb.mapper.ShowMapper;
import com.example.mdb.mapper.TheaterMapper;
import com.example.mdb.repository.*;
import com.example.mdb.service.ShowService;
import jakarta.transaction.Transactional;
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
                .orElseThrow(() -> new TheaterNotFoundException("Theater not found"));
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found"));
        Movie movie = movieRepository.findById(showRequest.movieId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));

        Instant start = Instant.ofEpochMilli(showRequest.startTime());
        if (start.isBefore(Instant.now())) throw new InvalidShowTimeException("Cannot schedule shows in the past!");

        Instant end = start.plus(movie.getRuntime());

        // Overlap Check Logic
        boolean overlap = screen.getShows().stream().anyMatch(s ->
                !(end.isBefore(s.getStartsAt()) || start.isAfter(s.getEndsAt()))
        );
        if (overlap) throw new ScreeningOverlapException("Time slot conflict on this screen!");

        Show show = new Show();
        show.setScreen(screen);
        show.setMovie(movie);
        show.setStartsAt(start);
        show.setEndsAt(end);
        show.setTheater(theater);
        show.setTicketPrice(showRequest.ticketPrice());

        Show savedShow = showRepository.save(show);

        // Standard: Generating ShowSeats from Physical Seats
        List<ShowSeat> showSeats = screen.getSeats().stream().map(seat -> {
            ShowSeat ss = new ShowSeat();
            ss.setShow(savedShow);
            ss.setSeat(seat);
            ss.setBooked(false);
            return ss;
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
        // First check if show exists
        if(!showRepository.existsById(showId)) throw new RuntimeException("Show not found");

        return showSeatRepository.findByShowShowIdOrderBySeatNameAsc(showId).stream()
                .map(ss -> SeatStatusResponse.builder()
                        .seatId(ss.getSeat().getSeatId())
                        .seatName(ss.getSeat().getName())
                        .isBooked(ss.isBooked())
                        .build())
                .toList();
    }
}
