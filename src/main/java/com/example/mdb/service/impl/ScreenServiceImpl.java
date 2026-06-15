package com.example.mdb.service.impl;

import com.example.mdb.dto.ScreenRequest;
import com.example.mdb.dto.ScreenResponse;
import com.example.mdb.entity.Screen;
import com.example.mdb.entity.Seat;
import com.example.mdb.entity.Theater;
import com.example.mdb.enums.SeatCategory;
import com.example.mdb.exception.RowLimitExceededException;
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
        if (screenRequest.noOfRows() > screenRequest.capacity()) {
            throw new RowLimitExceededException("Rows cannot be more than total capacity!");
        }
        screen.setName(screenRequest.screenName());
        screen.setScreenType(screenRequest.screenType());
        screen.setCapacity(screenRequest.capacity());
        screen.setNoOfRows(screenRequest.noOfRows());
        screen.setTheater(theater);

        Screen savedScreen = screenRepository.save(screen);
        savedScreen.setSeats(createSeats(savedScreen, screenRequest.capacity(), screenRequest.noOfRows()));
        return savedScreen;
    }

    private List<Seat> createSeats(Screen screen, Integer capacity, Integer totalRows) {
        List<Seat> seats = new LinkedList<>();
        int seatsPerRow = capacity / totalRows;

        int normalRowLimit = totalRows / 3;
        int premiumRowLimit = (totalRows * 2) / 3;

        char rowLabel = 'A';
        int currentRowNum = 1;

        for (int i = 1, seatInRow = 1; i <= capacity; i++, seatInRow++) {
            Seat seat = new Seat();
            seat.setScreen(screen);

            seat.setRowLabel(String.valueOf(rowLabel));
            seat.setColIndex(seatInRow);
            seat.setName(rowLabel + "" + seatInRow);
            seat.setDeleted(false);
            seat.setAisle(false);

            if (currentRowNum <= normalRowLimit) {
                seat.setSeatCategory(SeatCategory.NORMAL);
            } else if (currentRowNum <= premiumRowLimit) {
                seat.setSeatCategory(SeatCategory.PREMIUM);
            } else {
                seat.setSeatCategory(SeatCategory.VIP);
            }

            seats.add(seatRepository.save(seat));

            if (seatInRow == seatsPerRow) {
                seatInRow = 0;
                rowLabel++;
                currentRowNum++;
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