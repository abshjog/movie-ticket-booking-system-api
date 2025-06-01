package com.example.mdb.repository;

import com.example.mdb.entity.Show;
import com.example.mdb.enums.ScreenType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ShowRepository extends JpaRepository<Show, String> {

    public interface TheaterId {
        String getTheater_TheaterId();     // exactly matches the property name
    }

    List<Show> findDistinctByStartsAtBetweenAndMovie_MovieIdAndScreen_ScreenType(
            Instant start,
            Instant end,
            String movieId,
            ScreenType screenType
    );

    @Query("SELECT s FROM Show s " +
            "WHERE s.movie.movieId = :movieId " +
            "AND s.startsAt BETWEEN :start AND :end " +
            "AND s.screen.screenType = :screenType " +
            "AND s.theater.id IN :theaterIds")
    List<Show> findShowsForTheaters(
            @Param("movieId") String movieId,
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("screenType") ScreenType screenType,
            @Param("theaterIds") List<String> theaterIds
    );

    @Query("""
                SELECT DISTINCT t.id
                FROM Theater t
                JOIN t.screens s
                JOIN s.shows sh
                WHERE sh.movie.id = :movieId
                  AND sh.startsAt BETWEEN :start AND :end
                  AND s.screenType = :screenType
                  AND t.city = :city
            """)
    Page<String> findTheaterIdsWithMatchingShowsAndCity(
            @Param("movieId") String movieId,
            @Param("start") Instant start,
            @Param("end") Instant end,
            @Param("screenType") ScreenType screenType,
            @Param("city") String city,
            Pageable pageable
    );

}
