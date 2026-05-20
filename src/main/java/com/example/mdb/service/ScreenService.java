package com.example.mdb.service;

import com.example.mdb.dto.ScreenRequest;
import com.example.mdb.dto.ScreenResponse;

import java.util.List;

public interface ScreenService {

    ScreenResponse addScreen(ScreenRequest screenRequest, String theaterId);

    ScreenResponse findScreen(String theaterId, String screenId);

    List<ScreenResponse> getScreensByTheater(String theaterId);

}
