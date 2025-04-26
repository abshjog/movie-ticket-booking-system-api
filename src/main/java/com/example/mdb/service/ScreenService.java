package com.example.mdb.service;

import com.example.mdb.dto.ScreenRequest;
import com.example.mdb.dto.ScreenResponse;

public interface ScreenService {

    ScreenResponse addScreen(ScreenRequest screenRequest, String theaterId);
}
