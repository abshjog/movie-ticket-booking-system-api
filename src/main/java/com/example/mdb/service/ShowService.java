package com.example.mdb.service;

import com.example.mdb.dto.MovieShowsRequest;
import com.example.mdb.dto.ShowResponse;
import com.example.mdb.dto.TheaterShowProjection;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

public interface ShowService {

    ShowResponse addShow(String theaterId, String screenId, String movieId, @NotNull Long startTime, @NotNull String zoneId);

    Page<TheaterShowProjection> fetchShows(String movieId, MovieShowsRequest showsRequest, String city);
}
