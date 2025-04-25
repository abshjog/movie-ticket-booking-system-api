package com.example.mdb.service.impl;

import com.example.mdb.dto.TheaterRequest;
import com.example.mdb.dto.TheaterResponse;
import com.example.mdb.entity.Theater;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.enums.UserRole;
import com.example.mdb.exception.TheaterNotFoundByIdException;
import com.example.mdb.exception.UserNotFoundByEmailException;
import com.example.mdb.mapper.TheaterMapper;
import com.example.mdb.repository.TheaterRepository;
import com.example.mdb.repository.UserRepository;
import com.example.mdb.service.TheaterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private final TheaterMapper theaterMapper;
    private final UserRepository userRepository;

    @Override
    public TheaterResponse addTheater(String email, TheaterRequest theaterRequest) {

        if (userRepository.existsByEmail(email) && userRepository.findByEmail(email).getUserRole() == UserRole.THEATER_OWNER) {
            UserDetails user = userRepository.findByEmail(email);
            Theater theater = copy(theaterRequest, new Theater(), user);
            return theaterMapper.theaterResponseMapper(theater);
        }
        throw new UserNotFoundByEmailException("Theater Owner with the provided email is not present");
    }

    private Theater copy(TheaterRequest theaterRequest, Theater theater, UserDetails user) {

        theater.setAddress(theaterRequest.address());
        theater.setCity(theaterRequest.city());
        theater.setName(theaterRequest.name());
        theater.setLandmark(theaterRequest.landmark());
        theater.setTheaterOwner((TheaterOwner) user);
        theaterRepository.save(theater);
        return theater;
    }

    @Override
    public TheaterResponse findTheater(String theaterId) {
        if(theaterRepository.existsById(theaterId)){
            Theater theater = theaterRepository.findById(theaterId).get();
            return theaterMapper.theaterResponseMapper(theater);
        }
        throw new TheaterNotFoundByIdException("Theater not found by the entered ID");
    }

    @Override
    public TheaterResponse updateTheater(String theaterId, TheaterRequest theaterRequest) {
        if(theaterRepository.existsById(theaterId)) {
            Theater theater = theaterRepository.findById(theaterId).get();
            theater = copy(theaterRequest, theater);
            return theaterMapper.theaterResponseMapper(theater);
        }
        throw new TheaterNotFoundByIdException("Theater not found by id");
    }

    private Theater copy(TheaterRequest theaterRequest, Theater theater) {

        theater.setAddress(theaterRequest.address());
        theater.setCity(theaterRequest.city());
        theater.setName(theaterRequest.name());
        theater.setLandmark(theaterRequest.landmark());
        theaterRepository.save(theater);
        return theater;
    }
}
