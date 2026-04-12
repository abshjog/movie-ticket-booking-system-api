package com.example.mdb.service;

import com.example.mdb.dto.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ShowService {

    ShowResponse addShow(String theaterId, String screenId, ShowRequest showRequest);

    Page<TheaterShowProjection> fetchShows(String movieId, MovieShowsRequest showsRequest, String city);

    List<SeatStatusResponse> getSeatAvailability(String showId);
}
