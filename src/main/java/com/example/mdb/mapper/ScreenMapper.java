package com.example.mdb.mapper;

import com.example.mdb.dto.ScreenResponse;
import com.example.mdb.entity.Screen;
import org.springframework.stereotype.Component;

@Component
public class ScreenMapper {

    public ScreenResponse screenResponseMapper(Screen screen) {
        if (screen == null)
            return null;
        return new ScreenResponse(
                screen.getScreenId(),
                screen.getScreenType(),
                screen.getCapacity(),
                screen.getNoOfRows(),
                screen.getSeats()
        );
    }
}
