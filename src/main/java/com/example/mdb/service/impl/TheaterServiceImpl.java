package com.example.mdb.service.impl;

import com.example.mdb.dto.TheaterRegistrationRequest;
import com.example.mdb.dto.TheaterResponse;
import com.example.mdb.entity.Theater;
import com.example.mdb.entity.TheaterOwner;
import com.example.mdb.entity.UserDetails;
import com.example.mdb.enums.UserRole;
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
    public TheaterResponse addTheater(String email, TheaterRegistrationRequest theaterRegistrationRequest) {
        if(userRepository.existsByEmail(email) && userRepository.findByEmail(email).getUserRole() == UserRole.THEATER_OWNER ){
            UserDetails user = userRepository.findByEmail(email);
            Theater theater = copy(theaterRegistrationRequest, new Theater(), user);
            return theaterMapper.theaterResponseMapper(theater);
        }
        throw new UserNotFoundByEmailException("Theater Owner with the provided email is not present");
    }

    private Theater copy(TheaterRegistrationRequest registrationRequest, Theater theater , UserDetails userDetails) {
        theater.setAddress(registrationRequest.address());
        theater.setCity(registrationRequest.city());
        theater.setName(registrationRequest.name());
        theater.setLandmark(registrationRequest.landmark());
        theater.setTheaterOwner((TheaterOwner) userDetails);
        theaterRepository.save(theater);
        return theater;
    }
}
