package com.example.mdb.service.impl;

import com.example.mdb.dto.RowLayoutRequest;
import com.example.mdb.dto.ScreenRequest;
import com.example.mdb.dto.ScreenResponse;
import com.example.mdb.entity.Screen;
import com.example.mdb.entity.Seat;
import com.example.mdb.entity.Theater;
import com.example.mdb.exception.ScreenNotFoundException;
import com.example.mdb.exception.TheaterNotFoundException;
import com.example.mdb.mapper.ScreenMapper;
import com.example.mdb.repository.ScreenRepository;
import com.example.mdb.repository.SeatRepository;
import com.example.mdb.repository.TheaterRepository;
import com.example.mdb.service.ScreenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class ScreenServiceImpl implements ScreenService {

    private final TheaterRepository theaterRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final ScreenMapper screenMapper;

    @Override
    public ScreenResponse addScreen(ScreenRequest screenRequest, String theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new TheaterNotFoundException("Theater not found with ID: " + theaterId));

        Screen screen = copy(screenRequest, new Screen(), theater);
        return screenMapper.screenResponseMapper(screen);
    }

    @Override
    public ScreenResponse findScreen(String theaterId, String screenId) {
        return screenRepository.findById(screenId)
                .filter(screen -> screen.getTheater().getTheaterId().equals(theaterId))
                .map(screenMapper::screenResponseMapper)
                .orElseThrow(() -> new ScreenNotFoundException("Screen not found or doesn't belong to this theater"));
    }

    private Screen copy(ScreenRequest screenRequest, Screen screen, Theater theater) {
        screen.setName(screenRequest.screenName());
        screen.setScreenType(screenRequest.screenType());
        screen.setTheater(theater);

        // Save initially to generate the Screen ID needed for Seat relationships
        Screen savedScreen = screenRepository.save(screen);

        // Generate seats based on the Matrix layout
        List<Seat> generatedSeats = createSeats(savedScreen, screenRequest.seatLayout());
        savedScreen.setSeats(generatedSeats);

        // Automatically calculate and update capacity and rows so DB stays consistent
        savedScreen.setCapacity(generatedSeats.size());
        savedScreen.setNoOfRows(screenRequest.seatLayout().size());

        // Save the updated capacity and rows
        return screenRepository.save(savedScreen);
    }

    private List<Seat> createSeats(Screen screen, List<RowLayoutRequest> seatLayout) {
        List<Seat> seats = new LinkedList<>();

        for (RowLayoutRequest rowLayout : seatLayout) {
            String rowLabel = rowLayout.rowLabel().toUpperCase();
            int colIndex = 1;

            for (String seatVal : rowLayout.seats()) {
                // If it's an AISLE, skip saving to DB, but increment the column index for physical spacing
                if (seatVal == null || seatVal.equalsIgnoreCase("AISLE") || seatVal.equalsIgnoreCase("NONE")) {
                    colIndex++;
                    continue;
                }

                Seat seat = new Seat();
                seat.setScreen(screen);
                seat.setRowLabel(rowLabel);
                seat.setColIndex(colIndex);
                seat.setName(rowLabel + seatVal);
                seat.setSeatCategory(rowLayout.category());
                seat.setDeleted(false);
                seat.setAisle(false);

                seats.add(seatRepository.save(seat));
                colIndex++;
            }
        }
        return seats;
    }

    @Override
    public List<ScreenResponse> getScreensByTheater(String theaterId) {
        if(!theaterRepository.existsById(theaterId)) {
            throw new TheaterNotFoundException("Theater not found with ID: " + theaterId);
        }

        List<Screen> screens = screenRepository.findByTheater_TheaterId(theaterId);
        return screens.stream()
                .map(screenMapper::screenResponseMapper)
                .toList();
    }
}