package com.example.mdb.repository;

import com.example.mdb.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, String> {
}
