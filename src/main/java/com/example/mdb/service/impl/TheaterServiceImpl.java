package com.example.mdb.service.impl;

import com.example.mdb.dto.TheaterRequest;
import com.example.mdb.dto.TheaterResponse;
import com.example.mdb.entity.Theater;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.exception.TheaterNotFoundException;
import com.example.mdb.exception.UserNotFoundException;
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
    public TheaterResponse addTheater(String email, TheaterRequest request) {
        // 1. Owner search
        UserDetails user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Owner not found"));

        // 2. Duplicate Check
        if (theaterRepository.existsByNameAndAddressAndCity(request.name(), request.address(), request.city())) {
            throw new RuntimeException("Bhai, ye theater is address par pehle se registered hai!");
        }

        // 3. Mapping & Saving
        Theater theater = theaterMapper.mapToEntity(request);
        theater.setTheaterOwner((TheaterOwner) user); // Owner set karna zaroori hai

        theater = theaterRepository.save(theater);
        return theaterMapper.theaterResponseMapper(theater);
    }

    @Override
    public TheaterResponse findTheater(String theaterId) {
        return theaterRepository.findById(theaterId)
                .map(theaterMapper::theaterResponseMapper)
                .orElseThrow(() -> new TheaterNotFoundException("Theater not found with ID: " + theaterId));
    }

    @Override
    public TheaterResponse updateTheater(String theaterId, TheaterRequest request) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new TheaterNotFoundException("Theater not found with ID: " + theaterId));

        // Manually update fields (ya ek common update helper use karo)
        theater.setName(request.name());
        theater.setAddress(request.address());
        theater.setCity(request.city());
        theater.setLandmark(request.landmark());

        theater = theaterRepository.save(theater);
        return theaterMapper.theaterResponseMapper(theater);
    }
}
