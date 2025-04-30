package com.example.mdb.repository;

import com.example.mdb.entity.Screen;
import com.example.mdb.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, String> {

    List<Seat> findByScreenAndIsDeleteFalse(Screen screen);
}
