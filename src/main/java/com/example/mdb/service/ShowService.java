package com.example.mdb.service;

import com.example.mdb.dto.ShowResponse;

public interface ShowService {

    ShowResponse addShow(String theaterId, String screenId, String movieId, Long startTime);
}
