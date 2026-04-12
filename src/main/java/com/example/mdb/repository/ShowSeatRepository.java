package com.example.mdb.repository;

import com.example.mdb.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, String> {

    List<ShowSeat> findByShowShowIdAndSeatSeatIdIn(String showId, List<String> seatIds);

    List<ShowSeat> findByShowShowIdOrderBySeatNameAsc(String showId);
}
