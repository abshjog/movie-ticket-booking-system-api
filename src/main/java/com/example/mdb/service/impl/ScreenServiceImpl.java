package com.example.mdb.service.impl;

import com.example.mdb.dto.ScreenRequest;
import com.example.mdb.dto.ScreenResponse;
import com.example.mdb.entity.Screen;
import com.example.mdb.entity.Seat;
import com.example.mdb.entity.Theater;
import com.example.mdb.exception.TheaterNotFoundByIdException;
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
        if (theaterRepository.existsById(theaterId)) {
            Theater theater = theaterRepository.findById(theaterId).get();
            Screen screen = copy(screenRequest, new Screen(), theater);
            return screenMapper.screenResponseMapper(screen);
        }

        throw new TheaterNotFoundByIdException("No Theater found by ID");
    }

    private Screen copy(ScreenRequest screenRequest, Screen screen, Theater theater){
        screen.setScreenType(screenRequest.screenType());
        screen.setCapacity(screenRequest.capacity());
        screen.setNoOfRows(screenRequest.noOfRows());
        screen.setTheater(theater);
        screenRepository.save(screen);
        screen.setSeats(createSeats(screen, screenRequest.capacity() ));
        return screen;
    }

    private List<Seat> createSeats(Screen screen, Integer capacity) {
        List<Seat> seats = new LinkedList<>();
        int noOfSeatsPerRow = screen.getCapacity() / screen.getNoOfRows();
        char row = 'A';
        for (int i = 1, j = 1; i <= capacity; i++, j++) {
            Seat seat = new Seat();
            seat.setScreen(screen);
            seat.setName(row + "" + j);
            seatRepository.save(seat);
            seats.add(seat);
            if (j == noOfSeatsPerRow) {
                j = 0;
                row++;
            }
        }
        return seats;
    }
}
